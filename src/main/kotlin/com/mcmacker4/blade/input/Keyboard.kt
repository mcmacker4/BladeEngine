package com.mcmacker4.blade.input

import com.mcmacker4.blade.BladeEngine
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.glfwGetKey


class Keyboard {
    
    fun isKeyDown(key: Int) = glfwGetKey(BladeEngine.window.ref(), key) == GLFW_PRESS
    
}