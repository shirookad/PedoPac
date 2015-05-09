package com.naronco.pedopac.rendering;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.*;
import java.nio.*;

import javax.imageio.*;

import org.lwjgl.*;

public class TextureLoader {
	public static Texture2D load(String name) {
		try {
			BufferedImage img = ImageIO.read(TextureLoader.class
					.getResource(name));
			int w = img.getWidth();
			int h = img.getHeight();
			int[] pixels = img.getRGB(0, 0, w, h, null, 0, w);
			ByteBuffer bb = BufferUtils.createByteBuffer(w * h * 4);
			for (int i = 0; i < pixels.length; ++i) {
				bb.put((byte) ((pixels[i] >> 16) & 0xFF))
						.put((byte) ((pixels[i] >> 8) & 0xFF))
						.put((byte) ((pixels[i]) & 0xFF))
						.put((byte) ((pixels[i] >> 24) & 0xFF));
			}
			return new Texture2D(w, h, GL_RGBA, GL_RGBA, (ByteBuffer) bb.flip());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
