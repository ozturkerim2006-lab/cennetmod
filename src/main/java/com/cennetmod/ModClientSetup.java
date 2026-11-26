package com.cennetmod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "cennetmod",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ModClientSetup {
   @SubscribeEvent
   public static void efektKaydet(RegisterDimensionSpecialEffectsEvent e) {
      e.register(ResourceLocation.fromNamespaceAndPath("cennetmod", "cennet"), new CennetEffects(false));
      e.register(ResourceLocation.fromNamespaceAndPath("cennetmod", "nur"), new CennetEffects(true));
   }

   @SubscribeEvent
   public static void katmanKaydet(EntityRenderersEvent.RegisterLayerDefinitions e) {
      e.registerLayerDefinition(AngelModel.LAYER, AngelModel::createBodyLayer);
   }

   @SubscribeEvent
   public static void ciziciKaydet(EntityRenderersEvent.RegisterRenderers e) {
      e.registerEntityRenderer((EntityType)CennetMod.MELEK.get(), AngelRenderer::new);
   }
}
