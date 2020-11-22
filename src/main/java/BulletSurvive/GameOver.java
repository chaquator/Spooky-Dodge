package BulletSurvive;

import org.joml.*;

import org.joml.Math;

import static BulletSurvive.BulletSurvive.*;
import static org.lwjgl.glfw.GLFW.*;

public class GameOver implements ILevel {

	/*
		State diagram - entry point 0:
		0 - waiting for release of enter, on release of enter go to 1
		1 - waiting for press of enter, on press of enter go to new level
	 */
	int state = 0;

	char[] letters = {'G', 'A', 'M', 'E', 'O', 'V', 'E', 'R'};
	Letter[] letterEnts = new Letter[8];

	public GameOver() {
		// Create letters, set position
		// phase interval
		float ph_in = (float) Math.PI * 2.f / 3.f;
		for (int i = 0; i < 8; ++i) {
			Letter l = letterEnts[i] = new Letter(letters[i], ph_in * i);
			int h_i = i % 4;
			int v_i = i / 4;
			// intervals
			float v_int = 88.f;
			float h_int = 120.f;
			l.pos().set(-1.5 * h_int + h_int * h_i, v_int - 2 * v_int * v_i);
		}
	}

	private void clk() {
		if (state == 0) {
			if (!gameInstance().getKeyState(GLFW_KEY_ENTER)) state = 1;
		} else if (state == 1) {
			if(gameInstance().getKeyState(GLFW_KEY_ENTER)) gameInstance().signalLevel(LEVEL.IN_GAME);
		}
	}

	@Override
	public void tick(float dt) {
		for (Letter l : letterEnts) l.tick(dt);
		clk();
	}

	@Override
	public void render(float dt) {
		for (Letter l : letterEnts) l.render(dt);
	}

	@Override
	public void end() {
		for (Letter l : letterEnts) l.close();
	}
}
