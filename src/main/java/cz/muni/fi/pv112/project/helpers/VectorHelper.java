package cz.muni.fi.pv112.project.helpers;


import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * This class simplifies manipulation with vectors
 * @author Filip Gdovin rewritten by Adam Gdovin
 */
public class VectorHelper {

    public static Vector4f toVector4f(Vector3f vec3, float fourth) {
        return new Vector4f(vec3.x, vec3.y, vec3.z, fourth);
    }

    public static Vector3f toVector3f(Vector4f vec4) {
        return new Vector3f(vec4.x, vec4.y, vec4.z);
    }

    public static Vector3f getUnitVector(Vector3f vec3){
        float len = (float) Math.sqrt(vec3.x*vec3.x + vec3.y*vec3.y + vec3.z*vec3.z);
        return new Vector3f(vec3.x/len, vec3.y/len, vec3.z/len);
    }

    public static double get2DDistance(Vector3f src, Vector3f dest){
        return Math.sqrt(Math.pow(Math.abs(src.x - dest.x),2) + Math.pow(Math.abs(src.z - dest.z),2));
    }

    public static float[] getArray(Vector3f vec3){
        return new float[]{vec3.x, vec3.y, vec3.z};
    }

    public static Vector3f getVector(float[] vec){
        return new Vector3f(vec[0], vec[1], vec[2]);
    }
}
