package BulletSurvive;

import org.joml.*;

import java.nio.*;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Utils {
	public static Vector2f temp_v2f_0 = new Vector2f();
	public static Vector2f temp_v2f_1 = new Vector2f();
	public static Matrix4f temp_m4f = new Matrix4f();
	public static FloatBuffer temp_flbf;

	public static void initTemp() {
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

	public static boolean circleCollide(Vector2fc p1, float r1, Vector2fc p2, float r2) {
		float d = p1.distanceSquared(p2);
		float d2 = r1+r2;
		return d <= (d2*d2);
	}
}
