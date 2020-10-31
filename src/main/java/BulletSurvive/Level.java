package BulletSurvive;

public class Level implements AutoCloseable{
	SpriteManager manager;

	public Level() {
		this.manager = new SpriteManager();
	}

	@Override
	public void close() {
		manager.close();
	}
}
