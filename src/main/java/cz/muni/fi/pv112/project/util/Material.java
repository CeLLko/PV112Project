package cz.muni.fi.pv112.project.util;

import com.jogamp.opengl.util.texture.Texture;
import cz.muni.fi.pv112.project.helpers.ResourceHelper;
import org.joml.Vector3f;

public class Material {

    private Texture texture;
    private Vector3f specularColor;
    private float shininess;

    public Material(Texture texture, Vector3f specularColor, float shininess) {
        this.texture = texture;
        this.specularColor = specularColor;
        this.shininess = shininess;
    }

    public Material(String resource, String suffix, Vector3f specularColor, float shininess) {
        this.texture = ResourceHelper.loadTexture(resource, suffix);
        this.specularColor = specularColor;
        this.shininess = shininess;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector3f getSpecularColor() {
        return specularColor;
    }

    public float getShininess() {
        return shininess;
    }

}
