package com.mcmacker4.blade.render

import com.mcmacker4.blade.render.framebuffer.GBuffer
import com.mcmacker4.blade.render.framebuffer.LightingBuffer
import com.mcmacker4.blade.render.gl.ShaderProgram
import com.mcmacker4.blade.render.gl.VertexArrayObject
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.PointLightComponent
import org.lwjgl.opengl.GL20.glUniform1i
import org.lwjgl.opengl.GL30.*
import java.io.Closeable


class LightingPass : Closeable {

    private val shader = ShaderProgram.loadNamed("/shaders/lighting")
    
    fun render(
            target: LightingBuffer,
            quadVAO: VertexArrayObject,
            gBuffer: GBuffer,
            scene: Scene) {

        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        
        glDisable(GL_DEPTH_TEST)
        
        shader.use()
        
        val lights = scene.getEntities().filter { it.hasComponent(PointLightComponent::class) }

        glUniform1i(shader.getUniformLocation("numLights"), lights.size)

        lights.forEachIndexed { index, light ->
            val pointLight = light.getComponent(PointLightComponent::class)!!
            val lightPosLocation = shader.getUniformLocation("lights[$index].position")
            glUniform3f(lightPosLocation, light.position.x, light.position.y, light.position.z)
            val lightColorLocation = shader.getUniformLocation("lights[$index].color")
            glUniform3f(lightColorLocation, pointLight.color.x, pointLight.color.y, pointLight.color.z)
        }
        
        glActiveTexture(GL_TEXTURE0)
        gBuffer.positionTexture.bind()
        glActiveTexture(GL_TEXTURE1)
        gBuffer.normalTexture.bind()
        glActiveTexture(GL_TEXTURE2)
        gBuffer.diffuseTexture.bind()
        
        quadVAO.bind()
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)

    }

    override fun close() {
        shader.close()
    }
    
}