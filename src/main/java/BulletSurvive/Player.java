package BulletSurvive;

import org.joml.*;

import static org.lwjgl.glfw.GLFW.*;
import static BulletSurvive.BulletSurvive.*;

public class Player implements IEntity, IPos, AutoCloseable {
	Sprite playerSprite;
	Vector2f mov = new Vector2f();
	Vector2f pos = new Vector2f();

	float speed = 1024.f;

	public Player() {
		playerSprite = new Sprite("assets/chaq.png");
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
			mov.normalize().mul(speed*dt);
			pos.add(mov);
		}
	}

	@Override
	public void render(float dt) {
		playerSprite.draw(pos, 0, 0.5f, Utils.temp_m4f, Utils.temp_flbf);
	}

	@Override
	public void close() {
		playerSprite.close();
	}
}
