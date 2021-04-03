#version 460 core

in vec3 position;

out vec2 _uvcoords;

void main() {
    gl_Position = vec4(position, 1.0);
    _uvcoords = (position.xy + 1) / 2;
    _uvcoords.y = 1.0 - _uvcoords.y; // OpenGL Texture coordinates
}
