package com.mcmacker4.blade.scene

import org.joml.Vector3f
import kotlin.reflect.KClass
import kotlin.reflect.full.cast


class Entity(
        val position: Vector3f = Vector3f(0.0f),
        val rotation: Vector3f = Vector3f(0.0f),
        val scale: Vector3f = Vector3f(1.0f)
) {

    private val components = arrayListOf<Component>()
    private val cache = hashMapOf<Any, Component>()
    
    fun onDestroy() {
        components.forEach { it.onDestroy() }
    }
    

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