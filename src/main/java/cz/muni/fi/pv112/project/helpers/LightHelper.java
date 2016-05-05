package cz.muni.fi.pv112.project.helpers;

import cz.muni.fi.pv112.project.util.Light;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class offers static methods to ease access to lights in 3D.
 * @author Filip Gdovin rewritten by Adam Gdovin
 */
public class LightHelper {

    public static void redrawLight(Light light, ShaderHelper shaderHelper, int program) {
        shaderHelper.setUniform(program, "spotLight.intensities", light.getIntensities());
        shaderHelper.setUniform(program, "spotLight.attenuation", light.getAttenuation());
        shaderHelper.setUniform(program, "spotLight.ambientCoefficient", light.getAmbientCoefficient());
        shaderHelper.setUniform(program, "spotLight.coneAngle", light.getConeAngle());
        shaderHelper.setUniform(program, "spotLight.position", light.getPosition());
        shaderHelper.setUniform(program, "spotLight.coneDirection", light.getConeDirection());
    }
    private static Vector3f createRandomColor() {
        int delimiter = randomInt(0,2);
        switch (delimiter) {
            case 0: {   //red
                return new Vector3f(1.0f, 0.0f, 0.0f);
            }
            case 1: {   //green
                return new Vector3f(0.0f, 1.0f, 0.0f);
            }
            default: {   //blue
                return new Vector3f(0.0f, 0.0f, 1.0f);
            }
        }
    }

    private static int randomInt(int from, int to) {
        Random rand = new Random();

        return rand.nextInt((to - from) + 1) + from;
    }
}
