#version 150

#moj_import <fog.glsl>

in vec3 Position;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

out vec3 position;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    position = vec3(ModelViewMat * vec4(Position, 1.0));
}
