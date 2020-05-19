#version 430 core

in vec2 _uvcoords;

out vec4 color;

layout (binding = 0) uniform sampler2D blitTexture;

void main() {
    color = texture(blitTexture, _uvcoords);
}
