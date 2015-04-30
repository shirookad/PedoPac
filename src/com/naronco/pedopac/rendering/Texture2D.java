package com.naronco.pedopac.rendering;

import static org.lwjgl.opengl.GL11.*;

import java.nio.*;

public class Texture2D {
	private int texture = 0;

	public Texture2D(int width, int height, int internalformat, int format,
			ByteBuffer buffer) {
		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glTexImage2D(GL_TEXTURE_2D, 0, internalformat, width, height, 0,
				format, GL_UNSIGNED_BYTE, buffer);

		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public Texture2D(int width, int height, int internalformat, int format,
			FloatBuffer buffer) {
		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glTexImage2D(GL_TEXTURE_2D, 0, internalformat, width, height, 0,
				format, GL_FLOAT, buffer);

		glBindTexture(GL_TEXTURE_2D, 0);
	}

	@Override
	protected void finalize() throws Throwable {
		glDeleteTextures(texture);
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, texture);
	}

	public int getTexture() {
		return texture;
	}
}
