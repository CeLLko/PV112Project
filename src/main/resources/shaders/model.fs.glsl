#version 150

uniform mat4 model;
uniform vec3 cameraPosition;

uniform sampler2D materialTex;
uniform float materialShininess;
uniform vec3 materialSpecularColor;

#define MAX_LIGHTS 10
uniform int numLights;
uniform struct Light {
   vec4 position;
   vec3 intensities; //a.k.a the color of the light
   float attenuation;
   float ambientCoefficient;
   float coneAngle;
   vec3 coneDirection;
} allLights[MAX_LIGHTS];

in vec2 vTex;
in vec3 vNormal;
in vec3 vPosition;

out vec4 fragColor;

vec3 ApplyLight(Light light, vec3 surfaceColor, vec3 normal, vec3 surfacePos, vec3 surfaceToCamera) {
    vec3 surfaceToLight;
    float attenuation = 1.0;
    if(light.position.w == 0.0) {
        //directional light
        surfaceToLight = normalize(light.position.xyz);
        attenuation = 1.0; //no attenuation for directional lights
    } else {
        //point light
        surfaceToLight = normalize(light.position.xyz - surfacePos);
        float distanceToLight = length(light.position.xyz - surfacePos);
        attenuation = 1.0 / (1.0 + light.attenuation * pow(distanceToLight, 2));

        //cone restrictions (affects attenuation)
        float lightToSurfaceAngle = degrees(acos(dot(-surfaceToLight, normalize(light.coneDirection))));
        if(lightToSurfaceAngle > light.coneAngle){
            attenuation = 0.0;
        }
    }

    //ambient
    vec3 ambient = light.ambientCoefficient * surfaceColor.rgb * light.intensities;

    //diffuse
    float diffuseCoefficient = max(0.0, dot(normal, surfaceToLight));
    vec3 diffuse = diffuseCoefficient * surfaceColor.rgb * light.intensities;

    //specular
    float specularCoefficient = 0.0;
    if(diffuseCoefficient > 0.0)
        specularCoefficient = pow(max(0.0, dot(surfaceToCamera, reflect(-surfaceToLight, normal))), materialShininess);
    vec3 specular = specularCoefficient * materialSpecularColor * light.intensities;

    //linear color (color before gamma correction)
    return ambient + attenuation*(diffuse + specular);
}

void main() {
    vec3 normal = normalize(transpose(inverse(mat3(model))) * vNormal);
    vec3 surfacePos = vec3(model * vec4(vPosition, 1));
    vec4 surfaceColor = texture(materialTex, vTex);
    vec3 surfaceToCamera = normalize(cameraPosition - surfacePos);

    //combine color from all the lights
    vec3 linearColor = vec3(0);
    for(int i = 0; i < 1; i++){
        linearColor += ApplyLight(allLights[i], surfaceColor.rgb, normal, surfacePos, surfaceToCamera);
    }

    //final color (after gamma correction)
    vec3 gamma = vec3(1.0/2.2);
    fragColor = vec4(pow(linearColor, gamma), surfaceColor.a);
}

/*
#version 330

out vec4 fragColor;

in vec3 vNormal;
in vec3 vPosition;

struct LightSource
{
  int isLit;
  vec4 position;
  vec3 ambientColor;
  vec3 diffuseColor;
  vec3 specularColor;
};

uniform LightSource lights[10];

uniform vec3 materialAmbientColor;
uniform vec3 materialDiffuseColor;
uniform vec3 materialSpecularColor;
uniform float materialShininess;

uniform vec3 eyePosition;

uniform vec3 color;

void main() {

    vec3 v = normalize(eyePosition - vPosition);

    for(int i = 0; i < 10; ++i) {
    LightSource current = lights[i];
        if(current.isLit > 0.5) {
            vec4 lightPosition = current.position;
            vec3 lightAmbientColor = current.ambientColor;
            vec3 lightDiffuseColor = current.diffuseColor;
            vec3 lightSpecularColor = current.specularColor;

            vec3 light;
            if(lightPosition.w == 0.0) {
                light = normalize(lightPosition.xyz);
            } else {
                light = normalize(lightPosition.xyz - vPosition);
            }

            vec3 h = normalize(light + v);

            float d = max(dot(vNormal, light), 0.0);
            float s = pow(max(dot(vNormal, h), 0.0), 100);

                vec3 lightFinal = materialSpecularColor * lightSpecularColor * s +
                                    materialDiffuseColor * lightDiffuseColor * d +
                                    materialAmbientColor * lightAmbientColor;

            fragColor = vec4(lightFinal, 1.0);
        }
    }
}*/
