package BulletSurvive;

import java.nio.*;
import java.util.*;

import org.joml.*;

import static org.lwjgl.system.MemoryUtil.*;
import static BulletSurvive.BulletSurvive.*;

import java.lang.Math;

public class Bullets implements IEntity, AutoCloseable {
	Sprite bulletSprite;

	// For each bullet need position, angle/velocity
	// Buffer which will holds positions for bullets, will be uploaded to opengl instanced drawing
	ByteBuffer bullet_buf;
	PriorityQueue<Integer> bullet_vacancies;
	int bulletsUsed;
	int bulletCapacity;
	int bulletSize = 18; // pos v2(2 floats -- 4 bytes), vel v2(2 floats -- 4 bytes), valid bool(char -- 2 bytes)
	final byte[] bullet_empty = new byte[bulletSize];

	public Bullets() {
		initBuffers();

		bulletSprite = new Sprite("assets/candy_corn.png");
	}

	private void initBuffers() {
		bulletsUsed = 0;
		bulletCapacity = 2048;

		bullet_buf = memAlloc(bulletCapacity * bulletSize);
		memSet(bullet_buf, 0);

		bullet_vacancies = new PriorityQueue<>();
	}

	// Attempts to add a bullet
	public boolean addBullet(Vector2fc position, Vector2fc velocity) {
		int ind = bulletVacancy();

		if (ind < 0) return false;

		putBullet(ind, position, velocity);
		this.bulletsUsed = this.bulletsUsed + 1;

		return true;
	}

	public void removeBullet(int index) {
		bullet_vacancies.add(index);

		bullet_buf.position(baseIndex(index));
		bullet_buf.put(bullet_empty);
		bullet_buf.position(0);
	}

	private int bulletVacancy() {
		if (!bullet_vacancies.isEmpty()) {
			return bullet_vacancies.poll();
		}

		if (bulletsUsed < bulletCapacity) {
			return bulletsUsed;
		}

		return -1;
	}

	// Puts new bullet into position
	private void putBullet(int index, Vector2fc position, Vector2fc velocity) {
		int val_ind = valIndex(index);
		if (bullet_buf.getChar(val_ind) != (char) 0)
			throw new RuntimeException("Putting bullet into supposedly occupied index");
		setBulletPos(index, position);
		setBulletVelocity(index, velocity);
		bullet_buf.putChar(val_ind, (char) 1);
	}

	// sets position and velocity of bullet at index
	private void setBulletVelocity(int index, Vector2fc velocity) {
		int vel_ind = velIndex(index);
		velocity.get(vel_ind, bullet_buf);
	}

	private void setBulletPos(int index, Vector2fc position) {
		int pos_ind = posIndex(index);
		position.get(pos_ind, bullet_buf);
	}

	private int baseIndex(int index) {
		return index * bulletSize;
	}

	private int posIndex(int index) {
		return baseIndex(index);
	}

	private int velIndex(int index) {
		return baseIndex(index) + 8;
	}

	private int valIndex(int index) {
		return baseIndex(index) + 16;
	}

	@Override
	public void tick(float dt) {
		// Advance velocities of bullets in here
		// Check for collision outside in the level code when implemented
		for (int i = 0; i < bulletCapacity; ++i) {
			int pos_ind = posIndex(i);
			int vel_ind = velIndex(i);
			int val_ind = valIndex(i);

			if (bullet_buf.getChar(val_ind) == (char) 0) continue;

			Vector2f pos = Utils.temp_v2f_0.set(pos_ind, bullet_buf);
			Vector2f vel = Utils.temp_v2f_1.set(vel_ind, bullet_buf);
			pos.add(vel.mul(dt));

			Vector2fc dim = gameInstance().getDimensions();
			int margin = 256;
			if (pos.x >= (dim.x() + margin) ||
					pos.x <= -(dim.x() + margin) ||
					pos.y >= (dim.y() + margin) ||
					pos.y <= -(dim.y() + margin)) {
				removeBullet(i);
				continue;
			}

			setBulletPos(i, pos);
		}
	}

	@Override
	public void render(float dt) {
		// For now, draw each individually
		for (int i = 0; i < bulletCapacity; ++i) {
			int pos_ind = posIndex(i);
			int vel_ind = velIndex(i);
			int val_ind = valIndex(i);

			if (bullet_buf.getChar(val_ind) == (char) 0) continue;

			Vector2f tmp = Utils.temp_v2f_0.set(1, 0);
			Vector2f vel = Utils.temp_v2f_1.set(vel_ind, bullet_buf);
			float angle = -vel.angle(tmp);

			Vector2f pos = Utils.temp_v2f_0.set(pos_ind, bullet_buf);

			bulletSprite.draw(pos, angle, 1.f, Utils.temp_m4f, Utils.temp_flbf);
		}
	}

	@Override
	public void close() {
		memFree(bullet_buf);
	}
}
