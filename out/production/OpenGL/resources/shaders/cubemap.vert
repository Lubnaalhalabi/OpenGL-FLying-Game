#version 330 core
layout(location = 0) in vec3 aPosition;

out vec3 texCoords;
out float fogDistance;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    gl_Position = projection * view * model * vec4(aPosition, 1.0);
    // We want to flip the z axis due to the different coordinate systems (left hand vs right hand)
    texCoords = vec3(aPosition.x, aPosition.y, -aPosition.z);

    // Calculate fog distance
    vec3 cameraPosition = vec3(view[3]);
    fogDistance = length(gl_Position.xyz - cameraPosition);
}