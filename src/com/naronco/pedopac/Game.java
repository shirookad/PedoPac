package com.naronco.pedopac;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.util.glu.GLU.*;

import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;

import javax.imageio.*;
import javax.vecmath.*;

import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.naronco.pedopac.physics.*;
import com.naronco.pedopac.rendering.*;

public class Game {
	private static final int SSAO_KERNEL_SIZE = 4; // Must be 2^x

	private Framebuffer screenBuffer;
	private Texture2D[] geometryTextures;
	private Framebuffer geometryBuffer;

	private Mesh quadMesh = new Mesh(new Vertex[] {
			new Vertex(-1, -1, 0, 0, 0), new Vertex(1, -1, 0, 1, 0),
			new Vertex(1, 1, 0, 1, 1), new Vertex(-1, 1, 0, 0, 1) }, new int[] {
			0, 1, 2, 0, 2, 3 });

	private PhysicsWorld physicsWorld;
	private Mesh carMesh, wheelMesh, levelMesh, heightmap;
	private RigidBody heightBody;
	private FloatBuffer fbuf;

	private Shader textureShader;
	private Shader diffuseShader;
	private PostProcessShader deferredLightingShader;
	private PostProcessShader ssaoShader;

	private PostProcessShader horizontalGaussianBlurShader;
	private PostProcessShader verticalGaussianBlurShader;

	private Texture2D ssaoOutput, horizontalGaussianBlurOutput;

	private Vector2f[] kernel;

	private Texture2D randomTexture;
	private Texture2D kernelTexture;

	private Vehicle vehicle;
	private com.bulletphysics.linearmath.Transform out = new com.bulletphysics.linearmath.Transform();

