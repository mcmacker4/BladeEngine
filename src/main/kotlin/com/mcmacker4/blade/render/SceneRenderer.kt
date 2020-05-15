package com.mcmacker4.blade.render

import com.mcmacker4.blade.render.gl.ShaderProgram
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.MeshComponent
import org.lwjgl.opengl.GL20.glUseProgram


class SceneRenderer {
    
    private val shader = ShaderProgram.loadNamed("shader")
    
    fun render(scene: Scene) {
        
        shader.use()
        
        scene.entities.filter { it.hasComponent(MeshComponent::class) }.forEach { entity ->
            val component = entity.getComponent(MeshComponent::class)!!
            component.mesh.render()
        }
        
        glUseProgram(0)
        
    }
    
}