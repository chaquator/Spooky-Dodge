package BulletSurvive;

import org.joml.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.*;

public class Sprite implements AutoCloseable {
	private int vbo, vao, ebo, tex;

	private Matrix4f scale = new Matrix4f();

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

		// Pass down base shader program here
		int program = BulletSurvive.getInstance().getBaseShader().getShaderProgram();

		// Set layout stuff
		int pos = glGetAttribLocation(program, "position");
		glEnableVertexAttribArray(pos);
		glVertexAttribPointer(pos, 2, GL_FLOAT, false, 16, 0);

		int texc = glGetAttribLocation(program, "texcoord");
		glEnableVertexAttribArray(texc);
		glVertexAttribPointer(texc, 2, GL_FLOAT, false, 16, 8);
		Utils.checkGlErrors();

		loadImage();
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

	public String getTextureSource() {
		return this.textureSource;
	}

	@Override
	public void close(){
		// Free outstanding resources
		glDeleteTextures(this.tex);

		glDeleteBuffers(this.ebo);
		glDeleteBuffers(this.vbo);

		glDeleteVertexArrays(this.vao);
	}
}
