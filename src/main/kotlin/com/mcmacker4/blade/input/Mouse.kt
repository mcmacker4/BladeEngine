package com.mcmacker4.blade.input

import com.mcmacker4.blade.BladeEngine
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryStack


class Mouse {
    
    var xpos = 0.0
        private set
    var ypos = 0.0
        private set
    
    var isGrabbed = false
        private set
    
    fun grab() {
        isGrabbed = true
        glfwSetInputMode(BladeEngine.window.ref(), GLFW_CURSOR, GLFW_CURSOR_DISABLED)
    }
    
    fun release() {
        isGrabbed = false
        glfwSetInputMode(BladeEngine.window.ref(), GLFW_CURSOR, GLFW_CURSOR_NORMAL)
    }
    
    internal fun update(newx: Double, newy: Double) {
        xpos = newx
        ypos = newy
    }
    
}