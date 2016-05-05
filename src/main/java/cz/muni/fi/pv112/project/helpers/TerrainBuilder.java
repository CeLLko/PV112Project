package cz.muni.fi.pv112.project.helpers;

import cz.muni.fi.pv112.project.util.Shape;
import cz.muni.fi.pv112.project.util.Terrain;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Gdovin, 433305
 */
public class TerrainBuilder {
    private int subdivisions;
    private float tileSize;
    private float heightDelta;
    private float noiseScale;
    private long seed;
    private boolean generateFile;
    private boolean smooth;
    private String path;

    public TerrainBuilder(){
        this.subdivisions = 100;
        this.tileSize = 1.0f;
        this.heightDelta = 10.0f;
        this.noiseScale = 10.0f;
        this.seed = 0L;
        this.generateFile = false;
        this.smooth = false;
        this.path = (Shape.class.getClassLoader().getResource("models/")+"untitled.obj").split("/",2)[1];
    }

    public TerrainBuilder subdivisions(final int subdivisions){
        this.subdivisions=subdivisions;
        return this;
    }

    public TerrainBuilder tileSize(final float tileSize){
        this.tileSize=tileSize;
        return this;
    }

    public TerrainBuilder heightDelta(final float heightDelta){
        this.heightDelta=heightDelta;
        return this;
    }

    public TerrainBuilder noiseScale(final float noiseScale){
        this.noiseScale=noiseScale;
        return this;
    }

    public TerrainBuilder seed(final long seed){
        this.seed=seed;
        return this;
    }

    public TerrainBuilder generateFile(){
        this.generateFile=true;
        return this;
    }
    public TerrainBuilder smooth(){
        this.smooth=true;
        return this;
    }
    /*
    public TerrainBuilder generateFile(final boolean generateFile){
        this.generateFile=generateFile;
        return this;
    }*/

    public TerrainBuilder path(final String path){
        this.path=path;
        return this;
    }

    public Terrain build(){

        Terrain terrain = new Terrain(subdivisions, tileSize);

        long tStart = System.currentTimeMillis();
        System.out.print("Generating terrain ...");
        if(subdivisions%2 == 0)
            subdivisions++;

        SimplexValueNoise noiseGen = new SimplexValueNoise(seed);

        List<float[]> vertices = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();
        List<float[]> texCoords = new ArrayList<>();
        List<int[]> vertexIndices = new ArrayList<>();
        List<int[]> normalIndices = new ArrayList<>();
        List<int[]> texCoordIndices = new ArrayList<>();

        for(int gridZ = 0; gridZ<subdivisions;gridZ++){
            for(int gridX = 0; gridX<subdivisions;gridX++) {
                float x = (gridX-subdivisions/2);
                float z = (gridZ-subdivisions/2);
                float y = ((float) (noiseGen.eval(x/noiseScale, z/noiseScale)+1f)*heightDelta)
                        *(TerrainHelper.getTerrainHeightMultiplier(x,z, subdivisions));
                vertices.add(new float[]{x*tileSize,y,z*tileSize});

            }
        }

        for(int gridZ = 0; gridZ<subdivisions-1;gridZ++){
            for(int gridX = 0; gridX<subdivisions-1;gridX++){
                int current = gridZ*subdivisions+gridX;
                vertexIndices.add(new int[]{current, current+1, current+subdivisions});
                vertexIndices.add(new int[]{current+subdivisions+1, current+subdivisions, current+1});
            }
        }

        texCoords.add(new float[]{1.0f,0.0f});
        texCoords.add(new float[]{1.0f,1.0f});
        texCoords.add(new float[]{0.0f,0.0f});
        texCoords.add(new float[]{0.0f,1.0f});
        for(int i=0;i<vertexIndices.size();i++){
            if(i%2==0) {
                texCoordIndices.add(new int[]{0, 1, 2});
            }
            else {
                texCoordIndices.add(new int[]{3, 2, 1});
            }
        }

        for(int i=0;i<vertices.size();i++){
            int upIndex = i-subdivisions;
            int uprightIndex = i-subdivisions+1;
            int rightIndex = i+1;
            int downIndex = i+subdivisions;
            int downleftIndex = i+subdivisions-1;
            int leftIndex = i-1;

            Vector3f currentVec = new Vector3f(vertices.get(i)[0],vertices.get(i)[1],vertices.get(i)[2]);
            Vector3f[] vectors = new Vector3f[6];

            if(upIndex>0 && upIndex<vertices.size())
                vectors[0] = new Vector3f(vertices.get(upIndex)[0],vertices.get(upIndex)[1],vertices.get(upIndex)[2]);
            if(uprightIndex>0 && uprightIndex<vertices.size())
                vectors[1] = new Vector3f(vertices.get(uprightIndex)[0],vertices.get(uprightIndex)[1],vertices.get(uprightIndex)[2]);
            if(rightIndex>0 && rightIndex<vertices.size())
                vectors[2] = new Vector3f(vertices.get(rightIndex)[0],vertices.get(rightIndex)[1],vertices.get(rightIndex)[2]);
            if(downIndex>0 && downIndex<vertices.size())
                vectors[3] = new Vector3f(vertices.get(downIndex)[0],vertices.get(downIndex)[1],vertices.get(downIndex)[2]);
            if(downleftIndex>0 && downleftIndex<vertices.size())
                vectors[4] = new Vector3f(vertices.get(downleftIndex)[0],vertices.get(downleftIndex)[1],vertices.get(downleftIndex)[2]);
            if(leftIndex>0 && leftIndex<vertices.size())
                vectors[5] = new Vector3f(vertices.get(leftIndex)[0],vertices.get(leftIndex)[1],vertices.get(leftIndex)[2]);

            List<Vector3f> faceNormals = new ArrayList<>();
            for(int j =0; j<6; j++){
                if(vectors[j] != null && vectors[(j+1)%6] != null){
                    Vector3f V = new Vector3f(currentVec).sub(vectors[j]);
                    Vector3f W = new Vector3f(vectors[(j+1)%6]).sub(vectors[j]);
                    faceNormals.add((V.cross(W)));
                }
            }
            Vector3f normal = faceNormals.get(0);
            for(int j=1;j<faceNormals.size();j++){
                normal.add(faceNormals.get(j));
            }
            normals.add(VectorHelper.getArray(VectorHelper.getUnitVector(normal)));
        }
        for(int i=0;i<vertexIndices.size();i++){
            normalIndices.add(vertexIndices.get(i));
        }

        if(vertexIndices.size()!= normalIndices.size() ||
                normalIndices.size() != texCoordIndices.size() ||
                vertexIndices.size()!= texCoordIndices.size())
            throw new ArithmeticException("array sizes not same");

        Shape shape = new Shape(vertices, normals, texCoords, vertexIndices, normalIndices, texCoordIndices);
        terrain.setTerrainShape(shape);

        double tEnd = (System.currentTimeMillis()-tStart)/1000.0f;
        System.out.format("done in %.3f seconds %n", tEnd);
        if(generateFile) {
            shape.generateFile(path);
        }
        return terrain;
    }
}
