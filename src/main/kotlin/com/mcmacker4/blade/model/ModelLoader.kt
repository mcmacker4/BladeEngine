package com.mcmacker4.blade.model

import com.mcmacker4.blade.render.data.Material
import com.mcmacker4.blade.render.data.Mesh
import org.lwjgl.assimp.AIMaterial
import org.lwjgl.assimp.AIScene
import org.lwjgl.assimp.Assimp

class Model(
        val meshes: Array<Mesh>
) {
    
    companion object {

//        fun loadFromFile(path: String) : Model {
//            val flags = Assimp.aiProcess_Triangulate or
//                    Assimp.aiProcess_FixInfacingNormals or
//                    Assimp.aiProcess_CalcTangentSpace
//            
//            Assimp.aiImportFile(path, flags)?.use { aiScene ->
//                val materials = processMaterials(aiScene)
//                val meshes = processMeshes(aiScene, materials)
//            }
//        }
//        
//        private fun processMaterials(aiScene: AIScene) : ArrayList<Material> {
//            val count = aiScene.mNumMaterials()
//            val materials = arrayListOf<Material>()
//            aiScene.mMaterials()?.let { aiMaterialsBuffer ->
//                while (aiMaterialsBuffer.hasRemaining()) {
//                    val aiMaterial = AIMaterial.create(aiMaterialsBuffer.get())
//                    
//                }
//            }
//        }
//        
//        private fun processSingleMaterial(aiMaterial: AIMaterial) : Material {
//            
//        }
//        
//        private fun processMeshes(scene: AIScene, materials: Array<Material>) {
//            
//        }

    }
    
}