package com.naronco.pedopac.physics;

import java.nio.*;

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

	public static RigidBody createHeightmap(float[][] data, int width,
			int height, float xzScale) {
		ByteBuffer vertices;

		int vertStride = 4 * 3 /* sizeof(btVector3) */;
		int indexStride = 3 * 4 /* 3*sizeof(int) */;

		final int totalVerts = width * height;

		final int totalTriangles = 2 * (width - 1) * (height - 1);

		vertices = ByteBuffer.allocateDirect(totalVerts * vertStride).order(
				ByteOrder.nativeOrder());
		ByteBuffer gIndices = ByteBuffer.allocateDirect(totalTriangles * 3 * 4)
				.order(ByteOrder.nativeOrder());

		Vector3f tmp = new Vector3f();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				float wl = 0.2f;
				tmp.set((i - width * 0.5f) * xzScale, data[i][j],
						(j - height * 0.5f) * xzScale);

				int index = i + j * width;
				vertices.putFloat((index * 3 + 0) * 4, tmp.x);
				vertices.putFloat((index * 3 + 1) * 4, tmp.y);
				vertices.putFloat((index * 3 + 2) * 4, tmp.z);
			}
		}

		gIndices.clear();
		for (int i = 0; i < width - 1; i++) {
			for (int j = 0; j < height - 1; j++) {
				gIndices.putInt(j * width + i);
				gIndices.putInt(j * width + i + 1);
				gIndices.putInt((j + 1) * width + i + 1);

				gIndices.putInt(j * width + i);
				gIndices.putInt((j + 1) * width + i + 1);
				gIndices.putInt((j + 1) * width + i);
			}
		}
		gIndices.flip();

		TriangleIndexVertexArray indexVertexArrays = new TriangleIndexVertexArray(
				totalTriangles, gIndices, indexStride, totalVerts, vertices,
				vertStride);

		BvhTriangleMeshShape groundShape = new BvhTriangleMeshShape(
				indexVertexArrays, true);

		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set((width - 1) * 0.5f * xzScale, 0, (height - 1)
				* 0.5f * xzScale);

		return createRigidBody(groundShape, 0, transform);

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
