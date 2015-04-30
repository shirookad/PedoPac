#extension GL_EXT_gpu_shader4 : enable

uniform mat4 projectionMatrix;

uniform vec2 screenSize;
uniform float tanHalfFov;
uniform float aspectRatio;

uniform sampler2D randomTexture;
uniform sampler2D kernelTexture;

#define SAMPLE_KERNEL_SIZE 16.0
#define RADIUS 1.5

void main() {
	vec3 viewRay = normalize(vec3(
		(gl_TexCoord[0].x * 2.0 - 1.0) * tanHalfFov * aspectRatio,
		(gl_TexCoord[0].y * 2.0 - 1.0) * tanHalfFov,
		1.0
		));
	
	vec3 origin = viewRay * depth;
	
	vec2 noiseScale = screenSize * 0.25;
	
	vec3 rvec = texture2D(randomTexture, gl_TexCoord[0].xy * noiseScale).xyz * 2.0 - 1.0;
	vec3 tangent = normalize(rvec - normal * dot(rvec, normal));
	vec3 bitangent = cross(normal, tangent);
	mat3 tbn = mat3(tangent, bitangent, normal);
	
	float occlusion = 0.0;
	for (int i = 0; i < SAMPLE_KERNEL_SIZE; ++i) {
		vec2 kernelUV = vec2(float(i % 4) * 0.25, float(i >> 2) * 0.25);
		vec3 sample = tbn * texture2D(kernelTexture, kernelUV).xyz;
		sample = sample * RADIUS + origin;
	  	
		vec4 offset = vec4(sample, 1.0);
		offset = projectionMatrix * offset;
		offset.xy /= offset.w;
		offset.xy = offset.xy * 0.5 + 0.5;
	  
		float sampledDepth = sampleDepth(offset.xy);
	
		float rangeCheck = abs(origin.z - sampledDepth) < RADIUS ? 1.0 : 0.0;
		occlusion += (sampledDepth <= sample.z ? 1.0 : 0.0) * rangeCheck;
	}
	
	occlusion = 1.0 - (occlusion / SAMPLE_KERNEL_SIZE);
	occlusion = pow(occlusion, 2.0);

	gl_FragColor = vec4(vec3(occlusion), 1.0);
}