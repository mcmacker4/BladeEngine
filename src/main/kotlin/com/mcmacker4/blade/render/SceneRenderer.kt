package com.mcmacker4.blade.render

import com.mcmacker4.blade.render.gl.ShaderProgram
import com.mcmacker4.blade.scene.Entity
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.CameraComponent
import com.mcmacker4.blade.scene.components.MeshComponent
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil


class SceneRenderer {
    
    private val shader = ShaderProgram.loadNamed("/shader")
    
    private val viewMatrix = Matrix4f()
    
    private val matrixBuffer = MemoryUtil.memAllocFloat(16)
    
    private fun drawEntityRecursive(entity: Entity, parentTransform: Matrix4f, modelMatrixLocation: Int) {
        
        val modelMatrix = parentTransform.mul(entity.getModelMatrix(), Matrix4f())
        modelMatrix.get(matrixBuffer)
        glUniformMatrix4fv(modelMatrixLocation, false, matrixBuffer)

        val meshComponent = entity.getComponent(MeshComponent::class)!!
        meshComponent.material.texture.bind()
        meshComponent.mesh.render()

        entity.getChildren().forEach { drawEntityRecursive(it, modelMatrix, modelMatrixLocation) }
        
    }
    
    fun render(scene: Scene) {
        
        scene.getActiveCamera()?.let { camera ->
            
            glEnable(GL_DEPTH_TEST)
            
            shader.use()

            val modelMatrixLocation = shader.getUniformLocation("modelMatrix")
            val viewMatrixLocation = shader.getUniformLocation("viewMatrix")
            val projectionMatrixLocation = shader.getUniformLocation("projectionMatrix")
            
            val cameraComponent = camera.getComponent(CameraComponent::class)!!
            cameraComponent.matrix.get(matrixBuffer)
            glUniformMatrix4fv(projectionMatrixLocation, false, matrixBuffer)
                
            viewMatrix.identity()
                    .rotate(camera.rotation.invert(Quaternionf()))
                    .translate(camera.position.mul(-1f, Vector3f()))
                    .get(matrixBuffer)
            glUniformMatrix4fv(viewMatrixLocation, false, matrixBuffer)

            val entities = scene.getEntities()
                    .filter { it.hasComponent(MeshComponent::class) }

            entities.forEach { drawEntityRecursive(it, Matrix4f().identity(), modelMatrixLocation) }

            glUseProgram(0)

        }
        
    }
    
    fun destroy() {
        MemoryUtil.memFree(matrixBuffer)
        shader.delete()
    }
    
}