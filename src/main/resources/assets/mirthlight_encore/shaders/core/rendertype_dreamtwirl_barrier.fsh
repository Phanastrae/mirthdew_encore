#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec2 ScreenSize;
uniform float GameTime;

in vec3 position;

out vec4 fragColor;

#define PI 3.14159265
#define TAU 6.28318531

void main() {
    vec2 screenUV = gl_FragCoord.xy / ScreenSize.xy;
    vec2 centeredScreenUv = screenUV * 2. - 1.;

    float l = length(position);

    float r = length(centeredScreenUv);
    float theta = atan(centeredScreenUv.y, centeredScreenUv.x);

    float t = 0.;
    for(int i = 0; i < 5; i++) {
        float s = float(i % 2) * 2. - 1.;
        t += sin(s * float(i * i) * l * 0.08 - s * float(i) * 200.0 * TAU * GameTime);
    }
    t /= 5.;
    float k = 0.5 + 0.5 * t;
    float g = (1.0 - exp(-pow(k, 10.0) * 10000.0)) * exp(-l * 0.03);
    float f = 0.03 * g;

    vec4 color1 = texture(Sampler0, screenUV + vec2(f, 0.0));
    vec4 color2 = texture(Sampler0, screenUV + vec2(-f, 0.0));
    vec4 color = (1.0 - g * 0.5) * vec4(color1.r, color2.g, 0.5 * (color1.b + color2.b), 1.0);

    //color = vec4(k, 0.0, 0.0, 1.0);

    fragColor = vec4(color.rgb, 1.0);
}
