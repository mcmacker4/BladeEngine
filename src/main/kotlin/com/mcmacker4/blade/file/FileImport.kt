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
            Channels.newChannel(source).use { channel ->
                var buffer = MemoryUtil.memAlloc(8 * 1024) // 8KB initial size
                while (true) {
                    val bytes = channel.read(buffer)
                    if (bytes == -1) break
                    if (buffer.remaining() == 0)
                        buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() * 3 / 2) // 50%
                }
                buffer.flip()
                return buffer
            }
        }

        throw FileNotFoundException(path)
    }
    
}