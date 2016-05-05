package cz.muni.fi.pv112.project;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.TextureIO;
import cz.muni.fi.pv112.project.helpers.*;
import cz.muni.fi.pv112.project.util.*;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.jogamp.opengl.GL3.*;

/**
 * @author Adam Jurcik <xjurc@fi.muni.cz>. Rewritten by Filip Gdovin
 * @author Adam Gdovin, 433305 (PV112 project)
 */
public class Scene implements GLEventListener {

    private final float NUMBER_OF_SHADES = (float) 5;
    private final short NUMBER_OF_COWS = 1;
    private int ACTUAL_NUMBER_OF_COWS;
    private final short NUMBER_OF_TREES = 0;
    private int ACTUAL_NUMBER_OF_TREES;
    private ShaderHelper shaderHelper;

    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Scene.class);


    private FPSAnimator animator;
    private Camera camera;
    private int mode = GL_FILL;

    // window size
    private int width;
    private int height;

    //geometry models
    private Map<String, SceneObject> sceneObjects = new HashMap<>();
    private SceneObject theChosenOne;

    //lights
    private Light spotLight;

    // JOGL resources
    private int joglArray; // JOGL uses own vertex array for updating GLJPanel

    // our GLSL resources (model)
    private int modelProgram;
    private int outlineProgram;
    private int terrainProgram;
    private int emissionProgram;

    private float t = 0;

    private TerrainHelper terrainHelper;

    public Scene(FPSAnimator animator, Camera camera) {
        this.animator = animator;
        this.camera = camera;
    }

    public void toggleLines() {
        mode = GL_LINE;
    }

    public void toggleFill() {
        mode = GL_FILL;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // add listener that will inform us when we make an error.
        gl.getContext().addGLDebugListener(event -> {
            switch (event.getDbgType()) {
                case GL_DEBUG_TYPE_ERROR:
                case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR:
                case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:
                    System.err.println(event.getDbgMsg());
                    break;
            }
        });

        shaderHelper = new ShaderHelper(gl);

        // empty scene color
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glLineWidth(3.0f);

        // enable depth test
        gl.glEnable(GL_DEPTH_TEST);

        // load GLSL program (vertex and fragment shaders)
        try {
            modelProgram = ShaderHelper.loadProgram(gl, "shaders/toon.vs.glsl",
                    "shaders/toon.fs.glsl");
            terrainProgram = ShaderHelper.loadProgram(gl, "shaders/terrain.vs.glsl",
                    "shaders/terrain.fs.glsl");
            outlineProgram = ShaderHelper.loadProgram(gl, "shaders/outline.vs.glsl",
                    "shaders/outline.fs.glsl");
            emissionProgram = ShaderHelper.loadProgram(gl, "shaders/emission.vs.glsl",
                    "shaders/emission.fs.glsl");
        } catch (IOException ex) {
            LOGGER.error(ex.getCause() + ex.getMessage());
            System.exit(1);
        }

        // create buffers with geometry
        int[] buffers = new int[1];
        gl.glGenBuffers(1, buffers, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
        int[] arrays = new int[1];
        gl.glGenVertexArrays(1, arrays, 0);

        // get JOGL vertex array
        int binding[] = new int[1];
        gl.glGetIntegerv(GL_VERTEX_ARRAY_BINDING, binding, 0);
        joglArray = binding[0];
        gl.glBindVertexArray(joglArray);

        // create geometry and save it
        terrainHelper = new TerrainHelper(new TerrainBuilder().seed((long) (Math.random() * 100)).subdivisions(150).heightDelta(75f).tileSize(5f).noiseScale(20f).build());

        camera.setTerrainHelper(terrainHelper);

        SceneObject skyDome = new SceneObject(Geometry.create(ResourceHelper.loadShape("models/sphere.obj"), shaderHelper, terrainProgram));
        skyDome.addMaterial(new Material("textures/stardome.png", TextureIO.PNG,
                new Vector3f(1.0f, 1.0f, 1.0f), 100.0f));
        sceneObjects.put("sky", skyDome);

        SceneObject terrainShape = new SceneObject(Geometry.create(terrainHelper.getTerrain().getTerrainShape(), shaderHelper, terrainProgram));
        terrainShape.addMaterial(new Material("textures/grass.png", TextureIO.PNG,
                new Vector3f(1.0f, 1.0f, 1.0f), 100.0f));
        sceneObjects.put("terrain", terrainShape);

        Map<String, SceneObject> cows = new CowsBuilder(gl, modelProgram, terrainHelper).numberOfCows(NUMBER_OF_COWS).build();
        ACTUAL_NUMBER_OF_COWS = cows.size();
        theChosenOne = cows.get("cow" + ((int) Math.random() * ACTUAL_NUMBER_OF_COWS));
        theChosenOne.setPosition(theChosenOne.getPosition().add(0, 20, 0));
        sceneObjects.putAll(cows);

        SceneObject ufo = new SceneObject(Geometry.create(ResourceHelper.loadShape("models/ufo.obj"), shaderHelper, modelProgram));
        ufo.setPosition(new Vector3f(theChosenOne.getPosition()).add(0,40,0));
        ufo.addMaterial(new Material("textures/UFO_D.tga", TextureIO.TGA, new Vector3f(1, 1, 1), 100f));
        ufo.setNormalMap(new Material("textures/ufo_n.png", TextureIO.PNG, new Vector3f(1, 1, 1), 100f));
        sceneObjects.put("ufo", ufo);

        SceneObject ufo_lights = new SceneObject(Geometry.create(ResourceHelper.loadShape("models/ufo_lights.obj"), shaderHelper, emissionProgram));
        ufo_lights.addMaterial(new Material("textures/UFO_Light_D.tga", TextureIO.TGA, new Vector3f(1, 1, 1), 100f));
        sceneObjects.put("ufo_lights", ufo_lights);

        Map<String, SceneObject> forest = new ForestBuilder(gl, modelProgram, terrainHelper).numberOfTrees(NUMBER_OF_TREES).build();
        ACTUAL_NUMBER_OF_TREES = forest.size();
        sceneObjects.putAll(forest);

        spotLight = new Light(new Vector4f(ufo.getPosition(), 1.0f), new Vector3f(0,100,200), 0.1f, 0.0f, 20.0f, new Vector3f(0,-1,0));

    }


    @Override
    public void dispose(GLAutoDrawable drawable) {
        // not used
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // animate variables
        if (animator.isAnimating()) {
            t += 0.05f;
        }

        // set perspective projection
        Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(60.0f), (float) width / (float) height, 1.0f, 50000.0f);

        // set view transform based on camera position and orientation
        Matrix4f view = new Matrix4f().lookAt(camera.getEyePosition(), new Vector3f(), AXIS.Y.getValue());

        // draw filled polygons or lines
        gl.glPolygonMode(GL_FRONT_AND_BACK, mode);

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // sky
        Matrix4f modelSky = new Matrix4f().scale(100f).rotate(-30f, AXIS.X.getValue());
        Matrix4f vpSky = new Matrix4f(projection).mul(view).mul(modelSky);
        drawObject(gl, modelProgram, 0, 0f, sceneObjects.get("sky"), modelSky, vpSky);

        // terrain
        Matrix4f modelTerrain = new Matrix4f();
        Matrix4f vpTerrain = new Matrix4f(projection).mul(view).mul(modelTerrain);
        drawTerrainObject(gl, terrainProgram, sceneObjects.get("terrain"), new Vector3f(1, 1, 1), modelTerrain, vpTerrain);

        // cows
        for (int i = 0; i < ACTUAL_NUMBER_OF_COWS; i++) {
            SceneObject cow = sceneObjects.get("cow" + i);
            Matrix4f modelCow = new Matrix4f().translate(cow.getPosition()).rotate(cow.getRotation(), AXIS.Y.getValue()).scale(cow.getScale());
            if (cow.equals(theChosenOne)) {
                modelCow.translate(0, (float) Math.sin(t) * 5, 0);
            }
            Matrix4f vpCow = new Matrix4f(projection).mul(view);
            drawObject(gl, modelProgram, outlineProgram, 0.25f, cow, modelCow, vpCow);
        }

        // UFO
        SceneObject ufo = sceneObjects.get("ufo");
        Matrix4f modelUFO = new Matrix4f().translate(ufo.getPosition());
               // .rotate(t, AXIS.Y.getValue());
        Matrix4f vpUFO = new Matrix4f(projection).mul(view).mul(modelTerrain);
        drawObject(gl, modelProgram, 0, 0.25f, ufo, modelUFO, vpUFO);

        // UFO_lights
        Matrix4f modelUFOL = new Matrix4f().translate(sceneObjects.get("ufo").getPosition());
               // .rotate(-t, AXIS.Y.getValue());
        Matrix4f vpUFOL = new Matrix4f(projection).mul(view).mul(modelTerrain);
        drawEmissionObject(gl, emissionProgram, sceneObjects.get("ufo_lights"), modelUFOL, vpUFOL);

        // trees
        for (int i = 0; i < ACTUAL_NUMBER_OF_TREES; i++) {
            SceneObject tree = sceneObjects.get("tree" + i);
            Matrix4f modelTree = new Matrix4f().translate(tree.getPosition()).scale(10f);
            Matrix4f vpTree = new Matrix4f(projection).mul(view);
            drawObject(gl, modelProgram, outlineProgram, 0.1f, tree, modelTree, vpTree);
        }

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }


    private void drawOutline(GL3 gl, int outlineProgram, float lineWidth, SceneObject object, Matrix4f mvp) {
        gl.glUseProgram(outlineProgram);

        shaderHelper.setUniform(outlineProgram, "u_mvp_mat", mvp);
        gl.glEnable(GL_CULL_FACE);

        gl.glCullFace(GL_FRONT);
        gl.glDepthMask(true);
        shaderHelper.setUniform(outlineProgram, "u_color1", new Vector3f(0, 0, 0));
        shaderHelper.setUniform(outlineProgram, "u_offset1", lineWidth);
        object.getGeometry().draw(gl);
        gl.glDisable(GL_CULL_FACE);
    }

    private void drawObject(GL3 gl, int modelProgram, int outlineProgram, float lineWidth, SceneObject object, Matrix4f model, Matrix4f vp) {

        gl.glEnable(GL_DEPTH_TEST);
        if (outlineProgram != 0)
            this.drawOutline(gl, outlineProgram, lineWidth, object, new Matrix4f(vp).mul(model));

        gl.glUseProgram(modelProgram);

        Matrix3f n = (new Matrix3f(model).transpose()).invert();
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shaderHelper.setUniform(modelProgram, "u_model_mat", model);
        shaderHelper.setUniform(modelProgram, "u_viewProj_mat", vp);
        shaderHelper.setUniform(modelProgram, "u_normal_mat", n);

        gl.glDepthMask(true);
        shaderHelper.setUniform(modelProgram, "u_camera_position", camera.getEyePosition());
        shaderHelper.setUniform(modelProgram, "u_light_position", new Vector3f(0, 300, 1000));
        shaderHelper.setUniform(modelProgram, "u_numShades", NUMBER_OF_SHADES);

        shaderHelper.setUniform(modelProgram, "overlayingTexture", false);
        shaderHelper.setUniform(modelProgram, "normalMapping", false);
        shaderHelper.setUniformTexture(modelProgram, "u_baseTexture", object.getMaterials().get(0).getTexture(), GL_TEXTURE0, 0);
        if (object.getMaterials().size() > 1) {
            shaderHelper.setUniform(modelProgram, "overlayingTexture", true);
            shaderHelper.setUniformTexture(modelProgram, "u_overlayTexture", object.getMaterials().get(1).getTexture(), GL_TEXTURE1, 1);
        }
        if (object.getNormalMap() != null) {
            shaderHelper.setUniform(modelProgram, "normalMapping", true);
            shaderHelper.setUniformTexture(modelProgram, "u_normalTexture", object.getNormalMap().getTexture(), GL_TEXTURE2, 2);
        }

        LightHelper.redrawLight(spotLight, shaderHelper, modelProgram );
        object.getGeometry().draw(gl);
        gl.glDisable(GL_DEPTH_TEST);

        gl.glUseProgram(0);
    }

    private void drawEmissionObject(GL3 gl, int emissionProgram, SceneObject object, Matrix4f model, Matrix4f vp) {

        gl.glEnable(GL_DEPTH_TEST);

        gl.glUseProgram(emissionProgram);

        shaderHelper.setUniform(emissionProgram, "u_model_mat", model);
        shaderHelper.setUniform(emissionProgram, "u_viewProj_mat", vp);

        gl.glDepthMask(true);
        shaderHelper.setUniformTexture(emissionProgram, "u_baseTexture", object.getMaterials().get(0).getTexture(), GL_TEXTURE0, 0);

        object.getGeometry().draw(gl);
        gl.glDisable(GL_DEPTH_TEST);

        gl.glUseProgram(0);
    }

    private void drawTerrainObject(GL3 gl, int program, SceneObject object, Vector3f baseColor, Matrix4f model, Matrix4f vp) {
        gl.glUseProgram(program);
        gl.glEnable(GL_DEPTH_TEST);

        Matrix3f n = (new Matrix3f(model).transpose()).invert();

        shaderHelper.setUniform(program, "u_model_mat", model);
        shaderHelper.setUniform(program, "u_viewProj_mat", vp);
        shaderHelper.setUniform(program, "u_normal_mat", n);

        gl.glDepthMask(true);
        shaderHelper.setUniform(program, "u_camera_position", camera.getEyePosition());
        shaderHelper.setUniform(program, "u_light_position", new Vector3f(0, 300, 1000));
        shaderHelper.setUniform(program, "u_numShades", NUMBER_OF_SHADES);
        shaderHelper.setUniform(program, "u_baseColor", baseColor);

        ArrayList<Material> mats = (ArrayList<Material>) object.getMaterials();
        shaderHelper.setUniformTexture(program, "u_baseTexture", mats.get(0).getTexture(), GL_TEXTURE0, 0);

        LightHelper.redrawLight(spotLight, shaderHelper, program );

        object.getGeometry().draw(gl);

        gl.glUseProgram(0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();

        this.width = width;
        this.height = height;

        gl.glViewport(0, 0, width, height);
    }
}