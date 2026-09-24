#version 150

uniform float bloomIntensity = 0.6;
uniform float bloomThreshold = 0.6;
uniform float bloomRadius = 2.5;

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    if (color.a < 0.0001) discard;

    ivec2 size = textureSize(Sampler0, 0);
    vec2 texel = 1.0 / vec2(size);

    float lum = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    float bloomMask = max(lum - bloomThreshold, 0.0);

    vec3 bloom = vec3(0.0);

    bloom += texture(Sampler0, texCoord0).rgb * 0.25;

    bloom += texture(Sampler0, texCoord0 + vec2( texel.x, 0) * bloomRadius).rgb * 0.125;
    bloom += texture(Sampler0, texCoord0 + vec2(-texel.x, 0) * bloomRadius).rgb * 0.125;
    bloom += texture(Sampler0, texCoord0 + vec2(0,  texel.y) * bloomRadius).rgb * 0.125;
    bloom += texture(Sampler0, texCoord0 + vec2(0, -texel.y) * bloomRadius).rgb * 0.125;

    bloom += texture(Sampler0, texCoord0 + vec2( texel.x,  texel.y) * bloomRadius).rgb * 0.0625;
    bloom += texture(Sampler0, texCoord0 + vec2(-texel.x,  texel.y) * bloomRadius).rgb * 0.0625;
    bloom += texture(Sampler0, texCoord0 + vec2( texel.x, -texel.y) * bloomRadius).rgb * 0.0625;
    bloom += texture(Sampler0, texCoord0 + vec2(-texel.x, -texel.y) * bloomRadius).rgb * 0.0625;

    bloom *= bloomMask;

    vec3 finalColor = color.rgb + bloom * bloomIntensity;
    fragColor = vec4(finalColor, color.a) * ColorModulator;
}
