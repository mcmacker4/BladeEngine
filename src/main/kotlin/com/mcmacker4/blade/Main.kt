package com.mcmacker4.blade

import com.mcmacker4.blade.input.KeyboardListener
import com.mcmacker4.blade.input.MouseListener
import com.mcmacker4.blade.model.ModelLoader
import com.mcmacker4.blade.scene.Entity
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.BehaviourComponent
import com.mcmacker4.blade.scene.components.CameraComponent
import com.mcmacker4.blade.scene.components.PointLightComponent
import org.joml.Quaternionf
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class LightBehaviour : BehaviourComponent() {
    
    private var originalX = 0f
    private var seed = Random.nextFloat() * PI.toFloat()
    
    override fun onUpdate() {
        entity?.apply {
            position.x = sin(Timer.now.toFloat() + seed) * 2 + originalX
        }
    }

    override fun onAttach() {
        entity?.apply {
            originalX = position.x
        }
    }
    
}


class CameraBehaviour : BehaviourComponent(), MouseListener {
    
    private val speed = 1f
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


class SystemBehaviour : BehaviourComponent(), KeyboardListener {
    
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
        
        if (key == GLFW_KEY_G) {
            if (BladeEngine.mouse.isGrabbed) {
                BladeEngine.mouse.release()
            } else {
                BladeEngine.mouse.grab()
            }
        }
        
        if (key == GLFW_KEY_O) {
            BladeEngine.useAO = !BladeEngine.useAO
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

    val scene = Scene()
    
    val sponza = ModelLoader.loadFromFile("models", "sponza.glb")
    
    println("Sponza is a total of ${countEntities(sponza)} entities.")
    
    val camera = Entity()
    camera.addComponent(CameraComponent(Math.toRadians(80.0)))
    camera.addComponent(CameraBehaviour())

    repeat(20) {
        val light = Entity()
        light.position.x = Random.nextFloat() * 10 - 5
        light.position.y = Random.nextFloat() * 6 + 1
        light.addComponent(PointLightComponent(Vector3f(
                Random.nextFloat() + 1.5f,
                Random.nextFloat() + 1.5f,
                Random.nextFloat() + 1.5f
        )))
        light.addComponent(LightBehaviour())
        scene.addEntity(light)
    }
    
    scene.setActiveCamera(camera)
    scene.addEntity(camera)
    scene.addEntity(sponza)
    
    scene.addEntity(Entity(components = arrayListOf(SystemBehaviour())))

    BladeEngine.start(scene)
    
    BladeEngine.close()
    
}