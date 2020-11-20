package BulletSurvive;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.joml.*;

import java.nio.*;
import java.util.HashMap;

// import org.lwjgl.openal.AL;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class BulletSurvive {
	// Singletons :(
	private static BulletSurvive instance = null;

	// The window handle
	private long window;

	private Shader base_shader;
	private ILevel level;

	// Timers
	private final Timer game_timer = new Timer();
	private final Timer render_timer = new Timer();

	// Key input
	private final KeyCallBack kcb = new KeyCallBack();
	private final HashMap<Integer, Boolean> key_map = new HashMap<>();

	/**
	 * Dimensions vector -- window dimensions
	 */
	private final Vector2f dimensions = new Vector2f(960, 720);

	/**
	 * Transformation matrix from pixel-space (1/dimension magnitude) to opengl-space (-1.0 to 1.0 mapping)
	 */
	private Matrix4f pixelMatrix;

	/**
	 * Singleton instance
	 *
	 * @return instance
	 */
	public static BulletSurvive gameInstance() {
		if (instance == null) instance = new BulletSurvive();
		return instance;
	}

	public static ILevel getLevel() {
		return gameInstance().level;
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

	/**
	 * Query game instance for current key state
	 *
	 * @param key GLFW Key
	 * @return true if key is down, false if up
	 */
	public boolean getKeyState(int key) {
		return key_map.getOrDefault(key, false);
	}

	public static void main(String[] args) {
		gameInstance().run();
	}

	public void run() {
		init();

		level = new InGameLevel();

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
		base_shader = new Shader("assets/shaders/base.vert", "assets/shaders/base.frag");

		// Set pixel matrix
		pixelMatrix = new Matrix4f().scale(1 / getDimensions().x, 1 / getDimensions().y, 1);

		// Open AL initialization here, something something ALC.create() and get device null and idk


		// Utility temporaries
		Utils.initTemp();
	}

	private void gameRender(float dt) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		level.render(dt);

		glfwSwapBuffers(window); // swap the color buffers
	}

	private void gameUpdate(float dt) {
		level.tick(dt);
	}

	private void loop() {
		game_timer.init();
		render_timer.init();

		// Update inputs at 240 Hz
		float game_elapsed;
		float render_time;
		float game_acc = 0f;
		float game_interval = 1f / 240;
		while (!glfwWindowShouldClose(window)) {
			// Process glfw events
			glfwPollEvents();

			game_elapsed = game_timer.getElapsedTime();
			render_time = render_timer.getElapsedTime();

			// Update game at fixed rate
			game_acc += game_elapsed;
			while (game_acc >= game_interval) {
				gameUpdate(game_interval);
				game_acc -= game_interval;
			}

			// Render game
			gameRender(render_time);

			Utils.checkGlErrors();
		}
	}

	private void cleanup() {
		this.base_shader.close();

		level.end();

		// Free utilitiy temps
		Utils.cleanTemp();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private BulletSurvive() {
		super();
	}

	class KeyCallBack implements GLFWKeyCallbackI {
		public void invoke(long window, int key, int scancode, int action, int mods) {
			// yes lets put dense key codes into a heap hash map managed by java
			if (action == GLFW_PRESS) {
				key_map.put(key, true);
			} else if (action == GLFW_RELEASE) {
				key_map.put(key, false);
			}

			if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
				glfwSetWindowShouldClose(window, true);
		}
	}
}