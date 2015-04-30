package com.naronco.pedopac;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.nio.*;

import javax.vecmath.*;

import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.naronco.pedopac.mesh.*;
import com.naronco.pedopac.physics.*;

public class Game {
	private PhysicsWorld physicsWorld;
	private Mesh carMesh;
	private Transform carTransform;
	private FloatBuffer fbuf;
	private Mesh levelMesh;
	private Shader diffuseShader;
	private RigidBody cube;
	private Vehicle vehicle;

	public Game() {
		physicsWorld = new PhysicsWorld();
		carMesh = ObjLoader.load("Hummer");
		levelMesh = ObjLoader.load("levels/level1_deco");
		carTransform = new Transform();
		diffuseShader = new Shader("diffuse");
		fbuf = BufferUtils.createFloatBuffer(16);

		physicsWorld.addRigidBody(PhysicsWorld.createRigidBody(
				new StaticPlaneShape(new Vector3f(0, 1, 0), 0), 0));
		
		vehicle = new Vehicle();
		vehicle.create(physicsWorld);

		glEnable(GL_DEPTH_TEST);
	}

	public void update(float delta) {
		if(Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			vehicle.accelerate(1);
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			vehicle.slow(1);
		}
		else
		{
			vehicle.accelerate(0);
		}
		
		physicsWorld.update(delta);
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
			com.bulletphysics.linearmath.Transform out = new com.bulletphysics.linearmath.Transform();
			vehicle.getChassis().getCenterOfMassTransform(out);
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			glRotatef(180, 1, 0, 0);
			glTranslatef(0, -1, 0);
			carMesh.render();
		}
		glPopMatrix();

		levelMesh.render();
	}
}
