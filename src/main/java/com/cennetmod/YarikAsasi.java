package com.cennetmod;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public class YarikAsasi extends Item {
   public static final ResourceKey<Level> CENNET;
   public static final int SURE = 260;
   public static final int YILDIZ = 220;
   public static final double MERKEZ_YUKSEKLIK = (double)38.0F;

   public YarikAsasi(Item.Properties properties) {
      super(properties);
   }

   public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
      return 260;
   }

   public UseAnim getUseAnimation(ItemStack itemStack) {
      return UseAnim.BOW;
   }

   private boolean geceYarisi(Level level) {
      long l = level.getDayTime() % 24000L;
      if (l < 0L) {
         l += 24000L;
      }

      return l >= 17500L && l <= 18500L;
   }

   private static void baslikGonder(ServerPlayer serverPlayer, String string, String string2, ChatFormatting chatFormatting, int n, int n2, int n3) {
      {}
      serverPlayer.sendSystemMessage(Component.literal(string2).withStyle(ChatFormatting.YELLOW));
      serverPlayer.sendSystemMessage(Component.literal(string).withStyle(new ChatFormatting[]{chatFormatting, ChatFormatting.BOLD}));
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack itemStack = player.getItemInHand(interactionHand);
      if (player.getCooldowns().isOnCooldown(this)) {
         return InteractionResultHolder.fail(itemStack);
      } else {
         boolean bl = NurSistemi.cennetBoyutu(level);
         if (!bl && !this.geceYarisi(level)) {
            if (!level.isClientSide) {
               player.sendSystemMessage(Component.literal("Yıldızlar sessiz... (Gece yarısı 17500-18500 vakitlerinde tekrar dene)").withStyle(ChatFormatting.GRAY));
               level.playSound((Player)null, player.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            return InteractionResultHolder.fail(itemStack);
         } else {
            if (!level.isClientSide) {
               player.getPersistentData().putBoolean("yarik_cennet_gidis", !bl);
            }

            player.startUsingItem(interactionHand);
            if (!level.isClientSide) {
               CompoundTag compoundTag = player.getPersistentData();
               compoundTag.putDouble("yarik_x", player.getX());
               compoundTag.putDouble("yarik_y", player.getY());
               compoundTag.putDouble("yarik_z", player.getZ());
               compoundTag.putLong("yarik_seed", level.random.nextLong());
               if (player instanceof ServerPlayer) {
                  ServerPlayer serverPlayer = (ServerPlayer)player;
                  baslikGonder(serverPlayer, "✦ YARIK ASASI UYANIYOR ✦", "Kadim yıldızlara ve boyutlar ötesine seslendin...", ChatFormatting.AQUA, 10, 50, 15);
               }

               ses(level, player, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.8F, 0.6F);
               ses(level, player, SoundEvents.BEACON_ACTIVATE, 1.4F, 0.5F);
            }

            return InteractionResultHolder.consume(itemStack);
         }
      }
   }

   private static void ses(Level level, Entity entity, SoundEvent soundEvent, float f, float f2) {
      level.playSound((Player)null, entity.blockPosition(), soundEvent, SoundSource.PLAYERS, f, f2);
   }

   private static void p(ServerLevel serverLevel, ServerPlayer serverPlayer, ParticleOptions particleOptions, double d, double d2, double d3, int n, double d4, double d5, double d6, double d7) {
      serverLevel.sendParticles(serverPlayer, particleOptions, true, d, d2, d3, n, d4, d5, d6, d7);
   }

   private static double[][] yildizlar(long l, double d, double d2, double d3) {
      Random random = new Random(l);
      double[][] dArray = new double[220][3];

      for(int i = 0; i < 220; ++i) {
         double d4 = random.nextDouble() * Math.PI * (double)2.0F;
         double d5 = Math.toRadians((double)25.0F + random.nextDouble() * (double)60.0F);
         double d6 = (double)22.0F + random.nextDouble() * (double)14.0F;
         dArray[i][0] = d + Math.cos(d4) * Math.cos(d5) * d6;
         dArray[i][1] = d2 + 1.6 + Math.sin(d5) * d6;
         dArray[i][2] = d3 + Math.sin(d4) * Math.cos(d5) * d6;
      }

      return dArray;
   }

   private static List<List<double[]>> catlak(double d, double d2, double d3, long l) {
      Random random = new Random(l ^ 25214903917L);
      ArrayList<List<double[]>> arrayList = new ArrayList();
      double d4 = random.nextDouble() * Math.PI;
      double d5 = Math.cos(d4);
      double d6 = Math.sin(d4);
      double d7 = -d6;
      double d8 = d5;

      for(int i = -1; i <= 1; i += 2) {
         ArrayList<double[]> arrayList2 = new ArrayList();
         arrayList2.add(new double[]{d, d2, d3, (double)0.0F});
         double d9 = (double)0.0F;
         double d10 = (double)0.0F;
         double d11 = (double)0.0F;

         for(int n = 1; n <= 14; ++n) {
            arrayList2.add(new double[]{d + d5 * (double)i * (d9 += 1.1 + random.nextDouble() * 0.9) + d7 * (d10 += (random.nextDouble() - (double)0.5F) * 1.8), d2 + (d11 += (random.nextDouble() - (double)0.5F) * 1.8), d3 + d6 * (double)i * d9 + d8 * d10, (double)n / (double)14.0F});
         }

         arrayList.add(arrayList2);

         for(int var40 = 3; var40 <= 11; var40 += 4) {
            double[] dArray = (double[])arrayList2.get(var40);
            ArrayList<double[]> arrayList3 = new ArrayList();
            arrayList3.add(dArray);
            double d12 = dArray[0];
            double d13 = dArray[1];
            double d14 = dArray[2];
            double d15 = random.nextBoolean() ? (double)1.0F : (double)-1.0F;

            for(int j = 1; j <= 5; ++j) {
               arrayList3.add(new double[]{d12 += d7 * d15 * (0.9 + random.nextDouble() * 0.6) + d5 * (double)i * 0.4, d13 += (random.nextDouble() - (double)0.5F) * (double)1.5F, d14 += d8 * d15 * (0.9 + random.nextDouble() * 0.6) + d6 * (double)i * 0.4, dArray[3] + (double)j * 0.03});
            }

            arrayList.add(arrayList3);
         }
      }

      return arrayList;
   }

   private void cennetSinematigi(ServerLevel level, ServerPlayer player, int elapsed, long seed) {
      double x = player.getX();
      double y = player.getY();
      double z = player.getZ();
      double progress = Math.min((double)1.0F, (double)elapsed / (double)260.0F);
      double ring = Math.min(5.8, 1.2 + progress * 4.6);
      double spin = (double)elapsed * 0.16;

      for(int i = 0; i < 18; ++i) {
         double a = spin + (double)i * 0.3490658503988659;
         double px = x + Math.cos(a) * ring;
         double pz = z + Math.sin(a) * ring;
         p(level, player, ParticleTypes.END_ROD, px, y + 0.12, pz, 1, 0.025, 0.02, 0.025, (double)0.0F);
         if ((i + elapsed) % 3 == 0) {
            p(level, player, ParticleTypes.REVERSE_PORTAL, px, y + 0.24, pz, 1, 0.08, 0.05, 0.08, 0.02);
         }
      }

      int stars = 5 + Math.min(14, elapsed / 18);

      for(int i = 0; i < stars; ++i) {
         double a = spin * 0.7 + (double)i * 2.399963;
         double r = 0.55 + (double)(i % 5) * 0.22;
         double py = y + 0.3 + ((double)elapsed * 0.055 + (double)i * 0.17) % 2.7;
         p(level, player, ParticleTypes.END_ROD, x + Math.cos(a) * r, py, z + Math.sin(a) * r, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
      }

      if (elapsed >= 45 && elapsed % 4 == 0) {
         p(level, player, ParticleTypes.ENCHANT, x, y + (double)1.0F, z, 7, 0.55, 0.85, 0.55, 0.45);
      }

      if (elapsed >= 70 && elapsed < 215) {
         float yaw = player.getYRot() * ((float)Math.PI / 180F);
         double fx = (double)(-Mth.sin(yaw));
         double fz = (double)Mth.cos(yaw);
         double cx = x + fx * 3.2;
         double cz = z + fz * 3.2;
         double width = 0.7 + Math.min(2.6, (double)(elapsed - 70) * 0.025);

         for(int i = 0; i < 10; ++i) {
            double py = y + 0.4 + (double)i / (double)9.0F * 4.1;
            double side = (double)((i + elapsed) % 2 == 0 ? -1 : 1) * width;
            p(level, player, ParticleTypes.END_ROD, cx + -fz * side, py, cz + fx * side, 1, 0.04, 0.05, 0.04, 0.01);
            if (i % 2 == 0) {
               p(level, player, ParticleTypes.REVERSE_PORTAL, cx, py, cz, 2, 0.1, 0.1, 0.1, 0.04);
            }
         }
      }

      if (elapsed == 2) {
         ses(level, player, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.5F, 0.65F);
      }

      if (elapsed == 55) {
         baslikGonder(player, "✦ YILDIZLAR UYANIYOR ✦", "Kadim ormanın yolu açılıyor...", ChatFormatting.AQUA, 8, 55, 15);
         ses(level, player, SoundEvents.BEACON_ACTIVATE, 1.5F, 0.65F);
      }

      if (elapsed == 120) {
         baslikGonder(player, "✧ GERÇEKLİK YARILIYOR ✧", "Yıldız ışığı seni çağırıyor", ChatFormatting.LIGHT_PURPLE, 8, 65, 15);
         ses(level, player, SoundEvents.WARDEN_HEARTBEAT, 1.8F, 0.75F);
      }

      if (elapsed == 205) {
         ses(level, player, SoundEvents.END_PORTAL_SPAWN, 1.8F, 0.75F);
         p(level, player, ParticleTypes.FLASH, x, y + 1.4, z, 3, 0.35, 0.35, 0.35, (double)0.0F);
      }

      if (elapsed >= 225) {
         p(level, player, ParticleTypes.DRAGON_BREATH, x, y + (double)1.0F, z, 7, (double)0.75F, 1.3, (double)0.75F, 0.02);
      }

   }

   public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int n) {
      if (!level.isClientSide && livingEntity instanceof ServerPlayer serverPlayer) {
         ServerLevel serverLevel = (ServerLevel)level;
         int n4 = 260 - n;
         CompoundTag compoundTag = serverPlayer.getPersistentData();
         double d10 = compoundTag.contains("yarik_x") ? compoundTag.getDouble("yarik_x") : serverPlayer.getX();
         double d11 = compoundTag.contains("yarik_y") ? compoundTag.getDouble("yarik_y") : serverPlayer.getY();
         double d12 = compoundTag.contains("yarik_z") ? compoundTag.getDouble("yarik_z") : serverPlayer.getZ();
         long l = compoundTag.contains("yarik_seed") ? compoundTag.getLong("yarik_seed") : 12345L;
         if (compoundTag.getBoolean("yarik_cennet_gidis")) {
            this.cennetSinematigi(serverLevel, serverPlayer, n4, l);
         } else {
            double d13 = d10;
            double d14 = d11 + (double)38.0F;
            double d15 = d12;
            RandomSource randomSource = level.random;
            float f = serverPlayer.getYRot() * ((float)Math.PI / 180F);
            double d16 = (double)(-Mth.sin(f));
            double d17 = (double)Mth.cos(f);
            double d18 = (double)Mth.cos(f);
            double d19 = (double)Mth.sin(f);
            double d20 = serverPlayer.getX() + d16 * (double)7.5F;
            double d21 = serverPlayer.getY() + 3.2;
            double d22 = serverPlayer.getZ() + d17 * (double)7.5F;
            if (n4 % 2 == 0) {
               double d9 = Math.min(4.8, 1.2 + (double)n4 * 0.04);
               double d8 = (double)n4 * 0.14;

               for(int n3 = 0; n3 < 8; ++n3) {
                  double d7 = d8 + (double)n3 * (Math.PI / 4D);
                  double d6 = serverPlayer.getX() + Math.cos(d7) * d9;
                  double d5 = serverPlayer.getZ() + Math.sin(d7) * d9;
                  p(serverLevel, serverPlayer, ParticleTypes.END_ROD, d6, serverPlayer.getY() + 0.15, d5, 1, 0.04, 0.02, 0.04, (double)0.0F);
                  if (n3 % 2 == 0) {
                     p(serverLevel, serverPlayer, ParticleTypes.REVERSE_PORTAL, d6, serverPlayer.getY() + 0.3, d5, 2, 0.1, 0.2, 0.1, 0.02);
                  }
               }

               p(serverLevel, serverPlayer, ParticleTypes.ENCHANT, serverPlayer.getX(), serverPlayer.getY() + 1.2, serverPlayer.getZ(), 12, 0.8, (double)1.0F, 0.8, 0.6);
            }

            if (n4 >= 30 && n4 % 2 == 0) {
               double d9 = Math.min((double)5.5F, (double)(n4 - 30) * 0.065);
               double d8 = Math.min(3.2, (double)(n4 - 30) * 0.038);

               for(int n3 = 0; n3 < 14; ++n3) {
                  double d7 = (double)n3 / (double)14.0F * Math.PI * (double)2.0F + (double)n4 * 0.18;
                  double d6 = Math.cos(d7) * d8;
                  double d5 = Math.sin(d7) * d9;
                  double d4 = d20 + d18 * d6;
                  double d3 = d21 + d5;
                  double d2 = d22 + d19 * d6;
                  p(serverLevel, serverPlayer, ParticleTypes.END_ROD, d4, d3, d2, 1, 0.06, 0.06, 0.06, 0.01);
                  p(serverLevel, serverPlayer, ParticleTypes.REVERSE_PORTAL, d4, d3, d2, 2, 0.15, 0.15, 0.15, 0.04);
               }

               if (n4 >= 75) {
                  p(serverLevel, serverPlayer, ParticleTypes.DRAGON_BREATH, d20, d21, d22, 5, d8 * 0.45, d9 * 0.45, d8 * 0.45, 0.02);
               }
            }

            if (n4 == 60) {
               baslikGonder(serverPlayer, "⚡ GERÇEKLİK YARILIYOR ⚡", "Boyutlar arası perde yırtılıyor, yıldızlar seni çağırıyor!", ChatFormatting.LIGHT_PURPLE, 8, 65, 15);
               ses(level, serverPlayer, SoundEvents.WARDEN_HEARTBEAT, 2.2F, 0.8F);
               ses(level, serverPlayer, SoundEvents.ELDER_GUARDIAN_CURSE, 0.9F, 0.7F);
            }

            double[][] dArray = yildizlar(l, d10, d11, d12);
            if (n4 >= 55) {
               int n2 = Math.min(220, (n4 - 55) * 4);

               for(int i = 0; i < n2; ++i) {
                  if ((n4 + i) % 7 == 0) {
                     p(serverLevel, serverPlayer, ParticleTypes.END_ROD, dArray[i][0], dArray[i][1], dArray[i][2], 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
                  }
               }

               if (n4 < 115 && n4 % 5 == 0) {
                  ses(level, serverPlayer, SoundEvents.AMETHYST_BLOCK_CHIME, 1.3F, 0.7F + randomSource.nextFloat() * 0.9F);
               }
            }

            if (n4 >= 90) {
               double d23 = Math.min((double)1.0F, (double)(n4 - 90) / (double)70.0F);
               if (n4 == 90) {
                  ses(level, serverPlayer, SoundEvents.LIGHTNING_BOLT_THUNDER, 0.8F, 0.5F);
               }

               if (n4 <= 165 && n4 % 8 == 0) {
                  ses(level, serverPlayer, SoundEvents.GLASS_BREAK, 0.9F, 0.5F + (float)d23 * 0.4F);
               }

               if (n4 % 4 == 0) {
                  double[] dArray2;
                  for(List<double[]> list : catlak(d10, d14, d12, l)) {
                     for(int i = 0; i < list.size() && !((dArray2 = (double[])list.get(i))[3] > d23); ++i) {
                        p(serverLevel, serverPlayer, ParticleTypes.END_ROD, dArray2[0], dArray2[1], dArray2[2], 2, 0.12, 0.12, 0.12, (double)0.0F);
                        if (i % 2 == 0) {
                           p(serverLevel, serverPlayer, ParticleTypes.REVERSE_PORTAL, dArray2[0], dArray2[1], dArray2[2], 3, 0.3, 0.3, 0.3, 0.05);
                        }

                        if (i + 1 < list.size() && ((double[])list.get(i + 1))[3] <= d23) {
                           double[] dArray3 = (double[])list.get(i + 1);
                           p(serverLevel, serverPlayer, ParticleTypes.END_ROD, (dArray2[0] + dArray3[0]) / (double)2.0F, (dArray2[1] + dArray3[1]) / (double)2.0F, (dArray2[2] + dArray3[2]) / (double)2.0F, 1, 0.05, 0.05, 0.05, (double)0.0F);
                        }
                     }
                  }
               }

               double d = Math.min((double)3.5F, (double)0.5F + (double)(n4 - 100) * 0.04);
               if (n4 >= 100) {
                  for(int i = 0; i < 6; ++i) {
                     double d24 = (double)n4 * 0.18 + (double)i * Math.PI / (double)3.0F;
                     p(serverLevel, serverPlayer, ParticleTypes.REVERSE_PORTAL, d13 + Math.cos(d24) * d, d14 + Math.sin((double)n4 * 0.1 + (double)i) * 0.6, d15 + Math.sin(d24) * d, 2, 0.1, 0.1, 0.1, (double)0.0F);
                  }

                  p(serverLevel, serverPlayer, ParticleTypes.DRAGON_BREATH, d13, d14, d15, 6, (double)1.5F, (double)0.5F, (double)1.5F, 0.02);
                  p(serverLevel, serverPlayer, ParticleTypes.END_ROD, d13, d14, d15, 3, (double)0.5F, 0.3, (double)0.5F, 0.02);
               }
            }

            if (n4 >= 95) {
               if (n4 == 95) {
                  for(int n2 = 0; n2 < 6; ++n2) {
                     Phantom phantom2 = (Phantom)EntityType.PHANTOM.create(serverLevel);
                     if (phantom2 != null) {
                        phantom2.setNoAi(true);
                        phantom2.setInvulnerable(true);
                        phantom2.setSilent(true);
                        phantom2.addTag("yarik_phantom");
                        phantom2.setPos(d13, d14, d15);
                        serverLevel.addFreshEntity(phantom2);
                     }
                  }
               }

               List list;
               if ((list = serverLevel.getEntitiesOfClass(Phantom.class, serverPlayer.getBoundingBox().inflate((double)120.0F), (phantom) -> phantom.getTags().contains("yarik_phantom"))) != null) {
                  for(int i = 0; i < list.size(); ++i) {
                     double d = (double)n4 * 0.07 + (double)i * ((Math.PI * 2D) / (double)Math.max(1, list.size()));
                     double d25 = (double)9.0F + Math.sin((double)n4 * 0.05 + (double)i) * (double)2.0F;
                     ((Phantom)list.get(i)).moveTo(d13 + Math.cos(d) * d25, d14 + (double)2.0F * Math.sin((double)n4 * 0.08 + (double)i), d15 + Math.sin(d) * d25, (float)Math.toDegrees(d) + 90.0F, 0.0F);
                  }
               }
            }

            if (n4 == 135) {
               baslikGonder(serverPlayer, "\ud83c\udf0c YILDIZ KORİDORU AÇILDI \ud83c\udf0c", "Bambaşka bir boyuta yükseliyorsun...", ChatFormatting.GOLD, 5, 115, 20);

               for(int n2 = 0; n2 < 220; ++n2) {
                  p(serverLevel, serverPlayer, ParticleTypes.FLASH, dArray[n2][0], dArray[n2][1], dArray[n2][2], 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
               }

               p(serverLevel, serverPlayer, ParticleTypes.FLASH, d20, d21, d22, 3, (double)0.5F, (double)0.5F, (double)0.5F, (double)0.0F);
               ses(level, serverPlayer, SoundEvents.BEACON_POWER_SELECT, 2.2F, 0.7F);
               ses(level, serverPlayer, SoundEvents.END_PORTAL_SPAWN, 1.1F, 0.8F);
            }

            if (n4 >= 135 && n4 < 258) {
               double d26 = serverPlayer.getX();
               double d = serverPlayer.getY() + (double)1.0F;
               double d27 = serverPlayer.getZ();
               double d28 = Math.max(1.1, 3.4 - (double)(n4 - 135) / (double)125.0F * 2.1);

               for(int n6 = 0; n6 < 3; ++n6) {
                  for(int n5 = -2; n5 <= 6; n5 += 2) {
                     double d4 = (double)n4 * 0.32 + (double)n6 * 2.0943951023931953 + (double)n5 * 0.45;
                     double d3 = d26 + Math.cos(d4) * d28;
                     double d2 = d + (double)n5 * 1.1;
                     double d29 = d27 + Math.sin(d4) * d28;
                     p(serverLevel, serverPlayer, ParticleTypes.END_ROD, d3, d2, d29, 1, 0.04, 0.04, 0.04, 0.01);
                     if ((n5 + n6) % 2 == 0) {
                        p(serverLevel, serverPlayer, ParticleTypes.REVERSE_PORTAL, d3, d2, d29, 2, 0.12, 0.12, 0.12, 0.03);
                     }
                  }
               }

               int var93 = Math.min(18, 6 + (n4 - 140) / 8);

               for(int n5 = 0; n5 < var93; ++n5) {
                  int n7 = (n4 * 7 + n5 * 23) % 220;
                  double d30 = ((double)n4 * 0.07 + (double)n5 * 0.13) % (double)1.0F;

                  for(int i = 0; i < 4; ++i) {
                     double d2 = Math.min((double)1.0F, d30 + (double)i * 0.04);
                     p(serverLevel, serverPlayer, ParticleTypes.END_ROD, dArray[n7][0] + (d26 - dArray[n7][0]) * d2, dArray[n7][1] + (d - dArray[n7][1]) * d2, dArray[n7][2] + (d27 - dArray[n7][2]) * d2, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
                  }
               }

               if (n4 % 8 == 0) {
                  ses(level, serverPlayer, SoundEvents.BEACON_AMBIENT, 1.3F, 0.5F + (float)(n4 - 140) / 120.0F);
               }

               if (n4 % 22 == 0) {
                  ses(level, serverPlayer, SoundEvents.PORTAL_AMBIENT, 1.0F, 0.9F);
               }
            }

            if (n4 >= 130) {
               int n8 = Math.min(8, (n4 - 130) / 14);
               serverPlayer.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 6, n8, false, false, false));
               serverPlayer.fallDistance = 0.0F;
            }

            if (n4 == 236) {
               ses(level, serverPlayer, SoundEvents.LIGHTNING_BOLT_THUNDER, 1.4F, 0.8F);
               ses(level, serverPlayer, SoundEvents.END_PORTAL_SPAWN, 1.4F, 1.2F);
            }

            if (n4 >= 236) {
               p(serverLevel, serverPlayer, ParticleTypes.FLASH, serverPlayer.getX(), serverPlayer.getY() + (double)1.5F, serverPlayer.getZ(), 2, 0.3, 0.3, 0.3, (double)0.0F);
            }

         }
      }
   }

   public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
      if (!level.isClientSide && livingEntity instanceof ServerPlayer serverPlayer) {
         this.temizle((ServerLevel)level, serverPlayer);
         serverPlayer.getPersistentData().remove("yarik_cennet_gidis");
         ResourceKey resourceKey = level.dimension();
         boolean bl = resourceKey.equals(CENNET);
         ServerLevel serverLevel = serverPlayer.server.getLevel(bl ? Level.OVERWORLD : CENNET);
         if (serverLevel != null) {
            BlockPos blockPos = bl ? serverLevel.getSharedSpawnPos() : BlockPos.ZERO;
            serverLevel.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
            BlockPos blockPos2 = serverLevel.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, blockPos);
            serverPlayer.removeEffect(MobEffects.LEVITATION);
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 140, 0, false, false, false));
            serverPlayer.fallDistance = 0.0F;
            double d = (double)blockPos2.getX() + (double)0.5F;
            double d2 = (double)blockPos2.getY() + (double)6.5F;
            double d3 = (double)blockPos2.getZ() + (double)0.5F;
            serverPlayer.teleportTo(serverLevel, d, d2, d3, serverPlayer.getYRot(), 0.0F);
            serverLevel.playSound((Player)null, serverPlayer.blockPosition(), SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 1.2F, 1.0F);
            serverLevel.playSound((Player)null, serverPlayer.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.2F, 0.6F);
            serverLevel.playSound((Player)null, serverPlayer.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 2.0F, 0.85F);
            serverLevel.sendParticles(ParticleTypes.END_ROD, d, d2 + (double)1.0F, d3, 90, 1.6, 2.2, 1.6, 0.18);
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, d, d2 + (double)1.0F, d3, 80, 1.4, (double)2.0F, 1.4, 0.15);
            serverLevel.sendParticles(ParticleTypes.FLASH, d, d2 + (double)1.5F, d3, 4, 0.3, 0.3, 0.3, (double)0.0F);
            serverPlayer.getPersistentData().putInt("YarikVarisSure", 90);
            if (!bl) {
               baslikGonder(serverPlayer, "\ud83c\udf0c YILDIZ IŞIĞI ORMANI \ud83c\udf0c", "Yarık Asası'nın açtığı boyuttan kadim yıldızların diyarına ulaştın!", ChatFormatting.AQUA, 15, 85, 25);
               serverPlayer.sendSystemMessage(Component.literal("✦ Yarık Asası gerçekliği yardı ve seni Yıldız Işığı Ormanı boyutuna taşıdı! ✦").withStyle(new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD}));
            } else {
               baslikGonder(serverPlayer, "✦ NORMAL DÜNYA ✦", "Boyut yarığından kendi dünyana geri döndün", ChatFormatting.GOLD, 15, 70, 20);
            }
         }

         itemStack.hurtAndBreak(1, serverPlayer, EquipmentSlot.MAINHAND);
         serverPlayer.getCooldowns().addCooldown(this, 20);
      }

      return itemStack;
   }

   public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int n) {
      if (!level.isClientSide && livingEntity instanceof ServerPlayer serverPlayer) {
         this.temizle((ServerLevel)level, serverPlayer);
         serverPlayer.getPersistentData().remove("yarik_cennet_gidis");
         serverPlayer.removeEffect(MobEffects.LEVITATION);
         serverPlayer.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0, false, false, false));
      }

   }

   public void temizle(ServerLevel serverLevel, ServerPlayer serverPlayer) {
      List list = serverLevel.getEntitiesOfClass(Phantom.class, serverPlayer.getBoundingBox().inflate((double)150.0F), (phantom) -> phantom.getTags().contains("yarik_phantom"));
      if (list != null) {
         list.forEach((entity) -> ((Entity)entity).discard());
      }

   }

   static {
      CENNET = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("cennetmod", "cennet_karanligi"));
   }
}
