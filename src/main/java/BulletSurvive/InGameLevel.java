package BulletSurvive;

import org.joml.Math;
import static BulletSurvive.BulletSurvive.*;

public class InGameLevel implements ILevel {

	// Entities
	Player ply;
	Boss boss;
	PlyGhost plyGhost;

	public InGameLevel() {
		ply = new Player();
		boss = new Boss();
		plyGhost = new PlyGhost();

		boss.setPlyPosHandle(ply.pos());
	}

	@Override
	public void tick(float dt) {
		ply.tick(dt);
		plyGhost.setPos(ply.pos());
		boss.tick(dt);

		if (boss.bulletCollide(ply.pos(), ply.radius())) {
			gameInstance().signalLevel(LEVEL.GAME_OVER);
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
