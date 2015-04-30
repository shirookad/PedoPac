package com.naronco.pedopac.rendering;

import java.util.*;

import org.lwjgl.util.vector.*;

import com.naronco.pedopac.*;

public class ObjLoader {
	public static Mesh load(String name) {
		List<Vector3f> vertexPos = new ArrayList<Vector3f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Vector2f> texCoords = new ArrayList<Vector2f>();

		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Integer> indices = new ArrayList<Integer>();

		List<String> lines = Util.loadResourceLines("/models/" + name + ".obj");

		for (String line : lines) {
			String[] args = line.split(" ");
			if (args[0].equals("v")) {
				vertexPos.add(new Vector3f(Float.parseFloat(args[1]), Float
						.parseFloat(args[2]), Float.parseFloat(args[3])));
			}
			if (args[0].equals("vt")) {
				texCoords.add(new Vector2f(Float.parseFloat(args[1]), Float
						.parseFloat(args[2])));
			}
			if (args[0].equals("vn")) {
				normals.add(new Vector3f(Float.parseFloat(args[1]), Float
						.parseFloat(args[2]), Float.parseFloat(args[3])));
			}
			if (args[0].equals("f")) {
				for (int i = 1; i < args.length; i++) {
					String[] subArgs = args[i].split("/");
					int v = Integer.parseInt(subArgs[0]) - 1;
					int vt = Integer.parseInt(subArgs[1]) - 1;
					int vn = Integer.parseInt(subArgs[2]) - 1;
					vertices.add(new Vertex(vertexPos.get(v),
							texCoords.get(vt), normals.get(vn)));
					indices.add(indices.size());
				}
			}
		}
		
		Vertex[] vert = new Vertex[vertices.size()];
		int i = 0;
		for(Vertex v : vertices)
			vert[i++] = v;

		return new Mesh(vert, Util.toIntArray(indices));
	}
}
