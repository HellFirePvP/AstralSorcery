#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D TransparencySampler;
uniform sampler2D TransparencyDepthSampler;
uniform vec4 ColorOverlay;
uniform int IgnoreDepth;

in vec2 texCoord;

float depth, depthTransparency;
vec4 color, colorTransparency;

out vec4 fragColor;

vec4 blend(vec4 dst, vec4 src) {
    vec3 srcRgb = src.rgb * ColorOverlay.rgb;
    float srcA = src.a * ColorOverlay.a;

    //additive
    //vec3 rgb = srcRgb + dst.rgb;
    //float a = srcA + dst.a * (1.0 - srcA);
    //return vec4(rgb, a);

    //default
    vec3 rgb = srcRgb * srcA + dst.rgb * (1.0 - srcA);
    float a = srcA + dst.a * (1.0 - srcA);
    return vec4(rgb, a);
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    float depth = texture(DiffuseDepthSampler, texCoord).r;

    vec4 colorTransparency = texture(TransparencySampler, texCoord);
    float depthTransparency = texture(TransparencyDepthSampler, texCoord).r;

    if (colorTransparency.a != 0.0 && (IgnoreDepth > 0 || depthTransparency < depth)) {
        color = blend(color, colorTransparency);
    }
    fragColor = color;
}