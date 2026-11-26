package com.cennetmod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "cennetmod",
   bus = Bus.FORGE
)
public class NurSistemi {
   public static final ResourceKey<Level> KRALLIK;
   public static final ResourceKey<Level> ZIFIRI_BATAKLIK;
   public static final double SINIR_Z = (double)-1500.0F;
   private static final double BASKENT_X = (double)0.5F;
   private static final double BASKENT_Y = (double)101.0F;
   private static final double BASKENT_Z = (double)274.5F;

   public static boolean cennetBoyutu(Level var0) {
      ResourceKey var1 = var0.dimension();
      return var1.equals(YarikAsasi.CENNET) || var1.equals(KRALLIK) || var1.equals(ZIFIRI_BATAKLIK);
   }

   public static boolean kartVar(ServerPlayer var0) {
      Inventory var1 = var0.getInventory();

      for(int var2 = 0; var2 < var1.getContainerSize(); ++var2) {
         ItemStack var3 = var1.getItem(var2);
         if (!var3.isEmpty() && var3.is((Item)CennetMod.NUR_KARTI.get())) {
            return true;
         }
      }

      return false;
   }

   public static boolean batiKartiVar(ServerPlayer var0) {
      Inventory var1 = var0.getInventory();

      for(int var2 = 0; var2 < var1.getContainerSize(); ++var2) {
         ItemStack var3 = var1.getItem(var2);
         if (!var3.isEmpty() && var3.is((Item)CennetMod.BATI_KARTI.get())) {
            return true;
         }
      }

      return false;
   }

   public static boolean zifiriMesaleVar(ServerPlayer var0) {
      Inventory var1 = var0.getInventory();

      for(int var2 = 0; var2 < var1.getContainerSize(); ++var2) {
         ItemStack var3 = var1.getItem(var2);
         if (var3 != null && !var3.isEmpty() && var3.is((Item)CennetMod.ZIFIRI_MESALE.get())) {
            return true;
         }
      }

      return false;
   }

