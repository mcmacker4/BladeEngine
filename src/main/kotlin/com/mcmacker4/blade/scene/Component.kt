package com.mcmacker4.blade.scene


abstract class Component {
    
    private var entity: Entity? = null
    
    val hasParent: Boolean
        get() = entity != null
    
    fun setParent(parent: Entity?) {
        this.entity = parent
    }
    
    open fun onInit() {}
    open fun onUpdate() {}
    open fun onDestroy() {}
    
}