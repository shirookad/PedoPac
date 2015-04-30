#ifdef _COMPILING_VERTEX

varying vec3 normal;
varying vec4 color;
varying vec4 position;

void main() {
	color = gl_Color;
	normal = gl_NormalMatrix * gl_Normal;
	position = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_Position = position;
}

#endif

#ifdef _COMPILING_FRAGMENT

varying vec3 normal;
varying vec4 color;
varying vec4 position;

void main() {
#ifdef _DEFERRED_SHADING
	_WRITE_TO_TEXTURES(position, color, normal);
#else
	gl_FragColor = vec4(dot(normalize(normal), normalize(vec3(1, 1, 1))) * color.rgb, color.a);
#endif
}

#endif
