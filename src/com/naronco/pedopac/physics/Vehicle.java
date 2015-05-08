package com.naronco.pedopac.physics;

import java.nio.*;

import javax.vecmath.*;

import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.vehicle.*;
import com.bulletphysics.dynamics.vehicle.WheelInfo;
import com.bulletphysics.linearmath.*;
import com.naronco.pedopac.schemas.*;

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

	public float engineForce = 0.f;
	public float breakingForce = 0.f;
	public float vehicleSteering = 0.f;

	public float steeringIncrement, steeringClamp, maxEngineForce,
			maxBreakingForce;

	public VehicleInfo info;

	public Vehicle(byte[] data) {
		info = VehicleInfo.getRootAsVehicleInfo(ByteBuffer.wrap(data));
	}

	public RigidBody getChassis() {
		return vehicle.getRigidBody();
	}

	public void create(PhysicsWorld world) {
		Transform tr = new Transform();
		tr.setIdentity();
		CollisionShape chassisShape = new BoxShape(new Vector3f(1.0f, 0.3f,
				3.0f));

		CompoundShape compound = new CompoundShape();
		Transform localTrans = new Transform();
		localTrans.setIdentity();
		localTrans.origin.set(0, info.offsetHeight(), 0);

		compound.addChildShape(localTrans, chassisShape);

		tr.origin.set(0, 0, 0);

		carChassis = PhysicsWorld.createRigidBody(compound, info.mass(), tr);
		world.addRigidBody(carChassis);

		vehicleSteering = 0f;
		Transform tr2 = new Transform();
		tr2.setIdentity();
		tr2.origin.set(0, 1.0f, 0);
		carChassis.setCenterOfMassTransform(tr2);
		carChassis.setLinearVelocity(new Vector3f(0, 0, 0));
		carChassis.setAngularVelocity(new Vector3f(0, 0, 0));
		world.getDynamicsWorld()
				.getBroadphase()
				.getOverlappingPairCache()
				.cleanProxyFromPairs(carChassis.getBroadphaseHandle(),
						world.getDynamicsWorld().getDispatcher());
		if (vehicle != null) {
			vehicle.resetSuspension();
			for (int i = 0; i < vehicle.getNumWheels(); i++) {
				vehicle.updateWheelTransform(i, true);
			}
		}

		vehicleRayCaster = new DefaultVehicleRaycaster(world.getDynamicsWorld());
		vehicle = new RaycastVehicle(tuning, carChassis, vehicleRayCaster);

		tuning.maxSuspensionTravelCm = (float) info.maxSuspensionTravelCm();
		tuning.frictionSlip = (float) info.frictionSlip();
		tuning.suspensionCompression = (float) info.suspensionCompression();
		tuning.suspensionDamping = (float) info.suspensionDamping();
		tuning.suspensionStiffness = (float) info.suspensionStiffness();

		carChassis.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

		world.getDynamicsWorld().addVehicle(vehicle);

		vehicle.setCoordinateSystem(rightIndex, upIndex, forwardIndex);

		for (int i = 0; i < info.wheelsLength(); i++) {
			VehicleWheelInfo w = info.wheels(i);
			WheelInfo wheel = vehicle.addWheel(new Vector3f(w.position().x(), w
					.position().y(), w.position().z()), wheelDirectionCS0,
					wheelAxleCS, (float) info.suspensionRestLength(), (float) w
							.wheelRadius(), tuning, !w.isFront());
			wheel.rollInfluence = (float) info.rollInfluence();
		}

		/*
		 * vehicle.addWheel(new Vector3f(-1.0f, h, 2.2f), wheelDirectionCS0,
		 * wheelAxleCS, (float)info.suspensionRestLength(), wheelRadius, tuning,
		 * isFrontWheel);
		 * 
		 * vehicle.addWheel(new Vector3f(1.0f, h, 2.2f), wheelDirectionCS0,
		 * wheelAxleCS, (float)info.suspensionRestLength(), wheelRadius, tuning,
		 * isFrontWheel);
		 * 
		 * isFrontWheel = false; vehicle.addWheel(new Vector3f(-1.0f, h, -1.7f),
		 * wheelDirectionCS0, wheelAxleCS, (float)info.suspensionRestLength(),
		 * wheelRadius, tuning, isFrontWheel);
		 * 
		 * vehicle.addWheel(new Vector3f(1.0f, h, -1.7f), wheelDirectionCS0,
		 * wheelAxleCS, (float)info.suspensionRestLength(), wheelRadius, tuning,
		 * isFrontWheel);
		 */

		steeringIncrement = (float) info.steeringIncrement();
		steeringClamp = (float) info.steeringClamp();
		maxBreakingForce = (float) info.breakForce();
		maxEngineForce = (float) info.engineForce();
	}

	public void steer(float amount) {
		if (amount == 0)
			vehicleSteering *= 0.5f;
		else
			vehicleSteering -= amount * steeringIncrement;

		if (vehicleSteering > steeringClamp)
			vehicleSteering = steeringClamp;

		if (vehicleSteering < -steeringClamp)
			vehicleSteering = -steeringClamp;
	}

	public void accelerate(float amount) {
		engineForce = maxEngineForce * amount;
		breakingForce = 0.f;
	}

	public void slow(float amount) {
		breakingForce = maxBreakingForce * amount;
		engineForce = 0.f;
	}

	public void update() {
		for (int i = 0; i < vehicle.getNumWheels(); i++) {
			vehicle.applyEngineForce(engineForce, i);
			vehicle.setBrake(breakingForce, i);
			if (vehicle.getWheelInfo(i).bIsFrontWheel)
				vehicle.setSteeringValue(vehicleSteering, i);
		}
	}

	public Transform getTransform(Transform t) {
		return vehicle.getChassisWorldTransform(t);
	}

	public Transform getWheelTransform(int index, Transform t) {
		return vehicle.getWheelTransformWS(index, t);
	}
}
