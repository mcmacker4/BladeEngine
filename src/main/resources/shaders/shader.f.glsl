#version 330 core

in vec2 _uvcoords;

out vec4 color;

uniform sampler2D materialTexture;

void main() {
    color = texture(materialTexture, _uvcoords);
}
