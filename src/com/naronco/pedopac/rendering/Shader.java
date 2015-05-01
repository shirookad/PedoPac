package com.naronco.pedopac.rendering;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;

import java.nio.*;
import java.util.*;

import javax.vecmath.*;

import org.lwjgl.*;

import com.naronco.pedopac.*;

public class Shader {
	public static final boolean DEFERRED_SHADING = true;
	public static final float Z_NEAR = 0.01f;
	public static final float Z_FAR = 100.0f;

	protected int program;
	protected List<String> preprocessorDirectives;

	public Shader(String name, List<String> preprocessorDirectives) {
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

	public void set(String name, int v0) {
		glUniform1i(glGetUniformLocation(program, name), v0);
	}

	public void set(String name, int v0, int v1) {
		glUniform2i(glGetUniformLocation(program, name), v0, v1);
	}

	public void set(String name, int v0, int v1, int v2) {
		glUniform3i(glGetUniformLocation(program, name), v0, v1, v2);
	}

	public void set(String name, int v0, int v1, int v2, int v3) {
		glUniform4i(glGetUniformLocation(program, name), v0, v1, v2, v3);
	}

	public void set(String name, float v0) {
		glUniform1f(glGetUniformLocation(program, name), v0);
	}

	public void set(String name, float v0, float v1) {
		glUniform2f(glGetUniformLocation(program, name), v0, v1);
	}

	public void set(String name, float v0, float v1, float v2) {
		glUniform3f(glGetUniformLocation(program, name), v0, v1, v2);
	}

	public void set(String name, float v0, float v1, float v2, float v3) {
		glUniform4f(glGetUniformLocation(program, name), v0, v1, v2, v3);
	}

	public void set(String name, Vector2f v) {
		set(name, v.x, v.y);
	}

	public void set(String name, Vector3f v) {
		set(name, v.x, v.y, v.z);
	}

	public void set(String name, Vector4f v) {
		set(name, v.x, v.y, v.z, v.w);
	}

	public void set(String name, Matrix3f matrix) {
		glUniformMatrix3(glGetUniformLocation(program, name), false,
				Util.createFloatBufferFromMatrix(matrix));
	}

	public void set(String name, Matrix4f matrix) {
		glUniformMatrix4(glGetUniformLocation(program, name), false,
				Util.createFloatBufferFromMatrix(matrix));
	}

	public void set(String name, FloatBuffer matrix) {
		glUniformMatrix4(glGetUniformLocation(program, name), false, matrix);
	}

	public void set(String name, Vector2f[] v) {
		FloatBuffer fb = BufferUtils.createFloatBuffer(v.length * 2);
		for (Vector2f vec : v) {
			fb.put(vec.x).put(vec.y);
		}
		glUniform2(glGetUniformLocation(program, name), (FloatBuffer) fb.flip());
	}

	public void set(String name, Texture2D texture, int slot) {
		texture.bind(slot);
		set(name, slot);
	}

	public int getProgram() {
		return program;
	}
}
