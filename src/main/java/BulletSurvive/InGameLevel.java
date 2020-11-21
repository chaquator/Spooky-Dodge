package BulletSurvive;

import org.joml.Math;
import static BulletSurvive.BulletSurvive.*;

public class InGameLevel implements ILevel {

	// Entities
	Player ply;
	Boss boss;
	PlyGhost plyGhost;

	// Level timer
	Timer timer = new Timer();
	float acc;

	float ph = 0.f;

	public InGameLevel() {
		ply = new Player();
		boss = new Boss();
		plyGhost = new PlyGhost();

		timer.init();
	}

	@Override
	public void tick(float dt) {
		ply.tick(dt);
		plyGhost.setPos(ply.pos());
		boss.tick(dt);

		if (boss.bulletCollide(ply.pos(), ply.radius())) {
			gameInstance().signalLevel(LEVEL.GAME_OVER);
		}

		acc += timer.getElapsedTime();
		float fps = 0.7f; // flips per sec
		float inv = (float) Math.PI * (2.f * fps);
		ph = (ph + (dt * inv)) % ((float) Math.PI * 2.f);
		float interval = 1.f / 20;
		while (acc > interval) {
			acc -= interval;

			boss.shootCircle(3, ph);
		}
	}

	@Override
	public void render(float dt) {
		plyGhost.render(dt);
		boss.render(dt);
		ply.render(dt);
	}

	@Override
	public void end() {
		ply.close();
		boss.close();
		plyGhost.close();
	}
}
