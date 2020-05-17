package com.mcmacker4.blade.render.gl

import com.mcmacker4.blade.file.FileImport.fileToBuffer
import com.mcmacker4.blade.file.FileImport.resourceToBuffer
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.glGenerateMipmap
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer


class Texture2D {
    
    private var id: Int
    val useAlpha: Boolean
    
    constructor(width: Int, height: Int, data: ByteBuffer, format: Int,
                internalFormat: Int = GL_RGBA, type: Int = GL_UNSIGNED_BYTE,
                parameters: Map<Int, Int> = defaultParams(), useAlpha: Boolean = false) {
        id = glGenTextures()
        bind()
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, data)
        glGenerateMipmap(GL_TEXTURE_2D)
        applyParameters(parameters)
        unbind()
        
        this.useAlpha = useAlpha
    }
    
    constructor(width: Int, height: Int, format: Int, internalFormat: Int, type: Int) {
        id = glGenTextures()
        bind()
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, MemoryUtil.NULL)
        unbind()

        this.useAlpha = false
    }
    
    private constructor(id: Int) {
        this.id = id
        useAlpha = false
    }
    
    private fun applyParameters(parameters: Map<Int, Int>) {
        parameters.forEach { (key, value) ->
            glTexParameteri(GL_TEXTURE_2D, key, value)
        }
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
    
    companion object {
        
        val EMPTY = Texture2D(0)
        
        private fun defaultParams() = mapOf(
                Pair(GL_TEXTURE_WRAP_S, GL_REPEAT),
                Pair(GL_TEXTURE_WRAP_T, GL_REPEAT),
                Pair(GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR),
                Pair(GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        )
        
        fun loadFromResource(path: String, parameters: Map<Int, Int> = defaultParams()) : Texture2D {
            println("Loading texture from resource: $path")
            val buffer = resourceToBuffer(path)
            val texture = readTexture(buffer, parameters)
            MemoryUtil.memFree(buffer)
            return texture
        }
        
        fun loadFromFileSystem(path: String, parameters: Map<Int, Int> = defaultParams()) : Texture2D {
            println("Loading texture from file: $path")
            val buffer = fileToBuffer(path)
            val texture = readTexture(buffer, parameters)
            MemoryUtil.memFree(buffer)
            return texture
        }
        
        fun loadFromMemory(width: Int, height: Int, data: ByteBuffer,
                           format: Int = GL_RGBA, parameters: Map<Int, Int> = defaultParams()) : Texture2D {
            return Texture2D(width, height, data, format, parameters = parameters)
        }
        
        fun readTexture(buffer: ByteBuffer, parameters: Map<Int, Int> = defaultParams()) : Texture2D {
            val texture = MemoryStack.stackPush().use { stack ->
                val widthBuff = stack.mallocInt(1)
                val heightBuff = stack.mallocInt(1)
                val channelsBuff = stack.mallocInt(1)

                // TODO: Support for RGBA
                val image = stbi_load_from_memory(buffer, widthBuff, heightBuff, channelsBuff, 4)
                        ?: throw RuntimeException("Failed to load image: ${stbi_failure_reason()}")
                
                val texture = Texture2D(widthBuff.get(), heightBuff.get(), image,
                        GL_RGBA, parameters = parameters, useAlpha = channelsBuff.get() == 4)

                stbi_image_free(image)

                texture
            }

            MemoryUtil.memFree(buffer)

            return texture
        }
        
    }
    
}