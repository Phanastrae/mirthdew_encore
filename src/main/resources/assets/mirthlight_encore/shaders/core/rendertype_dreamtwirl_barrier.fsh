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

    float l = length(position);
    l = l * l;
    float j = l * (0.75 + 0.25 * sin(l - TAU * GameTime * 400.0));

    float f = 0.15 * max(0.0, 1.0 - j * 0.05);
    float g = min(1.0, j * 0.08);

    vec4 color1 = texture(Sampler0, screenUV + vec2(f, 0.0));
    vec4 color2 = texture(Sampler0, screenUV + vec2(-f, 0.0));

    vec4 color = g * g * vec4(color1.r, color2.g, 0.5 * (color1.b + color2.b), 1.0);

    float alpha = 0.6 * (1.0 - g * g);
    color = (1.0 - alpha) * color + alpha * vec4(0.8 + g * 0.2, 0.2 + g * 0.2, 0.5 + g * 0.2, 1.0);

    alpha = 0.4 * (1.0 - g);
    color = (1.0 - alpha) * color + alpha * vec4(0.1, 0.05, 0.1, 1.0);

    fragColor = vec4(color.rgb, 1.0);
}
