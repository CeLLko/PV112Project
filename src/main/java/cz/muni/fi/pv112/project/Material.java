package cz.muni.fi.pv112.project;

import com.hackoeur.jglm.Vec3;
import com.jogamp.opengl.util.texture.Texture;

public class Material {

    private Texture texture;
    private Vec3 specularColor;
    private float shininess;

    public Material(Texture texture, Vec3 specularColor, float shininess) {
        this.texture = texture;
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
