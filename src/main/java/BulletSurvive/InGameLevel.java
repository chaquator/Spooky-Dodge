package BulletSurvive;

public class InGameLevel implements Level{

	// Entities
	Player ply;

	public InGameLevel() {
		ply = new Player();
	}

	@Override
	public void render(float dt) {
		ply.render(dt);
	}

	@Override
	public void tick(float dt) {
		ply.tick(dt);
	}

	@Override
	public void end() {
		ply.close();
	}
}
