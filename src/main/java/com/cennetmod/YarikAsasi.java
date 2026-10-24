package com.cennetmod;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Yarık Asası - sinematik ışınlanma (13 sn / 260 tick).
 *  0-50   : şarj, yerden yükselen parçacıklar
 *  60     : "Yıldızlar kabul etti"
 *  55-110 : gökyüzünde yüzlerce yıldız tek tek belirir
 *  90-160 : gökyüzü yavaş yavaş çatlar
 *  100+   : phantomlar yarığın etrafında döner
 *  135    : tüm yıldızlar aynı anda parlar
 *  140-236: yıldızlar sana ışın yollar
 *  130+   : havaya yükselirsin (kamera titrer, ekran morlaşır)
 *  236-260: beyaz flaş, ışınlanma
 */
public class YarikAsasi extends Item {
    public static final ResourceKey<Level> CENNET = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(CennetMod.MODID, "cennet_karanligi"));
    public static final String TAG = "yarik_phantom";
    public static final int SURE = 260;
    private static final int YILDIZ = 220;
    private static final double MERKEZ_YUKSEKLIK = 38;

    public YarikAsasi(Properties p) { super(p); }

    @Override public int getUseDuration(ItemStack s, LivingEntity e) { return SURE; }
    @Override public UseAnim getUseAnimation(ItemStack s) { return UseAnim.BOW; }

    private boolean geceYarisi(Level l) {
        long t = l.getDayTime() % 24000;
        return t >= 17500 && t <= 18500;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);

        boolean cennette = level.dimension().equals(CENNET);
        if (!cennette && !geceYarisi(level)) {
            if (!level.isClientSide) {
                player.sendSystemMessage(Component.literal("Yıldızlar sessiz...").withStyle(ChatFormatting.GRAY));
                level.playSound(null, player.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1f, 1f);
            }
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        if (!level.isClientSide) {
            CompoundTag d = player.getPersistentData();
            d.putDouble("yarik_x", player.getX());
            d.putDouble("yarik_y", player.getY());
            d.putDouble("yarik_z", player.getZ());
            d.putLong("yarik_seed", level.random.nextLong());
            player.sendSystemMessage(Component.literal("Yıldızlara seslendin...").withStyle(ChatFormatting.WHITE));
            ses(level, player, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.5f, 0.6f);
            ses(level, player, SoundEvents.BEACON_ACTIVATE, 1f, 0.5f);
        }
        return InteractionResultHolder.consume(stack);
    }

    private static void ses(Level l, Entity e, SoundEvent s, float vol, float pitch) {
        l.playSound(null, e.blockPosition(), s, SoundSource.PLAYERS, vol, pitch);
    }

    private static void p(ServerLevel sl, ServerPlayer sp, ParticleOptions t, double x, double y, double z,
                          int n, double dx, double dy, double dz, double speed) {
        sl.sendParticles(sp, t, true, x, y, z, n, dx, dy, dz, speed);
    }

    /** Gökyüzü kubbesindeki sabit yıldız konumları. */
    private static double[][] yildizlar(long seed, double x, double y, double z) {
        Random r = new Random(seed);
        double[][] s = new double[YILDIZ][3];
        for (int i = 0; i < YILDIZ; i++) {
            double az = r.nextDouble() * Math.PI * 2;
            double el = Math.toRadians(25 + r.nextDouble() * 60);
            double rad = 22 + r.nextDouble() * 14;
            s[i][0] = x + Math.cos(az) * Math.cos(el) * rad;
            s[i][1] = y + 1.6 + Math.sin(el) * rad;
            s[i][2] = z + Math.sin(az) * Math.cos(el) * rad;
        }
        return s;
    }

    /** Çatlak: ana çizgi (iki yön) + dallar. Her noktanın 4. değeri "ne zaman belirir" (0-1). */
    private static List<List<double[]>> catlak(double cx, double cy, double cz, long seed) {
        Random r = new Random(seed ^ 0x5DEECE66DL);
        List<List<double[]>> lines = new ArrayList<>();
        double ang = r.nextDouble() * Math.PI;
        double dx = Math.cos(ang), dz = Math.sin(ang), px = -dz, pz = dx;
        for (int side = -1; side <= 1; side += 2) {
            List<double[]> main = new ArrayList<>();
            main.add(new double[]{cx, cy, cz, 0});
            double off = 0, wob = 0, up = 0;
            for (int i = 1; i <= 14; i++) {
                off += 1.1 + r.nextDouble() * 0.9;
                wob += (r.nextDouble() - 0.5) * 1.8;
                up += (r.nextDouble() - 0.5) * 1.8;
                main.add(new double[]{cx + dx * side * off + px * wob, cy + up, cz + dz * side * off + pz * wob, i / 14.0});
            }
            lines.add(main);
            for (int b = 3; b <= 11; b += 4) {
                double[] s = main.get(b);
                List<double[]> br = new ArrayList<>();
                br.add(s);
                double bx = s[0], by = s[1], bz = s[2];
                double bs = r.nextBoolean() ? 1 : -1;
                for (int k = 1; k <= 5; k++) {
                    bx += px * bs * (0.9 + r.nextDouble() * 0.6) + dx * side * 0.4;
                    by += (r.nextDouble() - 0.5) * 1.5;
                    bz += pz * bs * (0.9 + r.nextDouble() * 0.6) + dz * side * 0.4;
                    br.add(new double[]{bx, by, bz, s[3] + k * 0.03});
                }
                lines.add(br);
            }
        }
        return lines;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (level.isClientSide || !(entity instanceof ServerPlayer sp)) return;
        ServerLevel sl = (ServerLevel) level;
        int t = SURE - remaining;

        CompoundTag d = sp.getPersistentData();
        double x0 = d.contains("yarik_x") ? d.getDouble("yarik_x") : sp.getX();
        double y0 = d.contains("yarik_y") ? d.getDouble("yarik_y") : sp.getY();
        double z0 = d.contains("yarik_z") ? d.getDouble("yarik_z") : sp.getZ();
        long seed = d.contains("yarik_seed") ? d.getLong("yarik_seed") : 12345L;
        double cx = x0, cy = y0 + MERKEZ_YUKSEKLIK, cz = z0;
        RandomSource rnd = level.random;

        // ---- 1) Şarj: yerden yükselen parçacıklar
        if (t < 60 && t % 2 == 0) {
            p(sl, sp, ParticleTypes.ENCHANT, sp.getX(), sp.getY() + 1, sp.getZ(), 10, 0.6, 0.8, 0.6, 0.5);
            p(sl, sp, ParticleTypes.END_ROD, sp.getX(), sp.getY() + 0.2, sp.getZ(), 2, 0.4, 0.1, 0.4, 0.05);
        }
        // ---- 2) Yıldızlar kabul etti
        if (t == 60) {
            sp.sendSystemMessage(Component.literal("Yıldızlar kabul etti").withStyle(ChatFormatting.RED));
            ses(level, sp, SoundEvents.WARDEN_HEARTBEAT, 2f, 0.8f);
            ses(level, sp, SoundEvents.ELDER_GUARDIAN_CURSE, 0.8f, 0.7f);
        }

        // ---- 3) Yıldızlar tek tek belirir ve ışıldar
        double[][] st = yildizlar(seed, x0, y0, z0);
        if (t >= 55) {
            int gorunen = Math.min(YILDIZ, (t - 55) * 4);
            for (int i = 0; i < gorunen; i++) {
                if ((t + i) % 7 == 0) p(sl, sp, ParticleTypes.END_ROD, st[i][0], st[i][1], st[i][2], 1, 0, 0, 0, 0);
            }
            if (t < 115 && t % 5 == 0)
                ses(level, sp, SoundEvents.AMETHYST_BLOCK_CHIME, 1.2f, 0.7f + rnd.nextFloat() * 0.9f);
        }

        // ---- 4) Gökyüzü yavaş yavaş çatlar
        if (t >= 90) {
            double g = Math.min(1.0, (t - 90) / 70.0);
            if (t == 90) ses(level, sp, SoundEvents.LIGHTNING_BOLT_THUNDER, 0.6f, 0.5f);
            if (t <= 165 && t % 8 == 0) ses(level, sp, SoundEvents.GLASS_BREAK, 0.8f, 0.5f + (float) g * 0.4f);

            if (t % 4 == 0) {
                for (List<double[]> line : catlak(cx, cy, cz, seed)) {
                    for (int i = 0; i < line.size(); i++) {
                        double[] a = line.get(i);
                        if (a[3] > g) break;
                        p(sl, sp, ParticleTypes.END_ROD, a[0], a[1], a[2], 2, 0.12, 0.12, 0.12, 0.0);
                        if (i % 2 == 0) p(sl, sp, ParticleTypes.REVERSE_PORTAL, a[0], a[1], a[2], 3, 0.3, 0.3, 0.3, 0.05);
                        if (i + 1 < line.size() && line.get(i + 1)[3] <= g) {
                            double[] b = line.get(i + 1);
                            p(sl, sp, ParticleTypes.END_ROD, (a[0] + b[0]) / 2, (a[1] + b[1]) / 2, (a[2] + b[2]) / 2, 1, 0.05, 0.05, 0.05, 0.0);
                        }
                    }
                }
            }
            // Yarığın merkezi: büyüyen mor-siyah delik
            double rr = Math.min(3.5, 0.5 + (t - 100) * 0.04);
            if (t >= 100) {
                for (int i = 0; i < 6; i++) {
                    double a = t * 0.18 + i * Math.PI / 3;
                    p(sl, sp, ParticleTypes.REVERSE_PORTAL, cx + Math.cos(a) * rr, cy + Math.sin(t * 0.1 + i) * 0.6, cz + Math.sin(a) * rr, 2, 0.1, 0.1, 0.1, 0.0);
                }
                p(sl, sp, ParticleTypes.DRAGON_BREATH, cx, cy, cz, 6, 1.5, 0.5, 1.5, 0.02);
                p(sl, sp, ParticleTypes.END_ROD, cx, cy, cz, 3, 0.5, 0.3, 0.5, 0.02);
            }
        }

        // ---- 5) Phantomlar yarığın etrafında döner
        if (t >= 95) {
            if (t == 95) {
                for (int i = 0; i < 6; i++) {
                    Phantom ph = EntityType.PHANTOM.create(sl);
                    if (ph == null) continue;
                    ph.setNoAi(true);
                    ph.setInvulnerable(true);
                    ph.setSilent(true);
                    ph.addTag(TAG);
                    ph.setPos(cx, cy, cz);
                    sl.addFreshEntity(ph);
                }
            }
            List<Phantom> list = sl.getEntitiesOfClass(Phantom.class,
                    sp.getBoundingBox().inflate(120), ph -> ph.getTags().contains(TAG));
            for (int i = 0; i < list.size(); i++) {
                double a = t * 0.07 + i * (2 * Math.PI / Math.max(1, list.size()));
                double rad = 9 + Math.sin(t * 0.05 + i) * 2;
                list.get(i).moveTo(cx + Math.cos(a) * rad, cy + 2 * Math.sin(t * 0.08 + i), cz + Math.sin(a) * rad,
                        (float) Math.toDegrees(a) + 90f, 0);
            }
        }

        // ---- 6) Tüm yıldızlar aynı anda parlar
        if (t == 135) {
            for (int i = 0; i < YILDIZ; i++) p(sl, sp, ParticleTypes.FLASH, st[i][0], st[i][1], st[i][2], 1, 0, 0, 0, 0);
            ses(level, sp, SoundEvents.BEACON_POWER_SELECT, 2f, 0.7f);
            ses(level, sp, SoundEvents.END_PORTAL_SPAWN, 0.9f, 0.8f);
        }

        // ---- 7) Yıldızlardan sana ışınlar
        if (t >= 140 && t < SURE - 20) {
            double tx = sp.getX(), ty = sp.getY() + 1.0, tz = sp.getZ();
            int beams = Math.min(18, 6 + (t - 140) / 8);
            for (int k = 0; k < beams; k++) {
                int idx = (t * 7 + k * 23) % YILDIZ;
                double f0 = (t * 0.07 + k * 0.13) % 1.0;
                for (int j = 0; j < 4; j++) {
                    double f = Math.min(1.0, f0 + j * 0.04);
                    p(sl, sp, ParticleTypes.END_ROD,
                            st[idx][0] + (tx - st[idx][0]) * f, st[idx][1] + (ty - st[idx][1]) * f, st[idx][2] + (tz - st[idx][2]) * f,
                            1, 0, 0, 0, 0);
                }
                if (t % 3 == 0) p(sl, sp, ParticleTypes.END_ROD, st[idx][0], st[idx][1], st[idx][2], 2, 0.2, 0.2, 0.2, 0.0);
            }
            p(sl, sp, ParticleTypes.END_ROD, tx, ty, tz, 4, 0.5, 0.8, 0.5, 0.03);
            p(sl, sp, ParticleTypes.ENCHANT, tx, ty, tz, 8, 0.6, 1.0, 0.6, 0.6);
            if (t % 8 == 0) ses(level, sp, SoundEvents.BEACON_AMBIENT, 1.2f, 0.5f + (t - 140) / 120f);
            if (t % 25 == 0) ses(level, sp, SoundEvents.PORTAL_AMBIENT, 0.8f, 0.9f);
        }

        // ---- 8) Havaya yükselme (Levitation ile yumuşak hızlanma)
        if (t >= 130) {
            int amp = Math.min(8, (t - 130) / 14);
            sp.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 6, amp, false, false, false));
            sp.fallDistance = 0;
        }

        // ---- 9) Final: flaş
        if (t == SURE - 24) {
            ses(level, sp, SoundEvents.LIGHTNING_BOLT_THUNDER, 1.2f, 0.8f);
            ses(level, sp, SoundEvents.END_PORTAL_SPAWN, 1f, 1.2f);
        }
        if (t >= SURE - 24)
            p(sl, sp, ParticleTypes.FLASH, sp.getX(), sp.getY() + 1.5, sp.getZ(), 1, 0, 0, 0, 0);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer sp) {
            temizle((ServerLevel) level, sp);
            boolean cennetteydi = level.dimension().equals(CENNET);
            ServerLevel hedef = sp.server.getLevel(cennetteydi ? Level.OVERWORLD : CENNET);
            if (hedef != null) {
                BlockPos baz = cennetteydi ? hedef.getSharedSpawnPos() : BlockPos.ZERO;
                hedef.getChunk(baz.getX() >> 4, baz.getZ() >> 4);
                BlockPos yuzey = hedef.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, baz);
                sp.removeEffect(MobEffects.LEVITATION);
                sp.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0, false, false, false));
                sp.fallDistance = 0;
                sp.teleportTo(hedef, yuzey.getX() + 0.5, yuzey.getY() + 1, yuzey.getZ() + 0.5, sp.getYRot(), 0);
                hedef.playSound(null, sp.blockPosition(), SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 1f, 1f);
                hedef.playSound(null, sp.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1f, 0.6f);
                hedef.sendParticles(ParticleTypes.END_ROD, sp.getX(), sp.getY() + 1, sp.getZ(), 60, 0.8, 1.2, 0.8, 0.15);
                hedef.sendParticles(ParticleTypes.FLASH, sp.getX(), sp.getY() + 1.5, sp.getZ(), 3, 0.2, 0.2, 0.2, 0.0);
                if (!cennetteydi)
                    sp.sendSystemMessage(Component.literal("Cennet Karanlığı seni içine aldı.").withStyle(ChatFormatting.DARK_PURPLE));
            }
            stack.hurtAndBreak(1, sp, EquipmentSlot.MAINHAND);
            sp.getCooldowns().addCooldown(this, 1200);
        }
        return stack;
    }

    @Override
    public void releaseUsing(ItemStack s, Level l, LivingEntity e, int remaining) {
        if (!l.isClientSide && e instanceof ServerPlayer sp) {
            temizle((ServerLevel) l, sp);
            sp.removeEffect(MobEffects.LEVITATION);
            sp.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0, false, false, false));
        }
    }

    private void temizle(ServerLevel l, ServerPlayer sp) {
        l.getEntitiesOfClass(Phantom.class, sp.getBoundingBox().inflate(150), ph -> ph.getTags().contains(TAG))
                .forEach(Entity::discard);
    }
}
