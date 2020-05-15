package com.mcmacker4.blade.scene


class Scene {
    
    val entities = ArrayList<Entity>()
    
    fun addEntity(entity: Entity) {
        entities.add(entity)
    }
    
    fun removeEntity(entity: Entity) {
        entities.remove(entity)
        entity.onDestroy()
    }
    
    open fun render() {
        
    }
    
    fun destroy() {
        for (entity in entities) {
            entity.onDestroy()
        }
    }
    
}