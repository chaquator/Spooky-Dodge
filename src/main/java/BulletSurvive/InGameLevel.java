package BulletSurvive;

public class InGameLevel implements ILevel {

	// Entities
	Player ply;
	Boss boss;

	public InGameLevel() {
		ply = new Player();
		boss = new Boss();
	}

	@Override
	public void render(float dt) {
		boss.render(dt);
		ply.render(dt);
	}

	@Override
	public void tick(float dt) {
		ply.tick(dt);
		boss.tick(dt);
	}

	@Override
	public void end() {
		ply.close();
	}
}
