package com.cennetmod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Nur Başkenti geçiş sistemi.
 *
 * Başkent (nur_krallik) ayrı bir boyuttur. Oraya yalnızca şu yolla girilir:
 *   1) Cennet Karanlığı'nda Z <= -4000'de Nur Kapısı Kimlik Kartına sağ tıkla -> kapı sahnesi,
 *   2) kartı envanterinde taşıyarak açılan kapıdan geç.
 * Geçtiğin anda başkente alınırsın; kapı kapanır, sur ve muhafızlar kaybolur (başkalarına görünmez).
 * Başkentteyken kart envanterden çıkarsa Cennet Karanlığı'na geri çağrılırsın.
 */
@Mod.EventBusSubscriber(modid = CennetMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NurSistemi {
    public static final ResourceKey<Level> KRALLIK = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(CennetMod.MODID, "nur_krallik"));

    /** Kapı sahnesi için gereken sınır (Cennet Karanlığı'nda bu Z'nin kuzeyi). */
    public static final double SINIR_Z = -4000;

    /** Başkentte varış noktası: güney kapının hemen içi, kuzeye (şehre) bakar. */
    private static final double BASKENT_X = 0.5;
    private static final double BASKENT_Y = NurBaskentFeature.ZEMIN + 1;
    private static final double BASKENT_Z = 274.5;

    public static boolean cennetBoyutu(Level l) {
        ResourceKey<Level> d = l.dimension();
        return d.equals(YarikAsasi.CENNET) || d.equals(KRALLIK);
    }

    public static boolean kartVar(ServerPlayer sp) {
        var env = sp.getInventory();
        for (int i = 0; i < env.getContainerSize(); i++) {
            ItemStack s = env.getItem(i);
            if (!s.isEmpty() && s.is(CennetMod.NUR_KARTI.get())) return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void tick(LivingEvent.LivingTickEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp)) return;
        NurKapi.isle();
        if (sp.tickCount % 5 != 0 || sp.isSpectator()) return;

        // Kapıyı kuran oyuncu uzaklaştıysa ya da başka boyuta geçtiyse kapı kalkar
        NurKapi.Kapi kendi = NurKapi.KAPILAR.get(sp.getUUID());
        if (kendi != null && (sp.level() != kendi.sl || sp.distanceToSqr(kendi.kx, kendi.ky, kendi.kz) > 150.0 * 150.0)) {
            NurKapi.temizle(sp.getUUID());
        }

        ResourceKey<Level> d = sp.level().dimension();
        if (d.equals(YarikAsasi.CENNET)) {
            kapidanGecisKontrol(sp);
        } else if (d.equals(KRALLIK)) {
            if (!kartVar(sp)) geriCagir(sp);
        }
    }

    @SubscribeEvent
    public static void cikis(PlayerEvent.PlayerLoggedOutEvent e) {
        NurKapi.temizle(e.getEntity().getUUID());
    }

    /** Kartı taşıyan oyuncu açık kapının içine girdiyse başkente alınır. */
    private static void kapidanGecisKontrol(ServerPlayer sp) {
        if (NurKapi.KAPILAR.isEmpty() || sp.getZ() > SINIR_Z + 10 || !kartVar(sp)) return;
        for (NurKapi.Kapi k : NurKapi.KAPILAR.values()) {
            if (k.sl != sp.level()) continue;
            double rx = sp.getX() - (k.kx + 0.5), rz = sp.getZ() - k.kz;
            if (Math.abs(rx) <= 6.5 && rz >= -6 && rz <= 2.5 && sp.getY() >= k.ky - 1 && sp.getY() <= k.ky + 27) {
                baskenteGec(sp, k);
                return;
            }
        }
    }

    private static void baskenteGec(ServerPlayer sp, NurKapi.Kapi k) {
        ServerLevel kaynak = (ServerLevel) sp.level();
        sp.getPersistentData().putIntArray("NurGeri", new int[]{k.kx, k.ky, k.kz});
        kaynak.playSound(null, BlockPos.containing(k.kx + 0.5, k.ky + 3, k.kz + 2),
                SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, 3f, 0.8f);
        gec(sp, KRALLIK, BASKENT_X, BASKENT_Y, BASKENT_Z, 180f, false);
        ServerLevel hedef = (ServerLevel) sp.level();
        hedef.playSound(null, sp.blockPosition(), SoundEvents.BELL_RESONATE, SoundSource.NEUTRAL, 3f, 0.7f);
        NurKapi.temizle(k.sahip);   // kapı kapanır, sur ve muhafızlar kaybolur
    }

    /** Başkentte kart kalmadıysa Cennet Karanlığı'na dön. */
    private static void geriCagir(ServerPlayer sp) {
        int[] g = sp.getPersistentData().getIntArray("NurGeri");
        if (g.length == 3) gec(sp, YarikAsasi.CENNET, g[0] + 0.5, g[1], g[2] + 10.5, sp.getYRot(), false);
        else gec(sp, YarikAsasi.CENNET, 0.5, 200, SINIR_Z + 10, sp.getYRot(), true);
        sp.displayClientMessage(Component.literal("Perde seni geri çağırdı."), true);
    }

    private static void gec(ServerPlayer sp, ResourceKey<Level> hedefKey, double x, double y, double z, float yaw, boolean yuzey) {
        ServerLevel hedef = sp.server.getLevel(hedefKey);
        if (hedef == null) return;
        if (sp.isPassenger()) sp.stopRiding();

        BlockPos p = BlockPos.containing(x, y, z);
        hedef.getChunk(p.getX() >> 4, p.getZ() >> 4);   // bölgeyi yükle/üret
        if (yuzey || !hedef.getBlockState(p).getCollisionShape(hedef, p).isEmpty()
                || !hedef.getBlockState(p.above()).getCollisionShape(hedef, p.above()).isEmpty()) {
            p = hedef.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, p).above();
            y = p.getY();
        }
        sp.fallDistance = 0;
        sp.teleportTo(hedef, x, y, z, yaw, 0);
    }
}
