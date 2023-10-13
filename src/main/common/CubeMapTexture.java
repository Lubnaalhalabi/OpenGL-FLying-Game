package main.common;

import java.io.IOException;
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
public class CubeMapTexture extends Texture {

    public static CubeMapTexture loadFromFile(String[] cubeMapFaces) throws IOException {
        // Generate handle
        int handle = GL40.glGenTextures();

        // Bind the handle
        GL40.glActiveTexture(GL40.GL_TEXTURE0);
        GL40.glBindTexture(GL40.GL_TEXTURE_CUBE_MAP, handle);
        // important - in cube maps, no need to flip the images
        stbi_set_flip_vertically_on_load(false);

        for (int i = 0; i < 6; i++) {
            int[] w = new int[1], h = new int[1], nrChannels = new int[1];
            String path = Utils.getTexturePath(cubeMapFaces[i]);

            ByteBuffer image = stbi_load(path, w, h, nrChannels, 0);

            if (image == null) {
                throw new RuntimeException("Failed to load a texture file!"
                        + System.lineSeparator() + stbi_failure_reason());
            }

            GL40.glTexImage2D(GL40.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL40.GL_RGB, w[0], h[0], 0, GL40.GL_RGB, GL40.GL_UNSIGNED_BYTE, image);
        }

        GL40.glTexParameteri(GL40.GL_TEXTURE_CUBE_MAP, GL40.GL_TEXTURE_MIN_FILTER, (int) GL40.GL_LINEAR);
        GL40.glTexParameteri(GL40.GL_TEXTURE_CUBE_MAP, GL40.GL_TEXTURE_MAG_FILTER, (int) GL40.GL_LINEAR);
        GL40.glTexParameteri(GL40.GL_TEXTURE_CUBE_MAP, GL40.GL_TEXTURE_WRAP_S, (int) GL40.GL_CLAMP_TO_EDGE);
        GL40.glTexParameteri(GL40.GL_TEXTURE_CUBE_MAP, GL40.GL_TEXTURE_WRAP_T, (int) GL40.GL_CLAMP_TO_EDGE);
        GL40.glTexParameteri(GL40.GL_TEXTURE_CUBE_MAP, GL40.GL_TEXTURE_WRAP_R, (int) GL40.GL_CLAMP_TO_EDGE);

        //GL.GenerateMipmap(GenerateMipmapTarget.TextureCubeMap);
        return new CubeMapTexture(handle);
    }

    public CubeMapTexture(int glHandle) {
        super(glHandle);
    }

    // Activate texture
    // Multiple textures can be bound, if your shader needs more than just one.
    // If you want to do that, use GL.ActiveTexture to set which slot GL.BindTexture binds to.
    // The OpenGL standard requires that there be at least 16, but there can be more depending on your graphics card.
    public void use(int unit) {
        GL40.glActiveTexture(unit);
        GL40.glBindTexture(GL40.GL_TEXTURE_CUBE_MAP, this.handle);
    }

}
