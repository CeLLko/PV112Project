package cz.muni.fi.pv112.project.util;

import org.joml.Vector3f;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

public class Shape {

    private List<float[]> vertices;
    private List<float[]> normals;
    private List<float[]> texCoords;
    private List<int[]> vertexIndices;
    private List<int[]> normalIndices;
    private List<int[]> texCoordIndices;

    public Shape(List<float[]> vertices, List<float[]> normals,
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

    public Vector3f getRandomVertex(){
        float[] rand = vertices.get((int) (Math.random()*vertices.size()));
        return new Vector3f(rand[0], rand[1], rand[2]);
    }

    public void generateFile(String path){
        System.out.println(path);

        try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"))){
            writer.write("#Adam Gdovin, 433305\n#" +LocalDate.now().toString()+"\n");

            long tStart = System.currentTimeMillis();
            System.out.print("Generating OBJ file ...");
            for (int i = 0; i < vertices.size(); i++) {
                writer.write("v " + vertices.get(i)[0] + " " + vertices.get(i)[1] + " " + vertices.get(i)[2]+"\n");
            }

            for (int i = 0; i < normals.size(); i++) {
                writer.write("vn " + normals.get(i)[0] + " " + normals.get(i)[1] + " " + normals.get(i)[2]+"\n");
            }

            for(int i = 0; i < texCoords.size(); i++){
                writer.write("vt "+texCoords.get(i)[0]+" "+texCoords.get(i)[1]+"\n");
            }

            for(int i=0;i<vertexIndices.size();i++){
                int[] face = vertexIndices.get(i);
                int[] texture = texCoordIndices.get(i);
                int[] normal = normalIndices.get(i);

                String[] out = new String[3];
                out[0] = (face[0]+1)+"/"+(texture[0]+1)+"/"+(normal[0]+1);
                out[1] = (face[1]+1)+"/"+(texture[1]+1)+"/"+(normal[1]+1);
                out[2] = (face[2]+1)+"/"+(texture[2]+1)+"/"+(normal[2]+1);
                writer.write("f "+out[0]+" "+out[1]+" "+out[2]+"\n");
            }
            double tEnd = (System.currentTimeMillis()-tStart)/1000.0f;
            System.out.format("done in %.3f seconds %n", tEnd);
        }catch(IOException ex){
            System.err.println("obj file was not generated");
        }
    }
}
