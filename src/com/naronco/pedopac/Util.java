package com.naronco.pedopac;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.lwjgl.BufferUtils;

import com.naronco.pedopac.rendering.Shader;

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
