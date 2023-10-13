package main.common;

import main.models.MainCharacter;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Ali Khaddour
 */
public class Camera {

    private Vector3f position = new Vector3f(0, 0, 0);
    private float distanceFromCharacter = -5;
    private float pitch, yaw, roll;
    private Vector3f offset = new Vector3f();
    
    private MainCharacter mainCharacter;

    public Camera(MainCharacter mainCharacter) {
        this.mainCharacter = mainCharacter;
    }

    public Vector3f getPosition() {
        return position;
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

    public void move() {
        mainCharacter.updateMouseInput();
        yaw = mainCharacter.getYaw();
        pitch = mainCharacter.getPitch();

        mainCharacter.updateKeyboardInput();

        if (Input.isKeyDown(GLFW.GLFW_KEY_W)) {
            if(distanceFromCharacter > -10)
                distanceFromCharacter -= 0.05;
        }
        else if (Input.isKeyDown(GLFW.GLFW_KEY_S)) {
            if(distanceFromCharacter < -3.5)
                distanceFromCharacter += 0.15;
        }
        else{
            if(distanceFromCharacter < -5)
                distanceFromCharacter += 0.15;
            else if(distanceFromCharacter > -5)
                distanceFromCharacter -= 0.15;
        }

        float horizontalDistanceFromCharachter = (float) (distanceFromCharacter * Math.cos((float) Math.toRadians(-pitch)));
        float verticalDistanceFromCharachter = (float) (distanceFromCharacter * Math.sin((float) Math.toRadians(-pitch)));

        offset.x = (float) (horizontalDistanceFromCharachter * Math.sin((float) Math.toRadians(-yaw)));
        offset.z = (float) (horizontalDistanceFromCharachter * Math.cos((float) Math.toRadians(-yaw)));

        position.x = mainCharacter.getPosition().x - offset.x;
        position.y = mainCharacter.getPosition().y + verticalDistanceFromCharachter;
        position.z = mainCharacter.getPosition().z - offset.z;
    }

}
