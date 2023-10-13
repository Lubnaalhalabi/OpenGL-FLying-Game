package main.models;

import main.common.Texture;
import main.gameengine.GameEngine;
import main.utils.Utils;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import org.lwjgl.opengl.GL40;

/**
 *
 * @author Ali Khaddour
 */
public class OBJModel extends Model {
    public String imageFile;
    public OBJModel(float[] vertices, int[] indices, String imageFile) {
        super.setModelName("square");
        this.imageFile = imageFile;
        super.setVertices(vertices);
        super.setIndices(indices);
        
        super.setStride(5);
        super.setVertexCount(vertices.length / 5);
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
    }
    
    @Override
    public void render() {
        super.render();

        super.getTexture().use(GL40.GL_TEXTURE0);
        super.getShader().setMatrix4("model", super.getTransformation());
        super.getShader().setMatrix4("projection", Utils.createProjectionMatrix(super.getFOV(), super.getNEAR_PLANE(), super.getFAR_PLANE()));
        super.getShader().setMatrix4("view", Utils.createViewMatrix(GameEngine.getCamera()));
        glDrawElements(GL20.GL_TRIANGLES, super.getIndices().length, GL_UNSIGNED_INT, 0);
    }
}
