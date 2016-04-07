package cz.muni.fi.pv112.project;

import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Light {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(Light.class);

    //actual light properties
    private Vec4 position;
    private Vec3 ambientColor;
    private Vec3 diffuseColor;
    private Vec3 specularColor;

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

    public Vec4 getPosition() {
        return position;
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

    public float getConeAngle() {
        return coneAngle;
    }

    public Vec3 getConeDirection() {
        return coneDirection;
    }
}
