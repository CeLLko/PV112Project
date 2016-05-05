package cz.muni.fi.pv112.project.helpers;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.TextureIO;
import cz.muni.fi.pv112.project.util.Geometry;
import cz.muni.fi.pv112.project.util.Material;
import cz.muni.fi.pv112.project.util.SceneObject;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam Gdovin, 433305
 */
public class ForestBuilder {
    private GL3 gl;
    private int program;
    private ShaderHelper shaderHelper;
    private short numberOfTrees;
    private String treeTopModel;
    private String trunkModel;
    private String treeTexture;

    private TerrainHelper terrainHelper;

    private Map<String, SceneObject> forest = new HashMap<>();

    public ForestBuilder(GL3 gl, int program, TerrainHelper terrainHelper){
        this.gl = gl;
        this.program = program;
        this.numberOfTrees = 10;
        this.treeTopModel = "models/treetop.obj";
        this.trunkModel = "models/trunk.obj";
        this.treeTexture = "textures/tree.png";
        this.terrainHelper = terrainHelper;
    }

    public ForestBuilder numberOfTrees(final short numberOfTrees){
        this.numberOfTrees=numberOfTrees;
        return this;
    }

    public ForestBuilder treeTopModel(final String treeTopModel){
        this.treeTopModel=treeTopModel;
        return this;
    }

    public ForestBuilder trunkModel(final String trunkModel){
        this.trunkModel=trunkModel;
        return this;
    }

    public ForestBuilder treeTexture(final String treeTexture){
        this.treeTexture=treeTexture;
        return this;
    }

    public Map<String, SceneObject> build(){
        shaderHelper = new ShaderHelper(gl);
        for(int i = 0;i<numberOfTrees;i++){

            long tStart = System.currentTimeMillis();
            System.out.print("Generating tree #"+(i+1)+"...");
            try {
                SceneObject tree = new SceneObject(Geometry.create(new TreeBuilder().treeTopModel(treeTopModel).trunkModel(trunkModel).build(), shaderHelper, program));
                tree.setPosition(terrainHelper.getRandomTreePosition(forest.values(), 0).add(new Vector3f(0,7,0)));
                tree.setRotation((float) Math.random() * 360);
                tree.setScale((float) Math.random()*10f + 5f);
                tree.addMaterial(new Material(treeTexture, TextureIO.PNG, new Vector3f(1.0f, 1.0f, 1.0f), 100.0f));

                forest.put("tree" + i, tree);
                double tEnd = (System.currentTimeMillis() - tStart) / 1000.0f;
                System.out.format("done in %.3f seconds %n", tEnd);
            } catch (ArithmeticException ex){
                System.out.print("failed: "+ex.getMessage());
                break;
            }
        }
        return forest;
    }

}
