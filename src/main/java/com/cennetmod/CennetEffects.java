package com.cennetmod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.DimensionSpecialEffects.SkyType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class CennetEffects extends DimensionSpecialEffects {
   private static final ResourceLocation[] YUZ = new ResourceLocation[6];
   private static final ResourceLocation[] BULUT = new ResourceLocation[6];
   private final boolean bulut;

   public CennetEffects(boolean bulut) {
      super(Float.NaN, true, SkyType.NORMAL, false, false);
      this.bulut = bulut;
   }

   public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
      return fogColor.multiply((double)(brightness * 0.94F + 0.06F), (double)(brightness * 0.94F + 0.06F), (double)(brightness * 0.91F + 0.09F));
   }

   public boolean isFoggyAt(int x, int y) {
      return false;
   }

   public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
      return this.gokyuzuCiz(poseStack, ticks, partialTick, setupFog);
   }

   public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
      PoseStack ps = new PoseStack();
      ps.mulPose(modelViewMatrix);
      return this.gokyuzuCiz(ps, ticks, partialTick, setupFog);
   }

   public boolean renderSky(ClientLevel level, int ticks, float partialTick, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
      PoseStack ps = new PoseStack();
      ps.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
      ps.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
      return this.gokyuzuCiz(ps, ticks, partialTick, setupFog);
   }

   private boolean gokyuzuCiz(PoseStack ps, int ticks, float partialTick, Runnable setupFog) {
      setupFog.run();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      ps.pushPose();
      if (!this.bulut) {
         ps.mulPose(Axis.XP.rotationDegrees(25.0F));
         ps.mulPose(Axis.YP.rotationDegrees(((float)ticks + partialTick) * 0.012F));
      }

      ciz(ps, this.bulut ? BULUT : YUZ);
      ps.popPose();
      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
      return true;
   }

   private static void ciz(PoseStack ps, ResourceLocation[] dokular) {
      Tesselator tess = Tesselator.getInstance();

      for(int i = 0; i < 6; ++i) {
         ps.pushPose();
         switch (i) {
            case 1 -> ps.mulPose(Axis.XP.rotationDegrees(90.0F));
            case 2 -> ps.mulPose(Axis.XP.rotationDegrees(-90.0F));
            case 3 -> ps.mulPose(Axis.XP.rotationDegrees(180.0F));
            case 4 -> ps.mulPose(Axis.ZP.rotationDegrees(90.0F));
            case 5 -> ps.mulPose(Axis.ZP.rotationDegrees(-90.0F));
         }

         Matrix4f m = ps.last().pose();
         RenderSystem.setShaderTexture(0, dokular[i]);
         BufferBuilder bb = tess.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         bb.addVertex(m, -100.0F, -100.0F, -100.0F).setUv(0.0F, 0.0F).setColor(255, 255, 255, 255);
         bb.addVertex(m, -100.0F, -100.0F, 100.0F).setUv(0.0F, 1.0F).setColor(255, 255, 255, 255);
         bb.addVertex(m, 100.0F, -100.0F, 100.0F).setUv(1.0F, 1.0F).setColor(255, 255, 255, 255);
         bb.addVertex(m, 100.0F, -100.0F, -100.0F).setUv(1.0F, 0.0F).setColor(255, 255, 255, 255);
         BufferUploader.drawWithShader(bb.buildOrThrow());
         ps.popPose();
      }

   }

   static {
      for(int i = 0; i < 6; ++i) {
         YUZ[i] = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/environment/cennet_sky_" + i + ".png");
         BULUT[i] = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/environment/cennet_bulut_" + i + ".png");
      }

   }
}
