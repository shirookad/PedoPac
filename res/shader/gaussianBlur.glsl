uniform sampler2D inputTexture;

uniform int blurRadius;

void main() {
	vec2 step;
#ifdef _GAUSSIANBLUR_VERTICAL
	step = vec2(0, texelSize.y);
#else
	step = vec2(texelSize.x, 0);
#endif
	
	vec4 color = vec4(0);
	vec2 offset = gl_TexCoord[0].xy;
	for (int i = -blurRadius; i <= blurRadius; ++i) {
		color += texture2D(inputTexture, offset + float(i) * step);
	}
	color /= float(blurRadius << 1);
	gl_FragColor = color;
}