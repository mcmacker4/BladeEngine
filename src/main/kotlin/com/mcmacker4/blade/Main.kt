package com.mcmacker4.blade

import com.mcmacker4.blade.input.KeyboardListener
import com.mcmacker4.blade.input.MouseListener
import com.mcmacker4.blade.render.gl.Texture2D
import com.mcmacker4.blade.resource.Material
import com.mcmacker4.blade.resource.Mesh
import com.mcmacker4.blade.scene.Entity
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.BehaviourComponent
import com.mcmacker4.blade.scene.components.CameraComponent
import com.mcmacker4.blade.scene.components.MeshComponent
import org.joml.Quaternionf
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

class TriangleBehaviour : BehaviourComponent() {
    
    override fun onUpdate() {
        val deltaRot = Math.PI * Timer.delta
        entity?.apply {
            rotation.mul(Quaternionf().rotateY(deltaRot.toFloat()))
        }
    }
    
}


class CameraBehaviour : BehaviourComponent(), MouseListener {
    
    private val speed = 1f
    private val sensitivity = 0.0005f
    
    private var pitch = 0.0
    private var yaw = 0.0
    
    override fun onUpdate() {
        entity?.apply {
            
            rotation.set(Quaternionf().rotateY(yaw.toFloat())
                    .rotateX(pitch.toFloat()))
            
            val delta = Vector3f()
            if (BladeEngine.keyboard.isKeyDown(GLFW_KEY_W))
                delta.z -= 1f
            if (BladeEngine.keyboard.isKeyDown(GLFW_KEY_S))
                delta.z += 1f
            if (BladeEngine.keyboard.isKeyDown(GLFW_KEY_A))
                delta.x -= 1f
            if (BladeEngine.keyboard.isKeyDown(GLFW_KEY_D))
                delta.x += 1f
            
            delta.normalize().mul(speed * Timer.delta.toFloat())
            if (delta.length() > 0)
                position.add(delta.rotate(rotation))
            
        }
    }
    
    override fun onMouseMoved(x: Double, y: Double, dx: Double, dy: Double) {
        if (BladeEngine.mouse.isGrabbed) {
            entity?.apply {
                pitch -= dy * sensitivity
                yaw -= dx * sensitivity
            }
        }
    }

}


class CloseGameBehaviour : BehaviourComponent(), KeyboardListener {
    
    override fun onKeyDown(key: Int) {}

    override fun onKeyUp(key: Int) {
        if (key == GLFW_KEY_ESCAPE)
            BladeEngine.stop()
    }

}

fun main() {

    BladeEngine.initialize()
    
    BladeEngine.mouse.grab()

    val scene = Scene()
    val triangle = Entity(Vector3f(0f, 0f, -1.5f))
    val triangle2 = Entity(Vector3f(0.5f, 0f, 0f), scale = Vector3f(0.3f))

    val camera = Entity()
    camera.addComponent(CameraComponent(Math.toRadians(90.0)))
    camera.addComponent(CameraBehaviour())

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
    //triangle.addComponent(TriangleBehaviour())
    triangle.addChild(triangle2)
    
    triangle2.addComponent(MeshComponent(mesh, material))
    triangle2.addComponent(TriangleBehaviour())

    scene.addEntity(camera)
    scene.addEntity(triangle)
    scene.addEntity(Entity(components = arrayListOf(CloseGameBehaviour())))

    BladeEngine.start(scene)
    
    BladeEngine.destroy()
    
}