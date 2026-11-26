#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;
uniform float Intensity;
uniform float Time;
uniform float DayPhase;
uniform float MorningPhase;
uniform float PitchFactor;
uniform float RiftWarp;

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
    float e = max(l, maxC * 0.90);
    float w = smoothstep(thresh, thresh + 0.45, e);
    return s * w * (1.0 + 0.75 * w);
}

void main() {
    float str = clamp(Intensity * 1.25, 0.0, 1.0);
    float warp = clamp(RiftWarp, 0.0, 1.0);
    float nightPhase = clamp(DayPhase, 0.0, 1.0);
    float abyssPhase = clamp(-DayPhase, 0.0, 1.0);
    float mornPhase = clamp(MorningPhase, 0.0, 1.0);
    float skyLook = clamp(PitchFactor, 0.0, 1.0);

    // Zifiri Bataklikta (abyssPhase > 0.5) MorningPhase uniformu Zifiri Mesale'nin elde tutulma oranidir!
    float torchHeld = abyssPhase * mornPhase;
    float dawnOnly  = (1.0 - abyssPhase) * mornPhase;

    vec2 uv = texCoord;
    vec2 tx = 1.0 / max(InSize, vec2(1.0));
    vec2 centerVec = uv - vec2(0.5);
    float dist = length(centerVec);
    float distSq = dot(centerVec, centerVec);

    // BOYUT YARIGI SINEMATIK BUKULME (Dimensional Rift Wormhole Distortion when using Yarik Asasi!)
    if (warp > 0.005) {
        float angle = atan(centerVec.y, centerVec.x);
        float swirl = warp * warp * 0.42 * (1.0 - smoothstep(0.0, 0.68, dist)) * sin(Time * 3.2 - dist * 14.0);
        float radialPull = 1.0 - warp * 0.18 * sin(dist * 10.0 - Time * 5.0) * smoothstep(0.05, 0.55, dist);
        float newAngle = angle + swirl;
        uv = vec2(0.5) + vec2(cos(newAngle), sin(newAngle)) * (dist * radialPull);
        uv = clamp(uv, 0.002, 0.998);
        centerVec = uv - vec2(0.5);
    }

    // 1. Kromatik Prizma Kirilmasi
    vec2 ca = centerVec * (distSq * (0.0045 + 0.0025 * abyssPhase) + warp * 0.016 * dist) * str;
    float rCh = texture(DiffuseSampler, clamp(uv + ca, 0.001, 0.999)).r;
    float gCh = texture(DiffuseSampler, uv).g;
    float bCh = texture(DiffuseSampler, clamp(uv - ca, 0.001, 0.999)).b;
    vec3 centerCol = vec3(rCh, gCh, bCh);

    // 2. Contrast-Adaptive Sharpening (CAS)
    vec3 colN = texture(DiffuseSampler, clamp(uv + vec2(0.0, -tx.y), 0.001, 0.999)).rgb;
    vec3 colS = texture(DiffuseSampler, clamp(uv + vec2(0.0,  tx.y), 0.001, 0.999)).rgb;
    vec3 colE = texture(DiffuseSampler, clamp(uv + vec2( tx.x, 0.0), 0.001, 0.999)).rgb;
    vec3 colW = texture(DiffuseSampler, clamp(uv + vec2(-tx.x, 0.0), 0.001, 0.999)).rgb;
    vec3 avgCross = (colN + colS + colE + colW) * 0.25;
    vec3 baseCol = clamp(centerCol + (centerCol - avgCross) * 0.26 * str, 0.0, 1.0);

    // 3. 24-Tap Multi-Ring Starlight & Dawn HDR Bloom
    float bThresh = 0.34 - 0.12 * warp - 0.04 * dawnOnly;
    vec3 bloomCore = vec3(0.0);
    bloomCore += extractHDR(uv, vec2( 1.5,  0.0) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2(-1.5,  0.0) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2( 0.0,  1.5) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2( 0.0, -1.5) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2( 2.4,  2.4) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2(-2.4,  2.4) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2( 2.4, -2.4) * tx, bThresh);
    bloomCore += extractHDR(uv, vec2(-2.4, -2.4) * tx, bThresh);

    vec3 bloomMid = vec3(0.0);
    bloomMid += extractHDR(uv, vec2( 5.0,  0.0) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2(-5.0,  0.0) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2( 0.0,  5.0) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2( 0.0, -5.0) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2( 6.5,  6.5) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2(-6.5,  6.5) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2( 6.5, -6.5) * tx, bThresh);
    bloomMid += extractHDR(uv, vec2(-6.5, -6.5) * tx, bThresh);

    vec3 bloomWide = vec3(0.0);
    bloomWide += extractHDR(uv, vec2( 12.0,  0.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2(-12.0,  0.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2(  0.0, 11.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2(  0.0,-11.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2( 20.0,  4.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2(-20.0,  4.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2( 20.0, -4.0) * tx, bThresh);
    bloomWide += extractHDR(uv, vec2(-20.0, -4.0) * tx, bThresh);

    vec3 totalBloom = (bloomCore * 0.45 + bloomMid * 0.35 + bloomWide * 0.20) * (0.145 + 0.12 * warp);

    // 4. Nightfall Yildiz Isigi, Sabah Altin Safak & Zifiri Bataklik Tonlamasi
    float luma = getLuma(baseCol);
    vec3 shTone = mix(vec3(0.62, 0.68, 0.90), vec3(0.45, 0.62, 0.96), nightPhase);
    vec3 mdTone = mix(vec3(0.95, 0.94, 1.02), vec3(0.76, 0.88, 1.08), nightPhase);
    vec3 hiTone = mix(vec3(1.08, 1.02, 0.96), vec3(0.88, 0.98, 1.18), nightPhase);

    shTone = mix(shTone, vec3(0.82, 0.68, 0.78), dawnOnly * 0.55);
    mdTone = mix(mdTone, vec3(1.06, 0.95, 0.86), dawnOnly * 0.65);
    hiTone = mix(hiTone, vec3(1.18, 1.04, 0.84), dawnOnly * 0.75);

    float shMask = 1.0 - smoothstep(0.0, 0.42, luma);
    float hiMask = smoothstep(0.42, 0.92, luma);
    float mdMask = clamp(1.0 - shMask - hiMask, 0.0, 1.0);

    vec3 graded = mix(baseCol, baseCol * (shTone * shMask + mdTone * mdMask + hiTone * hiMask), 0.65 * str);
    graded += vec3(0.028, 0.052, 0.125) * (1.0 - luma) * str * nightPhase;

    vec3 bloomTint = mix(vec3(0.72, 0.90, 1.18), vec3(1.16, 0.96, 0.72), dawnOnly);
    graded += totalBloom * bloomTint * (0.85 + 0.15 * skyLook) * str * (1.0 - 0.75 * abyssPhase);

    // ZIFIRI BATAKLIK:
    // - Mesale yokken: zifiri siyah (#000000) DEGIL; etrafimizi "zor" gorecegimiz ama bastigimiz blogu,
    //   agaclarin siluetini ve 3RD (F5) kamerada kendi karakterimizi hafifce secebilecegimiz los karanlik!
    // - Mesale eldeyken: tum haritayi veya uzak agaclari ASLA aydinlatmaz; yalnizca oyuncunun 3 blokluk
    //   yakin cevresindeki mesale isigini (Blocks.LIGHT seviye 5) dogal ve sicak sekilde gosterir!
    if (abyssPhase > 0.005) {
        // Mor yaprak (karanlik_yaprak) veya gokyuzu piksellerini koyu bataklik komur tonuna indirge (uzak agaclar parlamasin!):
        float purpleMask = clamp((baseCol.b - baseCol.g * 1.15) * 8.0, 0.0, 1.0);
        vec3 darkLeafCol = vec3(luma * 0.28, luma * 0.30, luma * 0.33);
        vec3 swampCol = mix(baseCol, darkLeafCol, purpleMask);
        float swampLuma = getLuma(swampCol);

        vec3 charcoal = vec3(swampLuma * 0.86, swampLuma * 0.88, swampLuma * 0.94);
        float detailLift = smoothstep(0.004, 0.06, swampLuma) * 0.018;
        vec3 dimSwamp = mix(swampCol, charcoal, 0.62) * 0.76 + vec3(detailLift * 0.92, detailLift * 0.95, detailLift * 1.05);

        // Agaclardaki parlayan gozler (fullbright luma > 0.52) karanlikta parlamaya devam eder!
        float eyeGlowMask = smoothstep(0.50, 0.76, luma) * (1.0 - purpleMask);
        vec3 swampScene = mix(dimSwamp, baseCol * 1.12, eyeGlowMask);

        if (torchHeld > 0.005) {
            float flicker = 0.96 + 0.04 * (0.6 * sin(Time * 14.0) + 0.4 * cos(Time * 21.0));
            // Sadece dunyadaki 3 blokluk yakin mesale isiginin (Blocks.LIGHT seviye 5) ulastigi yuzeyleri isit:
            float nearWorldLight = smoothstep(0.090, 0.22, swampLuma) * (1.0 - purpleMask);
            float nearScreenMask = 1.0 - smoothstep(0.16, 0.44, dist);
            float torchMask = nearWorldLight * (0.42 + 0.58 * nearScreenMask) * torchHeld;

            vec3 warmNear = swampCol * vec3(1.16, 0.95, 0.72) * flicker;
            swampScene = mix(swampScene, warmNear, torchMask);
        }

        graded = mix(graded, swampScene, abyssPhase);
    }

    // 5. SINEMATIK BOYUT KORIDORU & YILDIZ AKISI EFEKTI (Sadece Yarik Asasi ile isinlanirken calisir!)
    if (warp > 0.005) {
        float ang = atan(centerVec.y, centerVec.x);
        float streak1 = pow(max(0.0, sin(ang * 24.0 + Time * 4.5 + dist * 18.0)), 8.0);
        float streak2 = pow(max(0.0, cos(ang * 36.0 - Time * 6.2 - dist * 26.0)), 10.0);
        float ringWave = 0.5 + 0.5 * sin(dist * 28.0 - Time * 9.0 + sin(ang * 6.0) * 1.5);
        float edgeMask = smoothstep(0.08, 0.58, dist);

        vec3 portalViolet = vec3(0.62, 0.28, 1.0);
        vec3 portalCyan   = vec3(0.32, 0.92, 1.0);
        vec3 portalGold   = vec3(1.0, 0.86, 0.42);

        vec3 tunnelGlow = (portalCyan * streak1 + portalGold * streak2 * 0.85 + portalViolet * ringWave * 0.45) * edgeMask * warp * 0.95;
        graded += tunnelGlow;

        float flashBurst = smoothstep(0.72, 1.0, warp);
        float coreGlow = exp(-dist * 3.2) * flashBurst * 1.35;
        graded += mix(portalCyan, vec3(1.0, 0.96, 1.0), 0.6) * coreGlow;
    }

    // Yumusak kenar vignette (bastigimiz yeri ve karakterimizi karartmaz!)
    float vigFactor = (0.34 + 0.25 * warp + 0.16 * abyssPhase) * str;
    float vig = 1.0 - distSq * vigFactor;
    graded *= clamp(vig, 0.58, 1.0);

    vec3 filmic = acesFitted(graded * (1.10 + 0.14 * str));
    fragColor = vec4(clamp(mix(centerCol, filmic, str), 0.0, 1.0), 1.0);
}
