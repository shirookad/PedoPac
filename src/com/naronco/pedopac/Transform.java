package com.naronco.pedopac;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Transform {
	private Vector3f translation;
	private Quaternion rotation;
	private Vector3f scale;

	private Transform parent;

	public Transform() {
		this.translation = new Vector3f(0, 0, 0);
		this.rotation = new Quaternion(0, 0, 0, 1);
		this.scale = new Vector3f(1, 1, 1);
	}

	public void setTranslation(Vector3f translation) {
		this.translation = translation;
	}

	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	public void setParent(Transform parent) {
		this.parent = parent;
	}

	public Matrix4f getMatrix() {
		Matrix4f translationMatrix = new Matrix4f().translate(translation);
		Matrix4f rotationMatrix = convertQuaternionToMatrix4f(rotation);
		Matrix4f scaleMatrix = new Matrix4f().scale(scale);

		Matrix4f result = new Matrix4f();
		Matrix4f.mul(scaleMatrix, rotationMatrix, result);
		Matrix4f.mul(result, translationMatrix, result);

		if (parent != null)
			Matrix4f.mul(parent.getMatrix(), result, result);

		return result;
	}

	public Vector3f getTranslation() {
		Matrix4f transformation = new Matrix4f();
		if (parent != null)
			transformation = parent.getMatrix();
		Vector4f result = new Vector4f();
		Matrix4f.transform(transformation, new Vector4f(translation.x,
				translation.y, translation.z, 1), result);
		return new Vector3f(result.x, result.y, result.z);
	}

	public Quaternion getRotation() {
		Quaternion globalRotation = new Quaternion(rotation);
		if (parent != null)
			Quaternion
					.mul(parent.getRotation(), globalRotation, globalRotation);
		return globalRotation;
	}

	public Vector3f getScale() {
		Vector3f globalScale = new Vector3f(scale);
		if (parent != null) {
			Vector3f parentScale = parent.getScale();
			globalScale.x *= parentScale.x;
			globalScale.y *= parentScale.y;
			globalScale.z *= parentScale.z;
		}
		return scale;
	}

	public Transform getParent() {
		return parent;
	}

	private static Matrix4f convertQuaternionToMatrix4f(Quaternion q) {
		Matrix4f matrix = new Matrix4f();
		matrix.m00 = 1.0f - 2.0f * (q.getY() * q.getY() + q.getZ() * q.getZ());
		matrix.m01 = 2.0f * (q.getX() * q.getY() + q.getZ() * q.getW());
		matrix.m02 = 2.0f * (q.getX() * q.getZ() - q.getY() * q.getW());
		matrix.m03 = 0.0f;

		matrix.m10 = 2.0f * (q.getX() * q.getY() - q.getZ() * q.getW());
		matrix.m11 = 1.0f - 2.0f * (q.getX() * q.getX() + q.getZ() * q.getZ());
		matrix.m12 = 2.0f * (q.getZ() * q.getY() + q.getX() * q.getW());
		matrix.m13 = 0.0f;

		matrix.m20 = 2.0f * (q.getX() * q.getZ() + q.getY() * q.getW());
		matrix.m21 = 2.0f * (q.getY() * q.getZ() - q.getX() * q.getW());
		matrix.m22 = 1.0f - 2.0f * (q.getX() * q.getX() + q.getY() * q.getY());
		matrix.m23 = 0.0f;

		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		matrix.m33 = 1.0f;

		return matrix;
	}
}
