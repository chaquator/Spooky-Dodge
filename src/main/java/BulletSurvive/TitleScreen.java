package BulletSurvive;

import org.joml.Math;

import static org.lwjgl.glfw.GLFW.*;
import static BulletSurvive.BulletSurvive.*;

public class TitleScreen implements ILevel {
	private final char[] text = {'S', 'P', 'O', 'O', 'K', 'Y', 'D', 'O', 'D', 'G', 'E'};
	private final Letter[] text_ents = new Letter[text.length];

	public TitleScreen() {
		float ph_in = (float) Math.PI * 1.f / 5.f;
		for(int i = 0; i < text.length; ++i) {
			Letter l = text_ents[i] = new Letter(text[i], ph_in * i);
			float v_int = 80.f;
			float h_int = 112.f;
			int line = i/6;
			l.pos().set(line*(-3f*h_int + h_int * (i-5)) + (1-line)*(-2.5*h_int + h_int*i), line*(-v_int) + (1-line)*(v_int));
		}
	}

	@Override
	public void tick(float dt) {
		for(Letter l: text_ents) l.tick(dt);
		if(gameInstance().getKeyState(GLFW_KEY_ENTER)) gameInstance().signalLevel(LEVEL.IN_GAME);
	}

	@Override
	public void render(float dt) {
		for(Letter l: text_ents) l.render(dt);
	}

	@Override
	public void end() {
		for(Letter l: text_ents) l.close();
	}
}
