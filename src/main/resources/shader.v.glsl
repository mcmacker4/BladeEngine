#version 330 core

layout (location = 0) in vec3 position;

out vec3 _color;

void main() {
    _color = position + 0.5;
    gl_Position = vec4(position, 1.0);
}