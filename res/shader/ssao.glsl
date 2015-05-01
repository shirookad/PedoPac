#extension GL_EXT_gpu_shader4 : enable

uniform vec2 kernel[16];

float getLinearizedDepth(float depth) {
	return projectionMatrix[3][2] / (depth - projectionMatrix[2][2]);
}

float startDepth = (depth);

float getAoAmount(vec2 offs) {
	vec2 tx = gl_TexCoord[0].xy + offs;
	tx.x = clamp(tx.x, 0.0, 1.0 - texelSize.x);
	tx.y = clamp(tx.y, 0.0, 1.0 - texelSize.y);
	float distance = sampleDepth(tx) - startDepth;
	distance = (clamp(distance, -0.001, 0.001) + 0.001) / 0.002;
	return smoothstep(0.0, 1.0, distance);
}

void main() {
	float occlusion = 0.0;
	
	for (int i = 0; i < 16; ++i) {
		occlusion += getAoAmount(kernel[i] * texelSize * 8.0);
	}
	
	occlusion /= 16.0;
	occlusion = clamp(occlusion, 0.0, 0.5) + 0.5;
	
	gl_FragColor = vec4(vec3(occlusion), 1.0);
}