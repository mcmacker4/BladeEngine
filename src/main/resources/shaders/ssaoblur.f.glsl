#version 460 core

in vec2 _uvcoords;

layout (location = 0) out float FragColor;

layout (binding = 0) uniform sampler2D ssao;

void main() {
    
    vec2 texelSize = 1.0 / vec2(textureSize(ssao, 0));
    float result = 0.0;
    for (int x = -2; x <= 2; x++) {
        for (int y = -2; y <= 2; y++) {
            vec2 offset = vec2(float(x), float(y)) * texelSize;
            result += texture(ssao, _uvcoords + offset).r;
        }
    }
    FragColor = result / 25f;

}
