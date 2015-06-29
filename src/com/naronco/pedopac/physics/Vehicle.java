package com.naronco.pedopac.physics;

import java.nio.*;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

import com.naronco.pedopac.schemas.*;

public class Vehicle {
	private Body carChassis;

	public VehicleInfo info;

	public Vehicle(byte[] data) {
		info = VehicleInfo.getRootAsVehicleInfo(ByteBuffer.wrap(data));
	}

	public Body rigidBody() {
		return carChassis;
	}

	public void create(PhysicsWorld world) {
		BodyDef bodyDef = PhysicsWorld.createRigidBodyDefinition(800);
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.allowSleep = false;
		bodyDef.fixedRotation = false;
		EdgeShape shape = new EdgeShape();
		shape.set(new Vec2(-2, -3), new Vec2(2, 3));
		FixtureDef fixDef = PhysicsWorld.createFixtureDefinition(shape);
		fixDef.restitution = 0.01f;
		fixDef.friction = 0.9f;
		fixDef.density = 800;
		bodyDef.bullet = true;
		carChassis = world.addRigidBody(bodyDef, fixDef);
	}

	public void steer(float amount) {
		carChassis.m_angularVelocity = amount * -100;
	}

	public void accelerate(float amount) {
		carChassis.applyLinearImpulse(new Vec2((float)Math.sin(carChassis.getAngle() * 0.0174532925) * amount, (float)Math.cos(carChassis.getAngle() * 0.0174532925) * amount), new Vec2(0, 0), true);
	}
	
	public void update(float delta) {
		carChassis.m_linearVelocity.x *= 0.992f;
		carChassis.m_linearVelocity.y *= 0.992f;
	}

	public void slow() {
		carChassis.m_linearVelocity.x *= 0.95f;
		carChassis.m_linearVelocity.y *= 0.95f;
	}
}
