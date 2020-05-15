package com.mcmacker4.blade.render

import com.mcmacker4.blade.render.gl.ShaderProgram
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.CameraComponent
import com.mcmacker4.blade.scene.components.MeshComponent
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryStack


class SceneRenderer {
    
    private val shader = ShaderProgram.loadNamed("shader")
    
    fun render(scene: Scene) {
        
        scene.getActiveCamera()?.let { camera ->
            
            shader.use()

            val modelMatrixLocation = shader.getUniformLocation("modelMatrix")
            val projectionMatrixLocation = shader.getUniformLocation("projectionMatrix")

            val cameraComponent = camera.getComponent(CameraComponent::class)!!
            MemoryStack.stackPush().use { stack ->
                val modelBuffer = cameraComponent.matrix.get(stack.mallocFloat(16))
                glUniformMatrix4fv(projectionMatrixLocation, false, modelBuffer)
            }

            val entities = scene.getDrawableEntities()

            entities.forEach { entity ->

                MemoryStack.stackPush().use { stack ->
                    val modelBuffer = entity.getModelMatrix().get(stack.mallocFloat(16))
                    glUniformMatrix4fv(modelMatrixLocation, false, modelBuffer)
                }

                val component = entity.getComponent(MeshComponent::class)!!
                component.mesh.render()

            }

            glUseProgram(0)

        }
        
    }
    
    fun destroy() {
        shader.delete()
    }
    
}