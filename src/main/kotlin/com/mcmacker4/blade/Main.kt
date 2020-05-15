package com.mcmacker4.blade

import com.mcmacker4.blade.render.Mesh
import com.mcmacker4.blade.scene.Entity
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.BehaviourComponent
import com.mcmacker4.blade.scene.components.CameraComponent
import com.mcmacker4.blade.scene.components.MeshComponent
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.sin

class TriangleBehaviour : BehaviourComponent() {
    
    override fun onUpdate() {
        val deltaRot = Math.PI * Timer.delta
        entity?.apply {
            rotation.mul(Quaternionf().rotateY(deltaRot.toFloat()))
            position.set(0f, 0f, sin(Timer.now).toFloat() - 2f)
        }
    }
    
}

fun main() {
    
    BladeEngine.initialize()
    
    val scene = Scene()
    val triangle = Entity(Vector3f(0f, 0f, -1f))
    
    scene.setActiveCamera(Entity(components = arrayListOf(
            CameraComponent(Math.toRadians(90.0))
    )))
    
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
    
    triangle.addComponent(MeshComponent(mesh))
    triangle.addComponent(TriangleBehaviour())
    
    scene.addEntity(triangle)
    
    BladeEngine.start(scene)
    
}