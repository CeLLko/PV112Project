#version 330

out vec4 fragColor;

in vec3 vNormal;
in vec3 vPosition;

uniform struct LightSource {
    vec4 position;
    vec3 ambientColor;
    vec3 diffuseColor;
    vec3 specularColor;
} allLights[10];

uniform vec3 materialAmbientColor;
uniform vec3 materialDiffuseColor;
uniform vec3 materialSpecularColor;
uniform float materialShininess;

uniform vec3 eyePosition;

uniform vec3 color;

vec3 applyLight(LightSource lightSource, vec3 v) {

    vec3 light;
        if(lightSource.position.w == 0.0) {
            light = normalize(lightSource.position.xyz);
        } else {
            light = normalize(lightSource.position.xyz - vPosition);
        }

        vec3 h = normalize(light + v);

        float d = max(dot(vNormal, light), 0.0);
        float s = pow(max(dot(vNormal, h), 0.0), 100);

        vec3 lightFinal = materialSpecularColor * lightSource.specularColor * s +
                            materialDiffuseColor * lightSource.diffuseColor * d +
                            materialAmbientColor * lightSource.ambientColor;

    //linear color (color before gamma correction)
    return lightFinal;
}


void main() {

    vec4 tempColor = vec4(0.0, 0.0, 0.0, 0.0);

    vec3 v = normalize(eyePosition - vPosition);

    for(int i = 0; i < 10; i++){
        tempColor += vec4(applyLight(allLights[i], v), 1.0);
    }
    fragColor = tempColor;
}