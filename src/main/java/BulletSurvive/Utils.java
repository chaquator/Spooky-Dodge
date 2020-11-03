package BulletSurvive;

import org.joml.*;

import java.nio.*;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Utils {
	public static Matrix4f temp_m4f = new Matrix4f();
	public static FloatBuffer temp_flbf;

	public static void initTemp() {
		temp_m4f = new Matrix4f();
		temp_flbf = memAllocFloat(16);
	}

	public static void cleanTemp() {
		memFree(temp_flbf);
	}

	public static void checkGlErrors() {
		int e;
		while ((e = glGetError()) != GL_NO_ERROR) {
			throw new RuntimeException(String.format("GL ERROR: %x%n", e));
		}
	}
}
