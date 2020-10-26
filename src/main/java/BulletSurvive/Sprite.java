package BulletSurvive;

import org.joml.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.*;

public class Sprite implements AutoCloseable {
	private int vbo, vao, ebo, vert_i, frag_i, shader_program, tex;
	private int u_tex, u_pixel, u_transform;

	private Matrix4f scale = new Matrix4f();

	private static String vert_src =
			"#version 330 core\n" +
					"in vec2 position;\n" +
					"in vec2 texcoord;\n" +
					"out vec2 Texcoord;\n" +
					"uniform mat4 pixel;" +
					"uniform mat4 trans;" +
					"void main()\n" +
					"{\n" +
					"    Texcoord = texcoord;\n" +
					"    gl_Position = pixel * trans * vec4(position, 0.0, 1.0);\n" +
					"}\n";
	private static String frag_src =
			"#version 330 core\n" +
					"in vec2 Texcoord;\n" +
					"out vec4 outColor;\n" +
					"uniform sampler2D tex;\n" +
					"void main()\n" +
					"{\n" +
					"    outColor = texture(tex, Texcoord);\n" +
					"}\n";

	public static int getVertexCount() {
		return 6;
	}

	private String textureSource;

	private void loadImage() {
		int width, height;
		ByteBuffer image;

		// load image
		try (MemoryStack stack = stackPush()) {
			IntBuffer x = stack.mallocInt(1);
			IntBuffer y = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);
			image = stbi_load(this.textureSource, x, y, channels, 4);
			width = x.get();
			height = y.get();
		}

		if (image == null) throw new RuntimeException(String.format("Image not found: %s", this.textureSource));

		// Transform scales by width and height
		this.scale.scale(width, height, 1);

		// Generate texture then associate it with texture 0
		this.tex = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, this.tex);
		Utils.checkGlErrors();

		// Upload then free image
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
		Utils.checkGlErrors();
		stbi_image_free(image);

		// Change sampling to only sample up to the borders of the texture
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		Utils.checkGlErrors();

		// Texture filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		Utils.checkGlErrors();

		// Point program's uniform "tex" sampler2d variable to texture 0
		this.u_tex = glGetUniformLocation(this.shader_program, "tex");
		glUniform1i(this.u_tex, 0);
		Utils.checkGlErrors();
	}

	private boolean compileShader(int shader) {
		try (MemoryStack stack = stackPush()) {
			glCompileShader(shader);
			IntBuffer status = stack.mallocInt(1);
			glGetShaderiv(shader, GL_COMPILE_STATUS, status);
			return status.get(0) == GL_TRUE;
		}
	}

	public Sprite(String filename) {
		this.textureSource = filename;

		// VAO
		this.vao = glGenVertexArrays();
		glBindVertexArray(this.vao);
		Utils.checkGlErrors();

		// VBO
		this.vbo = glGenBuffers();
		float[] vertices = {
				//  Position, Texcoords
				-1.0f, 1.0f, 0.0f, 0.0f, // Top-left
				1.0f, 1.0f, 1.0f, 0.0f, // Top-right
				1.0f, -1.0f, 1.0f, 1.0f, // Bottom-right
				-1.0f, -1.0f, 0.0f, 1.0f  // Bottom-left
		};
		glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		Utils.checkGlErrors();

		// Element array
		this.ebo = glGenBuffers();
		int[] elements = {
				0, 1, 3,
				1, 2, 3
		};
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
		Utils.checkGlErrors();

		// Shader stuff
		this.vert_i = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(this.vert_i, vert_src);
		if (!compileShader(this.vert_i)) {
			throw new RuntimeException(glGetShaderInfoLog(this.vert_i));
		}
		Utils.checkGlErrors();

		this.frag_i = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(this.frag_i, frag_src);
		if (!compileShader(this.frag_i)) {
			throw new RuntimeException(glGetShaderInfoLog(this.frag_i));
		}
		Utils.checkGlErrors();

		this.shader_program = glCreateProgram();
		glAttachShader(this.shader_program, this.vert_i);
		glAttachShader(this.shader_program, this.frag_i);
		glBindFragDataLocation(this.shader_program, 0, "outColor");
		glLinkProgram(this.shader_program);
		glUseProgram(this.shader_program);
		try {
			Utils.checkGlErrors();
		} catch (Exception e) {
			System.out.println(glGetProgramInfoLog(shader_program));
			throw new RuntimeException("glUseShader error");
		}

		// Layout stuff
		int pos = glGetAttribLocation(this.shader_program, "position");
		glEnableVertexAttribArray(pos);
		glVertexAttribPointer(pos, 2, GL_FLOAT, false, 16, 0);

		int texc = glGetAttribLocation(this.shader_program, "texcoord");
		glEnableVertexAttribArray(texc);
		glVertexAttribPointer(texc, 2, GL_FLOAT, false, 16, 8);
		Utils.checkGlErrors();

		this.u_pixel = glGetUniformLocation(shader_program, "pixel");
		this.u_transform = glGetUniformLocation(shader_program, "trans");
		Utils.checkGlErrors();

		loadImage();
	}

	// This is just here so we dont forget how to draw
	private void drawSingle() {
		// Bind the texture
		glActiveTexture(GL_TEXTURE0); // Active texture 0 for the uniform in shader
		glBindTexture(GL_TEXTURE_2D, tex); // Bind the texture to texture 0
		Utils.checkGlErrors();

		// Send transforms here for now
		// In the future, pixel matrix will get sent from the main loop
		// Other transforms will remain here
		// glUniformMatrix4fv(u_pixel, false, BulletSurvive.getInstance().getPixelMatrix().get(fb));
		// glUniformMatrix4fv(u_scale, false, scale.get(fb));
		// Utils.checkGlErrors();

		// Bind VAO, this binds the VBO, EBO, and layout stuff with it
		glBindVertexArray(vao);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		Utils.checkGlErrors();
	}

	public int getActiveTexture() {
		return GL_TEXTURE0;
	}

	public int getTextureId() {
		return this.tex;
	}

	public int getVao() {
		return this.vao;
	}

	public int getPixelUniform() {
		return this.u_pixel;
	}

	public int getTransformUniform() {
		return this.u_transform;
	}

	public String getTextureSource() {
		return this.textureSource;
	}

	@Override
	public void close(){
		// Free outstanding resources
		glDeleteTextures(this.tex);

		glDeleteProgram(this.shader_program);
		glDeleteShader(this.vert_i);
		glDeleteShader(this.frag_i);

		glDeleteBuffers(this.ebo);
		glDeleteBuffers(this.vbo);

		glDeleteVertexArrays(this.vao);
	}
}
