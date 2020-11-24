package BulletSurvive;

import static BulletSurvive.BulletSurvive.*;
import static org.lwjgl.glfw.GLFW.*;

import org.joml.Math;

public class InGame implements ILevel {

	// Entities
	Player ply;
	Boss boss;
	PlyGhost plyGhost;

	private final char[] text = {'P', 'A', 'U', 'S', 'E', 'D'};
	private final Letter[] text_ents = new Letter[text.length];
	private float player_score;

	private enum STATE {
		PAUSED_0,
		PAUSED_1,
		PLAY_0,
		PLAY_1,
    /*
		Paused 0 - pause key up -> paused 1
		Paused 1 - pause key down -> play 0
		Play 0 - pause key up -> play 1
		Play 1 - pause key down -> paused 0
		 */
	}

	STATE state = STATE.PLAY_0;
	int pause = GLFW_KEY_P;

	public InGame() {
		ply = new Player();
		boss = new Boss();
		plyGhost = new PlyGhost();

		this.player_score = 0;

		boss.setPlyPosHandle(ply.pos());

		float ph_in = (float) Math.PI * 1.f / 3.f;
		for (int i = 0; i < text.length; ++i) {
			Letter l = text_ents[i] = new Letter(text[i], ph_in * i);
			float h_inv = 128.f;
			l.pos().set(-h_inv * 2.5 + h_inv * i, 0);
		}
	}

	private void clk() {
		switch (state) {
			case PAUSED_0:
				if (!gameInstance().getKeyState(pause)) {
					state = STATE.PAUSED_1;
				}
				break;
			case PAUSED_1:
				if (gameInstance().getKeyState(pause)) {
					state = STATE.PLAY_0;
				}
				break;
			case PLAY_0:
				if (!gameInstance().getKeyState(pause)) {
					state = STATE.PLAY_1;
				}
				break;
			case PLAY_1:
				if (gameInstance().getKeyState(pause)) {
					state = STATE.PAUSED_0;
				}
				break;
		}
	}

	@Override
	public void tick(float dt) {
		clk();
		switch (state) {
			case PLAY_0:
			case PLAY_1:
				ply.tick(dt);
				plyGhost.setPos(ply.pos());
				boss.tick(dt);

				if (boss.bulletCollide(ply.pos(), ply.radius())) {
					gameInstance()
							.signalLevel(LEVEL.GAME_OVER, (int) this.player_score * 100);
				}
				this.player_score += dt;
				break;
			case PAUSED_0:
			case PAUSED_1:
				for (Letter l : text_ents) l.tick(dt);
				break;
		}
	}

	@Override
	public void render(float dt) {
		switch (state) {
			case PLAY_0:
			case PLAY_1:
				plyGhost.render(dt);
				boss.render(dt);
				ply.render(dt);
				break;
			case PAUSED_0:
			case PAUSED_1:
				plyGhost.render(dt);
				boss.render(dt);
				ply.render(dt);
				for (Letter l : text_ents) l.render(dt);
				break;
		}
	}

	@Override
	public void end() {
		ply.close();
		boss.close();
		plyGhost.close();
		for (Letter l : text_ents) l.close();
	}
}
