package main.models;

import main.common.Texture;
import main.gameengine.GameEngine;
import main.utils.Utils;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL40;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

/**
 *
 * @author Humam Tlay and Lubna Alhalabi
 */
public class Monstor extends Model {

    private String imageFile;
    private float moveSpeed;
    private MainCharacter mainCharacter;
    private  Vector3f startPosition;
    private Vector3f rotation;
    private Vector3f scale;

    public float t;

    public Monstor(OBJModel obj, float moveSpeed, Vector3f startPosition, MainCharacter mainCharacter, Vector3f rotation, Vector3f scale) {
        super.setModelName("square");
        super.setVertices(obj.getVertices());
        super.setIndices(obj.getIndices());
        this.imageFile = obj.imageFile;

        super.setStride(5);
        super.setVertexCount(obj.getVertexCount() / 5);

        this.moveSpeed = moveSpeed;
        this.mainCharacter = mainCharacter;
        this.startPosition = startPosition;

        this.t = 0;
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
        super.increasePosition(startPosition.x, startPosition.y, startPosition.z);
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
        Vector3f position = getPosition();

        float x = position.x + moveSpeed * (mainCharacter.getPosition().x - position.x);
        float y = position.y + moveSpeed * (mainCharacter.getPosition().y - position.y);
        float z = position.z + moveSpeed * (mainCharacter.getPosition().z - position.z);

        increasePosition(x - position.x, y - position.y, z - position.z);
    }
}
