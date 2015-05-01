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
		vec2 tx = offset + float(i) * step;
		tx.x = clamp(tx.x, 0.0, 1.0 - texelSize.y);
		tx.y = clamp(tx.y, 0.0, 1.0 - texelSize.y);
		color += texture2D(inputTexture, tx);
	}
	color /= float(blurRadius << 1);
	gl_FragColor = color;
}