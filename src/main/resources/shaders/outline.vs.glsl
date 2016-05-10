//////////////////////////////////
// VERTEX SHADER /////////////////
#version 330

// vertex attributes
layout(location = 0) in vec3 i_position;
layout(location = 1) in vec3 i_normal;

uniform mat4 u_view_mat; // view matrix
uniform mat4 u_proj_mat; // projection matrix
uniform mat4 u_model_mat; // model matrix

uniform float u_offset1; // offset along normal
out float visibility;

const float density = 0.002;
const float gradient = 2;

void main(void){
    vec4 tPos   = vec4(i_position + i_normal * u_offset1, 1.0);
    vec4 worldPos = u_model_mat * tPos;
    vec4 positionRelativeToCam = u_view_mat * worldPos;

    float distance = length(positionRelativeToCam.xyz);

    visibility = exp(-pow((distance*density), gradient));
    visibility = clamp (visibility, 0.0, 1.0);
    gl_Position = u_proj_mat * u_view_mat * u_model_mat * tPos;
}