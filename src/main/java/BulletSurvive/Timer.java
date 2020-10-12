package BulletSurvive;

/**
 * Retrieved from https://github.com/lwjglgamedev/lwjglbook/blob/master/chapter02/src/main/java/org/lwjglb/engine/Timer.java
  */
public class Timer {

	private double lastLoopTime;

	public void init() {
		lastLoopTime = getTime();
	}

	public double getTime() {
		return System.nanoTime() / 1_000_000_000.0;
	}

	public float getElapsedTime() {
		double time = getTime();
		float elapsedTime = (float) (time - lastLoopTime);
		lastLoopTime = time;
		return elapsedTime;
	}

	public double getLastLoopTime() {
		return lastLoopTime;
	}
}