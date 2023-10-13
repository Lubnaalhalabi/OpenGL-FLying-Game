#version 330 core
out vec4 FragColor;

in vec3 texCoords;
in float fogDistance;

uniform samplerCube skybox;
uniform vec4 fogColor;
uniform float fogDensity;

void main()
{    
    // Calculate fog factor
    float fogFactor = exp(-fogDensity * fogDistance * fogDistance);

    // Apply fog to skybox color
    vec4 skyboxColor = texture(skybox, texCoords);
    FragColor = mix(fogColor, skyboxColor, fogFactor);
}