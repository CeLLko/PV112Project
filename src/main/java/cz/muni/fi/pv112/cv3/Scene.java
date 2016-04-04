package cz.muni.fi.pv112.cv3;

import com.hackoeur.jglm.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.FPSAnimator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jogamp.opengl.GL3.*;

/**
 * @author Adam Jurcik <xjurc@fi.muni.cz>
 */
public class Scene implements GLEventListener {

    private static final int SIZEOF_AXES_VERTEX = 6 * Buffers.SIZEOF_FLOAT;
    private static final int COLOR_OFFSET = 3 * Buffers.SIZEOF_FLOAT;

    private static final float AXES[] = {
            // .. position .......... color .....
            // x axis
            1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            // y axis
            0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            // z axis
            0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
    };

    private FPSAnimator animator;
    private Camera camera;
    private int mode = GL_FILL;

    // window size
    private int width;
    private int height;

    // models
    private Geometry teapot;

    // JOGL resouces
    private int joglArray; // JOGL uses own vertex array for updating GLJPanel

    // our OpenGL resources
    private int axesBuffer;
    private int axesArray;

    // our GLSL resources (axes)
    private int axesProgram;
    private int axesLengthUniformLoc;
    private int axesMvpUniformLoc;

    // our GLSL resources (model)
    private int modelProgram;
    private int modelLoc;
    private int mvpLoc;
    private int nLoc;
    private int colorLoc;

    private int materialAmbientColorLoc;
    private int materialDiffuseColorLoc;
    private int materialSpecularColorLoc;
    private int materialShininessLoc;

    private int light1PositionLoc;
    private int light1AmbientColorLoc;
    private int light1DiffuseColorLoc;
    private int light1SpecularColorLoc;

    private int light2PositionLoc;
    private int light2AmbientColorLoc;
    private int light2DiffuseColorLoc;
    private int light2SpecularColorLoc;

    private int eyePositionLoc;
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
        gl.getContext().addGLDebugListener(new GLDebugListener() {
            @Override
            public void messageSent(GLDebugMessage event) {
                switch (event.getDbgType()) {
                    case GL_DEBUG_TYPE_ERROR:
                    case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR:
                    case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:
                        System.err.println(event.getDbgMsg());
                        break;
                }
            }
        });

        // empty scene color
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glLineWidth(3.0f);

        // enable depth test
        gl.glEnable(GL_DEPTH_TEST);

