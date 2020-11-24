package BulletSurvive;

public interface IEntity {
	/**
	 * Advances state of the entity. Passed in the change in time since the last tick.
	 *
	 * @param dt change in time as seconds
	 */
	void tick(float dt);

	/**
	 * Render the entity. Passed in the previous frame time
	 *
	 * @param dt frame time in seconds
	 */
	void render(float dt);
}
