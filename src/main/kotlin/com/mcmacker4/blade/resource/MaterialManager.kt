package com.mcmacker4.blade.resource

import com.mcmacker4.blade.render.gl.Texture2D
import org.lwjgl.assimp.AIMaterial
import org.lwjgl.assimp.AIString
import org.lwjgl.assimp.Assimp
import java.nio.IntBuffer


object MaterialManager {
    
    private class MaterialData(
            val texture: Texture2D
    )
    
    private val materials = hashMapOf<Int, MaterialData>()
    private var nextId = 1
    
//    private fun saveMaterial(materialData: MaterialData) : Material {
//        val id = nextId++
//        materials[nextId] = materialData
//        return Material(id)
//    }
//
//    fun loadMaterial(aiMaterial: AIMaterial) {
//        val path = AIString.calloc()
//        @Suppress("CAST_NEVER_SUCCEEDS")
//        Assimp.aiGetMaterialTexture(aiMaterial, Assimp.aiTextureType_DIFFUSE, 0, path, null as IntBuffer, null, null, null, null, null)
//        
//    }
    
}