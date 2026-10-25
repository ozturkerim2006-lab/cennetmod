package com.cennetmod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Nur Krallığı perde sistemi.
 *
 * Orta Cennet'te (cennet_karanligi) kuzeye (-Z) yürüyen oyuncu, envanterinde Nur Kapısı Kimlik Kartı
 * taşıyorsa Z = -4000 çizgisinde sessizce Nur Krallığı boyutuna aktarılır (aynı koordinat).
 * Kartı olmayan oyuncu için hiçbir şey olmaz: orta dünya kuzeye doğru normal devam eder.
 *
 * Krallıkta kart envanterden çıkarsa oyuncu orta Cennet'e geri çağrılır.
 * Kartla güneye (Z > GERI_Z) dönen oyuncu da orta Cennet'e geçer.
 * Kontrol konuma bakar; ender incisi, elytra, /tp gibi her yol aynı kurala bağlıdır.
 */
@Mod.EventBusSubscriber(modid = CennetMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NurSistemi {
    public static final ResourceKey<Level> KRALLIK = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(CennetMod.MODID, "nur_krallik"));

    /** Bu çizgiden kuzeye geçen (Z küçük/eşit) kartlı oyuncu krallığa alınır. */
    public static final double SINIR_Z = -4000;
    /** Krallıkta bunun güneyine inen oyuncu orta Cennet'e döner (titreşimi önlemek için SINIR_Z'den 20 blok içeride). */
    public static final double GERI_Z = -3980;
    /** Kart yüzünden geri çağrılan oyuncunun orta Cennet'te çıkacağı Z. */
    private static final double GERI_CIKIS_Z = -3990;

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
        if (sp.tickCount % 5 != 0 || sp.isSpectator()) return;

        ResourceKey<Level> d = sp.level().dimension();
        if (d.equals(YarikAsasi.CENNET)) {
            if (sp.getZ() <= SINIR_Z && kartVar(sp)) {
                gec(sp, KRALLIK, sp.getX(), sp.getY(), Math.min(sp.getZ(), SINIR_Z), null);
            }
        } else if (d.equals(KRALLIK)) {
            if (!kartVar(sp)) {
                gec(sp, YarikAsasi.CENNET, sp.getX(), sp.getY(), GERI_CIKIS_Z, "Perde seni geri çağırdı.");
            } else if (sp.getZ() > GERI_Z) {
                gec(sp, YarikAsasi.CENNET, sp.getX(), sp.getY(), sp.getZ(), null);
            }
        }
    }

    private static void gec(ServerPlayer sp, ResourceKey<Level> hedefKey, double x, double y, double z, String mesaj) {
        ServerLevel hedef = sp.server.getLevel(hedefKey);
        if (hedef == null) return;
        if (sp.isPassenger()) sp.stopRiding();

        BlockPos p = BlockPos.containing(x, y, z);
        hedef.getChunk(p.getX() >> 4, p.getZ() >> 4); // bölgeyi yükle/üret
        // Aynı noktada blok varsa yüzeye çık
        if (!hedef.getBlockState(p).getCollisionShape(hedef, p).isEmpty()
                || !hedef.getBlockState(p.above()).getCollisionShape(hedef, p.above()).isEmpty()) {
            p = hedef.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, p).above();
        }
        sp.fallDistance = 0;
        sp.teleportTo(hedef, x, p.getY(), z, sp.getYRot(), sp.getXRot());
        if (mesaj != null) sp.displayClientMessage(Component.literal(mesaj), true);
    }
}
