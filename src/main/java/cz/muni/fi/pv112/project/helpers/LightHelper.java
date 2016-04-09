package cz.muni.fi.pv112.project.helpers;

import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import cz.muni.fi.pv112.project.util.Light;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class offers static methods to ease access to lights in 3D.
 * @author Filip Gdovin
 */
public class LightHelper {

    private static final float SUN_DISTANCE = 15;
    private static final float SUN_ANGLE_CHANGE = 0.1f;

    /**
     * This will create one Light, which is set at (SUN_DISTANCE,0,0,0)
     * and is directional. It comes as turned on by default.
     * @return Sun
     */
    public static Light createSun() {
        Light defaultSun = new Light(new Vec4(0, 0, SUN_DISTANCE, 0),
                new Vec3(1, 1, 1), new Vec3(1, 1, 1), 0, new Vec3(0, 0, 0));
        return defaultSun;
    }

    public static Light moveSun(Light sun) {
        //position
        float x = sun.getPosition().getX();
        float y = sun.getPosition().getY();
        float z = sun.getPosition().getZ();
        float w = sun.getPosition().getW();

        x = (float) (0 + (Math.cos(Math.toRadians(SUN_ANGLE_CHANGE)) * (x - 0) - Math.sin(Math.toRadians(SUN_ANGLE_CHANGE)) * (z - 0)));
        z = (float) (0 + (Math.sin(Math.toRadians(SUN_ANGLE_CHANGE)) * (x - 0) + Math.cos(Math.toRadians(SUN_ANGLE_CHANGE)) * (z - 0)));

        sun.setPosition(new Vec4(x, y, z, w));

//        color
//        float r = sun.getAmbientColor().getX();
//        float g = sun.getAmbientColor().getY();
//        float b = sun.getAmbientColor().getZ();

        //sun.setAmbientColor(new Vec3(r-Math.abs(z), g, b));

        /*//turn on/off sun according to Z axis location
        if(z >= 0) {
            sun.setOn(true);
        } else if (z <= 0) {
            sun.setOn(false);
        }*/

        return sun;
    }

    /**
     * This will create N Lights, which are set at random positions
     * and are spotlights. All of them are either red, blue or green,
     * have angle between 5 and 75 degrees and are aimed at (0,0,0).
     * @return bunch of random spotlights
     */
    public static List<Light> createNRandomLights(int numOfLights) {
        List<Light> lights = new ArrayList<>();
        for(int i = 0; i < numOfLights; i++) {
            Vec4 position = randomizePosition();
            Vec3 diffuseColor = createRandomColor();
            Vec3 specularColor = new Vec3(1.0f, 1.0f, 1.0f);

            float coneAngle = randomInt(5,75);
            Vec3 coneDirection = new Vec3(0.0f, 0.0f, 0.0f);
            lights.add(new Light(position,  diffuseColor, specularColor, coneAngle, coneDirection));
        }
        return lights;
    }

    /**
     * This method updates changes in list of Lights into shaders.
     * @param lights Lights to be updated/redrawn
     * @param ambientColor Ambient light color
     * @param shaderHelper Helper to communicate with shaders
     * @param program program to use
     */
    public static void redrawLights(List<Light> lights, Vec3 ambientColor, ShaderHelper shaderHelper, int program) {
        for (int i = 0; i < lights.size(); i++) {

            Light current = lights.get(i);
            shaderHelper.setUniform(program, "allLights[" + i + "].position", current.getPosition());
            shaderHelper.setUniform(program, "allLights[" + i + "].diffuseColor", current.getDiffuseColor());
            shaderHelper.setUniform(program, "allLights[" + i + "].specularColor", current.getSpecularColor());
            /*shaderHelper.setUniform(program, "allLights[" + i + "].coneAngle", current.getConeAngle());
            shaderHelper.setUniform(program, "allLights[" + i + "].coneDirection", current.getConeDirection());*/
        }
        shaderHelper.setUniform(program, "ambientColor", ambientColor);
    }

    private static Vec4 randomizePosition() {
        return new Vec4(randomInt(0,10) + 0.0f, randomInt(0,10) + 0.0f, randomInt(0,10) + 0.0f, 1.0f);
    }

    private static Vec3 createRandomColor() {
        int delimiter = randomInt(0,2);
        switch (delimiter) {
            case 0: {   //red
                return new Vec3(1.0f, 0.0f, 0.0f);
            }
            case 1: {   //green
                return new Vec3(0.0f, 1.0f, 0.0f);
            }
            default: {   //blue
                return new Vec3(0.0f, 0.0f, 1.0f);
            }
        }
    }

    private static int randomInt(int from, int to) {
        Random rand = new Random();

        return rand.nextInt((to - from) + 1) + from;
    }
}
