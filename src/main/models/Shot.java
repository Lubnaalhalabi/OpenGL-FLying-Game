package main.models;

import main.common.Input;
import main.common.Texture;
import main.gameengine.GameEngine;
import main.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL40;

/**
 *
 * @author Humam Tlay and Lubna Alhalabi
 */
public class Shot extends Model {

    private String imageFile;
    private float moveSpeed;
    private Vector3f firstPoint;
    private Vector3f secondPoint;
    private Vector3f rotation;
    private Vector3f scale;

    public float t;

    public Shot(OBJModel obj, float moveSpeed, Vector3f p1, Vector3f p2, Vector3f rotation, Vector3f scale) {
        super.setModelName("square");
        super.setVertices(obj.getVertices());
        super.setIndices(obj.getIndices());
        this.imageFile = obj.imageFile;

        super.setStride(5);
        super.setVertexCount(obj.getVertexCount() / 5);

        this.moveSpeed = moveSpeed;
        this.firstPoint = p1;
        this.secondPoint = p2;
        this.t = 2;
        this.rotation = rotation;
        this.scale = scale;
    }

    @Override
    public void load() throws Exception {
        super.load();

        int vertexAttributeLocation = super.getShader().getAttributeLocation("aPosition");
        glEnableVertexAttribArray(vertexAttributeLocation);
        glVertexAttribPointer(vertexAttributeLocation, 3, GL_FLOAT, false, super.getStride() * Float.BYTES, 0);

        var texCoordLocation = super.getShader().getAttributeLocation("aTexCoord");
        glEnableVertexAttribArray(texCoordLocation);
        glVertexAttribPointer(texCoordLocation, 2, GL_FLOAT, false, super.getStride() * Float.BYTES, 3 * Float.BYTES);

        super.setTexture(Texture.loadFromFile(this.imageFile));

        super.increaseRotation(this.rotation.x,this.rotation.y,this.rotation.z);
        super.setScale(new Vector3f(this.scale.x, this.scale.y, this.scale.z));
        super.increasePosition(secondPoint.x, secondPoint.y, secondPoint.z);
    }

    @Override
    public void render() {
        updatePoition();
        super.render();
        super.getTexture().use(GL40.GL_TEXTURE0);
        super.getShader().setMatrix4("model", super.getTransformation());
        super.getShader().setMatrix4("projection", Utils.createProjectionMatrix(super.getFOV(), super.getNEAR_PLANE(), super.getFAR_PLANE()));
        super.getShader().setMatrix4("view", Utils.createViewMatrix(GameEngine.getCamera()));
//        glDrawElements(GL_TRIANGLES, super.getIndices().length, GL_UNSIGNED_INT, 0);
        glDrawElements(GL20.GL_TRIANGLES, super.getIndices().length, GL_UNSIGNED_INT, 0);
    }

    public void updatePoition(){
        t += moveSpeed;

        float x = firstPoint.x + t * (secondPoint.x - firstPoint.x);
        float y = firstPoint.y + t * (secondPoint.y - firstPoint.y);
        float z = firstPoint.z + t * (secondPoint.z - firstPoint.z);
        Vector3f position = getPosition();

        increasePosition(x - position.x, y - position.y, z - position.z);
    }
}
