package com.mcmacker4.blade.render.gl

import org.lwjgl.opengl.GL30.*
import java.io.Closeable


class RenderBuffer(
        width: Int,
        height: Int,
        internalFormat: Int
) : Closeable {
    
    private val id = glGenRenderbuffers()
    
    init {
        bind(GL_RENDERBUFFER)
        glRenderbufferStorage(GL_RENDERBUFFER, internalFormat, width, height)
        glBindRenderbuffer(GL_RENDERBUFFER, 0)
    }
    
    fun bind(target: Int) {
        glBindRenderbuffer(target, id)
    }
    
    fun ref() = id
    
    override fun close() {
        glDeleteRenderbuffers(id)
    }
    
}