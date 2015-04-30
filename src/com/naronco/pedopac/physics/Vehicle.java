package com.naronco.pedopac.physics;

import javax.vecmath.*;

import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.vehicle.*;
import com.bulletphysics.linearmath.*;

public class Vehicle {
	private RigidBody carChassis;
	private VehicleRaycaster vehicleRayCaster;
	private RaycastVehicle vehicle;
	private VehicleTuning tuning = new VehicleTuning();
	
	private static final int rightIndex = 0;
	private static final int upIndex = 1;
	private static final int forwardIndex = 2;

	private static final Vector3f wheelDirectionCS0 = new Vector3f(0, -1, 0);
	private static final Vector3f wheelAxleCS = new Vector3f(-1, 0, 0);

	private static float gEngineForce = 0.f;
	private static float gBreakingForce = 0.f;

	private static float maxEngineForce = 1000.f;
	private static float maxBreakingForce = 100.f;

	private static float gVehicleSteering = 0.f;
	private static float steeringIncrement = 0.04f;
	private static float steeringClamp = 0.3f;
	private static float wheelFriction = 1000;
	private static float suspensionStiffness = 20.f;
	private static float suspensionDamping = 2.3f;
	private static float suspensionCompression = 4.4f;
	private static float rollInfluence = 0.1f;
	private static float wheelWidth = 0.381f;
	private static float wheelRadius = 0.391f;

	private static final float suspensionRestLength = 0.6f;

	public Vehicle() {
	}

	public RigidBody getChassis() {
		return vehicle.getRigidBody();
	}

	public void create(PhysicsWorld world) {
		Transform tr = new Transform();
		tr.setIdentity();
		CollisionShape chassisShape = new BoxShape(new Vector3f(3.0f, 1.0f, 1.0f));

		CompoundShape compound = new CompoundShape();
		Transform localTrans = new Transform();
		localTrans.setIdentity();
		localTrans.origin.set(0, 1, 0);

		compound.addChildShape(localTrans, chassisShape);

		tr.origin.set(0, 0, 0);

		carChassis = PhysicsWorld.createRigidBody(compound, 800, tr);
		world.addRigidBody(carChassis);

		gVehicleSteering = 0f;
		Transform tr2 = new Transform();
		tr2.setIdentity();
		tr2.origin.set(0, 1, 0);
		tr2.setRotation(new Quat4f(0.7071067811865476f, 0.7071067811865476f, 0, 0));
		carChassis.setCenterOfMassTransform(tr2);
		carChassis.setLinearVelocity(new Vector3f(0, 0, 0));
		carChassis.setAngularVelocity(new Vector3f(0, 0, 0));
		world.getDynamicsWorld().getBroadphase().getOverlappingPairCache().cleanProxyFromPairs(carChassis.getBroadphaseHandle(), world.getDynamicsWorld().getDispatcher());
		if (vehicle != null) {
			vehicle.resetSuspension();
			for (int i = 0; i < vehicle.getNumWheels(); i++) {
				vehicle.updateWheelTransform(i, true);
			}
		}

		{
			vehicleRayCaster = new DefaultVehicleRaycaster(world.getDynamicsWorld());
			vehicle = new RaycastVehicle(tuning, carChassis, vehicleRayCaster);

			carChassis.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

			world.getDynamicsWorld().addVehicle(vehicle);

			float h = 0.4f;

			boolean isFrontWheel = true;

			vehicle.setCoordinateSystem(rightIndex, upIndex, forwardIndex);

			vehicle.addWheel(new Vector3f(-2.5f, h, -1.0f + wheelWidth), wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);

			vehicle.addWheel(new Vector3f(-2.5f, h, 1.0f - wheelWidth), wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);

			isFrontWheel = false;
			vehicle.addWheel(new Vector3f(2.5f, h, -1.0f + wheelWidth), wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);

			vehicle.addWheel(new Vector3f(2.5f, h, 1.0f - wheelWidth), wheelDirectionCS0, wheelAxleCS, suspensionRestLength, wheelRadius, tuning, isFrontWheel);

			for (int i = 0; i < vehicle.getNumWheels(); i++) {
				WheelInfo wheel = vehicle.getWheelInfo(i);
				wheel.suspensionStiffness = suspensionStiffness;
				wheel.wheelsDampingRelaxation = suspensionDamping;
				wheel.wheelsDampingCompression = suspensionCompression;
				wheel.frictionSlip = wheelFriction;
				wheel.rollInfluence = rollInfluence;
			}
		}
	}

	public void steer(float amount) {
		gVehicleSteering += steeringIncrement;
		gVehicleSteering = -steeringClamp * amount;
	}

	public void accelerate(float amount) {
		gEngineForce = maxEngineForce * amount;
		gBreakingForce = 0.f;
	}

	public void slow(float amount) {
		gBreakingForce = maxBreakingForce * amount;
		gEngineForce = 0.f;
	}

	public void update() {
		int wheelIndex = 2;
		vehicle.applyEngineForce(gEngineForce, wheelIndex);
		vehicle.setBrake(gBreakingForce, wheelIndex);
		wheelIndex = 3;
		vehicle.applyEngineForce(gEngineForce, wheelIndex);
		vehicle.setBrake(gBreakingForce, wheelIndex);

		wheelIndex = 0;
		vehicle.setSteeringValue(gVehicleSteering, wheelIndex);
		wheelIndex = 1;
		vehicle.setSteeringValue(gVehicleSteering, wheelIndex);
	}
	
	public Transform getTransform(Transform t)
	{
		return vehicle.getChassisWorldTransform(t);
	}
	
	public Transform getWheelTransform(int index, Transform t)
	{
		return vehicle.getWheelTransformWS(index, t);
	}
}
