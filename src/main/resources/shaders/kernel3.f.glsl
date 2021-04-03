#version 460 core

in vec2 _uvcoords;

layout (location = 0) out vec3 FragColor;

layout (binding = 0) uniform sampler2D inTexture;

uniform float kernel[9];

void main() {

    vec2 texelSize = 1.0 / vec2(textureSize(inTexture, 0));
    vec3 color = vec3(0.0);
    
    for (int j = -1; j <= 1; j++) {
        for (int i = -1; i <= 1; i++) {
            vec2 off = vec2(float(i) * texelSize.x, float(j) * texelSize.y); 
            int idx = (j + 1) * 3 + (i + 1);
            color += texture(inTexture, _uvcoords + off).xyz * kernel[idx];
        }
    }
    
    FragColor = color;
    
}
