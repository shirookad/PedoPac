package com.naronco.pedopac;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.util.*;

import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.*;

import com.naronco.pedopac.gameobjects.*;
import com.naronco.pedopac.level.*;
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
	private Random random = new Random();

	private LevelGenerator generator = new LevelGenerator(random);

	boolean hitG, hitN, hitB, hitH;

	private Car car;

	public GameScene() {
		diffuseShader = new Shader("diffuse", null);

		physicsWorld = new PhysicsWorld();
		levelMesh = ObjLoader.load("levels/level1_deco");

		addGameObject(car = new Car(Util.loadBinaryFile(Util
				.getResourceFileHandle("/CustomCar1.bin")), physicsWorld,
				Keyboard.KEY_A, Keyboard.KEY_D, Keyboard.KEY_RETURN,
				Keyboard.KEY_W, Keyboard.KEY_S, Keyboard.KEY_SPACE,
				ObjLoader.load("CustomCar1"), ObjLoader.load("Wheel"),
				TextureLoader.load("/CustomCar1.png"),
				TextureLoader.load("/CustomCar1.png")));
	}

	@Override
	public void debug() {
		System.out.println("Car Postion: " + car.position());
		System.out.println("Car Rotation: " + car.rotation());
	}

	@Override
	protected void update(float delta) {
		physicsWorld.update(delta);

		Matrix3f rotationMatrix = new Matrix3f();

		Vector3f forward = new Vector3f(0, 0, 1);
		Matrix3f.transform(rotationMatrix, forward, forward);

		cameraPosition = new Vector3f(car.position().x, 0, car.position().y);
		lookAt = new Vector3f(car.position().x, 0, car.position().y);

		cameraPosition.y += 40;

		forward.scale(5.0f);
		if (lastForward == null) {
			lastForward = forward;
		} else {
			float t = delta * 2.0f;
			lastForward = new Vector3f(lastForward.x * t + forward.x * (1 - t),
					lastForward.y * t + forward.y * (1 - t), lastForward.z * t
							+ forward.z * (1 - t));
		}

		if (hitG && !Keyboard.isKeyDown(Keyboard.KEY_G)) {
			generator.step();
		}
		hitG = Keyboard.isKeyDown(Keyboard.KEY_G);

		if (hitN && !Keyboard.isKeyDown(Keyboard.KEY_N)) {
			generator = new LevelGenerator(random);
		}
		hitN = Keyboard.isKeyDown(Keyboard.KEY_N);

		if (hitB && !Keyboard.isKeyDown(Keyboard.KEY_B)) {
			generator.simplify(1.6f);
		}
		hitB = Keyboard.isKeyDown(Keyboard.KEY_B);

		if (hitH && !Keyboard.isKeyDown(Keyboard.KEY_H)) {
			generator.subdivide();
			levelMesh = generator.generateMesh();
			generator.makePhysics(physicsWorld);
		}
		hitH = Keyboard.isKeyDown(Keyboard.KEY_H);

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
		Vector3f.sub(c, lookAt, c);
		c.normalise(c);

		gluLookAt(c.x, c.y, c.z, 0, 0, 0, 0, 1, 0);

		glDepthMask(false);
		skybox.render();
		glDepthMask(true);

		glLoadIdentity();

		gluLookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z,
				lookAt.x, lookAt.y, lookAt.z, 0, 1, 0);

		diffuseShader.use();

		levelMesh.render();

		glLineWidth(10);

		glBegin(GL_LINE_LOOP);

		for (int i = 0; i < generator.getDots().size(); i++)
			glVertex3f(generator.getDots().get(i).x, 0, generator.getDots()
					.get(i).y);

		glEnd();
	}

}
