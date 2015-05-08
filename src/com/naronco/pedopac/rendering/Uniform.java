package com.naronco.pedopac.rendering;

import static org.lwjgl.opengl.GL20.*;

import java.nio.*;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.lwjgl.*;

import com.naronco.pedopac.*;

public class Uniform {
	private Shader shader;
	private String name;
	private int location;

	public Uniform(String name) {
		this.name = name;
	}

	public void set(int value) {
		shader.use();
		glUniform1i(location, value);
	}

	public void set(float value) {
		shader.use();
		glUniform1f(location, value);
	}

	public void set(Vector2f value) {
		shader.use();
		glUniform2f(location, value.x, value.y);
	}

	public void set(Vector3f value) {
		shader.use();
		glUniform3f(location, value.x, value.y, value.z);
	}

	public void set(Vector4f value) {
		shader.use();
		glUniform4f(location, value.x, value.y, value.z, value.w);
	}

	public void set(Vector2f[] values) {
		shader.use();
		FloatBuffer fb = BufferUtils.createFloatBuffer(values.length << 1);
		for (Vector2f value : values)
			fb.put(value.x).put(value.y);
		glUniform2(location, (FloatBuffer) fb.flip());
	}

	public void set(Vector3f[] values) {
		shader.use();
		FloatBuffer fb = BufferUtils.createFloatBuffer(values.length * 3);
		for (Vector3f value : values)
			fb.put(value.x).put(value.y).put(value.z);
		glUniform3(location, (FloatBuffer) fb.flip());
	}

	public void set(Vector4f[] values) {
		shader.use();
		FloatBuffer fb = BufferUtils.createFloatBuffer(values.length << 2);
		for (Vector4f value : values)
			fb.put(value.x).put(value.y).put(value.z).put(value.w);
		glUniform4(location, (FloatBuffer) fb.flip());
	}

	public void set(Matrix3f matrix) {
		shader.use();
		glUniformMatrix3(location, false,
				Util.createFloatBufferFromMatrix(matrix));
	}

	public void set(Matrix4f matrix) {
		shader.use();
		glUniformMatrix4(location, false,
				Util.createFloatBufferFromMatrix(matrix));
	}

	public void set(Texture2D texture, int slot) {
		set(slot);
		texture.bind(slot);
	}

	public void setShader(Shader shader) {
		this.shader = shader;
		this.location = glGetUniformLocation(shader.getProgram(), name);
	}

	public Shader getShader() {
		return shader;
	}

	public String getName() {
		return name;
	}

	public int getLocation() {
		return location;
	}
}
