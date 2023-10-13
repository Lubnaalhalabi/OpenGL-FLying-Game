package main.common;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import main.models.Model;
import main.models.OBJModel;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 *
 * @author Ali Khaddour
 */
public class OBJLoader {
    public static OBJModel loadObjModel(String fileName, String imageName) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<Vector3f> vertices = new ArrayList<Vector3f>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Integer> indices = new ArrayList<Integer>();
        
        float[] vArray = null;
        ArrayList<Float> nArray = new ArrayList<Float>(), tArray = new ArrayList<Float>();
        int[] indicesArray = null;
        try (InputStream stream = OBJLoader.class.getClassLoader().getResourceAsStream("resources/objs/" + fileName);
             java.util.Scanner scanner = new java.util.Scanner(stream))
        {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                line = line.replace("  ", " ");
                String[] parts = line.split(" ");
                if(line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
                    vertices.add(vertex);
                } else if(line.startsWith("vt ")) {
                    Vector2f texture = new Vector2f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
                    textures.add(texture);
                } else if(line.startsWith("vn ")) {
                    // to be implemented later
                } else if(line.startsWith("f ")) {
                    String[] vertex1 = parts[1].split("/");
                    String[] vertex2 = parts[2].split("/");
                    String[] vertex3 = parts[3].split("/");

                    prepareVertex(vertex1, tArray, nArray, indices, textures, normals);
                    prepareVertex(vertex2, tArray, nArray, indices, textures, normals);
                    prepareVertex(vertex3, tArray, nArray, indices, textures, normals);
                    if (parts.length>4) {
                        String[] vertex4 = parts[4].split("/");
                        prepareVertex(vertex1, tArray, nArray, indices, textures, normals);
                        prepareVertex(vertex3, tArray, nArray, indices, textures, normals);
                        prepareVertex(vertex4, tArray, nArray, indices, textures, normals);
                    }
                }
            }
        } catch (IOException ex) {
            throw ex;
        }
        vArray = new float[indices.size() * 5];
        indicesArray = new int[indices.size()];
        
        int tmp = 0;
        for(int i = 0; i < indices.size(); i ++) {
            vArray[tmp ++] = vertices.get(indices.get(i)).x;
            vArray[tmp ++] = vertices.get(indices.get(i)).y;
            vArray[tmp ++] = vertices.get(indices.get(i)).z;
            vArray[tmp ++] = tArray.get(i * 2);
            vArray[tmp ++] = tArray.get(i * 2 + 1);
        }
        
        
        
        for(int i = 0; i < indices.size(); i ++) {
            indicesArray[i] = i;
        }
        
        return new OBJModel(vArray, indicesArray, imageName);
    }
    
    private static void prepareVertex(String[] vArray, ArrayList<Float> tArray, ArrayList<Float> nArray, List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals)  {
        int currentVertexIndex = Integer.parseInt(vArray[0]) - 1;
        indices.add(currentVertexIndex);
        Vector2f currentTexture = textures.get(Integer.parseInt(vArray[1]) - 1);
        tArray.add(currentTexture.x);
        tArray.add(currentTexture.y);
    }
            
            
            
            
            
}
