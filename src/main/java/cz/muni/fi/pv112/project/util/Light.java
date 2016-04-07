package cz.muni.fi.pv112.project.util;

import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Light {

    //actual light properties
    private boolean isOn = true;
    private Vec4 position;
    private Vec3 ambientColor = new Vec3(0.8f,0.8f,0.8f);
    private Vec3 diffuseColor = new Vec3(1,1,1);
    private Vec3 specularColor = new Vec3(1,1,1);

    /**
     * Angle between the center and the side of the cone, in degrees.
     */
    private float coneAngle;

    /**
     * Direction from the point of the cone, through the center of the cone.
     */
    private Vec3 coneDirection;

    public Light(Vec4 position, Vec3 ambientColor, Vec3 diffuseColor,
                 Vec3 specularColor, float coneAngle, Vec3 coneDirection) {
        this.position = position;
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.coneAngle = coneAngle;
        this.coneDirection = coneDirection;
    }

    public void toggle() {
        this.isOn = !isOn();
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public Vec4 getPosition() {
        return position;
    }

    public void setPosition(Vec4 position) {
        this.position = position;
    }

    public Vec3 getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(Vec3 ambientColor) {
        this.ambientColor = ambientColor;
    }

    public Vec3 getDiffuseColor() {
        return diffuseColor;
    }

    public Vec3 getSpecularColor() {
        return specularColor;
    }

    public float getConeAngle() {
        return coneAngle;
    }

    public Vec3 getConeDirection() {
        return coneDirection;
    }
}
