package com.cennetmod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class GokselMelekRenderer extends HumanoidMobRenderer<GokselMelekBoss, GokselMelekModel> {
    private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/goksel_melek.png");

    public GokselMelekRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GokselMelekModel(ctx.bakeLayer(ModelLayers.PLAYER)), 0.75F);
    }

    @Override
    public ResourceLocation getTextureLocation(GokselMelekBoss e) {
        return TEX;
    }

    @Override
    protected void scale(GokselMelekBoss e, PoseStack pose, float partialTick) {
        pose.scale(1.25F, 1.25F, 1.25F);
        float bob = Mth.sin((e.tickCount + partialTick) * 0.12F) * 0.14F;
        pose.translate(0.0F, bob, 0.0F);
    }

    private static void quad(VertexConsumer vc, Matrix4f mat, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int r, int g, int b, int a) {
        vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(mat, x3, y3, z3).setColor(r, g, b, a);
        vc.addVertex(mat, x4, y4, z4).setColor(r, g, b, a);
        // Cift tarafli gorunum
        vc.addVertex(mat, x4, y4, z4).setColor(r, g, b, a);
        vc.addVertex(mat, x3, y3, z3).setColor(r, g, b, a);
        vc.addVertex(mat, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a);
    }

    @Override
    public void render(GokselMelekBoss e, float yaw, float partialTick, PoseStack pose, MultiBufferSource buf, int light) {
        super.render(e, yaw, partialTick, pose, buf, light);

        float time = e.tickCount + partialTick;
        boolean p2 = e.getPhase() == 2;
        int anim = e.getAnimState();
        int r = p2 ? 120 : 255;
        int g = p2 ? 235 : 225;
        int b = p2 ? 255 : 120;

        VertexConsumer vc = buf.getBuffer(RenderType.lightning());
        pose.pushPose();
        float bodyYaw = Mth.lerp(partialTick, e.yBodyRotO, e.yBodyRot);
        float bob = Mth.sin(time * 0.12F) * 0.16F;
        pose.translate(0.0F, 1.5F + bob, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));

        // 1. 6 Parcali Isiltili Goksel Kanatlar
        float flap = Mth.sin(time * (p2 ? 0.25F : 0.15F)) * 14.0F;
        float wingScale = (anim == GokselMelekBoss.ANIM_DOMAIN || anim == GokselMelekBoss.ANIM_PHASE2_BREAK) ? 1.45F : 1.0F;
        for (int pair = 0; pair < 3; pair++) {
            float yOff = 0.25F - pair * 0.28F;
            float baseDeg = 18.0F + pair * 16.0F;
            float len = (1.75F - pair * 0.25F) * wingScale;
            for (int side : new int[]{-1, 1}) {
                pose.pushPose();
                pose.translate(side * 0.22F, yOff, -0.22F);
                pose.mulPose(Axis.YP.rotationDegrees(side * (28.0F + flap * 0.6F)));
                pose.mulPose(Axis.ZP.rotationDegrees(side * (baseDeg + flap * 0.4F)));
                Matrix4f m = pose.last().pose();
                quad(vc, m,
                    0, 0.18F, 0,
                    side * len, 0.45F, -0.15F,
                    side * (len * 0.78F), -0.18F, -0.10F,
                    0, -0.12F, 0,
                    r, g, b, 170);
                pose.popPose();
            }
        }

        // 2. Donen Goksel Hale (Bas ustu)
        pose.pushPose();
        pose.translate(0.0F, 1.08F, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(time * (p2 ? 5.0F : 2.5F)));
        Matrix4f hm = pose.last().pose();
        int segs = 12;
        float rIn = 0.42F, rOut = 0.56F;
        for (int i = 0; i < segs; i++) {
            float a1 = (float)(i * Math.PI * 2 / segs);
            float a2 = (float)((i + 1) * Math.PI * 2 / segs);
            quad(vc, hm,
                Mth.cos(a1) * rIn, 0, Mth.sin(a1) * rIn,
                Mth.cos(a1) * rOut, 0, Mth.sin(a1) * rOut,
                Mth.cos(a2) * rOut, 0, Mth.sin(a2) * rOut,
                Mth.cos(a2) * rIn, 0, Mth.sin(a2) * rIn,
                p2 ? 190 : 255, p2 ? 110 : 230, 255, 200);
        }
        pose.popPose();

        // 3. Boyut Yarigi & Faz 2 Ayna Kirilma Isinlari
        if (anim == GokselMelekBoss.ANIM_INTRO || anim == GokselMelekBoss.ANIM_PHASE2_BREAK) {
            pose.pushPose();
            pose.translate(0.0F, 0.0F, -0.55F);
            Matrix4f rm = pose.last().pose();
            float w = anim == GokselMelekBoss.ANIM_INTRO ? 0.55F + Mth.sin(time * 0.2F) * 0.12F : 1.1F;
            quad(vc, rm,
                0, 2.5F, 0,
                w, 0.0F, 0,
                0, -2.0F, 0,
                -w, 0.0F, 0,
                60, 220, 255, 195);
            pose.popPose();
        }

        pose.popPose();
    }
}
