package com.cennetmod;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "cennetmod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class YeniIcerikKayit {
    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent e) {
        e.put(CennetMod.CAMUR_YARATIGI.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.ATTACK_DAMAGE, 6.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.24D).add(Attributes.FOLLOW_RANGE, 32.0D).build());
        e.put(CennetMod.AGAC_GOZU.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.ATTACK_DAMAGE, 5.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.28D).add(Attributes.FOLLOW_RANGE, 32.0D).build());
        e.put(CennetMod.SIYAH_FIGUR.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 60.0D).add(Attributes.ATTACK_DAMAGE, 9.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.30D).add(Attributes.FOLLOW_RANGE, 40.0D).build());
        e.put(CennetMod.GOKSEL_MELEK.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 400.0D).add(Attributes.ATTACK_DAMAGE, 14.0D)
            .add(Attributes.ARMOR, 8.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.85D)
            .add(Attributes.MOVEMENT_SPEED, 0.34D).add(Attributes.FOLLOW_RANGE, 64.0D).build());
    }

    @SubscribeEvent
    public static void onTabs(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey() == CreativeModeTabs.COMBAT) {
            e.accept(CennetMod.YILDIZ_KILICI.get());
        }
        if (e.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            e.accept(CennetMod.CAMUR_YARATIGI_YUMURTA.get());
            e.accept(CennetMod.AGAC_GOZU_YUMURTA.get());
            e.accept(CennetMod.SIYAH_FIGUR_YUMURTA.get());
            e.accept(CennetMod.GOKSEL_MELEK_YUMURTA.get());
        }
    }
}
