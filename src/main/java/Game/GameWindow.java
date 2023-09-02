package Game;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.lwjgl.stb.*;

import java.nio.*;

public class GameWindow extends Main {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String BACKGROUND_IMAGE_PATH = "src/assets/b2.png";

    private int backgroundTexture;
    private long window;


    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        System.out.println("Image Path: " + BACKGROUND_IMAGE_PATH);

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "Game Window", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(
                window,
                (vidMode.width() - WIDTH) / 2,
                (vidMode.height() - HEIGHT) / 2
        );

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);

        GL.createCapabilities();

        try {
            // Load the image
            backgroundTexture = loadTexture(BACKGROUND_IMAGE_PATH);
            System.out.println("Image Texture: " + backgroundTexture);

        } catch (Exception e) {
            e.printStackTrace();
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // White clear color

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, WIDTH, HEIGHT, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GLFW.glfwShowWindow(window);
    }

    private int loadTexture(String path) {
        int textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        ByteBuffer image;
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer compBuffer = BufferUtils.createIntBuffer(1);

        image = STBImage.stbi_load(path, widthBuffer, heightBuffer, compBuffer, 4);

        if (image == null) {
            throw new RuntimeException("Failed to load a texture file: " + path);
        }

        int width = widthBuffer.get(0);
        int height = heightBuffer.get(0);
        System.out.println("Image Width: " + width);
        System.out.println("Image Height: " + height);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
        STBImage.stbi_image_free(image);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        int errorCode = GL11.glGetError();
        if (errorCode != GL11.GL_NO_ERROR) {
            System.err.println("OpenGL Error Code (Texture Loading): " + errorCode);
        }

        return textureID;
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, backgroundTexture);
            GL11.glBegin(GL11.GL_QUADS);

            // Specify texture coordinates for each vertex
            GL11.glTexCoord2f(0, 0); // Bottom-left
            GL11.glVertex2f(0, 0);

            GL11.glTexCoord2f(1, 0); // Bottom-right
            GL11.glVertex2f(WIDTH, 0);

            GL11.glTexCoord2f(1, 1); // Top-right
            GL11.glVertex2f(WIDTH, HEIGHT);

            GL11.glTexCoord2f(0, 1); // Top-left
            GL11.glVertex2f(0, HEIGHT);

            GL11.glEnd();

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void cleanup() {
        GL11.glDeleteTextures(backgroundTexture);

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

}
