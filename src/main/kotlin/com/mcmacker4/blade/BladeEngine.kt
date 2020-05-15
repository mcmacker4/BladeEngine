package com.mcmacker4.blade

import com.mcmacker4.blade.display.Window
import com.mcmacker4.blade.render.SceneRenderer
import com.mcmacker4.blade.scene.Scene
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*


object BladeEngine {
    
    private lateinit var window: Window
    private lateinit var scene: Scene
    
    private lateinit var sceneRenderer: SceneRenderer
    
    fun initialize() {
        glfwSetErrorCallback { error, description ->
            println("GLFW Error $error: $description")
        }

        if (!glfwInit()) {
            throw Exception("Could not initialize GLFW.")
        }

        window = Window(1280, 720, "Hello Blade Engine")

        glClearColor(0.3f, 0.6f, 0.9f, 1.0f)
        
        sceneRenderer = SceneRenderer()
    }
    
    fun setScene(scene: Scene) {
        this.scene = scene
    }
    
    fun start(scene: Scene) {
        this.scene = scene
        
        window.show()
        
        while (!window.shouldClose) {
            glfwPollEvents()
            
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            sceneRenderer.render(scene)
            
            window.swapBuffers()
        }
        
        scene.destroy()
        window.destroy()
        
        glfwTerminate()
        
    }
    
}
