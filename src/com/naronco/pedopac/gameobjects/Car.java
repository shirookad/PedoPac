package com.naronco.pedopac.gameobjects;

import static org.lwjgl.opengl.GL11.*;

import java.nio.*;

import javax.vecmath.*;

import org.lwjgl.*;
import org.lwjgl.input.*;

import com.naronco.pedopac.*;
import com.naronco.pedopac.physics.*;
import com.naronco.pedopac.rendering.*;
import com.naronco.pedopac.schemas.*;

public class Car extends GameObject {

	private Vehicle vehicle;
	private com.bulletphysics.linearmath.Transform out = new com.bulletphysics.linearmath.Transform();

	private int leftKey, rightKey, respawnKey, accelerateKey, reverseKey, breakKey;
	private boolean wasRespawnDown;
	private Texture2D carTexture, wheelTexture;
	private Mesh carMesh, wheelMesh;
	private FloatBuffer fbuf = BufferUtils.createFloatBuffer(16);

	public Car(byte[] data, PhysicsWorld world, int leftKey, int rightKey,
			int respawnKey, int accelerateKey, int reverseKey, int breakKey,
			Mesh carMesh, Mesh wheelMesh, Texture2D carTexture,
			Texture2D wheelTexture) {
		vehicle = new Vehicle(Util.loadBinaryFile(Util
				.getResourceFileHandle("/CustomCar1.bin")));
		vehicle.create(world);

		out.setIdentity();
		out.origin.set(0, 18, 0);
		out.setRotation(new Quat4f(0, 1, 0, 0));
		vehicle.rigidBody().setCenterOfMassTransform(out);

		this.leftKey = leftKey;
		this.rightKey = rightKey;
		this.respawnKey = respawnKey;
		this.accelerateKey = accelerateKey;
		this.reverseKey = reverseKey;
		this.breakKey = breakKey;

		this.carTexture = carTexture;
		this.wheelTexture = wheelTexture;
		this.carMesh = carMesh;
		this.wheelMesh = wheelMesh;
	}

	@Override
	protected void update(float delta) {
		if (Keyboard.isKeyDown(leftKey)) {
			vehicle.steer(-1);
		} else if (Keyboard.isKeyDown(rightKey)) {
			vehicle.steer(1);
		} else {
			vehicle.steer(0);
		}

		if (!Keyboard.isKeyDown(respawnKey) && wasRespawnDown) {
			out.setIdentity();
			out.origin.set(0, 18, 0);
			out.setRotation(new Quat4f(0, 1, 0, 0));
			vehicle.rigidBody().setAngularVelocity(new Vector3f());
			vehicle.rigidBody().setLinearVelocity(new Vector3f());
			vehicle.rigidBody().setCenterOfMassTransform(out);
		}
		wasRespawnDown = Keyboard.isKeyDown(respawnKey);

		if (Keyboard.isKeyDown(accelerateKey)) {
			vehicle.accelerate(1);
		} else if (Keyboard.isKeyDown(reverseKey)) {
			vehicle.accelerate(-1);
		} else if (Keyboard.isKeyDown(breakKey)) {
			vehicle.slow(1);
		} else {
			vehicle.accelerate(0);
		}
		vehicle.update();
	}

	@Override
	protected void render() {
		vehicle.getTransform(out);

		glPushMatrix();
		{
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			glTranslatef(0, vehicle.info.height(), 0);
			carTexture.bind();
			carMesh.render();
		}
		glPopMatrix();

		for (int i = 0; i < vehicle.info.wheelsLength(); i++) {
			glPushMatrix();

			vehicle.getWheelTransform(i, out);
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelTexture.bind();
			wheelMesh.render();

			glPopMatrix();
		}
	}

	public com.bulletphysics.linearmath.Transform getTransform(
			com.bulletphysics.linearmath.Transform transform) {
		return vehicle.getTransform(transform);
	}

	public com.bulletphysics.linearmath.Transform getTransform(int index,
			com.bulletphysics.linearmath.Transform transform) {
		return vehicle.getWheelTransform(index, transform);
	}

	public VehicleInfo info() {
		return vehicle.info;
	}

}
