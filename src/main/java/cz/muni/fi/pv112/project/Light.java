package cz.muni.fi.pv112.project;

import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Light {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(Light.class);

    //actual light properties
    private Vec4 position;
    private Vec3 intensities;

    /**
     * Attenuation is the loss of light intensity over distance.
     * The greater the distance, the lower the intensity.
     *
     * We will represent attenuation as a percentage of remaining light,
     * in a float with a value between zero and one.
     * For example, an attenuation value of 0.2 means that 80% of the light intensity has been lost,
     * and only 20% of the intensity remains.
     */
    private float attenuation;

    /**
     * We will calculate the ambient component using a percentage of the original intensities of the light source.
     *
     * We will store this ambient percentage as a float with a value between zero (0%) and one (100%).
     * For example if ambientCoefficient is 0.05 (5%) and the reflected light intensities are
     * (1,0,0), which is pure red light, then the ambient component will be (0.05,0,0), which is very dim red light.
     */
    private float ambientCoefficient;

    /**
     * Angle between the center and the side of the cone, in degrees.
     */
    private float coneAngle;

    /**
     * Direction from the point of the cone, through the center of the cone.
     */
    private Vec3 coneDirection;

    public Light(Vec4 position, Vec3 intensities, float attenuation,
                 float ambientCoefficient, float coneAngle, Vec3 coneDirection) {
        this.position = position;
        this.intensities = intensities;
        this.attenuation = attenuation;
        this.ambientCoefficient = ambientCoefficient;
        this.coneAngle = coneAngle;
        this.coneDirection = coneDirection;
    }

    public Vec4 getPosition() {
        return position;
    }

    public Vec3 getIntensities() {
        return intensities;
    }

    public float getAttenuation() {
        return attenuation;
    }

    public float getAmbientCoefficient() {
        return ambientCoefficient;
    }

    public float getConeAngle() {
        return coneAngle;
    }

    public Vec3 getConeDirection() {
        return coneDirection;
    }
}
