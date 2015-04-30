package com.naronco.pedopac.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;

import com.naronco.pedopac.*;

public class Framebuffer {
	private int framebuffer = 0;

	public Framebuffer(int framebuffer) {
		this.framebuffer = framebuffer;
	}

	public Framebuffer(Map<Integer, Texture2D> textures) throws Exception {
		framebuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

		for (Map.Entry<Integer, Texture2D> entry : textures.entrySet()) {
			glFramebufferTexture2D(GL_FRAMEBUFFER, entry.getKey(),
					GL_TEXTURE_2D, entry.getValue().getTexture(), 0);
		}

		Set<Integer> drawBuffers = textures.keySet();
		drawBuffers.remove(GL_DEPTH_ATTACHMENT);
		glDrawBuffers(Util.createIntBufferFromSet(drawBuffers));

		int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if (status != GL_FRAMEBUFFER_COMPLETE) {
			throw new Exception("Failed to create framebuffer");
		}

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	@Override
	protected void finalize() throws Throwable {
		if (framebuffer != 0)
			glDeleteFramebuffers(framebuffer);
	}

	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
	}
}
