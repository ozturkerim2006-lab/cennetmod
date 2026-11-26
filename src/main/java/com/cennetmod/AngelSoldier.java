package com.cennetmod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;

public class AngelSoldier extends Mob {
   public static final EntityDataAccessor<Integer> POZ;
   public static final EntityDataAccessor<Integer> KOLTUK;
   private static final int BITIS_INSA = 135;
   private static final BlockState SMOOTH;
   private static final BlockState PILLAR;
   private static final BlockState GOLD;
   private static final BlockState BRICKS;
   private static final BlockState GLASS;
   private static final BlockState AIR;
   private int rol = 0;
   private int kx;
   private int ky;
   private int kz;
   private UUID esUuid;
   private boolean mesgul = false;
   private boolean insaEdilecek = false;
   private int zaman = 0;
   private int plazaSatir = 20;
   private UUID oyuncu;
   private UUID kizginHedef;
   private int kizginSure = 0;
   private ItemStack emanet;
   private boolean geriVer;
   private ItemEntity ucanKart;
   private NurKapi.Kapi kapi;
   private double camurBatma;
   private int devriyeYon;
   private double devriyeAci;
   private int pegasusDurum;
   private int pegasusTick;
   private double pBasX;
   private double pBasY;
   private double pBasZ;
   private double pHedefX;
   private double pHedefY;
   private double pHedefZ;
   private double pYanKayma;
   private double pYukariKayma;
   private String pHedefIsim;
   private int pHedefBoyut;

   public AngelSoldier(EntityType<? extends Mob> var1, Level var2) {
      super(var1, var2);
      this.emanet = ItemStack.EMPTY;
      this.geriVer = false;
      this.camurBatma = (double)0.0F;
      this.devriyeYon = 1;
      this.devriyeAci = (double)0.0F;
      this.pegasusDurum = 0;
      this.pegasusTick = 0;
      this.pYanKayma = (double)0.0F;
      this.pYukariKayma = (double)0.0F;
      this.pHedefIsim = "";
      this.pHedefBoyut = 0;
      this.setInvulnerable(true);
      this.setNoGravity(true);
      this.setPersistenceRequired();
   }

