package com.mcmacker4.blade.render.gl

import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer


class Texture2D {
    
    private var id = glGenTextures()
    
    constructor(width: Int, height: Int, data: ByteBuffer, format: Int, internalFormat: Int, type: Int) {
        bind()
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, data)
        unbind()
    }
    
    constructor(width: Int, height: Int, format: Int, internalFormat: Int, type: Int) {
        bind()
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, MemoryUtil.NULL)
        unbind()
    }
    
    fun bind() {
        glBindTexture(GL_TEXTURE_2D, id)
    }
    
    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }
    
    fun delete() {
        glDeleteTextures(id)
        id = 0
    }
    
}