package cz.muni.fi.pv112.project.helpers;

import cz.muni.fi.pv112.project.util.SceneObject;
import cz.muni.fi.pv112.project.util.Shape;
import cz.muni.fi.pv112.project.util.Terrain;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import org.joml.Vector3f;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.jar.Pack200;

/**
 * @author Adam Gdovin, 433305
 */
public class TerrainHelper {

    private static final int MAX_NUM_OF_TRIES = 25;
    private static final ImageProcessor img = new ColorProcessor(ResourceHelper.loadImage("/textures/objectmap.png"));
    private final Terrain terrain;
    private final Vector3f[][] vertices;
    public TerrainHelper(Terrain terrain) {
        this.terrain = terrain;
        int subdivisions = terrain.getSubdivisions();
        vertices = new Vector3f[subdivisions][];
        for(int i = 0;i<vertices.length;i++){
            vertices[i] = new Vector3f[subdivisions];
        }

        for(int j=0;j<subdivisions;j++){
            for(int i=0;i<subdivisions;i++) {
                vertices[i][j]=VectorHelper.getVector(terrain.getTerrainShape().getVertices().get(j*subdivisions+i));
            }
        }
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public static float getTerrainHeightMultiplier(float x, float z, int subdivisions) {
        if (img == null) {
            return 1f;
        } else {
            int red = (img.get(getRealX(x, subdivisions), getRealZ(z, subdivisions)) >> 16) & 0xFF;
            return red / 255.0f;
        }
    }

    public Vector3f getRandomTreePosition(Collection<SceneObject> forest, int tries) throws ArithmeticException {

        if (tries >= MAX_NUM_OF_TRIES)
            throw new ArithmeticException("no suitable position found");

        Vector3f treePosition = new Vector3f(terrain.getTerrainShape().getRandomVertex());
        for (SceneObject tree : forest) {
            if (VectorHelper.get2DDistance(treePosition, tree.getPosition()) < 6.0) {
                return getRandomTreePosition(forest, ++tries);
            }
        }

        if (img == null) {
            return treePosition;
        } else {
            int green = (img.get(
                    getRealX(treePosition.x / terrain.getTileSite(), terrain.getSubdivisions()),
                    getRealZ(treePosition.z / terrain.getTileSite(), terrain.getSubdivisions())) >> 8) & 0xFF;
            float chance = (float) Math.random();
            if (green / 255.0f > chance)
                return treePosition;
            else
                return getRandomTreePosition(forest, tries);
        }
    }


    public Vector3f getRandomCowPosition(Collection<SceneObject> cows, int tries) throws ArithmeticException {

        if (tries >= MAX_NUM_OF_TRIES)
            throw new ArithmeticException("no suitable position found");

        Vector3f cowPosition = new Vector3f(terrain.getTerrainShape().getRandomVertex());
        for (SceneObject cow : cows) {
            if (VectorHelper.get2DDistance(cowPosition, cow.getPosition()) < 6.0) {
                return getRandomCowPosition(cows, ++tries);
            }
        }

        if (img == null) {
            return cowPosition;
        } else {
            int blue = (img.get(getRealX(cowPosition.x / terrain.getTileSite(), terrain.getSubdivisions()),
                    getRealZ(cowPosition.z / terrain.getTileSite(), terrain.getSubdivisions()))) & 0xFF;
            float chance = (float) Math.random();
            if (blue / 255.0f > chance)
                return cowPosition;
            else
                return getRandomCowPosition(cows, tries);
        }
    }

    private static int getRealZ(float z, int subdivisions) {
        int realZ = (int) ((z + (subdivisions / 2)) * (img.getHeight() / (float) subdivisions));
        if (realZ < 0)
            realZ = 0;
        if (realZ > img.getHeight() - 1)
            realZ = img.getHeight() - 1;

        return realZ;
    }

    private static int getRealX(float x, int subdivisions) {
        int realX = (int) ((x + (subdivisions / 2)) * (img.getWidth() / (float) subdivisions));
        if (realX < 0)
            realX = 0;
        if (realX > img.getWidth() - 1)
            realX = img.getWidth() - 1;

        return realX;
    }

    public Vector3f getClosestVertex(Vector3f camPos) {

        int subdivisions = vertices[0].length;
        int midX = subdivisions/2;
        int midZ = subdivisions/2;

        while(true) {
            Vector3f mid = vertices[midX][midZ];
            double dist = VectorHelper.get2DDistance(camPos, mid);

            if(dist< VectorHelper.get2DDistance(camPos, vertices[midX+1][midZ+1]) &&
                    dist< VectorHelper.get2DDistance(camPos, vertices[midX+1][midZ-1]) &&
                    dist< VectorHelper.get2DDistance(camPos, vertices[midX-1][midZ+1]) &&
                    dist< VectorHelper.get2DDistance(camPos, vertices[midX-1][midZ-1]))
                return mid;
            else {
                if (camPos.x < mid.x && camPos.z > mid.z) {
                    midX = midX / 2;
                    midZ = midZ / 2;
                } else if (camPos.x < mid.x && camPos.z < mid.z) {
                    midX = midX / 2;
                    midZ = midZ + (midZ / 2);
                } else if (camPos.x > mid.x && camPos.z > mid.z) {
                    midX = midX + (midX / 2);
                    midZ = midZ / 2;
                } else if (camPos.x > mid.x && camPos.z > mid.z) {
                    midX = midX + (midX / 2);
                    midZ = midZ + (midZ / 2);
                }
            }
        }
    }
}
