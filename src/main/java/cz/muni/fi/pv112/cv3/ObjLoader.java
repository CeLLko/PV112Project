package cz.muni.fi.pv112.cv3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ObjLoader {

    private String path;

    private List<float[]> vertices;
    private List<float[]> normals;
    private List<float[]> texCoords;
    private List<int[]> vertexIndices;
    private List<int[]> normalIndices;
    private List<int[]> texCoordIndices;

    public ObjLoader(String path) {
        this.path = path;
    }

    public void load() throws IOException {
        /* Mesh containing the loaded object */
        vertices = new ArrayList<>();
        normals = new ArrayList<>();
        texCoords = new ArrayList<>();
        vertexIndices = new ArrayList<>();
        normalIndices = new ArrayList<>();
        texCoordIndices = new ArrayList<>();

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(path);
        if (is == null) {
            throw new IOException("File not found " + path);
        }

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
    }

    private float[] create2DPointFromLine(String line) {

        String[] lineContents = getLineContents(line);
        float[] point2D = new float[2];

        point2D[0] = Float.parseFloat(lineContents[1]);
        point2D[1] = Float.parseFloat(lineContents[2]);
        return point2D;
    }

    private float[] create3DPointFromLine(String line) {

        String[] lineContents = getLineContents(line);
        float[] point3D = new float[3];

        point3D[0] = Float.parseFloat(lineContents[1]);
        point3D[1] = Float.parseFloat(lineContents[2]);
        point3D[2] = Float.parseFloat(lineContents[3]);

        return point3D;
    }

    private int[] create3DPointFromLine(String line, int index) {

        String[] lineContents = getLineContents(line);
        int[] point3D = new int[3];

        point3D[0] = Integer.parseInt(lineContents[1].split("/")[index]) - 1;
        point3D[1] = Integer.parseInt(lineContents[2].split("/")[index]) - 1;
        point3D[2] = Integer.parseInt(lineContents[3].split("/")[index]) - 1;

        return point3D;
    }

    private String[] getLineContents(String line) {
        return line.split("\\s+");
    }

    public List<float[]> getVertices() {
        return vertices;
    }

    public List<float[]> getNormals() {
        return normals;
    }

    public List<float[]> getTexCoords() {
        return texCoords;
    }

    public List<int[]> getVertexIndices() {
        return vertexIndices;
    }

    public List<int[]> getNormalIndices() {
        return normalIndices;
    }

    public List<int[]> getTexCoordIndices() {
        return texCoordIndices;
    }

    public int getTriangleCount() {
        return vertexIndices.size();
    }

}
