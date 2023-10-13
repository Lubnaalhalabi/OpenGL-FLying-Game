package main.models;

import main.common.Texture;
import main.gameengine.GameEngine;
import main.utils.Utils;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL40;

public class Fire extends Model {
    public long startTime;
    public Fire(long time) {
        super.setModelName("fire");
        this.startTime = time;

        super.setVertices(new float[] {
                0.0f,  0.5f, -1.0f, 1.0f, 0.0f, 0.2f, 1.0f, // Vertex 0
                -0.03f, 0.05f, -0.2f, 1.0f, 0.5f, 0.0f, 1.0f, // Vertex 1
                0.03f,  0.05f,  -0.2f, 1.0f, 0.5f, 0.0f, 1.0f,  // Vertex 2

                -0.5f,  0.0f, -1.0f, 1.0f, 0.0f, 0.2f, 1.0f, // Vertex 3
                -0.03f, -0.05f, -0.2f, 1.0f, 0.5f, 0.0f, 1.0f, // Vertex 4


                0.0f, -0.5f, -1.0f, 1.0f, 0.0f, 0.2f, 1.0f , // Vertex 5
                0.03f,  -0.05f, -0.2f, 1.0f, 0.5f, 0.0f, 1.0f, // Vertex 6

                0.5f, 0.0f, -1.0f, 1.0f, 0.0f, 0.2f, 1.0f, // Vertex 7
        });

        super.setIndices(new int[] {
                0, 1, 2,
                1, 3, 4,
                4, 5, 6,
                2, 6, 7,
                1, 4, 2,
                2, 4, 6
        });

        super.setStride(7);
        super.setVertexCount(7);
    }

    @Override
    public void load() throws Exception {
        super.load();
        int vertexAttributeLocation = super.getShader().getAttributeLocation("aPosition");
        glEnableVertexAttribArray(vertexAttributeLocation);
        glVertexAttribPointer(vertexAttributeLocation, 3, GL_FLOAT, false, super.getStride() * Float.BYTES, 0);

        int colorAttributeLocation = super.getShader().getAttributeLocation("aColor");
        glEnableVertexAttribArray(colorAttributeLocation);
        glVertexAttribPointer(colorAttributeLocation, 4, GL_FLOAT, false, super.getStride() * Float.BYTES, 3 * Float.BYTES);

//        super.increaseRotation (0.1f,0.4f,0.5f);
//        super.setScale(new Vector3f(0.1f,0.4f,0.5f));
//        super.increasePosition(0.1f,0.4f,0.5f);


    }

    public void render() {
        super.render();

//        glDrawElements(GL_TRIANGLES, super.getIndices().length, GL_UNSIGNED_INT, 0);
        super.getShader().setMatrix4("model", super.getTransformation());
        super.getShader().setMatrix4("projection", Utils.createProjectionMatrix(super.getFOV(), super.getNEAR_PLANE(), super.getFAR_PLANE()));
        super.getShader().setMatrix4("view", Utils.createViewMatrix(GameEngine.getCamera()));
        glDrawElements(GL_TRIANGLES, super.getIndices().length, GL_UNSIGNED_INT, 0);
    }

}
