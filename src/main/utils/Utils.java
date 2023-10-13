package main.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import main.common.Camera;
import main.renderengine.WindowManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Ali Khaddour
 */
public class Utils {
    public static String readShaderFile(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStream stream = Utils.class.getClassLoader().getResourceAsStream("resources/shaders/" + fileName);
             java.util.Scanner scanner = new java.util.Scanner(stream))
        {
            while (scanner.hasNext()) {
                sb.append(scanner.nextLine());
                sb.append("\n");
            }
        } catch (IOException ex) {
            throw ex;
        }
        return sb.toString();
    }
    
    public static FloatBuffer writeDataToFloatBuffer(float[] data) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length);
        floatBuffer.put(data);
        floatBuffer.flip();
        return floatBuffer;
    }
    
    public static IntBuffer writeDataToIntBuffer(int[] data) {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(data.length);
        intBuffer.put(data);
        intBuffer.flip();
        return intBuffer;
    }
    
    public static ByteBuffer writeDataToByteBuffer(byte[] data) {
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(data.length);
        byteBuffer.put(data);
        byteBuffer.flip();
        return byteBuffer;
    }
    
    public static String getTexturePath(String fileName) throws IOException {
        try {
            URL resource = Utils.class.getClassLoader().getResource("resources/textures/" + fileName);
            return new File(resource.toURI()).getAbsolutePath();
        }
        catch(Exception ex) {
            throw new IOException(ex.getMessage(), ex);
        }
    }

    public static Matrix4f createProjectionMatrix(float fov, float nearPlane, float farPlane) {
        float aspectRatio = (float) WindowManager.getWIDTH() / (float) WindowManager.getHEIGHT();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = farPlane - nearPlane;

        Matrix4f projectionMatrix = new Matrix4f().identity();
        projectionMatrix.set(0, 0, x_scale);
        projectionMatrix.set(1, 1, y_scale);
        projectionMatrix.set(2, 2, -((farPlane + nearPlane) / frustum_length));
        projectionMatrix.set(2, 3, -1);
        projectionMatrix.set(3, 2, -((2 * nearPlane * farPlane) / frustum_length));
        projectionMatrix.set(3, 3, 0);
        return projectionMatrix;
    }
    
    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0));
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        viewMatrix.translate(negativeCameraPos);
        return viewMatrix;
    }

    public static BufferedImage loadHeightMap(String heatmappng) throws IOException {
        String path = getTexturePath(heatmappng);
        BufferedImage heightmapImage = ImageIO.read(new File(path));
        return heightmapImage;
    }
}
