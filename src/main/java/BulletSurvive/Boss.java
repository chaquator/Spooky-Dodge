package BulletSurvive;

import org.joml.*;

import org.joml.Math;

import java.util.Random;

public class Boss implements IEntity, IPos, AutoCloseable {
	Vector2f pos = new Vector2f();
	Vector2f target = new Vector2f();
	Vector2f plyPosHandle = new Vector2f();

	Matrix2f tmp_m2f = new Matrix2f();
	Vector2f tmp_v2 = new Vector2f();

	Sprite bossSprite;

	Bullets bullets;

	final float bullet_speed = 96.f; // bullet speed in pixels per second
	final float dist_threshold = 32.f;

	float shoot_acc = 0;
	float state_acc = 0;
	int bullet_count = 0;

	enum STATE {
		IDLE(0, 184, "IDLE", 0), SPREAD(0, 184, "SPREAD", 3.f / 7.f), SPIRAL(0, 0, "SPIRAL", 1.f / 15.f),
		MOVE_BACK(0, 184, "MOVE BACK", 0), MOVE_SPREAD(0, 184, "MOVE SPREAD", 0), MOVE_SPIRAL(0, 0, "MOVE SPIRAL", 0);

		private final float x;
		private final float y;
		private final String name;
		private final float shoot_interval;

		STATE(float x, float y, String name, float shoot_interval) {
			this.x = x;
			this.y = y;
			this.name = name;
			this.shoot_interval = shoot_interval;
		}

		public float tx() {
			return this.x;
		}

		public float ty() {
			return this.y;
		}

		public float shoot_interval() {
			return this.shoot_interval;
		}

		private static final STATE[] ATTACKS =
				{MOVE_SPREAD, MOVE_SPIRAL};

		private static final Random RANDOM = new Random();

		public static STATE randomAttack() {
			return ATTACKS[RANDOM.nextInt(ATTACKS.length)];
		}

		@Override
		public String toString() {
			return String.format("%d %s", this.ordinal(), this.name);
		}
	}

	STATE state;

	public Boss() {
		bossSprite = new Sprite("assets/witch.png");
		bullets = new Bullets();

		pos.set(0, 512);
		state = STATE.MOVE_BACK;
		updateTarget();
	}

	private void clk(float dt) {
		state_acc += dt;
		switch (state) {
			case IDLE:
				if (state_acc >= 2.5f) {
					updateState(STATE.randomAttack());
				}
				break;
			case SPREAD:
				if (state_acc >= 10.f) {
					updateState(STATE.MOVE_BACK);
				}
				break;
			case SPIRAL:
				if (state_acc >= 5.f) {
					updateState(STATE.MOVE_BACK);
				}
				break;
			case MOVE_BACK:
				if (closeEnough()) {
					updateState(STATE.IDLE);
				}
				break;
			case MOVE_SPIRAL:
				if (closeEnough()) {
					updateState(STATE.SPIRAL);
				}
				break;
			case MOVE_SPREAD:
				if (closeEnough()) {
					updateState(STATE.SPREAD);
				}
				break;
		}
	}

	private boolean closeEnough() {
		return pos.distanceSquared(target) < (dist_threshold * dist_threshold);
	}

	private void updateTarget() {
		target.set(state.tx(), state.ty());
	}

	private void updateState(STATE st) {
		state_acc = 0.f;
		shoot_acc = 0.f;
		state = st;
		updateTarget();
	}

	private Vector2f bulletSrc() {
		return target;
	}

	/**
	 * Shoot n bullets spread out towards a direction
	 *
	 * @param count  number of bullets
	 * @param dir    direction to shoot
	 * @param spread angular spread
	 */
	private void shootSpread(int count, Vector2f dir, float spread) {
		if (count <= 1) {
			bullets.addBullet(bulletSrc(), dir.mul(bullet_speed));
			dir.div(bullet_speed);
			return;
		}

		// Unit
		tmp_v2.set(dir).mul(bullet_speed);

		// Offset
		tmp_m2f.identity().rotate(-spread / 2.f).transform(tmp_v2);

		// Interval
		tmp_m2f.identity().rotate(spread / (count - 1));

		// Bullets
		for (int i = 0; i < count; ++i) {
			bullets.addBullet(bulletSrc(), tmp_v2);
			tmp_m2f.transform(tmp_v2);
		}
	}

	/**
	 * Shoots in a circle around self
	 *
	 * @param count number of bullets to surround circle
	 * @param phase phase of first bullet (1st bullet is to the right)
	 */
	private void shootCircle(int count, float phase) {
		// Unit velocity
		tmp_v2.set(bullet_speed, 0);

		// Apply Phase
		tmp_m2f.identity().rotate(phase).transform(tmp_v2);

		// Calculate Interval
		tmp_m2f.identity().rotate(((float) Math.PI * 2.f) / (float) count);

		// Add bullets
		for (int i = 0; i < count; ++i) {
			bullets.addBullet(bulletSrc(), tmp_v2);
			tmp_m2f.transform(tmp_v2);
		}
	}

	public boolean bulletCollide(Vector2fc pos, float r) {
		return bullets.bulletsColliding(pos, r);
	}

	public void setPlyPosHandle(Vector2f pos) {
		this.plyPosHandle = pos;
	}

	@Override
	public Vector2f pos() {
		return this.pos;
	}

	@Override
	public void tick(float dt) {
		bullets.tick(dt);
		clk(dt);

		// Float to target
		pos.lerp(target, dt * 0.999f);

		// Shoot based on state
		float sh = state.shoot_interval(); // shoot interval
		if (sh != 0) {
			shoot_acc += dt;
			while (shoot_acc > sh) {
				shoot_acc -= sh;
				switch (state) {
					case SPREAD:
						bullet_count = (bullet_count + 1) % 2;
						shootSpread(6 + bullet_count, Utils.temp_v2f_0.set(plyPosHandle).sub(bulletSrc()).normalize(), (float) Math.PI * 0.5f);
						break;
					case SPIRAL: {
						float fps = 0.6f; // flips per sec
						shootCircle(3, ((float) Math.PI * 2.f * state_acc * fps) % ((float) Math.PI * 2.f));
					}
					break;
				}
			}
		}

	}

	@Override
	public void render(float dt) {
		bullets.render(dt);
		bossSprite.draw(pos, 0, 1.f, Utils.temp_m4f, Utils.temp_flbf);
	}

	@Override
	public void close() {
		bossSprite.close();
		bullets.close();
	}
}
