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
public class MainCharacter extends Model {
    
    private boolean isMoving = false;
    private float pitch, yaw, roll;
    
    // Mouse input variables
    private float sensitivity = 0.5f;
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private boolean firstMove = false;
    private String imageFile;
    private float moveSpeed = 3.01f;
    private float backSpeed = 0.5f;
    private float ordinarySpeed = 0.7f;

    public MainCharacter(OBJModel obj) {
        super.setModelName("square");
        super.setVertices(obj.getVertices());
        super.setIndices(obj.getIndices());
        this.imageFile = obj.imageFile;

        super.setStride(5);
        super.setVertexCount(obj.getVertexCount() / 5);
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

        super.increaseRotation(-1.55f, 0.0f, 0.0f);
        super.setScale(new Vector3f(0.08f, 0.08f, 0.08f));
    }
    
    @Override
    public void render() {
        super.render();

        super.getTexture().use(GL40.GL_TEXTURE0);
        super.getShader().setMatrix4("model", super.getTransformation());
        super.getShader().setMatrix4("projection", Utils.createProjectionMatrix(super.getFOV(), super.getNEAR_PLANE(), super.getFAR_PLANE()));
        super.getShader().setMatrix4("view", Utils.createViewMatrix(GameEngine.getCamera()));
//        glDrawElements(GL_TRIANGLES, super.getIndices().length, GL_UNSIGNED_INT, 0);
        glDrawElements(GL20.GL_TRIANGLES, super.getIndices().length, GL_UNSIGNED_INT, 0);
    }
    
    public void updateMouseInput() {
        Double mouseX = Input.getMouseX();
        Double mouseY = Input.getMouseY();

        float deltaX = 0f, deltaY = 0f;
        
        if (mouseX != null && mouseY != null) {
            if(!firstMove) {
                lastMouseX = mouseX;
                lastMouseY = mouseY;
                firstMove = true;
            }
            deltaX = (float) (mouseX - lastMouseX) * sensitivity;
            deltaY = (float) (mouseY - lastMouseY) * sensitivity;
            
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }

        yaw += deltaX;
        pitch += deltaY;

        super.increaseRotation((float) Math.toRadians(-deltaY), (float) Math.toRadians(-deltaX), 0.0f);
    }

    public boolean updateKeyboardInput() {
        Vector3f direction = new Vector3f();
        boolean isLefting = false;

        direction.z -= ordinarySpeed;
        isMoving = true;

        boolean isMovingForwardOrBackward = false;
        if (Input.isKeyDown(GLFW.GLFW_KEY_W)) {
            // Move in the direction the camera is facing
            direction.z -= moveSpeed;
            isMoving = true;
            isMovingForwardOrBackward = true;
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_S)) {
            direction.z += backSpeed;
            isMoving = true;
            isMovingForwardOrBackward = true;
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_D)) {
            if(super.getRotation().z > -1.55)
                super.increaseRotation(0.0f, 0.0f, -0.05f);
            direction.x += moveSpeed;
            isLefting = true;
            isMoving = true;
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_A)) {
            if(super.getRotation().z < 1.55)
                super.increaseRotation(0.0f, 0.0f, 0.05f);
            direction.x -= moveSpeed;
            isLefting = true;
            isMoving = true;
        }
        if (isMoving) {
            direction.rotateX((float) Math.toRadians(-pitch));
            direction.rotateY((float) Math.toRadians(-yaw));
            super.getPosition().add(direction.x, direction.y, direction.z);
        }

        if(isLefting == false){
            if(super.getRotation().z > 0)
                super.increaseRotation(0.0f, 0.0f, -0.05f);
            if(super.getRotation().z < 0)
                super.increaseRotation(0.0f, 0.0f, 0.05f);
        }

        isMoving = false;
        return isMoving;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
    
}
