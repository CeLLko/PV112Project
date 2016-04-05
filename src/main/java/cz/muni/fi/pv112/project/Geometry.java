package cz.muni.fi.pv112.project;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL3.*;

public class Geometry {

    private static final int SIZEOF_POSITION = 3 * Buffers.SIZEOF_FLOAT;
    private static final int SIZEOF_NORMAL = 3 * Buffers.SIZEOF_FLOAT;
    private static final int SIZEOF_TEXCOORD = 2 * Buffers.SIZEOF_FLOAT;
    private static int joglArray = 0;
    private int positionBuffer;
    private int normalBuffer;
    private int texCoordBuffer;
    private int geometryArray;
    private int triangleCount;

    public Geometry(int positionBuffer, int normalBuffer, int texCoordBuffer,
                    int geometryArray, int triangleCount) {
        this.positionBuffer = positionBuffer;
        this.normalBuffer = normalBuffer;
        this.texCoordBuffer = texCoordBuffer;
        this.geometryArray = geometryArray;
        this.triangleCount = triangleCount;
    }

    /**
     * Creates geometry with positions and normals. The geometry will not contain
     * texture coordinates!
     *
     * @param gl
     * @param model
     * @param positionAttribLoc
     * @param normalAttribLoc
     * @return
     */
    public static Geometry create(GL3 gl, Object model, int positionAttribLoc,
                                  int normalAttribLoc) {
        return create(gl, model, positionAttribLoc, normalAttribLoc, -1);
    }

    public static Geometry create(GL3 gl, Object model, int positionAttribLoc,
                                  int normalAttribLoc, int texCoordAttribLoc) {
        if (joglArray <= 0) {
            initJoglArray(gl);
        }

        int vertexCount = 3 * model.getTriangleCount();
        FloatBuffer positionData = Buffers.newDirectFloatBuffer(vertexCount * 3);
        FloatBuffer normalData = Buffers.newDirectFloatBuffer(vertexCount * 3);
        FloatBuffer texCoordData = Buffers.newDirectFloatBuffer(vertexCount * 2);

        for (int f = 0; f < model.getTriangleCount(); f++) {
            int[] pi = model.getVertexIndices().get(f);
            int[] ni = model.getNormalIndices().get(f);
            int[] ti = model.getTexCoordIndices().get(f);
            for (int i = 0; i < 3; i++) {
                float[] position = model.getVertices().get(pi[i]);
                float[] normal = model.getNormals().get(ni[i]);
                float[] texCoord = model.getTexCoords().get(ti[i]);
                positionData.put(position);
                normalData.put(normal);
                texCoordData.put(texCoord);
            }
        }

        positionData.rewind();
        normalData.rewind();
        texCoordData.rewind();

        // create buffers with geometry
        int positionBuffer = 0;
        int normalBuffer = 0;
        int texCoordBuffer = 0;
        if (positionAttribLoc >= 0) {
            int size = positionData.capacity() * Buffers.SIZEOF_FLOAT;
            positionBuffer = createBuffer(gl, GL_ARRAY_BUFFER, size, positionData);
        }
        if (normalAttribLoc >= 0) {
            int size = normalData.capacity() * Buffers.SIZEOF_FLOAT;
            normalBuffer = createBuffer(gl, GL_ARRAY_BUFFER, size, normalData);
        }
        if (texCoordAttribLoc >= 0) {
            int size = texCoordData.capacity() * Buffers.SIZEOF_FLOAT;
            texCoordBuffer = createBuffer(gl, GL_ARRAY_BUFFER, size, texCoordData);
        }

        int[] arrays = new int[1];
        gl.glGenVertexArrays(1, arrays, 0);
        gl.glBindVertexArray(arrays[0]);
        if (positionAttribLoc >= 0) {
            gl.glBindBuffer(GL_ARRAY_BUFFER, positionBuffer);
            gl.glEnableVertexAttribArray(positionAttribLoc);
            gl.glVertexAttribPointer(positionAttribLoc, 3, GL_FLOAT, false, SIZEOF_POSITION, 0);
        }
        if (normalAttribLoc >= 0) {
            gl.glBindBuffer(GL_ARRAY_BUFFER, normalBuffer);
            gl.glEnableVertexAttribArray(normalAttribLoc);
            gl.glVertexAttribPointer(normalAttribLoc, 3, GL_FLOAT, false, SIZEOF_POSITION, 0);
        }
        if (texCoordAttribLoc >= 0) {
            gl.glBindBuffer(GL_ARRAY_BUFFER, texCoordBuffer);
            gl.glEnableVertexAttribArray(texCoordAttribLoc);
            gl.glVertexAttribPointer(texCoordAttribLoc, 3, GL_FLOAT, false, SIZEOF_POSITION, 0);
        }

        // restore defaults
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
        gl.glBindVertexArray(joglArray);

        return new Geometry(positionBuffer, normalBuffer, texCoordBuffer,
                arrays[0], model.getTriangleCount());
    }

    private static void initJoglArray(GL3 gl) {
        // get JOGL vertex array
        int binding[] = new int[1];
        gl.glGetIntegerv(GL_VERTEX_ARRAY_BINDING, binding, 0);
        joglArray = binding[0];
    }

    private static int createBuffer(GL3 gl, int type, int size, Buffer data) {
        // create buffer
        int[] buffers = new int[1];
        gl.glGenBuffers(1, buffers, 0);
        // upload buffer data
        gl.glBindBuffer(type, buffers[0]);
        gl.glBufferData(type, size, data, GL_STATIC_DRAW);
        gl.glBindBuffer(type, 0);

        return buffers[0];
    }

    public void draw(GL3 gl) {
        gl.glBindVertexArray(geometryArray);
        gl.glDrawArrays(GL_TRIANGLES, 0, 3 * triangleCount);
        gl.glBindVertexArray(joglArray);
    }

}
