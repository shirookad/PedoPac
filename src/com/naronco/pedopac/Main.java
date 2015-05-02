package com.naronco.pedopac;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

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

		while (!Display.isCloseRequested()) {
			long now = System.currentTimeMillis();
			
			game.update((now - time) * 0.001f);
			game.render();
			
			Display.update();
			time = now;
		}

		Display.destroy();
	}

	public static void main(String[] args) {
		start();
	}

}
