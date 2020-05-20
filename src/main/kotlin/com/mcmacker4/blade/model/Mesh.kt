package com.mcmacker4.blade.model

import com.mcmacker4.blade.render.gl.ElementArrayBuffer
import com.mcmacker4.blade.render.gl.VertexArrayObject
import com.mcmacker4.blade.render.gl.VertexAttribute
import com.mcmacker4.blade.render.gl.VertexBufferObject
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW
import java.io.Closeable

class Mesh(
        indices: IntArray,
        positions: FloatArray,
        normals: FloatArray,
        tangents: FloatArray,
        uvcoords: FloatArray
) : Closeable {
    
    private val count = indices.size
    
    private val elements = ElementArrayBuffer(indices)
    
    private val attributes = arrayListOf(
            VertexAttribute(0, VertexBufferObject(positions, GL_STATIC_DRAW), GL_FLOAT, 3),
            VertexAttribute(1, VertexBufferObject(normals, GL_STATIC_DRAW), GL_FLOAT, 3),
            VertexAttribute(2, VertexBufferObject(tangents, GL_STATIC_DRAW), GL_FLOAT, 3),
            VertexAttribute(3, VertexBufferObject(uvcoords, GL_STATIC_DRAW), GL_FLOAT, 2)
    )
    
    private val vao = VertexArrayObject(elements, attributes)
    
    fun render() {
        vao.bind()
        glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0)
        vao.unbind()
    }
    
    override fun close() {
        elements.close()
        attributes.forEach { it.vbo.close() }
        vao.close()
    }
    
}