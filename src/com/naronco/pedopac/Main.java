package com.naronco.pedopac;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

public class Main {

	public static void start() {
		try {
			Display.setDisplayMode(new DisplayMode(1200, 750));
			Display.setTitle("Naronco™ RC Racers");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		Game game = new Game();

		long time = System.currentTimeMillis();

		long lastFinishedSecond = System.currentTimeMillis();
		int frames = 0;
		int skipms = 1000 / 58;
		long next = System.currentTimeMillis();
		long sleep = 0;

		while (!Display.isCloseRequested()) {
			long now = System.currentTimeMillis();

			game.update((now - time) * 0.001f);
			game.render();
			++frames;

			if ((now - lastFinishedSecond) >= 1000) {
				game.debug();
				System.out.println(frames + " FPS");
				frames = 0;
				lastFinishedSecond = now;
			}

			Display.update();
			time = now;
			
			next += skipms;
			sleep = next - System.currentTimeMillis();
			if(sleep > 0)
			{
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		Display.destroy();
	}

	public static void main(String[] args) {
		start();
	}

}
