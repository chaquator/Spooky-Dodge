package BulletSurvive;

import java.lang.Math;

public class InGameLevel implements ILevel {

	// Entities
	Player ply;
	Boss boss;
	HitCircle hitCircle;

	// Level timer
	Timer timer = new Timer();
	float acc;

	float ph = 0.f;

	public InGameLevel() {
		ply = new Player();
		boss = new Boss();
		hitCircle = new HitCircle();

		timer.init();
	}

	@Override
	public void tick(float dt) {
		ply.tick(dt);
		hitCircle.setPos(ply.pos());
		boss.tick(dt);

		acc += timer.getElapsedTime();
		// Shoot a bullet once per second
		float interval = 1.f / 16;
		while (acc > interval) {
			acc -= interval;

			/*Vector2f tmp = Utils.temp_v2f_0;
			tmp.set(ply.pos())
					.sub(boss.pos())
					.normalize();
			boss.shootAt(tmp);*/
			ph = (ph + (float)Math.PI * (2.f/60.f)) % ((float)Math.PI * 2.f);
			boss.shootCircle(10, ph);
		}
	}

	@Override
	public void render(float dt) {
		ply.render(dt);
		boss.render(dt);
		hitCircle.render(dt);
	}

	@Override
	public void end() {
		ply.close();
		boss.close();
		hitCircle.close();
	}
}
