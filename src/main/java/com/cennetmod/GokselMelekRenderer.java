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
    private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/goksel_melek_boss.png");

    public GokselMelekRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GokselMelekModel(ctx.bakeLayer(ModelLayers.PLAYER)), 1.05F);
    }

    @Override
    public ResourceLocation getTextureLocation(GokselMelekBoss e) {
        return TEX;
    }

    @Override
    protected void scale(GokselMelekBoss e, PoseStack pose, float partialTick) {
        pose.scale(1.75F, 1.75F, 1.75F);
        float bob = Mth.sin((e.tickCount + partialTick) * 0.12F) * 0.16F;
        pose.translate(0.0F, bob, 0.0F);
    }

    private static void quad(VertexConsumer vc, Matrix4f mat, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int r, int g, int b, int a) {
        vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(mat, x3, y3, z3).setColor(r, g, b, a);
        vc.addVertex(mat, x4, y4, z4).setColor(r, g, b, a);
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
        int r = p2 ? 110 : 255;
        int g = p2 ? 225 : 215;
        int b = p2 ? 255 : 95;

        VertexConsumer vc = buf.getBuffer(RenderType.lightning());
        pose.pushPose();
        float bodyYaw = Mth.lerp(partialTick, e.yBodyRotO, e.yBodyRot);
        float bob = Mth.sin(time * 0.12F) * 0.22F;
        // 180 - bodyYaw sonrasi: +Z = ARKA (sirt), -Z = ON (yuz)
        pose.translate(0.0F, 2.15F + bob, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));

        // 1. 6 Katmanli 1.5x Buyuk Goksel Kanatlar (SIRTTA VE GERIYE DONUK)
        float flap = Mth.sin(time * (p2 ? 0.24F : 0.14F)) * 13.0F;
        float wScale = (anim == GokselMelekBoss.ANIM_DOMAIN || anim == GokselMelekBoss.ANIM_PHASE2_BREAK) ? 1.32F : 1.0F;
        for (int pair = 0; pair < 3; pair++) {
            float yOff = 0.42F - pair * 0.40F;
            float baseDeg = 18.0F + pair * 16.0F;
            float len = (2.90F - pair * 0.36F) * wScale;
            for (int side : new int[]{-1, 1}) {
                pose.pushPose();
                pose.translate(side * 0.28F, yOff, 0.34F); // +Z = sirt!
                pose.mulPose(Axis.YP.rotationDegrees(-side * (28.0F + flap * 0.65F))); // geriye dogru acilir!
                pose.mulPose(Axis.ZP.rotationDegrees(side * (baseDeg + flap * 0.38F)));
                Matrix4f m = pose.last().pose();
                quad(vc, m,
                    0, 0.30F, 0,
                    side * len, 0.78F, 0.22F,
                    side * (len * 0.82F), -0.34F, 0.16F,
                    0, -0.18F, 0,
                    r, g, b, 175);
                quad(vc, m,
                    0, 0.15F, 0.03F,
                    side * (len * 0.74F), 0.42F, 0.12F,
                    side * (len * 0.60F), -0.18F, 0.08F,
                    0, -0.09F, 0.03F,
                    255, 255, 255, 215);
                pose.popPose();
            }
        }

        // 2. Cift Halkali Donen Goksel Hale (Bas ustu)
        pose.pushPose();
        pose.translate(0.0F, 1.56F, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(time * (p2 ? 4.8F : 2.4F)));
        Matrix4f hm = pose.last().pose();
        int segs = 16;
        for (int i = 0; i < segs; i++) {
            float a1 = (float)(i * Math.PI * 2 / segs), a2 = (float)((i + 1) * Math.PI * 2 / segs);
            quad(vc, hm, Mth.cos(a1)*0.56F,0,Mth.sin(a1)*0.56F, Mth.cos(a1)*0.78F,0,Mth.sin(a1)*0.78F, Mth.cos(a2)*0.78F,0,Mth.sin(a2)*0.78F, Mth.cos(a2)*0.56F,0,Mth.sin(a2)*0.56F, p2?195:255, p2?105:225, 255, 215);
            if (i % 4 == 0) {
                quad(vc, hm, Mth.cos(a1)*0.78F,0,Mth.sin(a1)*0.78F, Mth.cos(a1)*1.15F,0.08F,Mth.sin(a1)*1.15F, Mth.cos(a2)*0.78F,0,Mth.sin(a2)*0.78F, Mth.cos(a1)*0.78F,-0.08F,Mth.sin(a1)*0.78F, 255, 255, 255, 220);
            }
        }
        pose.popPose();

        // 3. Sag Elde Parlayan 3D Isik Kilici (-Z = ONDE)
        pose.pushPose();
        pose.translate(-0.74F, -0.10F, -0.45F);
        pose.mulPose(Axis.XP.rotationDegrees(anim == GokselMelekBoss.ANIM_BLINK_DASH ? -85.0F : -48.0F));
        Matrix4f sm = pose.last().pose();
        quad(vc, sm, -0.11F, 0, 0, 0.11F, 0, 0, 0.06F, 2.80F, 0, -0.06F, 2.80F, 0, r, g, b, 210);
        quad(vc, sm, -0.04F, 0, -0.01F, 0.04F, 0, -0.01F, 0.015F, 2.95F, -0.01F, -0.015F, 2.95F, -0.01F, 255, 255, 255, 240);
        quad(vc, sm, -0.46F, 0.22F, 0, 0.46F, 0.22F, 0, 0.38F, 0.36F, 0, -0.38F, 0.36F, 0, 255, 220, 90, 220);
        pose.popPose();

        // 4. Buyu Yaparken Zeminde Donen 3D Yildiz Cemberi
        if (anim != GokselMelekBoss.ANIM_IDLE) {
            pose.pushPose();
            pose.translate(0.0F, -2.02F - bob, 0.0F);
            pose.mulPose(Axis.YP.rotationDegrees(-time * 3.0F));
            Matrix4f cm = pose.last().pose();
            float cr = 3.4F;
            for (int i = 0; i < 18; i++) {
                float a1 = (float)(i * Math.PI * 2 / 18), a2 = (float)((i + 1) * Math.PI * 2 / 18);
                quad(vc, cm, Mth.cos(a1)*cr,0,Mth.sin(a1)*cr, Mth.cos(a1)*(cr-0.24F),0,Mth.sin(a1)*(cr-0.24F), Mth.cos(a2)*(cr-0.24F),0,Mth.sin(a2)*(cr-0.24F), Mth.cos(a2)*cr,0,Mth.sin(a2)*cr, r, g, b, 185);
            }
            pose.popPose();
        }

        // 5. Boyut Yarigi & Faz 2 Ayna Kirilmasi (+Z = ARKADA)
        if (anim == GokselMelekBoss.ANIM_INTRO || anim == GokselMelekBoss.ANIM_PHASE2_BREAK) {
            pose.pushPose();
            pose.translate(0.0F, 0.0F, 0.90F);
            Matrix4f rm = pose.last().pose();
            float w = anim == GokselMelekBoss.ANIM_INTRO ? 0.85F + Mth.sin(time * 0.2F) * 0.16F : 1.65F;
            quad(vc, rm, 0, 3.6F, 0, w, 0, 0, 0, -2.8F, 0, -w, 0, 0, 70, 225, 255, 200);
            pose.popPose();
        }

        pose.popPose();
    }
}
