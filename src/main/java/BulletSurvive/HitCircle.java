package BulletSurvive;

import org.joml.*;

public class HitCircle implements IEntity, IPos, AutoCloseable {
	Sprite hitSprite;

	Vector2f pos = new Vector2f();

	public HitCircle() {
		hitSprite = new Sprite("assets/hit.png");
	}

	public void setPos(Vector2fc pos) {
		this.pos.set(pos);
	}

	@Override
	public void tick(float dt) {
	}

	@Override
	public void render(float dt) {
		hitSprite.draw(pos, 0, 1.f, Utils.temp_m4f, Utils.temp_flbf);
	}

	@Override
	public Vector2f pos() {
		return this.pos;
	}

	@Override
	public void close() throws Exception {
		hitSprite.close();
	}
}
