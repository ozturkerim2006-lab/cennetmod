#version 150

uniform sampler2D DiffuseSampler;
uniform float Intensity;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 c = texture(DiffuseSampler, texCoord).rgb;
    float l = dot(c, vec3(0.299, 0.587, 0.114));

    // Kontrast: orta tonlar etrafında S eğrisi
    vec3 con = (c - 0.5) * (1.0 + 0.35 * Intensity) + 0.5;

    // Split-tone: gölgeler mor, ışıklar turkuaz
    vec3 golge = vec3(0.60, 0.30, 1.00);
    vec3 isik  = vec3(0.35, 1.00, 0.95);
    vec3 ton = mix(golge, isik, smoothstep(0.12, 0.75, l));
    vec3 renkli = mix(con, con * ton * 1.5, 0.6 * Intensity);

    // Karanlık yerlere hafif mor ışık, doygunluk artışı
    renkli += golge * 0.035 * Intensity;
    float l2 = dot(renkli, vec3(0.299, 0.587, 0.114));
    renkli = mix(vec3(l2), renkli, 1.0 + 0.25 * Intensity);

    fragColor = vec4(clamp(renkli, 0.0, 1.0), 1.0);
}
