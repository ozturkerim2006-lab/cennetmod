package com.cennetmod;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Nur Kapısı Kimlik Kartı.
 * Envanterde taşındığı sürece Nur Krallığı'na geçişe izin verir (bkz. NurSistemi).
 * Krallığın girişinde (Z -4000 ile -4150 arası) elde tutulunca ya da sağ tıklanınca kapı sahnesi başlar.
 */
public class NurKarti extends Item {
    public NurKarti(Properties p) { super(p); }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide || !(player instanceof ServerPlayer sp)) return InteractionResultHolder.success(stack);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);

        if (!level.dimension().equals(NurSistemi.KRALLIK)) {
            sp.sendSystemMessage(Component.literal("Kartın mührü henüz ışımıyor... Kuzeyin çok daha uzaklarına git.")
                    .withStyle(ChatFormatting.GRAY));
            return InteractionResultHolder.fail(stack);
        }
        player.getCooldowns().addCooldown(this, 40);
        sahneBaslat(sp, true);
        return InteractionResultHolder.consume(stack);
    }

    /**
     * Kapı sahnesini başlatır. Yakında kapı varsa onu yeniden çalıştırır, yoksa yeni kapı kurar.
     * @return sahne başladıysa true
     */
    public static boolean sahneBaslat(ServerPlayer sp, boolean mesajVer) {
        ServerLevel sl = (ServerLevel) sp.level();
        List<AngelSoldier> yakin = sl.getEntitiesOfClass(AngelSoldier.class,
                sp.getBoundingBox().inflate(60, 80, 60), a -> a.getRol() == 0);
        if (!yakin.isEmpty()) {
            AngelSoldier lider = yakin.get(0);
            if (lider.mesgul()) {
                if (mesajVer) sp.sendSystemMessage(Component.literal("Muhafızlar şu an meşgul.").withStyle(ChatFormatting.GRAY));
                return false;
            }
            lider.baslat(sp, false);
            return true;
        }
        if (sp.getZ() > NurSistemi.SINIR_Z || sp.getZ() < NurSistemi.SINIR_Z - NurSistemi.KAPI_PENCERE) {
            if (mesajVer) sp.sendSystemMessage(Component.literal("Kartın mührü sönük. Kapı burada belirmiyor.")
                    .withStyle(ChatFormatting.GRAY));
            return false;
        }
        AngelSoldier.kapiKur(sl, sp);
        return true;
    }
}
