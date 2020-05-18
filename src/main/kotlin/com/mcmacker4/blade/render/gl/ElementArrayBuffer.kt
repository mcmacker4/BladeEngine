package com.mcmacker4.blade.render.gl

import org.lwjgl.opengl.GL15.*


class ElementArrayBuffer(indices: IntArray) {
    
    private val id = glGenBuffers()
    
    init {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }
    
    fun bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id)
    }
    
    fun delete() {
        glDeleteBuffers(id)
    }
    
}