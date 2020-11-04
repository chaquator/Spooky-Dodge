package BulletSurvive;

import org.joml.*;

public class Boss implements IEntity, IPos, AutoCloseable {
	Vector2f pos;

	@Override
	public Vector2f pos() {
		return this.pos;
	}

	@Override
	public void tick(float dt) {

	}

	@Override
	public void render(float dt) {

	}

	@Override
	public void close() {

	}
}
