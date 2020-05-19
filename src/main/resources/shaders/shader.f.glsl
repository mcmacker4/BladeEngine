#version 430 core

in vec3 _normal;
in vec2 _uvcoords;

out vec4 color;

layout (binding = 0) uniform sampler2D diffuseTexture;

const vec3 lightDir = normalize(vec3(0, 1, -1));

void main() {
    float bright = max(0.4, max(0, dot(_normal, lightDir)));
    vec4 textureColor = texture(diffuseTexture, _uvcoords);
    
    vec3 finalColor = textureColor.xyz * bright;
    
    color = vec4(finalColor, textureColor.a);
}
