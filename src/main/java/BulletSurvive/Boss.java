package BulletSurvive;

import org.joml.*;
import java.lang.Math;

import java.nio.*;

public class Boss implements IEntity, IPos, AutoCloseable {
	Vector2f pos = new Vector2f();

	Matrix2f tmp_m2f = new Matrix2f();
	Vector2f tmp_v2 = new Vector2f();

	Sprite bossSprite;

	Bullets bullets;

	static final float bullet_speed = 336.f; // bullet speed in pixels per second

	public Boss() {
		bossSprite = new Sprite("assets/witch.png");
		bullets = new Bullets();

		pos.set(0, 384);
	}

	/**
	 * Shoots at a direction from boss's position
	 *
	 * @param dir normalized direciton to shoot in
	 */
	public void shootAt(Vector2f dir) {
		dir.mul(bullet_speed);
		bullets.addBullet(this.pos, dir);
		dir.div(bullet_speed);
	}

	/**
	 * Shoots in a circle around self
	 *
	 * @param count number of bullets to surround circle
	 * @param phase phase of first bullet (1st bullet is to the right)
	 */
	public void shootCircle(int count, float phase) {
		// Unit velocity
		tmp_v2.set(bullet_speed, 0);

		// Apply Phase
		tmp_m2f.identity().rotate(phase).transform(tmp_v2);

		// Calculate Interval
		tmp_m2f.identity().rotate(((float) Math.PI * 2.f) / (float) count);

		// Add bullets
		for (int i = 0; i < count; ++i) {
			bullets.addBullet(this.pos, tmp_v2);
			tmp_m2f.transform(tmp_v2);
		}
	}

	@Override
	public Vector2f pos() {
		return this.pos;
	}

	@Override
	public void tick(float dt) {
		bullets.tick(dt);
	}

	@Override
	public void render(float dt) {
		bullets.render(dt);
		bossSprite.draw(pos, 0, 1.f, Utils.temp_m4f, Utils.temp_flbf);
	}

	@Override
	public void close() {
		bossSprite.close();
		bullets.close();
	}
}
