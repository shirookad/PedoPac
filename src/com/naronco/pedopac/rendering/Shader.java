package com.naronco.pedopac.rendering;

import static org.lwjgl.opengl.GL20.*;

import java.util.*;

import com.naronco.pedopac.*;

public class Shader {
	public static final boolean DEFERRED_SHADING = true;
	public static final float Z_NEAR = 0.01f;
	public static final float Z_FAR = 400.0f;

	protected int program;
	protected List<Uniform> uniforms;
	protected List<String> preprocessorDirectives;

	public Shader(String name, List<String> preprocessorDirectives) {
		this.uniforms = new ArrayList<>();
		this.preprocessorDirectives = preprocessorDirectives;

		program = glCreateProgram();

		int vertexShader = loadShader("/shader/" + name + ".glsl",
				GL_VERTEX_SHADER);
		int fragmentShader = loadShader("/shader/" + name + ".glsl",
				GL_FRAGMENT_SHADER);

		glAttachShader(program, vertexShader);
		glAttachShader(program, fragmentShader);

		glLinkProgram(program);

		glDeleteShader(fragmentShader);
		glDeleteShader(vertexShader);

		addDefaultUniforms();
	}

	@Override
	protected void finalize() throws Throwable {
		glDeleteProgram(program);
	}

	protected final String markShaderType(String content, int type) {
		switch (type) {
		case GL_VERTEX_SHADER:
			content = "#define _COMPILING_VERTEX\n" + content;
			break;
		case GL_FRAGMENT_SHADER:
			content = "#define _COMPILING_FRAGMENT\n" + content;
			break;
		}

		return content;
	}

	protected final String addPreprocessorDirectives(String content) {
		if (preprocessorDirectives != null) {
			for (String directive : preprocessorDirectives) {
				content = directive + "\n" + content;
			}
		}
		return content;
	}

	protected String modifyContent(String content, int type) {
		content = markShaderType(content, type);

		if (DEFERRED_SHADING) {
			content = "#define _DEFERRED_SHADING\n" + content;
			content = "#define _WRITE_TO_TEXTURES(p, c, n) gl_FragData[0] = vec4(c.rgb, 1.0); gl_FragData[1] = vec4(normalize(n) * 0.5 + 0.5, 1.0);"
					+ //
					"gl_FragDepth = (p.z - "
					+ Z_NEAR
					+ ") / ("
					+ Z_FAR
					+ " - "
					+ Z_NEAR + ")\n" + //
					content;
		}

		return content;
	}

	protected void addDefaultUniforms() {
	}

	protected final int loadShader(String filename, int type) {
		String content = Util.loadResourceText(filename);
		content = modifyContent(content, type);

		int shader = glCreateShader(type);

		glShaderSource(shader, content);
		glCompileShader(shader);

		if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
			System.out.println(glGetShaderInfoLog(shader,
					glGetShaderi(shader, GL_INFO_LOG_LENGTH)));
		}

		return shader;
	}

	public void use() {
		glUseProgram(program);
	}

	public void addUniform(Uniform uniform) {
		uniforms.add(uniform);
		uniform.setShader(this);
	}

	public Uniform addUniform(String name) {
		Uniform uniform = new Uniform(name);
		uniforms.add(uniform);
		uniform.setShader(this);
		return uniform;
	}

	public int getProgram() {
		return program;
	}

	public Uniform getUniform(String name) {
		for (Uniform uniform : uniforms) {
			if (uniform.getName().equals(name)) {
				return uniform;
			}
		}
		return null;
	}
}
