package main.common;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.opengl.GL40;
import main.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Ali Khaddour
 */
public class Shader {

    private int handle;
    // new added
    private Map<String, Integer> uniformLocations;

    public Shader(String vertPath, String fragPath) throws IOException, Exception {
        String shaderSource = Utils.readShaderFile(vertPath);
        int vertexShader = GL40.glCreateShader(GL40.GL_VERTEX_SHADER);
        GL40.glShaderSource(vertexShader, shaderSource);
        compileShader(vertexShader);

        shaderSource = Utils.readShaderFile(fragPath);
        int fragShader = GL40.glCreateShader(GL40.GL_FRAGMENT_SHADER);
        GL40.glShaderSource(fragShader, shaderSource);
        compileShader(fragShader);

        handle = GL40.glCreateProgram();

        GL40.glAttachShader(handle, vertexShader);
        GL40.glAttachShader(handle, fragShader);

        // link the shaders together
        linkProgram(handle);

        GL40.glDetachShader(handle, vertexShader);
        GL40.glDetachShader(handle, fragShader);
        GL40.glDeleteShader(vertexShader);
        GL40.glDeleteShader(fragShader);

        // new added
        uniformLocations = new HashMap<>();

        // First, we have to get the number of active uniforms in the shader.
        int numberOfUniforms = GL40.glGetProgrami(handle, GL40.GL_ACTIVE_UNIFORMS);
//        int numberOfUniforms = outParam.get();

        // Loop over all the uniforms,
        for (int i = 0; i < numberOfUniforms; i++) {
            IntBuffer _out1 = BufferUtils.createIntBuffer(1);
            IntBuffer _out2 = BufferUtils.createIntBuffer(1);
            // get the name of this uniform,
            // params: program, index, size, type
            String name = GL40.glGetActiveUniform(handle, i, _out1, _out2);
            System.out.println(name);
            int location = GL40.glGetUniformLocation(handle, name);
            System.out.println(name + " " + location);
            uniformLocations.put(name, location);
        }

    }

    private static void compileShader(int shader) throws Exception {
        GL40.glCompileShader(shader);
        if (GL40.glGetShaderi(shader, GL40.GL_COMPILE_STATUS) == GL40.GL_FALSE) {
            throw new Exception("Error occurred while compiling Shader" + shader + GL40.glGetShaderInfoLog(shader));
        }
    }

    private static void linkProgram(int program) throws Exception {
        GL40.glLinkProgram(program);
        if (GL40.glGetProgrami(program, GL40.GL_LINK_STATUS) == GL40.GL_FALSE) {
            throw new Exception("Error occurred while linking Program" + program + GL40.glGetProgramInfoLog(program));
        }
    }

    public void use() {
        GL40.glUseProgram(handle);
    }

    public void stop() {
        GL40.glUseProgram(0);
    }

    public int getAttributeLocation(String attribName) {
        return GL40.glGetAttribLocation(handle, attribName);
    }

    public void setInt(String name, int data) {
        GL40.glUseProgram(handle);
        GL40.glUniform1i(uniformLocations.get(name), data);
    }

    public void setFloat(String name, float data) {
        GL40.glUseProgram(handle);
        GL40.glUniform1f(uniformLocations.get(name), data);
    }

    public void setMatrix4(String name, Matrix4f matrix) {
        FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);
        matrix.get(matrixBuffer);
        GL40.glUseProgram(handle);
        GL40.glUniformMatrix4fv(uniformLocations.get(name), false, matrixBuffer);
    }

    public void setVector3(String name, Vector3f vector) {
        FloatBuffer vectorBuffer = MemoryUtil.memAllocFloat(3);
        vector.get(vectorBuffer);
        GL40.glUseProgram(handle);
        GL40.glUniform3fv(uniformLocations.get(name), vectorBuffer);
    }

    public void setUniform(String name, int value) {
        int location = uniformLocations.get(name);
        GL40.glUniform1i(location, value);
    }

    public void setUniform(String name, float value) {
        int location = uniformLocations.get(name);
        GL40.glUniform1f(location, value);
    }

    public void setUniform(String name, Vector3f value) {
        int location = uniformLocations.get(name);
        GL40.glUniform3f(location, value.x, value.y, value.z);
    }

    public void setUniform(String name, Matrix4f value) {
        int location = uniformLocations.get(name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);
        GL40.glUniformMatrix4fv(location, false, buffer);
    }
}
