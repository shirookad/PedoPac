package com.naronco.pedopac.rendering;

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

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
