#version 330 core

in vec2 position;
in vec2 texcoord;
out vec2 Texcoord;
uniform mat4 pixel;
uniform mat4 trans;

void main() {
    Texcoord = texcoord;
    gl_Position = pixel * trans * vec4(position, 0.0, 1.0);
}