package com.cennetmod;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NurKarti extends Item {
   public NurKarti(Item.Properties p) {
      super(p);
   }

   public boolean isFoil(ItemStack stack) {
      return true;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!level.isClientSide && player instanceof ServerPlayer sp) {
         if (!level.dimension().equals(YarikAsasi.CENNET)) {
            return InteractionResultHolder.pass(stack);
         } else if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
         } else {
            ServerLevel sl = (ServerLevel)level;
            NurKapi.Kapi k = (NurKapi.Kapi)NurKapi.KAPILAR.get(sp.getUUID());
            if (k == null) {
               if (sp.getZ() > (double)-4000.0F) {
                  sp.sendSystemMessage(Component.literal("Kartın mührü henüz ışımıyor... Kuzeyin çok daha uzaklarına git.").withStyle(ChatFormatting.GRAY));
                  return InteractionResultHolder.fail(stack);
               } else {
                  player.getCooldowns().addCooldown(this, 40);
                  AngelSoldier.kapiKur(sl, sp);
                  return InteractionResultHolder.consume(stack);
               }
            } else {
               boolean onunde = k.sl == sl && Math.abs(sp.getX() - ((double)k.kx + (double)0.5F)) <= (double)16.0F && sp.getZ() > (double)(k.kz + 4) && sp.getZ() < (double)(k.kz + 46) && Math.abs(sp.getY() - (double)k.ky) <= (double)8.0F;
               if (!onunde) {
                  return InteractionResultHolder.fail(stack);
               } else {
                  if (k.lider != null) {
                     Entity var10 = sl.getEntity(k.lider);
                     if (var10 instanceof AngelSoldier) {
                        AngelSoldier lider = (AngelSoldier)var10;
                        if (lider.mesgul()) {
                           return InteractionResultHolder.fail(stack);
                        }

                        player.getCooldowns().addCooldown(this, 40);
                        lider.baslat(sp, false);
                        return InteractionResultHolder.consume(stack);
                     }
                  }

                  NurKapi.temizle(sp.getUUID());
                  return InteractionResultHolder.fail(stack);
               }
            }
         }
      } else {
         return InteractionResultHolder.success(stack);
      }
   }
}
