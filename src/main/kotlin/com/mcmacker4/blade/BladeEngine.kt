package com.mcmacker4.blade

import com.mcmacker4.blade.display.Window
import com.mcmacker4.blade.input.Keyboard
import com.mcmacker4.blade.input.Mouse
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
    
    lateinit var keyboard: Keyboard
        private set
    lateinit var mouse: Mouse
        private set
    
    private var running = true
    
    fun initialize() {
        
        println("Initializing Engine")
        
        glfwSetErrorCallback { error, description ->
            println("GLFW Error $error: $description")
        }

        if (!glfwInit()) {
            throw Exception("Could not initialize GLFW.")
        }
        
        window = Window(1280, 720, "Hello Blade Engine")

//        glClearColor(0.3f, 0.6f, 0.9f, 1.0f)
        glClearColor(0f, 0f, 0f, 1.0f)
        
        keyboard = Keyboard()
        mouse = Mouse()
        
        sceneRenderer = SceneRenderer()
    }
    
    fun start(scene: Scene) {
        this.scene = scene
        
        window.show()

        val maxFPS = 300.0
        val frameTime = 1 / maxFPS
        
        Timer.start()
        
        glEnable(GL_CULL_FACE)
        
        glfwSetCursorPosCallback(window.ref()) { _, xpos, ypos ->  
            val dx = xpos - mouse.xpos
            val dy = ypos - mouse.ypos
            mouse.update(xpos, ypos)
            scene.propagateMouseEvent(xpos, ypos, dx, dy)
        }
        
        glfwSetKeyCallback(window.ref()) { _, key, _, action, _ ->  
            scene.propagateKeyEvent(key, action)
        }
        
        while (running && !window.shouldClose) {
            val nextFrameTime = glfwGetTime() + frameTime
            
            glfwPollEvents()
            Timer.update()
            
            scene.update()
            
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            sceneRenderer.render(scene)
            
            window.swapBuffers()
            
            while (glfwGetTime() < nextFrameTime) Thread.yield()
        }
        
    }
    
    fun stop() {
        this.running = false
    }
    
    fun destroy() {
        sceneRenderer.destroy()
        window.destroy()

        glfwTerminate()
    }
    
}
