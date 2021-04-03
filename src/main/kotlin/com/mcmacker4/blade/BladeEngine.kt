package com.mcmacker4.blade

import com.mcmacker4.blade.display.Window
import com.mcmacker4.blade.input.Keyboard
import com.mcmacker4.blade.input.Mouse
import com.mcmacker4.blade.render.RenderingPipeline
import com.mcmacker4.blade.scene.Scene
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import java.io.Closeable


object BladeEngine : Closeable {
    
    lateinit var window: Window
        private set
    lateinit var scene: Scene
        private set
    
    private lateinit var renderingPipeline: RenderingPipeline
    
    lateinit var keyboard: Keyboard
        private set
    lateinit var mouse: Mouse
        private set
    
    var useAO = true
    
    private var running = true
    
    fun initialize() {
        
        println("Initializing Engine")
        
        println("Java Version: ${System.getProperty("java.version")}")
        
        glfwSetErrorCallback { error, description ->
            println("GLFW Error $error: $description")
        }

        if (!glfwInit()) {
            throw Exception("Could not initialize GLFW.")
        }
        
        window = Window(1280, 720, "Hello Blade Engine")

        glClearColor(0f, 0f, 0f, 0.0f)
        
        keyboard = Keyboard()
        mouse = Mouse()
        
        renderingPipeline = RenderingPipeline()
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
            
//            Runtime.getRuntime().let {
//                val mem = it.totalMemory() - it.freeMemory()
//                println("Memory Usage: ${mem / 1000000}Mb / ${it.totalMemory() / 1000000}Mb")
//            }
            
            glfwPollEvents()
            Timer.update()
            
            scene.update()
            
            renderingPipeline.render(scene)
            
            window.swapBuffers()
            
            while (glfwGetTime() < nextFrameTime) Thread.yield()
        }
        
    }
    
    fun stop() {
        this.running = false
    }
    
    fun onWindowSizeChanged(width: Int, height: Int) {
        println("New resolution: $width, $height")
        renderingPipeline.onWindowSizeChanged(width, height)
    }
    
    override fun close() {
        renderingPipeline.close()
        window.close()

        glfwTerminate()
    }
    
}
