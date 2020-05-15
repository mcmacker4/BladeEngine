package com.mcmacker4.blade

import com.mcmacker4.blade.display.Window
import com.mcmacker4.blade.render.SceneRenderer
import com.mcmacker4.blade.scene.Scene
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*


object BladeEngine {
    
    lateinit var window: Window
        private set
    lateinit var scene: Scene
        private set
    
    private lateinit var sceneRenderer: SceneRenderer
    
    fun initialize() {
        
        println("Initializing Engine")
        
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

        val maxFPS = 300.0
        val frameTime = 1 / maxFPS
        
        Timer.start()
        
        while (!window.shouldClose) {
            val nextFrameTime = glfwGetTime() + frameTime
            
            glfwPollEvents()
            Timer.update()
            
            scene.update()
            
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            sceneRenderer.render(scene)
            
            window.swapBuffers()
            
            while (glfwGetTime() < nextFrameTime) Thread.yield()
        }
        
        scene.destroy()
        sceneRenderer.destroy()
        window.destroy()
        
        glfwTerminate()
        
    }
    
}
