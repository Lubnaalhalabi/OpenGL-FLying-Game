package main.renderengine;

import main.common.Input;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import static org.lwjgl.opengl.GL40.*;

/**
 *
 * @author Ali Khaddour
 */
public class WindowManager {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1050;
    private static final String TITLE = "Game Engine";
    private static long window;
    private static Input input;
    private static long lastFrameTime;
    private static float delta;

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }
    
    public static void createWindow() {
        if(!GLFW.glfwInit()) {
            System.err.println("GLFW is not initialized!");
            return;
        }
        
        // fixed size
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
        // do not show the window until everything is set
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        // version 1
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 1);
        // version 1.0
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);
        
        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, TITLE, 0, 0);
        
        if(window == 0) {
            System.err.println("Window is not created!");
            return;
        }
        // set window position
        // get the monitor
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(window, (vidMode.width() - WIDTH) / 2, (vidMode.height() - HEIGHT) / 2);
        // tell what windw to edit
        GLFW.glfwMakeContextCurrent(window);
        
        input = new Input();
        GLFW.glfwSetKeyCallback(window, input.getKeyboardCallback());
        GLFW.glfwSetCursorPosCallback(window, input.getMouseMoveCallback());
        GLFW.glfwSetMouseButtonCallback(window, input.getMouseButtonCallback());
        
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
        GL.createCapabilities();
        GLUtil.setupDebugMessageCallback();
        
        GLFW.glfwShowWindow(window);
        // vsync
        // how many screen updates should the buffer swapping wait for
        GLFW.glfwSwapInterval(1);
        
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        
        lastFrameTime = System.currentTimeMillis();
    }
    
    public static void updateWindow() {
        GLFW.glfwPollEvents();
        GLFW.glfwSwapBuffers(window);
        long currentFrameTime = System.currentTimeMillis();
        delta = (currentFrameTime - lastFrameTime) / 1000.0f;
        lastFrameTime = currentFrameTime;
    }
    
    public static void closeWindow() {
        GLFW.glfwDestroyWindow(window);
    }
    
    public static boolean shouldClose() {
        if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            return true;
        }
        return GLFW.glfwWindowShouldClose(window);
    }
    
    public static float getDelta() {
        return delta;
    }
}
