package com.mcmacker4.blade.scene.components

import com.mcmacker4.blade.BladeEngine
import com.mcmacker4.blade.scene.Component
import org.joml.Matrix4f


class CameraComponent(
        fov: Double,
        nearPlane: Float = 0.01f,
        farPlane: Float = 1000f
) : Component() {
    
    val matrix: Matrix4f = Matrix4f().identity()
            .perspective(fov.toFloat(), BladeEngine.window.aspectRatio, nearPlane, farPlane)
    
    override fun onInit() {}
    override fun onUpdate() {}

}