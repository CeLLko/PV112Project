package cz.muni.fi.pv112.project.util;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Light {

    //actual light properties
    private Vector4f position;
    private Vector3f intensities;
    private float attenuation;
    private float ambientCoefficient;
    private float coneAngle;
    private Vector3f coneDirection;

    public Light(Vector4f position, Vector3f intensities, float attenuation,
                 float ambientCoefficient, float coneAngle, Vector3f coneDirection) {
        this.position = position;
        this.intensities = intensities;
        this.attenuation = attenuation;
        this.ambientCoefficient = ambientCoefficient;
        this.coneAngle = coneAngle;
        this.coneDirection = coneDirection;
    }


    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public void setConeAngle(float coneAngle) {
        this.coneAngle = coneAngle;
    }

    public void setAmbientCoefficient(float ambientCoefficient) {
        this.ambientCoefficient = ambientCoefficient;
    }

    public void setAttenuation(float attenuation) {
        this.attenuation = attenuation;
    }

    public void setIntensities(Vector3f intensities) {
        this.intensities = intensities;
    }

    public void setPosition(Vector4f position) {
        this.position = position;
    }

    public Vector4f getPosition() {
        return position;
    }

    public Vector3f getIntensities() {
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

    public Vector3f getConeDirection() {
        return coneDirection;
    }
}
