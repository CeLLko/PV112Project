//////////////////////////////////
// FRAGMENT SHADER ///////////////
#version 330
#extension GL_ARB_separate_shader_objects : enable

uniform sampler2D u_baseTexture; // diffuse texture
uniform sampler2D u_overlayTexture; // diffuse texture
uniform sampler2D u_normalTexture; // diffuse texture
uniform float u_numShades; // number of shades
uniform vec3 u_camera_position;
uniform bool overlayingTexture;
uniform bool normalMapping;
uniform vec3 u_skyColor;

uniform struct Light {
   vec4 position;
   vec3 intensities; //a.k.a the color of the light
   float attenuation;
   float ambientCoefficient;
   float coneAngle;
   vec3 coneDirection;
} spotLight;

// inputs from vertex shader
in vec3 v_normal;
in vec3 v_position;
in vec2 v_texcoord1;
in vec3 v_directionToLight;
in vec3 v_directionToCamera;
in vec3 v_tangentLightPos;
in vec3 v_tangentViewPos;
in vec3 v_tangentFragPos;
in float visibility;


layout(location = 0) out vec4 o_FragColor;

vec3 normal = v_normal;
vec3 lightDir = v_directionToLight;
vec3 viewDir = v_directionToCamera;

// calculate diffuse component of lighting
float diffuseSimple(vec3 L, vec3 N){
    return clamp(dot(L,N),0.0,1.0);
}

// calculate specular component of lighting
float specularSimple(vec3 L,vec3 N,vec3 H){
    if(dot(N,L)>0){
        return pow(clamp(dot(H,N),0.0,1.0),64.0);
    }
    return 0.0;
}

vec3 applySpotLight(Light lightSource) {

    vec3 surfaceToLight;
    float attenuation = 1.0;
    if(lightSource.position.w == 0.0) {
        surfaceToLight = normalize(lightSource.position.xyz);
        attenuation = 1.0;
    } else {
        surfaceToLight = normalize(lightSource.position.xyz - v_position);
        float distanceToLight = length(lightSource.position.xyz - v_position);
        attenuation = 1.0 / (1.0 + lightSource.attenuation * pow(distanceToLight, 2));
        float lightToSurfaceAngle = degrees(acos(dot(-surfaceToLight, normalize(lightSource.coneDirection))));
        if(lightToSurfaceAngle > lightSource.coneAngle){
            attenuation = 0.0;
        }
    }

    vec3 texture_color = texture (u_baseTexture, v_texcoord1).rgb;

    vec3 ambient = lightSource.ambientCoefficient * texture_color.rgb * lightSource.intensities;
    //diffuse
    float diffuseCoefficient = max(0.0, dot(normal, surfaceToLight));
    vec3 diffuse = diffuseCoefficient * texture_color.rgb *lightSource.intensities;

    //specular
    float specularCoefficient = 0.0;
    if(diffuseCoefficient > 0.0)
        specularCoefficient = max(0.0, dot(viewDir, reflect(-surfaceToLight, normal)));
    vec3 specular = specularCoefficient * lightSource.intensities;

    //linear color (color before gamma correction)
    return ambient + attenuation*(diffuse + specular);
}

void main(void){

    if(normalMapping)
    {
        normal = texture(u_normalTexture, v_texcoord1).rgb;
        normal = normalize(normal * 2.0 - 1.0);
        lightDir = normalize(v_tangentLightPos - v_tangentFragPos);
        viewDir = normalize(v_tangentViewPos-  v_tangentFragPos);
    }
    // calculate total intensity of lighting
    vec3 halfVector = normalize( lightDir + viewDir );
    float iambi = 0.1;
    float idiff = diffuseSimple(lightDir, normal);
    float ispec = specularSimple(lightDir,normal, halfVector);
    float intensity = iambi + idiff + ispec;

    // quantize intensity for cel shading
    float shadeIntensity = ceil(intensity * u_numShades)/ u_numShades;

    vec3 baseTex = texture (u_baseTexture, v_texcoord1).rgb;

    vec4 overlayTex = texture(u_overlayTexture, v_texcoord1);

    o_FragColor.xyz = baseTex*shadeIntensity+ applySpotLight(spotLight);

    if(overlayingTexture && overlayTex.a > 0.1){
        o_FragColor.xyz = overlayTex.rgb*shadeIntensity;
    }
    o_FragColor.w = 1.0;
    o_FragColor = mix(vec4(u_skyColor, 1.0), o_FragColor, visibility);
}
