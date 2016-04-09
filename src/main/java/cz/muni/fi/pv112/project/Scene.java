package cz.muni.fi.pv112.project;

import com.hackoeur.jglm.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.TextureIO;
import cz.muni.fi.pv112.project.helpers.*;
import cz.muni.fi.pv112.project.util.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.jogamp.opengl.GL3.*;

/**
 * @author Adam Jurcik <xjurc@fi.muni.cz>. Rewritten by Filip Gdovin
 */
public class Scene implements GLEventListener {

    private ShaderHelper shaderHelper;

    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Scene.class);

    private static final int SIZEOF_AXES_VERTEX = 6 * Buffers.SIZEOF_FLOAT;
    private static final int COLOR_OFFSET = 3 * Buffers.SIZEOF_FLOAT;
    private static final int NUM_OF_ADDITIONAL_LIGHTS = 4;

    private FPSAnimator animator;
    private Camera camera;
    private int mode = GL_FILL;

    // window size
    private int width;
    private int height;

    //geometry models
    private Map<String, Geometry> geometryModels = new HashMap<>();

    //materials
    private Map<String, Material> materials = new HashMap<>();

    //lights
    private List<Light> lights = new ArrayList<>();
    private Vec3 ambientColor = new Vec3(0.1f,0.1f,0.1f);

    // JOGL resources
    private int joglArray; // JOGL uses own vertex array for updating GLJPanel

    // our OpenGL resources
    private int axesBuffer;
    private int axesArray;

    // our GLSL resources (axes)
    private int axesProgram;

    // our GLSL resources (model)
    private int modelProgram;

    private float t = 0;

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
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glLineWidth(3.0f);

        // enable depth test
        gl.glEnable(GL_DEPTH_TEST);

        // load GLSL program (vertex and fragment shaders)
        try {
            axesProgram = ShaderHelper.loadProgram(gl, "shaders/axes.vs.glsl",
                    "shaders/axes.fs.glsl");
            modelProgram = ShaderHelper.loadProgram(gl, "shaders/model.vs.glsl",
                    "shaders/model.fs.glsl");
        } catch (IOException ex) {
            LOGGER.error(ex.getCause() + ex.getMessage());
            System.exit(1);
        }

        //create lights (one Sun + NUM_OF_ADDITIONAL_LIGHTS spotlights)
        lights.add(LightHelper.createSun());
     //   lights.addAll(LightHelper.createNRandomLights(NUM_OF_ADDITIONAL_LIGHTS));

        //create materials
        materials.put("rocks", new Material("textures/rocks.jpg", TextureIO.JPG,
                                            new Vec3(1.0f, 1.0f, 1.0f), 60.0f));
        materials.put("wood", new Material("textures/wood.jpg", TextureIO.JPG,
                                            new Vec3(1.0f, 1.0f, 1.0f), 20.0f));
        materials.put("sun", new Material("textures/sun.jpg", TextureIO.JPG,
                                            new Vec3(1.0f, 1.0f, 1.0f), 80.0f));

        // create buffers with geometry
        int[] buffers = new int[1];
        gl.glGenBuffers(1, buffers, 0);
        axesBuffer = buffers[0];

        // fill a buffer with geometry
        gl.glBindBuffer(GL_ARRAY_BUFFER, axesBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, AxisHelper.getAXES().length * SIZEOF_AXES_VERTEX,
                Buffers.newDirectFloatBuffer(AxisHelper.getAXES()), GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        // create a vertex array object for the geometry
        int[] arrays = new int[1];
        gl.glGenVertexArrays(1, arrays, 0);
        axesArray = arrays[0];

        // get JOGL vertex array
        int binding[] = new int[1];
        gl.glGetIntegerv(GL_VERTEX_ARRAY_BINDING, binding, 0);
        joglArray = binding[0];

        // get axes program attributes
        int positionAttribLoc = shaderHelper.getAttributeLocation(axesProgram, "position");
        int colorAttribLoc = shaderHelper.getAttributeLocation(axesProgram, "color");

        // bind axes buffer
        gl.glBindVertexArray(axesArray);
        gl.glBindBuffer(GL_ARRAY_BUFFER, axesBuffer);
        gl.glEnableVertexAttribArray(positionAttribLoc);
        gl.glVertexAttribPointer(positionAttribLoc, 3, GL_FLOAT, false, SIZEOF_AXES_VERTEX, 0);
        gl.glEnableVertexAttribArray(colorAttribLoc);
        gl.glVertexAttribPointer(colorAttribLoc, 3, GL_FLOAT, false, SIZEOF_AXES_VERTEX, COLOR_OFFSET);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        // clear current Vertex Array Shape state
        gl.glBindVertexArray(joglArray);

        // create geometry and save it
        geometryModels.put("teapot", Geometry.create(ResourceHelper.loadShape("models/teapot-high.obj"),
                                                    shaderHelper, modelProgram));

        geometryModels.put("sphere", Geometry.create(ResourceHelper.loadShape("models/sphere.obj"),
                                                    shaderHelper, modelProgram));
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
            t += 0.02f;
            //rotate sun around Y axis
            LightHelper.rotateLight(lights.get(0), AXIS.Y);
        }

        // set perspective projection
        Mat4 projection = Matrices.perspective(60.0f, (float) width / (float) height, 1.0f, 500.0f);

        // set view transform based on camera position and orientation
        Mat4 view = Matrices.lookAt(camera.getEyePosition(), Vec3.VEC3_ZERO, AXIS.Y.getValue());

        // get projection * view (VP) matrix
        Mat4 vp = Mat4.MAT4_IDENTITY;
        vp = vp.multiply(projection);
        vp = vp.multiply(view);

        gl.glUseProgram(modelProgram);

        LightHelper.redrawLights(lights, ambientColor, shaderHelper, modelProgram);

        shaderHelper.setUniform(modelProgram, "eyePosition", camera.getEyePosition());

        gl.glUseProgram(0);

        // draw filled polygons or lines
        gl.glPolygonMode(GL_FRONT_AND_BACK, mode);

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // draw global scene (world) coordinates
        drawAxes(gl, vp, 3.0f);

        // first teapot
        Mat4 model = Mat4.MAT4_IDENTITY;
        Mat4 mvp = projection.multiply(view).multiply(model);

        drawObject(gl, new SceneObject(geometryModels.get("teapot"), materials.get("rocks")), model, mvp);

        // second teapot
        Mat4 modelTranslated = Mat4.MAT4_IDENTITY.translate(new Vec3(5.0f, 0.0f, 0.0f));
        Mat4 mvpTranslated = projection.multiply(view).multiply(modelTranslated);

        drawObject(gl, new SceneObject(geometryModels.get("teapot"), materials.get("wood")), modelTranslated, mvpTranslated);

        Mat4 sunSphereTranslated = Mat4.MAT4_IDENTITY.translate(LightHelper.getLightVector(lights.get(0)));

        Mat4 mvpSphereTranslated = projection.multiply(view).multiply(sunSphereTranslated);

        drawObject(gl, new SceneObject(geometryModels.get("sphere"), materials.get("sun")), sunSphereTranslated, mvpSphereTranslated);

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    private Mat3 getMat3(Mat4 m) {
        Vec4 col0 = m.getColumn(0);
        Vec4 col1 = m.getColumn(1);
        Vec4 col2 = m.getColumn(2);
        return new Mat3(
                col0.getX(), col0.getY(), col0.getZ(),
                col1.getX(), col1.getY(), col1.getZ(),
                col2.getX(), col2.getY(), col2.getZ());
    }

    private void drawAxes(GL3 gl, Mat4 mvp, float length) {
        gl.glUseProgram(axesProgram);
        gl.glBindVertexArray(axesArray);

        shaderHelper.setUniform(axesProgram, "len", length);
        shaderHelper.setUniform(axesProgram, "MVP", mvp);

        gl.glDrawArrays(GL_LINES, 0, 6);

        gl.glBindVertexArray(joglArray);
        gl.glUseProgram(0);
    }

    private void drawObject(GL3 gl, SceneObject object, Mat4 model, Mat4 mvp) {
        gl.glUseProgram(modelProgram);

        Mat3 n = MatricesUtils.inverse(getMat3(model).transpose());

        //matrices describing model location
        shaderHelper.setUniform(modelProgram, "N", n);
        shaderHelper.setUniform(modelProgram, "model", model);

        Material objectMaterial = object.getMaterial();

        shaderHelper.setUniformTexture(modelProgram, "object.texture", objectMaterial.getTexture(), GL_TEXTURE0, 0);
        shaderHelper.setUniform(modelProgram, "object.specularColor", objectMaterial.getSpecularColor());
        shaderHelper.setUniform(modelProgram, "object.shininess", objectMaterial.getShininess());

        shaderHelper.setUniform(modelProgram, "MVP", mvp);

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

    /**
     * Toggles state of n-th light. Toggling of 0-th light is not possible (default Sun light)
     * @param lightNumber index of light which should be toggled (1 for 1st light)
     */
    public void toggleLight(int lightNumber) {
        if(lightNumber == 0) {
            LOGGER.warn("You can NOT turn off the Sun!");
        }
        if(lights.size() >= lightNumber) {
            lights.get(lightNumber-1).toggle();
        }
    }

    public void turnOnAllLights() {
        for (int i = 1; i < lights.size(); i++) {
            lights.get(i).setOn(true);
        }
    }

    public void turnOffAllLights() {
        for (int i = 1; i < lights.size(); i++) {
            lights.get(i).setOn(false);
        }
    }
}