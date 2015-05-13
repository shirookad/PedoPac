#ifdef _COMPILING_VERTEX

varying vec4 vertex;

void main() {
	vertex = gl_Vertex;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}

#endif

#ifdef _COMPILING_FRAGMENT

varying vec4 vertex;

uniform sampler2D environmentMap;

void main() {
	vec3 v=normalize(vertex.xyz);

	float tx=atan(v.z/v.x)/3.14159265*0.5+0.5;
	float len=sqrt(v.x*v.x+v.z*v.z);
	float ty=atan(-v.y/len)/(3.14159265*0.5);
	
	gl_FragColor = texture2D(environmentMap, vec2(tx, ty));
}

#endif
