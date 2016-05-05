package cz.muni.fi.pv112.project.helpers;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.stereo.generic.GenericStereoDeviceConfig;
import com.jogamp.opengl.util.texture.TextureIO;
import cz.muni.fi.pv112.project.util.Geometry;
import cz.muni.fi.pv112.project.util.Material;
import cz.muni.fi.pv112.project.util.SceneObject;
import cz.muni.fi.pv112.project.util.Shape;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Adam Gdovin, 433305
 */
public class TreeBuilder {
    private String treeTopModel;
    private String trunkModel;

    private List<float[]> treeVertices = new ArrayList<>();
    private List<float[]> treeNormals = new ArrayList<>();
    private List<float[]> treeTexCoords = new ArrayList<>();
    private List<int[]> treeVertexIndices = new ArrayList<>();
    private List<int[]> treeNormalIndices = new ArrayList<>();
    private List<int[]> treeTexCoordIndices = new ArrayList<>();

    public TreeBuilder(){
        this.treeTopModel = "models/treetop.obj";
        this.trunkModel = "models/trunk.obj";
    }

    public TreeBuilder treeTopModel(final String treeTopModel){
        this.treeTopModel=treeTopModel;
        return this;
    }

    public TreeBuilder trunkModel(final String trunkModel){
        this.trunkModel=trunkModel;
        return this;
    }

    public Shape build(){
        double chance=1;
        int layerIndex = 0;
        while(layerIndex<=5 && chance>Math.random()){
            Shape layer = new ResourceHelper().loadShape(treeTopModel);
            List<float[]> vertices = new ArrayList<>(layer.getVertices());
            List<float[]> normals = new ArrayList<>(layer.getNormals());
            List<float[]> texCoords = new ArrayList<>(layer.getTexCoords());
            List<int[]> vertexIndices = new ArrayList<>(layer.getVertexIndices());
            List<int[]> normalIndices = new ArrayList<>(layer.getNormalIndices());
            List<int[]> texCoordIndices = new ArrayList<>(layer.getTexCoordIndices());

            for(float[] vertex : vertices){
                if(vertex[1]>0.5) {
                    vertex[1] += ((0.5f+((float) Math.random()/20f)) * layerIndex)-(0.2*layerIndex);
                } else {
                    vertex[1] += ((0.5f+((float) Math.random()/20f))* layerIndex)+(0.2*layerIndex);
                    vertex[0] *= Math.pow(0.8,layerIndex);
                    vertex[2] *= Math.pow(0.8,layerIndex);
                }
            }

            for(int[] vertexIndex : vertexIndices){
                vertexIndex[0]+=(vertices.size()*layerIndex);
                vertexIndex[1]+=(vertices.size()*layerIndex);
                vertexIndex[2]+=(vertices.size()*layerIndex);
            }
            for(int[] normalIndex : normalIndices){
                normalIndex[0]+=(normals.size()*layerIndex);
                normalIndex[1]+=(normals.size()*layerIndex);
                normalIndex[2]+=(normals.size()*layerIndex);
            }

            treeVertices.addAll(vertices);
            treeNormals.addAll(normals);
            treeTexCoords.addAll(texCoords);
            treeVertexIndices.addAll(vertexIndices);
            treeNormalIndices.addAll(normalIndices);
            treeTexCoordIndices.addAll(texCoordIndices);

            layerIndex++;
            chance*=0.8;
        }

        Shape trunk = new ResourceHelper().loadShape(trunkModel);
        List<float[]> vertices = new ArrayList<>(trunk.getVertices());
        List<float[]> normals = new ArrayList<>(trunk.getNormals());
        List<float[]> texCoords = new ArrayList<>(trunk.getTexCoords());
        List<int[]> vertexIndices = new ArrayList<>(trunk.getVertexIndices());
        List<int[]> normalIndices = new ArrayList<>(trunk.getNormalIndices());
        List<int[]> texCoordIndices = new ArrayList<>(trunk.getTexCoordIndices());

        for(int[] vertexIndex : vertexIndices){
            vertexIndex[0]+=(treeVertices.size());
            vertexIndex[1]+=(treeVertices.size());
            vertexIndex[2]+=(treeVertices.size());
        }
        for(int[] normalIndex : normalIndices){
            normalIndex[0]+=(treeNormals.size());
            normalIndex[1]+=(treeNormals.size());
            normalIndex[2]+=(treeNormals.size());
        }
        for(int[] texCoordIndex : texCoordIndices){
            texCoordIndex[0]+=(treeTexCoordIndices.size());
            texCoordIndex[1]+=(treeTexCoordIndices.size());
            texCoordIndex[2]+=(treeTexCoordIndices.size());
        }

        treeVertices.addAll(vertices);
        treeNormals.addAll(normals);
        treeTexCoords.addAll(texCoords);
        treeVertexIndices.addAll(vertexIndices);
        treeNormalIndices.addAll(normalIndices);
        treeTexCoordIndices.addAll(texCoordIndices);

        return new Shape(treeVertices,treeNormals,treeTexCoords,
                        treeVertexIndices,treeNormalIndices,treeTexCoordIndices);
    }

}
