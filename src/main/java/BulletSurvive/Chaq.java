package BulletSurvive;

import org.joml.*;

import java.nio.*;

import org.lwjgl.system.*;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Chaq implements AutoCloseable {

	private int vbo, vao, ebo, vert_i, frag_i, shaderProgram, tex;
	private int u_tex, u_pixel, u_scale;

	private Matrix4f scale = new Matrix4f();
	private Vector3f pos = new Vector3f();

	FloatBuffer fb;

	String vert =
			"#version 330 core\n" +
					"in vec2 position;\n" +
					"in vec2 texcoord;\n" +
					"out vec2 Texcoord;\n" +
					"uniform mat4 pixel;" +
					"uniform mat4 scale;" +
					"void main()\n" +
					"{\n" +
					"    Texcoord = texcoord;\n" +
					"    gl_Position = pixel * scale * vec4(position, 0.0, 1.0);\n" +
					"}\n";
	String frag =
			"#version 330 core\n" +
					"in vec2 Texcoord;\n" +
					"out vec4 outColor;\n" +
					"uniform sampler2D tex;\n" +
					"void main()\n" +
					"{\n" +
					"    outColor = texture(tex, Texcoord);\n" +
					"}\n";

	private void loadImage() {
		String filename = "assets/candy_corn.png";
		int width, height;
		ByteBuffer image;

		// load image
		try (MemoryStack stack = stackPush()) {
			IntBuffer x = stack.mallocInt(1);
			IntBuffer y = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);
			image = stbi_load(filename, x, y, channels, 4);
			width = x.get();
			height = y.get();
		}

		if (image == null) throw new RuntimeException(String.format("Image not found: %s", filename));

		// Transform scales by width and height
		scale.scale(width, height, 1);

		// Generate texture then associate it with texture 0
		tex = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, tex);
		BulletSurvive.checkGlErrors();

		// Upload then free image
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
		BulletSurvive.checkGlErrors();
		stbi_image_free(image);

		// Change sampling to only sample up to the borders of the texture
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		BulletSurvive.checkGlErrors();

		// Texture filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		BulletSurvive.checkGlErrors();

		// Point program's uniform "tex" sampler2d variable to texture 0
		u_tex = glGetUniformLocation(shaderProgram, "tex");
		glUniform1i(u_tex, 0);
		BulletSurvive.checkGlErrors();
	}

	private boolean compileShader(int shader) {
		try (MemoryStack stack = stackPush()) {
			glCompileShader(shader);
			IntBuffer status = stack.mallocInt(1);
			glGetShaderiv(shader, GL_COMPILE_STATUS, status);
			return status.get(0) == GL_TRUE;
		}
	}

	public Chaq() {
		// Vertex array object for easy context switching ig?
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		BulletSurvive.checkGlErrors();

		// VBO
		vbo = glGenBuffers();
		float[] vertices = {
				//  Position, Texcoords
				-1.0f, 1.0f, 0.0f, 0.0f, // Top-left
				1.0f, 1.0f, 1.0f, 0.0f, // Top-right
				1.0f, -1.0f, 1.0f, 1.0f, // Bottom-right
				-1.0f, -1.0f, 0.0f, 1.0f  // Bottom-left
		};
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		BulletSurvive.checkGlErrors();

		// Element array
		ebo = glGenBuffers();
		int[] elements = {
				0, 1, 3,
				1, 2, 3
		};
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
		BulletSurvive.checkGlErrors();

		// Shader stuff
		vert_i = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vert_i, vert);
		if (!compileShader(vert_i)) {
			throw new RuntimeException(glGetShaderInfoLog(vert_i));
		}
		BulletSurvive.checkGlErrors();

		frag_i = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(frag_i, frag);
		if (!compileShader(frag_i)) {
			throw new RuntimeException(glGetShaderInfoLog(frag_i));
		}
		BulletSurvive.checkGlErrors();

		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vert_i);
		glAttachShader(shaderProgram, frag_i);
		glBindFragDataLocation(shaderProgram, 0, "outColor");
		glLinkProgram(shaderProgram);
		glUseProgram(shaderProgram);
		try {
			BulletSurvive.checkGlErrors();
		} catch (Exception e) {
			System.out.println(glGetProgramInfoLog(shaderProgram));
			throw new RuntimeException("glUseShader error");
		}


		// Layout stuff
		int pos = glGetAttribLocation(shaderProgram, "position");
		glEnableVertexAttribArray(pos);
		glVertexAttribPointer(pos, 2, GL_FLOAT, false, 16, 0);

		int texc = glGetAttribLocation(shaderProgram, "texcoord");
		glEnableVertexAttribArray(texc);
		glVertexAttribPointer(texc, 2, GL_FLOAT, false, 16, 8);
		BulletSurvive.checkGlErrors();

		u_pixel = glGetUniformLocation(shaderProgram, "pixel");
		u_scale = glGetUniformLocation(shaderProgram, "scale");
		BulletSurvive.checkGlErrors();

		loadImage();

		fb = memAllocFloat(16);
	}

	// Copy in position
	public void setPos(Vector3f pos) {
		pos.get(this.pos);
		scale.setTranslation(this.pos);
	}

	public void setPos(float x, float y) {
		scale.setTranslation(x, y, 0);
	}

	public void draw() {
		// Bind the texture
		glActiveTexture(GL_TEXTURE0); // Active texture 0 for the uniform in shader
		glBindTexture(GL_TEXTURE_2D, tex); // Bind the texture to texture 0
		BulletSurvive.checkGlErrors();

		// Send transforms here for now
		// In the future, pixel matrix will get sent from the main loop
		// Other transforms will remain here
		glUniformMatrix4fv(u_pixel, false, BulletSurvive.getInstance().getPixelMatrix().get(fb));
		glUniformMatrix4fv(u_scale, false, scale.get(fb));
		BulletSurvive.checkGlErrors();

		// Bind VAO, this binds the VBO, EBO, and layout stuff with it
		glBindVertexArray(vao);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		BulletSurvive.checkGlErrors();
	}

	public void close() {
		// Free outstanding resources
		glDeleteTextures(tex);

		glDeleteProgram(shaderProgram);
		glDeleteShader(vert_i);
		glDeleteShader(frag_i);

		glDeleteBuffers(ebo);
		glDeleteBuffers(vbo);

		glDeleteVertexArrays(vao);

		memFree(fb);
	}
}
