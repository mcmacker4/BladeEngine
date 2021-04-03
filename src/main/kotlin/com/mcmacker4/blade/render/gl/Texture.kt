package com.mcmacker4.blade.render.gl

import org.lwjgl.opengl.GL11.*
import java.io.Closeable


abstract class Texture(private val id: Int = glGenTextures()) : Closeable {
    
    fun ref() = id

    override fun close() {
        glDeleteTextures(id)
    }
    
    protected fun bind(target: Int) = glBindTexture(target, id)
    
}