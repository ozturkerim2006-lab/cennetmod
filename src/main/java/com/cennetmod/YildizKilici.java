package com.cennetmod;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

public class YildizKilici extends SwordItem {
    public YildizKilici() {
        super(Tiers.DIAMOND, new Item.Properties()
                .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 4, -2.2F))
                .durability(1800));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 160, 1));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0));
            player.getCooldowns().addCooldown(this, 300);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
