package com.mcmacker4.blade.render.passes

import com.mcmacker4.blade.BladeEngine
import com.mcmacker4.blade.render.framebuffer.SSAOBlurBuffer
import com.mcmacker4.blade.render.framebuffer.SSAOBuffer
import com.mcmacker4.blade.render.gl.ShaderProgram
import com.mcmacker4.blade.render.gl.VertexArrayObject
import org.lwjgl.opengl.GL30.*
import java.io.Closeable


class SSAOBlurPass : Closeable {

    private val shader = ShaderProgram.loadNamed("/shaders/ssaoblur")

    fun render(
            target: SSAOBlurBuffer,
            quadVAO: VertexArrayObject,
            ssaoBuffer: SSAOBuffer) {

        target.bind(GL_FRAMEBUFFER)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glViewport(0, 0, BladeEngine.window.width / 2, BladeEngine.window.height / 2)

        glDisable(GL_DEPTH_TEST)

        shader.use()

        glActiveTexture(GL_TEXTURE0)
        ssaoBuffer.ssao.bind()

        quadVAO.bind()
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)

    }

    override fun close() {
        shader.close()
    }

}