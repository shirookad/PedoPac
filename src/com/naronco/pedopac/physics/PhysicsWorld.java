package com.naronco.pedopac.physics;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

public class PhysicsWorld {
	private World dynamicsWorld;

	public PhysicsWorld() {
		dynamicsWorld = new World(new Vec2(0, 0));
	}

	public static BodyDef createRigidBodyDefinition(float mass) {
		BodyDef def = new BodyDef();
		if (mass <= 0.0001f)
			def.type = BodyType.STATIC;
		else
			def.type = BodyType.DYNAMIC;
		
		return def;
	}
	
	public static FixtureDef createFixtureDefinition(Shape shape) {
		FixtureDef fix = new FixtureDef();
		fix.shape = shape;
		return fix;
	}

	public World getDynamicsWorld() {
		return dynamicsWorld;
	}

	public Body addRigidBody(BodyDef rigidBody, FixtureDef fixture) {
		Body body = dynamicsWorld.createBody(rigidBody);
		body.createFixture(fixture);
		return body;
	}

	public void update(float delta) {
		dynamicsWorld.step(delta, 10, 10);
	}
}
