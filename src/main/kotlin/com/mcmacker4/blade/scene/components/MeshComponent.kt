package com.mcmacker4.blade.scene.components

import com.mcmacker4.blade.render.data.Material
import com.mcmacker4.blade.render.data.Mesh
import com.mcmacker4.blade.scene.Component


class MeshComponent(
        val mesh: Mesh,
        val material: Material 
) : Component() {

    override fun onInit() {}
    override fun onUpdate() {}

    override fun onDestroy() {
        mesh.delete()
        material.delete()
    }
    
}