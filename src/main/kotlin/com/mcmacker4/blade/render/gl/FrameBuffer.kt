package com.mcmacker4.blade.render.gl

import org.lwjgl.opengl.GL30.*
import java.io.Closeable


open class FrameBuffer : Closeable {

    private val id: Int = glGenFramebuffers()
    
    fun bind(target: Int) {
        glBindFramebuffer(target, id)
    }
    
    fun attachTexture2D(target: Int, attachment: Int, texture: Texture2D, level: Int = 0) {
        glFramebufferTexture2D(target, attachment, GL_TEXTURE_2D, texture.ref(), level)
    }
    
    fun attachRenderbuffer(target: Int, attachment: Int, renderBuffer: RenderBuffer) {
        glFramebufferRenderbuffer(target, attachment, GL_RENDERBUFFER, renderBuffer.ref())
    }
    
    fun isComplete(target: Int) = glCheckFramebufferStatus(target) == GL_FRAMEBUFFER_COMPLETE
    
    override fun close() {
        glDeleteFramebuffers(id)
    }
    
}