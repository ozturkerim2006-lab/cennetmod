#version 150

uniform sampler2D DiffuseSampler;
uniform float Intensity;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 c = texture(DiffuseSampler, texCoord).rgb;
    float l = dot(c, vec3(0.299, 0.587, 0.114));

    // Yumuşak kontrast: orta tonlar hafifçe açılır
    vec3 con = (c - 0.5) * (1.0 - 0.06 * Intensity) + 0.5;

    // Split-tone: gölgeler lavanta, ışıklar sıcak inci-altın (orta boyutun mor/turkuazıyla uyumlu ama aydınlık)
    vec3 golge = vec3(0.80, 0.74, 1.00);
    vec3 isik  = vec3(1.00, 0.96, 0.86);
    vec3 ton = mix(golge, isik, smoothstep(0.10, 0.85, l));
    vec3 renkli = mix(con, con * ton * 1.08, 0.7 * Intensity);

    // Siyahları kaldır: gölgelere hafif lavanta ışık
    renkli += vec3(0.055, 0.045, 0.085) * Intensity;
    // Parlak yerlere hafif ışıma
    renkli += isik * smoothstep(0.70, 1.0, l) * 0.05 * Intensity;

    float l2 = dot(renkli, vec3(0.299, 0.587, 0.114));
    renkli = mix(vec3(l2), renkli, 1.0 + 0.12 * Intensity);

    fragColor = vec4(clamp(renkli, 0.0, 1.0), 1.0);
}