   public static AttributeSupplier.Builder nitelikler() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, (double)20.0F).add(Attributes.MOVEMENT_SPEED, (double)0.0F);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(POZ, 0);
      var1.define(KOLTUK, 0);
   }

   public int getPoz() {
      return (Integer)this.entityData.get(POZ);
   }

   public void setPoz(int var1) {
      this.entityData.set(POZ, var1);
   }

   public int getKoltukSecim() {
      return (Integer)this.entityData.get(KOLTUK);
   }

   public void setKoltukSecim(int var1) {
      this.entityData.set(KOLTUK, var1 & 3);
   }

   public int getRol() {
      return this.rol;
   }

   public void setNpcRol(int var1, int var2, int var3, int var4, UUID var5) {
      this.rol = var1;
      this.kx = var2;
      this.ky = var3;
      this.kz = var4;
      this.oyuncu = var5;
      this.setPoz(var1);
      if (var1 == 40 || var1 == 60) {
         this.setInvulnerable(false);
      }

   }

   public void setEsUuid(UUID var1) {
      this.esUuid = var1;
   }

   public UUID getEsUuid() {
      return this.esUuid;
   }

   public void kizdir(UUID var1) {
      this.kizginHedef = var1;
      this.kizginSure = 500;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.rol != 63 && this.getPoz() != 63 && this.getPoz() != 65) {
         if (this.rol != 62 && this.rol != 64 && this.getPoz() != 62) {
            if (this.rol != 61 && this.getPoz() != 61) {
               if (this.rol == 40 && !this.level().isClientSide) {
                  Entity var10 = var1.getEntity();
                  if (var10 instanceof LivingEntity && !(var10 instanceof AngelSoldier)) {
                     UUID var4 = var10.getUUID();
                     this.kizdir(var4);
                     if (this.level() instanceof ServerLevel) {
                        ServerLevel var5 = (ServerLevel)this.level();
                        List var6 = var5.getEntitiesOfClass(AngelSoldier.class, this.getBoundingBox().inflate((double)28.0F), (var0) -> var0.getRol() == 40);
                        if (var6 != null) {
                           for(AngelSoldier var8 : var6) {
                              var8.kizdir(var4);
                           }
                        }

                        if (var10 instanceof ServerPlayer) {
                           ((ServerPlayer)var10).sendSystemMessage(Component.literal("<Doğu Dağ Savaşçısı> ").withStyle(ChatFormatting.GOLD).append(Component.literal("Bize el kaldırdın! Dağların gazabıyla yüzleş!").withStyle(ChatFormatting.WHITE)));
                        }
                     }
                  }
               }

               return super.hurt(var1, this.rol == 40 ? var2 * 0.25F : var2);
            } else {
               if (this.level() instanceof ServerLevel) {
                  ServerLevel var9 = (ServerLevel)this.level();
                  var9.sendParticles(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 1.1, this.getZ(), 15, (double)0.25F, (double)0.25F, (double)0.25F, 0.02);
                  this.discard();
               }

               return false;
            }
         } else {
            if (this.level() instanceof ServerLevel) {
               ServerLevel var3 = (ServerLevel)this.level();
               var3.sendParticles(ParticleTypes.DRAGON_BREATH, this.getX(), this.getY() + 1.2, this.getZ(), 35, 0.45, 0.9, 0.45, 0.04);
               var3.sendParticles(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 1.2, this.getZ(), 45, 0.55, (double)1.0F, 0.55, 0.02);
               ses(var3, this.getX(), this.getY(), this.getZ(), SoundEvents.ENDERMAN_TELEPORT, 1.8F, 0.5F);
               this.discard();
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public boolean mesgul() {
      return this.mesgul;
   }

   public boolean removeWhenFarAway(double var1) {
      return this.rol >= 10 && var1 > (double)6400.0F;
   }

   public boolean isPushable() {
      return false;
   }

   protected boolean canAddPassenger(Entity var1) {
      if (this.rol == 50 || this.getPoz() >= 50 && this.getPoz() < 60) {
         return this.getPassengers().size() < 4;
      } else {
         return super.canAddPassenger(var1);
      }
   }

   public LivingEntity getControllingPassenger() {
      return this.rol != 50 && (this.getPoz() < 50 || this.getPoz() >= 60) ? super.getControllingPassenger() : null;
   }

   public Vec3 koltukKonumuHesapla(Entity var1) {
      List var2 = this.getPassengers();
      int var3 = var2.indexOf(var1);
      if (var3 < 0) {
         var3 = 0;
      }

      int var4 = var3 + this.getKoltukSecim() & 3;
      double var5 = var4 < 2 ? -0.92 : 0.92;
      double var7 = var4 % 2 == 0 ? 0.7 : -0.7;
      float var9 = this.getYRot();
      float var10 = var9 * ((float)Math.PI / 180F);
      double var11 = (double)(-Mth.sin(var10));
      double var13 = (double)Mth.cos(var10);
      double var15 = (double)Mth.cos(var10);
      double var17 = (double)Mth.sin(var10);
      double var19 = this.getX() + var11 * var5 + var15 * var7;
      double var21 = this.getY() + 0.72;
      double var23 = this.getZ() + var13 * var5 + var17 * var7;
      return new Vec3(var19, var21, var23);
   }

   private static String koltukAdi(int var0) {
      switch (var0 & 3) {
         case 0 -> {
            return "Arka Sol Kadife Koltuk";
         }
         case 1 -> {
            return "Arka Sağ Kadife Koltuk";
         }
         case 2 -> {
            return "Ön Sol Kadife Koltuk";
         }
         default -> {
            return "Ön Sağ Kadife Koltuk";
         }
      }
   }

   protected void positionRider(Entity var1, Entity.MoveFunction var2) {
      if ((this.rol == 50 || this.getPoz() >= 50 && this.getPoz() < 60) && this.hasPassenger(var1)) {
         this.yBodyRot = this.getYRot();
         this.yBodyRotO = this.yRotO;
         Vec3 var3 = this.koltukKonumuHesapla(var1);
         var2.accept(var1, var3.x, var3.y, var3.z);
      } else {
         super.positionRider(var1, var2);
      }
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      if (this.rol != 62 && this.getPoz() != 62) {
         if (this.rol == 50 || this.getPoz() >= 50 && this.getPoz() < 60) {
            if (!this.level().dimension().equals(YarikAsasi.CENNET)) {
               return InteractionResult.PASS;
            } else {
               if (!this.level().isClientSide && var2 == InteractionHand.MAIN_HAND) {
                  if (this.hasPassenger(var1)) {
                     int var7 = this.getKoltukSecim() + 1 & 3;
                     this.setKoltukSecim(var7);
                     int var4 = Math.max(0, this.getPassengers().indexOf(var1));
                     int var5 = var4 + var7 & 3;
                     Vec3 var6 = this.koltukKonumuHesapla(var1);
                     var1.setPos(var6.x, var6.y, var6.z);
                     if (var1 instanceof ServerPlayer) {
                        ((ServerPlayer)var1).displayClientMessage(Component.literal("✦ Koltuk Değiştirildi: Koltuk " + (var5 + 1) + " — " + koltukAdi(var5) + " ✦").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}), true);
                     }
                  } else if (this.getPassengers().size() < 4 && !var1.isPassenger()) {
                     var1.startRiding(this, true);
                     int var8 = Math.max(0, this.getPassengers().indexOf(var1));
                     int var9 = var8 + this.getKoltukSecim() & 3;
                     if (var1 instanceof ServerPlayer) {
                        ((ServerPlayer)var1).displayClientMessage(Component.literal("✦ Kraliyet Arabası — Koltuk " + (var9 + 1) + " (" + koltukAdi(var9) + ")! (Sağ tıkla koltuk değiştir / Sohbete 'pegasus, beni ... götür' yaz) ✦").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}), true);
                     }
                  }
               }

               return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
         } else {
            return super.mobInteract(var1, var2);
         }
      } else {
         if (!this.level().isClientSide && this.level() instanceof ServerLevel) {
            ServerLevel var3 = (ServerLevel)this.level();
            var3.sendParticles(ParticleTypes.DRAGON_BREATH, this.getX(), this.getY() + 1.2, this.getZ(), 35, 0.45, 0.9, 0.45, 0.04);
            var3.sendParticles(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 1.2, this.getZ(), 45, 0.55, (double)1.0F, 0.55, 0.02);
            this.discard();
         }

         return InteractionResult.PASS;
      }
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Rol", this.rol);
      var1.putInt("Kx", this.kx);
      var1.putInt("Ky", this.ky);
      var1.putInt("Kz", this.kz);
      var1.putInt("KoltukSecim", this.getKoltukSecim());
      var1.putDouble("CamurBatma", this.camurBatma);
      if (this.esUuid != null) {
         var1.putUUID("Es", this.esUuid);
      }

      if (this.oyuncu != null) {
         var1.putUUID("Sahip", this.oyuncu);
      }

      if (this.rol == 50) {
         var1.putInt("PDurum", this.pegasusDurum);
         var1.putInt("PTick", this.pegasusTick);
         var1.putDouble("PHx", this.pHedefX);
         var1.putDouble("PHy", this.pHedefY);
         var1.putDouble("PHz", this.pHedefZ);
         var1.putDouble("PBx", this.pBasX);
         var1.putDouble("PBy", this.pBasY);
         var1.putDouble("PBz", this.pBasZ);
         if (this.pHedefIsim != null) {
            var1.putString("PIsim", this.pHedefIsim);
         }
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.rol = var1.getInt("Rol");
      this.kx = var1.getInt("Kx");
      this.ky = var1.getInt("Ky");
      this.kz = var1.getInt("Kz");
      this.setKoltukSecim(var1.getInt("KoltukSecim"));
      this.camurBatma = var1.getDouble("CamurBatma");
      if (var1.hasUUID("Es")) {
         this.esUuid = var1.getUUID("Es");
      }

      if (var1.hasUUID("Sahip")) {
         this.oyuncu = var1.getUUID("Sahip");
      }

      if (this.rol == 50) {
         this.pegasusDurum = var1.getInt("PDurum");
         this.pegasusTick = var1.getInt("PTick");
         this.pHedefX = var1.getDouble("PHx");
         this.pHedefY = var1.getDouble("PHy");
         this.pHedefZ = var1.getDouble("PHz");
         this.pBasX = var1.getDouble("PBx");
         this.pBasY = var1.getDouble("PBy");
         this.pBasZ = var1.getDouble("PBz");
         this.pHedefIsim = var1.getString("PIsim");
         this.setPoz(this.pegasusDurum == 1 ? 51 : (this.pegasusDurum == 3 ? 52 : 50));
         this.setNoGravity(true);
         this.setNoAi(true);
      } else if (this.rol >= 10) {
         this.setPoz(this.rol);
         if (this.rol == 40 || this.rol == 60) {
            this.setInvulnerable(false);
         }
      }

   }

   public void remove(Entity.RemovalReason var1) {
      if (!this.level().isClientSide && this.mesgul) {
         this.acilDurum();
      }

      super.remove(var1);
   }

   public void tick() {
      int var1 = this.getPoz();
      if (this.rol != 50 && (var1 < 50 || var1 >= 60)) {
         super.tick();
         if (!this.level().isClientSide) {
            if (this.rol >= 10) {
               this.npcTick();
            } else {
               if (this.rol == 0 && this.mesgul) {
                  this.sahne();
               } else if (this.rol == 0 && this.kapi != null && !this.mesgul) {
                  if (this.level() instanceof ServerLevel && this.oyuncu != null) {
                     ServerLevel var2 = (ServerLevel)this.level();
                     ServerPlayer var3 = var2.getServer().getPlayerList().getPlayer(this.oyuncu);
                     if (var3 != null && var3.isAlive() && var3.level() == var2) {
                        NurSistemi.kapidanGecisKontrol(var3);
                     }
                  }
               } else if (this.kapi == null && this.tickCount > 40 && (this.kx != 0 || this.ky != 0 || this.kz != 0)) {
                  this.yetimTemizle();
               }

            }
         }
      } else {
         this.rol = 50;
         this.setNoGravity(true);
         this.setNoAi(true);
         if (!this.level().isClientSide) {
            this.pegasusTick();
         }

         this.yBodyRot = this.getYRot();
         this.yBodyRotO = this.yRotO;
         this.yHeadRot = this.getYRot();
         this.yHeadRotO = this.yRotO;
         super.tick();
         this.yBodyRot = this.getYRot();
         this.yBodyRotO = this.yRotO;
      }
   }

   private void npcTick() {
      if (this.level() instanceof ServerLevel) {
         ServerLevel var1 = (ServerLevel)this.level();
         ServerPlayer var2 = this.oyuncu == null ? null : var1.getServer().getPlayerList().getPlayer(this.oyuncu);
         if (var2 != null && var2.isAlive() && var2.level() == var1 && !(var2.distanceToSqr(this.getX(), this.getY(), this.getZ()) > (double)7225.0F)) {
            double var3 = var2.distanceToSqr(this.getX(), this.getY(), this.getZ());
            int var5 = this.kx * 73856093 ^ this.kz * 19349663 ^ this.ky * 83492791;
            double var6 = (double)(var5 & 1023) * 0.01;
            if (this.rol == 60) {
               if (this.getPoz() != 60) {
                  this.setPoz(60);
               }

               if (this.tickCount <= 360 && !NurBaskentFeature.zifiriYerlesimIciMi(Mth.floor(this.getX()), Mth.floor(this.getZ()))) {
                  double var52 = var2.getX() - this.getX();
                  double var61 = var2.getZ() - this.getZ();
                  double var67 = Math.sqrt(var52 * var52 + var61 * var61);
                  float var73 = (float)(Mth.atan2(var61, var52) * (180D / Math.PI)) - 90.0F;
                  int var75 = Mth.floor(this.getX());
                  int var77 = Mth.floor(this.getZ());
                  int var79 = bataklikYuzeyBul(var1, var75, this.ky, var77);
                  this.ky = var79;
                  BlockState var80 = var1.getBlockState(new BlockPos(var75, var79 - 1, var77));
                  BlockState var81 = var1.getBlockState(new BlockPos(var75, var79, var77));
                  boolean var83 = var80.is(Blocks.MUD) || var81.is(Blocks.MUD) || var80.is(Blocks.SOUL_SAND);
                  if (var83) {
                     this.camurBatma = Math.min(1.88, this.camurBatma + 0.01);
                     if (this.tickCount % 12 == 0) {
                        pr(var1, var2, ParticleTypes.REVERSE_PORTAL, this.getX(), (double)var79 + 0.2, this.getZ(), 5, 0.35, 0.1, 0.35, 0.01);
                        ses(var1, this.getX(), (double)var79, this.getZ(), SoundEvents.STONE_PLACE, 1.0F, 0.55F);
                     }

                     if (this.camurBatma >= 1.82) {
                        pr(var1, var2, ParticleTypes.DRAGON_BREATH, this.getX(), (double)var79 + 0.2, this.getZ(), 12, 0.3, 0.2, 0.3, 0.02);
                        this.discard();
                        return;
                     }
                  } else {
                     this.camurBatma = Math.max((double)0.0F, this.camurBatma - 0.025);
                  }

                  double var21 = Math.max(0.22, (double)1.0F - this.camurBatma * 0.45);
                  double var23 = 0.16 * var21;
                  double var86 = this.getX();
                  double var90 = this.getZ();
                  if (var67 > (double)1.25F && var67 < (double)32.0F) {
                     int var91 = Mth.floor(var86 + var52 / var67 * var23);
                     int var93 = Mth.floor(var90 + var61 / var67 * var23);
                     if (!NurBaskentFeature.zifiriYerlesimIciMi(var91, var93)) {
                        var86 += var52 / var67 * var23;
                        var90 += var61 / var67 * var23;
                     }
                  }

                  double var92 = (double)var79 - this.camurBatma;
                  this.moveTo(var86, var92, var90, var73, 0.0F);
                  this.setYRot(var73);
                  this.setYBodyRot(var73);
                  this.setYHeadRot(var73);
                  if (var67 <= 2.65 && this.tickCount % 25 == 0) {
                     int var94 = Mth.floor(var2.getX());
                     int var95 = Mth.floor(var2.getZ());
                     double var33 = var67 > 0.1 ? var52 / var67 : (double)1.0F;
                     double var35 = var67 > 0.1 ? var61 / var67 : (double)0.0F;
                     double var37 = (double)999.0F;

                     for(int var39 = -3; var39 <= 3; ++var39) {
                        for(int var40 = -3; var40 <= 3; ++var40) {
                           if (var39 != 0 || var40 != 0) {
                              int var41 = var94 + var39;
                              int var42 = var95 + var40;
                              if (!NurBaskentFeature.bataklikSaglamZeminMi(var41, var42)) {
                                 double var43 = (double)(var39 * var39 + var40 * var40);
                                 if (var43 < var37) {
                                    var37 = var43;
                                    double var45 = Math.sqrt(var43);
                                    var33 = (double)var39 / var45;
                                    var35 = (double)var40 / var45;
                                 }
                              }
                           }
                        }
                     }

                     var2.hurt(this.damageSources().mobAttack(this), 2.0F);
                     var2.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 45, 2, false, false, false));
                     double var96 = var2.getX() + var33 * 1.18;
                     double var97 = var2.getZ() + var35 * 1.18;
                     var2.teleportTo(var1, var96, var2.getY(), var97, var2.getYRot(), var2.getXRot());
                     var2.getPersistentData().putDouble("CamurBatma", var2.getPersistentData().getDouble("CamurBatma") + 0.36);
                     ses(var1, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.STONE_PLACE, 1.5F, 0.45F);
                     var2.displayClientMessage(Component.literal("☠ Çamur Yaratığı seni bataklık çamuruna itti! Çabuk zıplayarak taşlara çık! ☠").withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.BOLD}), true);
                  }

               } else {
                  pr(var1, var2, ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.4, this.getZ(), 10, 0.3, 0.2, 0.3, 0.01);
                  this.discard();
               }
            } else if (this.rol == 61) {
               if (this.getPoz() != 61) {
                  this.setPoz(61);
               }

               if (this.tickCount <= 120 && !(var3 < (double)14.0F)) {
                  this.setYRot(0.0F);
                  this.setYBodyRot(0.0F);
                  this.setYHeadRot(0.0F);
                  this.setXRot(0.0F);
                  this.moveTo((double)this.kx + (double)0.5F, (double)71.0F, (double)this.kz + (double)0.5F, 0.0F, 0.0F);
               } else {
                  pr(var1, var2, ParticleTypes.SMOKE, this.getX(), this.getY() + (double)2.5F, this.getZ(), 8, 0.35, 1.4, 0.35, 0.01);
                  this.discard();
               }
            } else if (this.rol == 62) {
               if (this.getPoz() != 62) {
                  this.setPoz(62);
               }

               bak(this, var2);
               this.setYBodyRot(this.getYRot());
               this.setYHeadRot(this.getYRot());
               this.moveTo((double)this.kx + (double)0.5F, (double)this.ky, (double)this.kz + (double)0.5F, this.getYRot(), 0.0F);
               if (this.tickCount % 8 == 0) {
                  pr(var1, var2, ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + (double)1.5F, this.getZ(), 4, (double)0.25F, 0.8, (double)0.25F, 0.01);
               }

               if (var3 < (double)132.25F || this.tickCount > 170) {
                  pr(var1, var2, ParticleTypes.DRAGON_BREATH, this.getX(), this.getY() + 1.3, this.getZ(), 32, 0.4, 0.9, 0.4, 0.03);
                  pr(var1, var2, ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 1.3, this.getZ(), 40, (double)0.5F, (double)1.0F, (double)0.5F, 0.02);
                  if (var3 < (double)132.25F) {
                     ses(var1, this.getX(), this.getY() + (double)1.5F, this.getZ(), SoundEvents.ENDERMAN_TELEPORT, 1.4F, 0.45F);
                  }

                  this.discard();
               }

            } else if (this.rol == 63) {
               this.duvardanKurtar(var1, 72);
               if (var3 < (double)169.0F) {
                  this.setPoz(65);
                  bak(this, var2);
                  this.setYBodyRot(this.getYRot());
                  this.setYHeadRot(this.getYRot());
                  this.moveTo(this.getX(), (double)72.0F, this.getZ(), this.getYRot(), 0.0F);
               } else {
                  this.setPoz(63);
                  if (this.devriyeAci == (double)0.0F) {
                     this.devriyeAci = (double)(var5 & 511) * 0.02;
                  }

                  this.devriyeAci += 0.02 * (double)this.devriyeYon;
                  double var51 = (double)3.0F + (double)(Math.abs(var5) % 4);
                  double var60 = (double)this.kx + (double)0.5F + Math.cos(this.devriyeAci) * var51;
                  double var66 = (double)this.kz + (double)0.5F + Math.sin(this.devriyeAci * 1.1) * var51;
                  this.engelsizYuru(var1, var60, var66, 0.1, (double)0.0F);
               }

            } else if (this.rol == 64) {
               if (this.getPoz() != 62) {
                  this.setPoz(62);
               }

               AngelSoldier var50 = this.esBul(var1);
               if (var50 != null && var50.isAlive() && this.tickCount <= 125 && !(var3 < (double)7.5F)) {
                  double var56 = var50.getX() - var2.getX();
                  double var62 = var50.getZ() - var2.getZ();
                  double var69 = Math.sqrt(var56 * var56 + var62 * var62);
                  if (var69 > 0.05) {
                     double var74 = var56 / var69;
                     double var78 = var62 / var69;
                     int var19 = Mth.floor(var50.getY());
                     double var82 = var50.getX() - var78 * 0.55;
                     double var84 = var50.getZ() + var74 * 0.55;

                     for(double var89 : new double[]{1.28, 0.95, 0.65}) {
                        double var29 = var50.getX() + var74 * var89 - var78 * 0.24;
                        double var31 = var50.getZ() + var78 * var89 + var74 * 0.24;
                        if (npcKonumAcikMi(var1, var29, var19, var31)) {
                           var82 = var29;
                           var84 = var31;
                           break;
                        }
                     }

                     this.moveTo(var82, var50.getY(), var84, this.getYRot(), 0.0F);
                  }

                  bak(this, var2);
                  this.setYBodyRot(this.getYRot());
                  this.setYHeadRot(this.getYRot());
                  if (this.tickCount % 6 == 0) {
                     pr(var1, var2, ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.3, this.getZ(), 2, 0.22, 0.65, 0.22, 0.005);
                     pr(var1, var2, ParticleTypes.ASH, this.getX(), this.getY() + 1.6, this.getZ(), 3, 0.3, (double)0.5F, 0.3, 0.01);
                  }

                  if (this.tickCount == 45 || this.tickCount == 90) {
                     ses(var1, this.getX(), this.getY() + 1.4, this.getZ(), SoundEvents.WARDEN_HEARTBEAT, 1.1F, 0.5F);
                  }

               } else {
                  pr(var1, var2, ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.2, this.getZ(), 24, 0.35, 0.8, 0.35, 0.02);
                  pr(var1, var2, ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 1.2, this.getZ(), 28, 0.4, 0.9, 0.4, 0.02);
                  this.discard();
               }
            } else {
               if (this.rol == 40) {
                  this.duvardanKurtar(var1, this.ky);
                  if (this.kizginSure > 0) {
                     --this.kizginSure;
                  } else {
                     this.kizginHedef = null;
                  }

                  Object var8 = null;
                  if (this.kizginHedef != null) {
                     Entity var9 = var1.getEntity(this.kizginHedef);
                     if (var9 instanceof LivingEntity && var9.isAlive() && var9.distanceToSqr(this.getX(), this.getY(), this.getZ()) <= (double)1600.0F) {
                        var8 = (LivingEntity)var9;
                     }
                  }

                  if (var8 == null) {
                     List var53 = var1.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate((double)38.0F), (var0) -> !(var0 instanceof AngelSoldier) && var0.isAlive());
                     double var10 = Double.MAX_VALUE;
                     if (var53 != null) {
                        for(Mob var13 : var53) {
                           double var14 = var13.distanceToSqr(this.getX(), this.getY(), this.getZ());
                           if (var14 < var10) {
                              var10 = var14;
                              var8 = var13;
                           }
                        }
                     }
                  }

                  if (var8 != null) {
                     this.setPoz(41);
                     double var55 = ((LivingEntity)var8).getX() - this.getX();
                     double var11 = ((LivingEntity)var8).getZ() - this.getZ();
                     double var68 = Math.sqrt(var55 * var55 + var11 * var11);
                     float var15 = (float)(Mth.atan2(var11, var55) * (180D / Math.PI)) - 90.0F;
                     if (var68 > 1.8) {
                        this.engelsizYuru(var1, ((LivingEntity)var8).getX(), ((LivingEntity)var8).getZ(), 0.34, (double)0.0F);
                     } else {
                        this.setYRot(var15);
                        this.setYBodyRot(var15);
                        this.setYHeadRot(var15);
                     }

                     if (var68 <= 3.4 && this.tickCount % 12 == 0) {
                        float var76 = var8 instanceof ServerPlayer ? 12.0F : 28.0F;
                        ((LivingEntity)var8).hurt(this.damageSources().mobAttack(this), var76);
                        if (!(var8 instanceof ServerPlayer)) {
                           float var17 = ((LivingEntity)var8).getHealth() - 18.0F;
                           if (var17 <= 0.0F) {
                              ((LivingEntity)var8).discard();
                           } else {
                              ((LivingEntity)var8).setHealth(var17);
                           }
                        }

                        pr(var1, var2, ParticleTypes.CLOUD, ((LivingEntity)var8).getX(), ((LivingEntity)var8).getY() + (double)1.0F, ((LivingEntity)var8).getZ(), 8, 0.3, 0.3, 0.3, 0.08);
                        pr(var1, var2, ParticleTypes.END_ROD, ((LivingEntity)var8).getX(), ((LivingEntity)var8).getY() + (double)1.0F, ((LivingEntity)var8).getZ(), 5, 0.2, 0.2, 0.2, 0.05);
                        ses(var1, this.getX(), this.getY(), this.getZ(), SoundEvents.STONE_PLACE, 1.8F, 0.6F);
                     }

                     return;
                  }
               }

               if (this.rol == 10) {
                  if (this.getPoz() != 10) {
                     this.setPoz(10);
                  }

                  double var49 = (double)this.tickCount * 0.032 + var6;
                  double var59 = (double)16.0F + (double)(Math.abs(var5) % 14);
                  double var65 = (double)14.0F + (double)(Math.abs(var5 >> 4) % 12);
                  double var72 = (double)this.kx + (double)0.5F + Math.cos(var49) * var59;
                  double var16 = (double)this.kz + (double)0.5F + Math.sin(var49 * (double)1.5F) * var65;
                  double var18 = (double)this.ky + Math.sin(var49 * 2.2) * 3.4 + Math.cos(var49 * 0.8) * 1.2;
                  double var20 = var72 - this.getX();
                  double var22 = var18 - this.getY();
                  double var24 = var16 - this.getZ();
                  float var26 = (float)(Mth.atan2(var24, var20) * (180D / Math.PI)) - 90.0F;
                  float var27 = (float)(-(Mth.atan2(var22, Math.sqrt(var20 * var20 + var24 * var24)) * (180D / Math.PI)));
                  var27 = Mth.clamp(var27, -28.0F, 28.0F);
                  double var28 = this.getX() + var20 * 0.18;
                  double var30 = this.getY() + var22 * 0.18;
                  double var32 = this.getZ() + var24 * 0.18;
                  if (yurumeBosMu(var1, BlockPos.containing(var28, var30, var32)) && yurumeBosMu(var1, BlockPos.containing(var28, var30 + (double)1.5F, var32))) {
                     this.moveTo(var28, var30, var32, var26, var27);
                  }

                  this.setYRot(var26);
                  this.setYBodyRot(var26);
                  this.setYHeadRot(var26);
                  this.setXRot(var27);
                  if (this.tickCount % 10 == 0 && var3 < (double)3600.0F) {
                     pr(var1, var2, ParticleTypes.END_ROD, this.getX(), this.getY() + 1.4, this.getZ(), 1, (double)0.25F, 0.15, (double)0.25F, 0.01);
                  }

               } else if (this.rol == 11) {
                  if (this.getPoz() != 11) {
                     this.setPoz(11);
                  }

                  this.duvardanKurtar(var1, this.ky);
                  double var48 = 0.65 + Math.sin((double)this.tickCount * 0.08 + var6) * 0.22;
                  if (var3 < (double)64.0F) {
                     bak(this, var2);
                     this.moveTo(this.getX(), (double)this.ky + var48, this.getZ(), this.getYRot(), 0.0F);
                  } else {
                     if (this.devriyeAci == (double)0.0F) {
                        this.devriyeAci = var6;
                     }

                     this.devriyeAci += 0.022 * (double)this.devriyeYon;
                     double var58 = (double)5.0F + (double)(Math.abs(var5) % 5);
                     double var64 = (double)this.kx + (double)0.5F + Math.cos(this.devriyeAci) * var58;
                     double var71 = (double)this.kz + (double)0.5F + Math.sin(this.devriyeAci) * var58;
                     this.engelsizYuru(var1, var64, var71, 0.12, var48);
                  }

               } else {
                  this.duvardanKurtar(var1, this.ky);
                  int var47 = (this.tickCount + Math.abs(var5)) % 180;
                  boolean var54 = var3 < (double)36.0F;
                  if (!var54 && var47 < 125) {
                     this.setPoz(this.rol);
                     if (this.devriyeAci == (double)0.0F) {
                        this.devriyeAci = (double)(var5 & 511) * 0.024;
                     }

                     this.devriyeAci += 0.024 * (double)this.devriyeYon;
                     double var57 = (double)4.5F + (double)(Math.abs(var5) % 6);
                     double var63 = (double)this.kx + (double)0.5F + Math.cos(this.devriyeAci) * var57;
                     double var70 = (double)this.kz + (double)0.5F + Math.sin(this.devriyeAci * 1.2) * var57;
                     this.engelsizYuru(var1, var63, var70, 0.13, (double)0.0F);
                  } else {
                     this.setPoz(this.rol + 2);
                     if (var3 < (double)100.0F) {
                        bak(this, var2);
                     }
                  }

               }
            }
         } else {
            if (this.tickCount > 40) {
               this.discard();
            }

         }
      }
   }

   public static boolean yurumeBosMu(ServerLevel var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      if (!var2.isAir() && !var2.is(Blocks.LIGHT)) {
         return var2.is(Blocks.RED_CARPET) || var2.is(Blocks.YELLOW_CARPET) || var2.is(Blocks.GREEN_CARPET) || var2.is(Blocks.AZURE_BLUET) || var2.is(Blocks.OXEYE_DAISY) || var2.is(Blocks.PINK_TULIP) || var2.is(Blocks.WHITE_TULIP) || var2.is(Blocks.CORNFLOWER) || var2.is(Blocks.ALLIUM) || var2.is(Blocks.LILY_OF_THE_VALLEY);
      } else {
         return true;
      }
   }

   public static boolean npcKonumAcikMi(ServerLevel var0, double var1, int var3, double var4) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
      int var7 = Mth.floor(var1);
      int var8 = Mth.floor(var4);
      BlockState var9 = var0.getBlockState(var6.set(var7, var3 - 1, var8));
      if (!yurumeBosMu(var0, var6.set(var7, var3 - 1, var8)) && var9.getFluidState().isEmpty()) {
         double var10 = 0.38;

         for(double var15 : new double[]{-var10, var10}) {
            for(double var20 : new double[]{-var10, var10}) {
               int var22 = Mth.floor(var1 + var15);
               int var23 = Mth.floor(var4 + var20);
               if (!yurumeBosMu(var0, var6.set(var22, var3, var23)) || !yurumeBosMu(var0, var6.set(var22, var3 + 1, var23))) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void duvardanKurtar(ServerLevel var1, int var2) {
      if (this.tickCount % 15 == 1) {
         int var3 = var2 > 40 ? var2 : Mth.floor(this.getY());
         if (!npcKonumAcikMi(var1, this.getX(), var3, this.getZ())) {
            int var4 = Mth.floor(this.getX());
            int var5 = Mth.floor(this.getZ());

            for(int var6 = 1; var6 <= 6; ++var6) {
               for(int var7 = -var6; var7 <= var6; ++var7) {
                  for(int var8 = -var6; var8 <= var6; ++var8) {
                     if (Math.abs(var7) == var6 || Math.abs(var8) == var6) {
                        for(int var9 = -1; var9 <= 1; ++var9) {
                           int var10 = var3 + var9;
                           if (npcKonumAcikMi(var1, (double)(var4 + var7) + (double)0.5F, var10, (double)(var5 + var8) + (double)0.5F)) {
                              this.ky = var10;
                              this.kx = var4 + var7;
                              this.kz = var5 + var8;
                              this.moveTo((double)(var4 + var7) + (double)0.5F, (double)var10, (double)(var5 + var8) + (double)0.5F, this.getYRot(), 0.0F);
                              return;
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   private void engelsizYuru(ServerLevel var1, double var2, double var4, double var6, double var8) {
      double var10 = this.getX();
      double var12 = this.getZ();
      double var14 = var2 - var10;
      double var16 = var4 - var12;
      double var18 = Math.sqrt(var14 * var14 + var16 * var16);
      if (!(var18 < 0.05)) {
         double var20 = Math.min(var6, var18);
         double var22 = var14 / var18 * var20;
         double var24 = var16 / var18 * var20;
         float var26 = (float)(Mth.atan2(var16, var14) * (180D / Math.PI)) - 90.0F;
         int var27 = this.ky > 40 ? this.ky : Mth.floor(this.getY() - var8);

         for(int var31 : new int[]{0, 1, -1}) {
            int var32 = var27 + var31;
            if (npcKonumAcikMi(var1, var10 + var22, var32, var12 + var24)) {
               this.ky = var32;
               this.moveTo(var10 + var22, (double)var32 + var8, var12 + var24, var26, 0.0F);
               this.setYRot(var26);
               this.setYBodyRot(var26);
               this.setYHeadRot(var26);
               return;
            }
         }

         if (Math.abs(var22) > 0.01 && npcKonumAcikMi(var1, var10 + var22, var27, var12)) {
            this.moveTo(var10 + var22, (double)var27 + var8, var12, var26, 0.0F);
            this.setYRot(var26);
            this.setYBodyRot(var26);
            this.setYHeadRot(var26);
         } else if (Math.abs(var24) > 0.01 && npcKonumAcikMi(var1, var10, var27, var12 + var24)) {
            this.moveTo(var10, (double)var27 + var8, var12 + var24, var26, 0.0F);
            this.setYRot(var26);
            this.setYBodyRot(var26);
            this.setYHeadRot(var26);
         } else {
            this.devriyeYon = -this.devriyeYon;
            this.devriyeAci += 0.45 * (double)this.devriyeYon;
            float var33 = var26 + 180.0F;
            this.setYRot(var33);
            this.setYBodyRot(var33);
            this.setYHeadRot(var33);
         }
      }
   }

   private static int bataklikYuzeyBul(ServerLevel var0, int var1, int var2, int var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();

      for(int var5 = 76; var5 >= 66; --var5) {
         BlockState var6 = var0.getBlockState(var4.set(var1, var5 - 1, var3));
         if (var6.is(Blocks.MUD) || var6.is(Blocks.POLISHED_DEEPSLATE) || var6.is(Blocks.DEEPSLATE_BRICKS) || var6.is(Blocks.DEEPSLATE_TILES)) {
            return var5;
         }
      }

      return 71;
   }

   private static int zeminBul(ServerLevel var0, int var1, int var2, int var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();

      for(int var5 = 4; var5 >= -4; --var5) {
         var4.set(var1, var2 + var5, var3);
         BlockState var6 = var0.getBlockState(var4);
         if (!var6.isAir() && var6.getFluidState().isEmpty()) {
            var4.set(var1, var2 + var5 + 1, var3);
            if (var0.getBlockState(var4).isAir()) {
               return var2 + var5 + 1;
            }
         }
      }

      return var2;
   }

   public static void bolgeNpcKontrol(ServerLevel var0, ServerPlayer var1) {
      double var2 = var1.getX();
      double var4 = var1.getY();
      double var6 = var1.getZ();
      List var8 = var0.getEntitiesOfClass(AngelSoldier.class, var1.getBoundingBox().inflate((double)85.0F), (var0x) -> var0x.getRol() >= 10 && var0x.getRol() < 50);
      int var9 = var8 == null ? 0 : var8.size();
      if (var9 < 4) {
         boolean var10 = var0.dimension().equals(NurSistemi.KRALLIK);
         boolean var11 = var0.dimension().equals(YarikAsasi.CENNET);
         int var12 = -1;
         boolean var13 = false;
         if (var10) {
            double var14 = Math.sqrt(var2 * var2 + var6 * var6);
            if (var14 <= (double)310.0F) {
               var12 = (var1.tickCount & 1) == 0 ? 10 : 11;
               var13 = var12 == 10;
            } else if (var2 <= (double)-750.0F && var6 <= (double)650.0F && var14 >= (double)1500.0F) {
               for(int var28 = 0; var28 < 56; ++var28) {
                  double var17 = var2 - (double)NurBaskentFeature.SCX[var28];
                  double var19 = var6 - (double)NurBaskentFeature.SCZ[var28];
                  if (var17 * var17 + var19 * var19 <= (double)((NurBaskentFeature.SR[var28] + 22) * (NurBaskentFeature.SR[var28] + 22))) {
                     var12 = (var28 + var1.tickCount) % 3 == 0 ? 11 : 10;
                     var13 = var12 == 10;
                     break;
                  }
               }
            } else if (var2 >= (double)900.0F && var6 <= (double)650.0F && var14 >= (double)1520.0F && var14 <= (double)2700.0F) {
               double var16 = Math.sqrt((var2 - (double)1620.0F) * (var2 - (double)1620.0F) + (var6 - (double)0.0F) * (var6 - (double)0.0F));
               double var18 = Math.sqrt((var2 - (double)1180.0F) * (var2 - (double)1180.0F) + (var6 - (double)-1180.0F) * (var6 - (double)-1180.0F));
               boolean var20 = Math.min(var16, var18) <= (double)420.0F;
               var12 = var20 ? 21 : 20;
            }
         } else if (var11) {
            double var26 = Math.sqrt((var2 - (double)-2000.0F) * (var2 - (double)-2000.0F) + var6 * var6);
            if (var26 <= (double)355.0F) {
               var12 = 30;
            } else if (var2 >= (double)1520.0F && var2 <= (double)2680.0F && Math.abs(var6) <= (double)550.0F) {
               var12 = 40;
            }
         }

         if (var12 >= 0) {
            int var27 = var1.tickCount * 31 + 997 ^ Mth.floor(var2 * (double)13.0F + var6 * (double)17.0F);
            double var15 = (double)(var27 & 1023) / (double)1024.0F * Math.PI * (double)2.0F;
            double var29 = (double)14.0F + (double)(Math.abs(var27 >> 10) % 24);
            int var30 = Mth.floor(var2 + Math.cos(var15) * var29);
            int var31 = Mth.floor(var6 + Math.sin(var15) * var29);
            BlockPos var21 = var0.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing((double)var30, var4, (double)var31));
            int var22 = var21.getY();
            if (var22 < 60 || var22 > 235) {
               var22 = Mth.floor(var4);
            }

            AngelSoldier var23 = (AngelSoldier)((EntityType)CennetMod.MELEK.get()).create(var0);
            if (var23 != null) {
               if (var13) {
                  int var24 = var22 + 12 + Math.abs(var27 >> 5) % 14;
                  var23.moveTo((double)var30 + (double)0.5F, (double)var24, (double)var31 + (double)0.5F, 0.0F, 0.0F);
                  var23.setNpcRol(10, var30, var24, var31, var1.getUUID());
               } else {
                  double var32 = var12 == 11 ? 0.65 : (double)0.0F;
                  var23.moveTo((double)var30 + (double)0.5F, (double)var22 + var32, (double)var31 + (double)0.5F, 0.0F, 0.0F);
                  var23.setNpcRol(var12, var30, var22, var31, var1.getUUID());
               }

               var0.addFreshEntity(var23);
            }
         }
      }
   }

   public static void kapiKur(ServerLevel var0, ServerPlayer var1) {
      int var2 = Mth.floor(var1.getX());
      int var3 = Mth.floor(var1.getY());
      int var4 = Mth.floor(var1.getZ()) - 36;
      AngelSoldier var5 = (AngelSoldier)((EntityType)CennetMod.MELEK.get()).create(var0);
      AngelSoldier var6 = (AngelSoldier)((EntityType)CennetMod.MELEK.get()).create(var0);
      if (var5 != null && var6 != null) {
         var5.moveTo((double)var2 + (double)0.5F - (double)11.0F, (double)var3, (double)var4 + (double)6.5F, 0.0F, 0.0F);
         var6.moveTo((double)var2 + (double)0.5F + (double)11.0F, (double)var3, (double)var4 + (double)6.5F, 0.0F, 0.0F);
         var5.rol = 0;
         var6.rol = 1;
         var5.kx = var6.kx = var2;
         var5.ky = var6.ky = var3;
         var5.kz = var6.kz = var4;
         var5.esUuid = var6.getUUID();
         var6.esUuid = var5.getUUID();
         NurKapi.Kapi var7 = new NurKapi.Kapi(var0, var2, var3, var4, var1.getUUID());
         var7.lider = var5.getUUID();
         var7.es = var6.getUUID();
         NurKapi.KAPILAR.put(var1.getUUID(), var7);
         var5.kapi = var7;
         var6.kapi = var7;
         var0.addFreshEntity(var5);
         var0.addFreshEntity(var6);
         var5.baslat(var1, true);
      }
   }

   public void baslat(ServerPlayer var1, boolean var2) {
      this.oyuncu = var1.getUUID();
      this.mesgul = true;
      this.insaEdilecek = var2;
      this.zaman = var2 ? 0 : 135;
      this.plazaSatir = Math.max(8, (int)Math.ceil(var1.getZ()) - (this.kz + 5) + 3);
      if (var2) {
         this.setInvisible(true);
         AngelSoldier var3 = this.esBul((ServerLevel)this.level());
         if (var3 != null) {
            var3.setInvisible(true);
         }
      }

   }

   private void yaz(ServerLevel var1, BlockPos var2, BlockState var3) {
      if (this.kapi != null) {
         NurKapi.yaz(this.kapi, var2, var3);
      } else {
         var1.setBlock(var2, var3, 18);
      }

   }

   private AngelSoldier esBul(ServerLevel var1) {
      if (this.esUuid == null) {
         return null;
      } else {
         Entity var2 = var1.getEntity(this.esUuid);
         return var2 instanceof AngelSoldier ? (AngelSoldier)var2 : null;
      }
   }

   private void yetimTemizle() {
      if (this.level() instanceof ServerLevel) {
         ServerLevel var1 = (ServerLevel)this.level();
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();

         for(int var3 = -48; var3 <= 48; ++var3) {
            for(int var4 = -1; var4 <= 5; ++var4) {
               for(int var5 = 0; var5 <= 58; ++var5) {
                  var2.set(this.kx + var3, this.ky + var5, this.kz + var4);
                  if (!var1.getBlockState(var2).isAir()) {
                     this.yaz(var1, var2, AIR);
                  }
               }
            }
         }

         AngelSoldier var6 = this.esBul(var1);
         if (var6 != null) {
            var6.discard();
         }

         this.discard();
      }
   }

   private void sahne() {
      ServerLevel var1 = (ServerLevel)this.level();
      ServerPlayer var2 = this.oyuncu == null ? null : var1.getServer().getPlayerList().getPlayer(this.oyuncu);
      if (var2 != null && var2.isAlive() && var2.level() == var1 && !(var2.distanceToSqr(this.getX(), this.getY(), this.getZ()) > (double)10000.0F)) {
         AngelSoldier var3 = this.esBul(var1);
         if (!this.isInvisible()) {
            bak(this, var2);
            if (var3 != null) {
               bak(var3, var2);
            }
         }

         if (this.insaEdilecek && this.zaman < 135) {
            this.insa(var1, var2, var3, this.zaman);
         } else {
            this.diyalog(var1, var2, var3, this.zaman - 135);
         }

         ++this.zaman;
      } else {
         this.acilDurum();
      }
   }

   private static void bak(AngelSoldier var0, Entity var1) {
      double var2 = var1.getX() - var0.getX();
      double var4 = var1.getZ() - var0.getZ();
      double var6 = var1.getY() + 1.6 - (var0.getY() + 1.6);
      float var8 = (float)(Mth.atan2(var4, var2) * (180D / Math.PI)) - 90.0F;
      float var9 = (float)(-(Mth.atan2(var6, Math.sqrt(var2 * var2 + var4 * var4)) * (180D / Math.PI)));
      var0.setYRot(var8);
      var0.setYBodyRot(var8);
      var0.setYHeadRot(var8);
      var0.setXRot(Mth.clamp(var9, -30.0F, 30.0F));
   }

   private static void pr(ServerLevel var0, ServerPlayer var1, ParticleOptions var2, double var3, double var5, double var7, int var9, double var10, double var12, double var14, double var16) {
      var0.sendParticles(var1, var2, true, var3, var5, var7, var9, var10, var12, var14, var16);
   }

   private static void ses(ServerLevel var0, double var1, double var3, double var5, SoundEvent var7, float var8, float var9) {
      var0.playSound((Player)null, BlockPos.containing(var1, var3, var5), var7, SoundSource.NEUTRAL, var8, var9);
   }

   private void soyle(ServerLevel var1, ServerPlayer var2, AngelSoldier var3, String var4) {
      var2.sendSystemMessage(Component.literal("<Melek Muhafız> ").withStyle(ChatFormatting.GOLD).append(Component.literal(var4).withStyle(ChatFormatting.WHITE)));
      AngelSoldier var5 = var3 == null ? this : var3;
      ses(var1, var5.getX(), var5.getY() + (double)5.0F, var5.getZ(), SoundEvents.ALLAY_AMBIENT_WITH_ITEM, 3.0F, 0.5F);
   }

   private Vec3 el(AngelSoldier var1) {
      float var2 = var1.getYRot() * ((float)Math.PI / 180F);
      return new Vec3(var1.getX() - (double)Mth.sin(var2) * 1.4, var1.getY() + 3.3, var1.getZ() + (double)Mth.cos(var2) * 1.4);
   }

   private Vec3 oyuncuEl(ServerPlayer var1) {
      Vec3 var2 = var1.getLookAngle();
      return var1.getEyePosition().add(var2.x * 0.6, -0.4, var2.z * 0.6);
   }

   private void ucur(Vec3 var1, Vec3 var2, double var3, ServerLevel var5, ServerPlayer var6) {
      if (this.ucanKart != null) {
         double var7 = var3 * var3 * ((double)3.0F - (double)2.0F * var3);
         double var9 = var1.x + (var2.x - var1.x) * var7;
         double var11 = var1.y + (var2.y - var1.y) * var7 + Math.sin(Math.PI * var7) * 0.8;
         double var13 = var1.z + (var2.z - var1.z) * var7;
         this.ucanKart.setPos(var9, var11, var13);
         this.ucanKart.setDeltaMovement(Vec3.ZERO);
         pr(var5, var6, ParticleTypes.END_ROD, var9, var11 + 0.2, var13, 2, 0.1, 0.1, 0.1, 0.01);
      }
   }

   private void kartiAl(ServerLevel var1, ServerPlayer var2) {
      this.emanet = ItemStack.EMPTY;
      this.geriVer = false;

      for(int var3 = 0; var3 < var2.getInventory().getContainerSize(); ++var3) {
         ItemStack var4 = var2.getInventory().getItem(var3);
         if (var4.is((Item)CennetMod.NUR_KARTI.get())) {
            this.emanet = var4.copyWithCount(1);
            var4.shrink(1);
            this.geriVer = true;
            break;
         }
      }

      if (this.emanet.isEmpty()) {
         this.emanet = new ItemStack((ItemLike)CennetMod.NUR_KARTI.get());
      }

      Vec3 var5 = this.oyuncuEl(var2);
      this.ucanKart = new ItemEntity(var1, var5.x, var5.y, var5.z, this.emanet.copy());
      this.ucanKart.setNoGravity(true);
      this.ucanKart.setPickUpDelay(32767);
      this.ucanKart.setUnlimitedLifetime();
      this.ucanKart.setInvulnerable(true);
      this.ucanKart.setDeltaMovement(Vec3.ZERO);
      var1.addFreshEntity(this.ucanKart);
   }

   private void kartiVer(ServerLevel var1, ServerPlayer var2) {
      if (!this.emanet.isEmpty() && this.geriVer) {
         if (var2 != null && var2.isAlive()) {
            if (!var2.getInventory().add(this.emanet.copy())) {
               var2.drop(this.emanet.copy(), false);
            }
         } else {
            var1.addFreshEntity(new ItemEntity(var1, this.getX(), this.getY() + (double)1.0F, this.getZ(), this.emanet.copy()));
         }
      }

      this.emanet = ItemStack.EMPTY;
      this.geriVer = false;
      if (this.ucanKart != null) {
         this.ucanKart.discard();
         this.ucanKart = null;
      }

   }

   private void acilDurum() {
      if (this.level() instanceof ServerLevel) {
         ServerLevel var1 = (ServerLevel)this.level();
         ServerPlayer var2 = this.oyuncu == null ? null : var1.getServer().getPlayerList().getPlayer(this.oyuncu);
         this.kartiVer(var1, var2);
         AngelSoldier var3 = this.esBul(var1);
         if (var3 != null) {
            var3.setPoz(0);
            if (var3.isInvisible() && !this.insaEdilecek) {
               var3.setInvisible(false);
            }
         }
      }

      this.setPoz(0);
      this.mesgul = false;
      this.zaman = 0;
   }

   private void insa(ServerLevel var1, ServerPlayer var2, AngelSoldier var3, int var4) {
      double var5 = (double)this.kx + (double)0.5F;
      if (var4 == 0) {
         ses(var1, var5, (double)this.ky, (double)this.kz, SoundEvents.LIGHTNING_BOLT_THUNDER, 3.0F, 0.6F);
      }

      if (var4 < 62 && var4 % 2 == 0) {
         for(int var7 = 0; var7 < 3; ++var7) {
            pr(var1, var2, ParticleTypes.CLOUD, var5, (double)(this.ky + 2 + var7 * 6), (double)this.kz + (double)2.5F, 70, (double)22.0F, (double)2.5F, (double)3.0F, 0.02);
         }
      }

      if (var4 >= 20 && var4 < 20 + this.plazaSatir) {
         this.plazaSatiri(var1, this.kz + 5 + (var4 - 20));
      }

      if (var4 >= 50 && var4 < 118) {
         int var8 = -8 + (var4 - 50);
         this.katman(var1, var8);
         pr(var1, var2, ParticleTypes.CLOUD, var5, (double)(this.ky + var8 + 1), (double)this.kz + (double)2.5F, 60, (double)26.0F, (double)1.5F, (double)3.0F, 0.03);
         if (var4 % 5 == 0) {
            ses(var1, var5, (double)(this.ky + var8), (double)this.kz, SoundEvents.STONE_PLACE, 3.0F, 0.5F);
         }

         if (var4 % 12 == 0) {
            ses(var1, var5, (double)(this.ky + var8), (double)this.kz, SoundEvents.PISTON_EXTEND, 2.0F, 0.5F);
         }
      }

      if (var4 >= 118 && var4 < 134) {
         pr(var1, var2, ParticleTypes.CLOUD, var5, (double)(this.ky + 25), (double)this.kz + (double)2.5F, 120, (double)30.0F, (double)15.0F, (double)3.0F, 0.05);
      }

      if (var4 == 120) {
         this.setInvisible(false);
         if (var3 != null) {
            var3.setInvisible(false);
         }

         pr(var1, var2, ParticleTypes.END_ROD, this.getX(), this.getY() + (double)3.0F, this.getZ(), 80, 1.2, (double)3.0F, 1.2, 0.15);
         if (var3 != null) {
            pr(var1, var2, ParticleTypes.END_ROD, var3.getX(), var3.getY() + (double)3.0F, var3.getZ(), 80, 1.2, (double)3.0F, 1.2, 0.15);
         }

         ses(var1, var5, (double)(this.ky + 3), (double)(this.kz + 6), SoundEvents.BEACON_ACTIVATE, 3.0F, 0.8F);
      }

   }

   private void katman(ServerLevel var1, int var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();

      for(int var4 = -48; var4 <= 48; ++var4) {
         for(int var5 = -1; var5 <= 5; ++var5) {
            BlockState var6 = blok(var4, var2, var5);
            if (var6 != null) {
               var3.set(this.kx + var4, this.ky + var2, this.kz + var5);
               this.yaz(var1, var3, var6);
            }
         }
      }

   }

   private static BlockState blok(int var0, int var1, int var2) {
      int var3 = Math.abs(var0);
      if (var3 <= 7 && var1 >= 0 && var1 <= 26 && var2 >= 0 && var2 <= 4) {
         return var2 == 2 ? GLASS : AIR;
      } else if (var3 >= 8 && var3 <= 9 && var1 >= 0 && var1 <= 28 && var2 >= 0 && var2 <= 4) {
         return var3 == 8 ? GOLD : PILLAR;
      } else if (var3 <= 9 && var1 >= 27 && var1 <= 28 && var2 >= 0 && var2 <= 4) {
         return var1 == 27 ? GOLD : PILLAR;
      } else if (var3 >= 13 && var3 <= 19 && var2 >= -1 && var2 <= 5 && var1 >= -8 && var1 <= 58) {
         if (var1 == 58) {
            return GOLD;
         } else {
            boolean var4 = (var3 == 13 || var3 == 19) && (var2 == -1 || var2 == 5);
            return var4 ? PILLAR : SMOOTH;
         }
      } else if (var3 <= 40 && var2 >= 0 && var2 <= 4 && var1 >= -8 && var1 <= 44) {
         return var2 != 4 || var1 != 20 && var1 != 40 ? SMOOTH : GOLD;
      } else {
         return var3 <= 40 && var2 >= 0 && var2 <= 4 && var1 == 45 && var3 % 2 == 0 ? SMOOTH : null;
      }
   }

   private void plazaSatiri(ServerLevel var1, int var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();

      for(int var4 = -12; var4 <= 12; ++var4) {
         int var5 = this.kx + var4;

         for(int var6 = this.ky; var6 <= this.ky + 14; ++var6) {
            var3.set(var5, var6, var2);
            if (!var1.getBlockState(var3).isAir()) {
               this.yaz(var1, var3, AIR);
            }
         }

         var3.set(var5, this.ky - 1, var2);
         BlockState var9 = Math.abs(var4) <= 1 ? GOLD : ((var4 + var2 & 1) == 0 ? SMOOTH : BRICKS);
         this.yaz(var1, var3, var9);

         for(int var7 = this.ky - 6; var7 <= this.ky - 2; ++var7) {
            var3.set(var5, var7, var2);
            BlockState var8 = var1.getBlockState(var3);
            if (var8.isAir() || !var8.getFluidState().isEmpty() || var8.canBeReplaced()) {
               this.yaz(var1, var3, SMOOTH);
            }
         }
      }

   }

   private void kapiAc(ServerLevel var1, ServerPlayer var2, int var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();

      for(int var5 = -1; var5 <= 1; var5 += 2) {
         int var6 = var3 * var5;
         if (var3 != 0 || var5 != 1) {
            for(int var7 = 0; var7 <= 26; ++var7) {
               var4.set(this.kx + var6, this.ky + var7, this.kz + 2);
               if (var1.getBlockState(var4).is(Blocks.WHITE_STAINED_GLASS)) {
                  this.yaz(var1, var4, AIR);
                  if (var7 % 3 == 0) {
                     pr(var1, var2, ParticleTypes.END_ROD, (double)var4.getX() + (double)0.5F, (double)var4.getY() + (double)0.5F, (double)var4.getZ() + (double)0.5F, 3, 0.3, 0.3, 0.3, 0.05);
                  }
               }
            }
         }
      }

   }

   private void diyalog(ServerLevel var1, ServerPlayer var2, AngelSoldier var3, int var4) {
      String var5 = var2.getName().getString();
      if (var4 < 345 && var4 % 10 == 0) {
         var2.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 6, false, false, false));
      }

      if (var4 == 10) {
         this.setPoz(1);
         this.soyle(var1, var2, this, "Dur, yolcu! Burası Cennet'in başkentinin kapısıdır.");
      }

      if (var4 == 55) {
         this.soyle(var1, var2, this, "Giriş kimliğini göster.");
      }

      if (var4 == 90) {
         this.kartiAl(var1, var2);
         ses(var1, this.getX(), this.getY() + (double)3.0F, this.getZ(), SoundEvents.ALLAY_ITEM_TAKEN, 3.0F, 0.7F);
      }

      if (var4 >= 90 && var4 < 130) {
         this.ucur(this.oyuncuEl(var2), this.el(this), (double)(var4 - 90) / (double)40.0F, var1, var2);
      }

      if (var4 == 130) {
         this.setPoz(0);
         this.soyle(var1, var2, this, "Hımm... Bir de kardeşim bakmalı.");
      }

      if (var4 >= 150 && var4 < 180 && var3 != null) {
         this.ucur(this.el(this), this.el(var3), (double)(var4 - 150) / (double)30.0F, var1, var2);
      }

      if (var4 == 180 && var3 != null) {
         var3.setPoz(2);
      }

      if (var4 >= 180 && var4 < 270 && var3 != null) {
         Vec3 var6 = this.el(var3).add((double)0.0F, 0.3, (double)0.0F);
         if (this.ucanKart != null) {
            this.ucanKart.setPos(var6.x, var6.y + Math.sin((double)var4 * 0.12) * 0.06, var6.z);
            this.ucanKart.setDeltaMovement(Vec3.ZERO);
         }

         pr(var1, var2, ParticleTypes.ENCHANT, var6.x, var6.y, var6.z, 6, (double)0.5F, (double)0.5F, (double)0.5F, 0.6);
         double var7 = var6.y - 1.2 + (Math.sin((double)var4 * (double)0.25F) + (double)1.0F) * 1.2;
         pr(var1, var2, ParticleTypes.END_ROD, var6.x, var7, var6.z, 6, 0.6, (double)0.0F, 0.1, (double)0.0F);
         if (var4 % 15 == 0) {
            ses(var1, var6.x, var6.y, var6.z, SoundEvents.AMETHYST_BLOCK_CHIME, 3.0F, 0.8F + (float)(var4 % 45) * 0.01F);
         }
      }

      if (var4 == 195 && var3 != null) {
         this.soyle(var1, var2, var3, "Kimlik doğrulanıyor...");
      }

      if (var4 == 250 && var3 != null) {
         this.soyle(var1, var2, var3, "Mühür gerçek. Her şey yolunda.");
      }

      if (var4 == 270 && var3 != null) {
         var3.setPoz(0);
      }

      if (var4 >= 270 && var4 < 300 && var3 != null) {
         this.ucur(this.el(var3), this.el(this), (double)(var4 - 270) / (double)30.0F, var1, var2);
      }

      if (var4 == 300) {
         this.setPoz(1);
      }

      if (var4 >= 305 && var4 < 345) {
         this.ucur(this.el(this), this.oyuncuEl(var2), (double)(var4 - 305) / (double)40.0F, var1, var2);
      }

      if (var4 == 345) {
         this.kartiVer(var1, var2);
         ses(var1, var2.getX(), var2.getY() + (double)1.0F, var2.getZ(), SoundEvents.ALLAY_ITEM_GIVEN, 3.0F, 0.8F);
         this.setPoz(3);
         this.soyle(var1, var2, this, "Cennetin başkentine hoş geldiniz, " + var5 + ". Burası daha önce görmediğiniz kadar güzel bir yerdir. Mutlaka hoşunuza gider.");
      }

      if (var4 == 400) {
         var2.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 25));
         var2.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("Hoş geldin, " + var5).withStyle(ChatFormatting.YELLOW)));
         var2.connection.send(new ClientboundSetTitleTextPacket(Component.literal("CENNETİN BAŞKENTİ").withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.BOLD})));
         ses(var1, (double)this.kx + (double)0.5F, (double)(this.ky + 3), (double)(this.kz + 2), SoundEvents.BELL_RESONATE, 4.0F, 0.7F);
         ses(var1, (double)this.kx + (double)0.5F, (double)(this.ky + 3), (double)(this.kz + 2), SoundEvents.BEACON_POWER_SELECT, 3.0F, 0.8F);
      }

      if (var4 >= 400 && var4 < 432 && (var4 - 400) % 4 == 0) {
         this.kapiAc(var1, var2, (var4 - 400) / 4);
      }

      if (var4 >= 432) {
         NurSistemi.kapidanGecisKontrol(var2);
      }

      if (var4 == 470) {
         this.setPoz(0);
         if (var3 != null) {
            var3.setPoz(0);
         }

         this.mesgul = false;
         this.zaman = 0;
      }

   }

   public static int pegasusGuvenliZeminBul(ServerLevel var0, int var1, int var2, int var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      int var5 = Math.max(50, Math.min(220, var2 + 8));
      int var6 = Math.max(40, var2 - 24);

      for(int var7 = var5; var7 >= var6; --var7) {
         BlockState var8 = var0.getBlockState(var4.set(var1, var7 - 1, var3));
         if (!gecilirMi(var8)) {
            BlockState var9 = var0.getBlockState(var4.set(var1, var7, var3));
            BlockState var10 = var0.getBlockState(var4.set(var1, var7 + 1, var3));
            if (gecilirMi(var9) && gecilirMi(var10)) {
               return var7;
            }
         }
      }

      for(int var11 = 175; var11 >= 42; --var11) {
         BlockState var12 = var0.getBlockState(var4.set(var1, var11 - 1, var3));
         if (!gecilirMi(var12)) {
            BlockState var13 = var0.getBlockState(var4.set(var1, var11, var3));
            if (gecilirMi(var13)) {
               return var11;
            }
         }
      }

      return Math.max(64, var2);
   }

   private static boolean gecilirMi(BlockState var0) {
      if (!var0.isAir() && var0.getFluidState().isEmpty()) {
         if (!var0.is((Block)CennetMod.KARANLIK_YAPRAK.get()) && !var0.is((Block)CennetMod.KARANLIK_GOVDE.get())) {
            return !var0.is(Blocks.OAK_LEAVES) && !var0.is(Blocks.BIRCH_LEAVES) && !var0.is(Blocks.CHERRY_LEAVES) && !var0.is(Blocks.AZALEA_LEAVES) && !var0.is(Blocks.FLOWERING_AZALEA_LEAVES) && !var0.is(Blocks.AZURE_BLUET) && !var0.is(Blocks.OXEYE_DAISY) && !var0.is(Blocks.PINK_TULIP) && !var0.is(Blocks.WHITE_TULIP) && !var0.is(Blocks.CORNFLOWER) && !var0.is(Blocks.ALLIUM) && !var0.is(Blocks.LILY_OF_THE_VALLEY) ? var0.getCollisionShape((BlockGetter)null, BlockPos.ZERO).isEmpty() : true;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public static AngelSoldier pegasusCagir(ServerLevel var0, ServerPlayer var1) {
      if (!var0.dimension().equals(YarikAsasi.CENNET)) {
         return null;
      } else {
         List var2 = var0.getEntitiesOfClass(AngelSoldier.class, var1.getBoundingBox().inflate((double)250.0F), (var1x) -> (var1x.getRol() == 50 || var1x.getPoz() >= 50 && var1x.getPoz() < 60) && (var1.getUUID().equals(var1x.oyuncu) || !var1x.isVehicle()));
         if (var2 != null) {
            for(AngelSoldier var4 : var2) {
               var4.discard();
            }
         }

         AngelSoldier var21 = (AngelSoldier)((EntityType)CennetMod.MELEK.get()).create(var0);
         if (var21 == null) {
            return null;
         } else {
            float var22 = var1.getYRot() * ((float)Math.PI / 180F);
            double var5 = (double)(-Mth.sin(var22));
            double var7 = (double)Mth.cos(var22);
            double var9 = var1.getX() + var5 * (double)5.5F;
            double var11 = var1.getZ() + var7 * (double)5.5F;
            double var13 = (double)pegasusGuvenliZeminBul(var0, Mth.floor(var9), Mth.floor(var1.getY()), Mth.floor(var11));
            double var15 = var1.getX() + var5 * (double)36.0F;
            double var17 = var13 + (double)24.0F;
            double var19 = var1.getZ() + var7 * (double)36.0F;
            var21.setNpcRol(50, (int)Math.round(var9), (int)Math.round(var13), (int)Math.round(var11), var1.getUUID());
            var21.setPoz(50);
            var21.setKoltukSecim(0);
            var21.setNoGravity(true);
            var21.setNoAi(true);
            var21.pegasusDurum = 0;
            var21.pegasusTick = 0;
            var21.pBasX = var15;
            var21.pBasY = var17;
            var21.pBasZ = var19;
            var21.pHedefX = var9;
            var21.pHedefY = var13;
            var21.pHedefZ = var11;
            var21.setCustomName(Component.literal("✦ Kraliyet Soylu Pegasus Arabası (4 Kişilik) ✦").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}));
            var21.setCustomNameVisible(true);
            var21.moveTo(var15, var17, var19, var1.getYRot() + 180.0F, 22.0F);
            var0.addFreshEntity(var21);
            var1.connection.send(new ClientboundSetTitlesAnimationPacket(10, 65, 20));
            var1.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("4 Kişilik Kapalı Soylu Arabası göklerden kişneyerek önüne süzülüyor!").withStyle(ChatFormatting.YELLOW)));
            var1.connection.send(new ClientboundSetTitleTextPacket(Component.literal("\ud83d\udc0e KRALİYET PEGASUS ARABASI \ud83d\udc0e").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD})));
            return var21;
         }
      }
   }

   public void pegasusHedefAta(ServerLevel var1, ServerPlayer var2, double var3, double var5, double var7, String var9) {
      if (var1.dimension().equals(YarikAsasi.CENNET)) {
         this.oyuncu = var2.getUUID();
         this.pHedefX = var3;
         this.pHedefY = var5;
         this.pHedefZ = var7;
         this.pHedefIsim = var9;
         this.pHedefBoyut = var9 == null || !var9.contains("Zifiri") && !var9.contains("Güney") ? 0 : 1;
         this.pegasusDurum = 2;
         this.pegasusTick = 0;
         this.pYanKayma = (double)0.0F;
         this.pYukariKayma = (double)0.0F;
         this.setPoz(50);
         this.setNoGravity(true);
         this.setNoAi(true);
         if (!this.hasPassenger(var2) && this.getPassengers().size() < 4) {
            if (var2.isPassenger()) {
               var2.stopRiding();
            }

            var2.startRiding(this, true);
         }

         List var10 = var1.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate((double)5.5F), (var1x) -> var1x != var2 && var1x.isAlive() && !var1x.isPassenger());
         if (var10 != null) {
            for(ServerPlayer var12 : var10) {
               if (this.getPassengers().size() < 4) {
                  var12.startRiding(this, true);
               }
            }
         }

         ses(var1, this.getX(), this.getY(), this.getZ(), SoundEvents.HORSE_ANGRY, 3.0F, 1.05F);
         ses(var1, this.getX(), this.getY(), this.getZ(), SoundEvents.HORSE_GALLOP, 2.5F, 1.0F);
         ses(var1, this.getX(), this.getY(), this.getZ(), SoundEvents.BEACON_ACTIVATE, 2.2F, 1.2F);
         var2.connection.send(new ClientboundSetTitlesAnimationPacket(10, 60, 20));
         var2.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("Hedef: " + var9 + " (" + this.getPassengers().size() + "/4 Soylu Koltuk)").withStyle(ChatFormatting.GOLD)));
         var2.connection.send(new ClientboundSetTitleTextPacket(Component.literal("✦ PEGASUS GÖKLERE YÜKSELİYOR ✦").withStyle(new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.BOLD})));
      }
   }

   private static void cicekAcVeYesert(ServerLevel var0, ServerPlayer var1, double var2, double var4, double var6) {
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();
      BlockState var9 = Blocks.AIR.defaultBlockState();
      BlockState[] var10 = new BlockState[]{Blocks.AZURE_BLUET.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState(), Blocks.ALLIUM.defaultBlockState(), Blocks.LILY_OF_THE_VALLEY.defaultBlockState()};
      int var11 = Mth.floor(var2);
      int var12 = Mth.floor(var4);
      int var13 = Mth.floor(var6);

      for(int var14 = -4; var14 <= 4; ++var14) {
         for(int var15 = -4; var15 <= 4; ++var15) {
            for(int var16 = 0; var16 <= 5; ++var16) {
               var8.set(var11 + var14, var12 + var16, var13 + var15);
               BlockState var17 = var0.getBlockState(var8);
               if (var17.is((Block)CennetMod.KARANLIK_YAPRAK.get()) || var17.is((Block)CennetMod.KARANLIK_GOVDE.get())) {
                  var0.setBlock(var8, var9, 2);
               }
            }
         }
      }

      for(int var23 = -5; var23 <= 5; ++var23) {
         for(int var24 = -5; var24 <= 5; ++var24) {
            double var25 = (double)(var23 * var23 + var24 * var24);
            if (!(var25 > (double)26.0F)) {
               for(int var18 = 3; var18 >= -4; --var18) {
                  var8.set(var11 + var23, var12 + var18, var13 + var24);
                  BlockState var19 = var0.getBlockState(var8);
                  BlockPos var20 = var8.above();
                  BlockState var21 = var0.getBlockState(var20);
                  if (!gecilirMi(var19) && var21.isAir()) {
                     if (var19.is(Blocks.DIRT) || var19.is(Blocks.GRASS_BLOCK) || var19.is(Blocks.STONE) || var19.is(Blocks.DEEPSLATE)) {
                        var0.setBlock(var8, (var23 + var24) % 2 == 0 ? Blocks.MOSS_BLOCK.defaultBlockState() : Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                     }

                     int var22 = Math.abs((var11 + var23) * 31 + (var13 + var24) * 17);
                     if (var25 >= 2.2 && var22 % 3 != 0) {
                        var0.setBlock(var20, var10[var22 % var10.length], 2);
                     }
                     break;
                  }
               }
            }
         }
      }

      pr(var0, var1, ParticleTypes.HAPPY_VILLAGER, var2, var4 + 0.6, var6, 45, 3.2, 0.6, 3.2, 0.05);
      pr(var0, var1, ParticleTypes.END_ROD, var2, var4 + (double)1.0F, var6, 50, 2.8, (double)1.0F, 2.8, 0.08);
   }

   private static void yildizTozuDeseniCiz(ServerLevel var0, ServerPlayer var1, double var2, double var4, double var6, float var8, int var9) {
      float var10 = var8 * ((float)Math.PI / 180F);
      double var11 = (double)Mth.sin(var10);
      double var13 = (double)(-Mth.cos(var10));
      double var15 = (double)Mth.cos(var10);
      double var17 = (double)Mth.sin(var10);
      double var19 = var2 + var11 * 2.8;
      double var21 = var4 + 1.2;
      double var23 = var6 + var13 * 2.8;
      double var25 = (double)var9 * 0.22;
      double var27 = 1.85;

      for(int var29 = 0; var29 < 5; ++var29) {
         double var30 = var25 + (double)var29 * 1.2566370614359172;
         double var32 = var25 + (double)((var29 + 2) % 5) * 1.2566370614359172;
         double var34 = Math.cos(var30) * var27;
         double var36 = Math.sin(var30) * var27;
         double var38 = Math.cos(var32) * var27;
         double var40 = Math.sin(var32) * var27;

         for(int var42 = 0; var42 <= 3; ++var42) {
            double var43 = (double)var42 / (double)3.0F;
            double var45 = var34 + (var38 - var34) * var43;
            double var47 = var36 + (var40 - var36) * var43;
            double var49 = var19 + var15 * var45;
            double var51 = var21 + var47 * 0.65;
            double var53 = var23 + var17 * var45;
            pr(var0, var1, ParticleTypes.END_ROD, var49, var51, var53, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
         }
      }

      pr(var0, var1, ParticleTypes.CLOUD, var19, var21, var23, 2, 0.45, 0.2, 0.45, 0.01);
   }

   private void pegasusTick() {
      if (this.level() instanceof ServerLevel) {
         ServerLevel var1 = (ServerLevel)this.level();
         if (!var1.dimension().equals(YarikAsasi.CENNET)) {
            this.ejectPassengers();
            this.discard();
         } else {
            ServerPlayer var2 = this.oyuncu == null ? null : var1.getServer().getPlayerList().getPlayer(this.oyuncu);
            if (var2 == null) {
               for(Entity var4 : this.getPassengers()) {
                  if (var4 instanceof ServerPlayer) {
                     var2 = (ServerPlayer)var4;
                     this.oyuncu = var2.getUUID();
                     break;
                  }
               }
            }

            if (var2 == null) {
               List var39 = var1.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate((double)180.0F), (var0) -> var0.isAlive());
               if (var39 != null && !var39.isEmpty()) {
                  var2 = (ServerPlayer)var39.get(0);
                  this.oyuncu = var2.getUUID();
               }
            }

            if (var2 != null && var2.isAlive() && var2.level() == var1) {
               ++this.pegasusTick;
               if (this.pegasusDurum == 0) {
                  this.setPoz(50);
                  double var44 = Math.min((double)1.0F, (double)this.pegasusTick / (double)75.0F);
                  double var49 = (double)1.0F - Math.pow((double)1.0F - var44, 2.2);
                  double var52 = this.pHedefX - this.pBasX;
                  double var54 = this.pHedefZ - this.pBasZ;
                  double var57 = this.pBasX + var52 * var49;
                  double var58 = this.pBasZ + var54 * var49;
                  double var59 = this.pBasY + (this.pHedefY - this.pBasY) * Math.pow(var44, 1.35);
                  float var61 = (float)(Mth.atan2(var54, var52) * (180D / Math.PI)) - 90.0F;
                  float var18 = (float)((double)18.0F * ((double)1.0F - var44));
                  this.setDeltaMovement(Vec3.ZERO);
                  this.moveTo(var57, var59, var58, var61, var18);
                  this.setYRot(var61);
                  this.setYBodyRot(var61);
                  this.setYHeadRot(var61);
                  yildizTozuDeseniCiz(var1, var2, var57, var59, var58, var61, this.pegasusTick);
                  if (this.pegasusTick % 16 == 0) {
                     ses(var1, var57, var59, var58, SoundEvents.HORSE_GALLOP, 2.5F, 0.95F);
                     ses(var1, var57, var59, var58, SoundEvents.AMETHYST_BLOCK_CHIME, 2.2F, 0.9F + (float)var44 * 0.4F);
                  }

                  if (this.pegasusTick == 32) {
                     ses(var1, var57, var59, var58, SoundEvents.HORSE_ANGRY, 3.0F, 1.08F);
                  }

                  if (this.pegasusTick >= 75) {
                     this.pegasusDurum = 1;
                     this.pegasusTick = 0;
                     this.setPoz(51);
                     this.setDeltaMovement(Vec3.ZERO);
                     this.moveTo(this.pHedefX, this.pHedefY, this.pHedefZ, var2.getYRot(), 0.0F);
                     this.setYRot(var2.getYRot());
                     this.setYBodyRot(var2.getYRot());
                     ses(var1, this.pHedefX, this.pHedefY, this.pHedefZ, SoundEvents.HORSE_ANGRY, 3.0F, 1.0F);
                     ses(var1, this.pHedefX, this.pHedefY, this.pHedefZ, SoundEvents.BELL_RESONATE, 2.8F, 1.1F);
                     cicekAcVeYesert(var1, var2, this.pHedefX, this.pHedefY, this.pHedefZ);
                     var2.sendSystemMessage(Component.literal("✦ 4 Kişilik Kapalı Soylu Pegasus Arabası önüne indi ve etraf çiçeklerle yeşerdi! ").withStyle(ChatFormatting.GREEN).append(Component.literal("Arabaya sağ tıklayarak 4 köşe koltuğa oturabilir, arabadayken sağ tıklayarak koltuk değiştirebilir, sohbete ").withStyle(ChatFormatting.WHITE)).append(Component.literal("\"pegasus, beni batı şehrine götür\"").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD})).append(Component.literal(" veya ").withStyle(ChatFormatting.WHITE)).append(Component.literal("\"pegasus, beni güney bölgesine götür\"").withStyle(new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.BOLD})).append(Component.literal(" yazabilirsin!").withStyle(ChatFormatting.WHITE)));
                  }

               } else if (this.pegasusDurum == 1) {
                  this.setPoz(51);
                  this.setDeltaMovement(Vec3.ZERO);
                  this.moveTo(this.pHedefX, this.pHedefY, this.pHedefZ, this.getYRot(), 0.0F);
                  this.setYBodyRot(this.getYRot());
                  if (this.pegasusTick > 18000 && !this.isVehicle()) {
                     this.discard();
                  } else {
                     if (this.pegasusTick > 20 && this.getPassengers().size() < 4) {
                        List var43 = var1.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(2.6), (var0) -> var0.isAlive() && !var0.isPassenger());
                        if (var43 != null) {
                           for(ServerPlayer var48 : var43) {
                              if (this.getPassengers().size() < 4) {
                                 var48.startRiding(this, true);
                                 int var50 = Math.max(0, this.getPassengers().indexOf(var48));
                                 int var51 = var50 + this.getKoltukSecim() & 3;
                                 var48.displayClientMessage(Component.literal("✦ Kraliyet Arabası — Koltuk " + (var51 + 1) + " (" + koltukAdi(var51) + ")! (Sağ tıkla koltuk değiştir / Sohbete 'pegasus, beni ... götür' yaz) ✦").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD}), true);
                              }
                           }
                        }
                     }

                     if (this.pegasusTick % 15 == 0) {
                        pr(var1, var2, ParticleTypes.END_ROD, this.getX(), this.getY() + (double)1.5F, this.getZ(), 3, 1.2, (double)0.5F, 1.2, 0.01);
                     }

                  }
               } else if (this.pegasusDurum != 2) {
                  if (this.pegasusDurum == 3) {
                     this.setPoz(52);
                     float var42 = this.getYRot() + 6.0F;
                     double var46 = (double)var42 * 0.017453292;
                     double var6 = this.getX() - Math.sin(var46) * 0.55;
                     double var8 = this.getZ() + Math.cos(var46) * 0.55;
                     double var55 = this.getY() + 0.92;
                     this.setDeltaMovement(Vec3.ZERO);
                     this.moveTo(var6, var55, var8, var42, -40.0F);
                     this.setYRot(var42);
                     this.setYBodyRot(var42);
                     yildizTozuDeseniCiz(var1, var2, var6, var55, var8, var42, this.pegasusTick);
                     if (this.pegasusTick == 15) {
                        ses(var1, var6, var55, var8, SoundEvents.HORSE_ANGRY, 3.0F, 1.18F);
                     }

                     if (this.pegasusTick >= 50) {
                        pr(var1, var2, ParticleTypes.FLASH, var6, var55 + (double)1.0F, var8, 2, 0.2, 0.2, 0.2, (double)0.0F);
                        pr(var1, var2, ParticleTypes.END_ROD, var6, var55 + (double)1.0F, var8, 40, 1.2, 1.2, 1.2, 0.12);
                        this.discard();
                     }
                  }

               } else {
                  this.setPoz(50);
                  if (!this.hasPassenger(var2) && this.getPassengers().size() < 4 && this.pegasusTick < 20) {
                     var2.startRiding(this, true);
                  }

                  for(Entity var45 : this.getPassengers()) {
                     var45.fallDistance = 0.0F;
                  }

                  double var41 = this.getX();
                  double var5 = this.getY();
                  double var7 = this.getZ();
                  if (this.pHedefBoyut == 1 && this.pegasusTick >= 95) {
                     ArrayList var53 = new ArrayList(this.getPassengers());
                     this.ejectPassengers();

                     for(Entity var56 : var53) {
                        if (var56 instanceof ServerPlayer) {
                           NurSistemi.zifiriBataklikBoyutunaGec((ServerPlayer)var56);
                        }
                     }

                     this.discard();
                  } else {
                     double var9 = this.pHedefX - var41;
                     double var11 = this.pHedefZ - var7;
                     double var13 = Math.sqrt(var9 * var9 + var11 * var11);
                     double var15 = this.pHedefY;
                     if (var15 < (double)0.0F) {
                        if (var13 < (double)64.0F) {
                           var15 = (double)pegasusGuvenliZeminBul(var1, Mth.floor(this.pHedefX), Mth.floor(var5), Mth.floor(this.pHedefZ));
                           this.pHedefY = var15;
                        } else {
                           var15 = Math.max((double)92.0F, var5);
                        }
                     }

                     if (var13 <= 2.8) {
                        double var60 = this.pHedefY > (double)0.0F ? this.pHedefY : (double)pegasusGuvenliZeminBul(var1, Mth.floor(this.pHedefX), Mth.floor(var5), Mth.floor(this.pHedefZ));
                        this.setDeltaMovement(Vec3.ZERO);
                        this.moveTo(this.pHedefX, var60, this.pHedefZ, this.getYRot(), 0.0F);
                        ArrayList var62 = new ArrayList(this.getPassengers());
                        this.ejectPassengers();

                        for(Entity var63 : var62) {
                           var63.fallDistance = 0.0F;
                           if (var63 instanceof ServerPlayer) {
                              ServerPlayer var22 = (ServerPlayer)var63;
                              if (this.pHedefBoyut == 1) {
                                 NurSistemi.zifiriBataklikBoyutunaGec(var22);
                              } else {
                                 var22.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 80, 0, false, false, false));
                                 var22.teleportTo(var1, this.pHedefX, var60 + (double)0.5F, this.pHedefZ, this.getYRot(), 0.0F);
                                 var22.sendSystemMessage(Component.literal("✦ Kraliyet Pegasus Arabası seni " + this.pHedefIsim + " noktasına ulaştırdı ve kişneyerek göklere yükseliyor! ✦").withStyle(new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.BOLD}));
                              }
                           }
                        }

                        cicekAcVeYesert(var1, var2, this.pHedefX, var60, this.pHedefZ);
                        ses(var1, this.pHedefX, var60, this.pHedefZ, SoundEvents.HORSE_ANGRY, 3.2F, 1.08F);
                        ses(var1, this.pHedefX, var60, this.pHedefZ, SoundEvents.BEACON_POWER_SELECT, 2.5F, 1.15F);
                        this.pegasusDurum = 3;
                        this.pegasusTick = 0;
                        this.setPoz(52);
                     } else {
                        double var17 = var9 / var13;
                        double var19 = var11 / var13;
                        double var21 = var13 > (double)36.0F ? Math.max(var15 + (double)26.0F, (double)138.0F) : var15 + var13 / (double)36.0F * (double)26.0F;
                        BlockPos.MutableBlockPos var23 = new BlockPos.MutableBlockPos();
                        boolean var24 = false;

                        for(int var25 = 4; var25 <= 12; var25 += 4) {
                           int var26 = Mth.floor(var41 + var17 * (double)var25);
                           int var27 = Mth.floor(var7 + var19 * (double)var25);
                           BlockState var28 = var1.getBlockState(var23.set(var26, Mth.floor(var5) + 1, var27));
                           if (!var28.isAir()) {
                              var24 = true;
                              break;
                           }
                        }

                        if (var24) {
                           this.pYukariKayma = Math.min((double)18.0F, this.pYukariKayma + 0.85);
                        } else {
                           this.pYukariKayma = Math.max((double)0.0F, this.pYukariKayma * 0.9);
                        }

                        double var64 = Math.min(1.18, var13 * 0.35);
                        double var65 = var41 + var17 * var64;
                        double var29 = var7 + var19 * var64;
                        double var31 = var21 + this.pYukariKayma;
                        double var33 = var31 - var5;
                        double var35 = var5 + (double)Mth.clamp((float)(var33 * 0.14), -0.75F, 0.95F);
                        float var37 = (float)(Mth.atan2(var11, var9) * (180D / Math.PI)) - 90.0F;
                        float var38 = Mth.clamp((float)(-(var35 - var5) * (double)16.0F), -22.0F, 22.0F);
                        this.setDeltaMovement(Vec3.ZERO);
                        this.moveTo(var65, var35, var29, var37, var38);
                        this.setYRot(var37);
                        this.setYBodyRot(var37);
                        this.setYHeadRot(var37);
                        if (this.pegasusTick % 2 == 0) {
                           yildizTozuDeseniCiz(var1, var2, var65, var35, var29, var37, this.pegasusTick);
                        }

                        if (this.pegasusTick % 14 == 0) {
                           ses(var1, var65, var35, var29, SoundEvents.HORSE_GALLOP, 2.0F, 1.05F);
                        }

                        if (this.pegasusTick % 70 == 0) {
                           ses(var1, var65, var35, var29, SoundEvents.HORSE_AMBIENT, 2.4F, 1.1F);
                        }

                     }
                  }
               }
            } else {
               if (!this.isVehicle() && this.tickCount > 200) {
                  this.discard();
               }

            }
         }
      }
   }

   static {
      POZ = SynchedEntityData.defineId(AngelSoldier.class, EntityDataSerializers.INT);
      KOLTUK = SynchedEntityData.defineId(AngelSoldier.class, EntityDataSerializers.INT);
      SMOOTH = Blocks.SMOOTH_QUARTZ.defaultBlockState();
      PILLAR = Blocks.QUARTZ_PILLAR.defaultBlockState();
      GOLD = Blocks.GOLD_BLOCK.defaultBlockState();
      BRICKS = Blocks.QUARTZ_BRICKS.defaultBlockState();
      GLASS = Blocks.WHITE_STAINED_GLASS.defaultBlockState();
      AIR = Blocks.AIR.defaultBlockState();
   }
}
