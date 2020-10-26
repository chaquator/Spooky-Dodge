package BulletSurvive;

import static org.lwjgl.opengl.GL33.*;

public class Utils {
	public static void checkGlErrors() {
		int e;
		while ((e = glGetError()) != GL_NO_ERROR) {
			throw new RuntimeException(String.format("GL ERROR: %x%n", e));
		}
	}
}
