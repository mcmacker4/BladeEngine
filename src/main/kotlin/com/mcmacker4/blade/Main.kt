package com.mcmacker4.blade

import com.mcmacker4.blade.render.data.Material
import com.mcmacker4.blade.render.data.Mesh
import com.mcmacker4.blade.render.gl.Texture2D
import com.mcmacker4.blade.scene.Entity
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.BehaviourComponent
import com.mcmacker4.blade.scene.components.CameraComponent
import com.mcmacker4.blade.scene.components.MeshComponent
import org.joml.Quaternionf
import org.joml.Vector3f
import java.io.File

class TriangleBehaviour : BehaviourComponent() {
    
    override fun onUpdate() {
        val deltaRot = Math.PI * Timer.delta
        entity?.apply {
            rotation.mul(Quaternionf().rotateY(deltaRot.toFloat()))
        }
    }
    
}

fun main() {

    BladeEngine.initialize()

    val scene = Scene()
    val triangle = Entity(Vector3f(0f, 0f, -1.5f))

    val camera = Entity()
    camera.addComponent(CameraComponent(Math.toRadians(90.0)))
    scene.setActiveCamera(camera)

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
                    0f, 1f,
                    1f, 1f,
                    0.5f, 0f
            )
    )

    val material = Material(Texture2D.loadFromFile("/texture.png"))
    
    triangle.addComponent(MeshComponent(mesh, material))
    triangle.addComponent(TriangleBehaviour())

    scene.addEntity(triangle)

    BladeEngine.start(scene)
    
}