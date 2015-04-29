#ifdef __COMPILING_VERTEX

varying vec4 color;

void main() {
	color = gl_Color;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}

#endif

#ifdef __COMPILING_FRAGMENT

varying vec4 color;

void main() {
	gl_FragColor = color;
}

#endif
