#version 460 core

#define KERNEL_SIZE 24

in vec2 _uvcoords;

layout (location = 0) out float FragColor;

layout (binding = 0) uniform sampler2D gPosition;
layout (binding = 1) uniform sampler2D gNormal;
layout (binding = 2) uniform sampler2D noiseTexture;
//layout (binding = 2) uniform sampler2D gDepth;


uniform vec3 samples[KERNEL_SIZE];

//uniform vec3 cameraPosition;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform float radius;
uniform vec2 noiseScale;

const float bias = 0.025;

void main() {
    
    vec3 position = texture(gPosition, _uvcoords).xyz;
    position = (viewMatrix * vec4(position, 1.0)).xyz;
    
    vec3 normal = texture(gNormal, _uvcoords).xyz;
    
    // Discard pixels with no model
    if (normal == vec3(0.0)) {
        FragColor = 1.0;
        return;
    }
    
    normal = (viewMatrix * vec4(normal, 0.0)).xyz;
    
    vec3 randomVec = texture(noiseTexture, _uvcoords * noiseScale).xyz;
    
    vec3 tangent = normalize(randomVec - normal * dot(randomVec, normal));
    vec3 bitangent = cross(normal, tangent);
    
    mat3 TBN = mat3(tangent, bitangent, normal);

    float occlusion = 0.0;
    for (int i = 0; i < KERNEL_SIZE; i++) {
        vec3 samplePosition = position + (TBN * samples[i]) * radius;
        
        vec4 offset = projectionMatrix * vec4(samplePosition, 1.0);
        offset.xyz /= offset.w;
        offset.xyz = offset.xyz * 0.5 + 0.5;
        
        float sampleDepth = (viewMatrix * vec4(texture(gPosition, offset.xy).xyz, 1.0)).z;
        
        float rangeCheck = smoothstep(0.0, 1.0, radius / abs(position.z - sampleDepth));
        occlusion += (sampleDepth >= samplePosition.z + bias ? 1.0 : 0.0) * rangeCheck;
    }
    
    FragColor = 1.0 - (occlusion / KERNEL_SIZE);

}
