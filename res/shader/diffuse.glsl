#ifdef _COMPILING_VERTEX

varying vec3 normal;
varying vec4 color;

void main() {
	color = gl_Color;
	normal = gl_NormalMatrix * gl_Normal;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}

#endif

#ifdef _COMPILING_FRAGMENT

varying vec3 normal;
varying vec4 color;

void main() {
	gl_FragColor = vec4(dot(normalize(normal), normalize(vec3(1, 1, 1))) * color.rgb, color.a);
}

#endif
