package cz.muni.fi.pv112.project.helpers;


import org.joml.Vector3f;

public enum AXIS {
    X(new Vector3f(1.0f, 0.0f, 0.0f)), Y(new Vector3f(0.0f, 1.0f, 0.0f)), Z(new Vector3f(0.0f, 0.0f, 1.0f));

    private Vector3f value;

    AXIS(Vector3f value) {
        this.value = value;
    }

    public Vector3f getValue() {
        return value;
    }
}