package com.naronco.pedopac;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.*;

import com.naronco.pedopac.mesh.*;
import com.naronco.pedopac.physics.*;

public class Game {
	private PhysicsWorld physicsWorld;
	private Mesh carMesh;
	private Transform carTransform;
	private FloatBuffer fbuf;
	private Mesh levelMesh;
	private Shader diffuseShader;

	public Game() {
		physicsWorld = new PhysicsWorld();
		carMesh = ObjLoader.load("Hummer");
		levelMesh = ObjLoader.load("levels/level1_deco");
		carTransform = new Transform();
		diffuseShader = new Shader("diffuse");
		fbuf = BufferUtils.createFloatBuffer(16);

		glEnable(GL_DEPTH_TEST);
	}

	public void update(float delta) {
		if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			Quaternion q = new Quaternion();
			q.setFromAxisAngle(new Vector4f(0, 1, 0, delta));
			Quaternion out = new Quaternion();
			Quaternion.mul(carTransform.getRotation(), q, out);
			carTransform.setRotation(out);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			Quaternion q = new Quaternion();
			q.setFromAxisAngle(new Vector4f(0, 1, 0, -delta));
			Quaternion out = new Quaternion();
			Quaternion.mul(carTransform.getRotation(), q, out);
			carTransform.setRotation(out);
		}
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

		glPushMatrix();
		{
			diffuseShader.use();
			carTransform.getMatrix().store(fbuf);
			fbuf.flip();
			glMultMatrix(fbuf);
			carMesh.render();
		}
		glPopMatrix();

		levelMesh.render();
	}
}
