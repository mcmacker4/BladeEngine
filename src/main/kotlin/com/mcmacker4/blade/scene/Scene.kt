package com.mcmacker4.blade.scene

import com.mcmacker4.blade.scene.components.CameraComponent
import com.mcmacker4.blade.scene.components.MeshComponent


class Scene {
    
    private val entities = ArrayList<Entity>()
    
    private var activeCamera: Entity? = null
    
    fun addEntity(entity: Entity) {
        entities.add(entity)
    }
    
    fun removeEntity(entity: Entity) {
        entities.remove(entity)
    }
    
    fun setActiveCamera(camera: Entity) {
        if (!camera.hasComponent(CameraComponent::class))
            throw Exception("Active camera is missing the CameraComponent")
        this.activeCamera = camera
    }
    
    fun getActiveCamera() = activeCamera
    
    fun update() {
        entities.forEach { it.onUpdate() }
    }
    
    fun destroy() {
        for (entity in entities) {
            entity.onDestroy()
        }
    }
    
    fun getEntities() : List<Entity> = entities
    
    fun getDrawableEntities() : List<Entity> =
            entities.filter { it.hasComponent(MeshComponent::class) }
    
}