	public Game() {
		screenBuffer = new Framebuffer(0);

		int width = Display.getWidth();
		int height = Display.getHeight();

		geometryTextures = new Texture2D[3];
		geometryTextures[0] = new Texture2D(width, height, GL_RGBA8, GL_RGBA,
				(ByteBuffer) null);
		geometryTextures[1] = new Texture2D(width, height, GL_RGBA8, GL_RGBA,
				(ByteBuffer) null);
		geometryTextures[2] = new Texture2D(width, height,
				GL_DEPTH_COMPONENT32F, GL_DEPTH_COMPONENT, (FloatBuffer) null);

		try {
			Map<Integer, Texture2D> textures = new HashMap<Integer, Texture2D>();
			textures.put(GL_COLOR_ATTACHMENT0, geometryTextures[0]);
			textures.put(GL_COLOR_ATTACHMENT1, geometryTextures[1]);
			textures.put(GL_DEPTH_ATTACHMENT, geometryTextures[2]);
			geometryBuffer = new Framebuffer(textures);
		} catch (Exception e) {
			e.printStackTrace();
		}

		physicsWorld = new PhysicsWorld();
		carMesh = ObjLoader.load("Hummer");
		wheelMesh = ObjLoader.load("Wheel");
		levelMesh = ObjLoader.load("levels/level1_deco");
		textureShader = new Shader("texture", null);
		diffuseShader = new Shader("diffuse", null);
		deferredLightingShader = new PostProcessShader("deferredLighting",
				screenBuffer);

		ssaoOutput = new Texture2D(width, height, GL_RGBA8, GL_RGBA,
				(ByteBuffer) null);
		horizontalGaussianBlurOutput = new Texture2D(width, height, GL_RGBA8,
				GL_RGBA, (ByteBuffer) null);

		horizontalGaussianBlurShader = new PostProcessShader("gaussianBlur",
				horizontalGaussianBlurOutput);
		verticalGaussianBlurShader = new PostProcessShader("gaussianBlur",
				screenBuffer, Arrays.asList("#define _GAUSSIANBLUR_VERTICAL"));

		ssaoShader = new PostProcessShader("ssao", screenBuffer);

		Random random = new Random();

		// Vector3f[] noise = new Vector3f[16];
		// for (int i = 0; i < noise.length; ++i) {
		// noise[i] = new Vector3f(random.nextFloat() * 2.0f - 1.0f,
		// random.nextFloat() * 2.0f - 1.0f, 0.0f);
		// noise[i].normalize();
		// noise[i].scale(0.5f);
		// noise[i].add(new Vector3f(0.5f, 0.5f, 0.5f));
		// }
		// randomTexture = new Texture2D(4, 4, GL_RGB, GL_RGB,
		// Util.createFloatBufferFromArray(noise));
		//
		// Vector3f[] kernel = new Vector3f[SSAO_KERNEL_SIZE *
		// SSAO_KERNEL_SIZE];
		// for (int i = 0; i < kernel.length; ++i) {
		// kernel[i] = new Vector3f(random.nextFloat() * 2.0f - 1.0f,
		// random.nextFloat() * 2.0f - 1.0f, random.nextFloat());
		// kernel[i].normalize();
		// float scale = i / (float) kernel.length;
		// scale = 0.1f + 0.9f * scale * scale;
		// kernel[i].scale(scale);
		// kernel[i].scale(0.5f);
		// kernel[i].add(new Vector3f(0.5f, 0.5f, 0.5f));
		// }
		// kernelTexture = new Texture2D(SSAO_KERNEL_SIZE, SSAO_KERNEL_SIZE,
		// GL_RGB, GL_RGB, Util.createFloatBufferFromArray(kernel));

		kernel = new Vector2f[16];
		for (int i = 0; i < kernel.length; ++i) {
			kernel[i] = new Vector2f(random.nextFloat() * 2 - 1,
					random.nextFloat() * 2 - 1);
			kernel[i].normalize();
			kernel[i].scale(random.nextFloat());
		}

		ssaoShader.addInputTexture("geometryBuffer0", geometryTextures[0]);
		ssaoShader.addInputTexture("geometryBuffer1", geometryTextures[1]);
		ssaoShader.addInputTexture("depthBuffer", geometryTextures[2]);

		ssaoShader.addUniform("kernel").set(kernel);

		fbuf = BufferUtils.createFloatBuffer(16);

		out.setIdentity();

		float[][] data = new float[128][128];

		try {
			BufferedImage img = ImageIO.read(Util
					.getResourceFileHandle("/heightmap/level0.png"));
			for (int x = 0; x < 128; x++) {
				for (int y = 0; y < 128; y++) {
					data[x][y] = ((img.getRGB(x, y) << 16) & 0xFF) * 0.03125f;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		heightBody = PhysicsWorld.createHeightmap(data, 128, 128, 10);
		physicsWorld.addRigidBody(heightBody);
		heightmap = Heightmap.fromData(data, 128, 128, 5);

		out.setIdentity();

		vehicle = new Vehicle();
		vehicle.create(physicsWorld);

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glClearDepth(1.0);
	}

	private Vector3f lastForward = null;
	private Vector3f cameraPosition = new Vector3f();
	private Vector3f lookAt = new Vector3f();

	public void update(float delta) {
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			vehicle.steer(-1);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			vehicle.steer(1);
		} else {
			vehicle.steer(0);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			vehicle.accelerate(1);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			vehicle.accelerate(-1);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			vehicle.slow(1);
		} else {
			vehicle.accelerate(0);
		}
		vehicle.update();

		physicsWorld.update(delta);

		vehicle.getTransform(out);

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

	public void render() {
		geometryBuffer.bind();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(90.0f, Display.getWidth() / (float) Display.getHeight(),
				Shader.Z_NEAR, Shader.Z_FAR);

		FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
		glGetFloat(GL_PROJECTION_MATRIX, projectionMatrixBuffer);

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		vehicle.getTransform(out);

		gluLookAt(cameraPosition.x, cameraPosition.y, cameraPosition.z,
				lookAt.x, lookAt.y, lookAt.z, 0, 1, 0);

		glPushMatrix();
		{
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			diffuseShader.use();
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			carMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			vehicle.getWheelTransform(0, out);
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			diffuseShader.use();
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			vehicle.getWheelTransform(1, out);
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			diffuseShader.use();
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			vehicle.getWheelTransform(2, out);
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			diffuseShader.use();
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			vehicle.getWheelTransform(3, out);
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			diffuseShader.use();
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			heightBody.getCenterOfMassTransform(out);
			diffuseShader.use();
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			heightmap.render();
		}
		glPopMatrix();

		Matrix4f identity = new Matrix4f();
		identity.setIdentity();

		diffuseShader.use();

		levelMesh.render();

		screenBuffer.bind();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		Matrix4f projectionMatrix = Util
				.createMatrixFromFloatBuffer(projectionMatrixBuffer);
		Vector2f screenSize = new Vector2f(Display.getWidth(),
				Display.getHeight());

		ssaoShader.render(projectionMatrix, screenSize, quadMesh);

		/*
		 * horizontalGaussianBlurShader .addInputTexture("inputTexture",
		 * ssaoOutput); horizontalGaussianBlurShader.render(projectionMatrix,
		 * screenSize); horizontalGaussianBlurShader.set("blurRadius", 2);
		 * quadMesh.render();
		 * 
		 * verticalGaussianBlurShader.addInputTexture("inputTexture",
		 * horizontalGaussianBlurOutput);
		 * verticalGaussianBlurShader.render(projectionMatrix, screenSize);
		 * verticalGaussianBlurShader.set("blurRadius", 2); quadMesh.render();
		 */
	}
}
