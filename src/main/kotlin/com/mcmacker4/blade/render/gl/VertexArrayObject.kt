package com.mcmacker4.blade.render.gl

import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.*


class VertexArrayObject(vararg attribs: VertexAttribute) {
    
    private val id = glGenVertexArrays()
    
    init {
        bind()
        attribs.forEach(this::bindAttribute)
        unbind()
    }
    
    fun bindAttribute(attrib: VertexAttribute) {
        attrib.vbo.bind()
        glVertexAttribPointer(attrib.index, attrib.size, attrib.type, attrib.normalized, attrib.stride, 0)
        glEnableVertexAttribArray(attrib.index)
        attrib.vbo.unbind()
    }

    fun bind() {
        glBindVertexArray(id)
    }
    
    fun unbind() {
        glBindVertexArray(0)
    }
    
    fun delete() {
        glDeleteVertexArrays(id)
    }
    
}