package com.mcmacker4.blade.render.gl

import org.lwjgl.opengl.GL20.*


class ShaderProgram(vSource: String, fSource: String) {
    
    private var id: Int
    
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
    
    fun delete() {
        glDeleteProgram(id)
        id = 0
    }
    
    companion object {

        fun loadNamed(name: String): ShaderProgram {
            val vSource = readFileToString("/$name.v.glsl")
            val fSource = readFileToString("/$name.f.glsl")
            return ShaderProgram(vSource, fSource)
        }

        private fun readFileToString(path: String): String {
            return ShaderProgram::class.java.getResourceAsStream(path)?.use {
                it.bufferedReader().readText()
            } ?: throw Exception("File not found.")
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