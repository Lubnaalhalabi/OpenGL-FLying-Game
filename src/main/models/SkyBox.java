package main.models;

import main.common.CubeMapTexture;
import main.common.Texture;
import main.gameengine.GameEngine;
import main.utils.Utils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import org.lwjgl.opengl.GL40;

/**
 *
 * @author Ali Khaddour
 */
public class SkyBox extends Model {

    public SkyBox() {
        super.setModelName("skybox");

        super.setVertices(new float[]{
            //   Coordinates
            -1.0f, -1.0f,  1.0f,//        7--------6
             1.0f, -1.0f,  1.0f,//       /|       /|
             1.0f, -1.0f, -1.0f,//      4--------5 |
            -1.0f, -1.0f, -1.0f,//      | |      | |
            -1.0f,  1.0f,  1.0f,//      | 3------|-2
             1.0f,  1.0f,  1.0f,//      |/       |/
             1.0f,  1.0f, -1.0f,//      0--------1
            -1.0f,  1.0f, -1.0f
        });
        super.setIndices(new int[]{
            // Right
            6, 2, 1,
            1, 5, 6,
            // Left
            7, 4, 0,
            0, 3, 7,
            // Top
            6, 5, 4,
            4, 7, 6,
            // Bottom
            2, 3, 0,
            0, 1, 2,
            // Back
            5, 1, 0,
            0, 4, 5,
            // Front
            6, 7, 3,
            3, 2, 6
        });
        super.setStride(3);
        super.setVertexCount(8);
    }

    @Override
    public void load() throws Exception {
        super.load();

        String[] cubeMapFaces = new String[]{
            "yellowcloud-right.jpg",
            "yellowcloud-left.jpg",
            "yellowcloud-top.jpg",
            "yellowcloud-bottom.jpg",
            "yellowcloud-front.jpg",
            "yellowcloud-back.jpg",};

        int vertexAttributeLocation = super.getShader().getAttributeLocation("aPosition");
        glEnableVertexAttribArray(vertexAttributeLocation);
        glVertexAttribPointer(vertexAttributeLocation, 3, GL_FLOAT, false, super.getStride() * Float.BYTES, 0);

        super.setTexture(CubeMapTexture.loadFromFile(cubeMapFaces));
    }

    @Override
    public void render() {
        super.render();
        ((CubeMapTexture) super.getTexture()).use(GL40.GL_TEXTURE0);
        GL40.glDepthFunc(GL40.GL_LEQUAL);

        Matrix4f view = new Matrix4f(new Matrix3f(Utils.createViewMatrix(GameEngine.getCamera())));
        super.getShader().setMatrix4("projection", Utils.createProjectionMatrix(super.getFOV(), super.getNEAR_PLANE(), super.getFAR_PLANE()));
        super.getShader().setMatrix4("view", view);
        glDrawElements(GL40.GL_TRIANGLES, super.getIndices().length, GL_UNSIGNED_INT, 0);

        GL40.glDepthFunc(GL40.GL_LESS);
    }

}
