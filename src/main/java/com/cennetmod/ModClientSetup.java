package com.cennetmod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Cennet Karanlığı'na özel gökyüzü efektini kaydeder. */
@Mod.EventBusSubscriber(modid = CennetMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientSetup {
    @SubscribeEvent
    public static void efektKaydet(RegisterDimensionSpecialEffectsEvent e) {
        e.register(ResourceLocation.fromNamespaceAndPath(CennetMod.MODID, "cennet"), new CennetEffects());
    }
}
