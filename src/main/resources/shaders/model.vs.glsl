#version 330

in vec3 position;
in vec3 normal;

out vec3 vNormal;
out vec3 vPosition;

uniform mat4 MVP;
uniform mat3 N;
uniform mat4 model;

void main() {
    vPosition = vec3(model * vec4(position, 1.0));
    vNormal = normalize(N * normal);
    gl_Position = MVP * vec4(position, 1.0);
}