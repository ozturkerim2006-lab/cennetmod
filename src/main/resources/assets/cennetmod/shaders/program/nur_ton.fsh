#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;
uniform float Intensity;
uniform float Time;
uniform float DayPhase;
uniform float PitchFactor;

in vec2 texCoord;
out vec4 fragColor;

float getLuma(vec3 c) {
    return dot(c, vec3(0.2126, 0.7152, 0.0722));
}

vec3 acesFitted(vec3 x) {
    vec3 a = x * (x + 0.0245786) - 0.000090537;
    vec3 b = x * (0.983729 * x + 0.4329510) + 0.238081;
    return clamp(a / b, 0.0, 1.0);
}

vec3 extractHDR(vec2 uv, vec2 off, float thresh) {
    vec3 s = texture(DiffuseSampler, clamp(uv + off, 0.001, 0.999)).rgb;
    float l = getLuma(s);
    float maxC = max(max(s.r, s.g), s.b);
    float e = max(l, maxC * 0.85);
    float w = smoothstep(thresh, thresh + 0.48, e);
    return s * w * (1.0 + 0.65 * w);
}


void main() {
    float str = clamp(Intensity * 1.30, 0.0, 1.0);
    vec2 uv = texCoord;
    vec2 tx = 1.0 / max(InSize, vec2(1.0));
    vec2 centerVec = uv - vec2(0.5);
    float distSq = dot(centerVec, centerVec);

    // 1. İnce Kristal Mercek Kırılması
    vec2 ca = centerVec * distSq * 0.0042 * str;
    float rCh = texture(DiffuseSampler, clamp(uv + ca, 0.001, 0.999)).r;
    float gCh = texture(DiffuseSampler, uv).g;
    float bCh = texture(DiffuseSampler, clamp(uv - ca, 0.001, 0.999)).b;
    vec3 centerCol = vec3(rCh, gCh, bCh);

    // 2. Contrast-Adaptive Sharpening (CAS - Kuvars, Altın ve Elf Yapılarını Netleştirir)
    vec3 colN = texture(DiffuseSampler, clamp(uv + vec2(0.0, -tx.y), 0.001, 0.999)).rgb;
    vec3 colS = texture(DiffuseSampler, clamp(uv + vec2(0.0,  tx.y), 0.001, 0.999)).rgb;
    vec3 colE = texture(DiffuseSampler, clamp(uv + vec2( tx.x, 0.0), 0.001, 0.999)).rgb;
    vec3 colW = texture(DiffuseSampler, clamp(uv + vec2(-tx.x, 0.0), 0.001, 0.999)).rgb;
    vec3 minN = min(min(colN, colS), min(colE, colW));
    vec3 maxN = max(max(colN, colS), max(colE, colW));
    float localContrast = getLuma(maxN - minN);
    float sharpWeight = clamp(1.0 - localContrast * 2.1, 0.0, 1.0) * 0.28 * str;
    vec3 avgCross = (colN + colS + colE + colW) * 0.25;
    vec3 baseCol = clamp(centerCol + (centerCol - avgCross) * sharpWeight, 0.0, 1.0);

    // 3. 24-Tap 4-Halkalı Göksel Altın-İnci HDR Bloom
    vec3 bloomCore = vec3(0.0);
    bloomCore += extractHDR(uv, vec2( 1.6,  0.0) * tx, 0.48);
    bloomCore += extractHDR(uv, vec2(-1.6,  0.0) * tx, 0.48);
    bloomCore += extractHDR(uv, vec2( 0.0,  1.6) * tx, 0.48);
    bloomCore += extractHDR(uv, vec2( 0.0, -1.6) * tx, 0.48);
    bloomCore += extractHDR(uv, vec2( 2.6,  2.6) * tx, 0.48);
    bloomCore += extractHDR(uv, vec2(-2.6,  2.6) * tx, 0.48);
    bloomCore += extractHDR(uv, vec2( 2.6, -2.6) * tx, 0.48);
    bloomCore += extractHDR(uv, vec2(-2.6, -2.6) * tx, 0.48);

    vec3 bloomMid = vec3(0.0);
    bloomMid += extractHDR(uv, vec2( 5.2,  0.0) * tx, 0.48);
    bloomMid += extractHDR(uv, vec2(-5.2,  0.0) * tx, 0.48);
    bloomMid += extractHDR(uv, vec2( 0.0,  5.2) * tx, 0.48);
    bloomMid += extractHDR(uv, vec2( 0.0, -5.2) * tx, 0.48);
    bloomMid += extractHDR(uv, vec2( 7.0,  7.0) * tx, 0.48);
    bloomMid += extractHDR(uv, vec2(-7.0,  7.0) * tx, 0.48);
    bloomMid += extractHDR(uv, vec2( 7.0, -7.0) * tx, 0.48);
    bloomMid += extractHDR(uv, vec2(-7.0, -7.0) * tx, 0.48);

    vec3 bloomWide = vec3(0.0);
    bloomWide += extractHDR(uv, vec2( 13.0,   0.0) * tx, 0.50);
    bloomWide += extractHDR(uv, vec2(-13.0,   0.0) * tx, 0.50);
    bloomWide += extractHDR(uv, vec2(  0.0,  12.0) * tx, 0.50);
    bloomWide += extractHDR(uv, vec2(  0.0, -12.0) * tx, 0.50);
    bloomWide += extractHDR(uv, vec2( 20.0,   4.5) * tx, 0.50);
    bloomWide += extractHDR(uv, vec2(-20.0,   4.5) * tx, 0.50);
    bloomWide += extractHDR(uv, vec2( 20.0,  -4.5) * tx, 0.50);
    bloomWide += extractHDR(uv, vec2(-20.0,  -4.5) * tx, 0.50);

    vec3 totalBloom = (bloomCore * 0.44 + bloomMid * 0.36 + bloomWide * 0.20) * 0.125;

    // 4. İlahi Güneş Huzmeleri (Divine Volumetric Sunbeams)
    vec2 lightPos = vec2(0.5, 0.86);
    vec2 rayDelta = (lightPos - uv) * 0.032;
    vec3 rays = vec3(0.0);
    rays += extractHDR(uv, rayDelta * 1.0, 0.55) * 0.24;
    rays += extractHDR(uv, rayDelta * 2.0, 0.55) * 0.21;
    rays += extractHDR(uv, rayDelta * 3.0, 0.55) * 0.18;
    rays += extractHDR(uv, rayDelta * 4.0, 0.55) * 0.15;
    rays += extractHDR(uv, rayDelta * 5.0, 0.55) * 0.12;
    rays += extractHDR(uv, rayDelta * 6.0, 0.55) * 0.10;
    float rayMask = smoothstep(0.18, 0.96, uv.y) * clamp(PitchFactor * 1.35, 0.4, 1.0);

    // 5. Göksel İnci-Altın Atmosfer Tonlaması
    float luma = getLuma(baseCol);
    vec3 skyShadow = vec3(0.78, 0.88, 1.05);
    vec3 goldenSun = vec3(1.06, 0.98, 0.86);
    vec3 tone = mix(skyShadow, goldenSun, smoothstep(0.12, 0.85, luma));

    vec3 graded = mix(baseCol, baseCol * tone * 1.04, 0.62 * str);
    graded += vec3(0.030, 0.042, 0.072) * (1.0 - luma) * str;

    float maxCh = max(max(graded.r, graded.g), graded.b);
    float minCh = min(min(graded.r, graded.g), graded.b);
    float vibBoost = (1.0 - (maxCh - minCh)) * 0.22 * str;
    graded = mix(vec3(getLuma(graded)), graded, 1.0 + vibBoost);

    graded += totalBloom * goldenSun * 0.78 * str;
    graded += rays * vec3(1.08, 0.96, 0.78) * rayMask * 0.40 * str;

    vec3 filmic = acesFitted(graded * (1.05 + 0.10 * str));
    float dither = (fract(sin(dot(uv * InSize, vec2(12.9898, 78.233))) * 43758.5453) - 0.5) / 255.0;
    vec3 finalCol = mix(centerCol, filmic, str) + dither;

    fragColor = vec4(clamp(finalCol, 0.0, 1.0), 1.0);
}
