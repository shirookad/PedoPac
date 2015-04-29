package com.naronco.pedopac;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import org.lwjgl.opengl.*;

import com.naronco.pedopac.mesh.*;

public class Game {
	private Mesh carMesh;
	private Shader diffuseShader;
	private float rota = 0;

	public Game() {
		carMesh = ObjLoader.load("Hummer");
		diffuseShader = new Shader("diffuse");
		
		glEnable(GL_DEPTH_TEST);
	}

	public void update(float delta) {
		rota += delta * 50;
	}

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(90.0f, Display.getWidth() / (float) Display.getHeight(),
				0.01f, 100.0f);

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		gluLookAt(0, 2.5f, -5, 0, 0, 0, 0, 1, 0);

		diffuseShader.use();
		glRotatef(rota, 0, 1, 0);
		carMesh.render();
	}
}
