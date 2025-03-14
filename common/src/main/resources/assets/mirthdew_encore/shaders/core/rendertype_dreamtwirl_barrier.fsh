#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec2 ScreenSize;
uniform float GameTime;
uniform vec3 ChunkOffset;

in vec3 position;

out vec4 fragColor;

#define PI 3.14159265
#define TAU 6.28318531

#define PIXELS_PER_BLOCK 16.0

void main() {
    vec3 voxelPos = (floor(position * PIXELS_PER_BLOCK) + 0.5) / PIXELS_PER_BLOCK;

    vec3 voxelCameraPos = voxelPos + ChunkOffset;
    float voxelCameraDistance = length(voxelCameraPos);

    float fadeoffFactor = voxelCameraDistance * voxelCameraDistance / 16384.0;
    if(exp(-fadeoffFactor) < 0.15) {
        // discard early
        discard;
    }

    vec4 glowColor = vec4(0.0, 0.0, 0.0, 0.0);
    for(int k_ = 0; k_ <= 4; k_++) {
        float k = float(k_);

        float theta = atan(voxelPos.z, voxelPos.x);
        // divide by the length of voxelpos to ensure y and theta are on the same scale, and add a neat distortion thingy
        float y = voxelPos.y / length(voxelPos * vec3(1.0, 5.0, 1.0));

        // scale y and theta
        float p = pow(2.0, k);
        float scaleFactor = 5.0 * p + 1.0;
        theta *= scaleFactor;
        y *= scaleFactor;

        // offset theta
        theta += glowColor.a * 0.08 * (3.0 - k * 0.5);

        for (int i_ = -3; i_ <= 3; i_++) {
            float i = float(i_);

            // offset y and theta
            y += 1.0;
            theta += 1.0;
            theta += -glowColor.a * 0.007 * i * i / p;

            // calc w
            float thetaContribution = -theta * 5.0 * i;
            float yContribution = y * -5.0 * i * i;
            float timeContribution = TAU * GameTime * (k + 1.0) * (55.0 * i + 12.0 * i * i);
            float offsetContribution = k * k + i * i * i;

            float w = sin(thetaContribution + yContribution + timeContribution + offsetContribution);
            w += 0.39;

            // limit values
            float h = 13.0;
            w = sign(w) * sqrt((floor(w * w * h) + 0.5) / h);

            // fadeoff away from camera
            float fadeoff = exp(-fadeoffFactor * sqrt(p));
            fadeoff = (clamp(fadeoff, 0.15, 0.65) - 0.15) / 0.5;
            w *= fadeoff;

            // add to glowcolor
            float a = sin(y + theta + i + k + TAU * GameTime * (k + 1.0) * k * 7.0 + glowColor.a) * 1.2;
            vec4 color = vec4(a, exp(-a * a), 1.0, 1.0);
            glowColor += w * color;
        }
    }

    // vertical fadeoff
    glowColor *= exp(-voxelPos.y * voxelPos.y / 200000.);

    // adjustments
    glowColor *= 0.1;
    glowColor = 0.7 * glowColor + 0.3 * sqrt(glowColor);
    glowColor = clamp(glowColor, 0.0, 1.0);
    glowColor.a *= 0.9;

    // background distortion
    float distortionDistance = 0.005 * glowColor.a;

    vec2 screenUV = gl_FragCoord.xy / ScreenSize.xy;
    float gc = glowColor.r + glowColor.g + glowColor.b + glowColor.a;
    vec2 offset = vec2(cos(gc * 4.0), sin(gc * 4.0));

    vec4 colA = texture(Sampler0, screenUV + distortionDistance * offset);
    vec4 colB = texture(Sampler0, screenUV + distortionDistance * -offset);
    vec3 distortedColor = vec3(colA.r, colB.g, 0.5 * (colA.b + colB.b));

    // darken distorted color behind brighter borders, potentially going negative
    distortedColor *= (1.0 - 2.5 * glowColor.a * glowColor.a);

    // mix alpha
    vec4 outputColor = vec4(glowColor.a * glowColor.rgb + (1.0 - glowColor.a) * distortedColor, 1.0);

    // output
    fragColor = outputColor;
}
