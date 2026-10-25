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

/** Cennet Kimlik Kartı: Cennet Karanlığı'nda z &lt; -4000 bölgesinde sağ tıkla, kapı sahnesini başlatır. */
public class CennetKarti extends Item {
    public CennetKarti(Properties p) { super(p); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide || !(player instanceof ServerPlayer sp)) return InteractionResultHolder.success(stack);
        ServerLevel sl = (ServerLevel) level;

        if (!level.dimension().equals(YarikAsasi.CENNET)) {
            sp.sendSystemMessage(Component.literal("Kartın mührü sönük. Bu kart yalnızca Cennet Karanlığı'nda ışır.").withStyle(ChatFormatting.GRAY));
            return InteractionResultHolder.fail(stack);
        }
        if (player.getZ() > -4000) {
            sp.sendSystemMessage(Component.literal("Kartın mührü henüz ışımıyor... Kuzeyin çok daha uzaklarına git.").withStyle(ChatFormatting.GRAY));
            return InteractionResultHolder.fail(stack);
        }
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        player.getCooldowns().addCooldown(this, 40);

        List<AngelSoldier> yakin = sl.getEntitiesOfClass(AngelSoldier.class,
                player.getBoundingBox().inflate(60, 80, 60), a -> a.getRol() == 0);
        if (!yakin.isEmpty()) {
            AngelSoldier lider = yakin.get(0);
            if (lider.mesgul()) {
                sp.sendSystemMessage(Component.literal("Muhafızlar şu an meşgul.").withStyle(ChatFormatting.GRAY));
            } else {
                lider.baslat(sp, false);
            }
        } else {
            AngelSoldier.kapiKur(sl, sp);
        }
        return InteractionResultHolder.consume(stack);
    }
}
