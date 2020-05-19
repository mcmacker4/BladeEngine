#version 430 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec3 tangent;
layout (location = 3) in vec2 uvcoords;

out vec3 _normal;
out vec2 _uvcoords;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {
    _normal = normalize((modelMatrix * vec4(normal, 0.0)).xyz);
    _uvcoords = uvcoords;
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
}