        // load GLSL program (vertex and fragment shaders)
        try {
            axesProgram = loadProgram(gl, "shaders/axes.vs.glsl",
                    "shaders/axes.fs.glsl");
            modelProgram = loadProgram(gl, "shaders/model.vs.glsl",
                    "shaders/model.fs.glsl");
        } catch (IOException ex) {
            Logger.getLogger(Scene.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        // get uniform locations
        // axes program uniforms
        axesLengthUniformLoc = gl.glGetUniformLocation(axesProgram, "len");
        axesMvpUniformLoc = gl.glGetUniformLocation(axesProgram, "MVP");

        // model program uniforms
        modelLoc = gl.glGetUniformLocation(modelProgram, "model");
        mvpLoc = gl.glGetUniformLocation(modelProgram, "MVP");
        nLoc = gl.glGetUniformLocation(modelProgram, "N");

        colorLoc = gl.glGetUniformLocation(modelProgram, "color");

        materialAmbientColorLoc = gl.glGetUniformLocation(modelProgram, "materialAmbientColor");
        materialDiffuseColorLoc = gl.glGetUniformLocation(modelProgram, "materialDiffuseColor");
        materialSpecularColorLoc = gl.glGetUniformLocation(modelProgram, "materialSpecularColor");
        materialShininessLoc = gl.glGetUniformLocation(modelProgram, "materialShininess");

        light1PositionLoc = gl.glGetUniformLocation(modelProgram, "light1Position");
        light1AmbientColorLoc = gl.glGetUniformLocation(modelProgram, "light1AmbientColor");
        light1DiffuseColorLoc = gl.glGetUniformLocation(modelProgram, "light1DiffuseColor");
        light1SpecularColorLoc = gl.glGetUniformLocation(modelProgram, "light1SpecularColor");

        light2PositionLoc = gl.glGetUniformLocation(modelProgram, "light2Position");
        light2AmbientColorLoc = gl.glGetUniformLocation(modelProgram, "light2AmbientColor");
        light2DiffuseColorLoc = gl.glGetUniformLocation(modelProgram, "light2DiffuseColor");
        light2SpecularColorLoc = gl.glGetUniformLocation(modelProgram, "light2SpecularColor");

        eyePositionLoc = gl.glGetUniformLocation(modelProgram, "eyePosition");

        // create buffers with geometry
        int[] buffers = new int[1];
        gl.glGenBuffers(1, buffers, 0);
        axesBuffer = buffers[0];

        // fill a buffer with geometry
        gl.glBindBuffer(GL_ARRAY_BUFFER, axesBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, AXES.length * SIZEOF_AXES_VERTEX,
                Buffers.newDirectFloatBuffer(AXES), GL_STATIC_DRAW);
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
        int positionAttribLoc = gl.glGetAttribLocation(axesProgram, "position");
        int colorAttribLoc = gl.glGetAttribLocation(axesProgram, "color");
        // bind axes buffer
        gl.glBindVertexArray(axesArray);
        gl.glBindBuffer(GL_ARRAY_BUFFER, axesBuffer);
        gl.glEnableVertexAttribArray(positionAttribLoc);
        gl.glVertexAttribPointer(positionAttribLoc, 3, GL_FLOAT, false, SIZEOF_AXES_VERTEX, 0);
        gl.glEnableVertexAttribArray(colorAttribLoc);
        gl.glVertexAttribPointer(colorAttribLoc, 3, GL_FLOAT, false, SIZEOF_AXES_VERTEX, COLOR_OFFSET);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        // clear current Vertex Array Object state
        gl.glBindVertexArray(joglArray);

        ObjLoader model = new ObjLoader("models/teapot-high.obj");
        try {
            model.load();
        } catch (IOException ex) {
            Logger.getLogger(Scene.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        // get cube program attributes
        positionAttribLoc = gl.glGetAttribLocation(modelProgram, "position");
        int normalAttribLoc = gl.glGetAttribLocation(modelProgram, "normal");
        // create geometry
        teapot = Geometry.create(gl, model, positionAttribLoc, normalAttribLoc);
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
        }

        // set perspective projection
        Mat4 projection = Matrices.perspective(60.0f, (float) width / (float) height, 1.0f, 500.0f);

        // set view transform based on camera position and orientation
        Vec3 yAxis = new Vec3(0.0f, 1.0f, 0.0f);
        Mat4 view = Matrices.lookAt(camera.getEyePosition(), Vec3.VEC3_ZERO, yAxis);

        // get projection * view (VP) matrix
        Mat4 vp = Mat4.MAT4_IDENTITY;
        vp = vp.multiply(projection);
        vp = vp.multiply(view);


        Vec4 lightPos = new Vec4(3.0f, 0.0f, 0.0f, 1.0f);

        lightPos = MatricesUtils.transform(Matrices.rotate(t, yAxis), lightPos);


        gl.glUseProgram(modelProgram);

        //gl.glUniform4fv(light1PositionLoc, 1, lightPos.getBuffer());
        gl.glUniform4f(light1PositionLoc, lightPos.getX(), lightPos.getY(),
                lightPos.getZ(), lightPos.getW());

        gl.glUniform3f(light1AmbientColorLoc, 0.1f, 0.1f, 0.1f);
        gl.glUniform3f(light1DiffuseColorLoc, 1.0f, 1.0f, 0.0f);
        gl.glUniform3f(light1SpecularColorLoc, 1.0f, 1.0f, 1.0f);

        gl.glUniform3fv(eyePositionLoc, 1, camera.getEyePosition().getBuffer());

        gl.glUseProgram(0);

        // draw filled polygons or lines
        gl.glPolygonMode(GL_FRONT_AND_BACK, mode);

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // draw global scene (world) coordinates
        drawAxes(gl, vp, 3.0f);

        // teapot
        Mat4 model = Mat4.MAT4_IDENTITY;
        Mat4 mvp = projection.multiply(view).multiply(model);

        Material material = new Material(
                new Vec3(1.0f, 0.5f, 0.0f),
                new Vec3(1.0f, 0.5f, 0.0f),
                new Vec3(1.0f, 1.0f, 1.0f),
                100.0f);

        drawModel(gl, model, mvp, material);


        // teapot 2
        model = Mat4.MAT4_IDENTITY.translate(new Vec3(5.0f, 0.0f, 0.0f));
        mvp = projection.multiply(view).multiply(model);

        material = new Material(
                new Vec3(0.0f, 0.0f, 1.0f),
                new Vec3(0.0f, 1.0f, 1.0f),
                new Vec3(1.0f, 1.0f, 1.0f),
                100.0f);

        drawModel(gl, model, mvp, material);

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

        gl.glUniform1f(axesLengthUniformLoc, length);
        gl.glUniformMatrix4fv(axesMvpUniformLoc, 1, false, mvp.getBuffer());

        gl.glDrawArrays(GL_LINES, 0, 6);

        gl.glBindVertexArray(joglArray);
        gl.glUseProgram(0);
    }

    private void drawModel(GL3 gl, Mat4 model, Mat4 mvp, Material material) {
        gl.glUseProgram(modelProgram);

        Mat3 n = MatricesUtils.inverse(getMat3(model).transpose());
        gl.glUniformMatrix3fv(nLoc, 1, false, n.getBuffer());

        gl.glUniformMatrix4fv(modelLoc, 1, false, model.getBuffer());

        gl.glUniform3fv(materialAmbientColorLoc, 1, material.getAmbientColor().getBuffer());
        gl.glUniform3fv(materialDiffuseColorLoc, 1, material.getDiffuseColor().getBuffer());
        gl.glUniform3fv(materialSpecularColorLoc, 1, material.getSpecularColor().getBuffer());
        gl.glUniform1f(materialShininessLoc, material.getShininess());

        gl.glUniformMatrix4fv(mvpLoc, 1, false, mvp.getBuffer());

        gl.glUniform3f(colorLoc, 1f, 1f, 0.2f);

        teapot.draw(gl);

        gl.glUseProgram(0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();

        this.width = width;
        this.height = height;

        gl.glViewport(0, 0, width, height);
    }

    private int loadShader(GL3 gl, String filename, int shaderType) throws IOException {
        String source = readAllFromResource(filename);
        int shader = gl.glCreateShader(shaderType);

        // create and compile GLSL shader
        gl.glShaderSource(shader, 1, new String[]{source}, new int[]{source.length()}, 0);
        gl.glCompileShader(shader);

        // check GLSL shader compile status
        int[] status = new int[1];
        gl.glGetShaderiv(shader, GL_COMPILE_STATUS, status, 0);
        if (status[0] == GL_FALSE) {
            int[] length = new int[1];
            gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, length, 0);

            byte[] log = new byte[length[0]];
            gl.glGetShaderInfoLog(shader, length[0], length, 0, log, 0);

            String error = new String(log, 0, length[0]);
            System.err.println(error);
        }

        return shader;
    }

    private int loadProgram(GL3 gl, String vertexShaderFile, String fragmentShaderFile) throws IOException {
        // load vertex and fragment shaders (GLSL)
        int vs = loadShader(gl, vertexShaderFile, GL_VERTEX_SHADER);
        int fs = loadShader(gl, fragmentShaderFile, GL_FRAGMENT_SHADER);

        // create GLSL program, attach shaders and compile it
        int program = gl.glCreateProgram();
        gl.glAttachShader(program, vs);
        gl.glAttachShader(program, fs);
        gl.glLinkProgram(program);

        int[] linkStatus = new int[1];
        gl.glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] == GL_FALSE) {
            int[] length = new int[1];
            gl.glGetProgramiv(program, GL_INFO_LOG_LENGTH, length, 0);

            byte[] log = new byte[length[0]];
            gl.glGetProgramInfoLog(program, length[0], length, 0, log, 0);

            String error = new String(log, 0, length[0]);
            System.err.println(error);
        }

        return program;
    }

    private String readAllFromResource(String resource) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(resource);
        if (is == null) {
            throw new IOException("Resource not found: " + resource);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        int c;
        while ((c = reader.read()) != -1) {
            sb.append((char) c);
        }

        return sb.toString();
    }

}