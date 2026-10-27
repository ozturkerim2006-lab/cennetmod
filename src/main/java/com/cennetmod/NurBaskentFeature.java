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
 * Düzen: ortada katedral + meydan, dört cadde, halka kanal/yol, içte gökdelen semti, dışta bahçeli köy evleri,
 * çevrede sur, güneyde (Z+) ana kapı. Şehrin dışı yumuşak bulut tepeleri.
 */
public class NurBaskentFeature extends Feature<NoneFeatureConfiguration> {
    public static final int ZEMIN = 100;
    private static final int G = ZEMIN + 1;      // zeminin üstündeki ilk hava bloğu
    private static final int KAPI = 288;         // güney kapının merkeze uzaklığı (Z+)
    private static final int MAXH = 64;
    private static final int SEHIR_R = 350;

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
            }
        }
        return true;
    }

    private static void set(WorldGenLevel lvl, BlockPos.MutableBlockPos mp, int x, int y, int z, BlockState s) {
        lvl.setBlock(mp.set(x, y, z), s, 2);
    }

    // ------------------------------------------------------------------ dış tepeler

    private static void tepe(WorldGenLevel lvl, BlockPos.MutableBlockPos mp, int x, int z, double r) {
        double w = smooth(360, 470, r);
        if (w <= 0) return;
        double d = 0.65 * vn(x / 28.0, z / 28.0) + 0.35 * vn(x / 11.0 + 50, z / 11.0 + 50);
        int h = (int) (d * w * 8);
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
        for (int dy = 0; dy < MAXH; dy++) {
            BlockState s = yapi(x, z, dy);
            if (s != null) set(lvl, mp, x, G + dy, z, s);
        }
    }

    // ------------------------------------------------------------------ zemin (üst katman)

    private static BlockState zemin(int dx, int dz) {
        int adx = Math.abs(dx), adz = Math.abs(dz);
        double r = Math.sqrt((double) dx * dx + (double) dz * dz);

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

        if (adx <= 18 && adz <= 18) return katedral(dx, dz, dy, adx, adz);

        // çan kuleleri (±26, ±26)
        int tx = adx - 26, tz = adz - 26;
        if (Math.abs(tx) <= 6 && Math.abs(tz) <= 6) return kule(tx, tz, dy, 36);

        // güney ana kapı
        int gz = dz - KAPI;
        if (adx <= 26 && gz >= -8 && gz <= 8) return kapi(dx, gz, dy, adx);

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

        // çeşme direkleri
        int fx = adx - 40, fz = adz - 40, f2 = fx * fx + fz * fz;
        if (f2 <= 36) {
            if (f2 == 0) return dy <= 3 ? P.SUTUN : (dy == 4 ? P.FENER : null);
            if (f2 > 25 && dy == 0) return P.TUGLA;
            return null;
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

    // ---- katedral

    private static BlockState katedral(int dx, int dz, int dy, int adx, int adz) {
        if (dy >= 24) {
            if (dx == 0 && dz == 0 && dy >= 36 && dy <= 52) return dy <= 50 ? P.SUTUN : P.FENER;
            double r = Math.sqrt((double) dx * dx + (double) dz * dz + (double) (dy - 24) * (dy - 24));
            if (r >= 12 && r <= 13.4) return dy == 24 ? P.ALTIN : P.TUGLA;
            return null;
        }
        int m = Math.max(adx, adz);
        if (m > 12) {   // basamaklar
            boolean yol = (adx <= 3 && adz > 12) || (adz <= 3 && adx > 12);
            int h = m <= 14 ? 3 : (m <= 16 ? 2 : 1);
            return (!yol && dy < h) ? P.TUGLA : null;
        }
        if (m == 12) {  // duvar
            boolean kose = adx == 12 && adz == 12;
            int diger = adx == 12 ? adz : adx;
            if (!kose && diger <= 2 && dy <= 8) return null;                                  // kapı
            if (!kose && diger <= 9 && diger % 4 == 0 && dy >= 10 && dy <= 16) return null;  // pencere
            if (dy == 23) return P.ALTIN;
            return (kose || dy == 0) ? P.SUTUN : P.TUGLA;
        }
        if (adx == 6 && adz == 6) return P.SUTUN;   // iç sütunlar
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
                c.H = 12 + ((c.h >> 11) % 12) * 4;
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
}
