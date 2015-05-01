#extension GL_EXT_gpu_shader4 : enable

uniform sampler2D randomTexture;
uniform sampler2D kernelTexture;

float getLinearizedDepth(float depth) {
	return projectionMatrix[3][2] / (depth - projectionMatrix[2][2]);
}

void main() {
	vec3 viewRay = vec3(
		(gl_TexCoord[0].x * 2.0 - 1.0) * tanHalfFov * aspectRatio,
		(gl_TexCoord[0].y * 2.0 - 1.0) * tanHalfFov,
		1.0
		);
	
	float startDepth = getLinearizedDepth(depth);
	vec3 origin = viewRay * startDepth;
	
	vec2 noiseScale = screenSize * 0.25;
	
	vec3 rvec = normalize(texture2D(randomTexture, gl_TexCoord[0].xy * noiseScale).xyz * 2.0 - 1.0);
	vec3 tangent = normalize(rvec - normal * dot(rvec, normal));
	vec3 bitangent = cross(normal, tangent);
	mat3 tbn = mat3(tangent, bitangent, normal);
	
	float occlusion = 0.0;
	for (int i = 0; i < 16; ++i) {
		vec2 kernelUV = vec2(float(i % 4) * 0.25, float(i >> 2) * 0.25);
		vec3 sample = tbn * (texture2D(kernelTexture, kernelUV).xyz * 2.0 - 1.0);
		sample = sample * 0.5 + origin;
	  	
		vec4 offset = vec4(sample, 1.0);
		offset = projectionMatrix * offset;
		offset.xy /= offset.w;
		offset.xy = offset.xy * 0.5 + 0.5;
	  
		float sampledDepth = getLinearizedDepth(sampleDepth(offset.xy));
	
		float rangeCheck = smoothstep(0.0f, 1.0f, 0.5 / abs(origin.z - sampledDepth));
		occlusion += rangeCheck * step(sampledDepth, sample.z);
	}
	
	occlusion = 1.0 - (occlusion / float(16));
	occlusion = pow(occlusion, 1.0);

	gl_FragColor = vec4(vec3(occlusion), 1.0);
}