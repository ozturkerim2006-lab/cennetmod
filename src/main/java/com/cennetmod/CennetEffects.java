package com.cennetmod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/**
 * Gökyüzü: bulut=false -> Cennet Karanlığı (mor-pembe yıldızlı bulutsu, hafif eğik),
 *          bulut=true  -> Nur Başkenti (pastel bulut denizi ve sıcak güneş, ufuk düz).
 *
 * renderSky için Forge sürümlerinde farklı imzalar var; hangisi çağrılırsa çağrılsın derlensin diye
 * üç imza da @Override OLMADAN yazıldı.
 */
public class CennetEffects extends DimensionSpecialEffects {
    private static final ResourceLocation[] YUZ = new ResourceLocation[6];
    private static final ResourceLocation[] BULUT = new ResourceLocation[6];
    static {
        for (int i = 0; i < 6; i++) {
            YUZ[i] = ResourceLocation.fromNamespaceAndPath(CennetMod.MODID, "textures/environment/cennet_sky_" + i + ".png");
            BULUT[i] = ResourceLocation.fromNamespaceAndPath(CennetMod.MODID, "textures/environment/cennet_bulut_" + i + ".png");
        }
    }

    private final boolean bulut;

    public CennetEffects(boolean bulut) {
        super(Float.NaN, true, SkyType.NORMAL, false, false);
        this.bulut = bulut;
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return fogColor.multiply(brightness * 0.94F + 0.06F, brightness * 0.94F + 0.06F, brightness * 0.91F + 0.09F);
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }

    // Imza A: eski sürümler (PoseStack veren)
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack,
                             Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        return gokyuzuCiz(poseStack, ticks, partialTick, setupFog);
    }

    // Imza B: 1.21 (modelview Matrix4f veren)
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix,
                             Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        PoseStack ps = new PoseStack();
        ps.mulPose(modelViewMatrix);
        return gokyuzuCiz(ps, ticks, partialTick, setupFog);
    }

    // Imza C: matris vermeyen sürüm; kamera dönüşünden kendimiz kurarız
    public boolean renderSky(ClientLevel level, int ticks, float partialTick,
                             Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        PoseStack ps = new PoseStack();
        ps.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        ps.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180f));
        return gokyuzuCiz(ps, ticks, partialTick, setupFog);
    }

    private boolean gokyuzuCiz(PoseStack ps, int ticks, float partialTick, Runnable setupFog) {
        setupFog.run();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        ps.pushPose();
        if (!bulut) {
            ps.mulPose(Axis.XP.rotationDegrees(25f));
            ps.mulPose(Axis.YP.rotationDegrees((ticks + partialTick) * 0.012f)); // çok yavaş süzülme
        }
        ciz(ps, bulut ? BULUT : YUZ);
        ps.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        return true;
    }

    private static void ciz(PoseStack ps, ResourceLocation[] dokular) {
        Tesselator tess = Tesselator.getInstance();
        for (int i = 0; i < 6; i++) {
            ps.pushPose();
            switch (i) {
                case 1 -> ps.mulPose(Axis.XP.rotationDegrees(90f));
                case 2 -> ps.mulPose(Axis.XP.rotationDegrees(-90f));
                case 3 -> ps.mulPose(Axis.XP.rotationDegrees(180f));
                case 4 -> ps.mulPose(Axis.ZP.rotationDegrees(90f));
                case 5 -> ps.mulPose(Axis.ZP.rotationDegrees(-90f));
                default -> { }
            }
            Matrix4f m = ps.last().pose();
            RenderSystem.setShaderTexture(0, dokular[i]);
            BufferBuilder bb = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bb.addVertex(m, -100f, -100f, -100f).setUv(0f, 0f).setColor(255, 255, 255, 255);
            bb.addVertex(m, -100f, -100f, 100f).setUv(0f, 1f).setColor(255, 255, 255, 255);
            bb.addVertex(m, 100f, -100f, 100f).setUv(1f, 1f).setColor(255, 255, 255, 255);
            bb.addVertex(m, 100f, -100f, -100f).setUv(1f, 0f).setColor(255, 255, 255, 255);
            BufferUploader.drawWithShader(bb.buildOrThrow());
            ps.popPose();
        }
    }
}
