package cz.muni.fi.pv112.project.helpers;

import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;

/**
 * This class simplifies manipulation with vectors
 * @author Filip Gdovin
 */
public class VectorHelper {

    public static Vec4 toVec4(Vec3 vec3, float fourth) {
        return new Vec4(vec3.getX(), vec3.getY(), vec3.getZ(), fourth);
    }

    public static Vec3 toVec3(Vec4 vec4) {
        return new Vec3(vec4.getX(), vec4.getY(), vec4.getZ());
    }
}
