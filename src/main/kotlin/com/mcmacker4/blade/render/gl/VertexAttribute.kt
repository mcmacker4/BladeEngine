package com.mcmacker4.blade.render.gl


class VertexAttribute(
        val index: Int,
        val vbo: VertexBufferObject,
        val type: Int,
        val size: Int,
        val stride: Int = 0,
        val normalized: Boolean = false
)