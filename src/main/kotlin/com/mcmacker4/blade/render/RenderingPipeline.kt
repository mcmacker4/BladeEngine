package com.mcmacker4.blade.render

import com.mcmacker4.blade.BladeEngine
import com.mcmacker4.blade.render.framebuffer.GBuffer
import com.mcmacker4.blade.render.framebuffer.LightingBuffer
import com.mcmacker4.blade.render.framebuffer.SSAOBlurBuffer
import com.mcmacker4.blade.render.framebuffer.SSAOBuffer
import com.mcmacker4.blade.render.gl.*
import com.mcmacker4.blade.render.passes.GBufferPass
import com.mcmacker4.blade.render.passes.LightingPass
import com.mcmacker4.blade.render.passes.SSAOBlurPass
import com.mcmacker4.blade.render.passes.SSAOPass
import com.mcmacker4.blade.scene.Scene
import org.lwjgl.opengl.GL30.*
import java.io.Closeable


class RenderingPipeline : Closeable {
    
    private val sceneRenderer = SceneRenderer()
    
    private val blitShader = ShaderProgram.loadNamed("/shaders/blit")
    
    private val gBufferPass = GBufferPass()
    private val ssaoPass = SSAOPass()
    private val ssaoBlurPass = SSAOBlurPass()
    private val lightingPass = LightingPass()
    
    private var gBuffer = GBuffer(BladeEngine.window.width, BladeEngine.window.height)
    private var ssaoBuffer = SSAOBuffer(BladeEngine.window.width, BladeEngine.window.height)
    private var ssaoBlurBuffer = SSAOBlurBuffer(BladeEngine.window.width, BladeEngine.window.height)
    private var lightingBuffer = LightingBuffer(BladeEngine.window.width, BladeEngine.window.height)
    
    private val quadIndices = ElementArrayBuffer(intArrayOf(0, 1, 2, 0, 2, 3))
    private val quadVBO = VertexBufferObject(floatArrayOf(
            -1f, 1f, 0f,
            -1f, -1f, 0f,
            1f, -1f, 0f,
            1f,  1f, 0f
    ), GL_STATIC_DRAW)

    private val quadVAO = VertexArrayObject(quadIndices, listOf(
            VertexAttribute(0, quadVBO, GL_FLOAT, 3)
    ))
    
    fun render(scene: Scene) {
        
        gBufferPass.render(gBuffer, sceneRenderer, scene)

        if (BladeEngine.useAO) {
            ssaoPass.render(ssaoBuffer, quadVAO, gBuffer, scene)
            ssaoBlurPass.render(ssaoBlurBuffer, quadVAO, ssaoBuffer)
        }

        lightingPass.render(lightingBuffer, quadVAO, gBuffer, ssaoBlurBuffer, scene)

        blitTexture(lightingBuffer.result)
        
    }
    
    private fun blitTexture(texture: Texture2D) {

        blitShader.use()
        glViewport(0, 0, BladeEngine.window.width, BladeEngine.window.height)

        glDisable(GL_DEPTH_TEST)

        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0)
        glClear(GL_COLOR_BUFFER_BIT)

        glUniform1i(blitShader.getUniformLocation("blitTexture"), 0)
        glActiveTexture(GL_TEXTURE0)
        texture.bind()

        quadVAO.bind()
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)

    }
    
    fun onWindowSizeChanged(width: Int, height: Int) {
        if (width > 0 && height > 0) {
            gBuffer.close()
            gBuffer = GBuffer(width, height)
            
            ssaoBuffer.close()
            ssaoBuffer = SSAOBuffer(width, height)
            
            ssaoBlurBuffer.close()
            ssaoBlurBuffer = SSAOBlurBuffer(width, height)

            lightingBuffer.close()
            lightingBuffer = LightingBuffer(width, height)
        }
    }

    override fun close() {
        quadIndices.close()
        quadVBO.close()
        quadVAO.close()

        gBufferPass.close()
        ssaoPass.close()
        ssaoBlurPass.close()
        lightingPass.close()
        
        gBuffer.close()
        ssaoBuffer.close()
        ssaoBlurBuffer.close()
        lightingBuffer.close()
        
        sceneRenderer.close()
    }
    
}