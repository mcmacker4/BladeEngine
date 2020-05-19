package com.mcmacker4.blade.render

import com.mcmacker4.blade.render.framebuffer.GBuffer
import com.mcmacker4.blade.render.gl.ShaderProgram
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.CameraComponent
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import java.io.Closeable


class GBufferPass : Closeable {
    
    private val shader = ShaderProgram.loadNamed("/shaders/gbuffer")
    private val matrixBuffer = MemoryUtil.memAllocFloat(16)
    
    fun render(target: GBuffer, sceneRenderer: SceneRenderer, scene: Scene) {
        
        scene.getActiveCamera()?.let { camera ->

            target.bind(GL_FRAMEBUFFER)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            shader.use()

            val projectionMatrixLocation = shader.getUniformLocation("projectionMatrix")
            val viewMatrixLocation = shader.getUniformLocation("viewMatrix")
            
            val cameraComponent = camera.getComponent(CameraComponent::class)!!
            // View Matrix
            cameraComponent.getViewMatrix().get(matrixBuffer)
            glUniformMatrix4fv(viewMatrixLocation, false, matrixBuffer)
            // Projection Matrix
            cameraComponent.projectionMatrix.get(matrixBuffer)
            glUniformMatrix4fv(projectionMatrixLocation, false, matrixBuffer)
            
            sceneRenderer.prepare(scene)

            sceneRenderer.render(shader)

            sceneRenderer.finish()

            glUseProgram(0)
            
        }
        
    }

    override fun close() {
        shader.close()
        MemoryUtil.memFree(matrixBuffer)
    }
    
}