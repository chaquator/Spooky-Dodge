package BulletSurvive;

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

	char[] text = {'G', 'A', 'M', 'E', 'O', 'V', 'E', 'R'};
	Letter[] text_ents = new Letter[8];

	public GameOver() {
		// Create letters, set position
		// phase interval
		float ph_in = (float) Math.PI * 2.f / 3.f;
		for (int i = 0; i < 8; ++i) {
			Letter l = text_ents[i] = new Letter(text[i], ph_in * i);
			int h_i = i % 4;
			int v_i = i / 4;
			// intervals
			float v_int = 80.f;
			float h_int = 128.f;
			l.pos().set(-1.5 * h_int + h_int * h_i, v_int - 2 * v_int * v_i);
		}
	}

	private void clk() {
		if (state == 0) {
			if (!gameInstance().getKeyState(GLFW_KEY_ENTER)) state = 1;
		} else if (state == 1) {
			if(gameInstance().getKeyState(GLFW_KEY_ENTER)) gameInstance().signalLevel(LEVEL.IN_GAME);
			if(gameInstance().getKeyState(GLFW_KEY_T)) gameInstance().signalLevel(LEVEL.TITLE);
		}
	}

	@Override
	public void tick(float dt) {
		for (Letter l : text_ents) l.tick(dt);
		clk();
	}

	@Override
	public void render(float dt) {
		for (Letter l : text_ents) l.render(dt);
	}

	@Override
	public void end() {
		for (Letter l : text_ents) l.close();
	}
}
