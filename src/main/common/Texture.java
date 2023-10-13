package main.common;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import main.utils.Utils;
import org.lwjgl.opengl.GL40;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;


/**
 *
 * @author Ali Khaddour
 */
public class Texture {
    
    protected int handle;
    
    public static Texture loadFromFile(String fileName) throws URISyntaxException, IOException {
        int handle = GL40.glGenTextures();
        
        GL40.glActiveTexture(GL40.GL_TEXTURE0);
        GL40.glBindTexture(GL40.GL_TEXTURE_2D, handle);
        
        stbi_set_flip_vertically_on_load(true);
        int[] w = new int[1], h =new int[1], nrChannels = new int[1];
        String path = Utils.getTexturePath(fileName);
        
        ByteBuffer image = stbi_load(path, w, h, nrChannels, 0);
        
        if (image == null) {
            throw new RuntimeException("Failed to load a texture file!"
                    + System.lineSeparator() + stbi_failure_reason());
        }
        
        GL40.glTexImage2D(GL40.GL_TEXTURE_2D, 0, GL40.GL_RGB, w[0], h[0], 0, GL40.GL_RGB, GL40.GL_UNSIGNED_BYTE, image);
             
        GL40.glGenerateMipmap(GL40.GL_TEXTURE_2D);
        
        GL40.glTexParameterIi(GL40.GL_TEXTURE_2D, GL40.GL_TEXTURE_MIN_FILTER, GL40.GL_LINEAR_MIPMAP_LINEAR);
        GL40.glTexParameterIi(GL40.GL_TEXTURE_2D, GL40.GL_TEXTURE_MAG_FILTER, GL40.GL_LINEAR);
        
        GL40.glTexParameterIi(GL40.GL_TEXTURE_2D, GL40.GL_TEXTURE_WRAP_S, GL40.GL_REPEAT);
        GL40.glTexParameterIi(GL40.GL_TEXTURE_2D, GL40.GL_TEXTURE_WRAP_T, GL40.GL_REPEAT);
        
        return new Texture(handle);
    }
    
    public Texture(int glHandle) {
        this.handle = glHandle;
    }
    
    public void use(int textureUnit) {
        GL40.glActiveTexture(textureUnit);
        GL40.glBindTexture(GL40.GL_TEXTURE_2D, handle);
    }
}
