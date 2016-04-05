package cz.muni.fi.pv112.project;

import java.util.List;

public class Object {

    private List<float[]> vertices;
    private List<float[]> normals;
    private List<float[]> texCoords;
    private List<int[]> vertexIndices;
    private List<int[]> normalIndices;
    private List<int[]> texCoordIndices;

    public Object(List<float[]> vertices, List<float[]> normals,
                  List<float[]> texCoords, List<int[]> vertexIndices,
                  List<int[]> normalIndices, List<int[]> texCoordIndices) {
        this.vertices = vertices;
        this.normals = normals;
        this.texCoords = texCoords;
        this.vertexIndices = vertexIndices;
        this.normalIndices = normalIndices;
        this.texCoordIndices = texCoordIndices;
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
