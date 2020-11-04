package BulletSurvive;

import org.joml.*;

public class Boss implements IEntity, IPos, AutoCloseable {
	Vector2f pos = new Vector2f();
	Sprite bossSprite;

	Bullets bullets;

	public Boss() {
		bossSprite = new Sprite("assets/sample.png");
		bullets = new Bullets();

		pos.set(0, 256);
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
		bossSprite.draw(pos, 0, 0.25f, Utils.temp_m4f, Utils.temp_flbf);
		bullets.render(dt);
	}

	@Override
	public void close() {

	}
}
