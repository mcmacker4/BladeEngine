package com.mcmacker4.blade.render.framebuffer

import com.mcmacker4.blade.render.gl.FrameBuffer
import com.mcmacker4.blade.render.gl.RenderBuffer
import com.mcmacker4.blade.render.gl.Texture2D
import org.lwjgl.opengl.GL30.*
import java.io.Closeable


class LightingBuffer(width: Int, height: Int) : FrameBuffer(), Closeable {

    val result = Texture2D(width, height, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE)
    private val depthStencilBuffer = RenderBuffer(width, height, GL_DEPTH24_STENCIL8)
    
    init {
        bind(GL_FRAMEBUFFER)
        attachTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, result)
        attachRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, depthStencilBuffer)
        
        glDrawBuffers(intArrayOf(GL_COLOR_ATTACHMENT0))

        if (!isComplete(GL_FRAMEBUFFER))
            throw Exception("Framebuffer is not complete.")
        
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }
    
    override fun close() {
        result.close()
        depthStencilBuffer.close()
        super.close()
    }
    
}