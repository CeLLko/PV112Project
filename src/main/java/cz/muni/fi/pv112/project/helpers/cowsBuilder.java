package cz.muni.fi.pv112.project.helpers;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.TextureIO;
import cz.muni.fi.pv112.project.util.Geometry;
import cz.muni.fi.pv112.project.util.Material;
import cz.muni.fi.pv112.project.util.SceneObject;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Adam Gdovin, 433305
 */

public class CowsBuilder {
    private GL3 gl;
    private int program;
    private ShaderHelper shaderHelper;
    private short numberOfCows;
    private String cowModel;
    private String cowTexture;

    private TerrainHelper terrainHelper;

    private Map<String, SceneObject> cows = new HashMap<>();

    public CowsBuilder(GL3 gl, int program, TerrainHelper terrainHelper){
        this.gl = gl;
        this.program = program;
        this.numberOfCows = 10;
        this.cowModel = "models/cow.obj";
        this.cowTexture = "textures/cow_256.png";
        this.terrainHelper = terrainHelper;
    }

    public CowsBuilder numberOfCows(final short numberOfCows){
        this.numberOfCows=numberOfCows;
        return this;
    }

    public CowsBuilder cowModel(final String cowModel){
        this.cowModel=cowModel;
        return this;
    }

    public CowsBuilder cowTexture(final String cowTexture){
        this.cowTexture=cowTexture;
        return this;
    }

    public Map<String, SceneObject> build(){
        shaderHelper = new ShaderHelper(gl);
        for(int i = 0;i<numberOfCows;i++){
            long tStart = System.currentTimeMillis();
            System.out.print("Generating cow #"+(i+1)+"...");
            try {
                SceneObject cow = new SceneObject(Geometry.create(new ResourceHelper().loadShape(cowModel), shaderHelper, program));
                cow.setPosition(terrainHelper.getRandomCowPosition(cows.values(), 0));
                cow.setRotation((float) Math.random() * 360);
                cow.setScale((float) Math.random() / 2 + 0.5f);

                cow.addMaterial(new Material(ResourceHelper.binaryNoiseTexture(256, 256, 10f, (long) i * 500, new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random())), new Vector3f(1.0f, 1.0f, 1.0f), 100.0f));
                cow.addMaterial(new Material(cowTexture, TextureIO.PNG, new Vector3f(1.0f, 1.0f, 1.0f), 100.0f));
                cows.put("cow" + i, cow);
                double tEnd = (System.currentTimeMillis() - tStart) / 1000.0f;
                System.out.format("done in %.3f seconds %n", tEnd);
            } catch (ArithmeticException ex){
                System.out.print("failed: "+ex.getMessage());
                break;
            }
        }
        return cows;
    }

}
