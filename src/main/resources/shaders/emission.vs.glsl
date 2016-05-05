//////////////////////////////////
// VERTEX SHADER /////////////////
#version 330
#extension GL_ARB_separate_shader_objects : enable

// vertex attributes
layout(location = 0) in vec3 i_position;
layout(location = 1) in vec3 i_normal;
layout(location = 2) in vec2 i_texcoord1;

uniform mat4 u_viewProj_mat; // view-projection matrix
uniform mat4 u_model_mat; // model matrix
uniform mat3 u_normal_mat; // normal matrix

// inputs for fragment shader
out vec2 v_texcoord1;

void main(void){
   vec4 worldPos = u_model_mat * vec4(i_position, 1.0);
   v_texcoord1 = i_texcoord1;

   gl_Position = u_viewProj_mat * worldPos;
}