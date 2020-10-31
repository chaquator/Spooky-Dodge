package BulletSurvive;

import java.lang.Math;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.joml.*;

import java.nio.*;

// import org.lwjgl.openal.AL;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class BulletSurvive {
	// Singletons :(
	private static BulletSurvive instance = null;

	// The window handle
	private long window;

	private Shader base_shader;
	private Level level;

	// Timers
	private Timer game_timer = new Timer();
	private Timer render_timer = new Timer();

	// Callback
	private KeyCallBack kcb = new KeyCallBack();

	/**
	 * Dimensions vector -- window dimensions
	 */
	private final Vector2f dimensions = new Vector2f(960, 720);
	/**
	 * Pixel matrix -- transforms from pixel-space to opengl-space
	 */
	private Matrix4f pixelMatrix;

	/**
	 * Singleton instance
	 *
	 * @return instance
	 */
	public static BulletSurvive getInstance() {
		if (instance == null) instance = new BulletSurvive();
		return instance;
	}

	/**
	 * Dimensions of the game window
	 *
	 * @return dimensions vector
	 */
	public Vector2f getDimensions() {
		return dimensions;
	}

	// Returns matrix which scales down from coordiante units to pixel units (1.0 pixel unit is 1/dimension opengl units)

	/**
	 * Gets pixel matrix, a matrix which transforms from pixel-space to opengl-space by dividing each component by the
	 * game's window dimensions
	 *
	 * @return Homogenous pixel matrix
	 */
	public Matrix4f getPixelMatrix() {
		return pixelMatrix;
	}

	public Shader getBaseShader() {
		return this.base_shader;
	}

	private BulletSurvive() {
		super();
	}

	public void run() {
		init();

		pixelMatrix = new Matrix4f().scale(1 / getDimensions().x, 1 / getDimensions().y, 1);

		loop();

		cleanup();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow((int) dimensions.x, (int) dimensions.y, "Bullet Survive", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, kcb);

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		}

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);

		// V-sync -- disabled for now
		glfwSwapInterval(0);
		glfwWindowHint(GLFW_DOUBLEBUFFER, GL_FALSE);

		// Make the window visible
		glfwShowWindow(window);

		// Enable GL
		GL.createCapabilities();
		Utils.checkGlErrors();

		// Set the clear color
		glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
		Utils.checkGlErrors();

		// Alpha blending
		// Assets will be non-premultiplied alpha blended using one-minus-source-alpha function
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// Create the basic shader
		this.base_shader = new Shader("assets/shaders/base.vert", "assets/shaders/base.frag");

		// Open AL initialization here, something something ALC.create() and get device null and idk

	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		glfwSwapBuffers(window); // swap the color buffers
	}

	private void gameUpdate(float dt) {
		double t = game_timer.getTime() * 2 * Math.PI;
		float radius = 256;
	}

	private void loop() {

		game_timer.init();
		render_timer.init();

		// Update inputs at 240 Hz
		float game_elapsed;
		float game_acc = 0f;
		float game_interval = 1f / 240;
		while (!glfwWindowShouldClose(window)) {
			// Process glfw events
			glfwPollEvents();

			// Update game at fixed rate
			game_elapsed = game_timer.getElapsedTime();
			game_acc += game_elapsed;
			while (game_acc >= game_interval) {
				gameUpdate(game_interval);
				game_acc -= game_interval;
			}

			// Render game
			float render_time = render_timer.getElapsedTime();
			render();

			Utils.checkGlErrors();
		}
	}

	private void cleanup() {
		this.base_shader.close();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public static void main(String[] args) {
		BulletSurvive.getInstance().run();
	}

	static class KeyCallBack implements GLFWKeyCallbackI {
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
				glfwSetWindowShouldClose(window, true);
		}
	}
}