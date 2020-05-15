package com.mcmacker4.blade.scene

import org.joml.*
import kotlin.reflect.KClass
import kotlin.reflect.full.cast


class Entity(
        val position: Vector3f = Vector3f(0.0f),
        val rotation: Quaternionf = Quaternionf(),
        val scale: Vector3f = Vector3f(1.0f),
        private val components: ArrayList<Component> = arrayListOf()
) {
    
    private val modelMatrix = Matrix4f().identity()
    
    private var scene: Scene? = null
    private var parent: Entity? = null
    
    private val cache = hashMapOf<Any, Component>()
    
    private val children = arrayListOf<Entity>()
    
    fun getModelMatrix(): Matrix4fc = modelMatrix
    
    fun onUpdate() {
        components.forEach { it.onUpdate() }
        children.forEach { it.onUpdate() }
        
        modelMatrix.identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale)
    }
    
    fun onDestroy() {
        children.forEach { it.onDestroy() }
        components.forEach { it.onDestroy() }
    }
    
    // Hierarchy
    
    fun setScene(scene: Scene) {
        this.scene = scene
    }
    
    fun getChildren() : List<Entity> = children
    
    fun addChild(entity: Entity) {
        entity.parent = this
        children.add(entity)
    }
    
    fun removeChild(entity: Entity) {
        if (entity.parent == this) {
            entity.parent = null
            children.remove(entity)
        }
    }
    
    // Entity Component System

    fun <T : Component> hasComponent(kClass: KClass<T>) : Boolean {
        if (cache.containsKey(kClass))
            return true
        for (component in components) {
            if (kClass.isInstance(component))
                return true
        }
        return false
    }

    fun <T : Component> getComponent(kClass: KClass<T>) : T? {
        val cached = cache[kClass]
        if (cached != null)
            return kClass.cast(cached)
        for (component in components) {
            if (kClass.isInstance(component)) {
                cache[kClass] = component
                return kClass.cast(component)
            }
        }
        return null
    }

    fun <T : Component> addComponent(component: T) {
        if (component.hasParent)
            throw Exception("Component already has a parent.")
        if (hasComponent(component::class))
            throw Exception("Entity already has a component of type ${component::class.simpleName}")
        component.setParent(this)
        components.add(component)
    }

    fun <T : Component> removeComponent(kClass: KClass<T>) {
        val component = getComponent(kClass)
        if (component != null) {
            component.setParent(null)
            components.remove(component)
        }
        cache.remove(kClass)
    }
    
}