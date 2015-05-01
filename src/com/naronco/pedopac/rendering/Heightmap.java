package com.naronco.pedopac.rendering;

import org.lwjgl.util.vector.*;

public class Heightmap {
	public static Mesh fromData(float[][] data, int width, int height,
			float xzScale) {
		final int totalVerts = width * height;

		final int totalTriangles = 2 * (width - 1) * (height - 1);

		final float iWidth = 1.0f / width;
		final float iHeight = 1.0f / height;

		Vertex[] vertices = new Vertex[totalVerts];
		int[] indices = new int[totalTriangles * 3];

		Vector3f pos = new Vector3f();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				pos.set((i - width * 0.5f) * xzScale, data[i][j],
						(j - height * 0.5f) * xzScale);

				vertices[i + j * width] = new Vertex(pos, new Vector2f(i
						* iWidth, j * iHeight), new Vector3f(0, 1, 0));
			}
		}

		int n = 0;
		for (int i = 0; i < width - 1; i++) {
			for (int j = 0; j < height - 1; j++) {
				indices[n++] = ((j + 1) * width + i + 1);
				indices[n++] = (j * width + i + 1);
				indices[n++] = (j * width + i);

				indices[n++] = ((j + 1) * width + i);
				indices[n++] = ((j + 1) * width + i + 1);
				indices[n++] = (j * width + i);
			}
		}

		return new Mesh(vertices, indices);
	}
}
