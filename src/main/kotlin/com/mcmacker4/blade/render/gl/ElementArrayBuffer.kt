package com.mcmacker4.blade.render.gl

import org.lwjgl.opengl.GL15.*
import java.io.Closeable


class ElementArrayBuffer(indices: IntArray) : Closeable {
    
    private val id = glGenBuffers()
    
    init {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }
    
    fun bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id)
    }
    
    override fun close() {
        glDeleteBuffers(id)
    }
    
}