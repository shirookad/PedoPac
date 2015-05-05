#extension GL_EXT_gpu_shader4 : enable

uniform vec2 kernel[16];

vec2 rand2(vec2 co) {
    return vec2(
    			fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453),
    			fract(sin(dot(co.xy ,vec2(34.2532,23.123))) * 75623.1234)
    			);
}

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
		vec2 rndOffs = (rand2(gl_TexCoord[0].st * float(i)) * 2.0 - 1.0) * texelSize * 2.0;
		occlusion += getAoAmount(kernel[i] * texelSize * 8.0 + rndOffs);
	}
	
	occlusion /= 16.0;
	occlusion = clamp(occlusion, 0.0, 0.5) + 0.5;
	
	gl_FragColor = vec4(vec3(occlusion), 1.0);
}