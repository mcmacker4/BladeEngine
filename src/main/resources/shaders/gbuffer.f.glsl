#version 430 core

in vec3 _position;
in vec2 _uvcoords;

in mat3 _TBN;

layout (location = 0) out vec3 position;
layout (location = 1) out vec3 normal;
layout (location = 2) out vec4 diffuse;
layout (location = 3) out vec3 metallicRoughness;

layout (binding = 0) uniform sampler2D diffuseMap;
layout (binding = 1) uniform sampler2D normalMap;
layout (binding = 2) uniform sampler2D metallicRoughessMap;

void main() {
    
    position = _position;
    
    vec3 normalSample = texture2D(normalMap, _uvcoords).xyz;
    normalSample = normalize(normalSample * 2.0 - 1.0);
    
    normal = normalize(_TBN * normalSample);
    
    diffuse = texture(diffuseMap, _uvcoords);
    
    metallicRoughness = texture(metallicRoughessMap, _uvcoords).rgb;
    
}
