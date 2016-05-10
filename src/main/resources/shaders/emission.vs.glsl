//////////////////////////////////
// VERTEX SHADER /////////////////
#version 330
#extension GL_ARB_separate_shader_objects : enable

// vertex attributes
layout(location = 0) in vec3 i_position;
layout(location = 1) in vec3 i_normal;
layout(location = 2) in vec2 i_texcoord1;

uniform mat4 u_view_mat; // view matrix
uniform mat4 u_proj_mat; // projection matrix
uniform mat4 u_model_mat; // model matrix

// inputs for fragment shader
out vec2 v_texcoord1;
out float visibility;

const float density = 0.002;
const float gradient = 2;

void main(void){
    vec4 worldPos = u_model_mat * vec4(i_position, 1.0);
    vec4 positionRelativeToCam = u_view_mat * worldPos;

    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance*density), gradient));
    visibility = clamp (visibility, 0.0, 1.0);

    v_texcoord1 = i_texcoord1;

    gl_Position = u_proj_mat * u_view_mat * worldPos;
}