package com.mcmacker4.blade

import org.lwjgl.glfw.GLFW.glfwGetTime


object Timer {
    
    var now = 0.0
        private set
    
    var delta: Double = 0.0
        private set
    
    fun start() {
        now = glfwGetTime()
    }
    
    fun update() {
        val lastTime = now
        now = glfwGetTime()
        delta = now - lastTime
    }
    
}