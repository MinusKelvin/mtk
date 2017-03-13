#version 330 core

layout(location = 0) in vec2 pos;

uniform mat4 proj;

out vec2 texcoord;

const vec2 tex[4] = vec2[4](vec2(0,1), vec2(1,1), vec2(0,0), vec2(1,0));

void main() {
	gl_Position = proj * vec4(pos, 0.0, 1.0);
	texcoord = tex[gl_VertexID];
}
