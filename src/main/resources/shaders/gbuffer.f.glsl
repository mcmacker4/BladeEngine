#version 330 core

in vec3 _position;
in vec3 _normal;
in vec2 _uvcoords;

layout (location = 0) out vec4 g_position;
layout (location = 1) out vec4 g_normal;
layout (location = 2) out vec4 g_diffuse;

uniform sampler2D materialTexture;

void main() {
    
    g_position = vec4(_position, 1.0);
    g_normal = vec4(_normal, 1.0);
    
    g_diffuse = texture(materialTexture, _uvcoords);
    
}
