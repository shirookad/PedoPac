package com.naronco.pedopac;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
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

	private static int loadShader(String filename, int type) {
		String content = Util.loadResourceText(filename);
		switch (type) {
		case GL_VERTEX_SHADER:
			content = "#define _COMPILING_VERTEX\n" + content;
			break;
		case GL_FRAGMENT_SHADER:
			content = "#define _COMPILING_FRAGMENT\n" + content;
			break;
		}

		int shader = glCreateShader(type);

		glShaderSource(shader, content);
		glCompileShader(shader);
		
		if(glGetShaderi(shader, GL_COMPILE_STATUS) == 0)
		{
			int len = glGetShaderi(shader, GL_INFO_LOG_LENGTH);
			System.out.println(glGetShaderInfoLog(shader, len));
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
