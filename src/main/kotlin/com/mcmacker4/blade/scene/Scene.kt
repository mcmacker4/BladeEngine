package com.mcmacker4.blade.scene

import com.mcmacker4.blade.render.gl.ShaderProgram
import com.mcmacker4.blade.render.gl.VertexArrayObject
import com.mcmacker4.blade.render.gl.VertexAttribute
import com.mcmacker4.blade.render.gl.VertexBufferObject
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW
import org.lwjgl.opengl.GL20.glUseProgram


class Scene {
    
    private val shader = ShaderProgram.loadNamed("shader")
    
    private val positions = VertexBufferObject(
            floatArrayOf(
                    -0.5f, -0.5f, 0f,
                    0.5f, -0.5f, 0f,
                    0f, 0.5f, 0f
            ),
            GL_STATIC_DRAW
    )
    
    private val vao = VertexArrayObject(
            VertexAttribute(0, positions, GL_FLOAT, 3)
    )
    
    fun render() {
        
        shader.use()
        vao.bind()
        
        glDrawArrays(GL_TRIANGLES, 0, 3)
        
        vao.unbind()
        glUseProgram(0)
        
    }
    
    fun destroy() {
        shader.delete()
        positions.delete()
        vao.delete()
    }
    
}