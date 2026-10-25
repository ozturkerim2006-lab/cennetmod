package com.cennetmod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AngelRenderer extends MobRenderer<AngelSoldier, AngelModel> {
    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath(CennetMod.MODID, "textures/entity/melek_asker.png");

    public AngelRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new AngelModel(ctx.bakeLayer(AngelModel.LAYER)), 2.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(AngelSoldier e) { return TEX; }

    @Override
    protected void scale(AngelSoldier e, PoseStack ps, float partialTick) {
        ps.scale(1.5f, 1.5f, 1.5f);
    }
}
