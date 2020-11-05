package BulletSurvive;

import org.joml.*;

import java.nio.*;

import static org.lwjgl.opengl.GL33.*;
import static BulletSurvive.BulletSurvive.*;

public class Sprite extends ASprite {
	public Sprite(String filename) {
		super(filename, gameInstance().getBaseShader().getShaderProgram());
	}

	/**
	 * Draws single sprite. If you need to draw large multiples of the same sprite, use the instanced sprite class
	 *
	 * @param position  -- 2d position
	 * @param angle     -- angle of rotation
	 * @param scale     -- uniform scale
	 * @param temp_mat  -- pass in temporary matrix to make use of
	 * @param float_buf -- pass in temporary lwjgl-malloc'd floatbuffer to make use of
	 *                  NOTE: use org.lwjgl.system.MemoryUtil.memAllocFloat(16)
	 */
	public void draw(Vector2f position, float angle, float scale,
					 Matrix4f temp_mat, FloatBuffer float_buf) {

		// Bind texture
		glActiveTexture(getActiveTexture());
		glBindTexture(GL_TEXTURE_2D, getTextureId());

		// Upload uniforms

		// Pixel matrix
		glUniformMatrix4fv(gameInstance().getBaseShader().getPixelUniform(), false, gameInstance().getPixelMatrix().get(float_buf));

		// Transform matrix for given object
		temp_mat.identity()
				.translate(position.x, position.y, 0)
				.mulAffine(this.imgScale)
				.scale(scale, scale, 1)
				.rotateAffine(angle, 0, 0, 1);
		glUniformMatrix4fv(gameInstance().getBaseShader().getTransformUniform(), false, temp_mat.get(float_buf));

		// Bind VAO
		glBindVertexArray(getVao());

		// Draw call
		glDrawElements(GL_TRIANGLES, Sprite.getVertexCount(), GL_UNSIGNED_INT, 0);
	}
}
