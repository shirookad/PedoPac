package com.naronco.pedopac;

import java.io.*;
import java.nio.*;
import java.util.*;

import javax.vecmath.*;

import org.lwjgl.*;

import com.naronco.pedopac.rendering.*;

public class Util {
	public static String loadResourceText(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					Shader.class.getResourceAsStream(filename)));
			String content = "", line;
			while ((line = reader.readLine()) != null) {
				content += line + "\n";
			}
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> loadResourceLines(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					Shader.class.getResourceAsStream(filename)));
			String line;
			List<String> lines = new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int[] toIntArray(List<Integer> ints) {
		int[] arr = new int[ints.size()];
		for (int i = 0; i < ints.size(); i++)
			arr[i] = ints.get(i).intValue();
		return arr;
	}

	public static ByteBuffer createEmptyByteBuffer(int size) {
		ByteBuffer b = BufferUtils.createByteBuffer(size);
		for (int i = 0; i < size; ++i)
			b.put((byte) 0);
		b.flip();
		return b;
	}

	public static FloatBuffer createEmptyFloatBuffer(int size) {
		FloatBuffer b = BufferUtils.createFloatBuffer(size);
		for (int i = 0; i < size; ++i)
			b.put(0);
		b.flip();
		return b;
	}

	public static IntBuffer createIntBufferFromSet(Set<Integer> set) {
		IntBuffer b = BufferUtils.createIntBuffer(set.size());
		for (Integer i : set)
			b.put(i);
		b.flip();
		return b;
	}

	public static FloatBuffer createFloatBufferFromArray(float[] array) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(array.length);
		fb.put(array).flip();
		return fb;
	}

	public static FloatBuffer createFloatBufferFromArray(Vector3f[] array) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(array.length * 3);
		for (Vector3f v : array) {
			fb.put(v.x).put(v.y).put(v.z);
		}
		return (FloatBuffer) fb.flip();
	}

	public static FloatBuffer createFloatBufferFromMatrix(Matrix3f matrix) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(9);
		fb.put(matrix.m00).put(matrix.m01).put(matrix.m02);
		fb.put(matrix.m10).put(matrix.m11).put(matrix.m12);
		fb.put(matrix.m20).put(matrix.m21).put(matrix.m22);
		return (FloatBuffer) fb.flip();
	}

	public static FloatBuffer createFloatBufferFromMatrix(Matrix4f matrix) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		fb.put(matrix.m00).put(matrix.m01).put(matrix.m02).put(matrix.m03);
		fb.put(matrix.m10).put(matrix.m11).put(matrix.m12).put(matrix.m13);
		fb.put(matrix.m20).put(matrix.m21).put(matrix.m22).put(matrix.m23);
		fb.put(matrix.m30).put(matrix.m31).put(matrix.m32).put(matrix.m33);
		return (FloatBuffer) fb.flip();
	}

	public static Matrix4f createMatrixFromFloatBuffer(FloatBuffer fb) {
		float[] f = new float[16];
		fb.get(f);
		return new Matrix4f(f);
	}
}
