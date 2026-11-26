package com.cennetmod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "cennetmod",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class ClientEvents {
   private static final ResourceLocation EFEKT_DUNYA = ResourceLocation.fromNamespaceAndPath("cennetmod", "shaders/post/dunya.json");
   private static final ResourceLocation EFEKT_CENNET = ResourceLocation.fromNamespaceAndPath("cennetmod", "shaders/post/cennet.json");
   private static final ResourceLocation EFEKT_NUR = ResourceLocation.fromNamespaceAndPath("cennetmod", "shaders/post/nur.json");
   private static final ResourceLocation[][] ENVANTER_DOKU_ESLEMELERI = new ResourceLocation[][]{{ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/gui/ozel_envanter.png"), ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/inventory.png")}, {ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/gui/ozel_yaratici_envanter.png"), ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/creative_inventory/tab_inventory.png")}, {ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/gui/ozel_yaratici_esyalar.png"), ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/creative_inventory/tab_items.png")}, {ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/gui/ozel_yaratici_arama.png"), ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/creative_inventory/tab_item_search.png")}, {ResourceLocation.fromNamespaceAndPath("cennetmod", "textures/gui/ozel_sandik.png"), ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png")}};
   private static ResourceLocation yukluEfekt = null;
   private static int hataSayaci = 0;
   private static float yumusak = 0.0F;
   private static float yumusakGece = 0.0F;
   private static float yumusakSabah = 0.0F;
   private static float yumusakYarik = 0.0F;
   private static float varisParlamasi = 0.0F;
   private static ResourceKey<Level> sonBoyut = null;
   private static int sonDokuBaglamaTick = -100;
   private static ByteBuffer hotbarPikselBuffer = null;
   private static ByteBuffer secimPikselBuffer = null;

   private static ByteBuffer olusturHotbarBuffer() {
      if (hotbarPikselBuffer != null) {
         return hotbarPikselBuffer;
      } else {
         short var0 = 182;
         byte var1 = 22;
         ByteBuffer var2 = ByteBuffer.allocateDirect(var0 * var1 * 4).order(ByteOrder.nativeOrder());

         for(int var3 = 0; var3 < var1; ++var3) {
            for(int var4 = 0; var4 < var0; ++var4) {
               int var5 = 18;
               int var6 = 24;
               int var7 = 46;
               short var8 = 240;
               if (var4 != 0 && var4 != var0 - 1 && var3 != 0 && var3 != var1 - 1) {
                  int var9 = var4 - 1;
                  int var10 = var3 - 1;
                  int var11 = var9 % 20;
                  if (var11 != 0 && var11 != 19 && var10 != 0 && var10 != 19) {
                     if (var11 != 1 && var11 != 18 && var10 != 1 && var10 != 18) {
                        var5 = 20 - var10 / 3;
                        var6 = 28 - var10 / 3;
                        var7 = 52 - var10 / 3;
                        var8 = 232;
                        if ((var11 == 9 || var11 == 10) && (var10 == 9 || var10 == 10)) {
                           var5 = 56;
                           var6 = 92;
                           var7 = 138;
                        }
                     } else {
                        var5 = 38;
                        var6 = 48;
                        var7 = 82;
                        var8 = 245;
                     }
                  } else if (var11 != 0 && var11 != 19 || var10 != 0 && var10 != 19) {
                     if (var11 != 0 && var10 != 0) {
                        var5 = 152;
                        var6 = 112;
                        var7 = 42;
                        var8 = 255;
                     } else {
                        var5 = 240;
                        var6 = 198;
                        var7 = 84;
                        var8 = 255;
                     }
                  } else {
                     var5 = 105;
                     var6 = 240;
                     var7 = 255;
                     var8 = 255;
                  }
               } else if (var4 > 2 && var4 < var0 - 3 || var3 > 2 && var3 < var1 - 3) {
                  var5 = 248;
                  var6 = 208;
                  var7 = 88;
                  var8 = 255;
               } else {
                  var5 = 90;
                  var6 = 242;
                  var7 = 255;
                  var8 = 255;
               }

               var2.put((byte)var5).put((byte)var6).put((byte)var7).put((byte)var8);
            }
         }

         var2.flip();
         hotbarPikselBuffer = var2;
         return var2;
      }
   }

   private static ByteBuffer olusturSecimBuffer() {
      if (secimPikselBuffer != null) {
         return secimPikselBuffer;
      } else {
         byte var0 = 24;
         byte var1 = 23;
         ByteBuffer var2 = ByteBuffer.allocateDirect(var0 * var1 * 4).order(ByteOrder.nativeOrder());

         for(int var3 = 0; var3 < var1; ++var3) {
            for(int var4 = 0; var4 < var0; ++var4) {
               short var5 = 0;
               short var6 = 0;
               short var7 = 0;
               short var8 = 0;
               if (var4 != 0 && var4 != var0 - 1 && var3 != 0 && var3 != var1 - 1) {
                  if (var4 != 1 && var4 != var0 - 2 && var3 != 1 && var3 != var1 - 2) {
                     if (var4 == 2 || var4 == var0 - 3 || var3 == 2 || var3 == var1 - 3) {
                        var5 = 188;
                        var6 = 142;
                        var7 = 46;
                        var8 = 255;
                     }
                  } else {
                     var5 = 255;
                     var6 = 222;
                     var7 = 98;
                     var8 = 255;
                  }
               } else {
                  var5 = 95;
                  var6 = 245;
                  var7 = 255;
                  var8 = 255;
               }

               var2.put((byte)var5).put((byte)var6).put((byte)var7).put((byte)var8);
            }
         }

         var2.flip();
         secimPikselBuffer = var2;
         return var2;
      }
   }

   private static void hotbarAtlasGuncelle(Object var0) {
      if (var0 != null) {
         try {
            int var1 = -1;

            for(Method var5 : var0.getClass().getMethods()) {
               if (var5.getParameterCount() == 0 && var5.getReturnType() == Integer.TYPE && var5.getName().toLowerCase(Locale.ROOT).contains("id")) {
                  var1 = (Integer)var5.invoke(var0);
                  break;
               }
            }

            if (var1 <= 0 && var0.getClass().getSuperclass() != null) {
               for(Field var19 : var0.getClass().getSuperclass().getDeclaredFields()) {
                  if (var19.getType() == Integer.TYPE) {
                     var19.setAccessible(true);
                     int var6 = var19.getInt(var0);
                     if (var6 > 0) {
                        var1 = var6;
                        break;
                     }
                  }
               }
            }

            if (var1 <= 0) {
               return;
            }

            Map var12 = null;

            for(Field var22 : var0.getClass().getDeclaredFields()) {
               if (Map.class.isAssignableFrom(var22.getType())) {
                  var22.setAccessible(true);
                  Object var7 = var22.get(var0);
                  if (var7 instanceof Map && !((Map)var7).isEmpty()) {
                     var12 = (Map)var7;
                     break;
                  }
               }
            }

            if (var12 == null) {
               return;
            }

            Class var15 = Class.forName("org.lwjgl.opengl.GL11");
            Method var18 = var15.getMethod("glBindTexture", Integer.TYPE, Integer.TYPE);
            Method var21 = var15.getMethod("glTexSubImage2D", Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, ByteBuffer.class);
            var18.invoke((Object)null, 3553, var1);
            Object var23 = var12.get(ResourceLocation.fromNamespaceAndPath("minecraft", "hud/hotbar"));
            if (var23 != null) {
               int[] var24 = spriteKoordinatBul(var23);
               if (var24 != null) {
                  ByteBuffer var8 = olusturHotbarBuffer();
                  var8.rewind();
                  var21.invoke((Object)null, 3553, 0, var24[0], var24[1], 182, 22, 6408, 5121, var8);
               }
            }

            Object var25 = var12.get(ResourceLocation.fromNamespaceAndPath("minecraft", "hud/hotbar_selection"));
            if (var25 != null) {
               int[] var26 = spriteKoordinatBul(var25);
               if (var26 != null) {
                  ByteBuffer var9 = olusturSecimBuffer();
                  var9.rewind();
                  var21.invoke((Object)null, 3553, 0, var26[0], var26[1], 24, 23, 6408, 5121, var9);
               }
            }
         } catch (Throwable var10) {
         }

      }
   }

   private static int[] spriteKoordinatBul(Object var0) {
      try {
         int[] var1 = new int[4];
         int var2 = 0;

         for(Field var6 : var0.getClass().getDeclaredFields()) {
            if (var6.getType() == Integer.TYPE) {
               var6.setAccessible(true);
               var1[var2++] = var6.getInt(var0);
               if (var2 >= 2) {
                  break;
               }
            }
         }

         if (var2 >= 2) {
            return new int[]{var1[0], var1[1]};
         }
      } catch (Throwable var7) {
      }

      return null;
   }

   private static void ozelEnvanterDokusuBagla(Minecraft var0, int var1) {
      if (var1 - sonDokuBaglamaTick >= 30 || sonDokuBaglamaTick < 0) {
         sonDokuBaglamaTick = var1;

         try {
            Object var2 = null;

            for(Field var6 : var0.getClass().getDeclaredFields()) {
               if (var6.getType().getSimpleName().contains("TextureManager")) {
                  var6.setAccessible(true);
                  var2 = var6.get(var0);
                  break;
               }
            }

            if (var2 == null) {
               return;
            }

            Method var12 = null;
            Method var13 = null;

            for(Method var8 : var2.getClass().getMethods()) {
               Class[] var9 = var8.getParameterTypes();
               if (var9.length == 1 && var9[0] == ResourceLocation.class && var8.getReturnType() != Void.TYPE) {
                  var12 = var8;
               } else if (var9.length == 2 && var9[0] == ResourceLocation.class && var8.getReturnType() == Void.TYPE && var9[1].getSimpleName().contains("Texture")) {
                  var13 = var8;
               }
            }

            Map var15 = null;

            for(Field var24 : var2.getClass().getDeclaredFields()) {
               if (Map.class.isAssignableFrom(var24.getType())) {
                  var24.setAccessible(true);
                  Object var10 = var24.get(var2);
                  if (var10 instanceof Map) {
                     var15 = (Map)var10;
                     break;
                  }
               }
            }

            if (var12 != null) {
               for(ResourceLocation[] var25 : ENVANTER_DOKU_ESLEMELERI) {
                  Object var26 = var12.invoke(var2, var25[0]);
                  if (var26 != null) {
                     if (var15 != null) {
                        var15.put(var25[1], var26);
                     }

                     if (var13 != null) {
                        var13.invoke(var2, var25[1], var26);
                     }
                  }
               }

               if (var15 != null) {
                  Object var19 = var15.get(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/atlas/gui.png"));
                  hotbarAtlasGuncelle(var19);
               }
            }
         } catch (Throwable var11) {
         }

      }
   }

   private static float hesaplaGunEvresi(Level var0) {
      long var1 = var0.getDayTime() % 24000L;
      if (var1 < 0L) {
         var1 += 24000L;
      }

      if (var1 < 11500L) {
         return 0.0F;
      } else if (var1 < 13800L) {
         return (float)(var1 - 11500L) / 2300.0F;
      } else {
         return var1 < 22200L ? 1.0F : 1.0F - (float)(var1 - 22200L) / 1800.0F;
      }
   }

   private static float hesaplaSabahEvresi(Level var0) {
      long var1 = var0.getDayTime() % 24000L;
      if (var1 < 0L) {
         var1 += 24000L;
      }

      if (var1 >= 22000L) {
         return (float)(var1 - 22000L) / 2000.0F;
      } else if (var1 <= 3500L) {
         return 1.0F;
      } else if (var1 <= 7500L) {
         return 1.0F - 0.65F * ((float)(var1 - 3500L) / 4000.0F);
      } else {
         return var1 <= 11500L ? 0.35F : 0.0F;
      }
   }

   private static float zifiriBataklikOrani(LocalPlayer var0) {
      if (var0 == null) {
         return 0.0F;
      } else {
         ResourceKey var1 = var0.level().dimension();
         return var1.equals(NurSistemi.ZIFIRI_BATAKLIK) ? 1.0F : 0.0F;
      }
   }

   private static boolean zifiriMesaleTutuyorMu(LocalPlayer var0) {
      if (var0 != null && var0.level().dimension().equals(NurSistemi.ZIFIRI_BATAKLIK)) {
         try {
            ItemStack var1 = var0.getMainHandItem();
            if (var1 != null && !var1.isEmpty() && var1.is((Item)CennetMod.ZIFIRI_MESALE.get())) {
               return true;
            }

            ItemStack var2 = var0.getOffhandItem();
            if (var2 != null && !var2.isEmpty() && var2.is((Item)CennetMod.ZIFIRI_MESALE.get())) {
               return true;
            }
         } catch (Throwable var3) {
         }

         return false;
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void shader(ViewportEvent.ComputeFov var0) {
      Minecraft var1 = Minecraft.getInstance();
      LocalPlayer var2 = var1.player;
      if (var2 != null && var1.gameRenderer != null && hataSayaci <= 20) {
         ozelEnvanterDokusuBagla(var1, var2.tickCount);
         Level var3 = var2.level();
         ResourceKey var4 = var3.dimension();
         boolean var5 = var4.equals(NurSistemi.KRALLIK);
         boolean var6 = var4.equals(NurSistemi.ZIFIRI_BATAKLIK);
         boolean var7 = var4.equals(YarikAsasi.CENNET) || var6;
         if (sonBoyut != null && !sonBoyut.equals(var4)) {
            varisParlamasi = 1.0F;
         }

         sonBoyut = var4;
         if (varisParlamasi > 0.0F) {
            varisParlamasi = Math.max(0.0F, varisParlamasi - 0.012F);
         }

         float var8 = zifiriBataklikOrani(var2);
         float var9 = var7 ? 1.0F - 2.0F * var8 : (var5 ? 0.0F : hesaplaGunEvresi(var3));
         float var10 = var6 ? (zifiriMesaleTutuyorMu(var2) ? 1.0F : 0.0F) : (!var7 && !var5 ? hesaplaSabahEvresi(var3) : 0.0F);
         yumusakGece += (var9 - yumusakGece) * 0.05F;
         yumusakSabah += (var10 - yumusakSabah) * 0.12F;
         float var11 = var7 ? 0.85F + 0.12F * var8 : (var5 ? 0.8F : 0.82F + 0.08F * Math.max(Math.abs(yumusakGece), yumusakSabah));
         int var12 = ticks();
         float var13 = varisParlamasi;
         if (var12 >= 20) {
            float var14 = Math.min(1.0F, (float)(var12 - 20) / 220.0F);
            var11 = Math.max(var11, 0.65F + 0.35F * var14);
            var13 = Math.max(var13, var14);
         }

         yumusakYarik += (var13 - yumusakYarik) * 0.08F;
         yumusak += (var11 - yumusak) * 0.06F;
         ResourceLocation var20;
         if (var12 < 30 && !var7) {
            if (var5) {
               var20 = EFEKT_NUR;
            } else {
               var20 = EFEKT_DUNYA;
            }
         } else {
            var20 = EFEKT_CENNET;
         }

         if (!(var11 > 0.01F) && !(yumusak > 0.01F)) {
            if (yukluEfekt != null) {
               var1.gameRenderer.shutdownEffect();
               yukluEfekt = null;
            }
         } else {
            PostChain var15 = var1.gameRenderer.currentEffect();
            if (yukluEfekt != null && (!yukluEfekt.equals(var20) || var15 == null)) {
               var1.gameRenderer.shutdownEffect();
               yukluEfekt = null;
            }

            if (yukluEfekt == null) {
               try {
                  var1.gameRenderer.loadEffect(var20);
               } catch (Exception var19) {
                  ++hataSayaci;
                  return;
               }

               if (var1.gameRenderer.currentEffect() == null) {
                  ++hataSayaci;
                  return;
               }

               hataSayaci = 0;
               yukluEfekt = var20;
            }

            PostChain var16 = var1.gameRenderer.currentEffect();
            if (var16 != null) {
               float var17 = ((float)(var2.tickCount % 24000) + (float)var0.getPartialTick()) * 0.05F;
               float var18 = Math.max(0.0F, Math.min(1.0F, (90.0F - var2.getXRot()) / 180.0F));
               var16.setUniform("Intensity", yumusak);
               var16.setUniform("Time", var17);
               var16.setUniform("DayPhase", yumusakGece);
               var16.setUniform("MorningPhase", yumusakSabah);
               var16.setUniform("PitchFactor", var18);
               var16.setUniform("RiftWarp", yumusakYarik);
            }
         }

      }
   }

   private static int ticks() {
      LocalPlayer var0 = Minecraft.getInstance().player;
      return var0 != null && var0.isUsingItem() && var0.getUseItem().getItem() instanceof YarikAsasi ? var0.getTicksUsingItem() : -1;
   }

   @SubscribeEvent
   public static void sis(ViewportEvent.ComputeFogColor var0) {
      Minecraft var1 = Minecraft.getInstance();
      LocalPlayer var2 = var1.player;
      if (var2 != null) {
         Level var3 = var2.level();
         ResourceKey var4 = var3.dimension();
         float var5 = zifiriBataklikOrani(var2);
         if (var4.equals(NurSistemi.ZIFIRI_BATAKLIK) || var4.equals(YarikAsasi.CENNET) && var5 > 0.01F) {
            float var15 = var0.getRed() * 0.55F + 0.045F;
            float var16 = var0.getGreen() * 0.62F + 0.085F;
            float var17 = Math.min(1.0F, var0.getBlue() * 0.78F + 0.19F);
            boolean var18 = zifiriMesaleTutuyorMu(var2);
            float var19 = var18 ? 0.026F : 0.012F;
            float var20 = var18 ? 0.019F : 0.012F;
            float var21 = 0.016F;
            var0.setRed(var15 * (1.0F - var5) + var19 * var5);
            var0.setGreen(var16 * (1.0F - var5) + var20 * var5);
            var0.setBlue(var17 * (1.0F - var5) + var21 * var5);
         } else if (var4.equals(YarikAsasi.CENNET)) {
            var0.setRed(var0.getRed() * 0.55F + 0.045F);
            var0.setGreen(var0.getGreen() * 0.62F + 0.085F);
            var0.setBlue(Math.min(1.0F, var0.getBlue() * 0.78F + 0.19F));
         } else if (var4.equals(NurSistemi.KRALLIK)) {
            var0.setRed(Math.min(1.0F, var0.getRed() * 0.75F + 0.24F));
            var0.setGreen(Math.min(1.0F, var0.getGreen() * 0.75F + 0.23F));
            var0.setBlue(Math.min(1.0F, var0.getBlue() * 0.78F + 0.21F));
         } else {
            float var6 = hesaplaGunEvresi(var3);
            float var7 = hesaplaSabahEvresi(var3);
            float var8 = Math.max(0.0F, Math.min(1.0F, 1.0F - Math.abs(var6 - 0.45F) * 2.4F));
            float var9 = Math.max(0.0F, Math.min(1.0F, (var6 - 0.45F) / 0.55F));
            float var10 = var0.getRed();
            float var11 = var0.getGreen();
            float var12 = var0.getBlue();
            if (var7 > 0.01F && var9 < 0.25F) {
               var10 += (0.98F - var10) * var7 * 0.3F;
               var11 += (0.84F - var11) * var7 * 0.24F;
               var12 += (0.68F - var12) * var7 * 0.18F;
            }

            if (var8 > 0.01F) {
               var10 += (0.88F - var10) * var8 * 0.28F;
               var11 += (0.48F - var11) * var8 * 0.22F;
               var12 += (0.62F - var12) * var8 * 0.25F;
            }

            if (var9 > 0.01F) {
               var10 += (0.06F - var10) * var9 * 0.35F;
               var11 += (0.11F - var11) * var9 * 0.35F;
               var12 = Math.min(1.0F, var12 + (0.26F - var12) * var9 * 0.42F);
            }

            var0.setRed(Math.max(0.0F, Math.min(1.0F, var10)));
            var0.setGreen(Math.max(0.0F, Math.min(1.0F, var11)));
            var0.setBlue(Math.max(0.0F, Math.min(1.0F, var12)));
         }
      }

      int var13 = ticks();
      float var14 = var13 >= 30 ? Math.min(1.0F, (float)(var13 - 30) / 90.0F) : varisParlamasi;
      if (!(var14 <= 0.01F)) {
         var0.setRed(var0.getRed() + (0.38F - var0.getRed()) * var14);
         var0.setGreen(var0.getGreen() + (0.12F - var0.getGreen()) * var14);
         var0.setBlue(var0.getBlue() + (0.85F - var0.getBlue()) * var14);
      }
   }

   @SubscribeEvent
   public static void kamera(ViewportEvent.ComputeCameraAngles var0) {
      Minecraft var1 = Minecraft.getInstance();
      LocalPlayer var2 = var1.player;
      if (var2 != null && var2.getVehicle() instanceof AngelSoldier) {
         AngelSoldier var3 = (AngelSoldier)var2.getVehicle();
         int var4 = var3.getPoz();
         if (var3.getRol() == 50 || var4 >= 50 && var4 < 60) {
            var3.yBodyRot = var3.getYRot();
            var3.yBodyRotO = var3.yRotO;
         }
      }

      int var7 = ticks();
      if (var7 >= 80) {
         float var8 = Math.min(1.0F, (float)(var7 - 80) / 120.0F);
         double var5 = (double)var7 + var0.getPartialTick();
         var0.setRoll((float)((double)var0.getRoll() + Math.sin(var5 * 2.1) * 1.8 * (double)var8));
         var0.setPitch((float)((double)var0.getPitch() + Math.sin(var5 * 2.9) * 0.7 * (double)var8));
         var0.setYaw((float)((double)var0.getYaw() + Math.cos(var5 * 2.4) * 0.7 * (double)var8));
      } else if (varisParlamasi > 0.05F) {
         double var9 = (double)(var2 != null ? var2.tickCount : 0) + var0.getPartialTick();
         var0.setRoll((float)((double)var0.getRoll() + Math.sin(var9 * 1.4) * 1.1 * (double)varisParlamasi));
      }

   }

   @SubscribeEvent
   public static void fov(ViewportEvent.ComputeFov var0) {
      int var1 = ticks();
      if (var1 >= 80) {
         float var2 = Math.min(1.0F, (float)(var1 - 80) / 160.0F);
         var0.setFOV(var0.getFOV() + (double)26.0F * (double)var2 * (double)var2);
      } else if (varisParlamasi > 0.01F) {
         var0.setFOV(var0.getFOV() + (double)22.0F * (double)varisParlamasi * (double)varisParlamasi);
      }

   }
}
