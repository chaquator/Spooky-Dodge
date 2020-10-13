package BulletSurvive;

// import org.lwjgl.openal.AL;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.lang.Math;
import java.nio.*;
import org.joml.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

public class BulletSurvive {
  // Singletons :(
  private static BulletSurvive instance = null;

  // The window handle
  private long window;

  // Testing chaqs
  private Chaq[] chaqs;
  private Enemy enemy;

  // Timers
  private Timer game_timer = new Timer();
  private Timer render_timer = new Timer();
  private float total_time;

  // Callback
  private KeyCallBack kcb = new KeyCallBack();

  /**
   * Dimensions vector
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

  private BulletSurvive() {
    super();
  }

  public void run() {
    init();

    pixelMatrix =
      new Matrix4f().scale(1 / getDimensions().x, 1 / getDimensions().y, 1);

    loop();

    for (Chaq chaq : chaqs) {
      chaq.close();
    }

    // Free the window callbacks and destroy the window
    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    // Terminate GLFW and free the error callback
    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }

  private void init() {
    // Setup an error callback. The default implementation
    // will print the error message in System.err.
    GLFWErrorCallback.createPrint(System.err).set();

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (!glfwInit()) throw new IllegalStateException(
      "Unable to initialize GLFW"
    );

    // Configure GLFW
    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

    // Create the window
    window =
      glfwCreateWindow(
        (int) dimensions.x,
        (int) dimensions.y,
        "Bullet Survive",
        NULL,
        NULL
      );
    if (window == NULL) throw new RuntimeException(
      "Failed to create the GLFW window"
    );

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
    } // the stack frame is popped automatically

    // Make the OpenGL context current
    glfwMakeContextCurrent(window);
    // Enable v-sync
    glfwSwapInterval(1);

    // Make the window visible
    glfwShowWindow(window);

    // Enable GL
    GL.createCapabilities();
    processErrors();

    // Set the clear color
    glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
    processErrors();
    // Open AL initialization here, something something ALC.create() and get device null and idk

  }

  private void render() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

    // This should draw it
    for (Chaq chaq : chaqs) {
      chaq.draw();
    }
    enemy.draw();

    glfwSwapBuffers(window); // swap the color buffers
  }

  private void gameUpdate(float delta_time) {
    // Add time since last frame to total time
    this.total_time += delta_time;
    double t = this.total_time * 2 * Math.PI;
    float radius = 256;
    chaqs[0].setPos(radius * (float) Math.cos(t), radius * (float) Math.sin(t));
    chaqs[1].setPos(
        radius * (float) Math.cos(t + Math.PI),
        radius * (float) Math.sin(t + Math.PI)
      );

    // Subtract height of enemy to line up with top of screen
    float y = getDimensions().y - enemy.getHeight();
    enemy.setPos(0.0f, y);
    System.out.println(delta_time);
  }

  private void loop() {
    // The test chaq
    chaqs = new Chaq[2];
    chaqs[0] = new Chaq();
    chaqs[1] = new Chaq();
    enemy = new Enemy();

    // Update inputs at 60 fps
    float elapsed;
    //float acc = 0f;
    //float interval = 1f / 60;
    game_timer.init();
    render_timer.init();
    while (!glfwWindowShouldClose(window)) {
      // Process glfw events
      glfwPollEvents();

      // Render game
      float render_time = render_timer.getElapsedTime();
      render();

      // Update game at fixed rate
      elapsed = game_timer.getElapsedTime();
      //acc += elapsed;
      //total_time += elapsed;
      //while (acc >= interval) {
      //  gameUpdate(elapsed);
      //  acc -= interval;
      //}
      gameUpdate(elapsed);

      processErrors();
    }
  }

  public static void processErrors() {
    int e;
    while ((e = glGetError()) != GL_NO_ERROR) {
      throw new RuntimeException(String.format("%x%n", e));
    }
  }

  public static void main(String[] args) {
    BulletSurvive.getInstance().run();
  }

  static class KeyCallBack implements GLFWKeyCallbackI {

    public void invoke(
      long window,
      int key,
      int scancode,
      int action,
      int mods
    ) {
      if (
        key == GLFW_KEY_ESCAPE && action == GLFW_PRESS
      ) glfwSetWindowShouldClose(window, true);
    }
  }
}
