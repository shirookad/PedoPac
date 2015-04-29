package com.naronco.pedopac;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

public class Main {

	public static void start() {
		try {
			Display.setDisplayMode(new DisplayMode(800, 480));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		Game game = new Game();

		long time = System.currentTimeMillis();

		long lastFinishedSecond = System.currentTimeMillis();
		int frames = 0;

		while (!Display.isCloseRequested()) {
			long now = System.currentTimeMillis();

			game.update((now - time) * 0.001f);
			game.render();
			++frames;

			if ((now - lastFinishedSecond) >= 1000) {
				System.out.println(frames + " FPS");
				frames = 0;
				lastFinishedSecond = now;
			}

			Display.update();
			time = now;
		}

		Display.destroy();
	}

	public static void main(String[] args) {
		start();
	}

}
