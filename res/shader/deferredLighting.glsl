void main() {
	float nDotL = max(0.0, dot(normal, normalize(vec3(0.3, 1.0, 0.5))));
	gl_FragColor = vec4(nDotL * color, 1);
}