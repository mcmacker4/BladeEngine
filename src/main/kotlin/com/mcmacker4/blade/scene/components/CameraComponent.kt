package com.mcmacker4.blade.scene.components

import com.mcmacker4.blade.BladeEngine
import com.mcmacker4.blade.scene.Component
import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Quaternionf


class CameraComponent(
        fov: Double,
        nearPlane: Float = 0.01f,
        farPlane: Float = 1000f
) : Component() {
    
    val projectionMatrix: Matrix4fc = Matrix4f().identity()
            .perspective(fov.toFloat(), BladeEngine.window.aspectRatio, nearPlane, farPlane)
    
    private val viewMatrix = Matrix4f()
    private val invertedRotation = Quaternionf()
    
    fun getViewMatrix() : Matrix4fc {
        return viewMatrix
    }
    
    override fun onInit() {}
    override fun onUpdate() {
        entity?.let { entity ->
            val pos = entity.position
            viewMatrix.identity()
                    .rotate(entity.rotation.invert(invertedRotation))
                    .translate(-pos.x(), -pos.y(), -pos.z())
        }
    }

}