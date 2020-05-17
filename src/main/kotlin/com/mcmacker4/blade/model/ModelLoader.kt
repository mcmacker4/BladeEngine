package com.mcmacker4.blade.model

import com.mcmacker4.blade.file.FileImport
import com.mcmacker4.blade.render.gl.Texture2D
import com.mcmacker4.blade.resource.Material
import com.mcmacker4.blade.resource.Mesh
import com.mcmacker4.blade.scene.Entity
import com.mcmacker4.blade.scene.components.MeshComponent
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.lwjgl.assimp.*
import org.lwjgl.system.MemoryUtil
import java.nio.IntBuffer

object Model {
    
    private class MeshMaterialPair(val mesh: Mesh, val material: Material)
    
    fun loadFromFile(folder: String, name: String) : Entity {
        val flags = Assimp.aiProcess_Triangulate or
                Assimp.aiProcess_JoinIdenticalVertices or
                Assimp.aiProcess_CalcTangentSpace or
                Assimp.aiProcess_FlipUVs
        
        val buffer = FileImport.resourceToBuffer("/$folder/$name")
        
        val entity = Assimp.aiImportFileFromMemory(buffer, flags, name.substring(name.lastIndexOf(".") + 1))?.use { aiScene ->
            val rootNode = aiScene.mRootNode() ?: throw Exception("Scene has no root node.")

            val materials = processMaterials(aiScene)
            val meshMaterialPairs = processMeshes(aiScene, materials)

            processNodeRecursive(rootNode, meshMaterialPairs)
        }
        
        MemoryUtil.memFree(buffer)
        
        if (entity == null) 
            throw Exception("Could not load model at $folder/$name: " + Assimp.aiGetErrorString())
        
        return entity
    }
    
    private fun processNodeRecursive(aiNode: AINode, meshMaterialPairs: ArrayList<MeshMaterialPair>): Entity {
        val aiMatrix = aiNode.mTransformation()
        val matrix = Matrix4f(
                aiMatrix.a1(), aiMatrix.a2(), aiMatrix.a3(), aiMatrix.a4(),
                aiMatrix.b1(), aiMatrix.b2(), aiMatrix.b3(), aiMatrix.b4(),
                aiMatrix.c1(), aiMatrix.c2(), aiMatrix.c3(), aiMatrix.c4(),
                aiMatrix.d1(), aiMatrix.d2(), aiMatrix.d3(), aiMatrix.d4()
        )
        
        val position = matrix.getTranslation(Vector3f())
        val rotation = matrix.getUnnormalizedRotation(Quaternionf())
        val scale = matrix.getScale(Vector3f())

        val nodeEntity = Entity(position, rotation, scale)
        
        val aiMeshes = aiNode.mMeshes()
        
        if (aiMeshes != null) {
            val numMeshes = aiNode.mNumMeshes()
            
            if (numMeshes == 1) {
                // Only one mesh, can be attached to this entity
                val meshIndex = aiMeshes.get(0)
                val meshMaterialPair = meshMaterialPairs[meshIndex]
                val component = MeshComponent(meshMaterialPair.mesh, meshMaterialPair.material)
                nodeEntity.addComponent(component)
            } else if (numMeshes > 1) {
                // Multiple meshes, will put each in another entity and add them all as children of this enttiy
                // If we ever support entities with multiple mesh components fix this.
                for (i in 0 until numMeshes) {
                    val meshEntity = Entity()
                    val meshIndex = aiMeshes.get(i)
                    val meshMaterialPair = meshMaterialPairs[meshIndex]
                    val component = MeshComponent(meshMaterialPair.mesh, meshMaterialPair.material)
                    meshEntity.addComponent(component)
                    nodeEntity.addChild(meshEntity)
                }
            }
            
        }
        
        val aiChildren = aiNode.mChildren()
        if (aiChildren != null) {
            for (i in 0 until aiNode.mNumChildren()) {
                val aiChild = AINode.create(aiChildren.get(i))
                nodeEntity.addChild(processNodeRecursive(aiChild, meshMaterialPairs))
            }
        }
        
        return nodeEntity
    }

    private fun processMaterials(aiScene: AIScene) : ArrayList<Material> {
        val count = aiScene.mNumMaterials()
        val materials = arrayListOf<Material>()
        aiScene.mMaterials()?.let { aiMaterialsBuffer ->
            for (i in 0 until count) {
                val aiMaterial = AIMaterial.create(aiMaterialsBuffer.get(i))
                materials.add(processSingleMaterial(aiScene, aiMaterial))
            }
        }
        return materials
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun processSingleMaterial(aiScene: AIScene, aiMaterial: AIMaterial) : Material {
        val pathBuff = AIString.calloc()
        
        Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, pathBuff,
                null as IntBuffer?, null, null, null, null, null)

        val path = pathBuff.dataString()
        
        println("Texture path: $path")
        
        val texture = if (path.isNotEmpty())
            getEmbeddedTexture(aiScene, path)
        else
            Texture2D.EMPTY
        
        return Material(texture)
    }
    
