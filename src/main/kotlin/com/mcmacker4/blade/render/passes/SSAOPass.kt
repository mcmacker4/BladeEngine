package com.mcmacker4.blade.render.passes

import com.mcmacker4.blade.BladeEngine
import com.mcmacker4.blade.render.framebuffer.GBuffer
import com.mcmacker4.blade.render.framebuffer.SSAOBuffer
import com.mcmacker4.blade.render.gl.ShaderProgram
import com.mcmacker4.blade.render.gl.Texture2D
import com.mcmacker4.blade.render.gl.VertexArrayObject
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.CameraComponent
import org.joml.Vector3f
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import java.io.Closeable
import kotlin.random.Random


class SSAOPass : Closeable {
    
    private val samples = 24
    private val radius = 0.2f

    private val shader = ShaderProgram.loadNamed("/shaders/ssao")
    private val noiseTexture: Texture2D
    
    private val matrixBuffer = MemoryUtil.memAllocFloat(16)

    init {
        
        shader.use()
        
        glUniform1f(shader.getUniformLocation("radius"), radius)
        
        // Load samples. We can do it once because uniforms are bound to the shader
        // even when unbound and rebound
        repeat(samples) { index ->
            val location = shader.getUniformLocation("samples[$index]")
            val sample = Vector3f(
                    Random.nextFloat() * 2 - 1,
                    Random.nextFloat() * 2 - 1,
                    Random.nextFloat()
            ).normalize().mul(Random.nextFloat())
            glUniform3f(location, sample.x, sample.y, sample.z)
        }
        
        // Create Noise texture
        val buffer = MemoryUtil.memAllocFloat(samples * 3)
        repeat(samples) { idx ->
            val vec = Vector3f(Random.nextFloat() * 2 - 1, Random.nextFloat() * 2 - 1, 0f)
            buffer.put(idx * 3 + 0, vec.x)
            buffer.put(idx * 3 + 1, vec.y)
            buffer.put(idx * 3 + 2, vec.z)
        }
        
        noiseTexture = Texture2D(4, 4, buffer, GL_RGB, GL_RGB16F)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        noiseTexture.unbind()
        
    }

    fun render(
            target: SSAOBuffer,
            quadVAO: VertexArrayObject,
            gBuffer: GBuffer,
            scene: Scene) {

        scene.getActiveCamera()?.let { camera ->

            target.bind(GL_FRAMEBUFFER)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            glViewport(0, 0, BladeEngine.window.width / 2, BladeEngine.window.height / 2)

            glDisable(GL_DEPTH_TEST)

            shader.use()
            
            val component = camera.getComponent(CameraComponent::class)!!
            component.projectionMatrix.get(matrixBuffer)
            glUniformMatrix4fv(shader.getUniformLocation("projectionMatrix"), false, matrixBuffer)
            component.getViewMatrix().get(matrixBuffer)
            glUniformMatrix4fv(shader.getUniformLocation("viewMatrix"), false, matrixBuffer)
            
            glUniform2f(shader.getUniformLocation("noiseScale"),
                    BladeEngine.window.width / 8f,
                    BladeEngine.window.height / 8f)

            glActiveTexture(GL_TEXTURE0)
            gBuffer.positionTexture.bind()
            glActiveTexture(GL_TEXTURE1)
            gBuffer.normalTexture.bind()
            glActiveTexture(GL_TEXTURE2)
            noiseTexture.bind()

            quadVAO.bind()
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)

        }

    }

    override fun close() {
        shader.close()
        noiseTexture.close()
        MemoryUtil.memFree(matrixBuffer)
    }

}