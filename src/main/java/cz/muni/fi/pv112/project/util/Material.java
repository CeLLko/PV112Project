package cz.muni.fi.pv112.project.util;

import com.hackoeur.jglm.Vec3;
import com.jogamp.opengl.util.texture.Texture;
import cz.muni.fi.pv112.project.helpers.ResourceHelper;

public class Material {

    private Texture texture;
    private Vec3 specularColor;
    private float shininess;

    public Material(Texture texture, Vec3 specularColor, float shininess) {
        this.texture = texture;
        this.specularColor = specularColor;
        this.shininess = shininess;
    }

    public Material(String resource, String suffix, Vec3 specularColor, float shininess) {
        this.texture = ResourceHelper.loadTexture(resource, suffix);
        this.specularColor = specularColor;
        this.shininess = shininess;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vec3 getSpecularColor() {
        return specularColor;
    }

    public float getShininess() {
        return shininess;
    }

}
