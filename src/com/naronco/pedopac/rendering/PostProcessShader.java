package com.naronco.pedopac.rendering;

import static org.lwjgl.opengl.GL30.*;

import java.util.*;

import javax.vecmath.*;

public class PostProcessShader extends Shader {
	private Framebuffer outputBuffer;
	private Texture2D outputTexture;
	private Map<String, Texture2D> inputTextures;

	public PostProcessShader(String name, Texture2D outputTexture) {
		super(name, null);
		this.inputTextures = new HashMap<>();
		setOutputTexture(outputTexture);
	}

	public PostProcessShader(String name, Texture2D outputTexture,
			List<String> preprocessorDirectives) {
		super(name, preprocessorDirectives);
		this.inputTextures = new HashMap<>();
		setOutputTexture(outputTexture);
	}

	public PostProcessShader(String name, Framebuffer outputBuffer) {
		super(name, null);
		this.inputTextures = new HashMap<>();
		this.outputBuffer = outputBuffer;
	}

	@Override
	protected String modifyContent(String content, int type) {
		content = addPreprocessorDirectives(content);

		content = ""
				+ //
				"#ifdef _COMPILING_VERTEX\n"
				+ //
				"void main() {\n"
				+ //
				"	gl_TexCoord[0] = gl_MultiTexCoord0;\n"
				+ //
				"	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n"
				+ //
				"}\n"
				+ //
				"#endif\n"
				+ //
				"#ifdef _COMPILING_FRAGMENT\n"
				+ //
				"uniform sampler2D geometryBuffer0;\n"
				+ //
				"uniform sampler2D geometryBuffer1;\n"
				+ //
				"uniform sampler2D depthBuffer;\n"
				+ //
				"vec3 color = texture2D(geometryBuffer0, gl_TexCoord[0].xy).rgb;\n"
				+ //
				"vec3 normal = normalize(texture2D(geometryBuffer1, gl_TexCoord[0].xy).xyz * 2.0 - 1.0);\n"
				+ //
				"float depth = texture2D(depthBuffer, gl_TexCoord[0].xy).r;\n"
				+ //
				"float sampleDepth(vec2 coord) { return texture2D(depthBuffer, coord).r; }\n"
				+ //
				"uniform mat4 projectionMatrix;\n" + //
				"uniform vec2 screenSize;\n" + //
				"uniform float tanHalfFov;\n" + //
				"uniform float aspectRatio;\n" + //
				"vec2 texelSize = 1.0 / screenSize;\n" + //
				"\n" + //
				"\n" + //
				"\n" + //
				content + //
				"#endif\n" + //
				"";

		content = markShaderType(content, type);

		return content;
	}

	public void render(Matrix4f projectionMatrix, Vector2f screenSize) {
		outputBuffer.bind();

		use();

		int slot = 0;
		for (Map.Entry<String, Texture2D> inputTexture : inputTextures
				.entrySet()) {
			set(inputTexture.getKey(), inputTexture.getValue(), slot++);
		}

		float tanHalfFov = projectionMatrix.getElement(1, 1);
		float aspectRatio = screenSize.x / screenSize.y;

		set("projectionMatrix", projectionMatrix);
		set("screenSize", screenSize);
		set("tanHalfFov", tanHalfFov);
		set("aspectRatio", aspectRatio);
	}

	public void setOutputTexture(Texture2D outputTexture) {
		this.outputTexture = outputTexture;

		if (outputTexture != null) {
			try {
				Map<Integer, Texture2D> textures = new HashMap<>();
				textures.put(GL_COLOR_ATTACHMENT0, outputTexture);
				outputBuffer = new Framebuffer(textures);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addInputTexture(String name, Texture2D texture) {
		inputTextures.put(name, texture);
	}

	public Framebuffer getOutputBuffer() {
		return outputBuffer;
	}

	public Texture2D getOutputTexture() {
		return outputTexture;
	}
}
