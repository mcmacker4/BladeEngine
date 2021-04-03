package com.mcmacker4.blade.scene

import com.mcmacker4.blade.render.gl.Texture2D
import com.mcmacker4.blade.scene.components.CameraComponent
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE


class Scene {
    
    private val entities = HashSet<Entity>()
    
    private var activeCamera: Entity? = null
    
    var environment: Texture2D? = null

    fun getEntities() : Set<Entity> = entities
    
    fun addEntity(entity: Entity) {
        entities.add(entity)
    }
    
    fun removeEntity(entity: Entity) {
        entities.remove(entity)
    }

    fun getActiveCamera() = activeCamera
    
    fun setActiveCamera(camera: Entity) {
        if (!camera.hasComponent(CameraComponent::class))
            throw Exception("Active camera is missing the CameraComponent")
        if (!entities.contains(camera))
            entities.add(camera)
        this.activeCamera = camera
    }
    
    fun update() {
        entities.forEach { it.onUpdate() }
    }
    
    internal fun propagateMouseEvent(xpos: Double, ypos: Double, dx: Double, dy: Double) {
        entities.forEach { it.onMouseMoved(xpos, ypos, dx, dy) }
    }
    
    internal fun propagateKeyEvent(key: Int, action: Int) {
        if (action == GLFW_PRESS)
            entities.forEach { it.onKeyDown(key) }
        else if (action == GLFW_RELEASE)
            entities.forEach { it.onKeyUp(key) }
    }
    
}