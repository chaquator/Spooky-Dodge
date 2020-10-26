package BulletSurvive;

public class Level implements AutoCloseable{
	SpriteManager manager;

	@Override
	public void close() {
		manager.close();
	}
}
