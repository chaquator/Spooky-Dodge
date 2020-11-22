package BulletSurvive;

import org.joml.*;

import static org.lwjgl.glfw.GLFW.*;
import static BulletSurvive.BulletSurvive.*;

public class Player implements IEntity, IPos, AutoCloseable {
	Sprite playerSprite;
	Vector2f mov = new Vector2f();
	Vector2f pos;

	float speed = 96.f;

	public Player() {
		playerSprite = new Sprite("assets/hit.png");
		pos = new Vector2f(0, -256);
	}

	public float radius() {
		return 5.5f;
	}

	@Override
	public Vector2f pos() {
		return this.pos;
	}

	@Override
	public void tick(float dt) {
		int W = gameInstance().getKeyState(GLFW_KEY_W) || gameInstance().getKeyState(GLFW_KEY_UP) ? 1 : 0;
		int A = gameInstance().getKeyState(GLFW_KEY_A) || gameInstance().getKeyState(GLFW_KEY_LEFT) ? 1 : 0;
		int S = gameInstance().getKeyState(GLFW_KEY_S) || gameInstance().getKeyState(GLFW_KEY_DOWN) ? 1 : 0;
		int D = gameInstance().getKeyState(GLFW_KEY_D) || gameInstance().getKeyState(GLFW_KEY_RIGHT) ? 1 : 0;
		mov.set(D - A, W - S); // movement vector based off of keypresses
		if (mov.lengthSquared() != 0) {
			mov.normalize().mul(speed * dt);
			pos.add(mov);

			Vector2fc dim = gameInstance().getDimensions();
			if (pos.x() >= dim.x() / 2.f) pos.set(dim.x() / 2.f, pos.y());
			if (pos.x() <= -dim.x() / 2.f) pos.set(-dim.x() / 2.f, pos.y());
			if (pos.y() >= dim.y() / 2.f) pos.set(pos.x(), dim.y() / 2.f);
			if (pos.y() <= -dim.y() / 2.f) pos.set(pos.x(), -dim.y() / 2.f);
		}
	}

	@Override
	public void render(float dt) {
		playerSprite.draw(pos, 0, 1.f, Utils.temp_m4f, Utils.temp_flbf);
	}

	@Override
	public void close() {
		playerSprite.close();
	}
}
