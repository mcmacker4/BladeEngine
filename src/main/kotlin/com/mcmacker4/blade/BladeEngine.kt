package com.mcmacker4.blade

import com.mcmacker4.blade.display.Window
import com.mcmacker4.blade.scene.Scene
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import java.lang.Exception


class BladeEngine {
    
    private val window: Window
    
    init {
        
        glfwSetErrorCallback { error, description ->  
            println("GLFW Error $error: $description")
        }
        
        if (!glfwInit()) {
            throw Exception("Could not initialize GLFW.")
        }
        
        window = Window(1280, 720, "Hello Blade Engine")
        
        glClearColor(0.3f, 0.6f, 0.9f, 1.0f)
        
    }
    
    fun start() {
        
        window.show()
        
        val scene = Scene()
        
        while (!window.shouldClose) {
            glfwPollEvents()
            
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            scene.render()
            
            window.swapBuffers()
        }
        
        scene.destroy()
        window.destroy()
        
        glfwTerminate()
        
    }
    
}
