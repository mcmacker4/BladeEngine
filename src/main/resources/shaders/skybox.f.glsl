#version 460 core

in vec2 _uvcoords;

uniform sampler2D environment;

uniform mat4 inverseView;
uniform mat4 inverseProjection;

void main() {
    
    // _uvcoords to view space
    vec4 direction = normalize(inverseProjection * vec4(_uvcoords, 0.0, 1.0));
    direction = inverseView * direction;
    
}
