package com.mcmacker4.blade.render.gl

import org.lwjgl.opengl.GL20.*
import java.io.Closeable


class ShaderProgram(vSource: String, fSource: String) : Closeable {
    
    private var id: Int
    private val uniformCache = hashMapOf<String, Int>()
    
    init {
        val vShader = compileShader(GL_VERTEX_SHADER, vSource)
        val fShader = compileShader(GL_FRAGMENT_SHADER, fSource)
        
        id = glCreateProgram()
        glAttachShader(id, vShader)
        glAttachShader(id, fShader)
        
        glLinkProgram(id)
        checkProgramStatus(id, GL_LINK_STATUS)

        glValidateProgram(id)
        checkProgramStatus(id, GL_VALIDATE_STATUS)
        
        glDeleteShader(vShader)
        glDeleteShader(fShader)
    }
    
    fun use() {
        glUseProgram(id)
    }
    
    fun getUniformLocation(name: String)
            = uniformCache.getOrPut(name) { glGetUniformLocation(id, name) }
    
    override fun close() {
        glDeleteProgram(id)
        id = 0
    }
    
    companion object {

        fun loadNamed(name: String): ShaderProgram {
            println("Loading shader: $name")
            val vSource = readFileToString("$name.v.glsl")
            val fSource = readFileToString("$name.f.glsl")
            return ShaderProgram(vSource, fSource)
        }

        private fun readFileToString(path: String): String {
            return ShaderProgram::class.java.getResourceAsStream(path)?.use {
                it.bufferedReader().readText()
            } ?: throw Exception("File not found: $path")
        }
        
        private fun compileShader(type: Int, source: String): Int {
            val shader = glCreateShader(type)
            glShaderSource(shader, source)
            
            glCompileShader(shader)
            if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE)
                throw Exception(glGetShaderInfoLog(shader))
            
            return shader
        }
        
        private fun checkProgramStatus(program: Int, status: Int) {
            if (glGetProgrami(program, status) != GL_TRUE)
                throw Exception(glGetProgramInfoLog(program))
        }
        
    }
    
}