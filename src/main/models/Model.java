package main.models;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import main.common.Shader;
import main.common.Texture;
import main.renderengine.WindowManager;
import main.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL40.*;

/**
 *
 * @author Humam Tlay
 */
public class Model {

    private String modelName;
    private float[] vertices;
    private int[] indices;
    private int vertexCount;
    private int stride;
    private int vaoHandle;
    private int vboHandle;
    private int eboHandle;
    private Shader shader;
    private Texture texture;
    private Vector3f position;
    private Vector3f rotation;
    private Matrix4f transformation;
    private  Vector3f scale;
    private float High;
    private static final float FOV = 70, NEAR_PLANE = 0.1f, FAR_PLANE = 1000f;

    public Model() {
        //
        transformation = new Matrix4f();

        scale = new Vector3f(1,1,1);
        rotation = new Vector3f(0,0,0);
        position = new Vector3f(0,0,0);
    }

    public void load() throws Exception {
        vaoHandle = glGenVertexArrays();
        glBindVertexArray(vaoHandle);

        vboHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
        FloatBuffer floatBuffer = Utils.writeDataToFloatBuffer(vertices);
        // pass the data to the graphics card
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);

        int eboHandle = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboHandle);
        IntBuffer intBuffer = Utils.writeDataToIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL_STATIC_DRAW);

        shader = new Shader(modelName + ".vert", modelName + ".frag");
        
        this.position = new Vector3f();
        this.rotation = new Vector3f();


    }

    public void render() {
        /* fix position */
        float cur = this.High * this.getScale().y / 2.0f;

        this.setTransformation(
                new Matrix4f()
                        .translate(this.getPosition())
                        .rotate(this.getRotation().y, new Vector3f(0, 1, 0))
                        .rotate(this.getRotation().z, new Vector3f(0, 0, 1))
                        .rotate(this.getRotation().x, new Vector3f(1, 0, 0))
                        .scale(this.getScale())
                        .translate(0.0f, 0.0f, 0.0f)
        );

        glBindVertexArray(vaoHandle);
        shader.use();
    }

    public void distroy() {
        glDeleteVertexArrays(vaoHandle);
        glDeleteBuffers(vboHandle);
        glDeleteBuffers(eboHandle);
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public float[] getVertices() {
        return vertices;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public int getStride() {
        return stride;
    }

    public void setStride(int stride) {
        this.stride = stride;
    }

    public int getVaoHandle() {
        return vaoHandle;
    }

    public void setVaoHandle(int vaoHandle) {
        this.vaoHandle = vaoHandle;
    }

    public int getVboHandle() {
        return vboHandle;
    }

    public void setVboHandle(int vboHandle) {
        this.vboHandle = vboHandle;
    }

    public int getEboHandle() {
        return eboHandle;
    }

    public void setEboHandle(int eboHandle) {
        this.eboHandle = eboHandle;
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Matrix4f getTransformation() {
        return transformation;
    }

    public void setTransformation(Matrix4f transformation) {
        this.transformation = transformation;
    }

    public static float getFOV() {
        return FOV;
    }

    public static float getNEAR_PLANE() {
        return NEAR_PLANE;
    }

    public static float getFAR_PLANE() {
        return FAR_PLANE;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f s) {
        scale = s;
    }

    public void setHigh(float h) {
        this.High = h;
    }
    
    public void increasePosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }
    
    public void increaseRotation(float dx, float dy, float dz) {
        this.rotation.x += dx;
        this.rotation.y += dy;
        this.rotation.z += dz;
    }
    
}
