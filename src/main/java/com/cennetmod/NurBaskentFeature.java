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
 * NUR BAŞKENTİ — "İklîm-i Cennet" (Efsanevi tasarım)
 *
 * Efsaneye göre şehir, bulut denizinin üstünde yedi katlı bir nurlu âlemdir; merkezinde Arş-ı Nur
 * (Nur Tahtı) yükselir, etrafında sekiz Cennet Kapısı vardır (bkz. eş-Şura 7: "Cennet kapıları").
 *
 * Düzen (merkez 0,0; yer: ZEMIN):
 *   1. Arş-ı Nur Meydanı (r<=56): sekiz köşeli yıldız mozaiği, altın hatlar.
 *   2. Sidre Parkı (56-64): kutsal ağaçlar; dört yönde Melek Heykelleri (kanatlı nur abideleri).
 *   3. Kevser Halka Kanalı (64-68) + köprüler.
 *   4. Hatime Yolu (70-78): halka cadde, fener dizili.
 *   5. Rayhan Bahçeleri (78-88): çiçekli bahçe kuşağı.
 *   6. İç Semt (88-176): minareli gökdelenler (köşe kuleleri, piramit çatılar, nur mızrakları).
 *      Dış Semt (176-268): bahçeli köy evleri; arada parklar.
 *   7. Sur (286-290): sekiz kapılı sur halkası, kuleli; güneyde (Z+) büyük Nur Kapısı (cam mühürlü).
 *
 * Dışarıda yumuşak bulut tepeleri ve nadiren "sabit yıldız" yüzen bulut adacıkları.
 * Her parça (chunk) kendi sütunlarını çizer; tümü koordinatın saf fonksiyonudur — chunk
 * sınırlarında kusursuz birleşir.
 */
public class NurBaskentFeature extends Feature<NoneFeatureConfiguration> {
    public static final int ZEMIN = 100;
    private static final int G = ZEMIN + 1;      // zeminin üstündeki ilk hava bloğu
    private static final int KAPI = 288;         // güney büyük kapının merkeze uzaklığı (Z+)
    private static final int MAXH = 64;
    private static final int SEHIR_R = 350;

    /** Kapı merkezleri (8 yön): sur halkasındaki sekiz Cennet Kapısı. */
    private static final int[][] KAPILAR = {
            {288, 0}, {204, 204}, {0, 288}, {-204, 204},
            {-288, 0}, {-204, -204}, {0, -288}, {204, -204}};

