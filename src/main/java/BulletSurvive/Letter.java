package BulletSurvive;

import org.joml.*;
import org.joml.Math;

/**
 * Pathetic letter sprite, no time to implement bitmap text rendering
 */
public class Letter implements IEntity, IPos, AutoCloseable {
	Sprite letterSprite;
	float ang;

	// Orbit angular speed (rad/s)
	float rspeed = (float) Math.PI * 2.f * 0.75f;
	// Orbit radius
	float radius = 8.f;

	Vector2f orbit = new Vector2f();
	Vector2f pos = new Vector2f();

	public Letter(char letter, float phase) {
		letterSprite = new Sprite(String.format("assets/text/%c.png", letter));
		this.ang = phase;
	}

	@Override
	public void tick(float dt) {
		this.pos.set(this.orbit).add(radius * Math.cos(ang), radius * Math.sin(ang));
		this.ang += rspeed * dt;
	}

	@Override
	public void render(float dt) {
		letterSprite.draw(this.pos, 0, 1, Utils.temp_m4f, Utils.temp_flbf);
	}

	@Override
	public Vector2f pos() {
		return this.orbit;
	}

	@Override
	public void close() {
		letterSprite.close();
	}
}
