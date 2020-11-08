package BulletSurvive;

import org.joml.Vector2f;

public class InGameLevel implements ILevel {

	// Entities
	Player ply;
	Boss boss;

	// Level timer
	Timer timer = new Timer();
	float acc;

	public InGameLevel() {
		ply = new Player();
		boss = new Boss();

		timer.init();
	}

	@Override
	public void tick(float dt) {
		ply.tick(dt);
		boss.tick(dt);

		acc += timer.getElapsedTime();
		// Shoot a bullet once per second
		float interval = 1.f/512;
		while (acc > interval) {
			acc -= interval;

			Vector2f tmp = Utils.temp_v2f_0;
			tmp.set(ply.pos())
					.sub(boss.pos())
					.normalize();
			boss.shootAt(tmp);
		}
	}

	@Override
	public void render(float dt) {
		ply.render(dt);
		boss.render(dt);
	}

	@Override
	public void end() {
		ply.close();
		boss.close();
	}
}
