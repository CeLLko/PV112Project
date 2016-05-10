//////////////////////////////////
// FRAGMENT SHADER ///////////////
#version 330

uniform vec3 u_color1;
uniform vec3 u_skyColor;

in float visibility;
layout(location = 0) out vec4 o_FragColor;

void main(void){
   o_FragColor = vec4(u_color1, 1.0);
   o_FragColor = mix(vec4(u_skyColor, 1.0), o_FragColor, visibility);
}