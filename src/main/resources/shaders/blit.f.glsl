#version 330 core

in vec2 _uvcoords;

out vec4 color;

uniform sampler2D frame;

void main() {
    color = texture(frame, _uvcoords);
}
