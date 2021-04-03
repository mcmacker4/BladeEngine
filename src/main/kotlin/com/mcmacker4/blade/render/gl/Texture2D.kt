package com.mcmacker4.blade.render.gl

import com.mcmacker4.blade.file.FileImport
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL30.glGenerateMipmap
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.FloatBuffer


class Texture2D : Texture, Closeable {
    
    val useAlpha: Boolean
    
    constructor(width: Int, height: Int, data: ByteBuffer, format: Int,
                internalFormat: Int = GL_RGBA,
                useAlpha: Boolean = false) : super() {
        bind()
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, data)
        this.useAlpha = useAlpha
    }

    constructor(width: Int, height: Int, data: FloatBuffer, format: Int,
                internalFormat: Int = GL_RGBA,
                useAlpha: Boolean = false) : super() {
        bind()
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_FLOAT, data)
        this.useAlpha = useAlpha
    }
    
    constructor(width: Int, height: Int, format: Int, internalFormat: Int, type: Int) : super() {
        bind()
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, 0)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        this.useAlpha = false
    }
    
    private constructor(id: Int) : super(id) {
        useAlpha = false
    }
    
    fun bind() {
        bind(GL_TEXTURE_2D)
    }
    
    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }
    
    companion object {
        
        val EMPTY = Texture2D(0)
        
        fun loadFromResource(path: String) : Texture2D {
            println("Loading texture from resource: $path")
            val buffer = FileImport.resourceToBuffer(path)
            val texture = readTexture(buffer)
            MemoryUtil.memFree(buffer)
            return texture
        }
        
        fun loadFromResourceFloat(path: String) {
            println("Loading texture from resource: $path")
        }
        
        fun loadFromFileSystem(path: String) : Texture2D {
            println("Loading texture from file: $path")
            val buffer = FileImport.fileToBuffer(path)
            val texture = readTexture(buffer)
            MemoryUtil.memFree(buffer)
            return texture
        }
        
        fun loadFromMemory(width: Int, height: Int, data: ByteBuffer,
                           format: Int = GL_RGBA) : Texture2D {
            val texture = Texture2D(width, height, data, format)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glGenerateMipmap(GL_TEXTURE_2D)
            texture.unbind()
            return texture
        }
        
        fun readTexture(buffer: ByteBuffer) : Texture2D {
            return MemoryStack.stackPush().use { stack ->
                val widthBuff = stack.mallocInt(1)
                val heightBuff = stack.mallocInt(1)
                val channelsBuff = stack.mallocInt(1)

                // TODO: Support for RGBA
                val image = stbi_load_from_memory(buffer, widthBuff, heightBuff, channelsBuff, 4)
                        ?: throw RuntimeException("Failed to load image: ${stbi_failure_reason()}")
                
//                println("Texture size: ${widthBuff.get(0)}x${heightBuff.get(0)}")
                
                val texture = Texture2D(widthBuff.get(), heightBuff.get(), image,
                        GL_RGBA, useAlpha = channelsBuff.get() == 4)

                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

                glGenerateMipmap(GL_TEXTURE_2D)

                stbi_image_free(image)
                
                texture.unbind()

                texture
            }
        }
        
        fun readTextureFloats(buffer: ByteBuffer) {
            return MemoryStack.stackPush().use { stack ->
                val widthBuff = stack.mallocInt(1)
                val heightBuff = stack.mallocInt(1)
                val channelsBuff = stack.mallocInt(1)
                
                val image = stbi_loadf_from_memory(buffer, widthBuff, heightBuff, channelsBuff, 0)
                
                
            }
        }
        
    }
    
}