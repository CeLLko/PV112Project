package cz.muni.fi.pv112.project;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(ObjectLoader.class);

    public static Object load(String path) {

        List<float[]> vertices = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();
        List<float[]> texCoords = new ArrayList<>();
        List<int[]> vertexIndices = new ArrayList<>();
        List<int[]> normalIndices = new ArrayList<>();
        List<int[]> texCoordIndices = new ArrayList<>();

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(path);
        if (is == null) {
            LOGGER.error("File not found at given path, aborting");
        }

        try {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {

                String line;
                while ((line = in.readLine()) != null) {

                    if (line.startsWith("v ")) {
                        vertices.add(create3DPointFromLine(line));

                    } else if (line.startsWith("vn ")) {
                        normals.add(create3DPointFromLine(line));

                    } else if (line.startsWith("vt ")) {
                        texCoords.add(create2DPointFromLine(line));

                    } else if (line.startsWith("f ")) {
                        vertexIndices.add(create3DPointFromLine(line, 0));
                        texCoordIndices.add(create3DPointFromLine(line, 1));

                        if (getLineContents(line)[1].split("/").length >= 3) {
                            normalIndices.add(create3DPointFromLine(line, 2));
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Object loading failed. \nReason: " + e.getCause() + "\nMessage: "+ e.getMessage());
        }


        return new Object(vertices, normals, texCoords, vertexIndices, normalIndices, texCoordIndices);
    }

    public static Texture loadTexture(String path, String suffix) {
        Texture texture;

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(path);
        if (is == null) {
            LOGGER.error("File not found at given path, aborting");
            return null;
        } else {
            try {
                texture = TextureIO.newTexture(is, true, suffix);
                return texture;
            } catch (IOException ex) {
                LOGGER.error("Error reading texture file");
            }
            return null;
        }
    }

    private static float[] create2DPointFromLine(String line) {

        String[] lineContents = getLineContents(line);
        float[] point2D = new float[2];

        point2D[0] = Float.parseFloat(lineContents[1]);
        point2D[1] = Float.parseFloat(lineContents[2]);
        return point2D;
    }

    private static float[] create3DPointFromLine(String line) {

        String[] lineContents = getLineContents(line);
        float[] point3D = new float[3];

        point3D[0] = Float.parseFloat(lineContents[1]);
        point3D[1] = Float.parseFloat(lineContents[2]);
        point3D[2] = Float.parseFloat(lineContents[3]);

        return point3D;
    }

    private static int[] create3DPointFromLine(String line, int index) {

        String[] lineContents = getLineContents(line);
        int[] point3D = new int[3];

        point3D[0] = Integer.parseInt(lineContents[1].split("/")[index]) - 1;
        point3D[1] = Integer.parseInt(lineContents[2].split("/")[index]) - 1;
        point3D[2] = Integer.parseInt(lineContents[3].split("/")[index]) - 1;

        return point3D;
    }

    private static String[] getLineContents(String line) {
        return line.split("\\s+");
    }
}
