package main.models;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.common.Texture;
import main.gameengine.GameEngine;
import main.utils.Utils;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
public class Terrain extends Model {

    private String heightmapPath;
    private int width;
    private int height;
    private float[] vertices;
    private int[] indices;

    public Terrain(String modelName, String heightmapPath) {
        try {
            this.setModelName(modelName);
            this.heightmapPath = heightmapPath;
            this.generateTerrain();
            this.setVertexCount(vertices.length / 5);
            this.setStride(5);
        } catch (IOException ex) {
            Logger.getLogger(Terrain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(Terrain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateTerrain() throws IOException, URISyntaxException {
        BufferedImage heightmapImage = Utils.loadHeightMap(heightmapPath);

        width = heightmapImage.getWidth();
        height = heightmapImage.getHeight();
        vertices = new float[width * height * 5];
        indices = new int[(width - 1) * (height - 1) * 6];

        int vertexIndex = 0;
        int index = 0;
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                // Calculate vertex position
                float y = (float)(heightmapImage.getRGB(x, z) & 0xFF) / 255.0f - 0.5f;
                vertices[vertexIndex++] = (x - width / 2) * 10;
                vertices[vertexIndex++] = y * 100;
                vertices[vertexIndex++] = (z - height / 2) * 10;
                vertices[vertexIndex++] = (float)x / (float)width;
                vertices[vertexIndex++] = (float)z / (float)height;

                // Create indices
                if (x > 0 && z > 0) {
                    int topLeft = (z - 1) * width + (x - 1);
                    int topRight = (z - 1) * width + x;
                    int bottomLeft = z * width + (x - 1);
                    int bottomRight = z * width + x;
                    indices[index++] = topLeft;
                    indices[index++] = bottomLeft;
                    indices[index++] = topRight;
                    indices[index++] = topRight;
                    indices[index++] = bottomLeft;
                    indices[index++] = bottomRight;
                }
            }
        }

        this.setVertices(vertices);
        this.setIndices(indices);

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
        
        super.setTexture(Texture.loadFromFile("grass.png"));
    }

    @Override
    public void render() {
        super.render();
        
        super.getTexture().use(0);

        super.getShader().setMatrix4("model", super.getTransformation());
        super.getShader().setMatrix4("projection", Utils.createProjectionMatrix(super.getFOV(), super.getNEAR_PLANE(), super.getFAR_PLANE()));
        super.getShader().setMatrix4("view", Utils.createViewMatrix(GameEngine.getCamera()));
        
        glDrawElements(GL_TRIANGLES, super.getIndices().length, GL_UNSIGNED_INT, 0);
        
    }
}