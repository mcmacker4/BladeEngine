package com.mcmacker4.blade.file

import com.mcmacker4.blade.render.gl.Texture2D
import org.lwjgl.system.MemoryUtil
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.ByteBuffer
import java.nio.channels.Channels


object FileImport {

    fun fileToBuffer(path: String) : ByteBuffer {
        val file = File(path)
        val source = FileInputStream(file)
        val length = file.length()
        Channels.newChannel(source).use { channel ->
            val buffer = MemoryUtil.memAlloc(length.toInt() + 1)
            var readBytes = 0
            while (readBytes != -1) {
                readBytes = channel.read(buffer)
            }
            buffer.flip()
            return buffer
        }
    }

    fun resourceToBuffer(path: String) : ByteBuffer {
        Texture2D::class.java.getResourceAsStream(path)?.use { source ->
            val url = Texture2D::class.java.getResource(path)
            val length = File(url.toURI()).length()
            Channels.newChannel(source).use { channel ->
                val buffer = MemoryUtil.memAlloc(length.toInt() + 1)
                var readBytes = 0
                while (readBytes != -1) {
                    readBytes = channel.read(buffer)
                }
                buffer.flip()
                return buffer
            }
        } ?: throw FileNotFoundException(path)
    }
    
}