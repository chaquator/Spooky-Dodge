package BulletSurvive;

import org.joml.*;
import org.lwjgl.system.*;

import java.nio.*;

public class Boss implements IEntity, IPos, AutoCloseable {
	Vector2f pos = new Vector2f();
	Sprite bossSprite;

	Bullets bullets;

	static final float bullet_speed = 368.f ; // bullet speed in pixels per second

	public Boss() {
		bossSprite = new Sprite("assets/witch.png");
		bullets = new Bullets();

		pos.set(0, 384);
	}

	// Shoots at direction from boss's position
	public void shootAt(Vector2f dir) {
		dir.mul(bullet_speed);
		bullets.addBullet(this.pos, dir);
		dir.div(bullet_speed);
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
