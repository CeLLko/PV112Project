//////////////////////////////////
// VERTEX SHADER /////////////////
#version 330
#extension GL_ARB_separate_shader_objects : enable

// vertex attributes
layout(location = 0) in vec3 i_position;
layout(location = 1) in vec3 i_normal;
layout(location = 2) in vec2 i_texcoord1;
layout(location = 3) in vec3 i_tangent;
layout(location = 4) in vec3 i_bitangent;

uniform mat4 u_view_mat; // view matrix
uniform mat4 u_proj_mat; // projection matrix
uniform mat4 u_model_mat; // model matrix
uniform mat3 u_normal_mat; // normal matrix

uniform vec3 u_light_position;
uniform vec3 u_camera_position;

// inputs for fragment shader
out vec3 v_position;
out vec2 v_texcoord1;
out vec3 v_directionToLight;
out vec3 v_directionToCamera;
out vec3 v_tangentLightPos;
out vec3 v_tangentViewPos;
out vec3 v_tangentFragPos;
out float visibility;

const float density = 0.002;
const float gradient = 2;

void main(void){

    mat4 v_viewProj_mat = u_proj_mat * u_view_mat;

    vec4 worldPos = u_model_mat * vec4(i_position, 1.0);
    vec4 positionRelativeToCam = u_view_mat * worldPos;

    v_position = vec3(worldPos);
    v_texcoord1 = i_texcoord1;

    vec3 vectorToLight = u_light_position - worldPos.xyz;
    v_directionToLight = normalize( vectorToLight);
    v_directionToCamera = normalize( u_camera_position - worldPos.xyz );

    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance*density), gradient));
    visibility = clamp (visibility, 0.0, 1.0);

    mat3 normalMatrix = transpose(inverse(mat3(u_model_mat)));
    vec3 T = normalize(normalMatrix * i_tangent);
    vec3 B = normalize(normalMatrix * i_bitangent);
    vec3 N = normalize(normalMatrix * i_normal);

    mat3 TBN = transpose(mat3(T, B, N));
    v_tangentLightPos = TBN * u_light_position;
    v_tangentViewPos  = TBN * u_camera_position;
    v_tangentFragPos  = TBN * vec3(worldPos);

    gl_Position = v_viewProj_mat * worldPos;
}