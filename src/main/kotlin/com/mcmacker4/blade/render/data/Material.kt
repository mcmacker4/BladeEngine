package com.mcmacker4.blade.render.data

import com.mcmacker4.blade.render.gl.ShaderProgram
import com.mcmacker4.blade.render.gl.Texture2D
import org.joml.Vector3f
import org.lwjgl.opengl.GL20.glUniform1f
import org.lwjgl.opengl.GL20.glUniform3f


class Material(
        val texture: Texture2D
//        val albedo: Vector3f,
//        val roughness: Float,
//        val metalness: Float
) {


//    /**
//     * Assumes that the shader is already bound.
//     * Make sure to bind it before calling this method.
//     */
//    fun loadToShader(shader: ShaderProgram) {
//        
//        val albedoLocation = shader.getUniformLocation("albedo")
//        val roughnessLocation = shader.getUniformLocation("roughness")
//        val metalnessLocation = shader.getUniformLocation("metalness")
//        
//        glUniform3f(albedoLocation, albedo.x, albedo.y, albedo.z)
//        glUniform1f(roughnessLocation, roughness)
//        glUniform1f(metalnessLocation, metalness)
//        
//    }
    
    fun delete() {
        texture.delete()
    }
    
}