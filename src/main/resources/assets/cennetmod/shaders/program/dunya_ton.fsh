#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;
uniform float Intensity;
uniform float Time;
uniform float DayPhase;
uniform float MorningPhase;
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
    float e = max(l, maxC * 0.88);
    float w = smoothstep(thresh, thresh + 0.44, e);
    return s * w * (1.0 + 0.68 * w);
}


void main() {
    float str = clamp(Intensity * 1.28, 0.0, 1.0);
    float morn = clamp(MorningPhase, 0.0, 1.0);
    vec2 uv = texCoord;
    vec2 tx = 1.0 / max(InSize, vec2(1.0));
    vec2 centerVec = uv - vec2(0.5);
    float distSq = dot(centerVec, centerVec);

    // 1. Ince Optik Mercek Kirilmasi (Micro Chromatic Aberration)
    vec2 ca = centerVec * distSq * 0.0038 * str;
    float rCh = texture(DiffuseSampler, clamp(uv + ca, 0.001, 0.999)).r;
    float gCh = texture(DiffuseSampler, uv).g;
    float bCh = texture(DiffuseSampler, clamp(uv - ca, 0.001, 0.999)).b;
    vec3 centerCol = vec3(rCh, gCh, bCh);

    // 2. Contrast-Adaptive Sharpening (CAS)
    vec3 colN = texture(DiffuseSampler, clamp(uv + vec2(0.0, -tx.y), 0.001, 0.999)).rgb;
    vec3 colS = texture(DiffuseSampler, clamp(uv + vec2(0.0,  tx.y), 0.001, 0.999)).rgb;
    vec3 colE = texture(DiffuseSampler, clamp(uv + vec2( tx.x, 0.0), 0.001, 0.999)).rgb;
    vec3 colW = texture(DiffuseSampler, clamp(uv + vec2(-tx.x, 0.0), 0.001, 0.999)).rgb;
    vec3 minN = min(min(colN, colS), min(colE, colW));
    vec3 maxN = max(max(colN, colS), max(colE, colW));
    float localContrast = getLuma(maxN - minN);
    float sharpWeight = clamp(1.0 - localContrast * 2.2, 0.0, 1.0) * 0.30 * str;
    vec3 avgCross = (colN + colS + colE + colW) * 0.25;
    vec3 baseCol = clamp(centerCol + (centerCol - avgCross) * sharpWeight, 0.0, 1.0);

    // 3. 24-Tap Multi-Ring HDR Kawase + Sabah Güneş Parıltısı (Morning Sunlit Bloom)
    float bThresh = mix(mix(0.41, 0.31, morn), 0.35, clamp(DayPhase, 0.0, 1.0));
    vec3 bloomCore = vec3(0.0);
    bloomCore += extractHDR(uv, vec2( 1.5,  0.0) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2(-1.5,  0.0) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2( 0.0,  1.5) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2( 0.0, -1.5) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2( 2.2,  2.2) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2(-2.2,  2.2) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2( 2.2, -2.2) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2(-2.2, -2.2) * tx, bThresh);

    vec3 bloomMid = vec3(0.0);
    bloomMid += extractHDR(uv, vec2( 4.5,  0.0) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2(-4.5,  0.0) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2( 0.0,  4.5) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2( 0.0, -4.5) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2( 6.0,  6.0) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2(-6.0,  6.0) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2( 6.0, -6.0) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2(-6.0, -6.0) * tx, bThresh);

    vec3 bloomWide = vec3(0.0);
    bloomWide += extractHDR(uv, vec2( 11.0,   0.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2(-11.0,   0.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2(  0.0,  10.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2(  0.0, -10.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2( 18.0,   4.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2(-18.0,   4.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2( 18.0,  -4.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2(-18.0,  -4.0) * tx, bThresh);

    vec3 totalBloom = (bloomCore * 0.45 + bloomMid * 0.35 + bloomWide * 0.20) * (0.135 + 0.045 * morn);

    // 4. Sabaha Ozel Hacimsel Safak Huzmeleri (Morning Golden God-Rays)
    vec2 lightPos = vec2(0.5 + 0.12 * sin(Time * 0.05), mix(0.84, 0.78, morn));
    vec2 rayDelta = (lightPos - uv) * 0.034;
    float rayThresh = mix(0.52, 0.42, morn);
    vec3 rays = vec3(0.0);
    rays += extractHDR(uv, rayDelta * 1.0, rayThresh) * 0.25;
    rays += extractHDR(uv, rayDelta * 2.0, rayThresh) * 0.22;
    rays += extractHDR(uv, rayDelta * 3.0, rayThresh) * 0.19;
    rays += extractHDR(uv, rayDelta * 4.0, rayThresh) * 0.16;
    rays += extractHDR(uv, rayDelta * 5.0, rayThresh) * 0.13;
    rays += extractHDR(uv, rayDelta * 6.0, rayThresh) * 0.10;
    float rayMask = smoothstep(0.10, 0.95, uv.y) * clamp(PitchFactor * 1.38 + 0.25 * morn, 0.42, 1.15);

    // 5. Mikro-AO ve Derinlik Kontrasti
    float midLuma = getLuma(bloomMid * 0.125);
    float luma = getLuma(baseCol);
    float aoTerm = clamp((luma - midLuma) * 0.24, -0.07, 0.09);
    vec3 graded = baseCol * (1.0 + aoTerm * str);

    // 6. Sabah (Golden Dawn & Dew) / Gunduz / Gun Batimi / Gece (Nightfall) 4-Evreli Renk Tonlamasi
    float sunsetFactor = clamp(1.0 - abs(DayPhase - 0.45) * 2.4, 0.0, 1.0);
    float nightFactor  = smoothstep(0.45, 0.95, DayPhase);

    // Sabaha Ozel: Serin Turkuaz Ciy Golgeleri & Sicak Altin-Seftali Safak Isigi
    vec3 mornShadow = vec3(0.72, 0.89, 1.08);
    vec3 mornMid    = vec3(1.11, 1.00, 0.88);
    vec3 mornHigh   = vec3(1.25, 1.05, 0.79);

    // Oglen / Gunduz: Canli Gun Isigi
    vec3 dayShadow = vec3(0.80, 0.90, 1.05);
    vec3 dayMid    = vec3(1.04, 1.02, 0.96);
    vec3 dayHigh   = vec3(1.14, 1.03, 0.88);

    vec3 activeDayShadow = mix(dayShadow, mornShadow, morn);
    vec3 activeDayMid    = mix(dayMid,    mornMid,    morn);
    vec3 activeDayHigh   = mix(dayHigh,   mornHigh,   morn);

    // Gun Batimi
    vec3 sunShadow = vec3(0.58, 0.46, 0.78);
    vec3 sunMid    = vec3(1.06, 0.86, 0.72);
    vec3 sunHigh   = vec3(1.16, 0.88, 0.64);

    // Gece (Nightfall)
    vec3 nightShadow = vec3(0.42, 0.56, 0.88);
    vec3 nightMid    = vec3(0.70, 0.82, 1.04);
    vec3 nightHigh   = vec3(0.82, 0.94, 1.12);

    vec3 shTone = mix(mix(activeDayShadow, sunShadow, sunsetFactor), nightShadow, nightFactor);
    vec3 mdTone = mix(mix(activeDayMid,    sunMid,    sunsetFactor), nightMid,    nightFactor);
    vec3 hiTone = mix(mix(activeDayHigh,   sunHigh,   sunsetFactor), nightHigh,   nightFactor);

    float shMask = (1.0 - smoothstep(0.0, 0.45, luma));
    float hiMask = smoothstep(0.45, 0.95, luma);
    float mdMask = clamp(1.0 - shMask - hiMask, 0.0, 1.0);

    float warmLight = clamp((baseCol.r - baseCol.b) * 1.8, 0.0, 1.0) * smoothstep(0.35, 0.85, luma);
    vec3 colorBalance = shTone * shMask + mdTone * mdMask + hiTone * hiMask;
    colorBalance = mix(colorBalance, vec3(1.12, 0.96, 0.76), warmLight * nightFactor * 0.75);

    graded = mix(graded, graded * colorBalance, (0.72 + 0.12 * morn) * str);

    // Sabaha Ozel Altin-Pembe Ufuk Ciy Pusu + Gece/Gun Batimi Atmosferi
    vec3 mornAmbient   = vec3(0.068, 0.046, 0.024) * smoothstep(0.18, 0.88, uv.y) * morn * (1.0 - nightFactor);
    vec3 nightAmbient  = vec3(0.025, 0.048, 0.115) * (1.0 - luma) * nightFactor;
    vec3 sunsetAmbient = vec3(0.055, 0.025, 0.045) * smoothstep(0.25, 0.90, uv.y) * sunsetFactor;
    graded += (mornAmbient + nightAmbient + sunsetAmbient) * str;

    // 7. Sabaha Ozel Ciy Canliligi (Smart Vibrance)
    float maxCh = max(max(graded.r, graded.g), graded.b);
    float minCh = min(min(graded.r, graded.g), graded.b);
    float sat = maxCh - minCh;
    float vibBoost = (1.0 - sat) * mix(mix(0.32, 0.42, morn), 0.22, nightFactor) * str;
    float gLuma = getLuma(graded);
    graded = mix(vec3(gLuma), graded, 1.0 + vibBoost);

    // 8. HDR Bloom ve Sabaha Ozel Safak Huzmeleri
    vec3 mornRayTint = vec3(1.24, 0.95, 0.64);
    vec3 dayRayTint  = mix(vec3(1.08, 0.97, 0.80), mornRayTint, morn);
    vec3 rayTint = mix(mix(dayRayTint, vec3(1.15, 0.76, 0.50), sunsetFactor), vec3(0.65, 0.86, 1.10), nightFactor);
    graded += totalBloom * mix(vec3(1.0), rayTint, 0.52) * (0.82 + 0.18 * morn) * str;
    graded += rays * rayTint * rayMask * (0.46 + 0.28 * morn) * str;

    // 9. Sinematik Vinyet & ACES Filmic Ton Haritalama
    float vig = 1.0 - distSq * mix(0.28, 0.42, nightFactor) * str;
    graded *= clamp(vig, 0.65, 1.0);

    vec3 filmic = acesFitted(graded * (1.10 + 0.15 * str));
    float dither = (fract(sin(dot(uv * InSize, vec2(12.9898, 78.233))) * 43758.5453) - 0.5) / 255.0;
    vec3 finalCol = mix(centerCol, filmic, str) + dither;

    fragColor = vec4(clamp(finalCol, 0.0, 1.0), 1.0);
}
