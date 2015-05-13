package com.naronco.pedopac;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import javax.vecmath.*;

import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import com.bulletphysics.collision.shapes.*;
import com.naronco.pedopac.gameobjects.*;
import com.naronco.pedopac.physics.*;
import com.naronco.pedopac.rendering.*;

public class GameScene extends Scene {

	private Mesh levelMesh;

	private Shader diffuseShader;

	private Skybox skybox = new Skybox("/env_map.jpg");
	private PhysicsWorld physicsWorld;

	private Vector3f lastForward = null;
	private Vector3f cameraPosition = new Vector3f();
	private Vector3f lookAt = new Vector3f();

	private Car car;

	private com.bulletphysics.linearmath.Transform out = new com.bulletphysics.linearmath.Transform();

	public GameScene() {
		diffuseShader = new Shader("diffuse", null);

		physicsWorld = new PhysicsWorld();
		levelMesh = ObjLoader.load("levels/level3_deco");

		out.setIdentity();
		for (int x = -10; x < 11; x++) {
			for (int y = -10; y < 11; y++) {
				out.origin.set(x * 40, -20, y * 40);
				physicsWorld.addRigidBody(PhysicsWorld.createRigidBody(
						new BoxShape(new Vector3f(20, 20, 20)), 0, out));
			}
		}

		out.setIdentity();

		addGameObject(car = new Car(Util.loadBinaryFile(Util
				.getResourceFileHandle("/CustomCar1.bin")), physicsWorld,
				Keyboard.KEY_A, Keyboard.KEY_D, Keyboard.KEY_RETURN,
				Keyboard.KEY_W, Keyboard.KEY_S, Keyboard.KEY_SPACE,
				ObjLoader.load("CustomCar1"), ObjLoader.load("Wheel"),
				TextureLoader.load("/CustomCar1.png"),
				TextureLoader.load("/CustomCar1.png")));

		physicsWorld.addRigidBody(PhysicsWorld
				.createRigidBody(ObjLoader.load("levels/level3_collision")
						.buildCollisionShape(), 0));
	}

	@Override
	protected void update(float delta) {
		physicsWorld.update(delta);

		car.getTransform(out);

		float[] f = new float[16];
		out.getOpenGLMatrix(f);

		Matrix4f matrix = new Matrix4f(f);
		Matrix3f rotationMatrix = new Matrix3f();
		matrix.get(rotationMatrix);

		Vector3f forward = new Vector3f(0, 0, 1);
		rotationMatrix.transform(forward);

		cameraPosition.set(out.origin);
		lookAt.set(out.origin);

		cameraPosition.y += 3;

		forward.scale(5.0f);
		if (lastForward == null) {
			lastForward = forward;
		} else {
			lastForward.interpolate(forward, delta * 2.0f);
		}

		cameraPosition.x += lastForward.x;
		cameraPosition.z -= lastForward.z;
	}

	@Override
	protected void render() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(90.0f, Display.getWidth() / (float) Display.getHeight(),
				Shader.Z_NEAR, Shader.Z_FAR);

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		Vector3f c = new Vector3f(cameraPosition);
		c.sub(lookAt);
		c.normalize();

		gluLookAt(c.x, c.y, c.z, 0, 0, 0, 0, 1, 0);

		glDepthMask(false);
		skybox.render();
		glDepthMask(true);

		glLoadIdentity();
		gluLookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z,
				lookAt.x, lookAt.y, lookAt.z, 0, 1, 0);

		diffuseShader.use();

		levelMesh.render();
	}

}
