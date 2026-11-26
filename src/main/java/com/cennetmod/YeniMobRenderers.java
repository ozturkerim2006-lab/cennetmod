package com.cennetmod;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "cennetmod", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class YeniMobRenderers {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CennetMod.CAMUR_YARATIGI.get(), ctx ->
            new HumanoidMobRenderer<CamurYaratigi, HumanoidModel<CamurYaratigi>>(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5f) {
                @Override public ResourceLocation getTextureLocation(CamurYaratigi e) {
                    return ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/camur_yaratigi.png");
                }
            });
        event.registerEntityRenderer(CennetMod.AGAC_GOZU.get(), ctx ->
            new HumanoidMobRenderer<AgacGozu, HumanoidModel<AgacGozu>>(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5f) {
                @Override public ResourceLocation getTextureLocation(AgacGozu e) {
                    return ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/agac_gozu.png");
                }
            });
        event.registerEntityRenderer(CennetMod.SIYAH_FIGUR.get(), ctx ->
            new HumanoidMobRenderer<SiyahFigur, HumanoidModel<SiyahFigur>>(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.5f) {
                @Override public ResourceLocation getTextureLocation(SiyahFigur e) {
                    return ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/siyah_figur.png");
                }
            });
        event.registerEntityRenderer(CennetMod.GOKSEL_MELEK.get(), GokselMelekRenderer::new);
    }
}
