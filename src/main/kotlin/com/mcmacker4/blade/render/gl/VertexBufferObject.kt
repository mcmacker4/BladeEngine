package com.mcmacker4.blade.render.gl

import org.lwjgl.opengl.GL15.*


class VertexBufferObject {
    
    private var id: Int = glGenBuffers()

    constructor(data: FloatArray, usage: Int) {
        glBindBuffer(GL_ARRAY_BUFFER, id)
        glBufferData(GL_ARRAY_BUFFER, data, usage);
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }
    
    constructor(size: Long, usage: Int) {
        glBindBuffer(GL_ARRAY_BUFFER, id)
        glBufferData(GL_ARRAY_BUFFER, size, usage)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }
    
    fun bind() {
        glBindBuffer(GL_ARRAY_BUFFER, id)
    }
    
    fun unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }
    
    fun write(offset: Long, data: FloatArray) {
        glBufferSubData(GL_ARRAY_BUFFER, offset, data)
    }
    
    fun delete() {
        glDeleteBuffers(id)
        id = 0
    }
    
}