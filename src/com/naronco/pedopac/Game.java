package com.naronco.pedopac;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.nio.*;

import javax.vecmath.*;

import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import com.bulletphysics.collision.shapes.*;
import com.naronco.pedopac.physics.*;
import com.naronco.pedopac.rendering.*;

public class Game {
	private PhysicsWorld physicsWorld;
	private Mesh carMesh, wheelMesh, levelMesh;
	private FloatBuffer fbuf;
	private Shader diffuseShader;
	private Vehicle vehicle;
	private com.bulletphysics.linearmath.Transform out = new com.bulletphysics.linearmath.Transform();

	public Game() {
		physicsWorld = new PhysicsWorld();
		carMesh = ObjLoader.load("Hummer");
		wheelMesh = ObjLoader.load("Wheel");
		levelMesh = ObjLoader.load("levels/level1_deco");
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
			vehicle.accelerate(-1);
		}
		else
		{
			vehicle.accelerate(0);
		}
		vehicle.update();
		
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

		vehicle.getTransform(out);

		gluLookAt(out.origin.x + 5, out.origin.y + 2, out.origin.z, out.origin.x, out.origin.y, out.origin.z, 0, 1, 0);

		glPushMatrix();
		{
			diffuseShader.use();
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			carMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			vehicle.getWheelTransform(0, out);
			diffuseShader.use();
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			vehicle.getWheelTransform(1, out);
			diffuseShader.use();
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			vehicle.getWheelTransform(2, out);
			diffuseShader.use();
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			vehicle.getWheelTransform(3, out);
			diffuseShader.use();
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelMesh.render();
		}
		glPopMatrix();

		levelMesh.render();
	}
}
