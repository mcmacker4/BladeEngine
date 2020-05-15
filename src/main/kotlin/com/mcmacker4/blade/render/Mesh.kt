package com.mcmacker4.blade.render

import com.mcmacker4.blade.render.gl.VertexArrayObject
import com.mcmacker4.blade.render.gl.VertexAttribute
import com.mcmacker4.blade.render.gl.VertexBufferObject
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW

class Mesh(positions: FloatArray, normals: FloatArray, uvcoords: FloatArray) {
    
    private val count = positions.size / 3
    
    private val attributes = arrayListOf(
            VertexAttribute(0, VertexBufferObject(positions, GL_STATIC_DRAW), GL_FLOAT, 3),
            VertexAttribute(1, VertexBufferObject(normals, GL_STATIC_DRAW), GL_FLOAT, 3),
            VertexAttribute(2, VertexBufferObject(uvcoords, GL_STATIC_DRAW), GL_FLOAT, 2)
    )
    
    private val vao = VertexArrayObject(attributes)
    
    fun render() {
        vao.bind()
        glDrawArrays(GL_TRIANGLES, 0, count)
        vao.unbind()
    }
    
    fun delete() {
        attributes.forEach { it.vbo.delete() }
        vao.delete()
    }
    
}