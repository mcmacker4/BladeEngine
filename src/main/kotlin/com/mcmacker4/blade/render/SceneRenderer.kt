package com.mcmacker4.blade.render

import com.mcmacker4.blade.render.gl.ShaderProgram
import com.mcmacker4.blade.scene.Entity
import com.mcmacker4.blade.scene.Scene
import com.mcmacker4.blade.scene.components.MeshComponent
import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryUtil
import java.io.Closeable


class SceneRenderer : Closeable {
    
    private val identityMatrix: Matrix4fc = Matrix4f().identity()
    
    private val matrixBuffer = MemoryUtil.memAllocFloat(16)
    
    private val entityListNonAlpha = arrayListOf<Entity>()
    private val entityListWithAlpha = arrayListOf<Entity>()
    
    fun prepare(scene: Scene) {
        scene.getEntities().forEach { prepareEntityRecursive(it, identityMatrix) }
    }
    
    private fun prepareEntityRecursive(entity: Entity, parentTransform: Matrix4fc) {
        val transform = parentTransform.mul(entity.getModelMatrix(), entity.worldTransformMatrix)
        
        if (entity.hasComponent(MeshComponent::class)) {
            val meshComponent = entity.getComponent(MeshComponent::class)!!
            if (meshComponent.material.diffuse.useAlpha)
                entityListWithAlpha.add(entity)
            else
                entityListNonAlpha.add(entity)
        }
        
        entity.getChildren().forEach { prepareEntityRecursive(it, transform) }
    }
    
    private fun drawEntity(entity: Entity,
                           modelMatrixLocation: Int) {
        
        entity.worldTransformMatrix.get(matrixBuffer)
        glUniformMatrix4fv(modelMatrixLocation, false, matrixBuffer)

        val meshComponent = entity.getComponent(MeshComponent::class)!!
        
        glActiveTexture(GL_TEXTURE0)
        meshComponent.material.diffuse.bind()
        
        glActiveTexture(GL_TEXTURE1)
        meshComponent.material.normal.bind()
        
        glActiveTexture(GL_TEXTURE2)
        meshComponent.material.metallicRoughness.bind()
        
        meshComponent.mesh.render()
        
    }
    
    fun render(shader: ShaderProgram) {
        
        glEnable(GL_DEPTH_TEST)
        
        val modelMatrixLocation = shader.getUniformLocation("modelMatrix")
        
        entityListNonAlpha.forEach {
            drawEntity(it, modelMatrixLocation)
        }

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        entityListWithAlpha.forEach {
            drawEntity(it, modelMatrixLocation)
        }
        glDisable(GL_BLEND)

        glUseProgram(0)

    }
    
    fun finish() {
        entityListNonAlpha.clear()
        entityListWithAlpha.clear()
    }

    override fun close() {
        MemoryUtil.memFree(matrixBuffer)
    }
    
}