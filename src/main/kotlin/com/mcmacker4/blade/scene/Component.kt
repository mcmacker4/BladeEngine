package com.mcmacker4.blade.scene


abstract class Component {
    
    protected var entity: Entity? = null
    
    val hasParent: Boolean
        get() = entity != null
    
    fun setParent(parent: Entity?) {
        this.entity = parent
    }
    
    abstract fun onInit()
    abstract fun onUpdate()
    abstract fun onDestroy()
    
}