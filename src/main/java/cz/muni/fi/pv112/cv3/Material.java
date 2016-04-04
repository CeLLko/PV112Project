package cz.muni.fi.pv112.cv3;

import com.hackoeur.jglm.Vec3;

public class Material {

    private Vec3 ambientColor;
    private Vec3 diffuseColor;
    private Vec3 specularColor;
    private float shininess;

    public Material(Vec3 ambientColor, Vec3 diffuseColor, Vec3 specularColor, float shininess) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.shininess = shininess;
    }

    public Vec3 getAmbientColor() {
        return ambientColor;
    }

    public Vec3 getDiffuseColor() {
        return diffuseColor;
    }

    public Vec3 getSpecularColor() {
        return specularColor;
    }

    public float getShininess() {
        return shininess;
    }

}
