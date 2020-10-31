package BulletSurvive;

import org.joml.*;

import java.lang.*;
import java.util.*;
import java.nio.*;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

public class SpriteManager implements AutoCloseable {
	private ArrayList<Sprite> loadedSprites;
	private HashMap<String, Sprite> spriteMap;
	private PriorityQueue<Integer> vacancies;

	private FloatBuffer float_buf;
	private Matrix4f temp_mat;

	public SpriteManager() {
		float_buf = memAllocFloat(16);
		temp_mat = new Matrix4f();
	}

	/**
	 * Loads sprite and returns it
	 *
	 * @param filename -- asset filename
	 * @return reference to loaded asset
	 */
	public Sprite loadSprite(String filename) {
		// Return loaded asset if already loaded
		if (spriteMap.containsKey(filename)) {
			return spriteMap.get(filename);
		}

		// Create asset
		Sprite sprite = new Sprite(filename);

		// Add to sprite map
		spriteMap.put(filename, sprite);

		// Add to loaded sprites
		if (vacancies.isEmpty()) {
			// Adding to end
			loadedSprites.add(sprite);
		} else {
			// Adding at vacant spot
			loadedSprites.add(vacancies.poll(), sprite);
		}

		return sprite;
	}

	/**
	 * Removes sprite from manager and closes it
	 *
	 * @param sprite -- sset to remove
	 */
	public void removeSprite(Sprite sprite) {
		// TODO: need to add reference counting if multiple entities use the same sprite

		if (!spriteMap.containsKey(sprite.getTextureSource()))
			throw new RuntimeException("Asset not found in asset map");
		spriteMap.remove(sprite.getTextureSource());

		int index = loadedSprites.indexOf(sprite);
		if (index < 0) throw new RuntimeException("Asset not found in loaded assets list");
		loadedSprites.remove(index);
		vacancies.add(index);

		sprite.close();
	}

	public void drawSprite(Sprite sprite, Vector2f position, float angle, float scale) {
		// This method will change in the future.
		// Instead of issuing a draw call every method call, the method will
		// populate a queue with drawing information for its given sprite and
		// use instanced drawing triggered by another method whenever the game needs to render

		// Bind texture
		glActiveTexture(sprite.getActiveTexture());
		glBindTexture(GL_TEXTURE_2D, sprite.getTextureId());

		// Upload uniforms

		// Pixel matrix
		glUniformMatrix4fv(BulletSurvive.getInstance().getBaseShader().getPixelUniform(), false, BulletSurvive.getInstance().getPixelMatrix().get(float_buf));

		// Transform matrix for given object
		temp_mat.identity()
				.scale(scale, scale, 1)
				.rotateAffine(angle, 0, 0, 1)
				.translate(position.x, position.y, 0);
		glUniformMatrix4fv(BulletSurvive.getInstance().getBaseShader().getTransformUniform(), false, temp_mat.get(float_buf));

		// Bind VAO
		glBindVertexArray(sprite.getVao());

		// Draw call
		glDrawElements(GL_TRIANGLES, Sprite.getVertexCount(), GL_UNSIGNED_INT, 0);
	}

	public void close() {
		memFree(float_buf);
	}
}
