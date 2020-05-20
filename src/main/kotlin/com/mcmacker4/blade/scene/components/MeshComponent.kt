package com.mcmacker4.blade.scene.components

import com.mcmacker4.blade.model.Material
import com.mcmacker4.blade.model.Mesh
import com.mcmacker4.blade.scene.Component


class MeshComponent(
        val mesh: Mesh,
        val material: Material 
) : Component()