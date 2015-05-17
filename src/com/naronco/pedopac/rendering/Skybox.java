package com.naronco.pedopac.rendering;

import org.lwjgl.util.vector.Vector3f;

public class Skybox {
	public static final Mesh CUBE_MESH = new Mesh(new Vertex[] {//
			new Vertex(new Vector3f(-1, -1, +1)),//
					new Vertex(new Vector3f(+1, -1, +1)),//
					new Vertex(new Vector3f(+1, +1, +1)),//
					new Vertex(new Vector3f(-1, +1, +1)),//
					new Vertex(new Vector3f(-1, -1, -1)),//
					new Vertex(new Vector3f(+1, -1, -1)),//
					new Vertex(new Vector3f(+1, +1, -1)),//
					new Vertex(new Vector3f(-1, +1, -1)) //
			}, new int[] {//
			// front
					0, 1, 2,//
					2, 3, 0,//
					// top
					3, 2, 6,//
					6, 7, 3,//
					// back
					7, 6, 5,//
					5, 4, 7,//
					// bottom
					4, 5, 1,//
					1, 0, 4,//
					// left
					4, 0, 3,//
					3, 7, 4,//
					// right
					1, 5, 6,//
					6, 2, 1 });

	private Texture2D environmentMap;
	private Shader shader;

	public Skybox(String name) {
		environmentMap = TextureLoader.load(name);
		shader = new Shader("skybox", null);

		shader.addUniform("environmentMap");
	}

	public void render() {
		shader.use();
		shader.getUniform("environmentMap").set(environmentMap, 0);

		CUBE_MESH.render();
	}

	public Texture2D getEnvironmentMap() {
		return environmentMap;
	}

	public Shader getShader() {
		return shader;
	}
}