   public static boolean zifiriMesaleEldemi(ServerPlayer var0) {
      if (var0 != null && var0.level().dimension().equals(ZIFIRI_BATAKLIK)) {
         ItemStack var1 = var0.getMainHandItem();
         if (var1 != null && !var1.isEmpty() && var1.is((Item)CennetMod.ZIFIRI_MESALE.get())) {
            return true;
         } else {
            ItemStack var2 = var0.getOffhandItem();
            return var2 != null && !var2.isEmpty() && var2.is((Item)CennetMod.ZIFIRI_MESALE.get());
         }
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void tick(LivingEvent.LivingTickEvent var0) {
      if (var0.getEntity() instanceof ServerPlayer) {
         ServerPlayer var1 = (ServerPlayer)var0.getEntity();
         NurKapi.isle();
         if (!var1.isSpectator() && var1.level() instanceof ServerLevel) {
            ServerLevel var2 = (ServerLevel)var1.level();
            sinematikAnimasyonTick(var2, var1);
            yarikVarisSinematikTick(var2, var1);
            ozelEnvanterSlotiKontrol(var1);
            pegasusBiletiKontrol(var1);
            zifiriMesaleIsikKontrol(var2, var1);
            zifiriBataklikKontrol(var2, var1);
            if (var1.tickCount % 5 == 0) {
               NurKapi.Kapi var3 = (NurKapi.Kapi)NurKapi.KAPILAR.get(var1.getUUID());
               if (var3 != null && (var1.level() != var3.sl || var1.distanceToSqr((double)var3.kx, (double)var3.ky, (double)var3.kz) > (double)22500.0F)) {
                  NurKapi.temizle(var1.getUUID());
               }

               ResourceKey var4 = var1.level().dimension();
               if (var4.equals(YarikAsasi.CENNET)) {
                  kapidanGecisKontrol(var1);
                  yildizIsigiYanBiyomKontrol(var2, var1);
                  bolgeSinematikKontrol(var2, var1, false);
                  if (var1.tickCount % 100 == 0) {
                     AngelSoldier.bolgeNpcKontrol(var2, var1);
                  }
               } else if (var4.equals(KRALLIK)) {
                  if (!kartVar(var1)) {
                     geriCagir(var1);
                     return;
                  }

                  bolgeSinematikKontrol(var2, var1, true);
                  if (var1.tickCount % 120 == 0) {
                     AngelSoldier.bolgeNpcKontrol(var2, var1);
                  }
               }

            }
         }
      }
   }

   @SubscribeEvent
   public static void cikis(PlayerEvent.PlayerLoggedOutEvent var0) {
      if (var0.getEntity() instanceof ServerPlayer) {
         ServerPlayer var1 = (ServerPlayer)var0.getEntity();
         if (var1.level() instanceof ServerLevel) {
            eskiMesaleIsiginiSil((ServerLevel)var1.level(), var1.getPersistentData());
            eskiCamurCukurunuKapat((ServerLevel)var1.level(), var1.getPersistentData());
         }
      }

      NurKapi.temizle(var0.getEntity().getUUID());
   }

   private static void eskiCamurCukurunuKapat(ServerLevel var0, CompoundTag var1) {
      if (var1.getInt("CamurCukurAktif") == 1) {
         int var2 = var1.getInt("CamurCukurX");
         int var3 = var1.getInt("CamurCukurZ");

         for(int var4 = 68; var4 <= 70; ++var4) {
            BlockPos var5 = new BlockPos(var2, var4, var3);
            if (var0.getBlockState(var5).isAir()) {
               var0.setBlock(var5, Blocks.MUD.defaultBlockState(), 2);
            }
         }

         var1.putInt("CamurCukurAktif", 0);
      }

   }

   private static void eskiMesaleIsiginiSil(ServerLevel var0, CompoundTag var1) {
      if (var1.getInt("MesaleIsikAcik") == 1) {
         BlockPos var2 = new BlockPos(var1.getInt("MesaleIsikX"), var1.getInt("MesaleIsikY"), var1.getInt("MesaleIsikZ"));
         if (var0.getBlockState(var2).is(Blocks.LIGHT)) {
            var0.setBlock(var2, Blocks.AIR.defaultBlockState(), 2);
         }

         BlockPos var3 = new BlockPos(var1.getInt("MesaleOnX"), var1.getInt("MesaleOnY"), var1.getInt("MesaleOnZ"));
         if (var0.getBlockState(var3).is(Blocks.LIGHT)) {
            var0.setBlock(var3, Blocks.AIR.defaultBlockState(), 2);
         }

         var1.putInt("MesaleIsikAcik", 0);
      }

   }

   private static BlockPos uygunZeminUstuIsikYeri(ServerLevel var0, int var1, int var2, int var3) {
      for(int var4 = 0; var4 <= 2; ++var4) {
         BlockPos var5 = new BlockPos(var1, var2 + var4, var3);
         BlockState var6 = var0.getBlockState(var5);
         if (var6.isAir() || var6.is(Blocks.LIGHT)) {
            return var5;
         }
      }

      return new BlockPos(var1, var2 + 1, var3);
   }

   private static void zifiriMesaleIsikKontrol(ServerLevel var0, ServerPlayer var1) {
      CompoundTag var2 = var1.getPersistentData();
      boolean var3 = zifiriMesaleEldemi(var1);
      if (!var3) {
         if (var2.getInt("MesaleIsikAcik") == 1) {
            eskiMesaleIsiginiSil(var0, var2);
         }

      } else {
         int var4 = Mth.floor(var1.getX());
         int var5 = Mth.floor(var1.getY());
         int var6 = Mth.floor(var1.getZ());
         BlockPos var7 = uygunZeminUstuIsikYeri(var0, var4, var5, var6);
         float var8 = var1.getYRot() * ((float)Math.PI / 180F);
         double var9 = (double)(-Mth.sin(var8));
         double var11 = (double)Mth.cos(var8);
         if (var2.getInt("MesaleIsikAcik") == 1) {
            int var13 = var2.getInt("MesaleIsikX");
            int var14 = var2.getInt("MesaleIsikY");
            int var15 = var2.getInt("MesaleIsikZ");
            if (var13 != var7.getX() || var14 != var7.getY() || var15 != var7.getZ() || var2.getInt("MesaleOnY") != 0) {
               eskiMesaleIsiginiSil(var0, var2);
            }
         }

         BlockState var29 = Blocks.LIGHT.defaultBlockState();

         try {
            var29 = (BlockState)var29.setValue(LightBlock.LEVEL, 5);
         } catch (Throwable var28) {
         }

         BlockState var30 = var0.getBlockState(var7);
         if (var30.isAir() || var30.is(Blocks.LIGHT)) {
            var0.setBlock(var7, var29, 2);
         }

         var2.putInt("MesaleIsikAcik", 1);
         var2.putInt("MesaleIsikX", var7.getX());
         var2.putInt("MesaleIsikY", var7.getY());
         var2.putInt("MesaleIsikZ", var7.getZ());
         var2.putInt("MesaleOnX", 0);
         var2.putInt("MesaleOnY", 0);
         var2.putInt("MesaleOnZ", 0);
         if (var1.tickCount % 6 == 0) {
            double var31 = (double)Mth.cos(var8);
            double var17 = (double)Mth.sin(var8);
            boolean var19 = var1.getMainHandItem() != null && var1.getMainHandItem().is((Item)CennetMod.ZIFIRI_MESALE.get());
            double var20 = var19 ? -0.32 : 0.32;
            double var22 = var1.getX() + var9 * 0.45 + var31 * var20;
            double var24 = var1.getY() + 1.32;
            double var26 = var1.getZ() + var11 * 0.45 + var17 * var20;
            var0.sendParticles(var1, ParticleTypes.FLAME, true, var22, var24, var26, 1, 0.02, 0.03, 0.02, 0.002);
            var0.sendParticles(var1, ParticleTypes.SMOKE, true, var22, var24 + 0.08, var26, 1, 0.02, 0.04, 0.02, 0.002);
         }

         if (var1.tickCount % 55 == 0) {
            var0.playSound((Player)null, var1.blockPosition(), SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS, 0.35F, 0.95F);
         }

      }
   }

   public static void zifiriBataklikBoyutunaGec(ServerPlayer var0) {
      ServerLevel var1 = var0.server.getLevel(ZIFIRI_BATAKLIK);
      if (var1 != null) {
         CompoundTag var2 = var0.getPersistentData();
         if (var0.level() instanceof ServerLevel) {
            eskiMesaleIsiginiSil((ServerLevel)var0.level(), var2);
         }

         var2.putInt("ZifiriPortalCool", 100);
         var2.putLong("SonrakiCamurSaati", var1.getGameTime() + 1200L);
         if (!zifiriMesaleVar(var0)) {
            var0.getInventory().add(new ItemStack((ItemLike)CennetMod.ZIFIRI_MESALE.get()));
         }

         gec(var0, ZIFIRI_BATAKLIK, (double)0.5F, (double)72.0F, (double)0.5F, 0.0F, false);
         sinematikBaslat(var1, var0, "☠ GÜNEY — ZİFİRİ BATAKLIK ☠", "Zifiri Kale ve Surlu Köye Giden Taş Patikayı Takip Et!", 6);
         var0.sendSystemMessage(Component.literal("✦ Güney Zifiri Bataklık Boyutu'na ulaştın! ").withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.BOLD}).append(Component.literal("Envanterine sadece bu boyutta yanan ve elinde tuttuğunda 3 blok çevreni aydınlatan ").withStyle(ChatFormatting.WHITE)).append(Component.literal("[Zifiri Bataklık Meşalesi]").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD})).append(Component.literal(" eklendi! Güney patikasında (0, 72, 160) Zifiri Kale ve Surlu Köy, Batı (-160, 0) ve Doğu'da (160, 0) diğer 2 surlu yerleşim seni bekliyor.").withStyle(ChatFormatting.WHITE)));
      }
   }

   private static void zifiriBataklikKontrol(ServerLevel var0, ServerPlayer var1) {
      ResourceKey var2 = var0.dimension();
      double var3 = var1.getX();
      double var5 = var1.getY();
      double var7 = var1.getZ();
      CompoundTag var9 = var1.getPersistentData();
      int var10 = var9.getInt("ZifiriPortalCool");
      if (var10 > 0) {
         var9.putInt("ZifiriPortalCool", var10 - 1);
      }

      if (var2.equals(YarikAsasi.CENNET)) {
         if (var10 <= 0 && Math.abs(var3) <= (double)3.0F && Math.abs(var7 - (double)1500.0F) <= (double)2.0F) {
            zifiriBataklikBoyutunaGec(var1);
         }

      } else if (var2.equals(ZIFIRI_BATAKLIK)) {
         if (var1.tickCount % 40 == 0 && var9.getInt("ZifiriMesaleVerildi") == 0) {
            var9.putInt("ZifiriMesaleVerildi", 1);
            if (!zifiriMesaleVar(var1)) {
               var1.getInventory().add(new ItemStack((ItemLike)CennetMod.ZIFIRI_MESALE.get()));
               var1.sendSystemMessage(Component.literal("✦ Envanterine [Zifiri Bataklık Meşalesi] eklendi! Elinde tuttuğunda 3 blok çevreni aydınlatır. ✦").withStyle(ChatFormatting.GOLD));
            }
         }

         if (var10 <= 0 && Math.abs(var3) <= (double)3.0F && Math.abs(var7 - (double)-7.0F) <= (double)1.5F && var5 >= (double)70.0F && var5 <= (double)78.0F) {
            eskiMesaleIsiginiSil(var0, var9);
            var9.putInt("ZifiriPortalCool", 100);
            gec(var1, YarikAsasi.CENNET, (double)0.5F, (double)120.0F, (double)0.5F, 0.0F, true);
            if (var1.level() instanceof ServerLevel) {
               sinematikBaslat((ServerLevel)var1.level(), var1, "\ud83c\udf0c YILDIZ IŞIĞI ORMANI \ud83c\udf0c", "Zifiri Bataklık'tan Yıldız Işığı Diyarı'na Döndün", 4);
            }

         } else {
            int var11 = Mth.floor(var3);
            int var12 = Mth.floor(var7);
            var1.removeEffect(MobEffects.NIGHT_VISION);
            boolean var13 = NurBaskentFeature.bataklikSaglamZeminMi(var11, var12);
            if (!var13 && !var1.isPassenger() && var5 <= (double)71.25F && var5 >= 66.2) {
               if (var9.getInt("CamurCukurAktif") == 1 && (var9.getInt("CamurCukurX") != var11 || var9.getInt("CamurCukurZ") != var12)) {
                  eskiCamurCukurunuKapat(var0, var9);
               }

               double var14 = var9.getDouble("CamurBatma") + 0.018;
               var9.putDouble("CamurBatma", var14);
               int var16 = var14 >= 1.1 ? 4 : (var14 >= 0.52 ? 3 : 2);
               var1.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, var16, false, false, false));
               var9.putInt("CamurCukurAktif", 1);
               var9.putInt("CamurCukurX", var11);
               var9.putInt("CamurCukurZ", var12);
               if (var14 >= 0.52) {
                  BlockPos var17 = new BlockPos(var11, 70, var12);
                  if (var0.getBlockState(var17).is(Blocks.MUD)) {
                     var0.setBlock(var17, Blocks.AIR.defaultBlockState(), 2);
                     var0.playSound((Player)null, var17, SoundEvents.STONE_PLACE, SoundSource.NEUTRAL, 1.3F, 0.48F);
                  }
               }

               if (var14 >= 1.1) {
                  BlockPos var35 = new BlockPos(var11, 69, var12);
                  if (var0.getBlockState(var35).is(Blocks.MUD)) {
                     var0.setBlock(var35, Blocks.AIR.defaultBlockState(), 2);
                     var0.playSound((Player)null, var35, SoundEvents.STONE_PLACE, SoundSource.NEUTRAL, 1.5F, 0.42F);
                     var1.displayClientMessage(Component.literal("☠ Bataklık çamuru seni acımasızca aşağıya çekiyor! Çabuk zıplayarak taşlara tutun! ☠").withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD}), true);
                  }
               }

               if (var14 >= 1.65) {
                  BlockPos var36 = new BlockPos(var11, 68, var12);
                  if (var0.getBlockState(var36).is(Blocks.MUD)) {
                     var0.setBlock(var36, Blocks.AIR.defaultBlockState(), 2);
                  }

                  if (var1.tickCount % 16 == 0) {
                     var1.hurt(var0.damageSources().drown(), 2.5F);
                     var0.playSound((Player)null, var1.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.NEUTRAL, 1.4F, 0.45F);
                  }
               }

               if (var1.tickCount % 18 == 0) {
                  var0.playSound((Player)null, var1.blockPosition(), SoundEvents.STONE_PLACE, SoundSource.NEUTRAL, 0.9F, 0.5F);
                  var0.sendParticles(var1, ParticleTypes.LARGE_SMOKE, true, var3, var5 + 0.3, var7, 4, (double)0.25F, 0.1, (double)0.25F, 0.01);
               }
            } else {
               if (var9.getInt("CamurCukurAktif") == 1) {
                  eskiCamurCukurunuKapat(var0, var9);
               }

               if (var9.getDouble("CamurBatma") > (double)0.0F) {
                  var9.putDouble("CamurBatma", (double)0.0F);
               }
            }

            if (var1.tickCount % 30 == 0) {
               zifiriKoyluKontrol(var0, var1, var3, var7);
            }

            koyluUzunBakisKontrol(var0, var1);
            if (var1.tickCount % 45 == 0) {
               List var31 = var0.getEntitiesOfClass(AngelSoldier.class, var1.getBoundingBox().inflate((double)32.0F), (var0x) -> var0x.getRol() == 61);
               if (var31 == null || var31.size() < 5) {
                  agaclardaGozleriCikar(var0, var1, var11, var12);
               }
            }

            long var32 = var0.getGameTime();
            long var33 = var9.getLong("SonrakiCamurSaati");
            if (var33 <= 0L) {
               var33 = var32 + 1200L;
               var9.putLong("SonrakiCamurSaati", var33);
            } else if (var32 >= var33) {
               long var18 = 1000L + Math.abs((long)var1.tickCount * 37L + (long)var11 * 13L + (long)var12 * 29L) % 1001L;
               var9.putLong("SonrakiCamurSaati", var32 + var18);
               if (!NurBaskentFeature.zifiriYerlesimIciMi(var11, var12)) {
                  List var20 = var0.getEntitiesOfClass(AngelSoldier.class, var1.getBoundingBox().inflate((double)48.0F), (var0x) -> var0x.getRol() == 60);
                  if (var20 == null || var20.size() < 2) {
                     int var21 = 0;

                     for(int var22 = 0; var22 < 2; ++var22) {
                        int var23 = var1.tickCount * 37 + var22 * 97 ^ var11 * 13 + var12 * 29;
                        double var24 = (double)(var23 & 255) / (double)256.0F * Math.PI * (double)2.0F;
                        double var26 = (double)14.0F + (double)(Math.abs(var23 >> 8) % 8);
                        int var28 = Mth.floor(var3 + Math.cos(var24) * var26);
                        int var29 = Mth.floor(var7 + Math.sin(var24) * var26);
                        if (var28 * var28 + var29 * var29 > 144 && !NurBaskentFeature.bataklikSaglamZeminMi(var28, var29)) {
                           AngelSoldier var30 = (AngelSoldier)((EntityType)CennetMod.MELEK.get()).create(var0);
                           if (var30 != null) {
                              var30.moveTo((double)var28 + (double)0.5F, (double)71.0F, (double)var29 + (double)0.5F, 0.0F, 0.0F);
                              var30.setNpcRol(60, var28, 71, var29, var1.getUUID());
                              var0.addFreshEntity(var30);
                              ++var21;
                           }
                        }
                     }

                     if (var21 > 0) {
                        var0.playSound((Player)null, var1.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.NEUTRAL, 1.2F, 0.55F);
                        var1.displayClientMessage(Component.literal("☠ Bataklık çamuru kıpırdanıyor... Çamurdan karanlık yaratıklar yükseldi! ☠").withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}), true);
                     }
                  }
               }
            }

            if (var1.tickCount % 120 == 0 && !NurBaskentFeature.zifiriYerlesimIciMi(var11, var12)) {
               List var37 = var0.getEntitiesOfClass(AngelSoldier.class, var1.getBoundingBox().inflate((double)55.0F), (var0x) -> var0x.getRol() == 62);
               if (var37 == null || var37.isEmpty()) {
                  int var19 = var1.tickCount * 53 + 7 ^ var11 * 31 + var12 * 17;
                  double var38 = (double)(var19 & 255) / (double)256.0F * Math.PI * (double)2.0F;
                  double var39 = (double)19.5F + (double)(Math.abs(var19 >> 8) % 7);
                  int var40 = Mth.floor(var3 + Math.cos(var38) * var39);
                  int var25 = Mth.floor(var7 + Math.sin(var38) * var39);
                  int var41 = NurBaskentFeature.bataklikSaglamZeminMi(var40, var25) ? 72 : 71;
                  AngelSoldier var27 = (AngelSoldier)((EntityType)CennetMod.MELEK.get()).create(var0);
                  if (var27 != null) {
                     var27.moveTo((double)var40 + (double)0.5F, (double)var41, (double)var25 + (double)0.5F, 0.0F, 0.0F);
                     var27.setNpcRol(62, var40, var41, var25, var1.getUUID());
                     var0.addFreshEntity(var27);
                  }
               }
            }

         }
      }
   }

   private static void zifiriKoyluKontrol(ServerLevel var0, ServerPlayer var1, double var2, double var4) {
      int var6 = -1;

      for(int var7 = 0; var7 < NurBaskentFeature.ZIFIRI_YERLESIMLER.length; ++var7) {
         double var8 = var2 - (double)NurBaskentFeature.ZIFIRI_YERLESIMLER[var7][0];
         double var10 = var4 - (double)NurBaskentFeature.ZIFIRI_YERLESIMLER[var7][1];
         double var12 = (double)NurBaskentFeature.ZIFIRI_YERLESIMLER[var7][2] + (double)32.0F;
         if (var8 * var8 + var10 * var10 <= var12 * var12) {
            var6 = var7;
            break;
         }
      }

      if (var6 >= 0) {
         List var16 = var0.getEntitiesOfClass(AngelSoldier.class, var1.getBoundingBox().inflate((double)68.0F), (var0x) -> var0x.getRol() == 63);
         int var17 = var16 == null ? 0 : var16.size();
         if (var17 < 6) {
            int var9 = NurBaskentFeature.ZIFIRI_YERLESIMLER[var6][0];
            int var18 = NurBaskentFeature.ZIFIRI_YERLESIMLER[var6][1];
            int[][] var11 = new int[][]{{0, -24}, {-22, 0}, {22, 0}, {-20, -22}, {20, -22}, {0, 24}};

            for(int var19 = var17; var19 < var11.length; ++var19) {
               int var13 = var9 + var11[var19][0];
               int var14 = var18 + var11[var19][1];
               AngelSoldier var15 = (AngelSoldier)((EntityType)CennetMod.MELEK.get()).create(var0);
               if (var15 != null) {
                  var15.moveTo((double)var13 + (double)0.5F, (double)72.0F, (double)var14 + (double)0.5F, 0.0F, 0.0F);
                  var15.setNpcRol(63, var13, 72, var14, var1.getUUID());
                  var0.addFreshEntity(var15);
               }
            }

         }
      }
   }

   private static void koyluUzunBakisKontrol(ServerLevel var0, ServerPlayer var1) {
      List var2 = var0.getEntitiesOfClass(AngelSoldier.class, var1.getBoundingBox().inflate((double)24.0F), (var0x) -> var0x.getRol() == 63 && var0x.isAlive());
      CompoundTag var3 = var1.getPersistentData();
      if (var2 != null && !var2.isEmpty()) {
         Vec3 var4 = var1.getEyePosition();
         Vec3 var5 = var1.getLookAngle();
         double var6 = Math.max(0.001, Math.sqrt(var5.x * var5.x + var5.y * var5.y + var5.z * var5.z));
         double var8 = var5.x / var6;
         double var10 = var5.y / var6;
         double var12 = var5.z / var6;
         AngelSoldier var14 = null;
         double var15 = 0.9;

         for(AngelSoldier var18 : var2) {
            double var19 = var18.getX() - var4.x;
            double var21 = var18.getY() + 1.35 - var4.y;
            double var23 = var18.getZ() - var4.z;
            double var25 = Math.sqrt(var19 * var19 + var21 * var21 + var23 * var23);
            if (var25 >= 1.8 && var25 <= (double)22.0F) {
               double var27 = (var19 * var8 + var21 * var10 + var23 * var12) / var25;
               if (var27 > var15) {
                  var15 = var27;
                  var14 = var18;
               }
            }
         }

         if (var14 == null) {
            int var39 = var3.getInt("KoyluBakisSuresi");
            if (var39 > 0) {
               var3.putInt("KoyluBakisSuresi", Math.max(0, var39 - 2));
            }

         } else {
            UUID var38 = var14.getUUID();
            if (var3.hasUUID("SonBakilanKoylu") && !var3.getUUID("SonBakilanKoylu").equals(var38)) {
               var3.putInt("KoyluBakisSuresi", 0);
            }

            var3.putUUID("SonBakilanKoylu", var38);
            int var40 = var3.getInt("KoyluBakisSuresi") + 1;
            var3.putInt("KoyluBakisSuresi", var40);
            if (var40 == 30) {
               var0.playSound((Player)null, var1.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.NEUTRAL, 0.55F, 0.5F);
            }

            if (var40 >= 55) {
               List var20 = var0.getEntitiesOfClass(AngelSoldier.class, var14.getBoundingBox().inflate((double)8.0F), (var1x) -> var1x.getRol() == 64 && var38.equals(var1x.getEsUuid()));
               if (var20 == null || var20.isEmpty()) {
                  double var41 = var14.getX() - var1.getX();
                  double var42 = var14.getZ() - var1.getZ();
                  double var43 = Math.max(0.1, Math.sqrt(var41 * var41 + var42 * var42));
                  double var44 = var41 / var43;
                  double var29 = var42 / var43;
                  double var31 = var14.getX() + var44 * 1.28 - var29 * 0.24;
                  double var33 = var14.getZ() + var29 * 1.28 + var44 * 0.24;
                  double var35 = var14.getY();
                  AngelSoldier var37 = (AngelSoldier)((EntityType)CennetMod.MELEK.get()).create(var0);
                  if (var37 != null) {
                     var37.moveTo(var31, var35, var33, var14.getYRot(), 0.0F);
                     var37.setNpcRol(64, Mth.floor(var31), Mth.floor(var35), Mth.floor(var33), var1.getUUID());
                     var37.setPoz(62);
                     var37.setEsUuid(var38);
                     var0.addFreshEntity(var37);
                     var0.playSound((Player)null, BlockPos.containing(var31, var35 + (double)1.5F, var33), SoundEvents.WARDEN_HEARTBEAT, SoundSource.NEUTRAL, 1.35F, 0.48F);
                     var0.playSound((Player)null, BlockPos.containing(var31, var35 + (double)1.5F, var33), SoundEvents.ENDERMAN_STARE, SoundSource.NEUTRAL, 0.45F, 0.5F);
                     var0.sendParticles(var1, ParticleTypes.LARGE_SMOKE, true, var31, var35 + 1.2, var33, 18, 0.3, 0.7, 0.3, 0.01);
                  }
               }
            }

         }
      } else {
         var3.putInt("KoyluBakisSuresi", 0);
      }
   }

   private static void agaclardaGozleriCikar(ServerLevel var0, ServerPlayer var1, int var2, int var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      int var5 = 0;

      for(int var6 = -22; var6 <= 22 && var5 < 14; ++var6) {
         for(int var7 = -22; var7 <= 22 && var5 < 14; ++var7) {
            int var8 = var6 * var6 + var7 * var7;
            if (var8 >= 25 && var8 <= 484) {
               int var9 = var2 + var6;
               int var10 = var3 + var7;
               if (var0.getBlockState(var4.set(var9, 74, var10)).is((Block)CennetMod.KARANLIK_GOVDE.get()) && var0.getBlockState(var4.set(var9, 76, var10)).is((Block)CennetMod.KARANLIK_GOVDE.get())) {
                  AngelSoldier var11 = (AngelSoldier)((EntityType)CennetMod.MELEK.get()).create(var0);
                  if (var11 != null) {
                     var11.moveTo((double)var9 + (double)0.5F, (double)71.0F, (double)var10 + (double)0.5F, 0.0F, 0.0F);
                     var11.setYRot(0.0F);
                     var11.setYBodyRot(0.0F);
                     var11.setYHeadRot(0.0F);
                     var11.setNpcRol(61, var9, 71, var10, var1.getUUID());
                     var0.addFreshEntity(var11);
                     ++var5;
                  }
               }
            }
         }
      }

      if (var5 > 0) {
         var0.playSound((Player)null, var1.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.NEUTRAL, 0.75F, 0.6F);
      }

   }

   private static void yildizIsigiYanBiyomKontrol(ServerLevel var0, ServerPlayer var1) {
      double var2 = var1.getX();
      double var4 = var1.getY();
      double var6 = var1.getZ();
      if (var1.tickCount % 30 == 0) {
         BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();
         BlockState var9 = Blocks.AIR.defaultBlockState();

         for(int var10 = 0; var10 < NurBaskentFeature.KART_SUNAKLARI.length; ++var10) {
            int var11 = NurBaskentFeature.KART_SUNAKLARI[var10][0];
            int var12 = NurBaskentFeature.KART_SUNAKLARI[var10][1];
            double var13 = (var2 - (double)var11) * (var2 - (double)var11) + (var6 - (double)var12) * (var6 - (double)var12);
            if (var13 <= (double)3600.0F) {
               for(int var15 = var11 - 10; var15 <= var11 + 10; ++var15) {
                  for(int var16 = var12 - 10; var16 <= var12 + 10; ++var16) {
                     for(int var17 = 74; var17 <= 130; ++var17) {
                        var8.set(var15, var17, var16);
                        BlockState var18 = var0.getBlockState(var8);
                        if (var18.is((Block)CennetMod.KARANLIK_YAPRAK.get()) || var18.is((Block)CennetMod.KARANLIK_GOVDE.get())) {
                           var0.setBlock(var8, var9, 2);
                        }
                     }
                  }
               }
            }
         }
      }

      if (!batiKartiVar(var1)) {
         for(int var21 = 0; var21 < NurBaskentFeature.KART_SUNAKLARI.length; ++var21) {
            double var23 = var2 - (double)NurBaskentFeature.KART_SUNAKLARI[var21][0];
            double var25 = var6 - (double)NurBaskentFeature.KART_SUNAKLARI[var21][1];
            if (var23 * var23 + var25 * var25 <= (double)49.0F) {
               ItemStack var27 = new ItemStack((ItemLike)CennetMod.BATI_KARTI.get());
               if (!var1.getInventory().add(var27)) {
                  var1.drop(var27, false);
               }

               var0.playSound((Player)null, var1.blockPosition(), SoundEvents.BEACON_POWER_SELECT, SoundSource.NEUTRAL, 2.5F, 1.1F);
               var0.sendParticles(var1, ParticleTypes.END_ROD, true, var2, var4 + 1.2, var6, 45, 0.8, (double)1.0F, 0.8, 0.08);
               var1.sendSystemMessage(Component.literal("✦ Antik Yıldız Sunağı'nda ").withStyle(ChatFormatting.AQUA).append(Component.literal("[Batı Şehri Giriş Kartı]").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD})).append(Component.literal(" buldun! Batıdaki surlu şehre (-1690, 0) bu kartla girebilirsin.").withStyle(ChatFormatting.WHITE)));
               break;
            }
         }
      }

      double var22 = var2 - (double)-2000.0F;
      double var24 = var6 - (double)0.0F;
      double var26 = Math.sqrt(var22 * var22 + var24 * var24);
      if (var26 <= (double)420.0F) {
         List var14 = var0.getEntitiesOfClass(Mob.class, var1.getBoundingBox().inflate((double)72.0F), (var0x) -> !(var0x instanceof AngelSoldier) && var0x.isAlive());
         if (var14 != null) {
            for(Mob var30 : var14) {
               double var32 = var30.getX() - (double)-2000.0F;
               double var19 = var30.getZ() - (double)0.0F;
               if (var32 * var32 + var19 * var19 <= (double)145924.0F) {
                  var30.discard();
               }
            }
         }

         boolean var29 = var2 >= (double)-1701.0F && var2 <= (double)-1677.0F && Math.abs(var24) <= (double)8.5F;
         if (var29 && batiKartiVar(var1)) {
            batiKapisiCamlariniTemizle(var0, var1);
            var1.getPersistentData().putInt("BatiKapiAcik", 1);
            if (var1.getPersistentData().getInt("BatiGirisOnay") == 0) {
               batiKapisiSinematikAc(var0, var1);
            }
         } else if (!var29 && var1.getPersistentData().getInt("BatiKapiAcik") == 1) {
            batiKapisiCamlariniKapat(var0, var1);
            var1.getPersistentData().putInt("BatiKapiAcik", 0);
         }

         if (var26 < (double)304.0F) {
            boolean var31 = batiKartiVar(var1);
            if (!var31) {
               var1.teleportTo(var0, (double)-1674.5F, (double)101.0F, (double)0.5F, 90.0F, 0.0F);
               var0.playSound((Player)null, var1.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, 2.5F, 0.6F);
               var1.sendSystemMessage(Component.literal("⚠ Batı Şehri'nin surlarından yalnızca Yıldız Işığı Ormanı'nda bulunan 'Batı Şehri Giriş Kartı' ile geçebilirsin!").withStyle(ChatFormatting.YELLOW));
            } else if (var1.getPersistentData().getInt("BatiGirisOnay") == 0) {
               batiKapisiSinematikAc(var0, var1);
            }
         }
      }

      if (var2 >= (double)1500.0F && var2 <= (double)2720.0F && Math.abs(var6) <= (double)580.0F) {
         doguKalabalikCanavarKontrol(var0, var1, var2, var4, var6);
      }

   }

   public static void batiKapisiCamlariniTemizle(ServerLevel var0, ServerPlayer var1) {
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
      BlockState var3 = Blocks.AIR.defaultBlockState();

      for(int var4 = -1704; var4 <= -1662; ++var4) {
         for(int var5 = -9; var5 <= 9; ++var5) {
            for(int var6 = 98; var6 <= 120; ++var6) {
               var2.set(var4, var6, var5);
               BlockState var7 = var0.getBlockState(var2);
               if (var7.is(Blocks.LIGHT_BLUE_STAINED_GLASS) || var7.is(Blocks.WHITE_STAINED_GLASS)) {
                  var0.setBlock(var2, var3, 2);
               }
            }
         }
      }

   }

   public static void batiKapisiCamlariniKapat(ServerLevel var0, ServerPlayer var1) {
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
      BlockState var3 = Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState();
      BlockState var4 = Blocks.WHITE_STAINED_GLASS.defaultBlockState();
      byte var5 = 100;
      boolean var6 = false;

      for(int var7 = -1696; var7 <= -1674; ++var7) {
         if (var0.getBlockState(var2.set(var7, var5 + 14, 0)).is(Blocks.GOLD_BLOCK)) {
            var6 = true;

            for(int var8 = -4; var8 <= 4; ++var8) {
               int var9 = Math.abs(var8);
               int var10 = 10 + (int)Math.sqrt((double)Math.max(0, 16 - var9 * var9));

               for(int var11 = 1; var11 < var10; ++var11) {
                  var2.set(var7, var5 + var11, var8);
                  if (var0.getBlockState(var2).isAir()) {
                     var0.setBlock(var2, (var11 + var9 & 1) == 0 ? var3 : var4, 2);
                  }
               }
            }
         }
      }

      if (!var6) {
         for(int var12 = -1691; var12 <= -1689; ++var12) {
            for(int var13 = -4; var13 <= 4; ++var13) {
               int var14 = Math.abs(var13);
               int var15 = 10 + (int)Math.sqrt((double)Math.max(0, 16 - var14 * var14));

               for(int var16 = 1; var16 < var15; ++var16) {
                  var2.set(var12, var5 + var16, var13);
                  if (var0.getBlockState(var2).isAir()) {
                     var0.setBlock(var2, (var16 + var14 & 1) == 0 ? var3 : var4, 2);
                  }
               }
            }
         }
      }

      var0.playSound((Player)null, BlockPos.containing((double)-1690.0F, (double)104.0F, (double)0.5F), SoundEvents.PISTON_EXTEND, SoundSource.NEUTRAL, 2.2F, 0.75F);
      var1.displayClientMessage(Component.literal("✦ Batı Yıldız Şehri Kapısı arkandan mühürlenerek kapandı! ✦").withStyle(ChatFormatting.AQUA), true);
   }

   public static void batiKapisiSinematikAc(ServerLevel var0, ServerPlayer var1) {
      var1.getPersistentData().putInt("BatiGirisOnay", 1);
      var1.getPersistentData().putInt("BatiKapiAcik", 1);
      batiKapisiCamlariniTemizle(var0, var1);
      var0.sendParticles(var1, ParticleTypes.END_ROD, true, (double)-1688.0F, (double)105.0F, (double)0.5F, 45, (double)2.5F, (double)3.0F, (double)2.5F, 0.08);
      sinematikBaslat(var0, var1, "✦ BATI YILDIZ ŞEHRİ ✦", "Surların ardındaki ebedi sığınak seni selamlıyor", 4);
   }

   private static void doguKalabalikCanavarKontrol(ServerLevel var0, ServerPlayer var1, double var2, double var4, double var6) {
      CompoundTag var8 = var1.getPersistentData();
      int var9 = var8.getInt("DoguSonDalga");
      List var10 = var0.getEntitiesOfClass(Mob.class, var1.getBoundingBox().inflate((double)56.0F), (var0x) -> !(var0x instanceof AngelSoldier) && var0x.isAlive());
      int var11 = var10 == null ? 0 : var10.size();
      if (var1.tickCount - var9 > 80 && var1.tickCount - var9 < 440 && var11 > 0 && var11 <= 3) {
         for(Mob var31 : var10) {
            var31.discard();
         }

      } else {
         if (var1.tickCount - var9 >= 460 || var9 == 0) {
            var8.putInt("DoguSonDalga", var1.tickCount);
            int var12 = var1.tickCount ^ Mth.floor(var2 * (double)19.0F + var6 * (double)31.0F);
            double var13 = (double)(var12 & 255) / (double)256.0F * Math.PI * (double)2.0F;
            double var15 = var2 + Math.cos(var13) * (double)20.0F;
            double var17 = var6 + Math.sin(var13) * (double)20.0F;
            var0.playSound((Player)null, BlockPos.containing(var15, var4, var17), SoundEvents.WARDEN_HEARTBEAT, SoundSource.NEUTRAL, 3.0F, 0.7F);
            var0.playSound((Player)null, BlockPos.containing(var15, var4, var17), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.NEUTRAL, 2.0F, 0.8F);
            var1.displayClientMessage(Component.literal("⚔ Doğu Dağları'nda kalabalık bir canavar sürüsü belirdi! Dağ Savaşçıları hücuma geçiyor! ⚔").withStyle(ChatFormatting.YELLOW), true);
            byte var19 = 13;

            for(int var20 = 0; var20 < var19; ++var20) {
               double var21 = (Math.PI * 2D) * (double)var20 / (double)var19;
               double var23 = (double)2.0F + (double)(var20 % 4) * (double)1.5F;
               int var25 = Mth.floor(var15 + Math.cos(var21) * var23);
               int var26 = Mth.floor(var17 + Math.sin(var21) * var23);
               BlockPos var27 = var0.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing((double)var25, var4, (double)var26));
               int var28 = var27.getY();
               if (var28 < 60 || var28 > 225) {
                  var28 = Mth.floor(var4);
               }

               Mob var29;
               if (var20 % 3 == 0) {
                  var29 = (Mob)EntityType.SKELETON.create(var0);
               } else if (var20 % 4 == 0) {
                  var29 = (Mob)EntityType.SPIDER.create(var0);
               } else {
                  var29 = (Mob)EntityType.ZOMBIE.create(var0);
               }

               if (var29 != null) {
                  var29.moveTo((double)var25 + (double)0.5F, (double)var28, (double)var26 + (double)0.5F, 0.0F, 0.0F);
                  var0.addFreshEntity(var29);
                  var0.sendParticles(var1, ParticleTypes.REVERSE_PORTAL, true, (double)var25 + (double)0.5F, (double)var28 + (double)1.0F, (double)var26 + (double)0.5F, 8, 0.3, (double)0.5F, 0.3, 0.05);
               }
            }

            List var32 = var0.getEntitiesOfClass(AngelSoldier.class, var1.getBoundingBox().inflate((double)65.0F), (var0x) -> var0x.getRol() == 40);
            int var33 = var32 == null ? 0 : var32.size();

            for(int var22 = var33; var22 < 5; ++var22) {
               double var34 = (Math.PI * 2D) * (double)var22 / (double)5.0F;
               int var35 = Mth.floor(var15 + Math.cos(var34) * (double)8.0F);
               int var36 = Mth.floor(var17 + Math.sin(var34) * (double)8.0F);
               BlockPos var37 = var0.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing((double)var35, var4, (double)var36));
               int var38 = var37.getY();
               if (var38 < 60 || var38 > 225) {
                  var38 = Mth.floor(var4);
               }

               AngelSoldier var39 = (AngelSoldier)((EntityType)CennetMod.MELEK.get()).create(var0);
               if (var39 != null) {
                  var39.moveTo((double)var35 + (double)0.5F, (double)var38, (double)var36 + (double)0.5F, 0.0F, 0.0F);
                  var39.setNpcRol(40, var35, var38, var36, var1.getUUID());
                  var0.addFreshEntity(var39);
               }
            }
         }

      }
   }

   private static void bolgeSinematikKontrol(ServerLevel var0, ServerPlayer var1, boolean var2) {
      double var3 = var1.getX();
      double var5 = var1.getZ();
      double var7 = Math.sqrt(var3 * var3 + var5 * var5);
      byte var9 = 0;
      if (var2) {
         if (var7 <= (double)430.0F) {
            var9 = 1;
         } else if (var3 >= (double)750.0F && var5 <= (double)650.0F && var7 >= (double)1500.0F) {
            var9 = 2;
         } else if (var3 <= (double)-750.0F && var5 <= (double)650.0F && var7 >= (double)1500.0F) {
            var9 = 3;
         }
      } else {
         double var10 = Math.sqrt((var3 - (double)-2000.0F) * (var3 - (double)-2000.0F) + var5 * var5);
         if (var10 <= (double)372.0F && batiKartiVar(var1)) {
            var9 = 4;
         } else if (var3 >= (double)1500.0F && Math.abs(var5) <= (double)580.0F) {
            var9 = 5;
         }
      }

      CompoundTag var12 = var1.getPersistentData();
      int var11 = var12.getInt("AktifSinematikBolge");
      if (var9 != 0 && var9 != var11) {
         var12.putInt("AktifSinematikBolge", var9);
         if (var9 == 1) {
            sinematikBaslat(var0, var1, "✦ CENNETİN BAŞKENTİ ✦", "Ebedi Nurun ve İlahi Sarayın Kalbi", 1);
         } else if (var9 == 2) {
            sinematikBaslat(var0, var1, "\ud83c\udf3f KADİM ELF KRALLIĞI \ud83c\udf3f", "Sylvanor ve Eldamar Ormanlarına Hoş Geldin", 2);
         } else if (var9 == 3) {
            sinematikBaslat(var0, var1, "\ud83c\udff0 GÖKSEL BULUT ŞATOLARI \ud83c\udff0", "Bulutların Üzerinde Süzülen 56 Efsanevi Kale", 3);
         } else if (var9 == 4) {
            sinematikBaslat(var0, var1, "✦ BATI YILDIZ ŞEHRİ ✦", "Surların Ardındaki İkinci Büyük Başkent", 4);
         } else if (var9 == 5) {
            sinematikBaslat(var0, var1, "⚔ KOCA DAĞLAR — DOĞU DİYARI ⚔", "Kadim Dağ Savaşçılarının ve Sürü Baskınlarının Toprakları", 5);
         }
      } else if (var9 == 0) {
         var12.putInt("AktifSinematikBolge", 0);
      }

   }

   public static void sinematikBaslat(ServerLevel var0, ServerPlayer var1, String var2, String var3, int var4) {
      CompoundTag var5 = var1.getPersistentData();
      var5.putInt("SinematikSure", 60);
      var5.putInt("SinematikTip", var4);
      var1.connection.send(new ClientboundSetTitlesAnimationPacket(15, 75, 25));
      var1.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(var3).withStyle(var4 == 6 ? ChatFormatting.GRAY : ChatFormatting.YELLOW)));
      ChatFormatting var6 = var4 == 6 ? ChatFormatting.GRAY : (var4 == 2 ? ChatFormatting.GREEN : (var4 != 4 && var4 != 3 ? ChatFormatting.GOLD : ChatFormatting.AQUA));
      var1.connection.send(new ClientboundSetTitleTextPacket(Component.literal(var2).withStyle(new ChatFormatting[]{var6, ChatFormatting.BOLD})));
      var1.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 35, 2, false, false, false));
      var1.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false, false));
      var0.playSound((Player)null, var1.blockPosition(), SoundEvents.BELL_RESONATE, SoundSource.NEUTRAL, 3.5F, var4 == 6 ? 0.5F : 0.75F);
      var0.playSound((Player)null, var1.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 3.0F, var4 == 6 ? 0.55F : 0.9F);
      var0.sendParticles(var1, var4 == 6 ? ParticleTypes.DRAGON_BREATH : ParticleTypes.FLASH, true, var1.getX(), var1.getY() + (double)1.5F, var1.getZ(), var4 == 6 ? 25 : 2, 0.4, 0.4, 0.4, 0.02);
   }

   private static void sinematikAnimasyonTick(ServerLevel var0, ServerPlayer var1) {
      CompoundTag var2 = var1.getPersistentData();
      int var3 = var2.getInt("SinematikSure");
      if (var3 > 0) {
         var2.putInt("SinematikSure", var3 - 1);
         int var4 = 60 - var3;
         double var5 = var1.getX();
         double var7 = var1.getY();
         double var9 = var1.getZ();

         for(int var11 = 0; var11 < 2; ++var11) {
            double var12 = (double)var4 * 0.28 + (double)var11 * Math.PI;
            double var14 = 2.8 - (double)var4 / (double)60.0F * 1.4;
            double var16 = var5 + Math.cos(var12) * var14;
            double var18 = var9 + Math.sin(var12) * var14;
            double var20 = var7 + 0.2 + (double)var4 / (double)60.0F * 3.2;
            var0.sendParticles(var1, ParticleTypes.END_ROD, true, var16, var20, var18, 2, 0.05, 0.05, 0.05, 0.01);
            var0.sendParticles(var1, ParticleTypes.ENCHANT, true, var16, var20 + 0.3, var18, 3, 0.15, 0.15, 0.15, 0.2);
         }

         if (var4 % 12 == 0) {
            float var22 = 0.75F + (float)var4 / 60.0F * 0.65F;
            var0.playSound((Player)null, var1.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL, 2.5F, var22);
         }

         if (var3 == 1) {
            var0.playSound((Player)null, var1.blockPosition(), SoundEvents.BEACON_POWER_SELECT, SoundSource.NEUTRAL, 2.5F, 1.0F);
         }

      }
   }

   public static void kapidanGecisKontrol(ServerPlayer var0) {
      if (!NurKapi.KAPILAR.isEmpty() && kartVar(var0)) {
         for(NurKapi.Kapi var2 : NurKapi.KAPILAR.values()) {
            if (var2.sl == var0.level()) {
               double var3 = var0.getX() - ((double)var2.kx + (double)0.5F);
               double var5 = var0.getZ() - (double)var2.kz;
               if (Math.abs(var3) <= (double)6.5F && var5 >= (double)-6.0F && var5 <= (double)2.5F && var0.getY() >= (double)(var2.ky - 1) && var0.getY() <= (double)(var2.ky + 27)) {
                  baskenteGec(var0, var2);
                  return;
               }
            }
         }

      }
   }

   public static void dogrudanBaskenteGit(ServerPlayer var0) {
      gec(var0, KRALLIK, (double)0.5F, (double)101.0F, (double)274.5F, 180.0F, false);
      if (var0.level() instanceof ServerLevel) {
         ServerLevel var1 = (ServerLevel)var0.level();
         sinematikBaslat(var1, var0, "✦ CENNETİN BAŞKENTİ ✦", "Ebedi Nurun ve İlahi Sarayın Kalbi", 1);
         AngelSoldier.bolgeNpcKontrol(var1, var0);
      }

   }

   private static void baskenteGec(ServerPlayer var0, NurKapi.Kapi var1) {
      ServerLevel var2 = (ServerLevel)var0.level();
      var0.getPersistentData().putIntArray("NurGeri", new int[]{var1.kx, var1.ky, var1.kz});
      var2.playSound((Player)null, BlockPos.containing((double)var1.kx + (double)0.5F, (double)(var1.ky + 3), (double)(var1.kz + 2)), SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, 3.0F, 0.8F);
      gec(var0, KRALLIK, (double)0.5F, (double)101.0F, (double)274.5F, 180.0F, false);
      ServerLevel var3 = (ServerLevel)var0.level();
      var3.playSound((Player)null, var0.blockPosition(), SoundEvents.BELL_RESONATE, SoundSource.NEUTRAL, 3.0F, 0.7F);
      NurKapi.temizle(var1.sahip);
      sinematikBaslat(var3, var0, "✦ CENNETİN BAŞKENTİ ✦", "Ebedi Nurun ve İlahi Sarayın Kalbi", 1);
      AngelSoldier.bolgeNpcKontrol(var3, var0);
   }

   private static void geriCagir(ServerPlayer var0) {
      int[] var1 = var0.getPersistentData().getIntArray("NurGeri");
      if (var1.length == 3) {
         gec(var0, YarikAsasi.CENNET, (double)var1[0] + (double)0.5F, (double)var1[1], (double)var1[2] + (double)10.5F, var0.getYRot(), false);
      } else {
         gec(var0, YarikAsasi.CENNET, (double)0.5F, (double)200.0F, (double)-1500.0F, var0.getYRot(), true);
      }

      var0.displayClientMessage(Component.literal("Perde seni geri çağırdı."), true);
   }

   private static void gec(ServerPlayer var0, ResourceKey<Level> var1, double var2, double var4, double var6, float var8, boolean var9) {
      ServerLevel var10 = var0.server.getLevel(var1);
      if (var10 != null) {
         if (var0.isPassenger()) {
            var0.stopRiding();
         }

         BlockPos var11 = BlockPos.containing(var2, var4, var6);
         var10.getChunk(var11.getX() >> 4, var11.getZ() >> 4);
         if (var9 || !var10.getBlockState(var11).getCollisionShape(var10, var11).isEmpty() || !var10.getBlockState(var11.above()).getCollisionShape(var10, var11.above()).isEmpty()) {
            var11 = var10.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, var11).above();
            var4 = (double)var11.getY();
         }

         var0.fallDistance = 0.0F;
         var0.teleportTo(var10, var2, var4, var6, var8, 0.0F);
      }
   }

   private static void ozelEnvanterSlotiKontrol(ServerPlayer var0) {
      if (var0.tickCount % 25 == 0) {
         Inventory var1 = var0.getInventory();
         int[] var2 = new int[]{9, 10, 11};
         Item[] var3 = new Item[]{(Item)CennetMod.NUR_KARTI.get(), (Item)CennetMod.BATI_KARTI.get(), (Item)CennetMod.YILDIZ_KRISTALI.get()};

         for(int var4 = 0; var4 < 3; ++var4) {
            int var5 = var2[var4];
            Item var6 = var3[var4];
            ItemStack var7 = var1.getItem(var5);
            if (var7.isEmpty()) {
               for(int var8 = 12; var8 < 36; ++var8) {
                  ItemStack var9 = var1.getItem(var8);
                  if (!var9.isEmpty() && var9.is(var6)) {
                     var1.setItem(var5, var9.copy());
                     var1.setItem(var8, ItemStack.EMPTY);
                     break;
                  }
               }
            }
         }

         boolean var10 = false;

         for(int var14 : new int[]{9, 10, 11, 40}) {
            ItemStack var15 = var1.getItem(var14);
            if (!var15.isEmpty() && var15.is((Item)CennetMod.YILDIZ_KRISTALI.get())) {
               var10 = true;
               break;
            }
         }

         if (var10 && var0.tickCount % 100 == 0 && !var0.level().dimension().equals(ZIFIRI_BATAKLIK)) {
            var0.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, true, false, true));
         }

      }
   }

   private static void yarikVarisSinematikTick(ServerLevel var0, ServerPlayer var1) {
      CompoundTag var2 = var1.getPersistentData();
      int var3 = var2.getInt("YarikVarisSure");
      if (var3 > 0) {
         var2.putInt("YarikVarisSure", var3 - 1);
         int var4 = 90 - var3;
         double var5 = var1.getX();
         double var7 = var1.getY();
         double var9 = var1.getZ();
         double var11 = 1.2 + (double)var4 / (double)90.0F * (double)7.5F;

         for(int var13 = 0; var13 < 6; ++var13) {
            double var14 = (double)var4 * 0.26 + (double)var13 * (Math.PI / 3D);
            double var16 = var5 + Math.cos(var14) * var11;
            double var18 = var9 + Math.sin(var14) * var11;
            var0.sendParticles(var1, ParticleTypes.END_ROD, true, var16, var7 + 0.3, var18, 1, 0.05, 0.1, 0.05, 0.01);
            if (var13 % 2 == 0) {
               double var20 = var5 + Math.cos(-var14 * 1.4) * (3.2 - (double)var4 / (double)90.0F * (double)2.0F);
               double var22 = var9 + Math.sin(-var14 * 1.4) * (3.2 - (double)var4 / (double)90.0F * (double)2.0F);
               double var24 = var7 + (double)4.5F - (double)var4 / (double)90.0F * 4.2;
               var0.sendParticles(var1, ParticleTypes.REVERSE_PORTAL, true, var20, var24, var22, 2, 0.1, 0.1, 0.1, 0.02);
            }
         }

         if (var4 % 15 == 0) {
            var0.playSound((Player)null, var1.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 2.2F, 0.7F + (float)var4 / 90.0F * 0.7F);
         }

      }
   }

   private static void pegasusBiletiKontrol(ServerPlayer var0) {
      if (var0.tickCount % 40 == 0) {
         if (var0.level().dimension().equals(YarikAsasi.CENNET)) {
            CompoundTag var1 = var0.getPersistentData();
            if (var1.getInt("PegasusBiletiVerildi") == 0) {
               var1.putInt("PegasusBiletiVerildi", 1);
               Inventory var2 = var0.getInventory();
               boolean var3 = false;

               for(int var4 = 0; var4 < var2.getContainerSize(); ++var4) {
                  ItemStack var5 = var2.getItem(var4);
                  if (var5 != null && !var5.isEmpty() && var5.is((Item)CennetMod.PEGASUS_BILETI.get())) {
                     var3 = true;
                     break;
                  }
               }

               if (!var3) {
                  var2.add(new ItemStack((ItemLike)CennetMod.PEGASUS_BILETI.get()));
                  var0.sendSystemMessage(Component.literal("✦ Envanterine [Pegasus Arabası Çağırma Bileti] eklendi! Cennet Karanlığı'nda sağ tık basılı tutarak 4 kişilik Soylu Pegasus Arabası'nı çağırabilirsin. ✦").withStyle(ChatFormatting.AQUA));
               }
            }

         }
      }
   }

   @SubscribeEvent
   public static void sohbet(ServerChatEvent var0) {
      try {
         ServerPlayer var1 = null;
         String var2 = null;

         for(Method var6 : var0.getClass().getMethods()) {
            if (var6.getParameterCount() == 0) {
               if (ServerPlayer.class.isAssignableFrom(var6.getReturnType())) {
                  var1 = (ServerPlayer)var6.invoke(var0);
               } else if (var6.getName().equals("getRawText") && var6.getReturnType() == String.class) {
                  var2 = (String)var6.invoke(var0);
               } else if (var6.getName().equals("getMessage") && Component.class.isAssignableFrom(var6.getReturnType())) {
                  Component var7 = (Component)var6.invoke(var0);
                  if (var7 != null && var2 == null) {
                     var2 = var7.getString();
                  }
               }
            }
         }

         if (var1 == null || var2 == null) {
            return;
         }

         String var24 = var2.trim().replace('İ', 'i').replace('I', 'i').replace('ı', 'i').replace('Ş', 's').replace('ş', 's').replace('Ğ', 'g').replace('ğ', 'g').replace('Ü', 'u').replace('ü', 'u').replace('Ö', 'o').replace('ö', 'o').replace('Ç', 'c').replace('ç', 'c').toLowerCase(Locale.ROOT);
         if (!var24.startsWith("pegasus") && !var24.startsWith("pegagus") && !var24.startsWith("pega ") && !var24.startsWith("pega,") && !var24.startsWith("pega.")) {
            return;
         }

         for(Method var31 : var0.getClass().getMethods()) {
            if (var31.getName().equals("setCanceled") && var31.getParameterCount() == 1 && var31.getParameterTypes()[0] == Boolean.TYPE) {
               var31.invoke(var0, true);
               break;
            }
         }

         if (!(var1.level() instanceof ServerLevel)) {
            return;
         }

         ServerLevel var26 = (ServerLevel)var1.level();
         if (!var26.dimension().equals(YarikAsasi.CENNET)) {
            var1.sendSystemMessage(Component.literal("✦ Pegasus Soylu Arabası yalnızca ").withStyle(ChatFormatting.RED).append(Component.literal("Cennet Karanlığı (Yıldız Işığı Ormanı)").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD})).append(Component.literal(" boyutunda çalışır! ✦").withStyle(ChatFormatting.RED)));
            return;
         }

         ArrayList var28 = new ArrayList();
         Matcher var30 = Pattern.compile("-?\\d+(\\.\\d+)?").matcher(var24);

         while(var30.find()) {
            try {
               var28.add(Double.parseDouble(var30.group()));
            } catch (NumberFormatException var22) {
            }
         }

         double var32 = var1.getX();
         double var9 = (double)-1.0F;
         double var11 = var1.getZ();
         String var13 = "";
         if (var28.size() >= 3) {
            var32 = (Double)var28.get(0);
            var9 = Mth.clamp((Double)var28.get(1), (double)45.0F, (double)260.0F);
            var11 = (Double)var28.get(2);
            var13 = String.format(Locale.ROOT, "(%.0f, %.0f, %.0f) Koordinatı", var32, var9, var11);
         } else if (var28.size() == 2) {
            var32 = (Double)var28.get(0);
            var9 = (double)-1.0F;
            var11 = (Double)var28.get(1);
            var13 = String.format(Locale.ROOT, "(%.0f, %.0f) Koordinatı", var32, var11);
         } else if (!var24.contains("guney") && !var24.contains("batak") && !var24.contains("zifiri") && !var24.contains("camur") && !var24.contains("siyah")) {
            if (!var24.contains("bati") && !var24.contains("sehir") && !var24.contains("surlu")) {
               if (!var24.contains("dogu") && !var24.contains("dag") && !var24.contains("koy") && !var24.contains("vadi")) {
                  if (!var24.contains("baskent") && !var24.contains("nur") && !var24.contains("kapi") && !var24.contains("kuzey") && !var24.contains("saray") && !var24.contains("cennet")) {
                     if (!var24.contains("sunak") && !var24.contains("kart") && !var24.contains("antik")) {
                        if (!var24.contains("merkez") && !var24.contains("orman") && !var24.contains("yildiz") && !var24.contains("baslangic")) {
                           var1.sendSystemMessage(Component.literal("✦ Pegasus Soylu Arabası Talimat Rehberi (Cennet Karanlığı): ").withStyle(new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.BOLD}).append(Component.literal("Sohbete şu şekilde bir cümle yazabilirsin:\n").withStyle(ChatFormatting.WHITE)).append(Component.literal("• 'pegasus, beni güney bölgesine götür' (Zifiri Bataklık Boyutu)\n").withStyle(ChatFormatting.AQUA)).append(Component.literal("• 'pegasus, beni batı şehrine götür'\n").withStyle(ChatFormatting.GOLD)).append(Component.literal("• 'pegasus, beni doğu köyüne götür'\n").withStyle(ChatFormatting.GOLD)).append(Component.literal("• 'pegasus, beni nur kapısına götür' (veya başkente götür)\n").withStyle(ChatFormatting.GOLD)).append(Component.literal("• 'pegasus, beni yıldız sunağına götür'\n").withStyle(ChatFormatting.GOLD)).append(Component.literal("• 'pegasus, beni 0 120 -4150 koordinatına götür'").withStyle(ChatFormatting.AQUA)));
                           return;
                        }

                        var32 = (double)0.5F;
                        var9 = (double)-1.0F;
                        var11 = (double)0.5F;
                        var13 = "Yıldız Işığı Ormanı Merkezi";
                     } else {
                        var32 = (double)-36.5F;
                        var9 = (double)-1.0F;
                        var11 = (double)0.5F;
                        var13 = "Antik Yıldız Sunağı";
                     }
                  } else {
                     var32 = (double)0.5F;
                     var9 = (double)-1.0F;
                     var11 = (double)-1495.5F;
                     var13 = "Cennet Başkenti Nur Kapısı (-1500)";
                  }
               } else {
                  var32 = (double)1620.5F;
                  var9 = (double)79.0F;
                  var11 = (double)0.5F;
                  var13 = "Doğu Dağ Köyü";
               }
            } else {
               var32 = (double)-1675.5F;
               var9 = (double)101.0F;
               var11 = (double)0.5F;
               var13 = "Batı Yıldız Şehri";
            }
         } else {
            var32 = (double)0.5F;
            var9 = (double)72.0F;
            var11 = (double)1500.0F;
            var13 = "Güney — Zifiri Bataklık Boyutu";
         }

         AngelSoldier var14 = null;
         if (var1.getVehicle() instanceof AngelSoldier) {
            AngelSoldier var15 = (AngelSoldier)var1.getVehicle();
            if (var15.getRol() == 50 || var15.getPoz() >= 50 && var15.getPoz() < 60) {
               var14 = var15;
            }
         }

         if (var14 == null) {
            List var37 = var26.getEntitiesOfClass(AngelSoldier.class, var1.getBoundingBox().inflate((double)200.0F), (var0x) -> var0x.getRol() == 50 || var0x.getPoz() >= 50 && var0x.getPoz() < 60);
            if (var37 != null && !var37.isEmpty()) {
               double var16 = Double.MAX_VALUE;

               for(AngelSoldier var19 : var37) {
                  double var20 = var1.distanceToSqr(var19.getX(), var19.getY(), var19.getZ());
                  if (var20 < var16) {
                     var16 = var20;
                     var14 = var19;
                  }
               }
            }
         }

         if (var14 == null) {
            var14 = AngelSoldier.pegasusCagir(var26, var1);
            if (var14 != null) {
               var14.moveTo(var1.getX(), var1.getY(), var1.getZ(), var1.getYRot(), 0.0F);
            }
         }

         if (var14 != null) {
            var14.pegasusHedefAta(var26, var1, var32, var9, var11, var13);
         }
      } catch (Throwable var23) {
      }

   }

   static {
      KRALLIK = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("cennetmod", "nur_krallik"));
      ZIFIRI_BATAKLIK = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("cennetmod", "zifiri_bataklik"));
   }
}
