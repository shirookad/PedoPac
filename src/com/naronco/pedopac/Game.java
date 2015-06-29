package com.naronco.pedopac;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import com.naronco.pedopac.rendering.Framebuffer;
import com.naronco.pedopac.rendering.Mesh;
import com.naronco.pedopac.rendering.PostProcessShader;
import com.naronco.pedopac.rendering.Shader;
import com.naronco.pedopac.rendering.Texture2D;
import com.naronco.pedopac.rendering.Vertex;

public class Game {
	private Framebuffer screenBuffer;
	private Texture2D[] geometryTextures;
	private Framebuffer geometryBuffer;

	private Mesh quadMesh = new Mesh(new Vertex[] {
			new Vertex(-1, -1, 0, 0, 0), new Vertex(1, -1, 0, 1, 0),
			new Vertex(1, 1, 0, 1, 1), new Vertex(-1, 1, 0, 0, 1) }, new int[] {
			0, 1, 2, 0, 2, 3 });

	private PostProcessShader ssaoShader;

	private Texture2D horizontalGaussianBlurOutput;

	private Vector2f[] kernel;
	
	private Scene currentScene;

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

		
		new Shader("texture", null);
		new PostProcessShader("deferredLighting", screenBuffer);

		new Texture2D(width, height, GL_RGBA8, GL_RGBA, (ByteBuffer) null);
		horizontalGaussianBlurOutput = new Texture2D(width, height, GL_RGBA8,
				GL_RGBA, (ByteBuffer) null);

		new PostProcessShader("gaussianBlur", horizontalGaussianBlurOutput);
		new PostProcessShader("gaussianBlur", screenBuffer,
				Arrays.asList("#define _GAUSSIANBLUR_VERTICAL"));

		ssaoShader = new PostProcessShader("ssao", screenBuffer);

		Random random = new Random();

		kernel = new Vector2f[16];
		for (int i = 0; i < kernel.length; ++i) {
			kernel[i] = new Vector2f(random.nextFloat() * 2 - 1,
					random.nextFloat() * 2 - 1);
			kernel[i].normalise();
			kernel[i].scale(random.nextFloat());
		}

		ssaoShader.addInputTexture("geometryBuffer0", geometryTextures[0]);
		ssaoShader.addInputTexture("geometryBuffer1", geometryTextures[1]);
		ssaoShader.addInputTexture("depthBuffer", geometryTextures[2]);

		ssaoShader.addUniform("kernel").set(kernel);
		
		currentScene = new GameScene();

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glClearDepth(1.0);
	}
	
	public void debug() {
		currentScene.debug();
	}

	public void update(float delta) {
		currentScene.performUpdate(delta);
		
		if(currentScene.getNext() != null)
			currentScene = currentScene.getNext();
	}

	public void render() {
		geometryBuffer.bind();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		currentScene.performRender();

		screenBuffer.bind();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
		glGetFloat(GL_PROJECTION_MATRIX, projectionMatrixBuffer);

		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.load(projectionMatrixBuffer);
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
