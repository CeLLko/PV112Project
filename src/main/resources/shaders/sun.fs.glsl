#version 330

out vec4 fragColor;

in vec3 vNormal;
in vec3 vPosition;
in vec2 vTex_coord;

uniform vec3 ambientColor;

uniform struct LightSource {
    vec4 position;
    vec3 diffuseColor;
    vec3 specularColor;
//    float coneAngle;
//    vec3 coneDirection;
} allLights[5];

uniform struct Object {
    sampler2D texture;
    vec3 specularColor;
    float shininess;
} object;

uniform vec3 eyePosition;

vec3 applyLight(LightSource lightSource, vec3 v) {

    vec3 light;
    if(lightSource.position.w == 0.0) {
        light = normalize(lightSource.position.xyz);
    } else {
        light = normalize(lightSource.position.xyz - vPosition);
    }

    vec3 texture_color = texture(object.texture, vTex_coord).rgb;

    vec3 h = normalize(light + v);

    float d = max(dot(vNormal, light), 0.0);
    float s = pow(max(dot(vNormal, h), 0.0), object.shininess);

    vec3 lightFinal = object.specularColor * lightSource.specularColor * s +
                      texture_color * lightSource.diffuseColor * d;

    return lightFinal;
}

vec4 applyAmbientLight(){
    vec3 texture_color = texture(object.texture, vTex_coord).rgb;
    vec3 lightFinal = texture_color * ambientColor;
    return vec4(lightFinal, 1.0);
}

void main() {

    vec4 tempColor = vec4(0,0,0,0);

    vec3 v = normalize(eyePosition - vPosition);

    for(int i = 0; i < 5; i++){
        tempColor += vec4(applyLight(allLights[i], v), 1.0);
    }
    fragColor = tempColor+applyAmbientLight();
}