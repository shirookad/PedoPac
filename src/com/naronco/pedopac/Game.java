package com.naronco.pedopac;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Game {
	private Mesh carMesh;
	private Shader diffuseShader;

	public Game() {
		carMesh = new Mesh(new Vertex[] {
				new Vertex(new Vector3f(-1, -1, 0), new Vector4f(1, 0, 0, 1)),
				new Vertex(new Vector3f(0, 1, 0), new Vector4f(0, 1, 0, 1)),
				new Vertex(new Vector3f(1, -1, 0), new Vector4f(0, 0, 1, 1)) },
				new int[] { 0, 1, 2 });
		diffuseShader = new Shader("diffuse");
	}

	public void update(float delta) {

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
		carMesh.render();
	}
}
