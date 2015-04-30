package com.naronco.pedopac;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.util.glu.GLU.*;

import java.nio.*;
import java.util.*;

import javax.vecmath.*;

import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import com.bulletphysics.collision.shapes.*;
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
	private Mesh carMesh, wheelMesh, levelMesh;
	private FloatBuffer fbuf;

	private Shader textureShader;
	private Shader diffuseShader;
	private PostProcessShader deferredLightingShader;
	private PostProcessShader ssaoShader;

	private Texture2D randomTexture;
	private Texture2D kernelTexture;

	private Camera camera;

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
		textureShader = new Shader("texture");
		diffuseShader = new Shader("diffuse");
		deferredLightingShader = new PostProcessShader("deferredLighting");
		ssaoShader = new PostProcessShader("ssao");

		Random random = new Random();

		Vector3f[] noise = new Vector3f[16];
		for (int i = 0; i < noise.length; ++i) {
			noise[i] = new Vector3f(random.nextFloat() * 2.0f - 1.0f,
					random.nextFloat() * 2.0f - 1.0f, 0.0f);
			noise[i].normalize();
		}
		randomTexture = new Texture2D(4, 4, GL_RGB, GL_RGB,
				Util.createFloatBufferFromArray(noise));

		Vector3f[] kernel = new Vector3f[SSAO_KERNEL_SIZE * SSAO_KERNEL_SIZE];
		for (int i = 0; i < kernel.length; ++i) {
			kernel[i] = new Vector3f(random.nextFloat() * 2.0f - 1.0f,
					random.nextFloat() * 2.0f - 1.0f, random.nextFloat());
			kernel[i].normalize();
			float scale = i / (float) kernel.length;
			scale = 0.1f + 0.9f * scale * scale;
			kernel[i].scale(random.nextFloat() * scale);
		}
		kernelTexture = new Texture2D(SSAO_KERNEL_SIZE, SSAO_KERNEL_SIZE,
				GL_RGB, GL_RGB, Util.createFloatBufferFromArray(kernel));

		camera = new Camera(new Matrix4f());
		camera.setController(new FreeCameraController());

		fbuf = BufferUtils.createFloatBuffer(16);

		physicsWorld.addRigidBody(PhysicsWorld.createRigidBody(
				new StaticPlaneShape(new Vector3f(0, 1, 0), 0), 0));

		vehicle = new Vehicle();
		vehicle.create(physicsWorld);

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glClearDepth(1.0);
	}

	public void update(float delta) {
		camera.update(delta);

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			vehicle.accelerate(1);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			vehicle.accelerate(-1);
		} else {
			vehicle.accelerate(0);
		}
		vehicle.update();

		physicsWorld.update(delta);
	}

	public void render() {
		geometryBuffer.bind();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(90.0f, Display.getWidth() / (float) Display.getHeight(),
				0.01f, 100.0f);

		FloatBuffer projectionMatrix = BufferUtils.createFloatBuffer(16);
		glGetFloat(GL_PROJECTION_MATRIX, projectionMatrix);

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		vehicle.getTransform(out);

		gluLookAt(out.origin.x + 5, out.origin.y + 2, out.origin.z,
				out.origin.x, out.origin.y, out.origin.z, 0, 1, 0);

		glPushMatrix();
		{
			diffuseShader.use();
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			carMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			vehicle.getWheelTransform(0, out);
			diffuseShader.use();
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			vehicle.getWheelTransform(1, out);
			diffuseShader.use();
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			vehicle.getWheelTransform(2, out);
			diffuseShader.use();
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelMesh.render();
		}
		glPopMatrix();

		glPushMatrix();
		{
			vehicle.getWheelTransform(3, out);
			diffuseShader.use();
			float[] f = new float[16];
			out.getOpenGLMatrix(f);
			fbuf.put(f);
			fbuf.flip();
			glMultMatrix(fbuf);
			wheelMesh.render();
		}
		glPopMatrix();

		levelMesh.render();

		screenBuffer.bind();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		ssaoShader.use();

		ssaoShader.setUniform2f("screenSize", Display.getWidth(),
				Display.getHeight());
		ssaoShader.setUniform1f("tanHalfFov", (float) Math.tan(90.0f * 0.5f));
		ssaoShader.setUniform1f("aspectRatio", Display.getWidth()
				/ (float) Display.getHeight());

		ssaoShader.setUniformMatrix4f("projectionMatrix", projectionMatrix);

		randomTexture.bind(4);
		ssaoShader.setUniform1i("randomTexture", 4);

		kernelTexture.bind(3);
		ssaoShader.setUniform1i("kernelTexture", 3);

		geometryTextures[2].bind(2);
		ssaoShader.setUniform1i("depthBuffer", 2);
		geometryTextures[1].bind(1);
		ssaoShader.setUniform1i("geometryBuffer1", 1);
		geometryTextures[0].bind(0);
		ssaoShader.setUniform1i("geometryBuffer0", 0);

		quadMesh.render();
	}
}
