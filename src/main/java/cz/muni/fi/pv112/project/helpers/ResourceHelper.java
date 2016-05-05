package cz.muni.fi.pv112.project.helpers;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import cz.muni.fi.pv112.project.Scene;
import cz.muni.fi.pv112.project.util.Shape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;

/**
 * This class is used to load various resources (shapes from .obj files and textures)
 * @author Filip Gdovin
 */

public class ResourceHelper {

    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceHelper.class);

    public static Shape loadShape(String path) {

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
            LOGGER.error("Shape loading failed. \nReason: " + e.getCause() + "\nMessage: "+ e.getMessage());
        }
        return new Shape(vertices, normals, texCoords, vertexIndices, normalIndices, texCoordIndices);
    }

    public static Texture binaryNoiseTexture(int width, int height, float scale, long seed, Vector3f color) {
        SimplexValueNoise svn = new SimplexValueNoise(seed);

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int colBrown = ((int) (color.x*255) << 16) | ((int) (color.y*255) << 8) | (int) (color.z*255);
        int colWhite = (255 << 16) | (255 << 8) | 255;
        for(int x = 0;x<width;x++){
            for(int y = 0;y<height;y++){
                double val = svn.eval((double) x/scale, (double) y/scale);
                if(val>=0.0){
                    bi.setRGB(x, y, colBrown);
                }else {
                    bi.setRGB(x, y, colWhite);
                }
            }
        }
        try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(bi, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            return TextureIO.newTexture(is, true, TextureIO.PNG);
        } catch(IOException ex){
            System.err.println("Noise texture not generated");
            return null;
        }
    }

    public static Image loadImage(String path) {
        Image image;

        InputStream is = Scene.class.getResourceAsStream(path);
        if (is == null) {
            LOGGER.error("File not found at given path, aborting");
            return null;
        } else {
            try {
                image = ImageIO.read(is);
                return image;
            } catch (IOException ex) {
                LOGGER.error("Error reading texture file");
            }
            return null;
        }
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

    public static String readAllFromResource(String resource) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(resource);
        if (is == null) {
            LOGGER.error("Resource not found: " + resource);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        int c;
        try {
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            LOGGER.error("Error reading from resource: " + resource);
        }

        return sb.toString();
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
