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
import java.nio.IntBuffer

object Model {
    
    private class MeshMaterialPair(val mesh: Mesh, val material: Material)
    
    fun loadFromFile(folder: String, name: String) : Entity {
        val flags = Assimp.aiProcess_Triangulate or
                Assimp.aiProcess_JoinIdenticalVertices or
                Assimp.aiProcess_CalcTangentSpace
//        val buffer = FileImport.resourceToBuffer("/$folder/$name")
        Assimp.aiImportFile("$folder/$name", flags)?.use { aiScene ->
            val rootNode = aiScene.mRootNode() ?: throw Exception("Scene has no root node.")

            val materials = processMaterials(aiScene, folder)
            val meshMaterialPairs = processMeshes(aiScene, materials)
            
            return processNodeRecursive(rootNode, meshMaterialPairs)
        } ?: throw Exception("Could not load model at $folder/$name: " + Assimp.aiGetErrorString())
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

    private fun processMaterials(aiScene: AIScene, basePath: String) : ArrayList<Material> {
        val count = aiScene.mNumMaterials()
        val materials = arrayListOf<Material>()
        aiScene.mMaterials()?.let { aiMaterialsBuffer ->
            for (i in 0 until count) {
                val aiMaterial = AIMaterial.create(aiMaterialsBuffer.get(i))
                materials.add(processSingleMaterial(aiMaterial, basePath))
            }
        }
        return materials
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun processSingleMaterial(aiMaterial: AIMaterial, basePath: String) : Material {
        val pathBuff = AIString.calloc()
        
        Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, pathBuff,
                null as IntBuffer?, null, null, null, null, null)

        var path = pathBuff.dataString()
        if (path.isEmpty()) path = "white.png"
        
        return Material(Texture2D.loadFromFileSystem("$basePath/$path"))
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