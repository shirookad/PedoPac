package com.naronco.pedopac.gameobjects;

import java.nio.*;

import static org.lwjgl.opengl.GL11.*;

import org.jbox2d.common.*;
import org.jbox2d.common.Transform;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.*;

import com.naronco.pedopac.*;
import com.naronco.pedopac.physics.*;
import com.naronco.pedopac.rendering.*;
import com.naronco.pedopac.schemas.*;

public class Car extends GameObject {

	private Vehicle vehicle;

	private int leftKey, rightKey, respawnKey, accelerateKey, reverseKey, breakKey;
	private boolean wasRespawnDown;
	private Texture2D carTexture, wheelTexture;
	private Mesh carMesh, wheelMesh;

	public Car(byte[] data, PhysicsWorld world, int leftKey, int rightKey,
			int respawnKey, int accelerateKey, int reverseKey, int breakKey,
			Mesh carMesh, Mesh wheelMesh, Texture2D carTexture,
			Texture2D wheelTexture) {
		vehicle = new Vehicle(Util.loadBinaryFile(Util
				.getResourceFileHandle("/CustomCar1.bin")));
		vehicle.create(world);

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
			vehicle.rigidBody().setTransform(new Vec2(0, 0), 0);
			vehicle.rigidBody().setAngularVelocity(0);
			vehicle.rigidBody().setLinearVelocity(new Vec2(0, 0));
		}
		wasRespawnDown = Keyboard.isKeyDown(respawnKey);

		if (Keyboard.isKeyDown(accelerateKey)) {
			vehicle.accelerate(delta * 100);
		} else if (Keyboard.isKeyDown(reverseKey)) {
			vehicle.accelerate(-delta * 100);
		}
		
		if (Keyboard.isKeyDown(breakKey)) {
			vehicle.slow();
		}
		
		vehicle.update(delta);
	}

	@Override
	protected void render() {
		glPushMatrix();
		{
			Transform t = vehicle.rigidBody().getTransform();
			glTranslatef(t.p.x, 0, t.p.y);
			glRotatef(vehicle.rigidBody().getAngle(), 0, 1, 0);
			carTexture.bind();
			carMesh.render();
		}
		glPopMatrix();
	}
	
	public float rotation() {
		return vehicle.rigidBody().getAngle();
	}
	
	public Vec2 position() {
		return vehicle.rigidBody().getTransform().p;
	}

	public VehicleInfo info() {
		return vehicle.info;
	}
}
