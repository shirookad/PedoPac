package com.naronco.pedopac.mesh;

import static org.lwjgl.opengl.GL11.*;

public class Mesh {
	private int displayList;

	public Mesh(Vertex[] vertices, int[] indices) {
		displayList = glGenLists(1);

		glNewList(displayList, GL_COMPILE);
		glBegin(GL_TRIANGLES);

		for (int index : indices) {
			Vertex vertex = vertices[index];
			glTexCoord2f(vertex.getUv().x, vertex.getUv().y);
			glNormal3f(vertex.getNormal().x, vertex.getNormal().y,
					vertex.getNormal().z);
			glColor4f(vertex.getColor().x, vertex.getColor().y,
					vertex.getColor().z, vertex.getColor().w);
			glVertex3f(vertex.getPosition().x, vertex.getPosition().y,
					vertex.getPosition().z);
		}

		glEnd();
		glEndList();
	}

	@Override
	protected void finalize() throws Throwable {
		glDeleteLists(displayList, 1);
	}

	public void render() {
		glCallList(displayList);
	}
}
