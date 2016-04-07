package cz.muni.fi.pv112.project.helpers;

import com.hackoeur.jglm.Vec3;

/**
 * This class offers static methods to ease access to axes in 3D.
 * @author Filip Gdovin
 */
public class AxisHelper {

    private static final float AXES[] = {
            // .. position .......... color .....
            // x axis
            1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            // y axis
            0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            // z axis
            0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
    };

    //axes as Vec3
    private static Vec3 xAxis = new Vec3(1.0f, 0.0f, 0.0f);
    private static Vec3 yAxis = new Vec3(0.0f, 1.0f, 0.0f);
    private static Vec3 zAxis = new Vec3(0.0f, 0.0f, 1.0f);

    /**
     *
     * This returns array which will be interpreted as vectors
     * (1,0,0), (0,1,0) and (0,0,1).
     * @return vectors marking (0,0,0) and showing X,Y and Z axis
     */
    public static float[] getAXES() {
        return AXES;
    }

    /**
     * Vector (1,0,0)
     * @return returns normalized vector of x axis
     */
    public static Vec3 getxAxis() {
        return xAxis;
    }

    /**
     * Vector (0,1,0)
     * @return returns normalized vector of y axis
     */
    public static Vec3 getyAxis() {
        return yAxis;
    }

    /**
     * Vector (0,0,1)
     * @return returns normalized vector of z axis
     */
    public static Vec3 getzAxis() {
        return zAxis;
    }
}
