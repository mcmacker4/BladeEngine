#version 430 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec3 tangent;
layout (location = 3) in vec2 uvcoords;

out vec3 _position;
out vec2 _uvcoords;

out mat3 _TBN;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main() {
    
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);

    vec3 normalWorld = normalize((modelMatrix * vec4(normal, 0.0)).xyz);
    vec3 tangentWorld = normalize((modelMatrix * vec4(tangent, 0.0)).xyz);
    
    tangentWorld = normalize(tangentWorld - dot(tangentWorld, normalWorld) * normalWorld);
    
    vec3 bitangentWorld = cross(normalWorld, tangentWorld);
    _TBN = mat3(tangentWorld, bitangentWorld, normalWorld);
    
    _position = (modelMatrix * vec4(position, 1.0)).xyz;
    _uvcoords = uvcoords;
    
}
