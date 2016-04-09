package cz.muni.fi.pv112.project.helpers;

import com.hackoeur.jglm.Vec3;

public enum AXIS {
    X(new Vec3(1.0f, 0.0f, 0.0f)), Y(new Vec3(0.0f, 1.0f, 0.0f)), Z(new Vec3(0.0f, 0.0f, 1.0f));

    private Vec3 value;

    AXIS(Vec3 value) {
        this.value = value;
    }

    public Vec3 getValue() {
        return value;
    }
}
