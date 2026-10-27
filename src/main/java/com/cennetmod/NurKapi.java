package com.cennetmod;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Orta Cennet'te kurulan geçici kapı + sur kaydı (oyuncu başına bir tane).
 * Sur yazılırken her bloğun ESKİ hali kaydedilir; oyuncu kapıdan geçince (ya da uzaklaşınca/çıkınca)
 * sur ve meleklerle birlikte ortadan kalkar ve dünya eski haline döner. Böylece diğer oyuncular sur görmez.
 */
public class NurKapi {
    public static final class Kapi {
        public final ServerLevel sl;
        public final int kx, ky, kz;
        public final UUID sahip;
        public UUID lider, es;
        final Long2ObjectOpenHashMap<BlockState> orijinal = new Long2ObjectOpenHashMap<>();

        public Kapi(ServerLevel sl, int kx, int ky, int kz, UUID sahip) {
            this.sl = sl;
            this.kx = kx;
            this.ky = ky;
            this.kz = kz;
            this.sahip = sahip;
        }
    }

    /** sahip oyuncu -> kapısı */
    public static final Map<UUID, Kapi> KAPILAR = new ConcurrentHashMap<>();

    private static final class Geri {
        final Kapi k;
        final long[] anahtarlar;
        int i = 0;
        Geri(Kapi k) {
            this.k = k;
            this.anahtarlar = k.orijinal.keySet().toLongArray();
        }
        int adim(int max) {
            BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
            int n = 0;
            while (i < anahtarlar.length && n < max) {
                long key = anahtarlar[i++];
                m.set(BlockPos.getX(key), BlockPos.getY(key), BlockPos.getZ(key));
                k.sl.setBlock(m, k.orijinal.get(key), 18);
                n++;
            }
            return n;
        }
        boolean bitti() { return i >= anahtarlar.length; }
    }

    private static final ArrayDeque<Geri> KUYRUK = new ArrayDeque<>();

    /** Bloğu yazar ve eski halini saklar. */
    public static void yaz(Kapi k, BlockPos p, BlockState s) {
        long key = p.asLong();
        if (!k.orijinal.containsKey(key)) k.orijinal.put(key, k.sl.getBlockState(p));
        k.sl.setBlock(p, s, 18);
    }

    /** Kapıyı, surunu ve melekleri kaldırır; dünyayı eski haline getirmek üzere sıraya alır. */
    public static void temizle(UUID sahip) {
        Kapi k = KAPILAR.remove(sahip);
        if (k == null) return;
        for (UUID id : new UUID[]{k.lider, k.es}) {
            if (id == null) continue;
            Entity e = k.sl.getEntity(id);
            if (e != null) e.discard();
        }
        KUYRUK.add(new Geri(k));
    }

    /** Her tick çağrılır: geri yüklemeyi küçük parçalara bölerek yapar (takılma olmasın). */
    public static void isle() {
        int kalan = 2500;
        while (kalan > 0 && !KUYRUK.isEmpty()) {
            Geri g = KUYRUK.peek();
            kalan -= g.adim(kalan);
            if (g.bitti()) KUYRUK.poll();
        }
    }
}
