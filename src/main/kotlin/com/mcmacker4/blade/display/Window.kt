package com.mcmacker4.blade.display

import com.mcmacker4.blade.BladeEngine
import org.joml.Vector2i
import org.joml.Vector2ic
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.system.Callback
import org.lwjgl.system.MemoryStack
import java.io.Closeable


class Window(width: Int, height: Int, title: String) : Closeable {
   
    var width: Int = width
        private set
    var height: Int = height
        private set
    
    private val window: Long
    
    val shouldClose: Boolean
        get() = glfwWindowShouldClose(window)
    
    var isFullscreen = false
        private set
    
    val aspectRatio: Float
        get() = width.toFloat() / height.toFloat()
    
    private val debugProc: Callback?
    
    val framebufferSize: Vector2ic
        get() {
            val size = Vector2i()
            MemoryStack.stackPush().use { stack ->
                val width = stack.mallocInt(1)
                val height = stack.mallocInt(1)
                glfwGetFramebufferSize(window, width, height)
                size.set(width.get(), height.get())
            }
            return size
        }
    
    init {
        glfwDefaultWindowHints()
        
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6)

        window = glfwCreateWindow(width, height, title, 0, 0)
        if (window == 0L) {
            throw Exception("Could not create Window.")
        }
        
        println("Window has been created.")
        
        glfwMakeContextCurrent(window)
        
        GL.createCapabilities()
        debugProc = null
//        debugProc = GLUtil.setupDebugMessageCallback()
        
        glViewport(0, 0, width, height)
        
        glfwSetWindowSizeCallback(window) { _, w, h ->  
            this.width = w
            this.height = h
            glViewport(0, 0, w, h)
            BladeEngine.onWindowSizeChanged(w, h)
        }
        
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
    
    fun show() {
        glfwShowWindow(window)
    }
    
    fun goFullscreen() {
        val monitor = glfwGetPrimaryMonitor()
        glfwGetVideoMode(monitor)?.let { vidmode ->
            glfwSetWindowMonitor(window, monitor, 0, 0, vidmode.width(), vidmode.height(), GLFW_DONT_CARE)
            isFullscreen = true
        }
    }
    
    fun goWindowed() {
        width = 1280
        height = 720
        glfwGetVideoMode(glfwGetPrimaryMonitor())?.let { vidmode ->
            glfwSetWindowMonitor(window, 0,
                    (vidmode.width() - width) / 2, (vidmode.height() - height) / 2,
                    width, height,
                    GLFW_DONT_CARE)
            isFullscreen = false
        }
    }
    
    fun swapBuffers() {
        glfwSwapBuffers(window)
    }
    
    override fun close() {
        glfwDestroyWindow(window)
        debugProc?.apply { free() }
    }
    
    fun ref() = window
    
}