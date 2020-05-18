package com.mcmacker4.blade.render

import com.mcmacker4.blade.render.gl.FrameBuffer
import com.mcmacker4.blade.render.gl.RenderBuffer
import com.mcmacker4.blade.render.gl.ShaderProgram
import com.mcmacker4.blade.render.gl.Texture2D
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.CameraComponent
import org.lwjgl.opengl.GL20.glDrawBuffers
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil


class GBufferPass(width: Int, height: Int) {
    
    private val frameBuffer = FrameBuffer()
    
    private val shader = ShaderProgram.loadNamed("/shaders/gbuffer")
    
    private val matrixBuffer = MemoryUtil.memAllocFloat(16)
    
    private val textureProps = mapOf(
            Pair(GL_TEXTURE_MIN_FILTER, GL_NEAREST),
            Pair(GL_TEXTURE_MIN_FILTER, GL_NEAREST),
            Pair(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE),
            Pair(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    )
    
    val positionTexture = Texture2D(width, height, GL_RGBA, GL_RGBA16F, GL_FLOAT, textureProps)
    val normalTexture = Texture2D(width, height, GL_RGBA, GL_RGBA16F, GL_FLOAT, textureProps)
    val diffuseTexture = Texture2D(width, height, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE, textureProps)
    
    private val depthStencilBuffer = RenderBuffer(width, height, GL_DEPTH24_STENCIL8)
    
    init {
        frameBuffer.bind(GL_FRAMEBUFFER)
        frameBuffer.attachTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, positionTexture)
        frameBuffer.attachTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, normalTexture)
        frameBuffer.attachTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, diffuseTexture)
        frameBuffer.attachRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, depthStencilBuffer)
        
        if (!frameBuffer.isComplete(GL_FRAMEBUFFER))
            throw Exception("Framebuffer is not complete.")
    }
    
    fun render(sceneRenderer: SceneRenderer, scene: Scene) {
        
        scene.getActiveCamera()?.let { camera ->

            frameBuffer.bind(GL_DRAW_FRAMEBUFFER)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            shader.use()

            val projectionMatrixLocation = shader.getUniformLocation("projectionMatrix")
            val viewMatrixLocation = shader.getUniformLocation("viewMatrix")
            
            val cameraComponent = camera.getComponent(CameraComponent::class)!!
            cameraComponent.getViewMatrix().get(matrixBuffer)
            glUniformMatrix4fv(viewMatrixLocation, false, matrixBuffer)
            cameraComponent.projectionMatrix.get(matrixBuffer)
            glUniformMatrix4fv(projectionMatrixLocation, false, matrixBuffer)

            sceneRenderer.prepare(scene)

            glDrawBuffers(intArrayOf(
                    GL_COLOR_ATTACHMENT0,
                    GL_COLOR_ATTACHMENT1,
                    GL_COLOR_ATTACHMENT2
            ))

            sceneRenderer.render(scene, shader)

            sceneRenderer.finish()

            glUseProgram(0)
            
        }
        
    }
    
    fun delete() {
        frameBuffer.delete()
        positionTexture.delete()
        normalTexture.delete()
        diffuseTexture.delete()
    }
    
}