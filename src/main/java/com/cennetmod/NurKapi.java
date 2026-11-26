package com.cennetmod;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

public class NurKapi {
   public static final Map<UUID, Kapi> KAPILAR = new ConcurrentHashMap();
   private static final ArrayDeque<Geri> KUYRUK = new ArrayDeque();

   public static void yaz(Kapi k, BlockPos p, BlockState s) {
      long key = p.asLong();
      if (!k.orijinal.containsKey(key)) {
         k.orijinal.put(key, k.sl.getBlockState(p));
      }

      k.sl.setBlock(p, s, 18);
   }

   public static void temizle(UUID sahip) {
      Kapi k = (Kapi)KAPILAR.remove(sahip);
      if (k != null) {
         for(UUID id : new UUID[]{k.lider, k.es}) {
            if (id != null) {
               Entity e = k.sl.getEntity(id);
               if (e != null) {
                  e.discard();
               }
            }
         }

         KUYRUK.add(new Geri(k));
      }
   }

   public static void isle() {
      int kalan = 2500;

      while(kalan > 0 && !KUYRUK.isEmpty()) {
         Geri g = (Geri)KUYRUK.peek();
         kalan -= g.adim(kalan);
         if (g.bitti()) {
            KUYRUK.poll();
         }
      }

   }

   public static final class Kapi {
      public final ServerLevel sl;
      public final int kx;
      public final int ky;
      public final int kz;
      public final UUID sahip;
      public UUID lider;
      public UUID es;
      final Long2ObjectOpenHashMap<BlockState> orijinal = new Long2ObjectOpenHashMap();

      public Kapi(ServerLevel sl, int kx, int ky, int kz, UUID sahip) {
         this.sl = sl;
         this.kx = kx;
         this.ky = ky;
         this.kz = kz;
         this.sahip = sahip;
      }
   }

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

         int n;
         for(n = 0; this.i < this.anahtarlar.length && n < max; ++n) {
            long key = this.anahtarlar[this.i++];
            m.set(BlockPos.getX(key), BlockPos.getY(key), BlockPos.getZ(key));
            this.k.sl.setBlock(m, (BlockState)this.k.orijinal.get(key), 18);
         }

         return n;
      }

      boolean bitti() {
         return this.i >= this.anahtarlar.length;
      }
   }
}
