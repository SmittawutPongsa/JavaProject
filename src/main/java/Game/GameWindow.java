package Game;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

public class GameWindow extends Main{

    private long window;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        // Setup an error callback. The default implementation will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. You must do this before most other GLFW functions.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // Optional, but recommended: the current window hints are already the default.
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // The window will stay hidden after creation.
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // The window will be resizable.

        // Create the window
        window = glfwCreateWindow(800, 600, "Simple Game Window", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW window.");
        }

        // Center the window on the screen
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidMode.width() - 800) / 2, (vidMode.height() - 600) / 2);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
        GL.createCapabilities();

        // Set the clear color (black in this case)
        org.lwjgl.opengl.GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            // Poll for events and handle the window close button
            glfwPollEvents();

            // Clear the framebuffer
            org.lwjgl.opengl.GL11.glClear(org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT);

            // Render 2D elements
            org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_QUADS);
            org.lwjgl.opengl.GL11.glColor3f(1.0f, 0.0f, 0.0f); // Set color to red
            org.lwjgl.opengl.GL11.glVertex2f(100, 100); // Top-left vertex
            org.lwjgl.opengl.GL11.glVertex2f(200, 100); // Top-right vertex
            org.lwjgl.opengl.GL11.glVertex2f(200, 200); // Bottom-right vertex
            org.lwjgl.opengl.GL11.glVertex2f(100, 200); // Bottom-left vertex
            org.lwjgl.opengl.GL11.glEnd();

            // Swap the color buffer
            glfwSwapBuffers(window);
        }
    }

    private void cleanup() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }


}