    /** Bloklar kayıtlardan sonra, ilk kullanımda yüklenir. */
    private static final class P {
        static final BlockState TUGLA = CennetMod.BULUT_TUGLASI.get().defaultBlockState();
        static final BlockState SUTUN = CennetMod.KUTSAL_SUTUN.get().defaultBlockState();
        static final BlockState FENER = CennetMod.NUR_FENERI.get().defaultBlockState();
        static final BlockState BULUT = CennetMod.BULUT_BLOGU.get().defaultBlockState();
        static final BlockState ALTIN = Blocks.GOLD_BLOCK.defaultBlockState();
        static final BlockState MASUMIYET = Blocks.DIAMOND_BLOCK.defaultBlockState();   // "yıldız zemini"
        static final BlockState CAM = Blocks.WHITE_STAINED_GLASS.defaultBlockState();
        static final BlockState CAM_MAVI = Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState();
        static final BlockState CUBUK = Blocks.END_ROD.defaultBlockState();             // nur mızrağı
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
                else if (r2 > 360L * 360L) {
                    tepe(lvl, mp, x, z, Math.sqrt((double) r2));
                    yildizAdasi(lvl, mp, x, z);
                }
            }
        }
        return true;
    }

    private static void set(WorldGenLevel lvl, BlockPos.MutableBlockPos mp, int x, int y, int z, BlockState s) {
        lvl.setBlock(mp.set(x, y, z), s, 2);
    }

    // ------------------------------------------------------------------ dış tepeler ve yüzen adacıklar

    private static void tepe(WorldGenLevel lvl, BlockPos.MutableBlockPos mp, int x, int z, double r) {
        double w = smooth(360, 470, r);
        if (w <= 0) return;
        double d = 0.65 * vn(x / 28.0, z / 28.0) + 0.35 * vn(x / 11.0 + 50, z / 11.0 + 50);
        int h = (int) (d * w * 8);
        for (int y = G; y <= ZEMIN + h; y++) set(lvl, mp, x, y, z, P.BULUT);
    }

    /** Nadiren beliren "sabit yıldız": gökyüzünde asılı duran fenerli bulut adacığı. */
    private static void yildizAdasi(WorldGenLevel lvl, BlockPos.MutableBlockPos mp, int x, int z) {
        int cx = Math.floorDiv(x, 96), cz = Math.floorDiv(z, 96);
        int h = hash(cx, cz);
        if (h % 37 != 0) return;
        int ox = cx * 96 + 48 + (h % 17) - 8;
        int oz = cz * 96 + 48 + ((h >> 5) % 17) - 8;
        int dx = x - ox, dz = z - oz;
        int d2 = dx * dx + dz * dz;
        if (d2 > 20) return;
        int y0 = ZEMIN + 16 + (h >> 9) % 8;
        set(lvl, mp, x, y0, z, P.BULUT);
        if (d2 == 0) set(lvl, mp, x, y0 + 1, z, P.FENER);
        else if (d2 <= 4) set(lvl, mp, x, y0 + 1, z, P.BULUT);
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

        // ana caddeler (kanal üstünde köprü olur); sekiz kapı avlusu
        boolean cadde = (adx <= 4 && adz <= 345 && (dz >= 0 || -dz <= 290)) || (adz <= 4 && adx <= 290);
        if (cadde) {
            for (int[] k : KAPILAR)
                if (Math.abs(dx - k[0]) <= 24 && Math.abs(dz - k[1]) <= 24) return P.TUGLA;
            return P.TUGLA;
        }
        // Kevser halka kanalı + korkuluklar
        if (r >= 64 && r < 68) return P.SU;
        if ((r >= 63 && r < 64) || (r >= 68 && r < 69)) return P.TUGLA;
        // Arş-ı Nur Meydanı: sekiz köşeli yıldız mozaiği
        if (r <= 56) {
            if (r <= 30 && (adx == adz || adx <= 1 || adz <= 1)) return P.ALTIN;   // yıldız hatları
            return P.TUGLA;
        }
        if (r < 64) return P.CIM;                                    // Sidre Parkı
        if (r >= 70 && r < 78) return P.TUGLA;                       // Hatime Yolu
        if (r >= 78 && r < 88) return P.CIM;                         // Rayhan Bahçeleri
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

        // dört minare (±26, ±26)
        int tx = adx - 26, tz = adz - 26;
        if (Math.abs(tx) <= 6 && Math.abs(tz) <= 6) return kule(tx, tz, dy, 36);

        // dört melek heykeli: (±60, 0) ve (0, ±60)
        if (adx >= 58 && adx <= 62 && adz <= 8) return melek(dx - 60 * Integer.signum(dx), dz, dy);
        if (adz >= 58 && adz <= 62 && adx <= 8) return melek(dx, dz - 60 * Integer.signum(dz), dy);

        // güney büyük kapı (Nur Kapısı)
        int gz = dz - KAPI;
        if (adx <= 26 && gz >= -8 && gz <= 8) return kapi(dx, gz, dy, adx);

        // sekiz kapı kulesi
        for (int[] k : KAPILAR) {
            int gx = dx - k[0], gzc = dz - k[1];
            if (Math.abs(gx) <= 6 && Math.abs(gzc) <= 6) return kule(gx, gzc, dy, 20);
        }

        // sur halkası (kapı kulelerinin altından geçer, geçitler açık)
        if (r2 >= 286L * 286L && r2 < 290L * 290L && dy <= 11) {
            if (gecit(dx, dz) && dy <= 8) return null;
            if (dy == 11) return ((dx + dz) & 1) == 0 ? P.TUGLA : null;
            return dy == 10 ? P.ALTIN : P.TUGLA;
        }

        // cadde fenerleri (iki sıra)
        if (r2 > 62L * 62L && r2 < 285L * 285L && !(r2 >= 63L * 63L && r2 <= 68L * 68L)) {
            boolean direk = (adz == 6 && dx % 16 == 0 && adx <= 285) || (adx == 6 && dz % 16 == 0 && adz <= 285);
            if (direk) {
                if (dy <= 3) return P.SUTUN;
                if (dy == 4) return P.FENER;
            }
        }
        // Hatime Yolu fenerleri (halka)
        if (r2 >= 70L * 70 && r2 < 78L * 78 && (adx + adz) % 16 == 0 && dy <= 3) return P.SUTUN;
        if (r2 >= 70L * 70 && r2 < 78L * 78 && (adx + adz) % 16 == 0 && dy == 4) return P.FENER;

        // Kevser çeşmeleri (köşegenlerde, (±40,±40))
        int fx = adx - 40, fz = adz - 40, f2 = fx * fx + fz * fz;
        if (f2 <= 36) return cesme(dx, dz, fx, fz, f2, dy);

        // Sidre Parkı ağaçları (56 < r < 64)
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

    /** Surdaki bir nokta sekiz kapıdan birinin geçidinde mi? */
    private static boolean gecit(int dx, int dz) {
        for (int[] k : KAPILAR)
            if (Math.abs(dx - k[0]) <= 5 && Math.abs(dz - k[1]) <= 5) return true;
        return false;
    }

    // ---- Arş-ı Nur (merkez katedral): kubbe, altın kaburgalar, hilal ve nur alemi

    private static BlockState katedral(int dx, int dz, int dy, int adx, int adz) {
        if (dy >= 24) {
            // nur alemi: orta mızrak + hilal tacı
            if (dx == 0 && dz == 0 && dy >= 36) return P.SUTUN;
            if (dz == 0 && dy >= 54) {                                  // hilal (dikey altın halka, içi oyuk)
                int ddy = dy - 58;
                boolean dis = dx * dx + ddy * ddy <= 20;
                boolean ic = (dx + 2) * (dx + 2) + ddy * ddy <= 10;
                if (dis && !ic) return P.ALTIN;
            }
            if (dx == 0 && dz == 0 && dy >= 62) return P.FENER;
            // kubbe: küre kabuğu + 8 altın kaburga + altın kuşak
            double r = Math.sqrt((double) dx * dx + (double) dz * dz + (double) (dy - 24) * (dy - 24));
            if (r >= 12 && r <= 13.4) {
                double ang = Math.atan2(dz, dx);
                if (Math.abs(Math.sin(4 * ang)) < 0.18) return P.ALTIN; // kaburga
                return dy == 24 ? P.ALTIN : P.TUGLA;
            }
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
            if (!kose && diger <= 9 && diger % 4 == 0 && dy >= 10 && dy <= 16) return null;  // kemerli pencere
            if (dy == 23) return P.ALTIN;
            if (dy == 0 && ((adx + adz) % 4 == 0)) return P.MASUMIYET;                        // yıldız zemin taşları
            return (kose || dy == 0) ? P.SUTUN : P.TUGLA;
        }
        if (adx == 6 && adz == 6) return dy <= 16 ? P.SUTUN : null;   // iç sütunlar (altın başlıklı)
        if ((adx == 6 && adz < 6) || (adz == 6 && adx < 6)) return dy == 16 ? P.ALTIN : null;
        // iç avlu: merkezde nur havuzu
        if (dx * dx + dz * dz <= 4) return dy == 0 ? P.FENER : null;
        return null;
    }

    // ---- melek heykeli: kaide, sütun gövde, cam kanatlar, altın hale

    private static BlockState melek(int ux, int uz, int dy) {
        int ax = Math.abs(ux), az = Math.abs(uz);
        if (ax <= 2 && az <= 2 && dy <= 1) return (dy == 1 && (ax == 2 || az == 2)) ? P.ALTIN : P.TUGLA;
        if (ux == 0 && uz == 0) {
            if (dy >= 2 && dy <= 7) return P.SUTUN;
            if (dy == 8) return P.CAM;                              // nur yüzü
            return null;
        }
        int d2 = ux * ux + uz * uz;
        if (dy == 9 && d2 >= 2 && d2 <= 5) return P.ALTIN;          // hale (başın çevresinde altın halka)
        // kanatlar: gövdeye dik uzanan tüylü cam kanatlar, ucu altın
        if (ax <= 1 && uz != 0) {
            int reach = 8 - dy;
            if (dy >= 4 && dy <= 7 && az <= reach) return az == reach ? P.ALTIN : P.CAM_MAVI;
            if (dy == 3 && az <= 4) return P.CAM_MAVI;              // alt tüyler
        }
        return null;
    }

    // ---- Kevser çeşmesi: kaideli havuz, fenerli orta direk, dört su fıskıyesi

    private static BlockState cesme(int dx, int dz, int fx, int fz, int f2, int dy) {
        if (f2 == 0) {                                              // orta direk
            if (dy <= 2) return P.SUTUN;
            return dy == 3 ? P.FENER : null;
        }
        if (f2 <= 9) {                                              // havuz
            if (dy == 0) return P.SU;
            if (dy == 1 && ((fx == 0 && Math.abs(fz) == 2) || (fz == 0 && Math.abs(fx) == 2))) return P.CUBUK;
            return null;
        }
        if (f2 <= 25) return dy == 0 ? P.TUGLA : null;              // kenar taşı
        return null;
    }

    // ---- kuleler (minareler, kapı kuleleri)

    private static BlockState kule(int tx, int tz, int dy, int h) {
        double r2 = tx * tx + tz * tz;
        if (dy <= h) {
            if (r2 > 30.25) return null;
            if (r2 < 12.25 && dy != 0) return null;
            if (dy == 6 || dy == 18) return r2 >= 20.25 ? P.ALTIN : null;   // balkon kuşağı
            if (r2 >= 12.25 && (tx == 0 || tz == 0) && dy % 10 >= 5 && dy % 10 <= 7) return null;   // ok yarığı
            return dy % 12 == 11 ? P.ALTIN : P.TUGLA;
        }
        double rr = 6.2 - (dy - h - 1) * 0.33;                      // sivri kemerli başlık
        if (rr > 0.5 && r2 <= rr * rr) return rr < 1.2 ? P.FENER : P.TUGLA;
        return null;
    }

    // ---- güney büyük kapı (Nur Kapısı): ikiz kule, cam mühürlü kemer, nur feneri

    private static BlockState kapi(int dx, int gz, int dy, int adx) {
        int tx = adx - 16;
        if (Math.abs(tx) <= 6 && Math.abs(gz) <= 6) return kule(tx, gz, dy, 38);
        if (adx < 10 && Math.abs(gz) <= 2) {
            if (adx <= 6 && dy < 10 + (int) Math.sqrt(36 - adx * adx)) return gz == 0 ? P.CAM : null;   // cam mühür
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

    // ---- semtler: iç halkada minareli gökdelenler, dışta bahçeli köy evleri, arada parklar

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
            // köşe minareleri (yüksek binalarda)
            if (c.H >= 36 && c.ax == half && c.az == half && dy <= c.H + 4) return dy == c.H + 4 ? P.FENER : P.SUTUN;
            // piramit çatı
            int k = dy - c.H;
            int reach = half - k;
            if (k <= half && c.ax <= reach && c.az <= reach) return P.TUGLA;
            // nur mızrağı
            if (c.ax == 0 && c.az == 0 && dy <= c.H + 7) return dy == c.H + 7 ? P.FENER : P.CUBUK;
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
            if (dy == 3 && kapiYuzu(c, half)) return P.ALTIN;               // altın lenton (kapı başlığı)
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
