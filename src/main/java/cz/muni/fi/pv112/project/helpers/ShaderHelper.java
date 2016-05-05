package cz.muni.fi.pv112.project.helpers;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.GL_FALSE;
import static com.jogamp.opengl.GL2ES2.*;
import static com.jogamp.opengl.GL2ES2.GL_INFO_LOG_LENGTH;
import static com.jogamp.opengl.GL2ES2.GL_LINK_STATUS;

/**
 * This class is used to ease work with shaders, especially with setting and getting uniform locations
 * @author Filip Gdovin
 */
public class ShaderHelper {

    private GL3 gl;

    private static final Logger LOGGER = LoggerFactory.getLogger(ShaderHelper.class);

    public ShaderHelper(GL3 gl) {
        this.gl = gl;
    }

    public GL3 getGl() {
        return gl;
    }

    public void setGl(GL3 gl) {
        this.gl = gl;
    }

    public void setUniform(int program, String property, int value) {
        int position = getUniformLocation(program, property);
        gl.glUniform1i(position, value);
    }

    public void setUniform(int program, String property, float value) {
        int position = getUniformLocation(program, property);
        gl.glUniform1f(position, value);
    }

    public void setUniform(int program, String property, Vector3f value) {
        int position = getUniformLocation(program, property);
        FloatBuffer fb = BufferUtils.createFloatBuffer(9);
        gl.glUniform3fv(position, 1, value.get(fb));
    }

    public void setUniform(int program, String property, Vector4f value) {
        int position = getUniformLocation(program, property);
        gl.glUniform4f(position, value.x, value.y, value.z, value.w);
    }

    public void setUniform(int program, String property, boolean value) {
        int position = getUniformLocation(program, property);
        gl.glUniform1i(position, (value) ? 1 : 0);
    }

    public void setUniform(int program, String property, Matrix3f value) {
        int position = getUniformLocation(program, property);

        FloatBuffer fb = BufferUtils.createFloatBuffer(9);
        gl.glUniformMatrix3fv(position, 1, false, value.get(fb));
    }

    public void setUniform(int program, String property, Matrix4f value) {
        int position = getUniformLocation(program, property);

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        gl.glUniformMatrix4fv(position, 1, false, value.get(fb));
    }

    public void setUniformTexture(int program, String property, Texture value, int textureUnit, int index) {
        int position = getUniformLocation(program, property);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glActiveTexture(textureUnit);
        value.bind(gl);
        gl.glUniform1i(position, index);
    }

    public int getUniformLocation(int program, String property) {
        int location = gl.glGetUniformLocation(program, property);
        if(location == -1) {
            String errorMessage = "Invalid shader location: " + property;
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        return location;
    }

    public static int loadProgram(GL3 gl, String vertexShaderFile, String fragmentShaderFile) throws IOException {
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
            LOGGER.error(error);
        }

        return program;
    }

    private static int loadShader(GL3 gl, String filename, int shaderType) throws IOException {
        String source = ResourceHelper.readAllFromResource(filename);
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
            LOGGER.error(error);
        }

        return shader;
    }

    public int getAttributeLocation(int program, String property) {
        return gl.glGetAttribLocation(program, property);
    }
}
