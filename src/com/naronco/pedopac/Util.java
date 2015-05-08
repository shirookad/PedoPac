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

	public static File getResourceFileHandle(String filename) {
		try {
			return new File(Shader.class.getResource(filename).toURI());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getResourceFileName(String filename) {
		try {
			return Shader.class.getResource(filename).toURI().toString();
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

	public static Vector3f dot3(Vector3f base, Vector3f v0, Vector3f v1,
			Vector3f v2) {
		return new Vector3f(base.dot(v0), base.dot(v1), base.dot(v2));
	}

	public static Vector3f transformMulVector(
			com.bulletphysics.linearmath.Transform transform, Vector3f vec) {
		Vector3f x = (Vector3f) vec.clone();
		Vector3f v0 = new Vector3f();
		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		transform.basis.getRow(0, v0);
		transform.basis.getRow(1, v1);
		transform.basis.getRow(2, v2);
		Vector3f y = dot3(x, v0, v1, v2);
		y.add(transform.origin);
		return y;
	}

	public static Vector3f matrixMulVector(Matrix3f mat, Vector3f vec) {
		return new Vector3f(
				mat.m00 * vec.x + mat.m01 * vec.y + mat.m02 * vec.z, /**/
				mat.m10 * vec.x + mat.m11 * vec.y + mat.m12 * vec.z, /**/
				mat.m20 * vec.x + mat.m21 * vec.y + mat.m22 * vec.z);
	}

	public static byte[] loadBinaryFile(String path) {
		File file = new File(path);
		RandomAccessFile f = null;
		byte[] data = null;
		try {
			f = new RandomAccessFile(file, "r");
			data = new byte[(int) f.length()];
			f.readFully(data);
			f.close();
		} catch (IOException e) {
			System.out.println("Couldn't read binary file: " + path);
			return data;
		}
		return data;
	}

	public static byte[] loadBinaryFile(File file) {
		RandomAccessFile f = null;
		byte[] data = null;
		try {
			f = new RandomAccessFile(file, "r");
			data = new byte[(int) f.length()];
			f.readFully(data);
			f.close();
		} catch (IOException e) {
			System.out.println("Couldn't read binary file");
			return data;
		}
		return data;
	}
}
