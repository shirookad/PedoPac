package com.naronco.pedopac.rendering;

import org.lwjgl.util.vector.*;

public class Vertex {
	private Vector3f position;
	private Vector2f uv;
	private Vector3f normal;
	private Vector4f color;

	public Vertex(Vector3f position) {
		this.position = position;
		this.uv = new Vector2f(0, 0);
		this.normal = new Vector3f(0, 0, 1);
		this.color = new Vector4f(1, 1, 1, 1);
	}

	public Vertex(float x, float y, float z) {
		this.position = new Vector3f(x, y, z);
		this.uv = new Vector2f(0, 0);
		this.normal = new Vector3f(0, 0, 1);
		this.color = new Vector4f(1, 1, 1, 1);
	}

	public Vertex(Vector3f position, Vector4f color) {
		this.position = position;
		this.uv = new Vector2f(0, 0);
		this.normal = new Vector3f(0, 0, 1);
		this.color = color;
	}

	public Vertex(Vector3f position, Vector2f uv) {
		this.position = position;
		this.uv = uv;
		this.normal = new Vector3f(0, 0, 1);
		this.color = new Vector4f(1, 1, 1, 1);
	}

	public Vertex(float x, float y, float z, float u, float v) {
		this.position = new Vector3f(x, y, z);
		this.uv = new Vector2f(u, v);
		this.normal = new Vector3f(0, 0, 1);
		this.color = new Vector4f(1, 1, 1, 1);
	}

	public Vertex(Vector3f position, Vector2f uv, Vector3f normal) {
		this.position = position;
		this.uv = uv;
		this.normal = normal;
		this.color = new Vector4f(1, 1, 1, 1);
	}

	public Vertex(Vector3f position, Vector2f uv, Vector3f normal,
			Vector4f color) {
		this.position = position;
		this.uv = uv;
		this.normal = normal;
		this.color = color;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setUv(Vector2f uv) {
		this.uv = uv;
	}

	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}

	public void setColor(Vector4f color) {
		this.color = color;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector2f getUv() {
		return uv;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public Vector4f getColor() {
		return color;
	}
}
