package com.mcmacker4.blade.render

import com.mcmacker4.blade.BladeEngine
import com.mcmacker4.blade.render.gl.*
import com.mcmacker4.blade.scene.Scene
import org.lwjgl.opengl.GL30.*


class RenderingPipeline {
    
    private val sceneRenderer = SceneRenderer()
    
    private val blitShader = ShaderProgram.loadNamed("/shaders/blit")
    
    private var gBufferPass = GBufferPass(BladeEngine.window.width, BladeEngine.window.height)

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
        
        gBufferPass.render(sceneRenderer, scene)
        
        finalRender(gBufferPass.diffuseTexture)
        
    }
    
    private fun finalRender(texture: Texture2D) {

        blitShader.use()
        
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        
        glDisable(GL_DEPTH_TEST)
        
        glActiveTexture(GL_TEXTURE0)
        texture.bind()
        
        quadVAO.bind()
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)
        
    }
    
    fun onWindowSizeChanged(width: Int, height: Int) {
        if (width > 0 && height > 0) {
            gBufferPass.delete()
            gBufferPass = GBufferPass(width, height)
        }
    }
    
    fun destroy() {
        quadIndices.delete()
        quadVBO.delete()
        quadVAO.delete()
        
        sceneRenderer.destroy()
        gBufferPass.delete()
    }
    
}