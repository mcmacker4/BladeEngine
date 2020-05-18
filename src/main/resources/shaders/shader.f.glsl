#version 330 core

in vec3 _normal;
in vec2 _uvcoords;

out vec4 color;

uniform sampler2D materialTexture;

const vec3 lightDir = normalize(vec3(0, 1, -1));

void main() {
    float bright = max(0.4, max(0, dot(_normal, lightDir)));
    vec4 textureColor = texture(materialTexture, _uvcoords);
    
    vec3 finalColor = textureColor.xyz * bright;
    
    color = vec4(finalColor, textureColor.a);
}
