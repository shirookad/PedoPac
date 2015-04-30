package com.naronco.pedopac.physics;

import javax.vecmath.*;

import com.bulletphysics.collision.broadphase.*;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.constraintsolver.*;
import com.bulletphysics.linearmath.*;

public class PhysicsWorld {
	private DiscreteDynamicsWorld dynamicsWorld;

	public PhysicsWorld() {
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(
				collisionConfiguration);

		Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
		Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
		int maxProxies = 1024;
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin,
				worldAabbMax, maxProxies);

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher,
				overlappingPairCache, solver, collisionConfiguration);

		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
	}

	public static RigidBody createRigidBody(CollisionShape shape, float mass) {
		Vector3f localInertia = new Vector3f(0, 0, 0);
		if (mass != 0)
			shape.calculateLocalInertia(mass, localInertia);

		DefaultMotionState myMotionState = new DefaultMotionState();
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass,
				myMotionState, shape, localInertia);

		return new RigidBody(rbInfo);
	}

	public static RigidBody createRigidBody(CollisionShape shape, float mass,
			Transform start) {
		Vector3f localInertia = new Vector3f(0, 0, 0);
		if (mass != 0)
			shape.calculateLocalInertia(mass, localInertia);

		DefaultMotionState myMotionState = new DefaultMotionState(start);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass,
				myMotionState, shape, localInertia);

		return new RigidBody(rbInfo);
	}

	public DynamicsWorld getDynamicsWorld() {
		return dynamicsWorld;
	}

	public void addRigidBody(RigidBody rigidBody) {
		dynamicsWorld.addRigidBody(rigidBody);
	}

	public void update(float delta) {
		dynamicsWorld.stepSimulation(delta, 10);
	}
}
