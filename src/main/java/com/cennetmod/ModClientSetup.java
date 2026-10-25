package com.cennetmod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** İstemci tarafı kayıtlar: gökyüzü efekti, melek modeli ve çizicisi. */
@Mod.EventBusSubscriber(modid = CennetMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientSetup {
    @SubscribeEvent
    public static void efektKaydet(RegisterDimensionSpecialEffectsEvent e) {
        e.register(ResourceLocation.fromNamespaceAndPath(CennetMod.MODID, "cennet"), new CennetEffects());
    }

    @SubscribeEvent
    public static void katmanKaydet(EntityRenderersEvent.RegisterLayerDefinitions e) {
        e.registerLayerDefinition(AngelModel.LAYER, AngelModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void ciziciKaydet(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(CennetMod.MELEK.get(), AngelRenderer::new);
    }
}
