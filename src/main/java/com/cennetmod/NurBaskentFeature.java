package com.cennetmod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class NurBaskentFeature extends Feature<NoneFeatureConfiguration> {
   public static final int ZEMIN = 100;
   public static final int G = 101;
   public static final int KAPI = 368;
   public static final int SEHIR_R = 420;
   public static final int YAN_BIYOM_MIN_R = 1500;
   private static final int[][] HEYKEL = new int[][]{{-34, 250, 1, 0, 100}, {34, 250, -1, 0, 100}, {-28, 64, 0, 1, 90}, {28, 64, 0, 1, 90}, {-245, -34, 0, -1, 95}, {245, -34, 0, -1, 95}};
   private static final int N_PANTEON = 8;
   private static final int[] PX = new int[8];
   private static final int[] PZ = new int[8];
   private static final int N_ADA = 18;
   private static final int[] ACX = new int[18];
   private static final int[] ACZ = new int[18];
   private static final int[] ATY = new int[18];
   private static final int[] AR = new int[18];
   private static final int[] AD = new int[18];
   private static final int[] SX = new int[]{7, 5, 0, -5, -7, -5, 0, 5};
   private static final int[] SZ = new int[]{0, 5, 7, 5, 0, -5, -7, -5};
   public static final int N_SATO = 56;
   public static final int N_DEVASA_SATO = 24;
   public static final int[] SCX = new int[56];
   public static final int[] SCZ = new int[56];
   public static final int[] SY = new int[56];
   public static final int[] SR = new int[56];
   public static final int[] SD = new int[56];
   public static final int[] STIP = new int[56];
   public static final int ELF1_X = 1620;
   public static final int ELF1_Z = 0;
   public static final int ELF2_X = 1180;
   public static final int ELF2_Z = -1180;
   public static final int ELF_KASABA_R = 420;
   public static final int BATI_MERKEZ_X = -2000;
   public static final int BATI_MERKEZ_Z = 0;
   public static final int BATI_SEHIR_R = 316;
   public static final int BATI_KAPI_X = -1684;
   public static final int DOGU_BASLANGIC_X = 1500;
   public static final int GUNEY_BATAKLIK_Z = 1455;
   public static final int DOGU_MERKEZ_X = 2050;
   public static final int DOGU_MERKEZ_Z = 0;
   public static final int[][] KART_SUNAKLARI;
   public static final int[][] DOGU_KOYLERI;
   public static final int[][] ZIFIRI_YERLESIMLER;

   public NurBaskentFeature() {
      super(NoneFeatureConfiguration.CODEC);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      int var4 = var3.getX() & -16;
      int var5 = var3.getZ() & -16;
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
      if (!var2.getBlockState(var6.set(var4, 0, var5)).is(NurBaskentFeature.P.BULUT_BLOK_REF)) {
         return yildizIsigiBoyutuPlace(var2, var6, var4, var5);
      } else {
         int[] var7 = new int[8];
         int var8 = 0;
         if (var4 <= -700 && var5 <= 700) {
            for(int var9 = 0; var9 < 56; ++var9) {
               int var10 = SR[var9] + 14;
               if (var4 + 15 >= SCX[var9] - var10 && var4 <= SCX[var9] + var10 && var5 + 15 >= SCZ[var9] - var10 && var5 <= SCZ[var9] + var10 && var8 < var7.length) {
                  var7[var8++] = var9;
               }
            }
         }

         for(int var16 = 0; var16 < 16; ++var16) {
            for(int var17 = 0; var17 < 16; ++var17) {
               int var11 = var4 + var16;
               int var12 = var5 + var17;
               long var13 = (long)var11 * (long)var11 + (long)var12 * (long)var12;
               if (var13 <= 360000L) {
                  if (var13 <= 176400L) {
                     sehirKolonu(var2, var6, var11, var12);
                  } else if (var13 > 184900L) {
                     tepe(var2, var6, var11, var12, Math.sqrt((double)var13));
                  }

                  if (var13 >= 115600L && var13 <= 291600L) {
                     adalar(var2, var6, var11, var12);
                  }
               } else if (var13 >= 2250000L) {
                  if (var11 >= 750 && var12 <= 700 && var13 <= 8410000L) {
                     elfBolgesiKolonu(var2, var6, var11, var12, Math.sqrt((double)var13));
                  } else if (var11 <= -750 && var12 <= 700 && var13 <= 8410000L) {
                     kbBulutTabani(var2, var6, var11, var12);

                     for(int var15 = 0; var15 < var8; ++var15) {
                        satoKolonu(var2, var6, var11, var12, var7[var15]);
                     }
                  }
               }
            }
         }

         return true;
      }
   }

   private static void set(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3, int var4, BlockState var5) {
      if (var5 != NurBaskentFeature.P.HAVA) {
         var0.setBlock(var1.set(var2, var3, var4), var5, 2);
      }
   }

   private static void elfBolgesiKolonu(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3, double var4) {
      double var6 = smooth((double)1500.0F, (double)1560.0F, var4) * ((double)1.0F - smooth((double)2720.0F, (double)2880.0F, var4));
      if (!(var6 <= (double)0.0F)) {
         double var8 = Math.sqrt((double)(var2 - 1620) * (double)(var2 - 1620) + (double)(var3 - 0) * (double)(var3 - 0));
         double var10 = Math.sqrt((double)(var2 - 1180) * (double)(var2 - 1180) + (double)(var3 - -1180) * (double)(var3 - -1180));
         boolean var12 = var8 <= var10;
         int var13 = var12 ? var2 - 1620 : var2 - 1180;
         int var14 = var12 ? var3 - 0 : var3 - -1180;
         double var15 = var12 ? var8 : var10;
         double var17 = vn((double)var2 / (double)55.0F + (double)17.0F, (double)var3 / (double)55.0F + (double)31.0F);
         double var19 = vn((double)var2 / (double)22.0F + (double)5.0F, (double)var3 / (double)22.0F + (double)11.0F);
         int var21 = (int)Math.round(var6 * (var17 * (double)10.0F + var19 * (double)4.0F));
         if (var15 <= (double)420.0F) {
            var21 = 3;
         } else if (var15 <= (double)465.0F) {
            double var22 = smooth((double)420.0F, (double)465.0F, var15);
            var21 = (int)Math.round((double)3.0F * ((double)1.0F - var22) + (double)var21 * var22);
         }

         double var33 = Math.sin((double)var2 / (double)42.0F) * (double)28.0F + Math.cos((double)var3 / (double)38.0F) * (double)18.0F;
         double var24 = Math.abs((double)var14 - (double)var13 * 0.35 - var33);
         boolean var26 = var15 > (double)428.0F && var24 <= 3.8;
         int var27 = 100 + Math.max(0, var21);
         if (var26) {
            var27 = 100 + Math.max(0, var21 - 1);
         }

         for(int var28 = 100; var28 < var27; ++var28) {
            set(var0, var1, var2, var28, var3, var28 >= var27 - 2 ? NurBaskentFeature.P.TOPRAK : NurBaskentFeature.P.KALSIT);
         }

         BlockState var34 = elfZemin(var2, var3, var13, var14, var15, var26, var24);
         if (var34 == NurBaskentFeature.P.SU) {
            BlockState var29 = (Math.abs(var2) + Math.abs(var3)) % 3 == 0 ? NurBaskentFeature.P.MANTAR_ISIK : NurBaskentFeature.P.YOSUN;
            set(var0, var1, var2, var27 - 1, var3, var29);
         }

         set(var0, var1, var2, var27, var3, var34);
         if (var15 <= (double)68.0F) {
            for(int var36 = 0; var36 < 162; ++var36) {
               BlockState var39 = uluAgacSaray(var13, var14, var36, var15);
               if (var39 != null) {
                  set(var0, var1, var2, var27 + 1 + var36, var3, var39);
               }
            }

         } else {
            boolean var35 = false;
            if (var15 <= (double)420.0F) {
               byte var30 = 52;

               for(int var31 = 0; var31 < var30; ++var31) {
                  BlockState var32 = elfKasabaYapi(var2, var3, var13, var14, var31, var15);
                  if (var32 != null) {
                     if (var31 == 0) {
                        var35 = true;
                     }

                     set(var0, var1, var2, var27 + 1 + var31, var3, var32);
                  }
               }
            } else if (!var26) {
               for(int var37 = 0; var37 < 40; ++var37) {
                  BlockState var40 = sikElfOrmani(var2, var3, var37);
                  if (var40 != null) {
                     if (var37 == 0) {
                        var35 = true;
                     }

                     set(var0, var1, var2, var27 + 1 + var37, var3, var40);
                  }
               }
            }

            if (!var26 && !var35 && !elfYoluMu(var13, var14, var15) && (var34 == NurBaskentFeature.P.CIM || var34 == NurBaskentFeature.P.YOSUN)) {
               int var38 = hash(var2 * 29 + 11, var3 * 19 + 7);
               if (var38 % 10 == 0) {
                  set(var0, var1, var2, var27 + 1, var3, NurBaskentFeature.P.CICEK[var38 % NurBaskentFeature.P.CICEK.length]);
               }
            }

         }
      }
   }

   private static boolean elfYoluMu(int var0, int var1, double var2) {
      if (!(var2 <= (double)68.0F) && !(var2 > (double)420.0F)) {
         double var4 = Math.sin((double)var1 / (double)24.0F) * (double)4.5F;
         double var6 = Math.cos((double)var0 / (double)24.0F) * (double)4.5F;
         if (!(Math.abs((double)var0 - var4) <= 3.2) && !(Math.abs((double)var1 - var6) <= 3.2)) {
            double var8 = Math.sin((double)var0 / (double)18.0F + (double)var1 / (double)18.0F) * (double)3.0F;
            return Math.abs(var2 + var8 - (double)150.0F) <= 2.4 || Math.abs(var2 + var8 - (double)260.0F) <= 2.4;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private static BlockState elfZemin(int var0, int var1, int var2, int var3, double var4, boolean var6, double var7) {
      if (var6) {
         return NurBaskentFeature.P.SU;
      } else if (var7 <= 5.2 && var4 > (double)426.0F) {
         return (var0 + var1) % 2 == 0 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.CIM;
      } else if (var4 <= (double)68.0F) {
         double var11 = Math.atan2((double)var3, (double)var2);
         if (var4 >= (double)26.0F && var4 <= (double)42.0F && Math.cos(var11 * (double)8.0F) < -0.35 && Math.abs(var2) > 6 && Math.abs(var3) > 6) {
            return NurBaskentFeature.P.SU;
         } else if (var4 <= (double)22.0F) {
            return (var0 + var1) % 3 == 0 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.HUS_TAHTA;
         } else {
            return hash(var0, var1) % 2 == 0 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.CIM;
         }
      } else if (var4 <= (double)420.0F && elfYoluMu(var2, var3, var4)) {
         int var9 = hash(var0 * 13, var1 * 17) % 8;
         if (var9 <= 4) {
            return NurBaskentFeature.P.YOSUN;
         } else {
            return var9 <= 6 ? NurBaskentFeature.P.MESE_TAHTA : NurBaskentFeature.P.CIM;
         }
      } else {
         return hash(var0, var1) % 2 == 0 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.CIM;
      }
   }

   private static BlockState uluAgacSaray(int var0, int var1, int var2, double var3) {
      int var5 = Math.abs(var0);
      int var6 = Math.abs(var1);
      double var7 = Math.atan2((double)var1, (double)var0);
      if (var3 <= (double)4.5F && var2 <= 115) {
         if (var3 <= (double)1.5F) {
            return var2 % 2 == 0 ? NurBaskentFeature.P.MANTAR_ISIK : NurBaskentFeature.P.ZUMRUT;
         }

         if (var3 <= 2.6) {
            return var2 % 5 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.LADIN_GOVDE;
         }

         double var9 = (double)var2 * 0.45 % (Math.PI * 2D);
         double var11 = Math.abs(Math.atan2(Math.sin(var7 - var9), Math.cos(var7 - var9)));
         if (var11 <= 0.65) {
            return NurBaskentFeature.P.HUS_TAHTA;
         }
      }

      if (var2 <= 32 && var3 >= (double)16.0F && var3 <= (double)62.0F) {
         double var22 = Math.cos(var7 * (double)8.0F);
         double var28 = (double)23.0F + ((double)32.0F - (double)var2) * 1.15 * Math.max((double)0.0F, var22);
         if (var22 > (double)0.25F && var3 <= var28 && var3 >= var28 - 3.8) {
            if (var3 >= var28 - 1.2 && var2 >= 4) {
               return (var0 + var1 + var2) % 4 == 0 ? NurBaskentFeature.P.MANTAR_ISIK : NurBaskentFeature.P.YOSUN;
            }

            return (var2 + var5) % 5 == 0 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.LADIN_GOVDE;
         }
      }

      if (var2 <= 64) {
         double var23 = (double)23.5F - (double)var2 * 0.08 + Math.sin(var7 * (double)8.0F + (double)var2 * 0.1) * 1.2;
         int[] var29 = new int[]{14, 28, 42};

         for(int var12 = 0; var12 < var29.length; ++var12) {
            int var13 = var29[var12];
            double var14 = var23 + (double)7.5F - (double)var12 * (double)1.0F;
            if (var2 >= var13 && var2 <= var13 + 8 && var3 >= var23 - (double)1.0F && var3 <= var14) {
               int var16 = var2 - var13;
               if (var16 == 0) {
                  if (var3 >= var14 - 1.2) {
                     return (var0 + var1) % 3 == 0 ? NurBaskentFeature.P.MANTAR_ISIK : NurBaskentFeature.P.YOSUN;
                  }

                  return NurBaskentFeature.P.HUS_TAHTA;
               }

               if (var3 >= var14 - (double)1.5F) {
                  if (var16 == 1) {
                     return (var0 + var1) % 2 == 0 ? NurBaskentFeature.P.CICEKLI_YAPRAK : NurBaskentFeature.P.HUS_GOVDE;
                  }

                  if (var16 <= 6 && (int)Math.round(var7 * (double)16.0F / Math.PI) % 2 == 0 && var3 <= var14 - 0.4) {
                     return var16 == 3 ? NurBaskentFeature.P.MANTAR_ISIK : NurBaskentFeature.P.LADIN_GOVDE;
                  }

                  if (var16 == 7) {
                     return NurBaskentFeature.P.YOSUN;
                  }

                  if (var16 == 8) {
                     return NurBaskentFeature.P.CICEKLI_YAPRAK;
                  }
               }
            }
         }

         if (var3 <= var23 && var3 >= var23 - 2.8) {
            if (var2 >= 1 && var2 <= 11 && (var5 <= 4 || var6 <= 4)) {
               int var35 = Math.min(var5, var6);
               int var40 = 8 + (int)Math.sqrt((double)Math.max(0, 16 - var35 * var35));
               if (var2 < var40) {
                  return NurBaskentFeature.P.HAVA;
               }

               if (var2 == var40) {
                  return NurBaskentFeature.P.ZUMRUT;
               }
            }

            int var36 = var2 % 14;
            if (var36 >= 2 && var36 <= 9 && Math.cos(var7 * (double)8.0F) > 0.45) {
               if (var36 > 5 || var5 > 2 && var6 > 2) {
                  return (var2 + var5 + var6) % 3 == 0 ? NurBaskentFeature.P.ZUMRUT : NurBaskentFeature.P.CAM_GOK;
               }

               return NurBaskentFeature.P.HAVA;
            }

            if (var2 % 14 == 0) {
               return NurBaskentFeature.P.YOSUN;
            }

            return (var0 + var1 + var2) % 4 == 0 ? NurBaskentFeature.P.HUS_GOVDE : NurBaskentFeature.P.LADIN_GOVDE;
         }

         if (var3 < var23 - 2.8) {
            if (var2 != 0 && var2 != 14 && var2 != 28 && var2 != 42 && var2 != 56) {
               for(int var33 = 0; var33 < 8; ++var33) {
                  double var39 = (double)var33 * (Math.PI / 4D);
                  double var41 = (double)12.0F * Math.cos(var39);
                  double var43 = (double)12.0F * Math.sin(var39);
                  if (((double)var0 - var41) * ((double)var0 - var41) + ((double)var1 - var43) * ((double)var1 - var43) <= 1.2) {
                     return var2 % 7 == 3 ? NurBaskentFeature.P.MANTAR_ISIK : NurBaskentFeature.P.LADIN_GOVDE;
                  }
               }

               int var34 = var2 % 14;
               if (var34 == 1) {
                  if (var2 == 1 && var1 <= -14 && var1 >= -17 && var5 <= 2) {
                     return var5 == 0 && var1 == -16 ? NurBaskentFeature.P.ZUMRUT : NurBaskentFeature.P.ALTIN;
                  }

                  if (var3 >= (double)14.5F && var3 <= (double)17.0F) {
                     if ((var5 + var6) % 4 == 0) {
                        return NurBaskentFeature.P.KITAP;
                     }

                     if ((var5 + var6) % 4 == 1) {
                        return NurBaskentFeature.P.SANDIK;
                     }

                     if ((var5 + var6) % 4 == 2) {
                        return NurBaskentFeature.P.CALISMA_MASASI;
                     }

                     return NurBaskentFeature.P.CICEKLI_YAPRAK;
                  }

                  if (var3 >= (double)6.5F && var3 <= (double)8.5F && (var5 == 0 || var6 == 0)) {
                     return var2 < 28 ? NurBaskentFeature.P.BUYU_MASASI : NurBaskentFeature.P.KURSU;
                  }

                  if (var3 >= (double)5.0F && var3 <= (double)11.0F) {
                     return NurBaskentFeature.P.YESIL_HALI;
                  }
               }

               if ((var34 == 2 || var34 == 3) && var3 >= (double)14.5F && var3 <= (double)17.0F && (var5 + var6) % 4 == 0) {
                  return NurBaskentFeature.P.KITAP;
               }

               if (var34 != 12 || !(var3 >= (double)8.0F) || !(var3 <= (double)10.0F) || var5 > 1 && var6 > 1) {
                  return NurBaskentFeature.P.HAVA;
               }

               return NurBaskentFeature.P.MANTAR_ISIK;
            }

            if (var3 <= 4.8 && var2 > 0 && var2 < 56) {
               return null;
            }

            return (var0 + var1) % 3 == 0 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.HUS_TAHTA;
         }
      }

      if (var2 >= 44 && var2 <= 92) {
         int[][] var24 = new int[][]{{36, 0}, {-36, 0}, {0, 36}, {0, -36}};

         for(int var10 = 0; var10 < 4; ++var10) {
            int var30 = var24[var10][0];
            int var32 = var24[var10][1];
            double var37 = var30 != 0 ? (double)(var0 * (var30 > 0 ? 1 : -1)) : (double)(var1 * (var32 > 0 ? 1 : -1));
            double var15 = var30 != 0 ? (double)var6 : (double)var5;
            if (var37 >= (double)14.0F && var37 <= (double)42.0F && var15 <= 4.2) {
               double var17 = (double)46.0F + (var37 - (double)14.0F) * 0.42;
               if (Math.abs((double)var2 - var17) <= 2.4) {
                  return var15 >= 3.2 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.LADIN_GOVDE;
               }

               if (var2 == (int)Math.round(var17) + 3 && var15 <= (double)3.0F) {
                  return var15 >= 2.2 ? NurBaskentFeature.P.CICEKLI_YAPRAK : NurBaskentFeature.P.HUS_TAHTA;
               }
            }

            double var42 = Math.sqrt((double)(var0 - var30) * (double)(var0 - var30) + (double)(var1 - var32) * (double)(var1 - var32));
            if (var42 <= (double)12.5F && var2 >= 55 && var2 <= 84) {
               int var19 = var2 - 58;
               if (var19 < 0) {
                  if (var42 <= (double)10.0F + (double)var19 * (double)2.5F) {
                     return var19 == -1 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.LADIN_GOVDE;
                  }
               } else {
                  if (var19 == 0) {
                     return var42 >= (double)10.5F ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.HUS_TAHTA;
                  }

                  if (var19 <= 10) {
                     if (var19 == 10 && var42 <= (double)10.5F) {
                        return NurBaskentFeature.P.HUS_TAHTA;
                     }

                     if (var42 >= 9.2 && var42 <= 10.6) {
                        if (var19 > 4 || Math.abs(var0 - var30) > 1 && Math.abs(var1 - var32) > 1) {
                           if ((int)Math.round(Math.atan2((double)(var1 - var32), (double)(var0 - var30)) * (double)8.0F / Math.PI) % 2 == 0) {
                              return var19 == 5 ? NurBaskentFeature.P.MANTAR_ISIK : NurBaskentFeature.P.LADIN_GOVDE;
                           }

                           return var19 >= 3 && var19 <= 7 ? NurBaskentFeature.P.CAM_GOK : NurBaskentFeature.P.YOSUN;
                        }

                        return NurBaskentFeature.P.HAVA;
                     }

                     if (var42 < 9.2) {
                        if (var19 == 1 && var42 <= (double)2.0F) {
                           return var42 <= 0.8 ? NurBaskentFeature.P.BUYU_MASASI : NurBaskentFeature.P.YESIL_HALI;
                        }

                        if (var19 == 1 && Math.abs(var42 - 7.8) <= 0.8 && (var5 + var6) % 3 == 0) {
                           return NurBaskentFeature.P.KITAP;
                        }

                        if (var19 == 9 && var42 <= 1.2) {
                           return NurBaskentFeature.P.MANTAR_ISIK;
                        }

                        return NurBaskentFeature.P.HAVA;
                     }
                  } else {
                     double var20 = 11.2 - (double)(var19 - 10) * 0.92;
                     if (var20 > (double)0.0F && var42 <= var20) {
                        if (var20 <= (double)1.5F) {
                           return NurBaskentFeature.P.MANTAR_ISIK;
                        }

                        if (var42 >= var20 - (double)2.0F) {
                           return (var19 + var0 + var1) % 3 == 0 ? NurBaskentFeature.P.CICEKLI_YAPRAK : NurBaskentFeature.P.YOSUN;
                        }

                        return NurBaskentFeature.P.HAVA;
                     }
                  }
               }
            }
         }
      }

      if (var2 >= 64) {
         if (var2 >= 106 && var3 <= (double)18.0F) {
            int var25 = var2 - 106;
            if (var25 == 0 && var3 <= (double)16.5F) {
               return var3 >= (double)14.5F ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.HUS_TAHTA;
            }

            if (var25 >= 1 && var25 <= 16 && var3 <= (double)14.5F) {
               if (var25 == 16) {
                  return NurBaskentFeature.P.HUS_TAHTA;
               }

               if (!(var3 >= (double)12.5F)) {
                  if (var25 == 1 && var3 <= (double)2.5F) {
                     return var3 <= (double)1.0F ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
                  }

                  return NurBaskentFeature.P.HAVA;
               }

               if (var25 > 5 || var5 > 2 && var6 > 2) {
                  if (var5 != var6 && var5 > 1 && var6 > 1) {
                     return var25 % 4 == 0 ? NurBaskentFeature.P.ZUMRUT : NurBaskentFeature.P.CAM_GOK;
                  }

                  return NurBaskentFeature.P.LADIN_GOVDE;
               }

               return NurBaskentFeature.P.HAVA;
            }

            if (var25 > 16 && var25 <= 48) {
               double var27 = (double)14.5F - (double)(var25 - 16) * 0.44;
               if (var27 > (double)0.0F && var3 <= var27) {
                  if (var27 <= 1.6) {
                     return NurBaskentFeature.P.FENER;
                  }

                  if (var3 >= var27 - (double)2.0F) {
                     return var25 % 3 == 0 ? NurBaskentFeature.P.ZUMRUT : NurBaskentFeature.P.YOSUN;
                  }

                  return NurBaskentFeature.P.HAVA;
               }
            }
         }

         if (var2 <= 118) {
            double var26 = (double)var2 - (double)90.0F;
            double var31 = (double)66.0F - var26 * var26 * 0.065 + Math.sin(var7 * (double)9.0F) * (double)3.5F;
            if (var31 > (double)0.0F && var3 <= var31) {
               if (Math.abs(Math.sin(var7 * (double)4.0F)) > 0.9 && var3 <= var31 * 0.8 && var2 <= 96) {
                  return NurBaskentFeature.P.LADIN_GOVDE;
               }

               if (var3 >= var31 - (double)6.5F || var2 >= 96) {
                  int var38 = hash(var0 * 13 + var2, var1 * 17);
                  if (var38 % 11 == 0) {
                     return NurBaskentFeature.P.MANTAR_ISIK;
                  }

                  if (var38 % 17 == 0) {
                     return NurBaskentFeature.P.FENER;
                  }

                  if (var38 % 3 == 0) {
                     return NurBaskentFeature.P.CICEKLI_YAPRAK;
                  }

                  if (var38 % 2 == 0) {
                     return NurBaskentFeature.P.YAPRAK;
                  }

                  return NurBaskentFeature.P.ACELYA_YAPRAK;
               }
            }
         }
      }

      return null;
   }

   private static BlockState elfKasabaYapi(int var0, int var1, int var2, int var3, int var4, double var5) {
      int var7 = Math.abs(var2);
      int var8 = Math.abs(var3);
      if (var7 > 9 && var8 > 9 && !(var5 <= (double)72.0F)) {
         byte var9 = 36;
         int var10 = var7 - 10;
         int var11 = var8 - 10;
         int var12 = var10 / var9;
         int var13 = var11 / var9;
         int var14 = var10 % var9;
         int var15 = var11 % var9;
         double var16 = (double)10.0F + (double)(var12 * var9) + (double)18.0F;
         double var18 = (double)10.0F + (double)(var13 * var9) + (double)18.0F;
         double var20 = Math.sqrt((var16 + (double)15.0F) * (var16 + (double)15.0F) + (var18 + (double)15.0F) * (var18 + (double)15.0F));
         double var22 = Math.sqrt(Math.max((double)0.0F, var16 - (double)15.0F) * Math.max((double)0.0F, var16 - (double)15.0F) + Math.max((double)0.0F, var18 - (double)15.0F) * Math.max((double)0.0F, var18 - (double)15.0F));
         int var24 = var2 >= 0 ? 1 : -1;
         int var25 = var3 >= 0 ? 1 : -1;
         int var26 = hash(var12 * 17 + var24 * 500, var13 * 31 + var25 * 500);
         int var27 = var2 >= 0 ? var14 - 18 : 18 - var14;
         int var28 = var3 >= 0 ? var15 - 18 : 18 - var15;
         int var29 = Math.abs(var14 - 18);
         int var30 = Math.abs(var15 - 18);
         if (var14 >= 2 && var14 < 34 && var15 >= 2 && var15 < 34) {
            double var31 = Math.sqrt(var16 * var16 + var18 * var18);
            boolean var33 = Math.abs(var31 - (double)150.0F) <= (double)18.0F || Math.abs(var31 - (double)260.0F) <= (double)18.0F;
            boolean var34 = !var33 && var22 >= (double)74.0F && var20 <= (double)405.0F && var26 % 100 < 82;
            if (var34) {
               int var40 = var26 % 3;
               BlockState var41;
               if (var40 == 0) {
                  var41 = elfKonagi(var27, var28, var29, var30, var4, var26);
               } else if (var40 == 1) {
                  var41 = elfAgacEv(var27, var28, var29, var30, var4, var26);
               } else {
                  var41 = elfKulesi(var27, var28, var29, var30, var4, var26);
               }

               if (var41 != null) {
                  return var41;
               } else {
                  int[][] var42 = new int[][]{{7, 7}, {7, 29}, {29, 7}, {29, 29}};

                  for(int var43 = 0; var43 < 4; ++var43) {
                     BlockState var39 = elfAgaci(var14, var15, var4, var42[var43][0], var42[var43][1], 11 + (var26 + var43) % 5, var26 + var43 * 13);
                     if (var39 != null) {
                        return var39;
                     }
                  }

                  return null;
               }
            } else {
               BlockState var35 = uluOrmanAgaci(var14, var15, var4, 18, 18, 20 + var26 % 7, var26);
               if (var35 != null) {
                  return var35;
               } else {
                  int[][] var36 = new int[][]{{7, 7}, {7, 29}, {29, 7}, {29, 29}, {18, 7}, {18, 29}, {7, 18}, {29, 18}};

                  for(int var37 = 0; var37 < var36.length; ++var37) {
                     BlockState var38 = elfAgaci(var14, var15, var4, var36[var37][0], var36[var37][1], 10 + (var26 + var37 * 3) % 5, var26 + var37 * 19);
                     if (var38 != null) {
                        return var38;
                     }
                  }

                  return null;
               }
            }
         } else if (var4 <= 4 && (var14 == 2 || var14 == 33) && (var15 == 2 || var15 == 33)) {
            return var4 == 4 ? NurBaskentFeature.P.MANTAR_ISIK : NurBaskentFeature.P.LADIN_GOVDE;
         } else {
            return null;
         }
      } else if (var4 <= 5 && (var7 == 7 && var8 > 16 && var8 % 16 == 0 || var8 == 7 && var7 > 16 && var7 % 16 == 0)) {
         if (var4 <= 3) {
            return NurBaskentFeature.P.LADIN_GOVDE;
         } else {
            return var4 == 4 ? NurBaskentFeature.P.CICEKLI_YAPRAK : NurBaskentFeature.P.MANTAR_ISIK;
         }
      } else {
         return null;
      }
   }

   private static BlockState elfKonagi(int var0, int var1, int var2, int var3, int var4, int var5) {
      byte var6 = 7;
      byte var7 = 6;
      double var8 = Math.sqrt((double)var0 * (double)var0 + (double)var1 * (double)var1);
      if (var4 >= 20 && var4 <= 29) {
         double var10 = (double)var4 - (double)24.5F;
         double var12 = (double)9.5F - var10 * var10 * 0.35;
         if (var12 > (double)0.0F && var8 <= var12) {
            if (var2 == 0 && var3 == 0 && var4 <= 24) {
               return NurBaskentFeature.P.LADIN_GOVDE;
            }

            int var14 = hash(var0 * 17 + var4, var1 * 23);
            if (var14 % 9 == 0) {
               return NurBaskentFeature.P.MANTAR_ISIK;
            }

            return var14 % 2 == 0 ? NurBaskentFeature.P.CICEKLI_YAPRAK : NurBaskentFeature.P.YAPRAK;
         }
      }

      if (var2 <= var6 + 1 && var3 <= var7 + 1) {
         if (var4 == 0) {
            return var2 != var6 + 1 && var3 != var7 + 1 ? NurBaskentFeature.P.HUS_TAHTA : NurBaskentFeature.P.YOSUN;
         } else if (var4 > 10) {
            if (var4 <= 19) {
               if (var2 == 0 && var3 == 0) {
                  return NurBaskentFeature.P.LADIN_GOVDE;
               }

               int var16 = var4 - 11;
               int var11 = var7 + 1 - var16;
               if (var11 >= 0 && var2 <= var6 + 1 && var3 <= var11) {
                  if (var11 == 0) {
                     return var2 % 2 == 0 ? NurBaskentFeature.P.MANTAR_ISIK : NurBaskentFeature.P.YOSUN;
                  }

                  if (var3 >= var11 - 1) {
                     return (var2 + var16) % 3 == 0 ? NurBaskentFeature.P.CICEKLI_YAPRAK : NurBaskentFeature.P.YOSUN;
                  }

                  if (var2 >= var6) {
                     return NurBaskentFeature.P.HUS_TAHTA;
                  }

                  return NurBaskentFeature.P.HAVA;
               }
            }

            return null;
         } else if (var2 <= var6 && var3 <= var7) {
            boolean var15 = var2 == var6 || var3 == var7;
            if (!var15) {
               if (var2 == 0 && var3 == 0) {
                  return var4 != 4 && var4 != 9 ? NurBaskentFeature.P.LADIN_GOVDE : NurBaskentFeature.P.MANTAR_ISIK;
               } else if (var4 == 10) {
                  return NurBaskentFeature.P.HUS_TAHTA;
               } else if (var0 >= -var6 + 1 && var0 <= -var6 + 2 && var1 >= -3 && var1 <= 1 && var4 <= 5) {
                  return var1 == -4 + var4 ? NurBaskentFeature.P.HUS_TAHTA : NurBaskentFeature.P.HAVA;
               } else if (var4 == 5) {
                  return NurBaskentFeature.P.HUS_TAHTA;
               } else {
                  if (var4 == 1) {
                     if (var3 == var7 - 1 && var0 >= -3 && var0 <= 1) {
                        return NurBaskentFeature.P.KITAP;
                     }

                     if (var3 == var7 - 1 && var0 == 3) {
                        return NurBaskentFeature.P.SANDIK;
                     }

                     if (var3 == var7 - 1 && var0 == 4) {
                        return NurBaskentFeature.P.CALISMA_MASASI;
                     }

                     if (var3 == var7 - 1 && var0 == 5) {
                        return NurBaskentFeature.P.VARIL;
                     }

                     if (var0 == 2 && var1 == 0) {
                        return NurBaskentFeature.P.BUYU_MASASI;
                     }

                     if (var2 <= 2 && var3 <= 2) {
                        return NurBaskentFeature.P.YESIL_HALI;
                     }
                  }

                  if ((var4 == 2 || var4 == 3) && var3 == var7 - 1 && var0 >= -3 && var0 <= 1) {
                     return NurBaskentFeature.P.KITAP;
                  } else {
                     if (var4 == 6) {
                        if (var3 == var7 - 1 && var0 >= 2 && var0 <= 4) {
                           return var0 == 4 ? NurBaskentFeature.P.BEYAZ_YUN : NurBaskentFeature.P.YESIL_YUN;
                        }

                        if (var3 == var7 - 1 && var0 == -3) {
                           return NurBaskentFeature.P.SANDIK;
                        }

                        if (var0 == 2 && var1 == 0) {
                           return NurBaskentFeature.P.KURSU;
                        }

                        if (var2 <= 2 && var3 <= 2) {
                           return NurBaskentFeature.P.SARI_HALI;
                        }
                     }

                     return NurBaskentFeature.P.HAVA;
                  }
               }
            } else if (var4 <= 3 && var2 <= 1 && var3 == var7) {
               return NurBaskentFeature.P.HAVA;
            } else if ((var2 != var6 || var3 != var7) && (var2 != var6 || var3 != 0) && (var3 != var7 || var2 != 0)) {
               if (var4 != 5 && var4 != 10) {
                  if ((var4 < 2 || var4 > 4) && (var4 < 7 || var4 > 9) || var2 % 3 != 0 && var3 % 3 != 0) {
                     return (var0 + var1 + var4) % 4 == 0 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.HUS_TAHTA;
                  } else {
                     return NurBaskentFeature.P.CICEKLI_YAPRAK;
                  }
               } else {
                  return NurBaskentFeature.P.YOSUN;
               }
            } else {
               return NurBaskentFeature.P.LADIN_GOVDE;
            }
         } else if (var4 == 10) {
            return NurBaskentFeature.P.YOSUN;
         } else if (var2 == var6 + 1 && var3 == var7 + 1) {
            return var4 == 9 ? NurBaskentFeature.P.MANTAR_ISIK : NurBaskentFeature.P.LADIN_GOVDE;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static BlockState elfKulesi(int var0, int var1, int var2, int var3, int var4, int var5) {
      double var6 = Math.sqrt((double)var0 * (double)var0 + (double)var1 * (double)var1);
      if (var6 > (double)9.5F) {
         return null;
      } else {
         if (var4 <= 5) {
            if (var4 == 0 && var6 <= (double)8.0F) {
               return var6 >= 6.8 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.HUS_TAHTA;
            }

            if (var4 == 5 && var6 >= (double)4.0F && var6 <= 7.8) {
               return var6 >= 6.8 ? NurBaskentFeature.P.CICEKLI_YAPRAK : NurBaskentFeature.P.YOSUN;
            }

            if (var6 >= (double)6.5F && var6 <= 7.8) {
               if ((var2 <= 1 || var3 <= 1) && var4 <= 3) {
                  return NurBaskentFeature.P.HAVA;
               }

               if (var2 != var3 && var2 != 0 && var3 != 0) {
                  return NurBaskentFeature.P.CICEKLI_YAPRAK;
               }

               return NurBaskentFeature.P.LADIN_GOVDE;
            }
         }

         if (var4 <= 24) {
            double var8 = 5.2 - Math.sin((double)var4 / (double)24.0F * Math.PI) * 0.7;
            if (var6 <= var8) {
               if (var4 <= 3 && var2 <= 1 && var1 > 0) {
                  return NurBaskentFeature.P.HAVA;
               }

               if (var6 < var8 - 1.35) {
                  if (var4 != 24 && var4 % 6 != 0) {
                     if (var2 == 0 && var3 == 0) {
                        return NurBaskentFeature.P.MANTAR_ISIK;
                     }

                     if (var4 % 6 == 1 && var2 == 2 && var3 == 0) {
                        return NurBaskentFeature.P.KITAP;
                     }

                     if (var4 % 6 == 1 && var2 == 0 && var3 == 2) {
                        return var4 < 12 ? NurBaskentFeature.P.SANDIK : NurBaskentFeature.P.BUYU_MASASI;
                     }

                     return NurBaskentFeature.P.HAVA;
                  }

                  return NurBaskentFeature.P.HUS_TAHTA;
               }

               if (var4 % 6 == 0) {
                  return NurBaskentFeature.P.YOSUN;
               }

               if (var4 % 6 < 2 || var4 % 6 > 4 || var2 > 1 && var3 > 1) {
                  return (var0 + var1 + var4) % 3 == 0 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.LADIN_GOVDE;
               }

               return NurBaskentFeature.P.CAM_GOK;
            }

            if (var4 < 5 && var6 < (double)6.5F) {
               return NurBaskentFeature.P.HAVA;
            }
         }

         if (var4 == 25 && var6 <= 6.8) {
            return var6 >= (double)5.5F ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.HUS_TAHTA;
         } else {
            if (var4 >= 26 && var4 <= 38) {
               double var13 = (double)var4 - (double)31.0F;
               double var10 = 8.8 - var13 * var13 * 0.22;
               if (var10 > (double)0.0F && var6 <= var10) {
                  if (var6 <= 1.2 && var4 <= 34) {
                     return NurBaskentFeature.P.MANTAR_ISIK;
                  }

                  int var12 = hash(var0 * 19 + var4, var1 * 29);
                  if (var12 % 10 == 0) {
                     return NurBaskentFeature.P.MANTAR_ISIK;
                  }

                  if (var12 % 2 == 0) {
                     return NurBaskentFeature.P.CICEKLI_YAPRAK;
                  }

                  return NurBaskentFeature.P.ACELYA_YAPRAK;
               }
            }

            return null;
         }
      }
   }

   private static BlockState elfAgacEv(int var0, int var1, int var2, int var3, int var4, int var5) {
      double var6 = Math.sqrt((double)var0 * (double)var0 + (double)var1 * (double)var1);
      if (var4 <= 24) {
         double var8 = var4 < 5 ? 5.2 - (double)var4 * 0.35 : 3.4;
         if (var6 <= var8) {
            if (var4 <= 3 && var2 <= 1 && var1 > 0) {
               return NurBaskentFeature.P.HAVA;
            }

            if (var6 <= 0.9) {
               return NurBaskentFeature.P.MANTAR_ISIK;
            }

            if (var6 <= 2.1 && var4 < 20) {
               double var16 = Math.atan2((double)var1, (double)var0);
               double var17 = (double)var4 * 0.55 % (Math.PI * 2D);
               if (Math.abs(Math.atan2(Math.sin(var16 - var17), Math.cos(var16 - var17))) <= (double)0.75F) {
                  return NurBaskentFeature.P.HUS_TAHTA;
               }

               return NurBaskentFeature.P.HAVA;
            }

            return (var0 + var1 + var4) % 5 == 0 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.LADIN_GOVDE;
         }
      }

      if (var4 >= 10 && var4 <= 21 && var6 <= 8.8) {
         if (var4 == 10) {
            return var6 >= 7.6 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.HUS_TAHTA;
         }

         if (var4 == 11 && var6 >= (double)7.5F && var6 <= 8.8) {
            return (var2 + var3) % 3 == 0 ? NurBaskentFeature.P.MANTAR_ISIK : NurBaskentFeature.P.CICEKLI_YAPRAK;
         }

         if (var4 >= 11 && var4 <= 16) {
            if (var4 == 16 && var6 <= (double)7.5F) {
               return var6 >= 6.4 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.HUS_TAHTA;
            }

            if (var6 >= (double)5.5F && var6 <= 6.8) {
               if (var4 > 13 || var2 > 1 && var3 > 1) {
                  if (var2 != var3 && var2 != 0 && var3 != 0) {
                     return NurBaskentFeature.P.CICEKLI_YAPRAK;
                  }

                  return NurBaskentFeature.P.LADIN_GOVDE;
               }

               return NurBaskentFeature.P.HAVA;
            }

            if (var6 > 3.4 && var6 < (double)5.5F) {
               if (var4 == 11 && var2 == 4 && var3 == 0) {
                  return NurBaskentFeature.P.SANDIK;
               }

               if (var4 == 11 && var2 == 0 && var3 == 4) {
                  return NurBaskentFeature.P.KITAP;
               }

               if (var4 == 11 && var2 == 3 && var3 == 3) {
                  return NurBaskentFeature.P.YESIL_YUN;
               }

               return NurBaskentFeature.P.HAVA;
            }
         }

         if (var4 >= 17 && var4 <= 21) {
            double var14 = 7.4 - (double)(var4 - 17) * 1.15;
            if (var6 <= var14) {
               if (!(var6 >= var14 - (double)2.0F) && var4 != 21) {
                  return NurBaskentFeature.P.HAVA;
               }

               return var6 >= var14 - 0.9 ? NurBaskentFeature.P.CICEKLI_YAPRAK : NurBaskentFeature.P.YOSUN;
            }
         }
      }

      if (var4 >= 21 && var4 <= 35) {
         double var15 = (double)var4 - (double)27.0F;
         double var10 = 11.8 - var15 * var15 * 0.22;
         if (var10 > (double)0.0F && var6 <= var10) {
            if (var2 == 0 && var3 == 0 && var4 <= 27) {
               return NurBaskentFeature.P.LADIN_GOVDE;
            }

            int var12 = hash(var0 * 19 + var4, var1 * 23);
            if (var12 % 9 == 0) {
               return NurBaskentFeature.P.MANTAR_ISIK;
            }

            if (var12 % 2 == 0) {
               return NurBaskentFeature.P.CICEKLI_YAPRAK;
            }

            return NurBaskentFeature.P.YAPRAK;
         }
      }

      return null;
   }

   private static BlockState sikElfOrmani(int var0, int var1, int var2) {
      byte var3 = 20;
      int var4 = Math.floorDiv(var0, var3);
      int var5 = Math.floorDiv(var1, var3);
      int var6 = Math.floorMod(var0, var3);
      int var7 = Math.floorMod(var1, var3);
      int var8 = hash(var4 * 13 + 7, var5 * 19 + 3);
      int var9 = 7 + var8 % 6;
      int var10 = 7 + (var8 >> 4) % 6;
      int var11 = 22 + (var8 >> 8) % 10;
      BlockState var12 = uluOrmanAgaci(var6, var7, var2, var9, var10, var11, var8);
      if (var12 != null) {
         return var12;
      } else {
         if (var2 <= 20) {
            byte var13 = 10;
            int var14 = Math.floorDiv(var0, var13);
            int var15 = Math.floorDiv(var1, var13);
            int var16 = Math.floorMod(var0, var13);
            int var17 = Math.floorMod(var1, var13);
            int var18 = hash(var14 * 37 + 11, var15 * 23 + 5);
            if (var18 % 10 < 8) {
               int var19 = 3 + var18 % 4;
               int var20 = 3 + (var18 >> 3) % 4;
               int var21 = 9 + (var18 >> 6) % 6;
               return elfAgaci(var16, var17, var2, var19, var20, var21, var18);
            }
         }

         return null;
      }
   }

   private static BlockState uluOrmanAgaci(int var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = var0 - var3;
      int var8 = var1 - var4;
      int var9 = var7 * var7 + var8 * var8;
      if (var2 < var5) {
         int var10 = var2 < 3 ? 2 : 1;
         if (var9 <= var10) {
            return (var6 & 1) == 0 ? NurBaskentFeature.P.HUS_GOVDE : NurBaskentFeature.P.LADIN_GOVDE;
         }
      }

      int var14 = var2 - var5;
      if (var14 >= -4 && var14 <= 4) {
         double var11 = var14 < 0 ? 5.2 : 5.2 - (double)var14 * 0.95;
         if ((double)var9 <= var11 * var11) {
            if (var9 == 2 && var14 == -2 && (var0 + var1 & 1) == 0) {
               return NurBaskentFeature.P.MANTAR_ISIK;
            }

            int var13 = hash(var0 * 17 + var2, var1 * 31 + var6);
            if (var13 % 18 == 0) {
               return NurBaskentFeature.P.FENER;
            }

            if (var13 % 3 == 0) {
               return NurBaskentFeature.P.CICEKLI_YAPRAK;
            }

            if (var13 % 2 == 0) {
               return NurBaskentFeature.P.ACELYA_YAPRAK;
            }

            return NurBaskentFeature.P.MESE_YAPRAK;
         }
      }

      return null;
   }

   private static BlockState elfAgaci(int var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var0 == var3 && var1 == var4 && var2 < var5) {
         return (var6 & 1) == 0 ? NurBaskentFeature.P.HUS_GOVDE : NurBaskentFeature.P.LADIN_GOVDE;
      } else {
         int var7 = var0 - var3;
         int var8 = var1 - var4;
         int var9 = var2 - var5;
         if (var9 >= -2 && var9 <= 2) {
            double var10 = (double)(var7 * var7 + var8 * var8) + (double)(var9 * var9) * 1.4;
            if (var10 <= 6.2) {
               if (var7 == 0 && var8 == 0 && var9 == 0) {
                  return NurBaskentFeature.P.MANTAR_ISIK;
               }

               int var12 = var6 >> 4 & 3;
               if (var12 == 0) {
                  return NurBaskentFeature.P.YAPRAK;
               }

               if (var12 == 1) {
                  return NurBaskentFeature.P.CICEKLI_YAPRAK;
               }

               if (var12 == 2) {
                  return NurBaskentFeature.P.ACELYA_YAPRAK;
               }

               return NurBaskentFeature.P.MESE_YAPRAK;
            }
         }

         return null;
      }
   }

   private static void kbBulutTabani(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3) {
      double var4 = vn((double)var2 / (double)45.0F + (double)9.0F, (double)var3 / (double)45.0F + (double)14.0F);
      int var6 = (int)(var4 * (double)5.0F);

      for(int var7 = 100; var7 <= 100 + var6; ++var7) {
         set(var0, var1, var2, var7, var3, NurBaskentFeature.P.BULUT);
      }

   }

   private static void satoKolonu(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3, int var4) {
      int var5 = var2 - SCX[var4];
      int var6 = var3 - SCZ[var4];
      int var7 = SR[var4];
      int var8 = var7 + 12;
      if (var5 <= var8 && var5 >= -var8 && var6 <= var8 && var6 >= -var8) {
         double var9 = Math.sqrt((double)var5 * (double)var5 + (double)var6 * (double)var6);
         double var11 = (double)Math.max(Math.abs(var5), Math.abs(var6));
         double var13 = 0.22 * var9 + 0.78 * var11;
         if (!(var13 > (double)var7 + (double)6.5F)) {
            int var15 = SY[var4];
            int var16 = STIP[var4];
            double var17 = Math.min((double)1.0F, var13 / ((double)var7 + (double)2.0F));
            double var19 = (double)(hash(var2 * 11 + var4, var3 * 17) % 6) * ((double)1.0F - var17);
            int var21 = (int)Math.round((double)SD[var4] * Math.pow((double)1.0F - var17 * var17, 1.3) + var19);

            for(int var22 = var15 - 3 - var21; var22 <= var15; ++var22) {
               BlockState var23;
               if (var22 == var15) {
                  if (var13 >= (double)var7 + (double)0.5F) {
                     var23 = NurBaskentFeature.P.ALTIN;
                  } else if ((var5 + var6) % 4 == 0) {
                     var23 = NurBaskentFeature.P.OYMA;
                  } else {
                     var23 = NurBaskentFeature.P.KUVARS;
                  }
               } else if (var22 >= var15 - 3) {
                  var23 = NurBaskentFeature.P.KTUGLA;
               } else if (var22 <= var15 - var21) {
                  var23 = NurBaskentFeature.P.ALTIN;
               } else if (var22 >= var15 - var21 / 2) {
                  var23 = NurBaskentFeature.P.KALSIT;
               } else {
                  var23 = NurBaskentFeature.P.BULUT;
               }

               set(var0, var1, var2, var22, var3, var23);
            }

            if (var16 == 2 && (Math.abs(var5) == var7 && var6 == 0 || Math.abs(var6) == var7 && var5 == 0)) {
               for(int var26 = var15 - 18; var26 <= var15; ++var26) {
                  set(var0, var1, var2, var26, var3, NurBaskentFeature.P.SU);
               }

            } else {
               int var25 = var16 == 2 ? 118 : (var16 == 1 ? 68 : 48);

               for(int var27 = 0; var27 < var25; ++var27) {
                  BlockState var24;
                  if (var16 == 2) {
                     var24 = devasaSatoYapi(var5, var6, var27, var9, var7, var4);
                  } else if (var16 == 1) {
                     var24 = ortaSatoYapi(var5, var6, var27, var9, var7, var4);
                  } else {
                     var24 = kucukSatoYapi(var5, var6, var27, var9, var7, var4);
                  }

                  if (var24 != null) {
                     set(var0, var1, var2, var15 + 1 + var27, var3, var24);
                  }
               }

            }
         }
      }
   }

   private static BlockState satoDuvarBlok(int var0, int var1, int var2, int var3) {
      if (var0 == 1) {
         return (var2 + var1 + var3) % 5 == 0 ? NurBaskentFeature.P.OYMA : NurBaskentFeature.P.KALSIT;
      } else if (var0 == 2) {
         return var2 % 4 == 0 ? NurBaskentFeature.P.HUS_TAHTA : NurBaskentFeature.P.KUVARS;
      } else if (var0 == 3) {
         return (var2 + var1 + var3) % 4 == 0 ? NurBaskentFeature.P.TAS_TUGLA : NurBaskentFeature.P.KARATAS_TUGLA;
      } else if (var0 == 4) {
         return var2 % 5 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
      } else {
         return NurBaskentFeature.P.KTUGLA;
      }
   }

   private static BlockState satoVurguBlok(int var0) {
      if (var0 == 1) {
         return NurBaskentFeature.P.AMETIST;
      } else if (var0 == 2) {
         return NurBaskentFeature.P.ZUMRUT;
      } else if (var0 == 3) {
         return NurBaskentFeature.P.ALTIN;
      } else {
         return var0 == 4 ? NurBaskentFeature.P.PRIZMARIN : NurBaskentFeature.P.ALTIN;
      }
   }

   private static BlockState satoCatiBlok(int var0, int var1) {
      if (var0 == 1) {
         return var1 % 3 == 0 ? NurBaskentFeature.P.CAM_GOK : NurBaskentFeature.P.AMETIST;
      } else if (var0 == 2) {
         return var1 % 3 == 0 ? NurBaskentFeature.P.ALTIN : (var1 % 2 == 0 ? NurBaskentFeature.P.ZUMRUT : NurBaskentFeature.P.PRIZMARIN);
      } else if (var0 == 3) {
         return var1 % 2 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KARATAS_TUGLA;
      } else if (var0 == 4) {
         return var1 % 2 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.ZUMRUT;
      } else {
         return var1 % 3 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.LAPIS;
      }
   }

   private static BlockState satoCamBlok(int var0) {
      return var0 != 1 && var0 != 2 ? NurBaskentFeature.P.CAM_ALTIN : NurBaskentFeature.P.CAM_GOK;
   }

   private static BlockState devasaSatoYapi(int var0, int var1, int var2, double var3, int var5, int var6) {
      int var7 = Math.floorMod(var6, 5);
      int var8 = Math.abs(var0);
      int var9 = Math.abs(var1);
      int var10 = Math.max(var8, var9);
      int var11 = var5 - 3;
      byte var12 = 12;
      BlockState var13 = satoDuvarBlok(var7, var8, var2, var9);
      BlockState var14 = satoVurguBlok(var7);
      BlockState var15 = satoCamBlok(var7);
      int var17 = var8 - var11;
      int var18 = var9 - var11;
      if (var17 * var17 + var18 * var18 <= 32) {
         boolean var19 = (var8 >= var11 - 1 && var8 <= var11 && var9 <= var11 || var9 >= var11 - 1 && var9 <= var11 && var8 <= var11) && var2 <= 18;
         BlockState var20 = satoKulesiStil(var17, var18, var2, 42, false, var7, var19);
         if (var20 != null) {
            return var20;
         }
      }

      if ((var8 < var11 - 1 || var8 > var11 || var9 > var11) && (var9 < var11 - 1 || var9 > var11 || var8 > var11)) {
         if (var7 == 4 && var10 <= var12 + 4) {
            return zigguratSatoYapi(var0, var1, var8, var9, var10, var2, var3, var12);
         } else {
            int var26 = var8 - var12;
            int var28 = var9 - var12;
            if (var26 * var26 + var28 * var28 <= 32) {
               boolean var21 = var10 == var12 && var2 <= 35 || var10 <= var12 && var2 == 35;
               BlockState var22 = satoKulesiStil(var26, var28, var2, 58, false, var7, var21);
               if (var22 != null) {
                  return var22;
               }
            }

            if (var7 == 1 && var10 > var12 && var10 <= var12 + 5 && (var8 <= 1 || var9 <= 1 || Math.abs(var8 - 8) <= 1 || Math.abs(var9 - 8) <= 1)) {
               int var29 = var10 - var12;
               int var36 = 28 - var29 * 3;
               if (var2 >= var36 && var2 <= var36 + 3) {
                  return var2 == var36 + 3 ? NurBaskentFeature.P.AMETIST : NurBaskentFeature.P.KALSIT;
               }

               if (var29 == 5 && var2 <= var36) {
                  return var2 % 5 == 0 ? NurBaskentFeature.P.AMETIST : NurBaskentFeature.P.SUTUN;
               }
            }

            if (var10 > var12) {
               return var8 == var12 + 5 && var9 == var12 + 5 ? agac(var8, var9, var2, var12 + 5, var12 + 5, 6) : null;
            } else if (var2 >= 48) {
               if (var7 == 3) {
                  if (var2 <= 78 && var3 <= 9.2) {
                     if (var2 >= 75) {
                        if (var3 >= 7.6) {
                           return (var0 + var1 + var2 & 1) == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KARATAS_TUGLA;
                        } else if (var2 == 75) {
                           return var3 <= (double)2.5F ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.KARATAS_TUGLA;
                        } else {
                           return null;
                        }
                     } else if (var3 < 7.4) {
                        if (var2 != 48 && var2 != 62) {
                           return var8 == 0 && var9 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.HAVA;
                        } else {
                           return NurBaskentFeature.P.KARATAS_TUGLA;
                        }
                     } else if (var2 != 48 && var2 != 62 && var2 != 74) {
                        if (var2 % 7 < 2 || var2 % 7 > 5 || var8 > 1 && var9 > 1) {
                           return var8 != var9 && var8 > 1 && var9 > 1 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.SUTUN;
                        } else {
                           return NurBaskentFeature.P.KIRMIZI_YUN;
                        }
                     } else {
                        return NurBaskentFeature.P.ALTIN;
                     }
                  } else {
                     return null;
                  }
               } else if (var7 == 2) {
                  if (var2 <= 64 && var3 <= (double)7.5F) {
                     if (!(var3 < 5.8)) {
                        return var2 % 4 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KUVARS;
                     } else {
                        return var2 != 48 && var2 != 64 ? (var8 == 0 && var9 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.HAVA) : NurBaskentFeature.P.KUVARS;
                     }
                  } else {
                     if (var2 > 64 && var2 <= 84) {
                        double var35 = (double)(var2 - 64) / (double)20.0F;
                        double var41 = 8.8 * Math.sin(var35 * Math.PI * 0.88) + 1.2 * ((double)1.0F - var35);
                        if (var3 <= var41) {
                           if (!(var3 >= var41 - 1.8) && var2 < 82) {
                              return NurBaskentFeature.P.HAVA;
                           }

                           return (var2 + var8 + var9) % 3 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.ZUMRUT;
                        }
                     }

                     if (var2 > 84 && var2 <= 94 && var3 <= 1.2) {
                        return var2 == 94 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
                     } else {
                        return null;
                     }
                  }
               } else {
                  if (var2 <= 84) {
                     double var33 = 7.8 - (double)(var2 - 48) * 0.04;
                     if (var3 <= var33) {
                        if (var3 < var33 - 1.55) {
                           if (var2 != 48 && var2 != 84 && var2 % 12 != 0) {
                              if (var8 == 0 && var9 == 0) {
                                 return NurBaskentFeature.P.FENER;
                              }

                              return NurBaskentFeature.P.HAVA;
                           }

                           return NurBaskentFeature.P.KUVARS;
                        }

                        if (var2 != 64 && var2 != 84) {
                           if (var2 % 8 < 3 || var2 % 8 > 6 || var8 > 1 && var9 > 1) {
                              return var8 != var9 && var8 != 0 && var9 != 0 ? var13 : NurBaskentFeature.P.SUTUN;
                           }

                           return var15;
                        }

                        return var14;
                     }
                  } else {
                     double var34 = 6.8 - (double)(var2 - 84) * 0.21;
                     if (var34 > (double)0.0F && var3 <= var34) {
                        if (var34 <= 1.2) {
                           return NurBaskentFeature.P.FENER;
                        }

                        if (var3 >= var34 - 1.8) {
                           return satoCatiBlok(var7, var2 - 84);
                        }

                        return NurBaskentFeature.P.HAVA;
                     }
                  }

                  return null;
               }
            } else if (var2 == 35) {
               return var10 == var12 ? var14 : NurBaskentFeature.P.KUVARS;
            } else if (var2 >= 36) {
               if (var7 == 1) {
                  int var32 = var2 - 36;
                  int var38 = var12 - var32;
                  if (var38 >= 0 && (var8 <= var38 || var9 <= var38)) {
                     if (var8 != var38 && var9 != var38 && var32 != 11) {
                        return NurBaskentFeature.P.HAVA;
                     } else {
                        return var32 % 3 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.AMETIST;
                     }
                  } else {
                     return null;
                  }
               } else if (var2 >= 46 && var3 <= 8.2) {
                  return !(var3 >= (double)7.0F) && var2 != 47 ? var13 : var14;
               } else {
                  double var31 = (double)var2 - (double)36.0F;
                  double var40 = Math.sqrt(var3 * var3 + var31 * var31);
                  if (var3 <= (double)var12 - (double)1.5F && var40 >= 8.2 && var40 <= (double)10.5F) {
                     return var8 > 1 && var9 > 1 && Math.abs(var8 - var9) > 1 && var2 != 42 ? satoCatiBlok(var7, var2) : NurBaskentFeature.P.ALTIN;
                  } else if (var2 == 36 && var3 >= (double)8.0F) {
                     return var10 == var12 ? NurBaskentFeature.P.ALTIN : var13;
                  } else {
                     return var2 == 37 && var10 == var12 && (var0 + var1) % 2 == 0 ? var14 : null;
                  }
               }
            } else {
               boolean var30 = var8 == var12 || var9 == var12;
               if (var30) {
                  int var37 = var8 == var12 ? var9 : var8;
                  if (var37 <= 3 && var2 >= 1 && var2 <= 10) {
                     int var23 = 7 + (3 - var37);
                     if (var2 < var23) {
                        return null;
                     }

                     if (var2 == var23) {
                        return var14;
                     }
                  }

                  if (var37 <= 5 && var2 >= 18 && var2 <= 28) {
                     double var39 = Math.sqrt((double)(var37 * var37 + (var2 - 23) * (var2 - 23)));
                     if (var39 <= (double)5.0F) {
                        if (!(var39 >= (double)4.0F) && var37 != 0 && var2 != 23) {
                           return (var6 + var37 + var2) % 2 == 0 ? NurBaskentFeature.P.CAM_ALTIN : NurBaskentFeature.P.CAM_GOK;
                        }

                        return var14;
                     }
                  }

                  if (var7 == 3 && var2 >= 6 && var2 <= 30 && (var37 == 8 || var37 == 9)) {
                     return var37 == 8 && var2 % 4 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KIRMIZI_YUN;
                  } else if (var2 != 0 && var2 != 14 && var2 != 34) {
                     if (var37 % 5 == 0) {
                        return NurBaskentFeature.P.SUTUN;
                     } else {
                        return var37 % 5 == 2 && var2 >= 4 && var2 <= 31 ? var15 : var13;
                     }
                  } else {
                     return NurBaskentFeature.P.ALTIN;
                  }
               } else if (var8 == 7 && var9 == 7 && var2 <= 34) {
                  return NurBaskentFeature.P.SUTUN;
               } else if (var2 != 14 || var8 < 8 && var9 < 8) {
                  if (var0 >= -var12 + 2 && var0 <= -var12 + 3 && var1 >= -6 && var1 <= 7 && var2 >= 1 && var2 <= 14) {
                     return var1 == -7 + var2 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.HAVA;
                  } else {
                     if (var2 == 0) {
                        if (var8 <= 2 && var9 <= 2) {
                           return NurBaskentFeature.P.ALTIN;
                        }

                        if (var8 == var12 - 1 && var9 <= 5) {
                           return NurBaskentFeature.P.KITAP;
                        }

                        if (var9 == var12 - 1 && var8 == 4) {
                           return NurBaskentFeature.P.SANDIK;
                        }

                        if (var9 == var12 - 1 && var8 == 5) {
                           return NurBaskentFeature.P.ORS;
                        }

                        if (var9 == var12 - 1 && var8 == 6) {
                           return NurBaskentFeature.P.CALISMA_MASASI;
                        }

                        if (var8 <= 3 || var9 <= 3) {
                           return var7 == 3 ? NurBaskentFeature.P.KIRMIZI_HALI : NurBaskentFeature.P.SARI_HALI;
                        }
                     }

                     if ((var2 == 1 || var2 == 2) && var8 == var12 - 1 && var9 <= 5) {
                        return NurBaskentFeature.P.KITAP;
                     } else if (var2 == 1 && var8 == 0 && var9 == 0) {
                        return NurBaskentFeature.P.BUYU_MASASI;
                     } else if (var2 == 1 && var8 == 2 && var9 == 0) {
                        return NurBaskentFeature.P.KURSU;
                     } else {
                        if (var2 == 15) {
                           if (var9 == var12 - 1 && var8 >= 2 && var8 <= 4) {
                              return var8 == 4 ? NurBaskentFeature.P.BEYAZ_YUN : NurBaskentFeature.P.KIRMIZI_YUN;
                           }

                           if (var9 == var12 - 1 && var8 == 6) {
                              return NurBaskentFeature.P.SANDIK;
                           }

                           if (var8 == var12 - 1 && var9 <= 6) {
                              return NurBaskentFeature.P.KITAP;
                           }
                        }

                        if (var2 == 16 && var8 == var12 - 1 && var9 <= 6) {
                           return NurBaskentFeature.P.KITAP;
                        } else if (var8 == 0 && var9 == 0 && var2 >= 26 && var2 <= 34) {
                           return NurBaskentFeature.P.FENER;
                        } else {
                           return NurBaskentFeature.P.HAVA;
                        }
                     }
                  }
               } else {
                  return var7 == 3 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.KUVARS;
               }
            }
         }
      } else {
         int var25 = var8 >= var11 - 1 ? var9 : var8;
         if (var25 <= 4 && var2 >= 1 && var2 <= 14) {
            int var27 = 9 + (int)Math.sqrt((double)Math.max(0, 16 - var25 * var25));
            if (var2 < var27) {
               return null;
            }

            if (var2 == var27) {
               return var14;
            }
         }

         if (var2 <= 18) {
            if (var2 == 18) {
               return (var0 + var1 & 1) == 0 ? var14 : null;
            } else if (var2 != 17 && var2 != 0) {
               if (var7 == 3 && var2 >= 5 && var2 <= 15 && var25 % 8 == 4) {
                  return NurBaskentFeature.P.KIRMIZI_YUN;
               } else if (var2 >= 6 && var2 <= 13 && var25 % 6 == 0) {
                  return var15;
               } else {
                  return var25 % 6 == 3 ? NurBaskentFeature.P.SUTUN : var13;
               }
            } else {
               return NurBaskentFeature.P.ALTIN;
            }
         } else {
            return null;
         }
      }
   }

   private static BlockState zigguratSatoYapi(int var0, int var1, int var2, int var3, int var4, int var5, double var6, int var8) {
      int var9 = var5 / 12;
      int var10 = var5 % 12;
      if (var9 < 4) {
         int var15 = var8 + 3 - var9 * 4;
         if (var4 > var15 + 2) {
            return null;
         } else if (var4 > var15) {
            if (var10 == 0) {
               return var4 == var15 + 2 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.YOSUN;
            } else if (var10 == 1 && var4 == var15 + 2) {
               return (var0 + var1) % 3 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.CICEKLI_YAPRAK;
            } else {
               return var10 <= 3 && var2 == var15 + 1 && var3 == var15 + 1 ? NurBaskentFeature.P.SU : null;
            }
         } else if (var10 == 11) {
            return var4 == var15 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
         } else if (var4 == var15) {
            int var12 = var2 == var15 ? var3 : var2;
            if (var12 <= 2 && var10 >= 1 && var10 <= 6) {
               return NurBaskentFeature.P.HAVA;
            } else if (var12 % 4 == 0) {
               return NurBaskentFeature.P.SUTUN;
            } else if (var10 >= 3 && var10 <= 8 && var12 % 4 == 2) {
               return NurBaskentFeature.P.CAM_GOK;
            } else {
               return var10 != 0 && var10 != 10 ? NurBaskentFeature.P.OYMA : NurBaskentFeature.P.ALTIN;
            }
         } else if (var10 != 0 || var9 != 0 || var2 > 3 && var3 > 3) {
            if (var10 == 1 && var2 == 0 && var3 == 0) {
               return var9 == 0 ? NurBaskentFeature.P.BUYU_MASASI : NurBaskentFeature.P.FENER;
            } else if (var10 == 1 && var2 == var15 - 1 && var3 <= 4) {
               return NurBaskentFeature.P.KITAP;
            } else {
               return var10 == 10 && var2 == 0 && var3 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.HAVA;
            }
         } else {
            return NurBaskentFeature.P.YESIL_HALI;
         }
      } else {
         if (var5 <= 68) {
            double var11 = (double)var5 - (double)48.0F;
            double var13 = Math.sqrt(var6 * var6 + var11 * var11);
            if (var13 <= (double)9.5F) {
               if (!(var13 >= 7.6) && var5 < 65) {
                  return var2 == 0 && var3 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.HAVA;
               }

               return (var5 + var2 + var3) % 2 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.ZUMRUT;
            }
         }

         if (var5 > 68 && var5 <= 76 && var6 <= 1.3) {
            return var5 == 76 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
         } else {
            return null;
         }
      }
   }

   private static BlockState ortaSatoYapi(int var0, int var1, int var2, double var3, int var5, int var6) {
      int var7 = Math.floorMod(var6, 5);
      int var8 = Math.abs(var0);
      int var9 = Math.abs(var1);
      int var10 = var5 - 2;
      BlockState var11 = satoDuvarBlok(var7, var8, var2, var9);
      BlockState var12 = satoVurguBlok(var7);
      int var13 = var8 - var10;
      int var14 = var9 - var10;
      if (var13 * var13 + var14 * var14 <= 28) {
         boolean var15 = (var8 == var10 && var9 <= var10 || var9 == var10 && var8 <= var10) && var2 <= 11;
         BlockState var16 = satoKulesiStil(var13, var14, var2, 28, false, var7, var15);
         if (var16 != null) {
            return var16;
         }
      }

      if (var8 == var10 && var9 <= var10 || var9 == var10 && var8 <= var10) {
         int var17 = var8 == var10 ? var9 : var8;
         if (var17 <= 2 && var2 >= 1 && var2 <= 6) {
            return null;
         } else if (var2 <= 11) {
            if (var2 == 11) {
               return (var0 + var1 & 1) == 0 ? var12 : null;
            } else if (var2 != 10 && var2 != 0) {
               return var2 >= 4 && var2 <= 8 && var17 % 4 == 0 ? satoCamBlok(var7) : var11;
            } else {
               return NurBaskentFeature.P.ALTIN;
            }
         } else {
            return null;
         }
      } else {
         return satoKulesiStil(var0, var1, var2, 44, false, var7, false);
      }
   }

   private static BlockState kucukSatoYapi(int var0, int var1, int var2, double var3, int var5, int var6) {
      int var7 = Math.floorMod(var6, 5);
      int var8 = Math.abs(var0);
      int var9 = Math.abs(var1);
      int var10 = var5 - 3;
      if (Math.abs(var3 - (double)var10) <= 0.8 && var2 <= 2) {
         if (var8 > 1 && var9 > 1) {
            if (var2 == 2) {
               return (var0 + var1) % 4 == 0 ? NurBaskentFeature.P.FENER : null;
            } else {
               return var2 == 1 ? NurBaskentFeature.P.ALTIN : satoVurguBlok(var7);
            }
         } else {
            return null;
         }
      } else {
         return satoKulesiStil(var0, var1, var2, 28, false, var7, false);
      }
   }

   private static BlockState satoKulesi(int var0, int var1, int var2, int var3, boolean var4) {
      return satoKulesiStil(var0, var1, var2, var3, var4, 0, false);
   }

   private static BlockState satoKulesiStil(int var0, int var1, int var2, int var3, boolean var4, int var5, boolean var6) {
      int var7 = Math.abs(var0);
      int var8 = Math.abs(var1);
      double var9 = var5 != 1 && var5 != 3 ? Math.sqrt((double)var0 * (double)var0 + (double)var1 * (double)var1) : 0.35 * Math.sqrt((double)var0 * (double)var0 + (double)var1 * (double)var1) + 0.65 * (double)Math.max(var7, var8);
      double var11 = var4 ? 6.2 : 4.6;
      BlockState var13 = satoDuvarBlok(var5, var7, var2, var8);
      BlockState var14 = satoVurguBlok(var5);
      BlockState var15 = satoCamBlok(var5);
      if (var2 <= var3) {
         if (var9 > var11) {
            return null;
         } else {
            double var23 = var11 - 1.65;
            if (var9 < var23) {
               if (var2 != 0 && var2 != var3 && var2 % 8 != 0) {
                  if (var0 == 0 && var1 == 0) {
                     return NurBaskentFeature.P.FENER;
                  } else if (!var6) {
                     return NurBaskentFeature.P.HAVA;
                  } else {
                     return var2 % 8 < 1 || var2 % 8 > 3 || var7 > 1 && var8 > 1 ? var13 : NurBaskentFeature.P.HAVA;
                  }
               } else {
                  return var5 == 3 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.KUVARS;
               }
            } else if (!var6 && var2 >= 1 && var2 <= 4 && var7 <= 1 && (double)var8 >= var11 - 1.8) {
               return NurBaskentFeature.P.HAVA;
            } else if (var2 != 0 && var2 != var3 && var2 != var3 / 2) {
               if (var2 % 8 < 3 || var2 % 8 > 5 || var7 > 1 && var8 > 1) {
                  return var7 > 1 && var8 > 1 ? var13 : NurBaskentFeature.P.SUTUN;
               } else {
                  return var15;
               }
            } else {
               return var14;
            }
         }
      } else {
         int var16 = var2 - var3;
         if (var5 == 3) {
            double var25 = var11 + 0.6;
            if (var16 <= 4 && var9 <= var25) {
               if (var16 == 1) {
                  return NurBaskentFeature.P.ALTIN;
               } else if (var9 >= var25 - 1.4) {
                  if (var16 == 4) {
                     return (var0 + var1 & 1) == 0 ? NurBaskentFeature.P.ALTIN : null;
                  } else {
                     return NurBaskentFeature.P.KARATAS_TUGLA;
                  }
               } else {
                  return var16 == 2 && var9 <= (double)1.5F ? NurBaskentFeature.P.FENER : null;
               }
            } else {
               return null;
            }
         } else if (var5 == 2) {
            int var24 = var4 ? 14 : 11;
            if (var16 <= var24) {
               double var18 = (double)var16 / (double)var24;
               double var20 = (var11 + 0.8) * Math.sin(var18 * Math.PI * 0.86) + 0.8 * ((double)1.0F - var18);
               if (var9 <= var20) {
                  if (!(var9 >= var20 - 1.7) && var16 < var24 - 1) {
                     return NurBaskentFeature.P.HAVA;
                  }

                  return var16 % 3 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.ZUMRUT;
               }
            } else if (var16 <= var24 + 4 && var9 <= (double)1.0F) {
               return var16 == var24 + 4 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
            }

            return null;
         } else {
            double var17 = var5 == 1 ? 0.26 : 0.34;
            double var19 = var11 + 0.6 - (double)(var16 - 1) * var17;
            if (var19 > 0.35 && var9 <= var19) {
               if (var19 <= 1.3) {
                  return NurBaskentFeature.P.FENER;
               } else {
                  double var21 = Math.max((double)0.0F, var19 - (double)2.0F);
                  return var9 >= var21 ? satoCatiBlok(var5, var16) : NurBaskentFeature.P.HAVA;
               }
            } else {
               return null;
            }
         }
      }
   }

   private static int tabanY(int var0, int var1, double var2) {
      int var4 = Math.abs(var0);
      int var5 = Math.abs(var1);
      boolean var6 = var4 <= 11 || var5 <= 11;
      if (var2 <= (double)80.0F) {
         return 114;
      } else if (var2 < (double)96.0F) {
         if (var6) {
            int var8 = (int)Math.round(((double)96.0F - var2) * (double)8.0F / (double)16.0F);
            return 106 + Math.max(0, Math.min(8, var8));
         } else {
            return 106;
         }
      } else if (var2 <= (double)176.0F) {
         return 106;
      } else if (var2 < (double)190.0F) {
         if (var6) {
            int var7 = (int)Math.round(((double)190.0F - var2) * (double)6.0F / (double)14.0F);
            return 100 + Math.max(0, Math.min(6, var7));
         } else {
            return 100;
         }
      } else {
         return 100;
      }
   }

   private static void sehirKolonu(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3) {
      double var4 = Math.sqrt((double)var2 * (double)var2 + (double)var3 * (double)var3);
      int var6 = tabanY(var2, var3, var4);

      for(int var7 = 100; var7 < var6; ++var7) {
         BlockState var8 = var7 == var6 - 1 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.TUGLA;
         set(var0, var1, var2, var7, var3, var8);
      }

      istinatVeSelale(var0, var1, var2, var3, var4, var6);
      BlockState var12 = zemin(var2, var3, var4, var6);
      if (var12 != null) {
         if (var12 == NurBaskentFeature.P.SU) {
            BlockState var13 = (Math.abs(var2) + Math.abs(var3)) % 4 == 0 ? NurBaskentFeature.P.DFENER : NurBaskentFeature.P.ALTIN;
            set(var0, var1, var2, var6 - 1, var3, var13);
         }

         set(var0, var1, var2, var6, var3, var12);
      }

      boolean var14 = false;
      int var9 = hMax(var2, var3, var4);

      for(int var10 = 0; var10 < var9; ++var10) {
         BlockState var11 = yapi(var2, var3, var10, var4, var6);
         if (var11 != null) {
            if (var10 == 0) {
               var14 = true;
            }

            set(var0, var1, var2, var6 + 1 + var10, var3, var11);
         }
      }

      if (!var14 && var12 == NurBaskentFeature.P.CIM && hash(var2 * 31 + 7, var3 * 17 + 3) % 9 == 0) {
         set(var0, var1, var2, var6 + 1, var3, NurBaskentFeature.P.CICEK[hash(var2, var3) % NurBaskentFeature.P.CICEK.length]);
      }

      if (var4 >= (double)28.0F && var4 <= (double)44.0F) {
         gokselHale(var0, var1, var2, var3, var4);
      }

   }

   private static void istinatVeSelale(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3, double var4, int var6) {
      int var7 = Math.abs(var2);
      int var8 = Math.abs(var3);
      boolean var9 = (var7 == 11 || var7 == 12) && var8 >= 78 && var8 <= 192 || (var8 == 11 || var8 == 12) && var7 >= 78 && var7 <= 192;
      if (!var9 || (!(var4 >= (double)80.0F) || !(var4 <= (double)96.0F)) && (!(var4 >= (double)176.0F) || !(var4 <= (double)190.0F))) {
         if (var4 > (double)176.0F && var4 <= 178.2 && var7 > 12 && var8 > 12) {
            boolean var10 = Math.abs(var7 - var8) <= 3;

            for(int var11 = 101; var11 <= 106; ++var11) {
               if (var10) {
                  set(var0, var1, var2, var11, var3, NurBaskentFeature.P.SU);
                  set(var0, var1, var2, 100, var3, NurBaskentFeature.P.DFENER);
               } else if (var11 == 106) {
                  set(var0, var1, var2, var11, var3, NurBaskentFeature.P.ALTIN);
               } else if ((var2 + var3) % 4 == 0) {
                  set(var0, var1, var2, var11, var3, NurBaskentFeature.P.SUTUN);
               } else {
                  set(var0, var1, var2, var11, var3, NurBaskentFeature.P.OYMA);
               }
            }

            if (!var10 && (var2 + var3) % 8 == 0) {
               set(var0, var1, var2, 107, var3, NurBaskentFeature.P.FENER);
            }
         }

         if (var4 > (double)80.0F && var4 <= 82.2 && var7 > 12 && var8 > 12) {
            boolean var12 = Math.abs(var7 - var8) <= 2;

            for(int var13 = 107; var13 <= 114; ++var13) {
               if (var12) {
                  set(var0, var1, var2, var13, var3, NurBaskentFeature.P.SU);
                  set(var0, var1, var2, 106, var3, NurBaskentFeature.P.DFENER);
               } else if (var13 == 114) {
                  set(var0, var1, var2, var13, var3, NurBaskentFeature.P.ALTIN);
               } else if ((var2 + var3) % 4 == 0) {
                  set(var0, var1, var2, var13, var3, NurBaskentFeature.P.SUTUN);
               } else if (var13 == 110) {
                  set(var0, var1, var2, var13, var3, NurBaskentFeature.P.OYMA);
               } else {
                  set(var0, var1, var2, var13, var3, NurBaskentFeature.P.KTUGLA);
               }
            }

            if (!var12 && (var2 + var3) % 6 == 0) {
               set(var0, var1, var2, 115, var3, NurBaskentFeature.P.FENER);
            }
         }

      } else {
         set(var0, var1, var2, var6 + 1, var3, NurBaskentFeature.P.OYMA);
         if ((int)Math.round(var4) % 4 == 0) {
            set(var0, var1, var2, var6 + 2, var3, NurBaskentFeature.P.ALTIN);
            set(var0, var1, var2, var6 + 3, var3, NurBaskentFeature.P.FENER);
         }

      }
   }

   private static void gokselHale(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3, double var4) {
      double var6 = var4 - (double)36.0F;
      if (!(Math.abs(var6) > 2.6)) {
         double var8 = Math.atan2((double)var3, (double)var2);
         int var10 = 214 + (int)Math.round((double)4.0F * Math.sin(var8 * (double)2.0F));

         for(int var11 = -1; var11 <= 1; ++var11) {
            double var12 = Math.sqrt(var6 * var6 + (double)(var11 * var11));
            if (var12 <= 2.2) {
               BlockState var14;
               if (var12 <= 0.9) {
                  var14 = NurBaskentFeature.P.FENER;
               } else if (var11 != 0 && (var2 + var3 & 3) != 0) {
                  var14 = NurBaskentFeature.P.CAM_ALTIN;
               } else {
                  var14 = NurBaskentFeature.P.ALTIN;
               }

               set(var0, var1, var2, var10 + var11, var3, var14);
            }
         }

      }
   }

   private static BlockState zemin(int var0, int var1, double var2, int var4) {
      int var5 = Math.abs(var0);
      int var6 = Math.abs(var1);
      int var7 = heykelNo(var0, var1);
      if (var7 >= 0) {
         double var8 = (double)HEYKEL[var7][4] / (double)100.0F;
         if (heykelMesafe(var7, var0, var1) <= (double)11.0F * var8) {
            return NurBaskentFeature.P.KUVARS;
         }
      }

      int var16 = var5 - 42;
      int var9 = var6 - 42;
      int var10 = var16 * var16 + var9 * var9;
      if (var10 <= 49) {
         if (var10 <= 4) {
            return NurBaskentFeature.P.ALTIN;
         } else {
            return var10 <= 32 ? NurBaskentFeature.P.SU : NurBaskentFeature.P.OYMA;
         }
      } else {
         boolean var11 = var5 <= 15 && var1 <= 410 && var1 >= -370;
         boolean var12 = var6 <= 15 && var5 <= 370;
         if (!var11 && !var12) {
            if (var2 <= (double)80.0F) {
               if (var2 <= (double)52.0F) {
                  if (var5 <= 2 && var6 <= 2) {
                     return NurBaskentFeature.P.FENER;
                  } else if (!(Math.abs(var2 - (double)18.0F) < 1.2) && !(Math.abs(var2 - (double)32.0F) < 1.2)) {
                     if ((var5 + var6) % 6 == 0) {
                        return NurBaskentFeature.P.OYMA;
                     } else {
                        return (var0 + var1 & 1) == 0 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.TUGLA;
                     }
                  } else {
                     return NurBaskentFeature.P.ALTIN;
                  }
               } else if (var2 >= (double)56.0F && var2 <= (double)65.0F) {
                  return NurBaskentFeature.P.SU;
               } else if (var2 > (double)65.0F && var2 <= (double)74.0F) {
                  return (var5 + var6) % 3 == 0 ? NurBaskentFeature.P.OYMA : NurBaskentFeature.P.KUVARS;
               } else {
                  return NurBaskentFeature.P.KALSIT;
               }
            } else {
               int var17 = panteonNo(var0, var1);
               if (var17 >= 0) {
                  double var18 = panteonMesafe(var17, var0, var1);
                  if (var18 <= (double)16.0F) {
                     return NurBaskentFeature.P.KUVARS;
                  }

                  if (var18 <= (double)19.0F) {
                     return NurBaskentFeature.P.SU;
                  }

                  if (var18 <= (double)21.5F) {
                     return NurBaskentFeature.P.OYMA;
                  }
               }

               if ((!(var2 >= (double)96.0F) || !(var2 <= (double)102.0F)) && (!(var2 >= (double)172.0F) || !(var2 <= (double)194.0F)) && (!(var2 >= (double)352.0F) || !(var2 <= (double)374.0F))) {
                  if (var1 >= 340 && var1 <= 385 && var5 <= 36) {
                     if (var5 <= 2 && Math.abs(var1 - 354) <= 2) {
                        return NurBaskentFeature.P.ALTIN;
                     } else {
                        return (var5 + var6) % 5 == 0 ? NurBaskentFeature.P.OYMA : NurBaskentFeature.P.KUVARS;
                     }
                  } else if (var2 <= (double)352.0F) {
                     Hc var19 = hucre(var0, var1);
                     if (var19.lx >= 3 && var19.lx < 33 && var19.lz >= 3 && var19.lz < 33) {
                        if (var19.bina && var19.ax <= var19.half + 2 && var19.az <= var19.half + 2) {
                           return var19.ax != var19.half + 2 && var19.az != var19.half + 2 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.OYMA;
                        } else {
                           return NurBaskentFeature.P.CIM;
                        }
                     } else if (var19.lx != 0 && var19.lz != 0) {
                        return (var0 + var1 & 1) == 0 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.KALSIT;
                     } else {
                        return NurBaskentFeature.P.ALTIN;
                     }
                  } else if (var2 <= (double)420.0F) {
                     return (var0 + var1) % 3 == 0 ? NurBaskentFeature.P.KALSIT : NurBaskentFeature.P.CIM;
                  } else {
                     return null;
                  }
               } else if ((int)Math.round(var2) % 4 == 0 && (var0 + var1) % 6 == 0) {
                  return NurBaskentFeature.P.ALTIN;
               } else {
                  return (var0 + var1 & 1) == 0 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.TUGLA;
               }
            }
         } else {
            int var13 = var5 > 15 || var6 <= 15 && var5 > var6 ? var6 : var5;
            int var14 = var13 == var5 ? var6 : var5;
            if (var13 == 0) {
               return var14 % 6 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
            } else if (var13 <= 6) {
               if ((var5 + var6) % 8 == 0) {
                  return NurBaskentFeature.P.ALTIN;
               } else {
                  return (var5 + var6) % 2 == 0 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.KALSIT;
               }
            } else if (var13 != 7 && var13 != 8) {
               if (var13 >= 9 && var13 <= 11) {
                  return NurBaskentFeature.P.TUGLA;
               } else {
                  return var13 >= 12 && var13 <= 14 && var2 > (double)98.0F && var2 < (double)350.0F && Math.abs(var2 - (double)183.0F) > (double)10.0F ? NurBaskentFeature.P.SU : NurBaskentFeature.P.KUVARS;
               }
            } else {
               return NurBaskentFeature.P.OYMA;
            }
         }
      }
   }

   private static int hMax(int var0, int var1, double var2) {
      int var4 = Math.abs(var0);
      int var5 = Math.abs(var1);
      if (var4 <= 42 && var5 <= 44) {
         return 226;
      } else if (heykelNo(var0, var1) >= 0) {
         return 108;
      } else if (panteonNo(var0, var1) >= 0) {
         return 52;
      } else if (var4 <= 30 && Math.abs(var1 - 368) <= 10) {
         return 54;
      } else if (var2 >= (double)348.0F && var2 <= (double)385.0F) {
         return 46;
      } else {
         return var2 <= (double)80.0F ? 36 : 42;
      }
   }

   private static BlockState yapi(int var0, int var1, int var2, double var3, int var5) {
      int var6 = Math.abs(var0);
      int var7 = Math.abs(var1);
      int var8 = heykelNo(var0, var1);
      if (var8 >= 0) {
         return heykel(var8, var0, var1, var2);
      } else {
         int var9 = var6 - 42;
         int var10 = var7 - 42;
         int var11 = var9 * var9 + var10 * var10;
         if (var11 <= 49) {
            if (var11 == 0) {
               if (var2 <= 12) {
                  return var2 % 4 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.SUTUN;
               } else {
                  return var2 <= 16 ? NurBaskentFeature.P.FENER : null;
               }
            } else if (var11 <= 4 && var2 <= 4) {
               return var2 == 4 ? NurBaskentFeature.P.SU : NurBaskentFeature.P.OYMA;
            } else {
               return var11 > 32 && var2 == 0 ? NurBaskentFeature.P.OYMA : null;
            }
         } else if (var6 <= 38 && var7 <= 42) {
            return saray(var0, var1, var2);
         } else {
            if (var3 >= (double)54.0F && var3 <= (double)74.0F) {
               BlockState var12 = peristil(var0, var1, var2, var3);
               if (var12 != null) {
                  return var12;
               }
            }

            int var21 = var1 - 368;
            if (var6 <= 28 && var21 >= -9 && var21 <= 9) {
               return kapi(var0, var21, var2, var6);
            } else {
               if (var3 > (double)76.0F && var3 < (double)352.0F && Math.abs(var3 - (double)183.0F) > (double)9.0F && Math.abs(var3 - (double)88.0F) > (double)9.0F) {
                  BlockState var13 = bulvarRevak(var0, var1, var2, var6, var7);
                  if (var13 != null) {
                     return var13;
                  }
               }

               int var22 = panteonNo(var0, var1);
               if (var22 >= 0) {
                  return panteon(var22, var0, var1, var2);
               } else {
                  if (var3 > (double)350.0F && var3 < (double)386.0F && var2 <= 44) {
                     int var14 = (int)Math.round(Math.atan2((double)var1, (double)var0) / (Math.PI / 8D));
                     double var15 = (double)var14 * Math.PI / (double)8.0F;
                     int var17 = (int)Math.round((double)368.0F * Math.cos(var15));
                     int var18 = (int)Math.round((double)368.0F * Math.sin(var15));
                     if (Math.abs(var17) >= 32 || var18 <= 330) {
                        int var19 = var0 - var17;
                        int var20 = var1 - var18;
                        if (var19 * var19 + var20 * var20 <= 46) {
                           return kule(var19, var20, var2, 32);
                        }
                     }
                  }

                  if (var3 >= (double)365.0F && var3 <= (double)371.0F && var2 <= 16) {
                     boolean var23 = var6 <= 8 || var7 <= 8;
                     if (var23 && var2 <= 10) {
                        int var24 = Math.min(var6, var7);
                        int var16 = 7 + (int)Math.sqrt((double)Math.max(0, 64 - var24 * var24)) / 2;
                        if (var2 < var16) {
                           return null;
                        }

                        if (var2 == var16) {
                           return NurBaskentFeature.P.ALTIN;
                        }
                     }

                     if (var2 == 16) {
                        return (var0 + var1 & 1) == 0 ? NurBaskentFeature.P.OYMA : null;
                     } else if (var2 == 15) {
                        return NurBaskentFeature.P.ALTIN;
                     } else if (var2 == 14) {
                        return NurBaskentFeature.P.KUVARS;
                     } else if (var2 >= 5 && var2 <= 11 && (var0 + var1 & 7) == 0) {
                        return NurBaskentFeature.P.CAM_ALTIN;
                     } else {
                        return (var0 + var1 & 7) == 4 ? NurBaskentFeature.P.SUTUN : NurBaskentFeature.P.TUGLA;
                     }
                  } else {
                     return var3 >= (double)98.0F && var3 <= (double)352.0F && var6 >= 18 && var7 >= 18 ? semt(var0, var1, var2, var3) : null;
                  }
               }
            }
         }
      }
   }

   private static BlockState bulvarRevak(int var0, int var1, int var2, int var3, int var4) {
      if (var2 > 11) {
         return null;
      } else {
         boolean var5 = var3 == 10 && var4 > 16;
         boolean var6 = var4 == 10 && var3 > 16;
         if (!var5 && !var6) {
            return null;
         } else {
            int var7 = var5 ? Math.floorMod(var1, 8) : Math.floorMod(var0, 8);
            if (var7 == 0) {
               if (var2 > 8) {
                  if (var2 == 9) {
                     return NurBaskentFeature.P.ALTIN;
                  } else {
                     return var2 == 10 ? NurBaskentFeature.P.FENER : null;
                  }
               } else {
                  return var2 != 0 && var2 != 8 ? NurBaskentFeature.P.SUTUN : NurBaskentFeature.P.OYMA;
               }
            } else {
               int var8 = Math.abs(var7 - 4);
               int var9 = 6 + (4 - var8) / 2;
               if (var2 == var9) {
                  return NurBaskentFeature.P.OYMA;
               } else if (var2 == 9) {
                  return NurBaskentFeature.P.TUGLA;
               } else if (var2 == 10) {
                  return var7 == 4 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
               } else {
                  return null;
               }
            }
         }
      }
   }

   private static BlockState peristil(int var0, int var1, int var2, double var3) {
      int var5 = Math.abs(var0);
      int var6 = Math.abs(var1);
      if ((var5 <= 9 || var6 <= 9) && var3 >= (double)55.5F && var3 <= (double)65.5F) {
         int var18 = Math.min(var5, var6);
         if (var18 == 9 && var2 <= 1) {
            return var2 == 1 && (int)Math.round(var3) % 3 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.OYMA;
         } else {
            return null;
         }
      } else {
         if (var5 > 10 && var6 > 10) {
            double var7 = Math.atan2((double)var1, (double)var0);
            int var9 = (int)Math.round(var7 / 0.2617993877991494);
            double var10 = (double)var9 * 0.2617993877991494;
            double var12 = (double)69.0F * Math.cos(var10);
            double var14 = (double)69.0F * Math.sin(var10);
            double var16 = ((double)var0 - var12) * ((double)var0 - var12) + ((double)var1 - var14) * ((double)var1 - var14);
            if (var16 <= 2.2) {
               if (var2 == 0 || var2 == 13) {
                  return NurBaskentFeature.P.ALTIN;
               }

               if (var2 < 13) {
                  return NurBaskentFeature.P.SUTUN;
               }
            }

            if (var3 >= (double)67.5F && var3 <= (double)70.5F) {
               if (var2 == 14) {
                  return NurBaskentFeature.P.OYMA;
               }

               if (var2 == 15) {
                  return NurBaskentFeature.P.ALTIN;
               }

               if (var2 == 16 && var16 <= 2.2) {
                  return NurBaskentFeature.P.FENER;
               }
            }
         }

         return null;
      }
   }

   private static int panteonNo(int var0, int var1) {
      for(int var2 = 0; var2 < 8; ++var2) {
         if (panteonMesafe(var2, var0, var1) <= (double)18.0F) {
            return var2;
         }
      }

      return -1;
   }

   private static double panteonMesafe(int var0, int var1, int var2) {
      double var3 = (double)(var1 - PX[var0]);
      double var5 = (double)(var2 - PZ[var0]);
      return Math.sqrt(var3 * var3 + var5 * var5);
   }

   private static BlockState panteon(int var0, int var1, int var2, int var3) {
      double var4 = (double)(var1 - PX[var0]);
      double var6 = (double)(var2 - PZ[var0]);
      double var8 = Math.sqrt(var4 * var4 + var6 * var6);
      if (var3 <= 2) {
         double var19 = (double)16.0F - (double)var3 * (double)1.0F;
         if (var8 <= var19) {
            if (var8 >= var19 - 1.2) {
               return var3 == 2 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
            } else {
               return NurBaskentFeature.P.KUVARS;
            }
         } else {
            return null;
         }
      } else if (var8 <= 2.4 && var3 >= 3 && var3 <= 14) {
         if (var8 <= 0.8) {
            return var3 % 2 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.CAM_ALTIN;
         } else if (var3 == 3) {
            return NurBaskentFeature.P.ALTIN;
         } else {
            return var3 == 4 && var8 <= 1.6 ? NurBaskentFeature.P.AMETIST : null;
         }
      } else {
         if (var3 >= 3 && var3 <= 15) {
            for(int var10 = 0; var10 < 12; ++var10) {
               double var11 = (double)var10 * (Math.PI / 6D);
               double var13 = (double)12.5F * Math.cos(var11);
               double var15 = (double)12.5F * Math.sin(var11);
               if ((var4 - var13) * (var4 - var13) + (var6 - var15) * (var6 - var15) <= 1.3) {
                  if (var3 != 3 && var3 != 15) {
                     return NurBaskentFeature.P.SUTUN;
                  }

                  return NurBaskentFeature.P.ALTIN;
               }
            }
         }

         if (var3 >= 16 && var3 <= 18 && var8 >= 10.2 && var8 <= (double)14.0F) {
            return var3 == 17 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
         } else {
            if (var3 >= 18 && var3 <= 31) {
               if (var3 >= 29 && var8 <= 4.8) {
                  return !(var8 >= 3.6) && var3 != 30 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN;
               }

               double var18 = (double)var3 - (double)18.0F;
               double var12 = Math.sqrt(var8 * var8 + var18 * var18);
               if (var12 >= 10.6 && var12 <= (double)13.0F) {
                  double var14 = Math.atan2(var6, var4) * (double)6.0F / Math.PI;
                  double var16 = var14 - Math.floor(var14);
                  if (!(var16 < 0.22) && !(var16 > 0.78) && var3 != 24 && var3 != 30) {
                     return (var3 + var0) % 2 == 0 ? NurBaskentFeature.P.CAM_GOK : NurBaskentFeature.P.CAM;
                  }

                  return NurBaskentFeature.P.ALTIN;
               }
            }

            if (var3 >= 31 && var3 <= 46 && var8 <= 2.2) {
               if (var8 <= 0.8) {
                  return NurBaskentFeature.P.FENER;
               }

               if (var3 == 31 || var3 == 36) {
                  return NurBaskentFeature.P.ALTIN;
               }

               if (var3 < 36) {
                  return NurBaskentFeature.P.CAM_ALTIN;
               }
            }

            return null;
         }
      }
   }

   private static BlockState kule(int var0, int var1, int var2, int var3) {
      double var4 = (double)(var0 * var0 + var1 * var1);
      if (var2 <= var3) {
         double var8 = var2 <= 3 ? (double)38.0F : (double)30.25F;
         if (var4 > var8) {
            return null;
         } else if (var4 < (double)12.25F) {
            return var2 != 0 && var2 != var3 && var2 % 8 != 0 ? NurBaskentFeature.P.HAVA : NurBaskentFeature.P.KUVARS;
         } else if (!(var4 >= (double)20.0F) || Math.abs(var0) > 1 && Math.abs(var1) > 1) {
            if (var2 != var3 && var2 != var3 / 2) {
               return var2 % 10 == 9 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KTUGLA;
            } else {
               return var4 >= (double)25.0F ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
            }
         } else {
            return var2 % 10 >= 5 && var2 % 10 <= 7 ? NurBaskentFeature.P.CAM_ALTIN : NurBaskentFeature.P.SUTUN;
         }
      } else {
         double var6 = (double)6.5F - (double)(var2 - var3 - 1) * 0.32;
         if (var6 > 0.4 && var4 <= var6 * var6) {
            if (var6 < 1.3) {
               return NurBaskentFeature.P.FENER;
            } else {
               return (var2 - var3) % 3 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KUVARS;
            }
         } else {
            return null;
         }
      }
   }

   private static BlockState kapi(int var0, int var1, int var2, int var3) {
      int var4 = var3 - 18;
      if (Math.abs(var4) <= 7 && Math.abs(var1) <= 7) {
         return kule(var4, var1, var2, 40);
      } else if (var3 < 12 && Math.abs(var1) <= 3) {
         int var5 = 14 + (int)Math.sqrt((double)Math.max(0, 81 - var3 * var3));
         if (var3 <= 8 && var2 < var5) {
            if (var2 >= var5 - 1) {
               return NurBaskentFeature.P.ALTIN;
            } else {
               return var2 >= 14 && var1 == 0 ? NurBaskentFeature.P.CAM_ALTIN : null;
            }
         } else if (var2 <= 34) {
            if (var2 != 22 && var2 != 34) {
               if (var2 == 28 && var3 <= 3) {
                  return NurBaskentFeature.P.FENER;
               } else if (var3 % 4 == 0 && Math.abs(var1) == 3) {
                  return NurBaskentFeature.P.SUTUN;
               } else if (var2 >= 24 && var2 <= 31 && var3 <= 6 && Math.abs(var1) <= 1) {
                  return (var3 + var2) % 2 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.CAM_GOK;
               } else {
                  return NurBaskentFeature.P.KTUGLA;
               }
            } else {
               return NurBaskentFeature.P.ALTIN;
            }
         } else {
            int var6 = 35 + Math.max(0, 6 - var3 / 2);
            if (var2 <= var6) {
               if (var2 == var6) {
                  return var3 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
               } else {
                  return NurBaskentFeature.P.OYMA;
               }
            } else {
               return null;
            }
         }
      } else {
         if (var3 >= 22 && Math.abs(var1) <= 2) {
            if (var2 <= 15) {
               return var2 == 15 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KTUGLA;
            }

            if (var2 == 16 && var3 % 2 == 0) {
               return NurBaskentFeature.P.OYMA;
            }
         }

         return null;
      }
   }

   private static Hc hucre(int var0, int var1) {
      byte var2 = 36;
      int var3 = Math.abs(var0);
      int var4 = Math.abs(var1);
      int var5 = Math.max(0, var3 - 18);
      int var6 = Math.max(0, var4 - 18);
      int var7 = var5 / var2;
      int var8 = var6 / var2;
      Hc var9 = new Hc();
      var9.lx = var5 % var2;
      var9.lz = var6 % var2;
      int var10 = var0 >= 0 ? 1 : -1;
      int var11 = var1 >= 0 ? 1 : -1;
      var9.ccx = (double)var10 * ((double)18.0F + (double)(var7 * var2) + (double)18.0F);
      var9.ccz = (double)var11 * ((double)18.0F + (double)(var8 * var2) + (double)18.0F);
      var9.h = hash(var7 * 19 + var10 * 300, var8 * 29 + var11 * 300);
      var9.ax = Math.abs(var9.lx - 18);
      var9.az = Math.abs(var9.lz - 18);
      if (var3 >= 18 && var4 >= 18) {
         double var12 = Math.abs(var9.ccx);
         double var14 = Math.abs(var9.ccz);
         double var16 = Math.sqrt(Math.max((double)0.0F, var12 - (double)12.0F) * Math.max((double)0.0F, var12 - (double)12.0F) + Math.max((double)0.0F, var14 - (double)12.0F) * Math.max((double)0.0F, var14 - (double)12.0F));
         double var18 = Math.sqrt((var12 + (double)12.0F) * (var12 + (double)12.0F) + (var14 + (double)12.0F) * (var14 + (double)12.0F));

         for(int var20 = 0; var20 < 8; ++var20) {
            if (panteonMesafe(var20, (int)var9.ccx, (int)var9.ccz) <= (double)34.0F) {
               var9.bina = false;
               return var9;
            }
         }

         for(int var23 = 0; var23 < HEYKEL.length; ++var23) {
            if (heykelMesafe(var23, (int)var9.ccx, (int)var9.ccz) <= (double)42.0F) {
               var9.bina = false;
               return var9;
            }
         }

         boolean var24 = var16 >= (double)104.0F && var18 <= (double)170.0F;
         boolean var21 = var16 >= (double)196.0F && var18 <= (double)348.0F;
         int var22 = var24 ? 75 : (var21 ? 70 : 0);
         if ((var24 || var21) && var9.h % 100 < var22) {
            var9.bina = true;
            var9.tip = var9.h >> 5 & 1;
            var9.half = 6 + (var9.h >> 7) % 3;
            var9.H = 9 + (var9.h >> 11) % 5;
         }

         return var9;
      } else {
         var9.bina = false;
         return var9;
      }
   }

   private static BlockState semt(int var0, int var1, int var2, double var3) {
      Hc var5 = hucre(var0, var1);
      if (var5.lx >= 3 && var5.lx < 33 && var5.lz >= 3 && var5.lz < 33) {
         if ((var5.lx == 3 || var5.lx == 32) && (var5.lz == 3 || var5.lz == 32) && var2 <= 5) {
            if (var2 == 0) {
               return NurBaskentFeature.P.ALTIN;
            } else {
               return var2 <= 4 ? NurBaskentFeature.P.SUTUN : NurBaskentFeature.P.FENER;
            }
         } else if (var5.bina) {
            BlockState var12 = var5.tip == 0 ? tapinakKosk(var5, var2) : revakliVilla(var5, var2);
            if (var12 != null) {
               return var12;
            } else {
               int var13 = (var5.h & 1) == 0 ? 6 : 30;
               int var14 = (var5.h & 2) == 0 ? 6 : 30;
               return agac(var5.lx, var5.lz, var2, var13, var14, 5 + (var5.h >> 4 & 2));
            }
         } else {
            if (var5.ax <= 3 && var5.az <= 3 && var2 <= 7) {
               int var6 = var5.ax * var5.ax + var5.az * var5.az;
               if (var2 == 0 && var6 <= 9) {
                  return var6 <= 4 ? NurBaskentFeature.P.SU : NurBaskentFeature.P.OYMA;
               }

               if (var5.ax == 3 && var5.az == 3 && var2 <= 5) {
                  return NurBaskentFeature.P.SUTUN;
               }

               if (var2 == 6 && var6 <= 10) {
                  return NurBaskentFeature.P.ALTIN;
               }

               if (var2 == 7 && var6 <= 4) {
                  return var6 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.KUVARS;
               }
            }

            for(int var11 = 0; var11 < 3; ++var11) {
               int var7 = hash(var5.h, var11 + 77);
               int var8 = 7 + (var7 & 7) + var11 * 6;
               int var9 = 7 + (var7 >> 5 & 19);
               if (Math.abs(var8 - 18) > 5 || Math.abs(var9 - 18) > 5) {
                  BlockState var10 = agac(var5.lx, var5.lz, var2, var8, var9, 5 + (var7 >> 10) % 3);
                  if (var10 != null) {
                     return var10;
                  }
               }
            }

            return null;
         }
      } else if ((var5.lx == 3 || var5.lx == 32) && (var5.lz == 3 || var5.lz == 32) && var2 <= 5) {
         if (var2 == 0) {
            return NurBaskentFeature.P.ALTIN;
         } else {
            return var2 <= 4 ? NurBaskentFeature.P.SUTUN : NurBaskentFeature.P.FENER;
         }
      } else {
         return null;
      }
   }

   private static BlockState agac(int var0, int var1, int var2, int var3, int var4, int var5) {
      if (var0 == var3 && var1 == var4 && var2 < var5) {
         return NurBaskentFeature.P.GOVDE;
      } else {
         int var6 = var0 - var3;
         int var7 = var1 - var4;
         int var8 = var2 - var5;
         if (var8 >= -2 && var8 <= 3) {
            double var9 = (double)(var6 * var6 + var7 * var7) + (double)(var8 * var8) * (double)1.5F;
            if (var9 <= (double)8.5F) {
               if (var6 == 0 && var7 == 0 && var8 == 0) {
                  return NurBaskentFeature.P.FENER;
               }

               return NurBaskentFeature.P.YAPRAK;
            }
         }

         return null;
      }
   }

   private static boolean kapiYuzu(Hc var0, int var1) {
      return var0.ax == var1 && var0.az <= 1 || var0.az == var1 && var0.ax <= 1;
   }

   private static BlockState tapinakKosk(Hc var0, int var1) {
      int var2 = var0.half;
      int var3 = var0.H;
      if (var0.ax <= var2 + 1 && var0.az <= var2 + 1) {
         if (var1 == 0) {
            return var0.ax != var2 + 1 && var0.az != var2 + 1 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN;
         } else if (var1 <= var3) {
            if (var0.ax <= var2 && var0.az <= var2) {
               boolean var9 = var0.ax == var2 || var0.az == var2;
               if (!var9) {
                  if (var1 != var3) {
                     if (var1 == 1) {
                        if (var0.ax == 0 && var0.az == 0) {
                           return NurBaskentFeature.P.BUYU_MASASI;
                        }

                        if (var0.ax == var2 - 1 && var0.az <= 2) {
                           return NurBaskentFeature.P.KITAP;
                        }

                        if (var0.az == var2 - 1 && var0.ax == 2) {
                           return NurBaskentFeature.P.SANDIK;
                        }

                        if (var0.az == var2 - 1 && var0.ax == 3) {
                           return NurBaskentFeature.P.KURSU;
                        }

                        if (var0.ax <= 2 && var0.az <= 2) {
                           return NurBaskentFeature.P.SARI_HALI;
                        }
                     }

                     if (var1 == 2 && var0.ax == var2 - 1 && var0.az <= 2) {
                        return NurBaskentFeature.P.KITAP;
                     } else {
                        return var0.ax == 0 && var0.az == 0 && var1 == var3 - 1 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.HAVA;
                     }
                  } else {
                     return var0.ax <= 1 && var0.az <= 1 ? NurBaskentFeature.P.CAM_ALTIN : NurBaskentFeature.P.KUVARS;
                  }
               } else if (var1 <= 4 && kapiYuzu(var0, var2)) {
                  return NurBaskentFeature.P.HAVA;
               } else if ((var0.ax != var2 || var0.az != var2) && (var0.ax != var2 || var0.az != 3) && (var0.az != var2 || var0.ax != 3)) {
                  if (var1 == var3) {
                     return NurBaskentFeature.P.ALTIN;
                  } else if (var1 == var3 - 1) {
                     return NurBaskentFeature.P.OYMA;
                  } else if (var1 >= 3 && var1 <= var3 - 3 && (var0.ax <= 1 || var0.az <= 1)) {
                     return var1 == var3 - 3 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.CAM_GOK;
                  } else {
                     return NurBaskentFeature.P.KTUGLA;
                  }
               } else {
                  return NurBaskentFeature.P.SUTUN;
               }
            } else if (var1 == var3) {
               return NurBaskentFeature.P.ALTIN;
            } else if (var0.ax == var2 + 1 && var0.az == var2 + 1) {
               return var1 == var3 - 1 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.SUTUN;
            } else {
               return null;
            }
         } else {
            int var4 = var1 - var3;
            double var5 = Math.sqrt((double)var0.ax * (double)var0.ax + (double)var0.az * (double)var0.az);
            double var7 = (double)var2 + 0.8 - (double)var4 * (double)1.0F;
            if (var7 < (double)0.0F) {
               return null;
            } else if (!(var5 <= var7)) {
               return null;
            } else if (var7 <= 1.6) {
               return NurBaskentFeature.P.FENER;
            } else if (!(var5 >= var7 - 2.2)) {
               return NurBaskentFeature.P.HAVA;
            } else {
               return var0.ax != 0 && var0.az != 0 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN;
            }
         }
      } else {
         return null;
      }
   }

   private static BlockState revakliVilla(Hc var0, int var1) {
      int var2 = var0.half;
      int var3 = var0.H;
      if (var0.ax <= var2 + 1 && var0.az <= var2 + 1) {
         if (var1 == 0) {
            return var0.ax != var2 + 1 && var0.az != var2 + 1 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.OYMA;
         } else if (var1 < var3) {
            if (var0.ax != var2 + 1 && var0.az != var2 + 1) {
               if (var0.ax < var2 && var0.az < var2) {
                  if (var1 == var3 - 1) {
                     return NurBaskentFeature.P.KUVARS;
                  } else {
                     if (var1 == 1) {
                        if (var0.az == var2 - 1 && var0.ax <= 2) {
                           return var0.ax == 2 ? NurBaskentFeature.P.BEYAZ_YUN : NurBaskentFeature.P.KIRMIZI_YUN;
                        }

                        if (var0.ax == var2 - 1 && var0.az <= 2) {
                           return NurBaskentFeature.P.KITAP;
                        }

                        if (var0.ax == var2 - 1 && var0.az == 3) {
                           return NurBaskentFeature.P.SANDIK;
                        }

                        if (var0.ax == 0 && var0.az == 0) {
                           return NurBaskentFeature.P.CALISMA_MASASI;
                        }

                        if (var0.ax <= 2 && var0.az <= 2) {
                           return NurBaskentFeature.P.KIRMIZI_HALI;
                        }
                     }

                     if (var1 == 2 && var0.ax == var2 - 1 && var0.az <= 2) {
                        return NurBaskentFeature.P.KITAP;
                     } else {
                        return var1 == var3 - 2 && var0.ax == 0 && var0.az == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.HAVA;
                     }
                  }
               } else if (var1 <= 3 && kapiYuzu(var0, var2)) {
                  return NurBaskentFeature.P.HAVA;
               } else if (var0.ax == var2 && var0.az == var2) {
                  return NurBaskentFeature.P.SUTUN;
               } else if (var1 >= 2 && var1 <= var3 - 3 && (var0.ax + var0.az) % 3 == 0) {
                  return NurBaskentFeature.P.CAM_ALTIN;
               } else {
                  return var1 == var3 - 1 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.TUGLA;
               }
            } else {
               boolean var6 = var0.ax == var2 + 1 && var0.az == var2 + 1;
               boolean var7 = var0.ax == var2 + 1 && var0.az % 3 == 0 || var0.az == var2 + 1 && var0.ax % 3 == 0;
               if ((var6 || var7) && var1 <= var3 - 2) {
                  return var1 == var3 - 2 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.SUTUN;
               } else {
                  return var1 == var3 - 1 ? NurBaskentFeature.P.OYMA : null;
               }
            }
         } else {
            int var4 = var1 - var3;
            int var5 = var2 + 1 - var4;
            if (var5 < 0) {
               return null;
            } else if (var0.ax <= var5 && var0.az <= var5) {
               if (var5 == 0) {
                  return NurBaskentFeature.P.FENER;
               } else if (var0.ax != var5 && var0.az != var5) {
                  return var0.ax < var5 - 1 && var0.az < var5 - 1 ? NurBaskentFeature.P.HAVA : NurBaskentFeature.P.KUVARS;
               } else {
                  return var4 % 2 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
               }
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static int heykelNo(int var0, int var1) {
      for(int var2 = 0; var2 < HEYKEL.length; ++var2) {
         double var3 = (double)HEYKEL[var2][4] / (double)100.0F;
         if (heykelMesafe(var2, var0, var1) <= (double)26.0F * var3) {
            return var2;
         }
      }

      return -1;
   }

   private static double heykelMesafe(int var0, int var1, int var2) {
      double var3 = (double)(var1 - HEYKEL[var0][0]);
      double var5 = (double)(var2 - HEYKEL[var0][1]);
      return Math.sqrt(var3 * var3 + var5 * var5);
   }

   private static BlockState heykel(int var0, int var1, int var2, int var3) {
      int[] var4 = HEYKEL[var0];
      double var5 = (double)var4[4] / (double)100.0F;
      double var7 = (double)(var1 - var4[0]);
      double var9 = (double)(var2 - var4[1]);
      double var11 = (var7 * (double)(-var4[3]) + var9 * (double)var4[2]) / var5;
      double var13 = (var7 * (double)var4[2] + var9 * (double)var4[3]) / var5;
      return heykelBlok(var11, -var13, (double)var3 / var5);
   }

   private static double segMesafe(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16) {
      double var18 = var12 - var6;
      double var20 = var14 - var8;
      double var22 = var16 - var10;
      double var24 = var0 - var6;
      double var26 = var2 - var8;
      double var28 = var4 - var10;
      double var30 = (var18 * var24 + var20 * var26 + var22 * var28) / (var18 * var18 + var20 * var20 + var22 * var22);
      var30 = Math.max((double)0.0F, Math.min((double)1.0F, var30));
      double var32 = var6 + var18 * var30 - var0;
      double var34 = var8 + var20 * var30 - var2;
      double var36 = var10 + var22 * var30 - var4;
      return Math.sqrt(var32 * var32 + var34 * var34 + var36 * var36);
   }

   private static BlockState heykelBlok(double var0, double var2, double var4) {
      double var6 = Math.abs(var0);
      double var8 = Math.abs(var2);
      if (var4 < (double)8.0F) {
         if (var6 >= 6.8 && var6 <= 8.2 && var8 >= 6.8 && var8 <= 8.2) {
            if (var4 >= (double)7.0F) {
               return NurBaskentFeature.P.FENER;
            } else {
               return !(var4 >= (double)6.0F) && !(var4 < (double)1.0F) ? NurBaskentFeature.P.SUTUN : NurBaskentFeature.P.ALTIN;
            }
         } else {
            double var38 = var4 < (double)2.0F ? (double)9.0F : (var4 < (double)5.0F ? 7.8 : 6.8);
            if (var6 <= var38 && var8 <= var38 && var6 + var8 <= var38 * 1.35) {
               if (var4 < (double)1.0F || var4 >= (double)4.0F && var4 < (double)5.0F || var4 >= (double)7.0F && var4 < (double)8.0F) {
                  return !(var6 >= var38 - 1.1) && !(var8 >= var38 - 1.1) && !(var6 + var8 >= var38 * 1.35 - 1.2) ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN;
               } else if (!(var6 >= var38 - 0.9) && !(var8 >= var38 - 0.9)) {
                  return NurBaskentFeature.P.KTUGLA;
               } else {
                  return !(var6 <= 1.2) && !(var8 <= 1.2) ? NurBaskentFeature.P.OYMA : NurBaskentFeature.P.FENER;
               }
            } else {
               return null;
            }
         }
      } else {
         double var10 = Math.abs(var2 + (double)6.5F);
         if (var10 <= 1.2) {
            double var12 = Math.sqrt(var0 * var0 + (var2 + (double)6.5F) * (var2 + (double)6.5F) + (var4 - 57.2) * (var4 - 57.2));
            if (var12 <= 2.1) {
               if (var12 <= 0.9) {
                  return NurBaskentFeature.P.FENER;
               }

               return var4 >= (double)58.0F ? NurBaskentFeature.P.AMETIST : NurBaskentFeature.P.ALTIN;
            }

            if (var4 >= (double)39.5F && var4 < (double)55.5F && var6 <= (double)1.0F && var10 <= 0.8) {
               return (int)Math.floor(var4) % 2 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
            }

            double var14 = 37.6 + 0.055 * var6 * var6;
            if (var6 <= 6.8 && Math.abs(var4 - var14) <= 1.15 && var10 <= (double)1.0F) {
               if (var6 <= 1.2 && Math.abs(var4 - 37.8) <= 0.9) {
                  return NurBaskentFeature.P.AMETIST;
               }

               if (var6 >= (double)5.5F) {
                  return NurBaskentFeature.P.FENER;
               }

               return NurBaskentFeature.P.ALTIN;
            }

            if (var4 >= (double)7.5F && var4 < (double)36.5F) {
               double var16 = 1.85 - ((double)36.5F - var4) * 0.028;
               if (var6 <= var16 && var6 * 0.6 + var10 <= (double)1.25F) {
                  if (var6 <= 0.42) {
                     return NurBaskentFeature.P.FENER;
                  }

                  if (var6 <= 0.95) {
                     return var4 % (double)4.0F < (double)2.0F ? NurBaskentFeature.P.CAM_GOK : NurBaskentFeature.P.KUVARS;
                  }

                  return var4 < (double)11.0F ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KUVARS;
               }
            }
         }

         for(int var39 = -1; var39 <= 1; var39 += 2) {
            double var13 = segMesafe(var0, var4, var2, (double)var39 * 7.4, (double)45.0F, -0.4, (double)var39 * 5.6, 38.2, -3.4);
            if (var13 <= 1.85) {
               if (var4 >= (double)42.5F) {
                  return NurBaskentFeature.P.ALTIN;
               }

               return (int)var4 % 3 == 0 ? NurBaskentFeature.P.OYMA : NurBaskentFeature.P.KUVARS;
            }

            double var15 = segMesafe(var0, var4, var2, (double)var39 * 5.6, 38.2, -3.4, (double)var39 * 1.3, (double)41.5F, -6.4);
            if (var15 <= 1.65) {
               return !(var2 < -4.6) && (int)var4 % 2 != 0 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN;
            }
         }

         if (var4 >= (double)46.5F && var4 < (double)49.5F && var6 <= 2.1 && Math.abs(var2 + 0.2) <= 2.1) {
            return var4 < 47.8 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KUVARS;
         } else {
            double var40 = var4 - 52.8;
            double var41 = var2 + 0.4;
            double var18 = Math.sqrt(var0 * var0 / (double)1.0F + var40 * var40 / (double)1.25F + var41 * var41 / 1.1);
            if (var18 <= 4.3) {
               if (var4 >= 55.2) {
                  if (var6 <= 0.9 && var2 < (double)-1.5F && var4 <= 58.8) {
                     return NurBaskentFeature.P.ZUMRUT;
                  } else {
                     return var2 < (double)0.0F && (int)Math.round(var6 * (double)2.0F) % 2 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
                  }
               } else if (var4 >= 52.4 && var4 <= 53.7 && var6 >= (double)1.0F && var6 <= 2.2 && var2 <= (double)-3.0F) {
                  return NurBaskentFeature.P.FENER;
               } else if (var6 <= 0.65 && var4 >= (double)51.0F && var4 <= 54.2 && var2 <= -3.4) {
                  return NurBaskentFeature.P.KUVARS;
               } else if (var6 >= (double)3.5F && var4 >= (double)52.0F && var4 <= (double)56.5F) {
                  return NurBaskentFeature.P.ALTIN;
               } else if (var2 >= 1.4 || var6 >= (double)3.0F && var2 >= -0.3 && var4 <= (double)54.5F) {
                  return (int)Math.floor(var4 + var6) % 2 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
               } else {
                  return NurBaskentFeature.P.KUVARS;
               }
            } else if (var4 >= (double)53.0F && var4 <= (double)58.5F && var6 >= 3.8 && var6 <= 5.2 && Math.abs(var2 + 0.2 + (var4 - (double)53.0F) * 0.35) <= 1.1) {
               return var4 >= (double)57.0F ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
            } else {
               if (var2 >= (double)2.0F && var2 <= 3.3 && var4 >= (double)46.5F && var4 <= (double)64.5F) {
                  double var20 = var4 - (double)54.5F;
                  double var22 = Math.sqrt(var0 * var0 + var20 * var20);
                  double var24 = Math.atan2(var20, var0);
                  if (var22 >= 5.4 && var22 <= 7.2) {
                     if (var22 >= 6.1 && var22 <= 6.6) {
                        return NurBaskentFeature.P.FENER;
                     }

                     return NurBaskentFeature.P.ALTIN;
                  }

                  double var26 = Math.abs(Math.sin(var24 * (double)6.0F));
                  if (var22 > 7.2 && var22 <= 7.2 + var26 * 2.4 && var26 > (double)0.75F) {
                     return var22 > 8.6 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
                  }
               }

               double var42 = var6 - 2.2;
               if (var42 >= (double)0.0F && var42 <= (double)22.5F) {
                  double var43 = 3.2 + 0.3 * var42 - 0.0095 * var42 * var42;
                  double var48 = var42 < (double)6.0F ? 1.55 : (double)1.25F;
                  if (Math.abs(var2 - var43) <= var48) {
                     double var51 = (double)45.5F + 1.48 * var42 - 0.043 * var42 * var42;
                     double var28 = var42 % 3.1 / 3.1;
                     double var30 = Math.sin(var28 * Math.PI) * ((double)5.5F - var42 * 0.12);
                     double var32 = (double)24.0F + 0.82 * var42;
                     double var34 = var32 - Math.max((double)0.0F, var30);
                     if (var4 >= var34 && var4 <= var51) {
                        if (var4 >= var51 - (double)2.0F) {
                           return var4 >= var51 - 0.9 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
                        }

                        double var36 = var32 + (var51 - var32) * 0.48 - Math.sin(var28 * Math.PI) * 1.8;
                        if (var4 >= var36 && Math.abs(var2 - var43) <= 0.9) {
                           if (Math.abs(var4 - var36) <= 0.9) {
                              return NurBaskentFeature.P.ALTIN;
                           }

                           return (int)Math.floor(var42 + var4 * 0.4) % 3 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.OYMA;
                        }

                        if (var28 >= 0.42 && var28 <= 0.58) {
                           return NurBaskentFeature.P.ALTIN;
                        }

                        if (var4 <= var34 + 1.8) {
                           return NurBaskentFeature.P.FENER;
                        }

                        return (int)Math.floor(var42 * 0.8 + var4 * (double)0.25F) % 4 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.KUVARS;
                     }
                  }
               }

               if (var4 >= (double)32.0F && var4 < (double)47.0F) {
                  if (var4 >= (double)42.5F && var6 >= (double)5.0F && var6 <= (double)9.0F && Math.abs(var2) <= 3.8) {
                     double var44 = Math.sqrt((var6 - 6.8) * (var6 - 6.8) + (var4 - 44.2) * (var4 - 44.2) + var2 * 0.7 * var2 * 0.7);
                     if (var44 <= 2.8) {
                        return !(var4 >= (double)45.5F) && !(var6 >= 7.8) ? NurBaskentFeature.P.OYMA : NurBaskentFeature.P.ALTIN;
                     }
                  }

                  double var45 = 4.6 + (var4 - (double)32.0F) * 0.16;
                  double var49 = 3.6 + (var4 - (double)32.0F) * 0.04;
                  double var52 = var0 * var0 / (var45 * var45) + var2 * var2 / (var49 * var49);
                  if (var52 <= (double)1.0F) {
                     if (var2 < -2.2) {
                        if (var6 <= 1.3 && var4 >= (double)39.0F && var4 <= (double)42.5F) {
                           return NurBaskentFeature.P.FENER;
                        }

                        if (var6 <= 2.4 && var4 >= (double)38.0F && var4 <= (double)43.5F) {
                           return NurBaskentFeature.P.ALTIN;
                        }

                        if (Math.abs(var4 - ((double)36.0F + var6 * 0.9)) <= 1.1) {
                           return NurBaskentFeature.P.ALTIN;
                        }

                        return NurBaskentFeature.P.OYMA;
                     }

                     if (var2 > 2.4) {
                        return var6 >= var45 - (double)1.0F ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KTUGLA;
                     }

                     return NurBaskentFeature.P.KUVARS;
                  }
               }

               if (var4 >= (double)8.0F && var4 < (double)32.0F) {
                  if (var4 >= (double)30.0F && var6 <= (double)5.0F && var8 <= 3.8 && var0 * var0 / (double)25.0F + var2 * var2 / 14.4 <= 1.05) {
                     if (var6 <= 1.2 && var2 < (double)-2.5F) {
                        return NurBaskentFeature.P.AMETIST;
                     }

                     return NurBaskentFeature.P.ALTIN;
                  }

                  if (var4 < (double)12.5F) {
                     for(int var46 = -1; var46 <= 1; var46 += 2) {
                        double var23 = (double)var46 * 2.4;
                        double var25 = var46 == -1 ? -1.8 : -0.8;
                        if (Math.abs(var0 - var23) <= (double)1.5F && var2 >= var25 - 3.2 && var2 <= var25 + 2.2) {
                           if (!(var4 >= (double)11.0F) && !(var4 < (double)9.0F)) {
                              return NurBaskentFeature.P.OYMA;
                           }

                           return NurBaskentFeature.P.ALTIN;
                        }
                     }
                  }

                  double var47 = 6.4 - (var4 - (double)8.0F) * 0.075;
                  double var50 = 4.6 - (var4 - (double)8.0F) * 0.045;
                  double var53 = Math.atan2(var2, var0);
                  double var54 = Math.sin(var53 * (double)10.0F) * 0.35;
                  double var55 = var0 * var0 / ((var47 + var54) * (var47 + var54)) + var2 * var2 / ((var50 + var54) * (var50 + var54));
                  if (var55 <= (double)1.0F && (var4 >= (double)11.0F || var2 >= -2.2)) {
                     if (var6 <= 1.3 && var2 < -var50 * 0.55) {
                        if (var6 <= 0.45 && (int)var4 % 4 == 0) {
                           return NurBaskentFeature.P.FENER;
                        }

                        return NurBaskentFeature.P.ALTIN;
                     }

                     if (var4 >= (double)11.0F && var4 < 12.2 && var55 >= 0.72) {
                        return NurBaskentFeature.P.ALTIN;
                     }

                     if (var54 > 0.12 && var55 >= (double)0.75F) {
                        return NurBaskentFeature.P.OYMA;
                     }

                     return (int)var4 % 8 == 7 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.TUGLA;
                  }

                  double var56 = 3.2 + ((double)32.0F - var4) * 0.06 + Math.sin(var0 * 0.8) * (double)0.25F;
                  if (var6 <= 5.4 && Math.abs(var2 - var56) <= (double)0.75F) {
                     return !(var6 >= 4.4) && !(var4 < (double)10.0F) ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN;
                  }
               }

               return null;
            }
         }
      }
   }

   private static BlockState saray(int var0, int var1, int var2) {
      int var3 = Math.abs(var0);
      int var4 = Math.abs(var1);
      int var5 = Math.max(var3, var4);
      double var6 = Math.sqrt((double)var0 * (double)var0 + (double)var1 * (double)var1);
      if (var2 >= 68) {
         if (var2 <= 156) {
            double var8 = 5.6 - (double)(var2 - 68) * 0.04;
            if (var6 <= var8) {
               if (var6 <= 1.2) {
                  return NurBaskentFeature.P.FENER;
               }

               if (var2 % 10 == 0) {
                  return NurBaskentFeature.P.ALTIN;
               }

               if (var2 % 4 != 2 || var3 > 1 && var4 > 1) {
                  return NurBaskentFeature.P.KUVARS;
               }

               return NurBaskentFeature.P.CAM_ALTIN;
            }
         }

         double var18 = Math.sqrt(var6 * var6 + (double)((var2 - 162) * (var2 - 162)));
         if (var18 <= 6.2) {
            return var18 > (double)5.0F ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.FENER;
         }

         if (var2 >= 168 && var2 <= 225 && var6 <= 1.8) {
            return var6 < 0.8 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.CAM_ALTIN;
         }
      }

      if (var2 >= 46) {
         if (var2 >= 65 && var2 <= 68 && var6 <= (double)8.5F) {
            return !(var6 >= (double)7.0F) && var2 != 67 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN;
         }

         double var19 = (double)var2 - (double)46.0F;
         double var10 = Math.sqrt(var6 * var6 + var19 * var19);
         if (var10 >= 19.6 && var10 <= 22.4) {
            double var36 = Math.atan2((double)var1, (double)var0) * (double)16.0F / Math.PI;
            double var14 = var36 - Math.floor(var36);
            boolean var16 = var14 < 0.18 || var14 > 0.82;
            boolean var17 = (var2 - 46) % 6 == 5;
            if (!var16 && !var17) {
               return (int)Math.floor(var36) % 2 == 0 ? NurBaskentFeature.P.CAM_GOK : NurBaskentFeature.P.CAM;
            }

            return NurBaskentFeature.P.ALTIN;
         }
      }

      if (var2 >= 42 && var2 < 46 && var6 >= 19.6 && var6 <= 23.2) {
         double var24 = Math.atan2((double)var1, (double)var0) * (double)16.0F / Math.PI;
         boolean var31 = Math.floorMod((int)Math.floor(var24), 2) == 0 && var2 >= 43 && var2 <= 45;
         if (var2 == 45) {
            return NurBaskentFeature.P.ALTIN;
         } else {
            return var31 ? NurBaskentFeature.P.CAM_ALTIN : NurBaskentFeature.P.OYMA;
         }
      } else {
         if (var2 >= 24 && var2 <= 45 && var6 >= (double)22.0F && var6 <= (double)33.0F) {
            boolean var20 = var3 <= 1 || var4 <= 1 || Math.abs(var3 - var4) <= 1;
            if (var20) {
               double var9 = (double)45.0F - (var6 - (double)22.0F) * 1.6;
               if (Math.abs((double)var2 - var9) <= 1.4) {
                  return (double)var2 > var9 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
               }
            }
         }

         if ((var2 == 42 || var2 == 43) && var3 <= 23 && var4 <= 29 && var6 >= 18.8) {
            return var3 < 22 && var4 < 28 && var2 != 43 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN;
         } else {
            if (var2 >= 4 && var2 < 42) {
               boolean var21 = var3 >= 21 && var3 <= 22 && var4 <= 28;
               boolean var25 = var4 >= 27 && var4 <= 28 && var3 <= 22;
               if (var21 || var25) {
                  if (var1 > 0 && var4 >= 27 && var3 <= 7) {
                     int var28 = 19 + (int)Math.sqrt((double)Math.max(0, 49 - var3 * var3));
                     if (var2 < var28) {
                        return null;
                     }

                     if (var2 == var28) {
                        return NurBaskentFeature.P.ALTIN;
                     }
                  }

                  if (var3 >= 21 && var4 <= 5) {
                     int var29 = 16 + (int)Math.sqrt((double)Math.max(0, 25 - var4 * var4));
                     if (var2 < var29) {
                        return null;
                     }

                     if (var2 == var29) {
                        return NurBaskentFeature.P.ALTIN;
                     }
                  }

                  int var30 = var25 ? var3 : var4;
                  if (var30 <= 6 && var2 >= 25 && var2 <= 37) {
                     double var11 = Math.sqrt((double)(var30 * var30 + (var2 - 31) * (var2 - 31)));
                     if (var11 <= (double)6.0F) {
                        if (!(var11 >= (double)5.0F) && !(var11 <= (double)1.5F) && var30 != 0 && var2 != 31) {
                           return (var30 + var2) % 2 == 0 ? NurBaskentFeature.P.CAM_ALTIN : NurBaskentFeature.P.CAM_GOK;
                        }

                        return NurBaskentFeature.P.ALTIN;
                     }
                  }

                  if (var3 >= 21 && var4 >= 27) {
                     return NurBaskentFeature.P.SUTUN;
                  }

                  int var32 = var25 ? var3 : var4;
                  if (var32 % 6 == 0) {
                     return NurBaskentFeature.P.SUTUN;
                  }

                  if ((var32 % 6 == 2 || var32 % 6 == 3 || var32 % 6 == 4) && var2 >= 12 && var2 < 38) {
                     if (var2 != 12 && var2 != 24 && var2 != 37) {
                        return var32 % 6 == 3 ? NurBaskentFeature.P.CAM_ALTIN : NurBaskentFeature.P.CAM_GOK;
                     }

                     return NurBaskentFeature.P.ALTIN;
                  }

                  if (var2 != 41 && var2 != 4 && var2 != 24) {
                     return NurBaskentFeature.P.KTUGLA;
                  }

                  return NurBaskentFeature.P.ALTIN;
               }

               if (var3 <= 20 && var4 <= 26) {
                  if (var3 == 12 && var4 <= 20 && var1 % 8 == 0) {
                     if (var2 >= 39) {
                        if (var2 == 39) {
                           return NurBaskentFeature.P.FENER;
                        }

                        return null;
                     }

                     return var2 != 4 && var2 != 38 ? NurBaskentFeature.P.SUTUN : NurBaskentFeature.P.ALTIN;
                  }

                  if (var3 == 12 && var4 <= 20 && var2 >= 36 && var2 <= 38) {
                     return var2 == 37 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
                  }

                  if (var3 <= 1 && (var1 == -10 || var1 == 0 || var1 == 10) && var2 >= 32 && var2 <= 41) {
                     if (var3 == 0 && (var1 == -10 || var1 == 0 || var1 == 10)) {
                        return var2 <= 34 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
                     }

                     if (var2 == 34) {
                        return NurBaskentFeature.P.FENER;
                     }
                  }
               }
            }

            if (var4 >= 29 && var4 <= 32 && var3 <= 22) {
               if (var4 == 31 && var3 % 5 == 3 && var3 >= 8 && var2 < 40) {
                  return var2 != 4 && var2 != 39 ? NurBaskentFeature.P.SUTUN : NurBaskentFeature.P.ALTIN;
               }

               if (var2 == 40 || var2 == 41) {
                  return var2 == 41 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
               }

               int var22 = (int)((double)11.0F - (double)var3 * (double)0.5F);
               if (var2 >= 42 && var2 < 42 + var22) {
                  if (var2 == 42 + var22 - 1) {
                     return NurBaskentFeature.P.ALTIN;
                  }

                  if (var3 <= 3 && var2 >= 44 && var2 <= 48) {
                     return NurBaskentFeature.P.FENER;
                  }

                  return NurBaskentFeature.P.OYMA;
               }
            }

            int var23 = var3 - 27;
            int var26 = var4 - 35;
            double var27 = Math.sqrt((double)var23 * (double)var23 + (double)var26 * (double)var26);
            if (var27 <= 6.6) {
               if (var2 < 4) {
                  return NurBaskentFeature.P.KUVARS;
               }

               if (var2 <= 98) {
                  if ((var2 == 42 || var2 == 70 || var2 == 96) && var27 <= 6.4) {
                     return var27 > 5.2 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
                  }

                  double var12 = 4.8 - (double)var2 * 0.012;
                  if (var27 <= var12) {
                     if (var27 < var12 - 1.4) {
                        return var2 % 14 == 0 ? NurBaskentFeature.P.KUVARS : null;
                     }

                     if (var2 % 14 < 6 || var2 % 14 > 10 || var23 != 0 && var26 != 0) {
                        if (var2 % 14 == 13) {
                           return NurBaskentFeature.P.ALTIN;
                        }

                        return var23 != 0 && var26 != 0 ? NurBaskentFeature.P.KTUGLA : NurBaskentFeature.P.SUTUN;
                     }

                     return NurBaskentFeature.P.CAM_ALTIN;
                  }
               } else if (var2 <= 124) {
                  double var33 = 4.2 - (double)(var2 - 98) * 0.16;
                  if (var33 > (double)0.0F && var27 <= var33) {
                     if (var33 < 1.2) {
                        return NurBaskentFeature.P.FENER;
                     }

                     return var2 % 3 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KUVARS;
                  }
               }
            }

            if (var5 <= 30) {
               int var35 = 4;
               if (var3 <= 20 && var1 <= -14 && var1 >= -26) {
                  var35 = 4 + Math.min(4, (-var1 - 14) / 2 + 1);
               }

               if (var2 < var35) {
                  if (var3 <= 4 && var1 >= 18) {
                     return var3 == 4 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KUVARS;
                  } else if (var3 > 2 && var5 < 29) {
                     return var2 == var35 - 1 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.TUGLA;
                  } else {
                     return var2 == var35 - 1 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
                  }
               } else if (var3 <= 2 && var1 >= -23 && var1 <= -20 && var2 == var35) {
                  return var3 == 0 && var1 == -21 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
               } else if (var3 <= 2 && var1 == -24 && var2 >= var35 && var2 <= var35 + 8) {
                  if (var3 == 0 && var2 >= var35 + 2 && var2 <= var35 + 6) {
                     return NurBaskentFeature.P.FENER;
                  } else {
                     return NurBaskentFeature.P.ALTIN;
                  }
               } else if (var3 == 3 && var1 >= -23 && var1 <= -20 && var2 <= var35 + 2) {
                  return var2 == var35 + 2 && var1 == -20 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
               } else {
                  if (var1 == -25) {
                     double var13 = (double)var2 - (double)20.0F;
                     double var15 = Math.sqrt((double)(var0 * var0) + var13 * var13);
                     if (var15 >= (double)6.0F && var15 <= (double)11.5F) {
                        if (var15 >= 8.2 && var15 <= 9.6) {
                           return NurBaskentFeature.P.FENER;
                        }

                        return NurBaskentFeature.P.ALTIN;
                     }
                  }

                  return null;
               }
            } else {
               int var34 = var5 <= 32 ? 3 : (var5 <= 34 ? 2 : (var5 <= 36 ? 1 : 0));
               if (var2 >= var34) {
                  return null;
               } else {
                  return var2 != var34 - 1 || var3 > 6 && var4 > 6 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN;
               }
            }
         }
      }
   }

   private static void adalar(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3) {
      for(int var4 = 0; var4 < 18; ++var4) {
         int var5 = var2 - ACX[var4];
         int var6 = var3 - ACZ[var4];
         int var7 = AR[var4];
         if (var5 <= var7 && var5 >= -var7 && var6 <= var7 && var6 >= -var7) {
            double var8 = Math.sqrt((double)var5 * (double)var5 + (double)var6 * (double)var6);
            if (!(var8 > (double)var7)) {
               double var10 = var8 / (double)var7;
               int var12 = 101 + ATY[var4];
               double var13 = (double)(hash(var2 * 7, var3 * 13) % 5) * ((double)1.0F - var10);
               int var15 = (int)Math.round((double)AD[var4] * Math.pow((double)1.0F - var10 * var10, 1.35) + var13);

               for(int var16 = var12 - 3 - var15; var16 <= var12; ++var16) {
                  BlockState var17;
                  if (var16 == var12) {
                     var17 = var8 >= (double)var7 - (double)1.5F ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.CIM;
                  } else if (var16 >= var12 - 2) {
                     var17 = NurBaskentFeature.P.KALSIT;
                  } else if (var16 <= var12 - var15) {
                     var17 = NurBaskentFeature.P.ALTIN;
                  } else {
                     var17 = NurBaskentFeature.P.BULUT;
                  }

                  set(var0, var1, var2, var16, var3, var17);
               }

               if ((var5 != var7 - 1 || var6 != 0) && (var5 != 0 || var6 != var7 - 1)) {
                  boolean var19 = false;

                  for(int var20 = 0; var20 < 20; ++var20) {
                     BlockState var18 = var4 % 2 == 0 ? adaMabet(var5, var6, var20) : adaAgac(var5, var6, var20);
                     if (var18 != null) {
                        if (var20 == 0) {
                           var19 = true;
                        }

                        set(var0, var1, var2, var12 + 1 + var20, var3, var18);
                     }
                  }

                  if (!var19 && var8 < (double)(var7 - 2) && hash(var2 * 13 + var4, var3 * 7) % 7 == 0) {
                     set(var0, var1, var2, var12 + 1, var3, NurBaskentFeature.P.CICEK[hash(var2, var3 + var4) % NurBaskentFeature.P.CICEK.length]);
                  }
               } else {
                  set(var0, var1, var2, var12, var3, NurBaskentFeature.P.SU);
               }
            }
         }
      }

   }

   private static BlockState adaAgac(int var0, int var1, int var2) {
      BlockState var3 = agac(var0 + 10, var1 + 10, var2, 12, 10, 7);
      return var3 != null ? var3 : agac(var0 + 10, var1 + 10, var2, 6, 14, 6);
   }

   private static BlockState adaMabet(int var0, int var1, int var2) {
      double var3 = Math.sqrt((double)var0 * (double)var0 + (double)var1 * (double)var1);

      for(int var5 = 0; var5 < 8; ++var5) {
         if (var0 == SX[var5] && var1 == SZ[var5] && var2 < 10) {
            return var2 != 0 && var2 != 9 ? NurBaskentFeature.P.SUTUN : NurBaskentFeature.P.ALTIN;
         }
      }

      if (var2 == 0 && var3 <= (double)6.5F) {
         return var3 >= (double)5.5F ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KUVARS;
      } else if (var2 == 1 && var0 == 0 && var1 == 0) {
         return NurBaskentFeature.P.AMETIST;
      } else if (var2 == 2 && var0 == 0 && var1 == 0) {
         return NurBaskentFeature.P.FENER;
      } else if (var2 == 10 && var3 >= 5.2 && var3 <= (double)8.5F) {
         return NurBaskentFeature.P.ALTIN;
      } else if (var2 == 11 && var3 <= 7.8) {
         return NurBaskentFeature.P.OYMA;
      } else {
         if (var2 >= 12 && var2 <= 16) {
            double var7 = (double)7.5F - (double)(var2 - 11) * 1.4;
            if (var3 <= var7) {
               return var3 >= var7 - (double)1.5F ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.CAM_GOK;
            }
         }

         if (var2 >= 17 && var2 <= 19 && var3 <= 1.2) {
            return NurBaskentFeature.P.FENER;
         } else {
            return null;
         }
      }
   }

   private static void tepe(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3, double var4) {
      double var6 = smooth((double)430.0F, (double)600.0F, var4);
      if (!(var6 <= (double)0.0F)) {
         double var8 = (double)1.0F - Math.abs((double)2.0F * vn((double)var2 / (double)60.0F + (double)5.0F, (double)var3 / (double)60.0F + (double)9.0F) - (double)1.0F);
         double var10 = 0.6 * vn((double)var2 / (double)95.0F + (double)13.0F, (double)var3 / (double)95.0F + (double)7.0F) + 0.4 * var8;
         double var12 = 0.65 * vn((double)var2 / (double)28.0F, (double)var3 / (double)28.0F) + 0.35 * vn((double)var2 / (double)11.0F + (double)50.0F, (double)var3 / (double)11.0F + (double)50.0F);
         int var14 = (int)(var6 * (var10 * var10 * (double)88.0F + var12 * (double)8.0F));

         for(int var15 = 101; var15 <= 100 + var14; ++var15) {
            BlockState var16 = NurBaskentFeature.P.BULUT;
            if (var15 == 100 + var14 && var14 > 42) {
               var16 = (var2 + var3) % 7 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KUVARS;
            }

            set(var0, var1, var2, var15, var3, var16);
         }

      }
   }

   private static boolean yildizIsigiBoyutuPlace(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3) {
      for(int var4 = 0; var4 < KART_SUNAKLARI.length; ++var4) {
         int var5 = KART_SUNAKLARI[var4][0];
         int var6 = KART_SUNAKLARI[var4][1];
         if (var2 + 15 >= var5 - 6 && var2 <= var5 + 6 && var3 + 15 >= var6 - 6 && var3 <= var6 + 6) {
            yildizSunagiOlustur(var0, var1, var2, var3, var5, var6);
         }
      }

      if (var2 <= -1660 && var2 + 15 >= -2340 && var3 <= 340 && var3 + 15 >= -340) {
         for(int var14 = 0; var14 < 16; ++var14) {
            for(int var17 = 0; var17 < 16; ++var17) {
               int var20 = var2 + var14;
               int var21 = var3 + var17;
               int var8 = var20 - -2000;
               int var9 = var21 - 0;
               double var10 = Math.sqrt((double)var8 * (double)var8 + (double)var9 * (double)var9);
               if (var10 <= (double)322.0F) {
                  batiSehriKolonu(var0, var1, var20, var21, var8, var9, var10);
               }
            }
         }

         return true;
      } else if (var2 + 15 >= 1500 && var2 <= 2750 && var3 <= 620 && var3 + 15 >= -620) {
         for(int var13 = 0; var13 < 16; ++var13) {
            for(int var16 = 0; var16 < 16; ++var16) {
               int var19 = var2 + var13;
               int var7 = var3 + var16;
               if (var19 >= 1500 && Math.abs(var7) <= 600) {
                  doguDaglariKolonu(var0, var1, var19, var7);
               }
            }
         }

         return true;
      } else {
         boolean var12 = var0.getBlockState(var1.set(var2, 68, var3)).is(Blocks.MUD);
         if (!var12) {
            if (var2 + 15 >= -10 && var2 <= 10 && var3 + 15 >= 1490 && var3 <= 1510) {
               guneyBoyutKapisiOlustur(var0, var1, var2, var3);
               return true;
            } else {
               return false;
            }
         } else {
            for(int var15 = 0; var15 < 16; ++var15) {
               for(int var18 = 0; var18 < 16; ++var18) {
                  zifiriBataklikKolonu(var0, var1, var2 + var15, var3 + var18, true);
               }
            }

            return true;
         }
      }
   }

   private static void yildizSunagiOlustur(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3, int var4, int var5) {
      int var6 = 72;

      for(int var7 = 140; var7 >= 50; --var7) {
         BlockState var8 = var0.getBlockState(var1.set(var4, var7, var5));
         if (!var8.isAir() && var8 != NurBaskentFeature.P.SU && var8 != NurBaskentFeature.P.KARANLIK_YAPRAK && var8 != NurBaskentFeature.P.KARANLIK_GOVDE && var8 != NurBaskentFeature.P.YAPRAK && var8 != NurBaskentFeature.P.MESE_YAPRAK) {
            var6 = var7;
            break;
         }
      }

      for(int var16 = 0; var16 < 16; ++var16) {
         for(int var17 = 0; var17 < 16; ++var17) {
            int var9 = var2 + var16;
            int var10 = var3 + var17;
            int var11 = var9 - var4;
            int var12 = var10 - var5;
            int var13 = Math.abs(var11);
            int var14 = Math.abs(var12);
            if (var13 <= 7 && var14 <= 7) {
               for(int var15 = var6 + 1; var15 <= var6 + 28; ++var15) {
                  var0.setBlock(var1.set(var9, var15, var10), NurBaskentFeature.P.HAVA, 2);
               }

               if (var13 <= 4 && var14 <= 4) {
                  for(int var18 = var6 - 4; var18 <= var6; ++var18) {
                     set(var0, var1, var9, var18, var10, var13 != 4 && var14 != 4 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN);
                  }

                  if (var13 == 3 && var14 == 3) {
                     for(int var19 = 1; var19 <= 5; ++var19) {
                        set(var0, var1, var9, var6 + var19, var10, var19 == 5 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.SUTUN);
                     }
                  }

                  if (var13 <= 1 && var14 <= 1) {
                     set(var0, var1, var9, var6 + 1, var10, NurBaskentFeature.P.ALTIN);
                     if (var13 == 0 && var14 == 0) {
                        set(var0, var1, var9, var6 + 2, var10, NurBaskentFeature.P.AMETIST);
                        set(var0, var1, var9, var6 + 3, var10, NurBaskentFeature.P.SANDIK);
                        set(var0, var1, var9, var6 + 4, var10, NurBaskentFeature.P.FENER);
                     }
                  }
               }
            }
         }
      }

   }

   private static void batiSehriKolonu(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3, int var4, int var5, double var6) {
      int var8 = Math.abs(var4);
      int var9 = Math.abs(var5);
      byte var10 = 100;

      for(int var11 = 64; var11 < var10; ++var11) {
         set(var0, var1, var2, var11, var3, var11 >= var10 - 3 ? NurBaskentFeature.P.KTUGLA : NurBaskentFeature.P.KARATAS_TUGLA);
      }

      for(int var37 = var10 + 1; var37 <= var10 + 115; ++var37) {
         var0.setBlock(var1.set(var2, var37, var3), NurBaskentFeature.P.HAVA, 2);
      }

      if (var6 >= (double)304.0F && var6 <= (double)316.0F) {
         set(var0, var1, var2, var10, var3, NurBaskentFeature.P.KARATAS_TUGLA);
         if (var4 > 0 && var9 <= 7) {
            for(int var42 = 1; var42 <= 28; ++var42) {
               if (var9 <= 4 && var42 <= 14) {
                  int var44 = 10 + (int)Math.sqrt((double)Math.max(0, 16 - var9 * var9));
                  if (var42 < var44) {
                     if (var6 >= (double)309.0F && var6 <= (double)311.0F) {
                        set(var0, var1, var2, var10 + var42, var3, (var42 + var9 & 1) == 0 ? NurBaskentFeature.P.CAM_GOK : NurBaskentFeature.P.CAM);
                     }
                     continue;
                  }

                  if (var42 == var44) {
                     set(var0, var1, var2, var10 + var42, var3, NurBaskentFeature.P.ALTIN);
                     continue;
                  }
               }

               if (var42 != 28 && var42 != 18) {
                  if (var9 == 7) {
                     set(var0, var1, var2, var10 + var42, var3, NurBaskentFeature.P.SUTUN);
                  } else {
                     set(var0, var1, var2, var10 + var42, var3, NurBaskentFeature.P.KUVARS);
                  }
               } else {
                  set(var0, var1, var2, var10 + var42, var3, NurBaskentFeature.P.ALTIN);
               }
            }

         } else {
            double var41 = Math.atan2((double)var5, (double)var4);
            int var45 = (int)Math.round(var41 / (Math.PI / 8D));
            double var46 = (double)var45 * (Math.PI / 8D);
            double var47 = Math.atan2(Math.sin(var41 - var46), Math.cos(var41 - var46));
            double var48 = Math.abs(var47) * (double)310.0F;
            double var49 = var6 - (double)310.0F;
            if (var45 != 0 && var48 <= 6.2 && Math.abs(var49) <= 5.4) {
               for(int var51 = 1; var51 <= 40; ++var51) {
                  BlockState var54 = batiSurKulesi(var49, var48, var47, var51, var2, var3);
                  if (var54 != null) {
                     set(var0, var1, var2, var10 + var51, var3, var54);
                  } else {
                     var0.setBlock(var1.set(var2, var10 + var51, var3), NurBaskentFeature.P.HAVA, 2);
                  }
               }

            } else {
               for(int var50 = 1; var50 <= 24; ++var50) {
                  if (var50 == 24) {
                     if ((var6 >= (double)314.0F || var6 <= (double)306.0F) && (var2 + var3 & 1) == 0) {
                        set(var0, var1, var2, var10 + var50, var3, NurBaskentFeature.P.ALTIN);
                     }
                  } else if (var50 != 22 && var50 != 23) {
                     if (var6 >= (double)306.0F && var6 <= (double)314.0F) {
                        if (var50 % 7 == 0) {
                           set(var0, var1, var2, var10 + var50, var3, NurBaskentFeature.P.OYMA);
                        } else {
                           set(var0, var1, var2, var10 + var50, var3, (var50 + var8 + var9) % 5 == 0 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.KTUGLA);
                        }
                     }
                  } else if (var6 >= (double)305.0F && var6 <= (double)315.0F) {
                     set(var0, var1, var2, var10 + var50, var3, NurBaskentFeature.P.ALTIN);
                  }
               }

            }
         }
      } else if (!(var6 > (double)316.0F)) {
         if (var8 <= 36 && var9 <= 36) {
            set(var0, var1, var2, var10, var3, (var2 + var3 & 1) == 0 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.ALTIN);

            for(int var40 = 0; var40 < 86; ++var40) {
               BlockState var43 = batiMerkezYildizSarayi(var4, var5, var8, var9, var40, var6);
               if (var43 != null) {
                  set(var0, var1, var2, var10 + 1 + var40, var3, var43);
               }
            }

         } else if (var8 >= 12 && var9 >= 12 && (var8 > 39 || var9 > 39) && !(var6 > (double)301.5F)) {
            byte var39 = 26;
            int var12 = var8 - 12;
            int var13 = var9 - 12;
            int var14 = var12 / var39;
            int var15 = var13 / var39;
            int var16 = var12 % var39;
            int var17 = var13 % var39;
            int var18 = var4 >= 0 ? 1 : -1;
            int var19 = var5 >= 0 ? 1 : -1;
            Hc var20 = new Hc();
            var20.lx = var16;
            var20.lz = var17;
            var20.ccx = (double)var18 * ((double)12.0F + (double)(var14 * var39) + (double)13.0F);
            var20.ccz = (double)var19 * ((double)12.0F + (double)(var15 * var39) + (double)13.0F);
            var20.h = hash(var14 * 23 + var18 * 700, var15 * 41 + var19 * 700);
            var20.ax = Math.abs(var16 - 13);
            var20.az = Math.abs(var17 - 13);
            int var21 = var16 - 13;
            int var22 = var17 - 13;
            if (var16 >= 2 && var16 < 24 && var17 >= 2 && var17 < 24) {
               if (var16 != 2 && var16 != 23 || var17 != 2 && var17 != 23) {
                  double var53 = Math.abs(var20.ccx);
                  double var25 = Math.abs(var20.ccz);
                  double var27 = Math.sqrt((var53 + (double)13.0F) * (var53 + (double)13.0F) + (var25 + (double)13.0F) * (var25 + (double)13.0F));
                  if (var27 > (double)301.0F) {
                     if (var20.ax != 0 && var20.az != 0) {
                        if ((var8 + var9) % 6 == 0) {
                           set(var0, var1, var2, var10, var3, NurBaskentFeature.P.DFENER);
                        } else {
                           set(var0, var1, var2, var10, var3, (var2 + var3 & 1) == 0 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.KARATAS_TUGLA);
                        }
                     } else {
                        set(var0, var1, var2, var10, var3, NurBaskentFeature.P.ALTIN);
                     }

                     if (var20.ax == 0 && var20.az == 0) {
                        for(int var55 = 0; var55 <= 4; ++var55) {
                           set(var0, var1, var2, var10 + 1 + var55, var3, var55 == 4 ? NurBaskentFeature.P.FENER : (var55 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.SUTUN));
                        }
                     }

                  } else {
                     double var29 = Math.sqrt((var53 + (double)10.0F) * (var53 + (double)10.0F) + (var25 + (double)10.0F) * (var25 + (double)10.0F));
                     boolean var31 = var53 - (double)10.0F <= (double)39.0F && var25 - (double)10.0F <= (double)39.0F;
                     boolean var32 = !var31 && var29 <= (double)292.0F;
                     var20.bina = var32 && var20.h % 100 < 88;
                     var20.half = 6 + (var20.h & 1);
                     var20.H = 10 + (var20.h >> 6) % 4;
                     if (var20.ax < 9 && var20.az < 9) {
                        set(var0, var1, var2, var10, var3, NurBaskentFeature.P.KUVARS);
                     } else {
                        set(var0, var1, var2, var10, var3, var20.ax != 10 && var20.az != 10 ? NurBaskentFeature.P.YOSUN : NurBaskentFeature.P.KALSIT);
                     }

                     if (var20.bina) {
                        int var33 = Math.floorMod(var20.h >> 3, 6);
                        int var34 = var33 == 3 ? 36 : 24;

                        for(int var35 = 0; var35 < var34; ++var35) {
                           BlockState var36;
                           if (var33 == 0) {
                              var36 = tapinakKosk(var20, var35);
                           } else if (var33 == 1) {
                              var36 = revakliVilla(var20, var35);
                           } else if (var33 == 2) {
                              var36 = batiLoncaKonagi(var21, var22, var20.ax, var20.az, var35, var20.h);
                           } else if (var33 == 3) {
                              var36 = batiGozlemKulesi(var21, var22, var20.ax, var20.az, var35);
                           } else if (var33 == 4) {
                              var36 = batiKutuphaneBinasi(var21, var22, var20.ax, var20.az, var35);
                           } else {
                              var36 = batiCarsiPavyonu(var21, var22, var20.ax, var20.az, var35);
                           }

                           if (var36 != null) {
                              set(var0, var1, var2, var10 + 1 + var35, var3, var36);
                           }
                        }

                        if ((var16 == 4 || var16 == 21) && (var17 == 4 || var17 == 21)) {
                           for(int var58 = 0; var58 <= 3; ++var58) {
                              set(var0, var1, var2, var10 + 1 + var58, var3, var58 == 3 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.SUTUN);
                           }
                        }
                     } else {
                        for(int var56 = 0; var56 < 12; ++var56) {
                           BlockState var57 = batiMeydanParki(var16, var17, var20.ax, var20.az, var56, var20.h);
                           if (var57 != null) {
                              set(var0, var1, var2, var10 + 1 + var56, var3, var57);
                           }
                        }
                     }

                  }
               } else {
                  set(var0, var1, var2, var10, var3, NurBaskentFeature.P.ALTIN);

                  for(int var52 = 0; var52 <= 4; ++var52) {
                     set(var0, var1, var2, var10 + 1 + var52, var3, var52 == 4 ? NurBaskentFeature.P.FENER : (var52 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.SUTUN));
                  }

               }
            } else {
               if (var16 != 0 && var17 != 0) {
                  if ((var16 + var17) % 6 == 0) {
                     set(var0, var1, var2, var10, var3, NurBaskentFeature.P.DFENER);
                  } else {
                     set(var0, var1, var2, var10, var3, (var2 + var3 & 1) == 0 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.KARATAS_TUGLA);
                  }
               } else {
                  set(var0, var1, var2, var10, var3, NurBaskentFeature.P.ALTIN);
               }

               if ((var16 == 2 || var16 == 23) && (var17 == 2 || var17 == 23)) {
                  for(int var23 = 0; var23 <= 4; ++var23) {
                     set(var0, var1, var2, var10 + 1 + var23, var3, var23 == 4 ? NurBaskentFeature.P.FENER : (var23 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.SUTUN));
                  }
               }

            }
         } else {
            if (var8 != 0 && var9 != 0 && var8 != 11 && var9 != 11) {
               if ((var8 + var9) % 6 == 0) {
                  set(var0, var1, var2, var10, var3, NurBaskentFeature.P.DFENER);
               } else {
                  set(var0, var1, var2, var10, var3, (var2 + var3 & 1) == 0 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.KARATAS_TUGLA);
               }
            } else {
               set(var0, var1, var2, var10, var3, NurBaskentFeature.P.ALTIN);
            }

            if (var8 == 11 && var9 >= 42 && var9 % 14 == 0 || var9 == 11 && var8 >= 42 && var8 % 14 == 0) {
               for(int var38 = 0; var38 <= 4; ++var38) {
                  set(var0, var1, var2, var10 + 1 + var38, var3, var38 == 4 ? NurBaskentFeature.P.FENER : (var38 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.SUTUN));
               }
            }

         }
      }
   }

   private static BlockState batiMerkezYildizSarayi(int var0, int var1, int var2, int var3, int var4, double var5) {
      int var7 = Math.max(var2, var3);
      double var8 = 0.4 * var5 + 0.6 * (double)var7;
      int var10 = var2 - 26;
      int var11 = var3 - 26;
      double var12 = Math.sqrt((double)var10 * (double)var10 + (double)var11 * (double)var11);
      if (var12 <= 6.2 && var4 <= 46) {
         if (var4 <= 32) {
            if (var12 < (double)4.5F) {
               if (var4 != 0 && var4 != 16 && var4 != 32) {
                  if (var10 == 0 && var11 == 0) {
                     return NurBaskentFeature.P.DFENER;
                  } else {
                     return var7 == 26 && var4 <= 14 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.HAVA;
                  }
               } else {
                  return NurBaskentFeature.P.KUVARS;
               }
            } else if (var4 != 0 && var4 != 16 && var4 != 32) {
               if (var4 % 8 < 3 || var4 % 8 > 5 || Math.abs(var10) > 1 && Math.abs(var11) > 1) {
                  return Math.abs(var10) > 1 && Math.abs(var11) > 1 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.PRIZMARIN;
               } else {
                  return NurBaskentFeature.P.CAM_GOK;
               }
            } else {
               return NurBaskentFeature.P.ALTIN;
            }
         } else {
            double var22 = (double)var4 - (double)32.0F;
            double var25 = Math.sqrt(var12 * var12 + var22 * var22);
            if (var25 <= 6.2) {
               if (!(var25 >= (double)4.5F) && var4 < 37) {
                  return NurBaskentFeature.P.HAVA;
               } else {
                  return (var4 + var2 + var3) % 2 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.AMETIST;
               }
            } else if (var4 <= 44 && var12 <= (double)1.0F) {
               return var4 == 44 ? NurBaskentFeature.P.DFENER : NurBaskentFeature.P.ALTIN;
            } else {
               return null;
            }
         }
      } else if (var7 == 26 && var2 <= 26 && var3 <= 26 && var4 <= 14) {
         int var21 = var2 == 26 ? var3 : var2;
         if (var21 <= 4 && var4 >= 1 && var4 <= 10) {
            int var23 = 7 + (int)Math.sqrt((double)Math.max(0, 16 - var21 * var21));
            if (var4 < var23) {
               return null;
            }

            if (var4 == var23) {
               return NurBaskentFeature.P.ALTIN;
            }
         }

         if (var4 == 14) {
            return (var0 + var1 & 1) == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.PRIZMARIN;
         } else if (var4 != 0 && var4 != 13) {
            if (var21 % 5 == 0) {
               return NurBaskentFeature.P.PRIZMARIN;
            } else {
               return var4 >= 4 && var4 <= 10 && var21 % 5 == 2 ? NurBaskentFeature.P.CAM_GOK : NurBaskentFeature.P.KARATAS_TUGLA;
            }
         } else {
            return NurBaskentFeature.P.ALTIN;
         }
      } else if (var4 <= 42) {
         int var20 = var4 / 14;
         int var15 = var4 % 14;
         double var24 = var20 == 0 ? (double)21.0F : (var20 == 1 ? (double)16.5F : (double)12.5F);
         if (!(var8 <= var24)) {
            return null;
         } else if (var15 != 13 && var4 != 42) {
            if (var8 >= var24 - 1.6) {
               if (var15 < 1 || var15 > 8 || var2 > 3 && var3 > 3) {
                  if (var15 == 0) {
                     return NurBaskentFeature.P.ALTIN;
                  } else if (var15 >= 3 && var15 <= 10 && (var2 + var3) % 4 == 0) {
                     return NurBaskentFeature.P.CAM_GOK;
                  } else {
                     return (var4 + var2 + var3) % 3 == 0 ? NurBaskentFeature.P.PRIZMARIN : NurBaskentFeature.P.KARATAS_TUGLA;
                  }
               } else {
                  int var18 = Math.min(var2, var3);
                  return var15 < 6 + (3 - var18) ? NurBaskentFeature.P.HAVA : NurBaskentFeature.P.ALTIN;
               }
            } else if (var5 <= (double)2.5F) {
               if (var5 <= 1.2) {
                  return var4 % 3 == 0 ? NurBaskentFeature.P.DFENER : NurBaskentFeature.P.ALTIN;
               } else {
                  return NurBaskentFeature.P.KUVARS;
               }
            } else if ((var2 != 8 || var3 != 8) && (var20 != 0 || var2 != 14 || var3 != 14)) {
               if (var15 == 0) {
                  if (var2 <= 4 || var3 <= 4) {
                     return NurBaskentFeature.P.SARI_HALI;
                  }

                  if (var2 == (int)var24 - 3 && var3 <= 6) {
                     return NurBaskentFeature.P.KITAP;
                  }

                  if (var3 == (int)var24 - 3 && var2 == 4) {
                     return NurBaskentFeature.P.SANDIK;
                  }

                  if (var3 == (int)var24 - 3 && var2 == 5) {
                     return NurBaskentFeature.P.BUYU_MASASI;
                  }

                  if (var3 == (int)var24 - 3 && var2 == 6) {
                     return NurBaskentFeature.P.KURSU;
                  }
               }

               if (var15 == 1 && var2 == (int)var24 - 3 && var3 <= 6) {
                  return NurBaskentFeature.P.KITAP;
               } else if (var15 == 12 && var2 == 4 && var3 == 4) {
                  return NurBaskentFeature.P.DFENER;
               } else {
                  return NurBaskentFeature.P.HAVA;
               }
            } else {
               return var15 == 6 ? NurBaskentFeature.P.DFENER : NurBaskentFeature.P.PRIZMARIN;
            }
         } else {
            return var8 >= var24 - 1.2 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KUVARS;
         }
      } else if (var4 <= 56) {
         double var19 = (double)var4 - (double)42.0F;
         double var16 = Math.sqrt(var5 * var5 + var19 * var19);
         if (!(var16 <= 12.2)) {
            return null;
         } else if (!(var16 >= 10.2) && var4 < 53) {
            return var2 == 0 && var3 == 0 ? NurBaskentFeature.P.DFENER : NurBaskentFeature.P.HAVA;
         } else if (var2 > 1 && var3 > 1 && Math.abs(var2 - var3) > 1 && var4 != 48) {
            return (var4 + var2 + var3) % 2 == 0 ? NurBaskentFeature.P.AMETIST : NurBaskentFeature.P.CAM_GOK;
         } else {
            return NurBaskentFeature.P.ALTIN;
         }
      } else {
         double var14 = Math.max(0.8, 3.2 - (double)(var4 - 57) * 0.09);
         if ((var4 == 64 || var4 == 72) && var5 >= var14 + 1.2 && var5 <= var14 + 2.8) {
            return NurBaskentFeature.P.ALTIN;
         } else if (var5 <= var14) {
            if (var4 < 80 && !(var5 <= 1.1)) {
               return var4 % 3 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.AMETIST;
            } else {
               return NurBaskentFeature.P.DFENER;
            }
         } else {
            return null;
         }
      }
   }

   private static BlockState batiLoncaKonagi(int var0, int var1, int var2, int var3, int var4, int var5) {
      byte var6 = 7;
      byte var7 = 7;
      if (var2 <= var6 + 1 && var3 <= var7 + 1) {
         if (var4 == 0) {
            return var2 != var6 + 1 && var3 != var7 + 1 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN;
         } else if (var4 > 14) {
            int var10 = var4 - 15;
            int var9 = var6 + 1 - var10;
            if (var9 >= 0 && var2 <= var9 && var3 <= var7 + 1) {
               if (var9 == 0) {
                  return var3 % 2 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.ALTIN;
               } else if (var2 >= var9 - 1) {
                  return var3 == var7 + 1 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.PRIZMARIN;
               } else {
                  return var3 >= var7 ? NurBaskentFeature.P.OYMA : NurBaskentFeature.P.HAVA;
               }
            } else {
               return null;
            }
         } else if (var2 <= var6 && var3 <= var7) {
            boolean var8 = var2 == var6 || var3 == var7;
            if (!var8) {
               if (var4 != 5 && var4 != 10 && var4 != 14) {
                  if (var0 >= -var6 + 1 && var0 <= -var6 + 2 && var1 >= -3 && var1 <= 2 && var4 <= 10) {
                     return var1 == -3 + var4 % 5 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.HAVA;
                  } else {
                     if (var4 == 1 || var4 == 6 || var4 == 11) {
                        if (var3 == var7 - 1 && var2 <= 3) {
                           return NurBaskentFeature.P.KITAP;
                        }

                        if (var2 == var6 - 1 && var1 == 2) {
                           return NurBaskentFeature.P.SANDIK;
                        }

                        if (var2 == var6 - 1 && var1 == 3) {
                           return var4 == 1 ? NurBaskentFeature.P.CALISMA_MASASI : NurBaskentFeature.P.ORS;
                        }

                        if (var4 == 11 && var2 == var6 - 1 && var1 >= -3 && var1 <= -1) {
                           return var1 == -1 ? NurBaskentFeature.P.BEYAZ_YUN : NurBaskentFeature.P.KIRMIZI_YUN;
                        }

                        if (var2 == 0 && var3 == 0) {
                           return var4 == 6 ? NurBaskentFeature.P.BUYU_MASASI : NurBaskentFeature.P.KURSU;
                        }

                        if (var2 <= 2 && var3 <= 2) {
                           return NurBaskentFeature.P.SARI_HALI;
                        }
                     }

                     if ((var4 == 2 || var4 == 7 || var4 == 12) && var3 == var7 - 1 && var2 <= 3) {
                        return NurBaskentFeature.P.KITAP;
                     } else {
                        return var2 != 0 || var3 != 0 || var4 != 4 && var4 != 9 && var4 != 13 ? NurBaskentFeature.P.HAVA : NurBaskentFeature.P.FENER;
                     }
                  }
               } else {
                  return NurBaskentFeature.P.KUVARS;
               }
            } else if (var4 <= 3 && var2 <= 1 && var3 == var7) {
               return NurBaskentFeature.P.HAVA;
            } else if (var2 == var6 && var3 == var7) {
               return NurBaskentFeature.P.SUTUN;
            } else if (var4 != 5 && var4 != 10 && var4 != 14) {
               if (var4 % 5 < 2 || var4 % 5 > 4 || var2 != 0 && var2 != 3 && var3 != 0 && var3 != 3) {
                  return (var4 + var2 + var3) % 4 == 0 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.KTUGLA;
               } else {
                  return NurBaskentFeature.P.CAM_GOK;
               }
            } else {
               return NurBaskentFeature.P.ALTIN;
            }
         } else if (var4 == 14) {
            return NurBaskentFeature.P.ALTIN;
         } else if (var2 == var6 + 1 && var3 == var7 + 1) {
            return var4 == 13 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.SUTUN;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static BlockState batiGozlemKulesi(int var0, int var1, int var2, int var3, int var4) {
      double var5 = Math.sqrt((double)var0 * (double)var0 + (double)var1 * (double)var1);
      if (var5 > 8.2) {
         return null;
      } else if (var4 == 0 && var5 <= (double)8.0F) {
         return var5 >= (double)7.0F ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KUVARS;
      } else if (var4 <= 22) {
         double var11 = var4 <= 4 ? 6.8 : 5.6;
         if (!(var5 <= var11)) {
            return null;
         } else if (var4 <= 3 && var2 <= 1 && var1 > 0) {
            return NurBaskentFeature.P.HAVA;
         } else if (var5 < var11 - 1.55) {
            if (var4 != 22 && var4 % 7 != 0) {
               if (var2 == 0 && var3 == 0) {
                  return NurBaskentFeature.P.FENER;
               } else if (var4 % 7 == 1 && var2 == 2 && var3 == 0) {
                  return NurBaskentFeature.P.KITAP;
               } else {
                  return var4 % 7 == 1 && var2 == 0 && var3 == 2 ? NurBaskentFeature.P.BUYU_MASASI : NurBaskentFeature.P.HAVA;
               }
            } else {
               return NurBaskentFeature.P.KUVARS;
            }
         } else if (var4 < 15 || var4 > 19 || var2 > 2 && var3 > 2) {
            if (var4 != 7 && var4 != 14 && var4 != 22) {
               if (var4 % 7 < 2 || var4 % 7 > 5 || var2 > 1 && var3 > 1) {
                  return var2 != var3 && var2 > 1 && var3 > 1 ? NurBaskentFeature.P.KTUGLA : NurBaskentFeature.P.SUTUN;
               } else {
                  return NurBaskentFeature.P.CAM_GOK;
               }
            } else {
               return NurBaskentFeature.P.ALTIN;
            }
         } else {
            int var12 = Math.min(var2, var3);
            if (var12 == 0 && var4 == 17) {
               return NurBaskentFeature.P.FENER;
            } else {
               return var12 <= 1 && var4 >= 16 && var4 <= 18 ? NurBaskentFeature.P.CAM_ALTIN : NurBaskentFeature.P.ALTIN;
            }
         }
      } else if (var4 == 23 && var5 <= (double)7.0F) {
         return var5 >= (double)6.0F ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.OYMA;
      } else if (var4 != 24 || !(var5 >= 5.8) || !(var5 <= (double)7.0F) || var2 > 1 && var3 > 1) {
         if (var4 >= 24 && var4 <= 31) {
            double var7 = (double)var4 - (double)24.0F;
            double var9 = Math.sqrt(var5 * var5 + var7 * var7);
            if (var9 <= 5.8) {
               if (!(var9 >= 4.2) && var4 < 29) {
                  return NurBaskentFeature.P.HAVA;
               }

               return var2 != 0 && var3 != 0 && var4 != 28 ? NurBaskentFeature.P.AMETIST : NurBaskentFeature.P.ALTIN;
            }
         }

         if (var4 >= 31 && var4 <= 35 && var5 <= 1.4) {
            return var4 != 35 && !(var5 <= 0.7) ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.FENER;
         } else {
            return null;
         }
      } else {
         return NurBaskentFeature.P.FENER;
      }
   }

   private static BlockState batiKutuphaneBinasi(int var0, int var1, int var2, int var3, int var4) {
      byte var5 = 7;
      byte var6 = 7;
      if (var2 <= var5 + 1 && var3 <= var6 + 1) {
         if (var4 == 0) {
            return var2 != var5 + 1 && var3 != var6 + 1 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN;
         } else if (var4 <= 11) {
            if (var2 <= var5 && var3 <= var6) {
               boolean var10 = var2 == var5 || var3 == var6;
               if (!var10) {
                  if (var4 == 11) {
                     return var2 <= 2 && var3 <= 2 ? NurBaskentFeature.P.CAM_GOK : NurBaskentFeature.P.KUVARS;
                  } else if (var4 != 6 || var2 < 4 && var3 < 4) {
                     if (var4 >= 1 && var4 <= 4 && (var2 == var5 - 1 || var3 == var6 - 1) && var2 > 1 && var3 > 1) {
                        return NurBaskentFeature.P.KITAP;
                     } else if (var4 >= 7 && var4 <= 9 && (var2 == var5 - 1 || var3 == var6 - 1) && var2 > 1 && var3 > 1) {
                        return NurBaskentFeature.P.KITAP;
                     } else {
                        if (var4 == 1) {
                           if (var2 == 0 && var3 == 0) {
                              return NurBaskentFeature.P.BUYU_MASASI;
                           }

                           if (var2 == 2 && var3 == 2) {
                              return NurBaskentFeature.P.KURSU;
                           }

                           if (var2 == 3 && var3 == 0) {
                              return NurBaskentFeature.P.SANDIK;
                           }

                           if (var2 <= 3 && var3 <= 3) {
                              return NurBaskentFeature.P.KIRMIZI_HALI;
                           }
                        }

                        return var4 == 9 && var2 == 0 && var3 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.HAVA;
                     }
                  } else {
                     return NurBaskentFeature.P.KUVARS;
                  }
               } else if (var4 > 4 || (var2 > 1 || var3 != var6) && (var3 > 1 || var2 != var5)) {
                  if (var2 == var5 && var3 == var6) {
                     return NurBaskentFeature.P.SUTUN;
                  } else if (var4 != 6 && var4 != 11) {
                     return var4 < 2 || var4 > 9 || var2 > 2 && var3 > 2 ? NurBaskentFeature.P.KTUGLA : NurBaskentFeature.P.CAM_ALTIN;
                  } else {
                     return NurBaskentFeature.P.ALTIN;
                  }
               } else {
                  return NurBaskentFeature.P.HAVA;
               }
            } else if (var4 == 11) {
               return NurBaskentFeature.P.ALTIN;
            } else if (var2 == var5 + 1 && var3 % 3 == 0 || var3 == var6 + 1 && var2 % 3 == 0 || var2 == var5 + 1 && var3 == var6 + 1) {
               return var4 == 10 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.SUTUN;
            } else {
               return null;
            }
         } else {
            int var7 = var4 - 12;
            double var8 = Math.sqrt(Math.max((double)0.0F, (double)64.0F - ((double)var7 + (double)2.0F) * ((double)var7 + (double)2.0F)));
            if (var7 <= 6 && (double)var2 <= var8 && var3 <= var6 + 1) {
               if (!((double)var2 >= var8 - 1.8) && var7 != 6) {
                  return var3 >= var6 ? NurBaskentFeature.P.OYMA : NurBaskentFeature.P.HAVA;
               } else {
                  return var3 != 0 && var3 != var6 + 1 && var2 > 1 ? NurBaskentFeature.P.CAM_GOK : NurBaskentFeature.P.ALTIN;
               }
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static BlockState batiCarsiPavyonu(int var0, int var1, int var2, int var3, int var4) {
      byte var5 = 7;
      byte var6 = 7;
      if (var2 <= var5 + 1 && var3 <= var6 + 1) {
         if (var4 == 0) {
            return (var2 + var3) % 2 == 0 ? NurBaskentFeature.P.KUVARS : NurBaskentFeature.P.ALTIN;
         } else if (var4 > 8) {
            double var11 = Math.sqrt((double)var0 * (double)var0 + (double)var1 * (double)var1);
            double var9 = 7.8 - (double)(var4 - 9) * 1.15;
            if (var9 >= (double)0.0F && var11 <= var9) {
               if (var9 <= (double)1.5F) {
                  return NurBaskentFeature.P.FENER;
               } else if (var11 >= var9 - (double)2.0F) {
                  return (var4 - 9) % 2 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.LAPIS;
               } else {
                  return NurBaskentFeature.P.HAVA;
               }
            } else {
               return null;
            }
         } else if (var2 <= var5 && var3 <= var6) {
            boolean var7 = var2 == var5 || var3 == var6;
            if (!var7) {
               if (var4 == 8) {
                  return NurBaskentFeature.P.KUVARS;
               } else {
                  if (var4 == 1) {
                     if (var2 == 4 && var3 == 4) {
                        return NurBaskentFeature.P.SANDIK;
                     }

                     if (var2 == 4 && var3 == 3) {
                        return NurBaskentFeature.P.VARIL;
                     }

                     if (var2 == 3 && var3 == 4) {
                        return NurBaskentFeature.P.CALISMA_MASASI;
                     }

                     if (var2 == 5 && var3 == 5) {
                        return NurBaskentFeature.P.ORS;
                     }

                     if (var2 == 0 && var3 == 0) {
                        return NurBaskentFeature.P.FENER;
                     }

                     if (var2 <= 2 || var3 <= 2) {
                        return NurBaskentFeature.P.SARI_HALI;
                     }
                  }

                  if (var4 == 7 && var2 == 0 && var3 == 0) {
                     return NurBaskentFeature.P.FENER;
                  } else {
                     return NurBaskentFeature.P.HAVA;
                  }
               }
            } else if (var4 > 4 || var2 > 2 && var3 > 2) {
               if (var4 == 8) {
                  return NurBaskentFeature.P.ALTIN;
               } else {
                  return var2 == var5 && var3 == var6 ? NurBaskentFeature.P.SUTUN : NurBaskentFeature.P.OYMA;
               }
            } else {
               return NurBaskentFeature.P.HAVA;
            }
         } else if (var4 == 8) {
            return NurBaskentFeature.P.ALTIN;
         } else {
            return var2 == var5 + 1 && var3 == var6 + 1 ? NurBaskentFeature.P.SUTUN : null;
         }
      } else {
         return null;
      }
   }

   private static BlockState batiSurKulesi(double var0, double var2, double var4, int var6, int var7, int var8) {
      double var9 = Math.abs(var0);
      boolean var11 = var9 >= 4.2 || var2 >= 4.8;
      if (var6 <= 32) {
         if (var6 >= 24 && var6 <= 29) {
            if (var2 >= (double)4.0F && var9 <= 2.6) {
               int var17 = 28 + (var9 <= 1.2 ? 1 : 0);
               if (var6 < var17) {
                  return null;
               }

               return var6 == var17 ? NurBaskentFeature.P.DFENER : NurBaskentFeature.P.ALTIN;
            }

            if (var2 >= (double)4.0F && var9 <= 3.6 && var6 <= 28) {
               return var6 == 28 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.PRIZMARIN;
            }

            if (var9 >= 3.8 && var2 <= 1.8 && var6 <= 27) {
               if (var6 < 27 && var2 <= 1.1) {
                  return null;
               }

               return var6 == 27 ? NurBaskentFeature.P.DFENER : NurBaskentFeature.P.ALTIN;
            }
         }

         if (var6 <= 6 && var0 <= -3.6 && var2 <= 2.4) {
            if (var2 <= 1.4 && var6 <= 4) {
               return null;
            } else {
               return var2 <= 1.4 && var6 == 5 ? NurBaskentFeature.P.DFENER : NurBaskentFeature.P.ALTIN;
            }
         } else if (!var11) {
            if (var6 != 22 && var6 != 23) {
               if (var6 != 8 && var6 != 16 && var6 != 32) {
                  if (var9 <= 0.9 && var2 <= 0.9) {
                     return var6 % 4 == 0 ? NurBaskentFeature.P.DFENER : NurBaskentFeature.P.SUTUN;
                  } else {
                     if (var6 < 22) {
                        double var16 = (double)var6 * 0.55 % (Math.PI * 2D) - Math.PI;
                        double var18 = Math.atan2(var0, var4 * (double)310.0F);
                        if (Math.abs(Math.atan2(Math.sin(var18 - var16), Math.cos(var18 - var16))) <= 0.55) {
                           return NurBaskentFeature.P.KUVARS;
                        }
                     }

                     return null;
                  }
               } else {
                  return NurBaskentFeature.P.KUVARS;
               }
            } else {
               return NurBaskentFeature.P.ALTIN;
            }
         } else if (var6 != 8 && var6 != 16 && var6 != 23 && var6 != 31 && var6 != 32) {
            if (var9 >= 4.2 && var2 >= 4.8) {
               return NurBaskentFeature.P.PRIZMARIN;
            } else if (var6 % 8 < 3 || var6 % 8 > 6 || !(var2 <= 1.6) && !(var9 <= 1.6)) {
               return !(var2 <= 2.4) && !(var9 <= 2.4) ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.SUTUN;
            } else {
               return var6 >= 24 ? NurBaskentFeature.P.CAM_ALTIN : NurBaskentFeature.P.CAM_GOK;
            }
         } else {
            return NurBaskentFeature.P.ALTIN;
         }
      } else if (var6 == 33 && var11) {
         return (var7 + var8 & 1) == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.KARATAS_TUGLA;
      } else {
         if (var6 >= 33 && var6 <= 38) {
            double var12 = Math.sqrt(var0 * var0 + var2 * var2);
            double var14 = 4.8 - (double)(var6 - 33) * 0.65;
            if (var12 <= var14) {
               if (!(var12 >= var14 - (double)1.5F) && var6 != 38) {
                  return null;
               }

               return (var6 + var7 + var8) % 2 == 0 ? NurBaskentFeature.P.ALTIN : NurBaskentFeature.P.AMETIST;
            }
         }

         if (var6 >= 39 && var9 <= 0.9 && var2 <= 0.9) {
            return var6 == 40 ? NurBaskentFeature.P.DFENER : NurBaskentFeature.P.ALTIN;
         } else {
            return null;
         }
      }
   }

   private static BlockState parkAgaci(int var0, int var1, int var2, int var3, int var4) {
      if (var0 == var3 && var1 == var4 && var2 < 5) {
         return NurBaskentFeature.P.GOVDE;
      } else {
         int var5 = var0 - var3;
         int var6 = var1 - var4;
         int var7 = var2 - 5;
         if (var7 >= -1 && var7 <= 2) {
            double var8 = (double)(var5 * var5 + var6 * var6) + (double)(var7 * var7) * 1.4;
            if (var8 <= (double)4.5F) {
               if (var5 == 0 && var6 == 0 && var7 == 0) {
                  return NurBaskentFeature.P.FENER;
               }

               return NurBaskentFeature.P.YAPRAK;
            }
         }

         return null;
      }
   }

   private static BlockState batiMeydanParki(int var0, int var1, int var2, int var3, int var4, int var5) {
      double var6 = Math.sqrt((double)var2 * (double)var2 + (double)var3 * (double)var3);
      if (var6 <= (double)4.5F && var4 <= 7) {
         if (var4 == 0) {
            if (var6 <= 1.2) {
               return NurBaskentFeature.P.ALTIN;
            } else {
               return var6 <= 3.4 ? NurBaskentFeature.P.SU : NurBaskentFeature.P.OYMA;
            }
         } else if (var6 <= 1.2 && var4 <= 5) {
            return var4 == 5 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.SUTUN;
         } else {
            return var6 <= (double)2.0F && var4 == 3 ? NurBaskentFeature.P.SU : null;
         }
      } else {
         BlockState var8 = parkAgaci(var0, var1, var4, 7, 7);
         if (var8 != null) {
            return var8;
         } else {
            BlockState var9 = parkAgaci(var0, var1, var4, 19, 7);
            if (var9 != null) {
               return var9;
            } else {
               BlockState var10 = parkAgaci(var0, var1, var4, 7, 19);
               return var10 != null ? var10 : parkAgaci(var0, var1, var4, 19, 19);
            }
         }
      }
   }

   private static void doguDaglariKolonu(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3) {
      double var4 = smooth((double)1500.0F, (double)1580.0F, (double)var2) * ((double)1.0F - smooth((double)2580.0F, (double)2720.0F, (double)var2)) * ((double)1.0F - smooth((double)480.0F, (double)590.0F, (double)Math.abs(var3)));
      if (!(var4 <= (double)0.0F)) {
         int var6 = 74;

         for(int var7 = 135; var7 >= 50; --var7) {
            BlockState var8 = var0.getBlockState(var1.set(var2, var7, var3));
            if (!var8.isAir() && var8 != NurBaskentFeature.P.SU && var8 != NurBaskentFeature.P.KARANLIK_YAPRAK && var8 != NurBaskentFeature.P.KARANLIK_GOVDE && var8 != NurBaskentFeature.P.YAPRAK && var8 != NurBaskentFeature.P.MESE_YAPRAK) {
               var6 = var7;
               break;
            }
         }

         for(int var28 = 0; var28 < DOGU_KOYLERI.length; ++var28) {
            int var30 = DOGU_KOYLERI[var28][0];
            int var9 = DOGU_KOYLERI[var28][1];
            int var10 = var2 - var30;
            int var11 = var3 - var9;
            double var12 = Math.sqrt((double)var10 * (double)var10 + (double)var11 * (double)var11);
            if (var12 <= (double)62.0F) {
               doguKoyuKolonu(var0, var1, var2, var3, var10, var11, var12, var6);
               return;
            }
         }

         byte var29 = 36;
         int var31 = Math.floorDiv(var2, var29);
         int var32 = Math.floorDiv(var3, var29);
         int var33 = Math.floorMod(var2, var29);
         int var34 = Math.floorMod(var3, var29);
         int var35 = var31 * var29 + 18;
         int var13 = var32 * var29 + 18;
         int var14 = hash(var31 * 31 + 7, var32 * 19 + 13);
         double var15 = dagYukseklikOrani(var35, var13);
         boolean var17 = var4 > (double)0.25F && var15 < 0.52 && var14 % 100 < 55;
         if (var17) {
            int var18 = var33 - 18;
            int var19 = var34 - 18;
            int var20 = Math.abs(var18);
            int var21 = Math.abs(var19);
            if (var20 <= 8 && var21 <= 8) {
               byte var40 = 78;

               for(int var23 = 55; var23 <= var40; ++var23) {
                  set(var0, var1, var2, var23, var3, var23 == var40 ? NurBaskentFeature.P.MESE_TAHTA : NurBaskentFeature.P.TAS_TUGLA);
               }

               for(int var41 = 1; var41 <= 24; ++var41) {
                  var0.setBlock(var1.set(var2, var40 + var41, var3), NurBaskentFeature.P.HAVA, 2);
               }

               if (var20 <= 5 && var21 <= 5) {
                  for(int var42 = 1; var42 <= 14; ++var42) {
                     BlockState var43 = doguKulubesi(var18, var19, var20, var21, var42);
                     if (var43 != null) {
                        set(var0, var1, var2, var40 + var42, var3, var43);
                     }
                  }

                  return;
               }

               set(var0, var1, var2, var40, var3, NurBaskentFeature.P.YOSUN);
               return;
            }

            if (var33 <= 2 || var34 <= 2) {
               set(var0, var1, var2, var6, var3, (var2 + var3 & 1) == 0 ? NurBaskentFeature.P.TAS_TUGLA : NurBaskentFeature.P.KARATAS_TUGLA);
               return;
            }
         }

         double var36 = (double)999.0F;

         for(int var37 = 0; var37 < DOGU_KOYLERI.length; ++var37) {
            double var39 = Math.sqrt((double)(var2 - DOGU_KOYLERI[var37][0]) * (double)(var2 - DOGU_KOYLERI[var37][0]) + (double)(var3 - DOGU_KOYLERI[var37][1]) * (double)(var3 - DOGU_KOYLERI[var37][1]));
            if (var39 < var36) {
               var36 = var39;
            }
         }

         double var38 = smooth((double)58.0F, (double)108.0F, var36);
         double var22 = dagYukseklikOrani(var2, var3);
         if (var22 > 0.12 && var38 > 0.02) {
            int var24 = (int)Math.round(var4 * var38 * Math.pow((var22 - 0.12) / 0.88, 1.3) * (double)124.0F);
            int var25 = var6 + var24;

            for(int var26 = var6; var26 <= var25; ++var26) {
               BlockState var27;
               if (var26 < 156 && (var26 < 140 || (hash(var2, var3 + var26) & 3) != 0)) {
                  if (var26 >= 120) {
                     var27 = (var2 + var26 + var3) % 3 == 0 ? NurBaskentFeature.P.KARATAS : NurBaskentFeature.P.TAS;
                  } else if (var26 == var25 && var24 < 18) {
                     var27 = NurBaskentFeature.P.YOSUN;
                  } else {
                     var27 = (var2 + var3 & 1) == 0 ? NurBaskentFeature.P.TAS : NurBaskentFeature.P.KARATAS_TUGLA;
                  }
               } else {
                  var27 = var26 == var25 ? NurBaskentFeature.P.KAR_BLOGU : NurBaskentFeature.P.KALSIT;
               }

               set(var0, var1, var2, var26, var3, var27);
            }
         }

      }
   }

   private static void doguKoyuKolonu(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3, int var4, int var5, double var6, int var8) {
      int var9 = 78;
      if (var6 > (double)48.0F) {
         double var10 = smooth((double)48.0F, (double)62.0F, var6);
         var9 = (int)Math.round((double)78.0F * ((double)1.0F - var10) + (double)var8 * var10);
      }

      for(int var20 = 55; var20 < var9; ++var20) {
         set(var0, var1, var2, var20, var3, var20 >= var9 - 2 ? NurBaskentFeature.P.TOPRAK : NurBaskentFeature.P.TAS);
      }

      for(int var21 = 1; var21 <= 32; ++var21) {
         var0.setBlock(var1.set(var2, var9 + var21, var3), NurBaskentFeature.P.HAVA, 2);
      }

      int var22 = Math.abs(var4);
      int var11 = Math.abs(var5);
      int[][] var12 = new int[][]{{-22, -22}, {22, -22}, {-22, 22}, {22, 22}, {-38, 0}, {38, 0}, {0, -38}, {0, 38}};

      for(int var13 = 0; var13 < var12.length; ++var13) {
         int var14 = var4 - var12[var13][0];
         int var15 = var5 - var12[var13][1];
         if (var12[var13][1] > 0) {
            var15 = -var15;
         } else if (var12[var13][1] == 0 && var12[var13][0] != 0) {
            int var16 = var14;
            var14 = var15;
            var15 = var12[var13][0] > 0 ? -var16 : var16;
         }

         int var30 = Math.abs(var14);
         int var17 = Math.abs(var15);
         if (var30 <= 5 && var17 <= 5) {
            set(var0, var1, var2, var9, var3, NurBaskentFeature.P.TAS_TUGLA);

            for(int var18 = 1; var18 <= 14; ++var18) {
               BlockState var19 = doguKulubesi(var14, var15, var30, var17, var18);
               if (var19 != null) {
                  set(var0, var1, var2, var9 + var18, var3, var19);
               }
            }

            return;
         }
      }

      if (var6 <= (double)10.5F) {
         set(var0, var1, var2, var9, var3, (var2 + var3 & 1) == 0 ? NurBaskentFeature.P.TAS_TUGLA : NurBaskentFeature.P.KARATAS_TUGLA);
         if (var22 <= 5 && var11 <= 5) {
            boolean var24 = var22 == 5 || var11 == 5;

            for(int var26 = 1; var26 <= 18; ++var26) {
               if (var26 <= 12) {
                  if (var24) {
                     if (var26 > 3 || var22 > 1 && var11 > 1) {
                        if (var22 == 5 && var11 == 5) {
                           set(var0, var1, var2, var9 + var26, var3, NurBaskentFeature.P.LADIN_GOVDE);
                        } else if (var26 != 6 && var26 != 12) {
                           if (var26 != 3 && var26 != 9 || var22 > 1 && var11 > 1) {
                              set(var0, var1, var2, var9 + var26, var3, var26 <= 2 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.LADIN_TAHTA);
                           } else {
                              set(var0, var1, var2, var9 + var26, var3, NurBaskentFeature.P.CAM_ALTIN);
                           }
                        } else {
                           set(var0, var1, var2, var9 + var26, var3, NurBaskentFeature.P.LADIN_GOVDE);
                        }
                     }
                  } else if (var26 != 6 && var26 != 12) {
                     if (var22 != 0 || var11 != 0 || var26 != 5 && var26 != 11) {
                        if (var26 == 1 && var22 <= 1 && var11 <= 1) {
                           set(var0, var1, var2, var9 + var26, var3, NurBaskentFeature.P.KIRMIZI_HALI);
                        }
                     } else {
                        set(var0, var1, var2, var9 + var26, var3, NurBaskentFeature.P.FENER);
                     }
                  } else {
                     set(var0, var1, var2, var9 + var26, var3, NurBaskentFeature.P.MESE_TAHTA);
                  }
               } else {
                  int var29 = var26 - 12;
                  int var32 = 6 - var29;
                  if (var32 >= 0 && var22 <= var32 && var11 <= var32 && (var22 == var32 || var11 == var32 || var32 == 0)) {
                     set(var0, var1, var2, var9 + var26, var3, var32 == 0 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.LADIN_TAHTA);
                  }
               }
            }
         }

      } else {
         boolean var23 = var22 <= 2 && var11 <= 33 || var11 <= 2 && var22 <= 33;
         boolean var25 = Math.abs(var22 - 22) <= 1 && var11 <= 22 || Math.abs(var11 - 22) <= 1 && var22 <= 22;
         if (!var23 && !var25) {
            int var28 = hash(var2 * 17, var3 * 31) % 7;
            set(var0, var1, var2, var9, var3, var28 == 0 ? NurBaskentFeature.P.KARATAS_TUGLA : (var28 == 1 ? NurBaskentFeature.P.TAS : NurBaskentFeature.P.YOSUN));
            if (var6 >= (double)44.0F && var6 <= (double)50.0F && var22 % 14 == 0 && var11 % 14 == 0) {
               for(int var31 = 1; var31 <= 9; ++var31) {
                  set(var0, var1, var2, var9 + var31, var3, var31 <= 5 ? NurBaskentFeature.P.LADIN_GOVDE : NurBaskentFeature.P.MESE_YAPRAK);
               }
            }

         } else {
            set(var0, var1, var2, var9, var3, hash(var2, var3) % 3 == 0 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.TAS_TUGLA);
            if (var22 == 3 && var11 == 14 || var11 == 3 && var22 == 14) {
               for(int var27 = 1; var27 <= 4; ++var27) {
                  set(var0, var1, var2, var9 + var27, var3, var27 == 4 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.LADIN_GOVDE);
               }
            }

         }
      }
   }

   public static boolean zifiriYerlesimIciMi(int var0, int var1) {
      for(int var2 = 0; var2 < ZIFIRI_YERLESIMLER.length; ++var2) {
         int var3 = Math.abs(var0 - ZIFIRI_YERLESIMLER[var2][0]);
         int var4 = Math.abs(var1 - ZIFIRI_YERLESIMLER[var2][1]);
         int var5 = ZIFIRI_YERLESIMLER[var2][2];
         if (var3 <= var5 + 1 && var4 <= var5 + 1) {
            return true;
         }
      }

      return false;
   }

   public static boolean bataklikSaglamZeminMi(int var0, int var1) {
      if (zifiriYerlesimIciMi(var0, var1)) {
         return true;
      } else if (var0 * var0 + var1 * var1 <= 81) {
         return true;
      } else if ((Math.abs(var0) > 2 || var1 < -6 || var1 > 165) && (Math.abs(var1) > 2 || var0 < -165 || var0 > 165)) {
         double var16 = Math.sin((double)var1 / (double)12.0F) * 3.2;
         double var4 = Math.cos((double)var0 / (double)12.0F) * 3.2;
         boolean var6 = Math.abs((double)var0 - var16) <= 1.65;
         boolean var7 = Math.abs((double)var1 - var4) <= 1.65;
         if (!var6 && !var7) {
            int var17 = (Math.abs(var0) + Math.abs(var1)) % 44;
            if (var17 <= 1 && hash(var0 >> 1, var1 >> 1) % 5 != 0) {
               return true;
            } else {
               int var9 = Math.floorDiv(var0, 5);
               int var10 = Math.floorDiv(var1, 5);
               int var11 = hash(var9 * 37 + 19, var10 * 53 + 7);
               int var12 = 1 + var11 % 2;
               int var13 = 1 + (var11 >>> 3) % 2;
               int var14 = var0 - var9 * 5 - var12;
               int var15 = var1 - var10 * 5 - var13;
               return var14 >= 0 && var14 <= 1 && var15 >= 0 && var15 <= 1;
            }
         } else {
            int var8 = var6 ? Math.abs(var1) : Math.abs(var0);
            return var8 % 11 < 9;
         }
      } else {
         int var2 = Math.abs(var0) <= 2 ? Math.abs(var1) : Math.abs(var0);
         return var2 <= 14 || var2 >= 98 || var2 % 14 != 13;
      }
   }

   private static void guneyBoyutKapisiOlustur(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3) {
      int var4 = 72;

      for(int var5 = 140; var5 >= 55; --var5) {
         BlockState var6 = var0.getBlockState(var1.set(0, var5, 1500));
         if (!var6.isAir() && var6 != NurBaskentFeature.P.SU && var6 != NurBaskentFeature.P.KARANLIK_YAPRAK && var6 != NurBaskentFeature.P.KARANLIK_GOVDE) {
            var4 = var5;
            break;
         }
      }

      for(int var14 = 0; var14 < 16; ++var14) {
         for(int var15 = 0; var15 < 16; ++var15) {
            int var7 = var2 + var14;
            int var8 = var3 + var15;
            int var10 = var8 - 1500;
            int var11 = Math.abs(var7);
            int var12 = Math.abs(var10);
            if (var11 <= 8 && var12 <= 8) {
               for(int var13 = var4 + 1; var13 <= var4 + 24; ++var13) {
                  var0.setBlock(var1.set(var7, var13, var8), NurBaskentFeature.P.HAVA, 2);
               }

               for(int var16 = var4 - 3; var16 <= var4; ++var16) {
                  set(var0, var1, var7, var16, var8, (var7 + var8 & 1) == 0 ? NurBaskentFeature.P.CILALI_KARATAS : NurBaskentFeature.P.KARATAS_TUGLA);
               }

               if (var12 == 0 && var11 <= 3) {
                  for(int var17 = 1; var17 <= 7; ++var17) {
                     if (var11 == 3 || var17 == 7) {
                        set(var0, var1, var7, var4 + var17, var8, var17 != 4 && var17 != 7 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.AGLAYAN_OBSIDYEN);
                     }
                  }
               }
            }
         }
      }

   }

   private static BlockState zifiriKoyEvi(int var0, int var1, int var2, int var3, int var4) {
      byte var5 = 4;
      byte var6 = 4;
      if (var4 > 5) {
         int var9 = var4 - 6;
         int var8 = var5 + 1 - var9;
         if (var0 == 3 && var1 == 3 && var4 <= 11) {
            return var4 == 11 ? NurBaskentFeature.P.AGLAYAN_OBSIDYEN : NurBaskentFeature.P.KARATAS_TUGLA;
         } else if (var8 >= 0 && var3 <= var6 + 1 && var2 <= var8) {
            if (var2 < var8 - 1 && var8 != 0) {
               return var3 >= var6 ? NurBaskentFeature.P.LADIN_TAHTA : NurBaskentFeature.P.HAVA;
            } else {
               return NurBaskentFeature.P.KARATAS_TUGLA;
            }
         } else {
            return null;
         }
      } else if (var2 <= var5 && var3 <= var6) {
         boolean var7 = var2 == var5 || var3 == var6;
         if (var7) {
            if (var2 == var5 && var3 == var6) {
               return NurBaskentFeature.P.KARATAS_TUGLA;
            } else if (var1 == -var6 && var2 <= 1 && var4 <= 3) {
               return NurBaskentFeature.P.HAVA;
            } else if (var4 < 2 || var4 > 3 || var2 != 0 && var3 != 0) {
               return var4 == 1 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.LADIN_TAHTA;
            } else {
               return NurBaskentFeature.P.CAM;
            }
         } else if (var4 == 5) {
            return NurBaskentFeature.P.LADIN_TAHTA;
         } else {
            if (var4 == 1) {
               if (var1 == var6 - 1 && (var0 == -3 || var0 == -2)) {
                  return var0 == -2 ? NurBaskentFeature.P.BEYAZ_YUN : NurBaskentFeature.P.KIRMIZI_YUN;
               }

               if (var1 == var6 - 1 && var0 == 3) {
                  return NurBaskentFeature.P.SANDIK;
               }

               if (var1 == var6 - 1 && var0 == 2) {
                  return NurBaskentFeature.P.VARIL;
               }

               if (var0 == -3 && var1 == 0) {
                  return NurBaskentFeature.P.CALISMA_MASASI;
               }

               if (var0 == 3 && var1 == 0) {
                  return NurBaskentFeature.P.KITAP;
               }

               if (var2 <= 1 && var3 <= 1) {
                  return NurBaskentFeature.P.KIRMIZI_HALI;
               }
            }

            if (var4 == 4 && var2 == 0 && var3 == 0) {
               return NurBaskentFeature.P.FENER;
            } else {
               return NurBaskentFeature.P.HAVA;
            }
         }
      } else {
         return null;
      }
   }

   private static BlockState zifiriMerkezKalesi(int var0, int var1, int var2, int var3, int var4, int var5) {
      int var6 = Math.max(var2, var3);
      if (var6 > var5) {
         return null;
      } else {
         int var7 = var5 - 5;
         if (var2 >= var7 && var3 >= var7) {
            int var14 = var5 >= 16 ? 28 : 20;
            boolean var16 = var2 == var5 || var2 == var7 || var3 == var5 || var3 == var7;
            if (var4 <= var14) {
               if (!var16) {
                  if (var4 != 8 && var4 != 16 && var4 != var14) {
                     return var2 == var7 + 2 && var3 == var7 + 2 && var4 == var14 - 1 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.HAVA;
                  } else {
                     return NurBaskentFeature.P.CILALI_KARATAS;
                  }
               } else if (var4 > 4 || (var2 != var7 || var3 != var7 + 2) && (var3 != var7 || var2 != var7 + 2)) {
                  if (var4 != 8 && var4 != 16 && var4 != var14) {
                     return var4 % 6 < 3 || var4 % 6 > 4 || var2 != var7 + 2 && var3 != var7 + 2 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.AGLAYAN_OBSIDYEN;
                  } else {
                     return NurBaskentFeature.P.CILALI_KARATAS;
                  }
               } else {
                  return NurBaskentFeature.P.HAVA;
               }
            } else if (var4 == var14 + 1 && var16 && (var2 + var3 & 1) == 0) {
               return NurBaskentFeature.P.CILALI_KARATAS;
            } else {
               return var4 == var14 + 2 && var2 == var7 + 2 && var3 == var7 + 2 ? NurBaskentFeature.P.FENER : null;
            }
         } else {
            int var8 = var5 >= 16 ? 14 : 11;
            if (var6 >= var5 - 1) {
               int var15 = var2 >= var5 - 1 ? var3 : var2;
               if (var3 >= var5 - 1 && var2 <= 2 && var4 <= 6) {
                  int var17 = 5 + (2 - var2);
                  return var4 < var17 ? NurBaskentFeature.P.HAVA : NurBaskentFeature.P.AGLAYAN_OBSIDYEN;
               } else if (var4 <= var8) {
                  if (var4 == var8) {
                     return (var0 + var1 & 1) == 0 ? NurBaskentFeature.P.CILALI_KARATAS : NurBaskentFeature.P.HAVA;
                  } else if (var4 == var8 - 1) {
                     return NurBaskentFeature.P.CILALI_KARATAS;
                  } else if (var15 % 4 == 0) {
                     return NurBaskentFeature.P.CILALI_KARATAS;
                  } else {
                     return var4 >= 4 && var4 <= 8 && var15 % 4 == 2 ? NurBaskentFeature.P.CAM : NurBaskentFeature.P.KARATAS_TUGLA;
                  }
               } else {
                  return null;
               }
            } else {
               int var9 = var5 - 5;
               if (var6 <= var9) {
                  int var10 = var5 >= 16 ? 24 : 17;
                  boolean var11 = var6 == var9;
                  if (var4 <= var10) {
                     if (var11) {
                        if (var3 == var9 && var2 <= 2 && var4 <= 5) {
                           if (var4 < 5) {
                              return NurBaskentFeature.P.HAVA;
                           }

                           return NurBaskentFeature.P.AGLAYAN_OBSIDYEN;
                        }

                        if (var4 != 10 && var4 != 18 && var4 != var10) {
                           if (var2 == var9 && var3 == var9) {
                              return NurBaskentFeature.P.CILALI_KARATAS;
                           }

                           if ((var4 < 4 || var4 > 7) && (var4 < 13 || var4 > 16) || var2 > 2 && var3 > 2) {
                              return NurBaskentFeature.P.KARATAS_TUGLA;
                           }

                           return NurBaskentFeature.P.CAM;
                        }

                        return NurBaskentFeature.P.CILALI_KARATAS;
                     }

                     if (var4 != 10 && var4 != var10) {
                        if (var4 == 1) {
                           if (var2 <= 1 && var1 >= -var9 + 1 && var1 <= var9 - 2) {
                              return NurBaskentFeature.P.KIRMIZI_HALI;
                           }

                           if (var1 == var9 - 2 && var2 <= 1) {
                              return NurBaskentFeature.P.AGLAYAN_OBSIDYEN;
                           }

                           if (var1 == var9 - 3 && var0 == 0) {
                              return NurBaskentFeature.P.BUYU_MASASI;
                           }

                           if (var1 == var9 - 2 && (var2 == 3 || var2 == 4)) {
                              return NurBaskentFeature.P.SANDIK;
                           }

                           if (var2 == var9 - 1 && var3 <= var9 - 3 && var3 % 2 == 0) {
                              return NurBaskentFeature.P.KITAP;
                           }

                           if (var2 == var9 - 2 && var1 == 0) {
                              return NurBaskentFeature.P.ORS;
                           }
                        }

                        if (var4 == 2 && var2 == var9 - 1 && var3 <= var9 - 3 && var3 % 2 == 0) {
                           return NurBaskentFeature.P.KITAP;
                        }

                        if (var4 != 9 && var4 != var10 - 1 || (var2 != 3 || var3 != 3) && (var2 != 0 || var3 != 0)) {
                           if (var2 == var9 - 3 && var3 == var9 - 3) {
                              return var4 % 5 == 0 ? NurBaskentFeature.P.AGLAYAN_OBSIDYEN : NurBaskentFeature.P.CILALI_KARATAS;
                           }

                           return NurBaskentFeature.P.HAVA;
                        }

                        return NurBaskentFeature.P.FENER;
                     }

                     if (var2 <= 2 && var3 <= 2 && var4 == 10) {
                        return NurBaskentFeature.P.HAVA;
                     }

                     return (var0 + var1 & 1) == 0 ? NurBaskentFeature.P.CILALI_KARATAS : NurBaskentFeature.P.KARATAS_TUGLA;
                  }

                  int var12 = var4 - var10;
                  int var13 = var9 - (var12 + 1) / 2;
                  if (var13 >= 0 && var6 <= var13) {
                     if (var13 == 0) {
                        return var12 % 2 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.AGLAYAN_OBSIDYEN;
                     }

                     if (var6 >= var13 - 1) {
                        return (var12 + var2 + var3) % 3 == 0 ? NurBaskentFeature.P.AGLAYAN_OBSIDYEN : NurBaskentFeature.P.KARATAS_TUGLA;
                     }

                     return NurBaskentFeature.P.HAVA;
                  }
               }

               return null;
            }
         }
      }
   }

   private static void zifiriYerlesimKolonu(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      byte var8 = 71;

      for(int var9 = 63; var9 <= 70; ++var9) {
         set(var0, var1, var2, var9, var3, NurBaskentFeature.P.KARATAS);
      }

      int var21 = Math.abs(var5);
      int var10 = Math.abs(var6);
      int var11 = Math.max(var21, var10);
      if (var11 > var7) {
         set(var0, var1, var2, var8, var3, NurBaskentFeature.P.CILALI_KARATAS);
      } else {
         if (var11 >= var7 - 3) {
            set(var0, var1, var2, var8, var3, NurBaskentFeature.P.KARATAS_TUGLA);
            if (var21 >= var7 - 5 && var10 >= var7 - 5) {
               boolean var23 = var21 == var7 || var21 == var7 - 5 || var10 == var7 || var10 == var7 - 5;

               for(int var28 = 1; var28 <= 18; ++var28) {
                  if (var28 <= 16) {
                     if (!var23) {
                        if (var28 != 9 && var28 != 16) {
                           if (var21 == var7 - 2 && var10 == var7 - 2 && var28 == 15) {
                              set(var0, var1, var2, var8 + var28, var3, NurBaskentFeature.P.FENER);
                           }
                        } else {
                           set(var0, var1, var2, var8 + var28, var3, NurBaskentFeature.P.CILALI_KARATAS);
                        }
                     } else if (var28 > 4 || (var21 != var7 - 5 || var10 != var7 - 2) && (var10 != var7 - 5 || var21 != var7 - 2)) {
                        if (var28 != 9 && var28 != 16) {
                           if (var28 < 5 || var28 > 7 || var21 != var7 - 2 && var10 != var7 - 2) {
                              set(var0, var1, var2, var8 + var28, var3, NurBaskentFeature.P.KARATAS_TUGLA);
                           } else {
                              set(var0, var1, var2, var8 + var28, var3, NurBaskentFeature.P.AGLAYAN_OBSIDYEN);
                           }
                        } else {
                           set(var0, var1, var2, var8 + var28, var3, NurBaskentFeature.P.CILALI_KARATAS);
                        }
                     }
                  } else if (var28 == 17 && var23 && (var2 + var3 & 1) == 0) {
                     set(var0, var1, var2, var8 + var28, var3, NurBaskentFeature.P.CILALI_KARATAS);
                  } else if (var28 == 18 && var21 == var7 - 2 && var10 == var7 - 2) {
                     set(var0, var1, var2, var8 + var28, var3, NurBaskentFeature.P.FENER);
                  }
               }

               return;
            }

            int var12 = var21 >= var7 - 3 ? var10 : var21;
            if (var12 <= 4) {
               for(int var27 = 1; var27 <= 15; ++var27) {
                  if (var12 >= 3) {
                     if (var27 == 15) {
                        set(var0, var1, var2, var8 + var27, var3, NurBaskentFeature.P.AGLAYAN_OBSIDYEN);
                     } else {
                        set(var0, var1, var2, var8 + var27, var3, var27 % 4 == 0 ? NurBaskentFeature.P.CILALI_KARATAS : NurBaskentFeature.P.KARATAS_TUGLA);
                     }
                  } else {
                     int var32 = 6 + (2 - var12);
                     if (var27 >= var32) {
                        if (var27 == var32) {
                           set(var0, var1, var2, var8 + var27, var3, NurBaskentFeature.P.AGLAYAN_OBSIDYEN);
                        } else if (var27 <= 13) {
                           set(var0, var1, var2, var8 + var27, var3, var27 == 13 ? NurBaskentFeature.P.CILALI_KARATAS : NurBaskentFeature.P.KARATAS_TUGLA);
                        } else if (var27 == 14 && (var2 + var3 & 1) == 0) {
                           set(var0, var1, var2, var8 + var27, var3, NurBaskentFeature.P.CILALI_KARATAS);
                        }
                     }
                  }
               }

               return;
            }

            if (var11 >= var7 - 2) {
               boolean var26 = var11 == var7 || var11 == var7 - 2;

               for(int var31 = 1; var31 <= 11; ++var31) {
                  if (var31 <= 9) {
                     if (!var26) {
                        if (var31 == 9) {
                           set(var0, var1, var2, var8 + var31, var3, NurBaskentFeature.P.CILALI_KARATAS);
                        } else {
                           set(var0, var1, var2, var8 + var31, var3, NurBaskentFeature.P.KARATAS);
                        }
                     } else if (var31 == 9) {
                        set(var0, var1, var2, var8 + var31, var3, NurBaskentFeature.P.CILALI_KARATAS);
                     } else if (var12 % 6 == 0) {
                        set(var0, var1, var2, var8 + var31, var3, NurBaskentFeature.P.CILALI_KARATAS);
                     } else {
                        set(var0, var1, var2, var8 + var31, var3, NurBaskentFeature.P.KARATAS_TUGLA);
                     }
                  } else if (var31 == 10 && var26 && (var2 + var3 & 1) == 0) {
                     set(var0, var1, var2, var8 + var31, var3, NurBaskentFeature.P.CILALI_KARATAS);
                  } else if (var31 == 11 && var26 && var12 % 12 == 0) {
                     set(var0, var1, var2, var8 + var31, var3, NurBaskentFeature.P.FENER);
                  }
               }

               return;
            }
         }

         int var22 = var4 == 0 ? 16 : 11;
         if (var11 <= var22) {
            set(var0, var1, var2, var8, var3, (var2 + var3 & 1) == 0 ? NurBaskentFeature.P.CILALI_KARATAS : NurBaskentFeature.P.KARATAS_TUGLA);
            int var25 = var4 == 0 ? 34 : 24;

            for(int var30 = 1; var30 <= var25; ++var30) {
               BlockState var34 = zifiriMerkezKalesi(var5, var6, var21, var10, var30, var22);
               if (var34 != null) {
                  set(var0, var1, var2, var8 + var30, var3, var34);
               }
            }

         } else if (var21 <= 3 || var10 <= 3 || var11 >= var22 + 1 && var11 <= var22 + 4) {
            if (var21 != 0 && var10 != 0) {
               set(var0, var1, var2, var8, var3, (var2 + var3 & 1) == 0 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.TAS_TUGLA);
            } else {
               set(var0, var1, var2, var8, var3, (var21 + var10) % 5 == 0 ? NurBaskentFeature.P.AGLAYAN_OBSIDYEN : NurBaskentFeature.P.CILALI_KARATAS);
            }

            if (var21 == 3 && var10 > var22 + 4 && var10 < var7 - 5 && var10 % 12 == 0 || var10 == 3 && var21 > var22 + 4 && var21 < var7 - 5 && var21 % 12 == 0) {
               for(int var24 = 1; var24 <= 4; ++var24) {
                  set(var0, var1, var2, var8 + var24, var3, var24 == 4 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.KARATAS_TUGLA);
               }
            }

         } else {
            int[][] var13 = var4 == 0 ? new int[][]{{-34, -34}, {34, -34}, {-34, 34}, {34, 34}, {-36, -14}, {36, -14}, {-36, 14}, {36, 14}, {-14, -36}, {14, -36}, {-14, 36}, {14, 36}} : new int[][]{{-25, -25}, {25, -25}, {-25, 25}, {25, 25}, {-26, -11}, {26, -11}, {-11, 26}, {11, -26}};

            for(int var14 = 0; var14 < var13.length; ++var14) {
               int var15 = var5 - var13[var14][0];
               int var16 = var6 - var13[var14][1];
               if (var13[var14][1] < 0) {
                  var16 = -var16;
               }

               int var17 = Math.abs(var15);
               int var18 = Math.abs(var16);
               if (var17 <= 5 && var18 <= 5) {
                  set(var0, var1, var2, var8, var3, NurBaskentFeature.P.KARATAS_TUGLA);

                  for(int var19 = 1; var19 <= 12; ++var19) {
                     BlockState var20 = zifiriKoyEvi(var15, var16, var17, var18, var19);
                     if (var20 != null) {
                        set(var0, var1, var2, var8 + var19, var3, var20);
                     }
                  }

                  return;
               }
            }

            int var29 = var4 == 0 ? 23 : 17;
            if (Math.abs(var21 - var29) <= 2 && Math.abs(var10 - var29) <= 2) {
               int var33 = var21 - var29;
               int var35 = var10 - var29;
               int var36 = Math.max(Math.abs(var33), Math.abs(var35));
               set(var0, var1, var2, var8, var3, var36 == 0 ? NurBaskentFeature.P.SU : NurBaskentFeature.P.KARATAS_TUGLA);
               if (var36 == 1) {
                  set(var0, var1, var2, var8 + 1, var3, NurBaskentFeature.P.KARATAS_TUGLA);
                  if (Math.abs(var33) == 1 && Math.abs(var35) == 1) {
                     set(var0, var1, var2, var8 + 2, var3, NurBaskentFeature.P.LADIN_GOVDE);
                     set(var0, var1, var2, var8 + 3, var3, NurBaskentFeature.P.LADIN_GOVDE);
                  }
               }

               if (var36 <= 1) {
                  set(var0, var1, var2, var8 + 4, var3, var36 == 0 ? NurBaskentFeature.P.FENER : NurBaskentFeature.P.KARATAS_TUGLA);
               }

            } else {
               set(var0, var1, var2, var8, var3, (var2 * 7 + var3 * 13) % 4 == 0 ? NurBaskentFeature.P.YOSUN : ((var2 + var3 & 1) == 0 ? NurBaskentFeature.P.CILALI_KARATAS : NurBaskentFeature.P.KARATAS_TUGLA));
            }
         }
      }
   }

   private static void zifiriBataklikKolonu(WorldGenLevel var0, BlockPos.MutableBlockPos var1, int var2, int var3, boolean var4) {
      for(int var5 = 71; var5 <= 135; ++var5) {
         var0.setBlock(var1.set(var2, var5, var3), NurBaskentFeature.P.HAVA, 2);
      }

      for(int var19 = 63; var19 <= 66; ++var19) {
         set(var0, var1, var2, var19, var3, NurBaskentFeature.P.KARATAS);
      }

      for(int var20 = 0; var20 < ZIFIRI_YERLESIMLER.length; ++var20) {
         int var6 = ZIFIRI_YERLESIMLER[var20][0];
         int var7 = ZIFIRI_YERLESIMLER[var20][1];
         int var8 = ZIFIRI_YERLESIMLER[var20][2];
         int var9 = var2 - var6;
         int var10 = var3 - var7;
         if (Math.abs(var9) <= var8 + 1 && Math.abs(var10) <= var8 + 1) {
            zifiriYerlesimKolonu(var0, var1, var2, var3, var20, var9, var10, var8);
            return;
         }
      }

      boolean var21 = bataklikSaglamZeminMi(var2, var3);
      if (var21) {
         for(int var22 = 67; var22 <= 70; ++var22) {
            set(var0, var1, var2, var22, var3, NurBaskentFeature.P.KARATAS);
         }

         BlockState var23 = (var2 + var3 & 1) == 0 ? NurBaskentFeature.P.CILALI_KARATAS : NurBaskentFeature.P.KARATAS_TUGLA;
         set(var0, var1, var2, 71, var3, var23);
         if (var3 == -7 && Math.abs(var2) <= 3) {
            for(int var26 = 1; var26 <= 6; ++var26) {
               if (Math.abs(var2) == 3 || var26 == 6) {
                  set(var0, var1, var2, 71 + var26, var3, var26 != 3 && var26 != 6 ? NurBaskentFeature.P.KARATAS_TUGLA : NurBaskentFeature.P.AGLAYAN_OBSIDYEN);
               }
            }
         }
      } else {
         for(int var24 = 66; var24 <= 70; ++var24) {
            set(var0, var1, var2, var24, var3, NurBaskentFeature.P.CAMUR);
         }
      }

      int var25 = Math.floorDiv(var2, 10);
      int var27 = Math.floorDiv(var3, 10);
      int var28 = hash(var25 * 71 + 13, var27 * 89 + 29);
      int var29 = var25 * 10 + 3 + var28 % 4;
      int var30 = var27 * 10 + 3 + (var28 >>> 4) % 4;
      if (var29 * var29 + var30 * var30 > 144) {
         if (Math.abs(var29) > 4 && Math.abs(var30) > 4) {
            if (!(Math.abs((double)var29 - Math.sin((double)var30 / (double)12.0F) * 3.2) <= 2.6) && !(Math.abs((double)var30 - Math.cos((double)var29 / (double)12.0F) * 3.2) <= 2.6)) {
               for(int var11 = 0; var11 < ZIFIRI_YERLESIMLER.length; ++var11) {
                  int var12 = ZIFIRI_YERLESIMLER[var11][2];
                  if (Math.abs(var29 - ZIFIRI_YERLESIMLER[var11][0]) <= var12 + 6 && Math.abs(var30 - ZIFIRI_YERLESIMLER[var11][1]) <= var12 + 6) {
                     return;
                  }
               }

               int var31 = var2 - var29;
               int var32 = var3 - var30;
               int var13 = Math.abs(var31);
               int var14 = Math.abs(var32);
               if (var13 <= 3 && var14 <= 3) {
                  int var15 = 12 + var28 % 5;
                  if (var13 == 0 && var14 == 0) {
                     for(int var33 = 1; var33 <= var15; ++var33) {
                        set(var0, var1, var2, 70 + var33, var3, NurBaskentFeature.P.KARANLIK_GOVDE);
                     }
                  } else if (!var21 && var13 + var14 == 1 && (var28 % 2 == 0 || var13 == 1)) {
                     for(int var16 = 1; var16 <= 2; ++var16) {
                        set(var0, var1, var2, 70 + var16, var3, NurBaskentFeature.P.KARANLIK_GOVDE);
                     }
                  }

                  double var34 = Math.sqrt((double)(var31 * var31 + var32 * var32));
                  if (var34 <= 3.6) {
                     for(int var18 = var15 - 3; var18 <= var15 + 2; ++var18) {
                        if (var18 != var15 - 1 || var13 > 2 || var14 > 2 || var13 != var14 && var13 != 0 && var14 != 0) {
                           if (var34 <= 3.2 - (double)(var18 - (var15 - 3)) * 0.42) {
                              set(var0, var1, var2, 70 + var18, var3, NurBaskentFeature.P.KARANLIK_YAPRAK);
                           }
                        } else {
                           set(var0, var1, var2, 70 + var18, var3, NurBaskentFeature.P.KARANLIK_GOVDE);
                        }
                     }
                  }
               }

            }
         }
      }
   }

   private static double dagYukseklikOrani(int var0, int var1) {
      double var2 = (double)1.0F - Math.abs((double)2.0F * vn((double)var0 / (double)85.0F + (double)11.0F, (double)var1 / (double)85.0F + (double)23.0F) - (double)1.0F);
      double var4 = (double)1.0F - Math.abs((double)2.0F * vn((double)var0 / (double)42.0F + (double)37.0F, (double)var1 / (double)42.0F + (double)19.0F) - (double)1.0F);
      double var6 = vn((double)var0 / (double)110.0F + (double)3.0F, (double)var1 / (double)110.0F + (double)7.0F);
      return Math.min((double)1.0F, var2 * 0.55 + var4 * 0.3 + var6 * (double)0.25F);
   }

   private static BlockState doguKulubesi(int var0, int var1, int var2, int var3, int var4) {
      byte var5 = 4;
      byte var6 = 4;
      if (var4 > 5) {
         int var9 = var4 - 6;
         int var8 = var5 + 1 - var9;
         if (var0 == 3 && var1 == -3 && var4 <= 10) {
            return NurBaskentFeature.P.KARATAS_TUGLA;
         } else if (var8 >= 0 && var3 <= var6 + 1 && var2 <= var8) {
            if (var2 < var8 - 1 && var8 != 0) {
               return var3 >= var6 ? NurBaskentFeature.P.MESE_TAHTA : NurBaskentFeature.P.HAVA;
            } else {
               return NurBaskentFeature.P.LADIN_TAHTA;
            }
         } else {
            return null;
         }
      } else if (var2 <= var5 && var3 <= var6) {
         boolean var7 = var2 == var5 || var3 == var6;
         if (var7) {
            if (var2 == var5 && var3 == var6) {
               return NurBaskentFeature.P.LADIN_GOVDE;
            } else if (var1 == var6 && var2 <= 1 && var4 <= 3) {
               return NurBaskentFeature.P.HAVA;
            } else if (var4 < 2 || var4 > 3 || var2 != 0 && var3 != 0) {
               return var4 == 1 ? NurBaskentFeature.P.TAS_TUGLA : NurBaskentFeature.P.LADIN_TAHTA;
            } else {
               return NurBaskentFeature.P.CAM_ALTIN;
            }
         } else if (var4 == 5) {
            return NurBaskentFeature.P.MESE_TAHTA;
         } else {
            if (var4 == 1) {
               if (var1 == -var6 + 1 && (var0 == -3 || var0 == -2)) {
                  return var0 == -2 ? NurBaskentFeature.P.BEYAZ_YUN : NurBaskentFeature.P.KIRMIZI_YUN;
               }

               if (var1 == -var6 + 1 && var0 == 3) {
                  return NurBaskentFeature.P.SANDIK;
               }

               if (var1 == -var6 + 1 && var0 == 2) {
                  return NurBaskentFeature.P.VARIL;
               }

               if (var0 == -3 && var1 == 1) {
                  return NurBaskentFeature.P.CALISMA_MASASI;
               }

               if (var0 == 3 && var1 == 1) {
                  return NurBaskentFeature.P.ORS;
               }

               if (var2 <= 1 && var3 <= 1) {
                  return NurBaskentFeature.P.KIRMIZI_HALI;
               }
            }

            if (var4 == 4 && var2 == 0 && var3 == 0) {
               return NurBaskentFeature.P.FENER;
            } else {
               return NurBaskentFeature.P.HAVA;
            }
         }
      } else {
         return null;
      }
   }

   private static double smooth(double var0, double var2, double var4) {
      double var6 = Math.max((double)0.0F, Math.min((double)1.0F, (var4 - var0) / (var2 - var0)));
      return var6 * var6 * ((double)3.0F - (double)2.0F * var6);
   }

   private static int hash(int var0, int var1) {
      int var2 = var0 * 374761393 + var1 * 668265263 + 1540483477;
      var2 = (var2 ^ var2 >>> 13) * 1274126177;
      var2 ^= var2 >>> 16;
      return var2 & Integer.MAX_VALUE;
   }

   private static double vn(double var0, double var2) {
      int var4 = (int)Math.floor(var0);
      int var5 = (int)Math.floor(var2);
      double var6 = var0 - (double)var4;
      double var8 = var2 - (double)var5;
      var6 = var6 * var6 * ((double)3.0F - (double)2.0F * var6);
      var8 = var8 * var8 * ((double)3.0F - (double)2.0F * var8);
      double var10 = (double)(hash(var4, var5) & '\uffff') / (double)65535.0F;
      double var12 = (double)(hash(var4 + 1, var5) & '\uffff') / (double)65535.0F;
      double var14 = (double)(hash(var4, var5 + 1) & '\uffff') / (double)65535.0F;
      double var16 = (double)(hash(var4 + 1, var5 + 1) & '\uffff') / (double)65535.0F;
      return (var10 * ((double)1.0F - var6) + var12 * var6) * ((double)1.0F - var8) + (var14 * ((double)1.0F - var6) + var16 * var6) * var8;
   }

   static {
      for(int var0 = 0; var0 < 8; ++var0) {
         double var1 = ((double)var0 + (double)0.5F) * (Math.PI / 4D);
         PX[var0] = (int)Math.round((double)132.0F * Math.cos(var1));
         PZ[var0] = (int)Math.round((double)132.0F * Math.sin(var1));
      }

      for(int var26 = 0; var26 < 18; ++var26) {
         double var28 = (double)var26 * 2.399963229728653;
         double var3 = (double)(385 + var26 % 5 * 28 + hash(var26, 5) % 16);
         ACX[var26] = (int)Math.round(Math.cos(var28) * var3);
         ACZ[var26] = (int)Math.round(Math.sin(var28) * var3);
         ATY[var26] = 48 + hash(var26, 9) % 78;
         AR[var26] = 16 + hash(var26, 3) % 12;
         AD[var26] = 14 + hash(var26, 4) % 16;
      }

      int[][] var27 = new int[][]{{-1680, 0, 168, 44, 28, 2}, {-1880, -150, 184, 42, 28, 2}, {-1860, 160, 162, 42, 26, 2}, {-1660, -240, 154, 40, 25, 2}, {-1660, 240, 160, 40, 25, 2}, {-1220, -1220, 174, 44, 26, 2}};
      int var29 = 0;

      for(int var2 = 0; var2 < var27.length; ++var2) {
         SCX[var29] = var27[var2][0];
         SCZ[var29] = var27[var2][1];
         SY[var29] = var27[var2][2];
         SR[var29] = var27[var2][3];
         SD[var29] = var27[var2][4];
         STIP[var29] = var27[var2][5];
         ++var29;
      }

      int var30 = 0;

      while(var29 < 56 && var30 < 200000) {
         ++var30;
         int var4 = hash(var29 * 41 + var30, var30 * 17 + 9);
         int var5 = hash(var30 * 59 + 13, var29 * 23 + 5);
         int var6 = hash(var30 * 73 + 29, var29 * 31 + 7);
         int var7;
         int var8;
         int var9;
         byte var10;
         if (var29 < 24) {
            var7 = 32 + var4 % 12;
            var8 = 20 + var5 % 10;
            var9 = 144 + var6 % 58;
            var10 = 2;
         } else if (var29 < 40) {
            var7 = 19 + var4 % 6;
            var8 = 14 + var5 % 6;
            var9 = 128 + var6 % 68;
            var10 = 1;
         } else {
            var7 = 13 + var4 % 5;
            var8 = 10 + var5 % 5;
            var9 = 120 + var6 % 76;
            var10 = 0;
         }

         double var11 = (double)114.0F + (double)(var4 % 1000) * 0.084;
         double var13 = Math.toRadians(var11);
         double var15 = (double)1560.0F + (double)(var5 % 1000) * 1.22;
         int var17 = (int)Math.round(var15 * Math.cos(var13));
         int var18 = (int)Math.round(-var15 * Math.sin(var13));
         if ((long)var17 * (long)var17 + (long)var18 * (long)var18 >= 2371600L) {
            int var19 = var30 < 80000 ? 68 : (var30 < 140000 ? 54 : 42);
            boolean var20 = true;

            for(int var21 = 0; var21 < var29; ++var21) {
               double var22 = (double)(var17 - SCX[var21]);
               double var24 = (double)(var18 - SCZ[var21]);
               if (Math.sqrt(var22 * var22 + var24 * var24) < (double)(var7 + SR[var21] + var19)) {
                  var20 = false;
                  break;
               }
            }

            if (var20) {
               SCX[var29] = var17;
               SCZ[var29] = var18;
               SY[var29] = var9;
               SR[var29] = var7;
               SD[var29] = var8;
               STIP[var29] = var10;
               ++var29;
            }
         }
      }

      KART_SUNAKLARI = new int[][]{{-36, 0}, {-320, 0}, {-720, 0}, {-1150, 0}, {-1540, 0}};
      DOGU_KOYLERI = new int[][]{{1620, 0}, {1760, -150}, {1760, 150}, {1920, -40}, {2080, 140}, {2100, -160}, {2280, 0}, {2440, -100}};
      ZIFIRI_YERLESIMLER = new int[][]{{0, 160, 56}, {-160, 0, 44}, {160, 0, 44}};
   }

   static final class Hc {
      boolean bina;
      int tip;
      int half;
      int H;
      int ax;
      int az;
      int lx;
      int lz;
      int h;
      double ccx;
      double ccz;
   }

   static final class P {
      static final BlockState TUGLA;
      static final BlockState SUTUN;
      static final BlockState FENER;
      static final BlockState BULUT;
      static final BlockState ALTIN;
      static final BlockState KUVARS;
      static final BlockState OYMA;
      static final BlockState KTUGLA;
      static final BlockState KALSIT;
      static final BlockState CAM;
      static final BlockState CAM_ALTIN;
      static final BlockState CAM_GOK;
      static final BlockState DFENER;
      static final BlockState AMETIST;
      static final BlockState SU;
      static final BlockState CIM;
      static final BlockState TOPRAK;
      static final BlockState GOVDE;
      static final BlockState YAPRAK;
      static final BlockState LADIN_GOVDE;
      static final BlockState HUS_GOVDE;
      static final BlockState HUS_TAHTA;
      static final BlockState MESE_YAPRAK;
      static final BlockState ACELYA_YAPRAK;
      static final BlockState CICEKLI_YAPRAK;
      static final BlockState YOSUN;
      static final BlockState MANTAR_ISIK;
      static final BlockState ZUMRUT;
      static final BlockState PRIZMARIN;
      static final BlockState LAPIS;
      static final Block BULUT_BLOK_REF;
      static final BlockState KARANLIK_YAPRAK;
      static final BlockState KARANLIK_GOVDE;
      static final BlockState HAVA;
      static final BlockState KITAP;
      static final BlockState BUYU_MASASI;
      static final BlockState KURSU;
      static final BlockState SANDIK;
      static final BlockState VARIL;
      static final BlockState CALISMA_MASASI;
      static final BlockState ORS;
      static final BlockState BEYAZ_YUN;
      static final BlockState KIRMIZI_YUN;
      static final BlockState YESIL_YUN;
      static final BlockState KIRMIZI_HALI;
      static final BlockState SARI_HALI;
      static final BlockState YESIL_HALI;
      static final BlockState MESE_TAHTA;
      static final BlockState LADIN_TAHTA;
      static final BlockState TAS;
      static final BlockState TAS_TUGLA;
      static final BlockState KARATAS;
      static final BlockState KARATAS_TUGLA;
      static final BlockState CILALI_KARATAS;
      static final BlockState CAMUR;
      static final BlockState AGLAYAN_OBSIDYEN;
      static final BlockState KAR_BLOGU;
      static final BlockState[] CICEK;

      static {
         TUGLA = ((Block)CennetMod.BULUT_TUGLASI.get()).defaultBlockState();
         SUTUN = ((Block)CennetMod.KUTSAL_SUTUN.get()).defaultBlockState();
         FENER = ((Block)CennetMod.NUR_FENERI.get()).defaultBlockState();
         BULUT = ((Block)CennetMod.BULUT_BLOGU.get()).defaultBlockState();
         ALTIN = Blocks.GOLD_BLOCK.defaultBlockState();
         KUVARS = Blocks.SMOOTH_QUARTZ.defaultBlockState();
         OYMA = Blocks.CHISELED_QUARTZ_BLOCK.defaultBlockState();
         KTUGLA = Blocks.QUARTZ_BRICKS.defaultBlockState();
         KALSIT = Blocks.CALCITE.defaultBlockState();
         CAM = Blocks.WHITE_STAINED_GLASS.defaultBlockState();
         CAM_ALTIN = Blocks.YELLOW_STAINED_GLASS.defaultBlockState();
         CAM_GOK = Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState();
         DFENER = Blocks.SEA_LANTERN.defaultBlockState();
         AMETIST = Blocks.AMETHYST_BLOCK.defaultBlockState();
         SU = Blocks.WATER.defaultBlockState();
         CIM = Blocks.GRASS_BLOCK.defaultBlockState();
         TOPRAK = Blocks.DIRT.defaultBlockState();
         GOVDE = Blocks.CHERRY_LOG.defaultBlockState();
         YAPRAK = (BlockState)Blocks.CHERRY_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, Boolean.TRUE);
         LADIN_GOVDE = Blocks.SPRUCE_LOG.defaultBlockState();
         HUS_GOVDE = Blocks.BIRCH_LOG.defaultBlockState();
         HUS_TAHTA = Blocks.BIRCH_PLANKS.defaultBlockState();
         MESE_YAPRAK = (BlockState)Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, Boolean.TRUE);
         ACELYA_YAPRAK = (BlockState)Blocks.AZALEA_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, Boolean.TRUE);
         CICEKLI_YAPRAK = (BlockState)Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, Boolean.TRUE);
         YOSUN = Blocks.MOSS_BLOCK.defaultBlockState();
         MANTAR_ISIK = Blocks.SHROOMLIGHT.defaultBlockState();
         ZUMRUT = Blocks.EMERALD_BLOCK.defaultBlockState();
         PRIZMARIN = Blocks.DARK_PRISMARINE.defaultBlockState();
         LAPIS = Blocks.LAPIS_BLOCK.defaultBlockState();
         BULUT_BLOK_REF = (Block)CennetMod.BULUT_BLOGU.get();
         KARANLIK_YAPRAK = ((Block)CennetMod.KARANLIK_YAPRAK.get()).defaultBlockState();
         KARANLIK_GOVDE = ((Block)CennetMod.KARANLIK_GOVDE.get()).defaultBlockState();
         HAVA = Blocks.AIR.defaultBlockState();
         KITAP = Blocks.BOOKSHELF.defaultBlockState();
         BUYU_MASASI = Blocks.ENCHANTING_TABLE.defaultBlockState();
         KURSU = Blocks.LECTERN.defaultBlockState();
         SANDIK = Blocks.CHEST.defaultBlockState();
         VARIL = Blocks.BARREL.defaultBlockState();
         CALISMA_MASASI = Blocks.CRAFTING_TABLE.defaultBlockState();
         ORS = Blocks.ANVIL.defaultBlockState();
         BEYAZ_YUN = Blocks.WHITE_WOOL.defaultBlockState();
         KIRMIZI_YUN = Blocks.RED_WOOL.defaultBlockState();
         YESIL_YUN = Blocks.GREEN_WOOL.defaultBlockState();
         KIRMIZI_HALI = Blocks.RED_CARPET.defaultBlockState();
         SARI_HALI = Blocks.YELLOW_CARPET.defaultBlockState();
         YESIL_HALI = Blocks.GREEN_CARPET.defaultBlockState();
         MESE_TAHTA = Blocks.OAK_PLANKS.defaultBlockState();
         LADIN_TAHTA = Blocks.SPRUCE_PLANKS.defaultBlockState();
         TAS = Blocks.STONE.defaultBlockState();
         TAS_TUGLA = Blocks.STONE_BRICKS.defaultBlockState();
         KARATAS = Blocks.DEEPSLATE.defaultBlockState();
         KARATAS_TUGLA = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
         CILALI_KARATAS = Blocks.POLISHED_DEEPSLATE.defaultBlockState();
         CAMUR = Blocks.MUD.defaultBlockState();
         AGLAYAN_OBSIDYEN = Blocks.CRYING_OBSIDIAN.defaultBlockState();
         KAR_BLOGU = Blocks.SNOW_BLOCK.defaultBlockState();
         CICEK = new BlockState[]{Blocks.AZURE_BLUET.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState(), Blocks.LILY_OF_THE_VALLEY.defaultBlockState(), Blocks.ALLIUM.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState()};
      }
   }
}
