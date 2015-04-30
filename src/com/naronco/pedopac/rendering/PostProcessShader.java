package com.naronco.pedopac.rendering;

public class PostProcessShader extends Shader {
	public PostProcessShader(String name) {
		super(name);
	}

	@Override
	protected String modifyContent(String content, int type) {
		content = "" + //
				"#ifdef _COMPILING_VERTEX\n" + //
				"void main() {\n" + //
				"	gl_TexCoord[0] = gl_MultiTexCoord0;\n" + //
				"	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" + //
				"}\n" + //
				"#endif\n" + //
				"#ifdef _COMPILING_FRAGMENT\n" + //
				"uniform sampler2D geometryBuffer0;\n" + //
				"uniform sampler2D geometryBuffer1;\n" + //
				"uniform sampler2D depthBuffer;\n" + //
				"vec3 color = texture2D(geometryBuffer0, gl_TexCoord[0].xy).rgb;\n" + //
				"vec3 normal = normalize(texture2D(geometryBuffer1, gl_TexCoord[0].xy).xyz * 2.0 - 1.0);\n" + //
				"float depth = texture2D(depthBuffer, gl_TexCoord[0].xy).r;\n" + //
				"\n" + //
				"\n" + //
				"\n" + //
				"\n" + //
				"\n" + //
				"\n" + //
				"\n" + //
				content + //
				"#endif\n" + //
				"";

		content = markShaderType(content, type);

		return content;
	}
}
