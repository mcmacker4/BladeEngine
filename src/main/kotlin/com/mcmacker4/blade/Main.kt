package com.mcmacker4.blade

import com.mcmacker4.blade.input.KeyboardListener
import com.mcmacker4.blade.input.MouseListener
import com.mcmacker4.blade.model.Model
import com.mcmacker4.blade.scene.Entity
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.BehaviourComponent
import com.mcmacker4.blade.scene.components.CameraComponent
import org.joml.Quaternionf
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.PI

class TriangleBehaviour : BehaviourComponent() {
    
    override fun onUpdate() {
        val deltaRot = Math.PI * Timer.delta
        entity?.apply {
            rotation.mul(Quaternionf().rotateY(deltaRot.toFloat()))
        }
    }
    
}


class CameraBehaviour : BehaviourComponent(), MouseListener {
    
    private val speed = 5f
    private val sensitivity = 0.001f
    
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
            
            val mult = if (BladeEngine.keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT)) speed * 5f else speed
            
            delta.normalize().mul(mult * Timer.delta.toFloat())
            if (delta.length() > 0)
                position.add(delta.rotate(rotation))
            
        }
    }
    
    override fun onMouseMoved(x: Double, y: Double, dx: Double, dy: Double) {
        if (BladeEngine.mouse.isGrabbed) {
            entity?.apply {
                pitch -= (dy * sensitivity)
                pitch = pitch.coerceIn(-PI / 2, PI / 2)
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
        
        if (key == GLFW_KEY_F11) {
            if (BladeEngine.window.isFullscreen) {
                BladeEngine.window.goWindowed()
            } else {
                BladeEngine.window.goFullscreen()
            }
        }
    }

}

fun countEntities(entity: Entity): Int {
    val children = entity.getChildren()
    if (children.isEmpty()) return 1
    
    return 1 + children.map { countEntities(it) }.sum()
}

fun main() {

    BladeEngine.initialize()
    
    BladeEngine.mouse.grab()

    val scene = Scene()
//    val triangle = Entity(Vector3f(0f, 0f, -1.5f))
//    val triangle2 = Entity(Vector3f(0.5f, 0f, 0f), scale = Vector3f(0.3f))
    
    val sponza = Model.loadFromFile("models", "sponza.glb")
    
    println("Sponza is a total of ${countEntities(sponza)} entities.")
    
    val camera = Entity()
    camera.addComponent(CameraComponent(Math.toRadians(80.0)))
    camera.addComponent(CameraBehaviour())

    scene.setActiveCamera(camera)

//    val mesh = Mesh(
//            floatArrayOf(
//                    -0.5f, -0.5f, 0f,
//                    0.5f, -0.5f, 0f,
//                    0f, 0.5f, 0f
//            ),
//            floatArrayOf(
//                    0f, 0f, 0f,
//                    0f, 0f, 0f,
//                    0f, 0f, 0f
//            ),
//            floatArrayOf(
//                    0f, 1f,
//                    1f, 1f,
//                    0.5f, 0f
//            )
//    )

//    val material = Material(Texture2D.loadFromFile("/textures/texture.png"))
    
//    triangle.addComponent(MeshComponent(mesh, material))
//    //triangle.addComponent(TriangleBehaviour())
//    triangle.addChild(triangle2)
//    
//    triangle2.addComponent(MeshComponent(mesh, material))
//    triangle2.addComponent(TriangleBehaviour())

    scene.addEntity(camera)
//    scene.addEntity(triangle)
    scene.addEntity(sponza)
    
    
    scene.addEntity(Entity(components = arrayListOf(CloseGameBehaviour())))

    BladeEngine.start(scene)
    
    BladeEngine.destroy()
    
}