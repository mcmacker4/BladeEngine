package com.mcmacker4.blade

import com.mcmacker4.blade.render.Mesh
import com.mcmacker4.blade.scene.Entity
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.MeshComponent

fun main() {
    
    BladeEngine.initialize()
    
    val scene = Scene()
    val entity = Entity()
    
    val mesh = Mesh(
            floatArrayOf(
                    -0.5f, -0.5f, 0f,
                    0.5f, -0.5f, 0f,
                    0f, 0.5f, 0f
            ),
            floatArrayOf(
                    0f, 0f, 0f,
                    0f, 0f, 0f,
                    0f, 0f, 0f
            ),
            floatArrayOf(
                    0f, 0f,
                    1f, 0f,
                    .5f, 1f
            )
    )
    
    entity.addComponent(MeshComponent(mesh))
    
    scene.addEntity(entity)
    
    BladeEngine.start(scene)
    
}