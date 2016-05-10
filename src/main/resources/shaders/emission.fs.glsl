//////////////////////////////////
// FRAGMENT SHADER ///////////////
#version 330
#extension GL_ARB_separate_shader_objects : enable

uniform sampler2D u_baseTexture; // diffuse texture
uniform vec3 u_skyColor;

// inputs from vertex shader
in vec2 v_texcoord1;
in float visibility;

layout(location = 0) out vec4 o_FragColor;

void main(void){
    vec4 baseTex = texture (u_baseTexture, v_texcoord1);
    o_FragColor.xyz = baseTex.rgb ;
    o_FragColor.w = 1.0;
    o_FragColor = mix(vec4(u_skyColor, 1.0), o_FragColor, visibility);
}
