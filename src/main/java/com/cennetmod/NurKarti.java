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

/**
 * Nur Kapısı Kimlik Kartı.
 *
 * Kart yalnızca Cennet Karanlığı'nda, Z = -4000'in kuzeyinde sağ tıklanınca kapı sahnesini başlatır:
 * sur yükselir, muhafızlar kartı inceler, karşılar ve kapı açılır. Kapıdan geçen oyuncu Nur Başkenti'ne alınır.
 * Başka hiçbir yerde (ya da kapının önü dışında) kart hiçbir şey yapmaz, replik de çalmaz.
 * Başkente geçiş için kartın envanterde taşınması gerekir (bkz. NurSistemi).
 */
public class NurKarti extends Item {
    public NurKarti(Properties p) { super(p); }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide || !(player instanceof ServerPlayer sp)) return InteractionResultHolder.success(stack);

        // Cennet Karanlığı dışında kart sessizdir
        if (!level.dimension().equals(YarikAsasi.CENNET)) return InteractionResultHolder.pass(stack);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);

        ServerLevel sl = (ServerLevel) level;
        NurKapi.Kapi k = NurKapi.KAPILAR.get(sp.getUUID());

        if (k == null) {
            if (sp.getZ() > NurSistemi.SINIR_Z) {
                sp.sendSystemMessage(Component.literal("Kartın mührü henüz ışımıyor... Kuzeyin çok daha uzaklarına git.")
                        .withStyle(ChatFormatting.GRAY));
                return InteractionResultHolder.fail(stack);
            }
            player.getCooldowns().addCooldown(this, 40);
            AngelSoldier.kapiKur(sl, sp);
            return InteractionResultHolder.consume(stack);
        }

        // Kapı zaten kurulu: yalnızca kapının önündeyken yeniden çalışır
        boolean onunde = k.sl == sl
                && Math.abs(sp.getX() - (k.kx + 0.5)) <= 16
                && sp.getZ() > k.kz + 4 && sp.getZ() < k.kz + 46
                && Math.abs(sp.getY() - k.ky) <= 8;
        if (!onunde) return InteractionResultHolder.fail(stack);

        if (!(k.lider != null && sl.getEntity(k.lider) instanceof AngelSoldier lider)) {
            NurKapi.temizle(sp.getUUID());   // kayıt bayatlamış
            return InteractionResultHolder.fail(stack);
        }
        if (lider.mesgul()) return InteractionResultHolder.fail(stack);
        player.getCooldowns().addCooldown(this, 40);
        lider.baslat(sp, false);
        return InteractionResultHolder.consume(stack);
    }
}
