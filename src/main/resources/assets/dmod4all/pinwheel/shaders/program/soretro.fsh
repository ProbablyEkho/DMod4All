#version 150

uniform sampler2D DiffuseSampler0;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform float Resolution;
uniform float MosaicSize;
uniform float PlayerY;

out vec4 fragColor;

void main() {
	float nerfResolution = mix(Resolution, 6.0, PlayerY);

    vec2 mosaicInSize = InSize / mix(MosaicSize, 8.0, PlayerY);
    vec2 fractPix = fract(texCoord * mosaicInSize) / mosaicInSize;

    vec4 baseTexel = texture(DiffuseSampler0, texCoord - fractPix);

    vec3 fractTexel = baseTexel.rgb - fract(baseTexel.rgb * nerfResolution) / nerfResolution;
    float luma = dot(fractTexel, vec3(0.3, 0.59, 0.11));
    vec3 chroma = (fractTexel - luma);
    baseTexel.rgb = luma + chroma;
	baseTexel.r -= PlayerY * 0.1;
	baseTexel.g -= PlayerY * 0.1;
    baseTexel.a = 1.0;

    fragColor = baseTexel;
}
