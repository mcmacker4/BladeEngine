package com.mcmacker4.blade.display

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.system.MemoryUtil


class Window(private var width: Int, private var height: Int, title: String) {
    
    private val window: Long
    
    val shouldClose: Boolean
        get() = glfwWindowShouldClose(window)
    
    val aspectRatio: Float
            get() = width.toFloat() / height.toFloat()
    
    init {
        glfwDefaultWindowHints()
        
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)

        window = glfwCreateWindow(width, height, title, 0, 0)
        if (window == MemoryUtil.NULL) {
            throw Exception("Could not create Window.")
        }

        glfwSetWindowSizeCallback(window, this::resizeCallback)
        
        glfwMakeContextCurrent(window)
        
        GL.createCapabilities()
        glViewport(0, 0, width, height)
        
        glfwGetVideoMode(glfwGetPrimaryMonitor())?.let { vidmode ->
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - width) / 2,
                    (vidmode.height() - height) / 2
            )
        }
        
        glfwSetKeyCallback(window) { window, key, _, action, _ -> 
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
                glfwSetWindowShouldClose(window, true)
        }
        
        glfwSwapInterval(0)
    }
    
    private fun resizeCallback(window: Long, width: Int, height: Int) {
        if (window == this.window) {
            this.width = width
            this.height = height
            glViewport(0, 0, width, height)
        }
    }
    
    fun show() {
        glfwShowWindow(window)
    }
    
    fun swapBuffers() {
        glfwSwapBuffers(window)
    }
    
    fun destroy() {
        glfwDestroyWindow(window)
    }
    
}