    private fun getEmbeddedTexture(aiScene: AIScene, path: String) : Texture2D {
        
        val aiTexture = (if (path.startsWith("*")) {
            findEmbeddedTextureByIndex(aiScene, path.substring(1).toInt())
        } else findEmbeddedTextureByPath(aiScene, path))
            ?: return Texture2D.EMPTY

        if (aiTexture.mHeight() == 0) {
            val address = aiTexture.pcData(0).address0()
            val buffer = MemoryUtil.memByteBuffer(address, aiTexture.mWidth())
            
            return Texture2D.readTexture(buffer)
        } else {

            val width = aiTexture.mWidth()
            val height = aiTexture.mHeight()

            val bytes = width * height * 4
            val byteArray = ByteArray(bytes)

            val aiTexels = aiTexture.pcData(aiTexture.mWidth() * aiTexture.mHeight())
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val pi = y * width + x
                    val aiTexel = aiTexels.get(pi)
                    byteArray[pi] = aiTexel.r()
                    byteArray[pi + 1] = aiTexel.g()
                    byteArray[pi + 2] = aiTexel.b()
                    byteArray[pi + 3] = aiTexel.a()
                }
            }

            val buffer = MemoryUtil.memAlloc(bytes)
            buffer.put(byteArray)

            val texture = Texture2D.loadFromMemory(width, height, buffer)

            MemoryUtil.memFree(buffer)

            return texture

        }
        
    }
    
    private fun findEmbeddedTextureByIndex(aiScene: AIScene, index: Int) : AITexture? {
        val aiTextureAddr = aiScene.mTextures()?.get(index) ?: return null
        return AITexture.create(aiTextureAddr)
    }
    
    private fun findEmbeddedTextureByPath(aiScene: AIScene, path: String) : AITexture? {
        val aiTextures = aiScene.mTextures() ?: return null
        val count = aiScene.mNumTextures()
        for (i in 0 until count) {
            val aiTexture = AITexture.create(aiTextures.get(i)) ?: continue
            if (aiTexture.mFilename().dataString() == path)
                return aiTexture
        }
        return null
    }

    private fun processMeshes(aiScene: AIScene, materials: ArrayList<Material>): ArrayList<MeshMaterialPair> {
        val count = aiScene.mNumMeshes()
        val meshes = arrayListOf<MeshMaterialPair>()
        aiScene.mMeshes()?.let { meshesBuffer ->
            for (i in 0 until count) {
                val aiMesh = AIMesh.create(meshesBuffer.get(i))
                meshes.add(processSingleMesh(aiMesh, materials))
            }
        }
        return meshes
    }
    
    private fun processSingleMesh(aiMesh: AIMesh, materials: ArrayList<Material>) : MeshMaterialPair {
        val material = materials[aiMesh.mMaterialIndex()]
        val vertexCount = aiMesh.mNumVertices()
        
        val indices = processIndices(aiMesh)
        val vertices = processVertices(aiMesh, vertexCount)
        val normals = processNormals(aiMesh, vertexCount)
        val uvcoords = processUvCoords(aiMesh, vertexCount)
        
        val mesh = Mesh(indices, vertices, normals, uvcoords)
        return MeshMaterialPair(mesh, material)
    }
    
    private fun processIndices(aiMesh: AIMesh): IntArray {
        val numFaces = aiMesh.mNumFaces()
        val indices = IntArray(numFaces * 3)
        
        val aiFaces = aiMesh.mFaces()
        for (f in 0 until numFaces) {
            val aiFace = aiFaces.get(f)
            val aiIndices = aiFace.mIndices()
            for (v in 0 until 3) {
                indices[f * 3 + v] = aiIndices.get(v)
            }
        }
        
        return indices
    }
    
    private fun processVertices(aiMesh: AIMesh, vertexCount: Int): FloatArray {
        val vertices = FloatArray(vertexCount * 3)
        val aiVertices = aiMesh.mVertices()
        for (i in 0 until vertexCount) {
            val aiVertex = aiVertices.get(i)
            vertices[i * 3    ] = aiVertex.x()
            vertices[i * 3 + 1] = aiVertex.y()
            vertices[i * 3 + 2] = aiVertex.z()
        }
        return vertices
    }
    
    private fun processNormals(aiMesh: AIMesh, vertexCount: Int): FloatArray {
        val normals = FloatArray(vertexCount * 3)
        val aiNormals = aiMesh.mVertices() ?: return normals
        for (i in 0 until vertexCount) {
            val aiNormal = aiNormals.get(i)
            normals[i * 3    ] = aiNormal.x()
            normals[i * 3 + 1] = aiNormal.y()
            normals[i * 3 + 2] = aiNormal.z()
        }
        return normals
    }
    
    private fun processUvCoords(aiMesh: AIMesh, vertexCount: Int): FloatArray {
        val uvcoords = FloatArray(vertexCount * 2)
        val aiUvCoords = aiMesh.mTextureCoords(0) ?: return uvcoords
        for (i in 0 until vertexCount) {
            val aiUvCoord = aiUvCoords.get(i)
            uvcoords[i * 2    ] = aiUvCoord.x()
            uvcoords[i * 2 + 1] = aiUvCoord.y()
        }
        return uvcoords
    }
    
}