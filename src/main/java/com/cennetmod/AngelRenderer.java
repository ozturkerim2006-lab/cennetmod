package com.cennetmod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AngelRenderer extends MobRenderer<AngelSoldier, AngelModel> {
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/melek_asker.png");
   private static final ResourceLocation TEX_GOKSEL = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/goksel_melek.png");
   private static final ResourceLocation TEX_ORMAN_ELF = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/orman_elfi.png");
   private static final ResourceLocation TEX_YUKSEK_ELF = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/yuksek_elf.png");
   private static final ResourceLocation TEX_BATI_MUHAFIZ = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/bati_muhafiz.png");
   private static final ResourceLocation TEX_DOGU_SAVASCI = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/dogu_savasci.png");
   private static final ResourceLocation TEX_PEGASUS = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/pegasus_arabasi.png");
   private static final ResourceLocation TEX_CAMUR = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/camur_yaratigi.png");
   private static final ResourceLocation TEX_AGAC_GOZU = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/agac_gozu.png");
   private static final ResourceLocation TEX_SIYAH_FIGUR = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/siyah_figur.png");
   private static final ResourceLocation TEX_BATAKLIK_KOYLU = ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/entity/bataklik_koylusu.png");

   public AngelRenderer(EntityRendererProvider.Context var1) {
      super(var1, new AngelModel(var1.bakeLayer(AngelModel.LAYER)), 2.2F);
   }

   public ResourceLocation getTextureLocation(AngelSoldier var1) {
      int var2 = var1.getPoz();
      if (var2 != 63 && var2 != 65) {
         if (var2 == 62) {
            return TEX_SIYAH_FIGUR;
         } else if (var2 == 61) {
            return TEX_AGAC_GOZU;
         } else if (var2 == 60) {
            return TEX_CAMUR;
         } else if (var2 >= 50) {
            return TEX_PEGASUS;
         } else if (var2 >= 40) {
            return TEX_DOGU_SAVASCI;
         } else if (var2 >= 30) {
            return TEX_BATI_MUHAFIZ;
         } else if (var2 != 20 && var2 != 22) {
            if (var2 != 21 && var2 != 23) {
               return var2 != 10 && var2 != 11 ? TEX : TEX_GOKSEL;
            } else {
               return TEX_YUKSEK_ELF;
            }
         } else {
            return TEX_ORMAN_ELF;
         }
      } else {
         return TEX_BATAKLIK_KOYLU;
      }
   }

   protected void scale(AngelSoldier var1, PoseStack var2, float var3) {
      int var4 = var1.getPoz();
      if (var4 != 63 && var4 != 65) {
         if (var4 == 62) {
            var2.scale(0.62F, 0.84F, 0.62F);
         } else if (var4 == 61) {
            var2.scale(1.0F, 1.0F, 1.0F);
         } else if (var4 == 60) {
            var2.scale(0.62F, 0.62F, 0.62F);
         } else if (var4 >= 50) {
            var2.scale(1.48F, 1.48F, 1.48F);
         } else if (var4 >= 40) {
            var2.scale(0.64F, 0.64F, 0.64F);
         } else if (var4 >= 30) {
            var2.scale(0.58F, 0.58F, 0.58F);
         } else if (var4 >= 20) {
            var2.scale(0.54F, 0.54F, 0.54F);
         } else if (var4 >= 10) {
            var2.scale(0.74F, 0.74F, 0.74F);
         } else {
            var2.scale(1.5F, 1.5F, 1.5F);
         }
      } else {
         var2.scale(0.54F, 0.54F, 0.54F);
      }

   }
}
