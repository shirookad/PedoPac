#ifdef _COMPILING_VERTEX

void main() {
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}

#endif

#ifdef _COMPILING_FRAGMENT

uniform sampler2D anyTexture;

void main() {
	gl_FragColor = texture2D(anyTexture, gl_TexCoord[0].xy);
}

#endif
