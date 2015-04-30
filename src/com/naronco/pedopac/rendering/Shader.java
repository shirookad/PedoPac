package com.naronco.pedopac.rendering;

import static org.lwjgl.opengl.GL20.*;

import com.naronco.pedopac.*;

public class Shader {
	public static final boolean DEFERRED_SHADING = true;

	private int program;

	public Shader(String name) {
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

	protected String modifyContent(String content, int type) {
		content = markShaderType(content, type);

		if (DEFERRED_SHADING) {
			content = "#define _DEFERRED_SHADING\n" + content;
			content = "#define _WRITE_TO_TEXTURES(p, c, n) gl_FragData[0] = vec4(c.rgb, 1.0); gl_FragData[1] = vec4(normalize(n) * 0.5 + 0.5, 1.0);"
					+ //
					"gl_FragDepth = (2 * gl_DepthRange.near) / (gl_DepthRange.far + gl_DepthRange.near - p.z * (gl_DepthRange.far - gl_DepthRange.near));\n" + //
					content;
		}

		return content;
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

	public void setUniform1i(String name, int v0) {
		glUniform1i(glGetUniformLocation(program, name), v0);
	}

	public void setUniform2i(String name, int v0, int v1) {
		glUniform2i(glGetUniformLocation(program, name), v0, v1);
	}

	public void setUniform3i(String name, int v0, int v1, int v2) {
		glUniform3i(glGetUniformLocation(program, name), v0, v1, v2);
	}

	public void setUniform4i(String name, int v0, int v1, int v2, int v3) {
		glUniform4i(glGetUniformLocation(program, name), v0, v1, v2, v3);
	}

	public void setUniform1f(String name, float v0) {
		glUniform1f(glGetUniformLocation(program, name), v0);
	}

	public void setUniform2f(String name, float v0, float v1) {
		glUniform2f(glGetUniformLocation(program, name), v0, v1);
	}

	public void setUniform3f(String name, float v0, float v1, float v2) {
		glUniform3f(glGetUniformLocation(program, name), v0, v1, v2);
	}

	public void setUniform4f(String name, float v0, float v1, float v2, float v3) {
		glUniform4f(glGetUniformLocation(program, name), v0, v1, v2, v3);
	}

	public int getProgram() {
		return program;
	}
}
