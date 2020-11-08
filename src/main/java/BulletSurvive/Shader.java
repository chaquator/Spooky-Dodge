package BulletSurvive;

import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.*;

import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Shader implements AutoCloseable {
	private final int vert_i, frag_i, shader_program;
	private final int u_pixel, u_transform;

	public int getShaderProgram() {
		return this.shader_program;
	}

	public int getPixelUniform() {
		return this.u_pixel;
	}

	public int getTransformUniform() {
		return this.u_transform;
	}

	public Shader(String vert_f, String frag_f) {
		// Vertex shader
		this.vert_i = glCreateShader(GL_VERTEX_SHADER);
		String vert_src;
		try {
			vert_src = new String(Files.readAllBytes(Paths.get(vert_f)));
		} catch (IOException e) {
			throw new RuntimeException(String.format("File not found: %s", vert_f));
		}
		glShaderSource(this.vert_i, vert_src);
		if (compileShader(this.vert_i)) {

		} else {
			throw new RuntimeException(glGetShaderInfoLog(this.vert_i));
		}
		System.out.println(glGetShaderInfoLog(this.vert_i));
		Utils.checkGlErrors();

		// Fragment shader
		this.frag_i = glCreateShader(GL_FRAGMENT_SHADER);
		String frag_src;
		try {
			frag_src = new String(Files.readAllBytes(Paths.get(frag_f)));
		} catch (IOException e) {
			throw new RuntimeException(String.format("File not found: %s", frag_f));
		}
		glShaderSource(this.frag_i, frag_src);
		if (compileShader(this.frag_i)) {

		} else {
			throw new RuntimeException(glGetShaderInfoLog(this.frag_i));
		}
		System.out.println(glGetShaderInfoLog(this.frag_i));
		Utils.checkGlErrors();

		// Create shader program, attach shaders to it, bind color, and link
		this.shader_program = glCreateProgram();
		glAttachShader(this.shader_program, this.vert_i);
		glAttachShader(this.shader_program, this.frag_i);
		glBindFragDataLocation(this.shader_program, 0, "outColor");
		glLinkProgram(this.shader_program);

		// Use shader program
		glUseProgram(this.shader_program);
		try {
			Utils.checkGlErrors();
		} catch (Exception e) {
			System.out.println(glGetProgramInfoLog(shader_program));
			throw new RuntimeException("glUseProgram error");
		}

		// this is so not extendable idk
		// Point program's uniform "tex" sampler2d variable to texture 0
		int u_tex = glGetUniformLocation(this.shader_program, "tex");
		glUniform1i(u_tex, 0);
		Utils.checkGlErrors();

		// Get uniform locations
		this.u_pixel = glGetUniformLocation(shader_program, "pixel");
		this.u_transform = glGetUniformLocation(shader_program, "trans");
		Utils.checkGlErrors();
	}

	/**
	 * @param shader shader program number
	 * @return true if shader compiles successfully
	 */
	private boolean compileShader(int shader) {
		try (MemoryStack stack = stackPush()) {
			glCompileShader(shader);
			IntBuffer status = stack.mallocInt(1);
			glGetShaderiv(shader, GL_COMPILE_STATUS, status);
			return status.get(0) == GL_TRUE;
		}
	}

	@Override
	public void close() {
		glDeleteProgram(this.shader_program);
		glDeleteShader(this.vert_i);
		glDeleteShader(this.frag_i);
	}
}
