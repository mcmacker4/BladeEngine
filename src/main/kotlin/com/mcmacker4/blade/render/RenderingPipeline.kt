package com.mcmacker4.blade.render

import com.mcmacker4.blade.BladeEngine
import com.mcmacker4.blade.render.framebuffer.GBuffer
import com.mcmacker4.blade.render.framebuffer.LightingBuffer
import com.mcmacker4.blade.render.gl.*
import com.mcmacker4.blade.scene.Scene
import org.lwjgl.opengl.GL30.*
import java.io.Closeable


class RenderingPipeline : Closeable {
    
    private val sceneRenderer = SceneRenderer()
    
    private val blitShader = ShaderProgram.loadNamed("/shaders/blit")
    
    private var gBufferPass = GBufferPass()
    private var lightingPass = LightingPass()
    
    private var gBuffer = GBuffer(BladeEngine.window.width, BladeEngine.window.height)
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
        lightingPass.render(
                lightingBuffer,
                quadVAO,
                gBuffer,
                scene
        )

//        blitTexture(gBuffer.metallicRoughnessTexture)
        
    }
    
    private fun blitTexture(texture: Texture2D) {

        blitShader.use()

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

//            lightingBuffer.close()
//            lightingBuffer = LightingBuffer(width, height)
        }
    }

    override fun close() {
//        quadIndices.close()
//        quadVBO.close()
//        quadVAO.close()
//
        gBufferPass.close()
//        lightingPass.close()
        
        gBuffer.close()
//        lightingBuffer.close()

        sceneRenderer.close()
    }
    
}