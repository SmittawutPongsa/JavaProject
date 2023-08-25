package Game;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.*;

public class GameWindow extends Main{

//    private long window;

    public void run() {
        init();
//        loop();
//        cleanup();
    }

    private void init() {
//        // Set up an error callback. The default implementation will print the error message in System.err.
//        GLFWErrorCallback.createPrint(System.err).set();
//
//        // Initialize GLFW. You must do this before most other GLFW functions.
//        if (!glfwInit()) {
//            throw new IllegalStateException("Unable to initialize GLFW.");
//        }
//
//        // Configure GLFW
//        glfwDefaultWindowHints(); // Optional, but recommended: the current window hints are already the default.
//        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // The window will stay hidden after creation.
//        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // The window will be resizable.
//
//        // Create the window
//        window = glfwCreateWindow(800, 600, "Simple Game Window", 0, 0);
//        if (window == 0) {
//            throw new RuntimeException("Failed to create the GLFW window.");
//        }
//
//        // Center the window on the screen
//        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//        glfwSetWindowPos(window, (vidMode.width() - 800) / 2, (vidMode.height() - 600) / 2);
//
//        // Make the OpenGL context current
//        glfwMakeContextCurrent(window);
//
//        // Enable v-sync
//        glfwSwapInterval(1);
//
//        // Make the window visible
//        glfwShowWindow(window);
//
//        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed externally.
//        // LWJGL detects the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
//        GL.createCapabilities();
//
//        // Set the clear color (black in this case)
//        org.lwjgl.opengl.GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Create a window
        long window = glfwCreateWindow(800, 600, "Game Window", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Set up error callback and make OpenGL context current
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        // Initialize LWJGL's OpenGL bindings
        GL.createCapabilities();

        // Load texture
        int textureId = TextureLoader.loadTexture("C:\\Users\\aakas\\Downloads\\assets\\Project\\Project\\src\\main\\resources\\c4.jpg");

        // Main loop
        while (!glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear the framebuffer

            // Render the texture
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

            // Set up vertex positions and texture coordinates
            float[] vertices = {
                    -0.5f, -0.5f, 0, 0,
                    0.5f, -0.5f, 1, 0,
                    0.5f, 0.5f, 1, 1,
                    -0.5f, 0.5f, 0, 1 };
            glBindBuffer(GL_ARRAY_BUFFER, glGenBuffers());
            glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(vertices.length).put(vertices).flip(), GL_STATIC_DRAW);


            // Draw textured quad using OpenGL commands
//            glBegin(GL_QUADS);
//            glTexCoord2f(0,0);
//            glVertex2f(-0.5f, 0.5f); // Top-left vertex
//            glTexCoord2f(0,1);
//            glVertex2f(0.5f, 0.5f); // Top-right vertex
//            glTexCoord2f(1,1);
//            glVertex2f(0.5f, -0.5f); // Bottom-right vertex
//            glTexCoord2f(1,0);
//            glVertex2f(-0.5f, -0.5f); // Bottom-left vertex
//            glEnd();

            glfwSwapBuffers(window); // Swap the color buffers
            glfwPollEvents(); // Poll for and process events
        }

        // Clean up
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }


//    private void loop() {
//        while (!glfwWindowShouldClose(window)) {
//            // Poll for events and handle the window close button
//            glfwPollEvents();
//            // Clear the framebuffer to black color
//            glClear(GL_COLOR_BUFFER_BIT);
//            // Render 2D elements
//            glBegin(GL_QUADS);
////            glColor3f(1.0f, 0.0f, 0.0f); // Set color to red
//            glTexCoord2f(0,0);
//            glVertex2f(-0.5f, 0.5f); // Top-left vertex
//            glTexCoord2f(0,1);
//            glVertex2f(0.5f, 0.5f); // Top-right vertex
//            glTexCoord2f(1,1);
//            glVertex2f(0.5f, -0.5f); // Bottom-right vertex
//            glTexCoord2f(1,0);
//            glVertex2f(-0.5f, -0.5f); // Bottom-left vertex
//            glEnd();
//            // Swap the color buffer
//            glfwSwapBuffers(window);
//        }
//    }

//    private void cleanup() {
//        // Free the window callbacks and destroy the window
//        glfwFreeCallbacks(window);
//        glfwDestroyWindow(window);
//
//        // Terminate GLFW and free the error callback
//        glfwTerminate();
//        glfwSetErrorCallback(null).free();
//    }
}
