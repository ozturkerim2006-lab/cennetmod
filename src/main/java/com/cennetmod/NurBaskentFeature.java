package com.cennetmod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Nur Başkenti: düz bulut ovasının üstüne kodla üretilen şehir.
 *
 * Her parça (chunk) kendi sütunlarını çizer; yapılar koordinatların bir fonksiyonudur, bu yüzden
 * chunk sınırlarında kusursuz birleşir. Merkez (0,0). Yer: ZEMIN (üst bulut bloğunun Y'si).
 *
 * Düzen: ortada Nur Sarayı (kubbe, minareler, güneş küresi, gökyüzüne uzanan ışık sütunu) + meydan, dört cadde,
 * halka kanal/yol, içte gökdelen semti, dışta bahçeli köy evleri, 16 gözcü kuleli sur, güneyde (Z+) ana kapı,
 * kapıda ve saray önünde dev melek heykelleri, şehrin üstünde süzülen bulut adaları (şelaleli), dışında bulut dağları.
 */
public class NurBaskentFeature extends Feature<NoneFeatureConfiguration> {
    public static final int ZEMIN = 100;
    private static final int G = ZEMIN + 1;      // zeminin üstündeki ilk hava bloğu
    private static final int KAPI = 288;         // güney kapının merkeze uzaklığı (Z+)
    private static final int MAXH = 64;          // genel yapı yüksekliği
    private static final int MAXH_ORTA = 100;    // gökdelen semti
    private static final int MAXH_HEYKEL = 96;   // dev heykeller
    private static final int MAXH_SARAY = 218;   // saray + ışık sütunu (G+217 = 318 < 320)
    private static final int SEHIR_R = 350;

    private static final int FL = 4;             // saray zemin kotu (platform üstü)
    private static final int DOME_CY = FL + 44;  // kubbe merkezi
    private static final double DOME_R = 21.0;

    /** {merkezX, merkezZ, bakışX, bakışZ, ölçek*100} */
    private static final int[][] HEYKEL = {
            {-34, 250, 1, 0, 100},    // kapı koruyucusu (batı): doğuya, yola bakar
            {34, 250, -1, 0, 100},    // kapı koruyucusu (doğu): batıya bakar
            {-28, 64, 0, 1, 80},      // saray muhafızı: güneye (meydana) bakar
            {28, 64, 0, 1, 80}};

    // bulut adaları
    private static final int N_ADA = 14;
    private static final int[] ACX = new int[N_ADA], ACZ = new int[N_ADA], ATY = new int[N_ADA],
            AR = new int[N_ADA], AD = new int[N_ADA];
    private static final int[] SX = {7, 5, 0, -5, -7, -5, 0, 5};
    private static final int[] SZ = {0, 5, 7, 5, 0, -5, -7, -5};
    static {
        for (int i = 0; i < N_ADA; i++) {
            double ang = i * 2.399963;
            double rad = 310 + (i % 5) * 27 + hash(i, 5) % 18;
            ACX[i] = (int) Math.round(Math.cos(ang) * rad);
            ACZ[i] = (int) Math.round(Math.sin(ang) * rad);
            ATY[i] = 56 + hash(i, 9) % 74;
            AR[i] = 15 + hash(i, 3) % 11;
            AD[i] = 10 + hash(i, 4) % 14;
        }
    }

    /** Bloklar kayıtlardan sonra, ilk kullanımda yüklenir. */
    private static final class P {
        static final BlockState TUGLA = CennetMod.BULUT_TUGLASI.get().defaultBlockState();
        static final BlockState SUTUN = CennetMod.KUTSAL_SUTUN.get().defaultBlockState();
        static final BlockState FENER = CennetMod.NUR_FENERI.get().defaultBlockState();
        static final BlockState BULUT = CennetMod.BULUT_BLOGU.get().defaultBlockState();
        static final BlockState ALTIN = Blocks.GOLD_BLOCK.defaultBlockState();
        static final BlockState CAM = Blocks.WHITE_STAINED_GLASS.defaultBlockState();
        static final BlockState SU = Blocks.WATER.defaultBlockState();
        static final BlockState CIM = Blocks.GRASS_BLOCK.defaultBlockState();
        static final BlockState TOPRAK = Blocks.DIRT.defaultBlockState();
        static final BlockState GOVDE = Blocks.CHERRY_LOG.defaultBlockState();
        static final BlockState YAPRAK = Blocks.CHERRY_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, Boolean.TRUE);
        static final BlockState[] CICEK = {
                Blocks.AZURE_BLUET.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(),
                Blocks.PINK_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState(),
                Blocks.LILY_OF_THE_VALLEY.defaultBlockState(), Blocks.ALLIUM.defaultBlockState(),
                Blocks.CORNFLOWER.defaultBlockState()};
    }

    public NurBaskentFeature() { super(NoneFeatureConfiguration.CODEC); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel lvl = ctx.level();
        BlockPos o = ctx.origin();
        int x0 = o.getX() & ~15, z0 = o.getZ() & ~15;
        BlockPos.MutableBlockPos mp = new BlockPos.MutableBlockPos();
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int x = x0 + lx, z = z0 + lz;
                long r2 = (long) x * x + (long) z * z;
                if (r2 <= (long) SEHIR_R * SEHIR_R) sehirKolonu(lvl, mp, x, z);
                else if (r2 > 360L * 360L) tepe(lvl, mp, x, z, Math.sqrt((double) r2));
                if (r2 >= 270L * 270L && r2 <= 470L * 470L) adalar(lvl, mp, x, z);
            }
        }
        return true;
    }

    private static void set(WorldGenLevel lvl, BlockPos.MutableBlockPos mp, int x, int y, int z, BlockState s) {
        lvl.setBlock(mp.set(x, y, z), s, 2);
    }

    // ------------------------------------------------------------------ dış tepeler

    private static void tepe(WorldGenLevel lvl, BlockPos.MutableBlockPos mp, int x, int z, double r) {
        double w = smooth(360, 540, r);
        if (w <= 0) return;
        double ridge = 1 - Math.abs(2 * vn(x / 60.0 + 5, z / 60.0 + 9) - 1);   // sırt çizgileri
        double baz = 0.6 * vn(x / 95.0 + 13, z / 95.0 + 7) + 0.4 * ridge;
        double d = 0.65 * vn(x / 28.0, z / 28.0) + 0.35 * vn(x / 11.0 + 50, z / 11.0 + 50);
        int h = (int) (w * (baz * baz * 74 + d * 6));
        for (int y = G; y <= ZEMIN + h; y++) set(lvl, mp, x, y, z, P.BULUT);
    }

    private static double smooth(double a, double b, double x) {
        double t = Math.max(0, Math.min(1, (x - a) / (b - a)));
        return t * t * (3 - 2 * t);
    }

    private static int hash(int a, int b) {
        int h = a * 374761393 + b * 668265263 + 0x5bd1e995;
        h = (h ^ (h >>> 13)) * 1274126177;
        h ^= h >>> 16;
        return h & 0x7fffffff;
    }

    private static double vn(double x, double z) {
        int xi = (int) Math.floor(x), zi = (int) Math.floor(z);
        double fx = x - xi, fz = z - zi;
        fx = fx * fx * (3 - 2 * fx);
        fz = fz * fz * (3 - 2 * fz);
        double a = (hash(xi, zi) & 0xffff) / 65535.0, b = (hash(xi + 1, zi) & 0xffff) / 65535.0;
        double c = (hash(xi, zi + 1) & 0xffff) / 65535.0, d = (hash(xi + 1, zi + 1) & 0xffff) / 65535.0;
        return (a * (1 - fx) + b * fx) * (1 - fz) + (c * (1 - fx) + d * fx) * fz;
    }

    // ------------------------------------------------------------------ şehir sütunu

    private static void sehirKolonu(WorldGenLevel lvl, BlockPos.MutableBlockPos mp, int x, int z) {
        BlockState zem = zemin(x, z);
        if (zem != null) set(lvl, mp, x, ZEMIN, z, zem);
        if (zem == P.CIM && hash(x * 31 + 7, z * 17 + 3) % 9 == 0)
            set(lvl, mp, x, G, z, P.CICEK[hash(x, z) % P.CICEK.length]);
        int hmax = hMax(x, z);
        for (int dy = 0; dy < hmax; dy++) {
            BlockState s = yapi(x, z, dy);
            if (s != null) set(lvl, mp, x, G + dy, z, s);
        }
    }

    // ------------------------------------------------------------------ zemin (üst katman)

    private static BlockState zemin(int dx, int dz) {
        int adx = Math.abs(dx), adz = Math.abs(dz);
        double r = Math.sqrt((double) dx * dx + (double) dz * dz);

        // dev heykellerin kaidesi
        int hn0 = heykelNo(dx, dz);
        if (hn0 >= 0 && heykelMesafe(hn0, dx, dz) <= 10.0 * HEYKEL[hn0][4] / 100.0) return P.TUGLA;
        // çeşmeler (köşegenlerde)
        int fx = adx - 40, fz = adz - 40, f2 = fx * fx + fz * fz;
        if (f2 <= 36) return f2 == 0 ? P.TUGLA : (f2 <= 25 ? P.SU : P.TUGLA);
        // merkez ışığı
        if (adx <= 1 && adz <= 1) return P.FENER;
        // ana caddeler (kanal ve yeşil halkanın üstünde köprü olur); güneyde kapıdan öteye yaklaşım yolu
        boolean cadde = (adx <= 4 && dz <= 345 && (dz >= 0 || -dz <= 290)) || (adz <= 4 && adx <= 290);
        if (cadde) return P.TUGLA;
        if (r <= 56) return (r >= 44 && r < 46) ? null : P.TUGLA;   // büyük meydan
        if (r < 64) return P.CIM;                                    // iç park
        if (r < 67) return P.SU;                                     // halka kanal
        if (r >= 70 && r < 78) return P.TUGLA;                       // halka yol
        if (r >= 78 && r < 88) return P.CIM;
        int gz = dz - KAPI;
        if (gz >= -12 && gz <= 14 && adx <= 32) return P.TUGLA;     // kapı avlusu
        if (r >= 88 && r <= 272) {
            Hc c = hucre(dx, dz);
            if (c.bina && c.ax <= c.half + 1 && c.az <= c.half + 1) return P.TUGLA;
            return P.CIM;
        }
        return null;
    }

    // ------------------------------------------------------------------ yapılar

    private static BlockState yapi(int dx, int dz, int dy) {
        int adx = Math.abs(dx), adz = Math.abs(dz);
        long r2 = (long) dx * dx + (long) dz * dz;

        // dev melek heykelleri (bölge içindeki her şeyin üstüne yazar)
        int hn = heykelNo(dx, dz);
        if (hn >= 0) return heykel(hn, dx, dz, dy);

        // çeşme direkleri (köşegenlerde)
        int fx = adx - 40, fz = adz - 40, f2 = fx * fx + fz * fz;
        if (f2 <= 36) {
            if (f2 == 0) return dy <= 3 ? P.SUTUN : (dy == 4 ? P.FENER : null);
            if (f2 > 25 && dy == 0) return P.TUGLA;
            return null;
        }

        // Nur Sarayı
        if (adx <= 40 && adz <= 44) return saray(dx, dz, dy);

        // güney ana kapı
        int gz = dz - KAPI;
        if (adx <= 26 && gz >= -8 && gz <= 8) return kapi(dx, gz, dy, adx);

        // gözcü kuleleri (16 adet, geçit ve kapı yönleri hariç)
        if (r2 > 270L * 270L && r2 < 306L * 306L && dy <= 34) {
            int k = (int) Math.round(Math.atan2(dz, dx) / (Math.PI / 8));
            double ca = k * Math.PI / 8;
            int wx = (int) Math.round(288 * Math.cos(ca)), wz = (int) Math.round(288 * Math.sin(ca));
            if (Math.abs(wx) >= 30 && Math.abs(wz) >= 30) {
                int tx = dx - wx, tz = dz - wz;
                if (tx * tx + tz * tz <= 30) return kule(tx, tz, dy, 30);
            }
        }

        // sur halkası (kapı hariç), dört yönde geçit
        if (r2 >= 286L * 286L && r2 < 290L * 290L && dy <= 11) {
            boolean gecit = (adx <= 5 && dz < 0) || (adz <= 5);
            if (gecit && dy <= 7) return null;
            if (dy == 11) return ((dx + dz) & 1) == 0 ? P.TUGLA : null;
            return dy == 10 ? P.ALTIN : P.TUGLA;
        }

        // cadde fenerleri
        if (r2 > 62L * 62L && r2 < 285L * 285L && !(r2 >= 63L * 63L && r2 <= 68L * 68L)) {
            boolean direk = (adz == 6 && dx % 16 == 0 && adx <= 285) || (adx == 6 && dz % 16 == 0 && adz <= 285);
            if (direk) {
                if (dy <= 3) return P.SUTUN;
                if (dy == 4) return P.FENER;
            }
        }

        // iç park ağaçları (56 < r < 64)
        if (r2 > 57L * 57L && r2 < 63L * 63L) {
            int cx = Math.floorDiv(dx, 8), cz = Math.floorDiv(dz, 8);
            if (hash(cx, cz) % 3 == 0) {
                int hh = hash(cx + 91, cz + 17);
                return agac(Math.floorMod(dx, 8), Math.floorMod(dz, 8), dy, 4, 4, 4 + hh % 3);
            }
            return null;
        }

        // semtler
        double r = Math.sqrt((double) r2);
        if (r >= 88 && r <= 272) return semt(dx, dz, dy);
        return null;
    }

    // ---- kuleler (çan kulesi ve kapı kuleleri)

    private static BlockState kule(int tx, int tz, int dy, int h) {
        double r2 = tx * tx + tz * tz;
        if (dy <= h) {
            if (r2 > 30.25) return null;
            if (r2 < 12.25 && dy != 0) return null;
            if (r2 >= 12.25 && (tx == 0 || tz == 0) && dy % 10 >= 5 && dy % 10 <= 7) return null;   // ok yarığı
            return dy % 12 == 11 ? P.ALTIN : P.TUGLA;
        }
        double rr = 6.2 - (dy - h - 1) * 0.33;
        if (rr > 0.5 && r2 <= rr * rr) return rr < 1.2 ? P.FENER : P.TUGLA;
        return null;
    }

    // ---- güney kapı

    private static BlockState kapi(int dx, int gz, int dy, int adx) {
        int tx = adx - 16;
        if (Math.abs(tx) <= 6 && Math.abs(gz) <= 6) return kule(tx, gz, dy, 38);
        if (adx < 10 && Math.abs(gz) <= 2) {
            if (adx <= 6 && dy < 10 + (int) Math.sqrt(36 - adx * adx)) return gz == 0 ? P.CAM : null;   // kapalı kemerli kapı (cam mühür)
            if (dy <= 30) return (dy == 16 && dx == 0) ? P.FENER : P.TUGLA;
            if (dy == 31 && dx % 2 == 0) return P.TUGLA;
            return null;
        }
        if (adx >= 22 && Math.abs(gz) <= 1) {
            if (dy <= 13) return P.TUGLA;
            if (dy == 14 && adx % 2 == 0) return P.TUGLA;
        }
        return null;
    }

    // ---- semtler: iç halkada gökdelenler, dışta bahçeli köy evleri, arada parklar

    private static final class Hc {
        boolean bina;
        int tip, half, H, ax, az, lx, lz, h;
        double ccx, ccz;
    }

    private static Hc hucre(int dx, int dz) {
        final int C = 28;
        int cxg = Math.floorDiv(dx, C), czg = Math.floorDiv(dz, C);
        Hc c = new Hc();
        c.lx = Math.floorMod(dx, C);
        c.lz = Math.floorMod(dz, C);
        c.ccx = cxg * C + 14;
        c.ccz = czg * C + 14;
        c.h = hash(cxg, czg);
        c.ax = Math.abs(c.lx - 14);
        c.az = Math.abs(c.lz - 14);
        double rc = Math.sqrt(c.ccx * c.ccx + c.ccz * c.ccz);
        boolean a = rc >= 96 && rc < 176, b = rc >= 176 && rc <= 268;
        int olasilik = a ? 70 : (b ? 55 : 0);
        if ((a || b) && (c.h % 100) < olasilik) {
            c.bina = true;
            if (a) {
                c.tip = 0;
                c.half = 4 + ((c.h >> 7) % 3);
                c.H = 14 + ((c.h >> 11) % 16) * 5;
            } else {
                c.tip = 1;
                c.half = 3 + ((c.h >> 7) % 2);
                c.H = 5 + ((c.h >> 11) % 3);
            }
        }
        return c;
    }

    private static BlockState semt(int dx, int dz, int dy) {
        Hc c = hucre(dx, dz);
        if (c.bina) {
            BlockState s = c.tip == 0 ? gokdelen(c, dy) : ev(c, dy);
            if (s != null) return s;
            // bahçe ağacı: hücrenin köşelerinden biri
            int k = c.h & 3;
            int tx = (k & 1) == 0 ? 4 : 23, tz = (k & 2) == 0 ? 4 : 23;
            return agac(c.lx, c.lz, dy, tx, tz, 4 + ((c.h >> 4) & 3));
        }
        // park: üç ağaç
        for (int k = 0; k < 3; k++) {
            int hh = hash(c.h, k + 77);
            BlockState s = agac(c.lx, c.lz, dy, 4 + (hh & 15), 4 + ((hh >> 5) & 15), 4 + ((hh >> 10) % 3));
            if (s != null) return s;
        }
        return null;
    }

    private static BlockState agac(int lx, int lz, int dy, int tx, int tz, int ht) {
        if (lx == tx && lz == tz && dy < ht) return P.GOVDE;
        int ddx = lx - tx, ddz = lz - tz, ddy = dy - ht;
        if (ddy >= -2 && ddy <= 2 && (ddx * ddx + ddz * ddz + ddy * ddy * 1.4) <= 6.2) return P.YAPRAK;
        return null;
    }

    private static boolean kapiYuzu(Hc c, int half) {
        int sx = c.lx - 14, sz = c.lz - 14;
        if (Math.abs(c.ccx) <= Math.abs(c.ccz)) return (c.ccx > 0 ? sx == -half : sx == half) && c.az <= 1;
        return (c.ccz > 0 ? sz == -half : sz == half) && c.ax <= 1;
    }

    private static BlockState gokdelen(Hc c, int dy) {
        int half = c.half;
        if (dy > c.H) {
            if (c.ax == 0 && c.az == 0 && dy <= c.H + 6) return dy == c.H + 6 ? P.FENER : P.SUTUN;
            if (c.H >= 36 && c.ax == half && c.az == half && dy <= c.H + 4) return dy == c.H + 4 ? P.FENER : P.SUTUN;
            return null;
        }
        if (c.ax > half || c.az > half) return null;
        boolean duvar = c.ax == half || c.az == half;
        if (dy == c.H) return (c.ax == half && c.az == half) ? P.FENER : P.TUGLA;
        if (!duvar) {
            if (dy > 0 && dy % 8 == 0) return P.TUGLA;                      // kat tavanı
            if (dy % 8 == 1 && c.ax == 0 && c.az == 0) return P.FENER;      // kat lambası
            return null;
        }
        if (dy <= 3 && kapiYuzu(c, half)) return null;
        if (c.ax == half && c.az == half) return P.SUTUN;
        if (dy % 14 == 13) return P.ALTIN;
        if (dy >= 4 && (dy % 4 == 1 || dy % 4 == 2)) return P.CAM;
        return P.TUGLA;
    }

    private static BlockState ev(Hc c, int dy) {
        int half = c.half, H = c.H;
        if (c.ax > half + 1 || c.az > half + 1) return null;
        if (dy < H) {
            if (c.ax > half || c.az > half) return null;
            if (c.ax != half && c.az != half) return null;
            if (dy <= 2 && kapiYuzu(c, half)) return null;
            boolean kose = c.ax == half && c.az == half;
            if (!kose && (dy == 2 || dy == 3) && (c.ax + c.az) % 3 == 0) return P.CAM;
            return kose ? P.SUTUN : P.TUGLA;
        }
        int k = dy - H;
        int reach = half + 1 - k;
        if (reach < 0) return null;
        if (c.ax <= reach && c.az <= reach) return (reach == 0 && k > 0) ? P.FENER : P.TUGLA;
        return null;
    }

    // ------------------------------------------------------------------ yükseklik sınırı

    private static int hMax(int dx, int dz) {
        if (Math.abs(dx) <= 40 && Math.abs(dz) <= 44) return MAXH_SARAY;
        if (heykelNo(dx, dz) >= 0) return MAXH_HEYKEL;
        long r2 = (long) dx * dx + (long) dz * dz;
        if (r2 >= 90L * 90L && r2 <= 180L * 180L) return MAXH_ORTA;
        return MAXH;
    }

    // ------------------------------------------------------------------ dev melek heykelleri

    private static int heykelNo(int dx, int dz) {
        for (int i = 0; i < HEYKEL.length; i++) {
            if (heykelMesafe(i, dx, dz) <= 24.0 * HEYKEL[i][4] / 100.0) return i;
        }
        return -1;
    }

    private static double heykelMesafe(int i, int dx, int dz) {
        double ox = dx - HEYKEL[i][0], oz = dz - HEYKEL[i][1];
        return Math.sqrt(ox * ox + oz * oz);
    }

    private static BlockState heykel(int no, int dx, int dz, int dy) {
        int[] h = HEYKEL[no];
        double sc = h[4] / 100.0;
        double ox = dx - h[0], oz = dz - h[1];
        double lx = (ox * (-h[3]) + oz * h[2]) / sc;   // sağ
        double lf = (ox * h[2] + oz * h[3]) / sc;      // ileri
        return heykelBlok(lx, -lf, dy / sc);           // yerel: ön = -z
    }

    private static double segMesafe(double px, double py, double pz, double ax, double ay, double az,
                                    double bx, double by, double bz) {
        double vx = bx - ax, vy = by - ay, vz = bz - az;
        double wx = px - ax, wy = py - ay, wz = pz - az;
        double t = (vx * wx + vy * wy + vz * wz) / (vx * vx + vy * vy + vz * vz);
        t = Math.max(0, Math.min(1, t));
        double cx = ax + vx * t - px, cy = ay + vy * t - py, cz = az + vz * t - pz;
        return Math.sqrt(cx * cx + cy * cy + cz * cz);
    }

    /** Işık kılıcı tutan kanatlı melek: kaide, cüppe, gövde, kollar, kılıç, baş, hale, kanatlar. */
    private static BlockState heykelBlok(double lx, double lz, double y) {
        double ax = Math.abs(lx);

        // kaide
        if (y < 6) {
            if (ax <= 8 && Math.abs(lz) <= 8) return (y >= 5 && (ax >= 7 || Math.abs(lz) >= 7)) ? P.ALTIN : P.TUGLA;
            return null;
        }

        // ışık kılıcı (önde, ucu aşağı)
        if (Math.abs(lz + 6.5) <= 0.6) {
            if (ax <= 0.9 && y >= 6 && y < 56) return ax <= 0.3 ? P.FENER : P.TUGLA;
            if (y >= 37 && y < 39 && ax <= 5) return P.ALTIN;
        }
        double dp = Math.sqrt(lx * lx + (lz + 6.5) * (lz + 6.5) + (y - 57) * (y - 57));
        if (dp <= 1.6) return P.ALTIN;

        // kollar: omuzdan elleri kılıcın kabzasına
        for (int s = -1; s <= 1; s += 2) {
            if (segMesafe(lx, y, lz, s * 7.5, 45, 0, s * 1.6, 37, -6.0) <= 1.7) return y < 39 ? P.ALTIN : P.TUGLA;
        }

        // baş ve boyun
        double hd = Math.sqrt(lx * lx + (lz + 0.3) * (lz + 0.3) + (y - 52.5) * (y - 52.5));
        if (hd <= 4.3) return y > 54 ? P.ALTIN : P.TUGLA;
        if (y >= 47 && y < 49 && ax <= 1.6 && Math.abs(lz) <= 1.6) return P.TUGLA;

        // hale
        if (y >= 59 && y < 60.4) {
            double hr = Math.sqrt(lx * lx + lz * lz);
            if (hr >= 5.0 && hr <= 6.4) return (((int) Math.floor(lx + 20) + (int) Math.floor(lz + 20)) % 4 == 0) ? P.FENER : P.ALTIN;
        }

        // kanatlar (arkada, yukarı ve dışa doğru açılır)
        if (lz >= 4 && lz <= 6.5) {
            double u = ax - 3;
            if (u >= 0 && u <= 21) {
                double yu = 46 + 1.2 * u - 0.03 * u * u;
                double yl = 34 + 0.7 * u;
                if (y >= yl && y <= yu) {
                    if (y > yu - 1) return P.ALTIN;
                    return ((int) (u + y * 0.3)) % 5 == 0 ? P.CAM : P.TUGLA;
                }
            }
        }

        // omuzlar
        if (y >= 45 && y < 47 && ax <= 8.5 && Math.abs(lz) <= 3.5) return ax > 6.5 ? P.ALTIN : P.TUGLA;

        // gövde
        if (y >= 32 && y < 47) {
            double hw = y < 36 ? 5 : 7.2;
            if (ax <= hw && Math.abs(lz) <= 4) return (lz < -2.5 && y >= 38 && y < 44 && ax <= 4.5) ? P.ALTIN : P.TUGLA;
        }

        // cüppe (aşağı doğru genişler)
        if (y < 32) {
            double wx = 6.0 - (y - 6) * (1.0 / 26);
            if (ax <= wx && Math.abs(lz) <= 4.5) {
                if (ax <= 0.8 && lz < -3.0) return P.ALTIN;
                return ((int) y) % 10 == 9 ? P.ALTIN : P.TUGLA;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ Nur Sarayı

    private static BlockState saray(int dx, int dz, int dy) {
        int adx = Math.abs(dx), adz = Math.abs(dz);
        int m = Math.max(adx, adz);
        double r = Math.sqrt((double) dx * dx + (double) dz * dz);

        // merkez kule, güneş küresi ve gökyüzüne uzanan ışık sütunu
        if (dy >= 66) {
            if (dy <= 156) {
                double rad = 5.2 - (dy - 66) * 0.04;
                if (r <= rad) return dy % 12 == 0 ? P.ALTIN : P.TUGLA;
            }
            double dd = Math.sqrt(r * r + (double) (dy - 162) * (dy - 162));
            if (dd <= 6.0) return dd > 5.0 ? P.ALTIN : P.FENER;
            if (dy >= 169 && dy <= 217 && r <= 1.5) return r < 0.5 ? P.FENER : P.CAM;
        }

        // kubbe (cam paneller, altın nervürler)
        if (dy >= DOME_CY) {
            double dd = Math.sqrt(r * r + (double) (dy - DOME_CY) * (dy - DOME_CY));
            if (dd >= 19.5 && dd <= DOME_R) {
                double a = Math.atan2(dz, dx) * 16 / Math.PI;
                double fr = a - Math.floor(a);
                boolean rib = fr < 0.16 || fr > 0.84;
                boolean ring = (dy - DOME_CY) % 7 == 6;
                return (rib || ring) ? P.ALTIN : P.CAM;
            }
        }

        // kubbe tamburu (pencereli)
        if (dy >= FL + 40 && dy < DOME_CY && r >= 19.5 && r <= 22.5) {
            double a = Math.atan2(dz, dx) * 16 / Math.PI;
            boolean pen = Math.floorMod((int) Math.floor(a), 2) == 0 && dy >= FL + 41 && dy < FL + 44;
            if (dy == DOME_CY - 1) return P.ALTIN;
            return pen ? P.CAM : P.TUGLA;
        }

        // çatı plakası (kubbe altı açık)
        if ((dy == FL + 40 || dy == FL + 41) && adx <= 22 && adz <= 28 && r > 22.5)
            return (adx >= 21 || adz >= 27) ? P.ALTIN : P.TUGLA;

        // ana salon duvarları ve iç mekân
        if (dy >= FL && dy < FL + 40) {
            boolean wx = adx >= 21 && adx <= 22 && adz <= 28;
            boolean wz = adz >= 27 && adz <= 28 && adx <= 22;
            if (wx || wz) {
                if (dz > 0 && adz >= 27 && adx <= 7 && dy < FL + 14 + (int) Math.sqrt(49 - adx * adx)) return null;       // ana kapı
                if (adx >= 21 && adz <= 5 && dy < FL + 12 + (int) Math.sqrt(25 - adz * adz)) return null;                 // yan kapılar
                if (adx >= 21 && adz >= 27) return P.SUTUN;
                int t = wz ? adx : adz;
                if ((t % 8 == 3 || t % 8 == 4) && dy >= FL + 10 && dy < FL + 34) return P.CAM;
                if (dy == FL + 39 || dy == FL) return P.ALTIN;
                return P.TUGLA;
            }
            if (adx <= 20 && adz <= 26 && adx == 12 && adz <= 20 && dz % 10 == 0)
                return dy < FL + 36 ? P.SUTUN : (dy == FL + 36 ? P.FENER : null);
        }

        // ön revak: sütunlar, arşitrav, alınlık
        if (dz >= 29 && dz <= 31 && adx <= 22) {
            if (dz == 30 && adx % 6 == 3 && adx >= 9 && dy < FL + 38) return P.SUTUN;
            if (dy == FL + 38 || dy == FL + 39) return dy == FL + 39 ? P.ALTIN : P.TUGLA;
            int ph = (int) (11 - adx * 0.5);
            if (dy >= FL + 40 && dy < FL + 40 + ph) return dy == FL + 40 + ph - 1 ? P.ALTIN : P.TUGLA;
        }

        // dört minare (±27, ±35)
        int tx = adx - 27, tz = adz - 35;
        double mr = Math.sqrt((double) tx * tx + (double) tz * tz);
        if (mr <= 6.6) {
            if (dy < FL) return P.TUGLA;
            if (dy <= 96) {
                if (dy == 60 && mr <= 6.2) return mr > 5.4 ? P.ALTIN : P.TUGLA;     // balkon
                double rad = 4.6 - dy * 0.012;
                if (mr <= rad) {
                    if (mr < rad - 1.4) return dy % 16 == 0 ? P.TUGLA : null;
                    if (dy % 16 >= 8 && dy % 16 <= 11 && (tx == 0 || tz == 0)) return P.CAM;
                    return dy % 20 == 19 ? P.ALTIN : P.TUGLA;
                }
            } else if (dy <= 118) {
                double rad = 4.0 - (dy - 96) * 0.18;
                if (rad > 0 && mr <= rad) return rad < 1.0 ? P.FENER : P.TUGLA;
            }
        }

        // platform, taht kürsüsü ve basamaklı teraslar
        if (m <= 29) {
            int h = FL;
            if (adx <= 20 && dz <= -14 && dz >= -26) h = FL + Math.min(3, (-dz - 14) / 2 + 1);
            if (dy < h) return ((adx <= 2 || m >= 28) && dy == h - 1) ? P.ALTIN : P.TUGLA;
            if (adx <= 1 && dz >= -22 && dz <= -20 && dy == h) return P.ALTIN;                 // taht oturağı
            if (adx <= 1 && dz == -23 && dy >= h && dy <= h + 6) return P.ALTIN;               // sırtlık
            if (adx == 2 && dz >= -22 && dz <= -20 && dy == h) return P.ALTIN;                 // kolçaklar
            if (dz == -25) {                                                                   // dev hale
                double dyy = dy - (FL + 14);
                double hr = Math.sqrt((double) dx * dx + dyy * dyy);
                if (hr >= 8 && hr <= 9.4) return P.ALTIN;
            }
            return null;
        }
        int ht = m <= 31 ? 3 : (m <= 33 ? 2 : (m <= 35 ? 1 : 0));
        return dy < ht ? P.TUGLA : null;
    }

    // ------------------------------------------------------------------ bulut adaları

    private static void adalar(WorldGenLevel lvl, BlockPos.MutableBlockPos mp, int x, int z) {
        for (int i = 0; i < N_ADA; i++) {
            int dx = x - ACX[i], dz = z - ACZ[i];
            int R = AR[i];
            if (dx > R || dx < -R || dz > R || dz < -R) continue;
            double d = Math.sqrt((double) dx * dx + (double) dz * dz);
            if (d > R) continue;
            double t = d / R;
            int top = G + ATY[i];
            int depth = (int) Math.round(AD[i] * (1 - t * t));
            for (int y = top - 3 - depth; y <= top; y++) {
                BlockState s = y == top ? P.CIM : (y >= top - 2 ? P.TOPRAK : P.BULUT);
                set(lvl, mp, x, y, z, s);
            }
            if (i % 4 == 0 && dx == R && dz == 0) {          // şelale kaynağı
                set(lvl, mp, x, top, z, P.SU);
                continue;
            }
            if (hash(x * 13 + i, z * 7) % 8 == 0) set(lvl, mp, x, top + 1, z, P.CICEK[hash(x, z + i) % P.CICEK.length]);
            for (int dyy = 0; dyy < 18; dyy++) {
                BlockState s = (i % 2 == 0) ? adaAgac(dx, dz, dyy) : adaMabet(dx, dz, dyy);
                if (s != null) set(lvl, mp, x, top + 1 + dyy, z, s);
            }
        }
    }

    private static BlockState adaAgac(int dx, int dz, int dyy) {
        BlockState s = agac(dx + 10, dz + 10, dyy, 12, 10, 6);
        if (s != null) return s;
        return agac(dx + 10, dz + 10, dyy, 6, 14, 5);
    }

    private static BlockState adaMabet(int dx, int dz, int dyy) {
        double r = Math.sqrt((double) dx * dx + (double) dz * dz);
        for (int k = 0; k < 8; k++) {
            if (dx == SX[k] && dz == SZ[k] && dyy < 9) return P.SUTUN;
        }
        if (dyy == 0 && r <= 5) return P.TUGLA;
        if (dyy == 1 && dx == 0 && dz == 0) return P.FENER;
        if (dyy == 9 && r >= 5.5 && r <= 8.5) return P.ALTIN;
        if (dyy == 10 && r <= 7.5) return P.TUGLA;
        if (dyy >= 11 && dyy <= 13) {
            double rr = 7.5 - (dyy - 10) * 2.2;
            if (r <= rr) return P.TUGLA;
        }
        if (dyy == 14 && r <= 1.5) return P.FENER;
        return null;
    }
}
