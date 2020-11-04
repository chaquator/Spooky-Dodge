package BulletSurvive;

public interface ILevel {
	/**
	 * Advances the state of the level. Passed in dt which is change in time since last tick.
	 * In this method you should call the tick methods of all entities.
	 *
	 * @param dt change in time as seconds
	 */
	void tick(float dt);

	/**
	 * Render the level. Passed in the frame time.
	 * In this method you should call the render method of all entities.
	 *
	 * @param dt frame time in seconds
	 */
	void render(float dt);

	/**
	 * Close level
	 */
	void end();
}
