#version 460 core

in vec2 _uvcoords;

out vec4 FragColor;

layout (binding = 0) uniform sampler2D positionTexture;
layout (binding = 1) uniform sampler2D normalTexture;
layout (binding = 2) uniform sampler2D diffuseTexture;
layout (binding = 3) uniform sampler2D metallicRoughnessTexture;
layout (binding = 4) uniform sampler2D ssao;

uniform vec3 cameraPosition;

uniform bool useAO;

struct Light {
    vec3 position;
    vec3 color;
};

uniform int numLights;
uniform Light lights[20];


const float PI = 3.14159265359;


vec3 fresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
}

float DistributionGGX(vec3 N, vec3 H, float roughness) {
    float a      = roughness*roughness;
    float a2     = a*a;
    float NdotH  = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float num   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return num / denom;
}

float GeometrySchlickGGX(float NdotV, float roughness) {
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float num   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return num / denom;
}

float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2  = GeometrySchlickGGX(NdotV, roughness);
    float ggx1  = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}

void main() {
    
    vec3 position = texture(positionTexture, _uvcoords).xyz;
    vec3 N = texture(normalTexture, _uvcoords).xyz;
    vec4 diffuseAlpha = texture(diffuseTexture, _uvcoords);
    vec3 diffuse = diffuseAlpha.rgb;
    float alpha = diffuseAlpha.a;
    
    float ao = texture(ssao, _uvcoords).r;
    
    vec4 metallicRoughness = texture(metallicRoughnessTexture, _uvcoords);
    float metallic = metallicRoughness.b;
    float roughness = metallicRoughness.g;
    
    vec3 V = normalize(cameraPosition - position);

    vec3 F0 = vec3(0.04);
    F0 = mix(F0, diffuse, metallic);
    
    vec3 Lo = vec3(0.0);
    for (int i = 0; i < numLights; i++) {
        vec3 L = normalize(lights[i].position - position);
        vec3 H = normalize(V + L);
        
        float distance = length(lights[i].position - position);
        float attenuation = 1.0 / (distance * distance);
        
        vec3 radiance = lights[i].color * attenuation;
        
        float NDF = DistributionGGX(N, H, roughness);
        float G = GeometrySmith(N, V, L, roughness);
        vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

        vec3 kS = F;
        vec3 kD = vec3(1.0) - kS;
        kD *= 1.0 - metallic;

        vec3 numerator    = NDF * G * F;
        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0);
        vec3 specular     = numerator / max(denominator, 0.001);

        float NdotL = max(dot(N, L), 0.0);
        Lo += (kD * diffuse / PI + specular) * radiance * NdotL;
    }

    vec3 color = Lo * (useAO ? ao : 1.0);
    
    FragColor = vec4(color, alpha);
}
