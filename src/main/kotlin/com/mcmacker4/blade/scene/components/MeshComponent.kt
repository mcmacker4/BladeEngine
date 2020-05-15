package com.mcmacker4.blade.scene.components

import com.mcmacker4.blade.render.Mesh
import com.mcmacker4.blade.scene.Component


class MeshComponent(val mesh : Mesh) : Component() {

    override fun onDestroy() {
        mesh.delete()
    }
    
}