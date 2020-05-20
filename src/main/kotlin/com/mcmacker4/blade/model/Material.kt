package com.mcmacker4.blade.model

import com.mcmacker4.blade.render.gl.Texture2D


class Material(
        val diffuse: Texture2D,
        val normal: Texture2D,
        val metallicRoughness: Texture2D
)