#version 330

out vec4 fragColor;

in vec3 vNormal;
in vec3 vPosition;

uniform vec4 light1Position;

uniform vec3 light1AmbientColor;
uniform vec3 light1DiffuseColor;
uniform vec3 light1SpecularColor;

uniform vec3 materialAmbientColor;
uniform vec3 materialDiffuseColor;
uniform vec3 materialSpecularColor;
uniform float materialShininess;

uniform vec3 eyePosition;

uniform vec3 color;

void main() {

    vec3 v = normalize(eyePosition - vPosition);

    vec3 light;
    if(light1Position.w == 0.0) {
        light = normalize(light1Position.xyz);
    } else {
        light = normalize(light1Position.xyz - vPosition);
    }

    vec3 h = normalize(light + v);

    float d = max(dot(vNormal, light), 0.0);
    float s = pow(max(dot(vNormal, h), 0.0), 100);

    vec3 lightFinal = materialSpecularColor * light1SpecularColor * s +
                        materialDiffuseColor * light1DiffuseColor * d +
                        materialAmbientColor * light1AmbientColor;

    fragColor = vec4(lightFinal, 1.0);
}