#version 330 core

layout (location = 0) in vec3 position;
layout (location = 2) in vec2 uvcoords;

out vec3 _color;

uniform mat4 modelMatrix;
uniform mat4 projectionMatrix;

void main() {
    _color = vec3(uvcoords, 1.0);
    gl_Position = projectionMatrix * modelMatrix * vec4(position, 1.0);
}