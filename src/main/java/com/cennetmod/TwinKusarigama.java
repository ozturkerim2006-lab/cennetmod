package com.cennetmod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TwinKusarigama extends SwordItem {
    public static final String[] STIL_ADLARI = {
        "§5⛓ I. Çift Zincir Kasırgası",
        "§d🐍 II. Yılan Kancası",
        "§c⚔ III. Kızıl Biçici",
        "§b🌀 IV. Zincir Çelmesi",
        "§8🌌 V. Gölge Zincir Kafesi"
    };

    private static final String[] STIL_ACIKLAMA = {
        "4,5 Blok 360° Sürekli Zincir Biçişi",
        "12 Blok Zincir Kancası (Çek veya Atıl)",
        "İleri Atılım & 8 Ardışık Çapraz Orak",
        "Alçak Zincir Çelmesi & Havaya Savurma",
        "5 Blok Çevrede 6 Gölge Zinciri Mührü"
    };

    private static class KState {
        boolean wasSwinging = false;
        int lastSwingTime = 0;
        int combo = 0;
        long lastHitTick = 0;
        int spinTicks = 0;
        int flurryTicks = 0;
        int cageTicks = 0;
        Vec3 cageCenter = null;
        float mult = 1.0F;
    }

    private static final Map<UUID, KState> STATES = new ConcurrentHashMap<>();

    public TwinKusarigama() {
        this(new Item.Properties());
    }

    public TwinKusarigama(Item.Properties props) {
        super(Tiers.NETHERITE, props
            .attributes(SwordItem.createAttributes(Tiers.NETHERITE, 5, -1.7F))
            .durability(3600)
            .fireResistant()
            .rarity(Rarity.EPIC));
    }

    public static int getStyle(ItemStack stack) {
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        int s = cd != null ? cd.copyTag().getInt("kus_stil") : 0;
        return (s < 0 || s >= 5) ? 0 : s;
    }

    public static void setStyle(ItemStack stack, int next) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("kus_stil", ((next % 5) + 5) % 5);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static KState stateOf(Player p) {
        return STATES.computeIfAbsent(p.getUUID(), k -> new KState());
    }

    private void executeChainSweep(ServerLevel sl, Player player, ItemStack stack) {
        KState st = stateOf(player);
        long now = sl.getGameTime();
        if (now - st.lastHitTick > 65) st.combo = 0;
        st.combo = (st.combo % 4) + 1;
        st.lastHitTick = now;

        int style = getStyle(stack);
        boolean finisher = (st.combo == 4);
        double radius = finisher ? 4.8 : 4.2; // 4+ blok etrafa zincir vurus menzili!
        float dmg = finisher ? 12.5F : (7.0F + st.combo * 1.1F);

        sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 0.9F, 1.3F + st.combo * 0.1F);
        sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.1F, 1.15F);

        List<LivingEntity> targets = sl.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius, 2.2, radius),
            e -> e != player && e.isAlive() && e.distanceToSqr(player) <= radius * radius);

        for (LivingEntity e : targets) {
            e.invulnerableTime = 0;
            e.hurt(sl.damageSources().playerAttack(player), dmg);
            if (style == 0 && finisher) {
                Vec3 out = e.position().subtract(player.position()).normalize().scale(0.8);
                e.setDeltaMovement(out.x, 0.35, out.z);
                e.hurtMarked = true;
            } else if (style == 1) {
                Vec3 pull = player.position().subtract(e.position()).normalize().scale(0.45);
                e.setDeltaMovement(e.getDeltaMovement().add(pull.x, 0.12, pull.z));
                e.hurtMarked = true;
            } else if (style == 2) {
                e.addEffect(new MobEffectInstance(MobEffects.WITHER, 45, 0));
            } else if (style == 3 && finisher) {
                e.setDeltaMovement(0, 0.72, 0);
                e.hurtMarked = true;
            } else if (style == 4 && finisher) {
                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
                e.hurt(sl.damageSources().magic(), 5.0F);
            }
        }

        String bar = STIL_ADLARI[style] + "  §7|  §d⛓ Zincir Kombo: §f" + st.combo + "/4" + (st.combo >= 3 ? " §5★ ZİNCİR TAŞKINI HAZIR!" : "");
        player.displayClientMessage(Component.literal(bar), true);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            int next = (getStyle(stack) + 1) % 5;
            if (!level.isClientSide) {
                setStyle(stack, next);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 1.1F, 1.2F + next * 0.1F);
                player.displayClientMessage(Component.literal(STIL_ADLARI[next] + " §7— §f" + STIL_ACIKLAMA[next]), true);
                player.getCooldowns().addCooldown(this, 6);
            }
            return InteractionResultHolder.success(stack);
        }

        if (level.isClientSide) return InteractionResultHolder.success(stack);
        ServerLevel sl = (ServerLevel) level;
        KState st = stateOf(player);
        int style = getStyle(stack);
        boolean burst = (sl.getGameTime() - st.lastHitTick <= 65 && st.combo >= 3);
        st.mult = burst ? 1.45F : 1.0F;
        if (burst) {
            st.combo = 0;
            player.displayClientMessage(Component.literal("§d★ ZİNCİR TAŞKINI! " + STIL_ADLARI[style]), true);
        }

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();

        switch (style) {
            case 0 -> { // Cift Zincir Kasirgasi (4.5 blok 360 derece 2.5 sn)
                st.spinTicks = burst ? 60 : 48;
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 1.4F, 0.85F);
                player.getCooldowns().addCooldown(this, 65);
            }
            case 1 -> { // Yilan Kancasi (12 blok zincir cekme / atilma)
                AABB ray = new AABB(eye, eye.add(look.scale(12.5))).inflate(1.8);
                LivingEntity hit = sl.getEntitiesOfClass(LivingEntity.class, ray, e -> e != player && e.isAlive())
                    .stream().min(Comparator.comparingDouble(e -> e.distanceToSqr(player))).orElse(null);
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHAIN_STEP, SoundSource.PLAYERS, 1.5F, 1.4F);
                if (hit != null) {
                    hit.hurt(sl.damageSources().playerAttack(player), 11.0F * st.mult);
                    if (hit.getMaxHealth() >= 100.0F) {
                        // Dev boss: seni bossa firlatir
                        Vec3 zip = hit.position().subtract(player.position()).normalize().scale(2.1).add(0, 0.45, 0);
                        player.setDeltaMovement(zip);
                        player.hurtMarked = true;
                        player.fallDistance = 0;
                    } else {
                        // Normal dusman: onune ceker + capraz orak kesigi
                        Vec3 front = player.position().add(look.multiply(1, 0, 1).normalize().scale(2.2));
                        hit.teleportTo(front.x, player.getY() + 0.2, front.z);
                        hit.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2));
                        hit.invulnerableTime = 0;
                        hit.hurt(sl.damageSources().magic(), 8.0F * st.mult);
                    }
                    sl.playSound(null, hit.getX(), hit.getY(), hit.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.3F, 0.8F);
                }
                player.getCooldowns().addCooldown(this, 36);
            }
            case 2 -> { // Kizil Bicici (8 hizli capraz orak + agirlik carpmasi)
                Vec3 dash = new Vec3(look.x, 0.15, look.z).normalize().scale(1.65);
                player.setDeltaMovement(dash);
                player.hurtMarked = true;
                st.flurryTicks = 28;
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.3F, 1.6F);
                player.getCooldowns().addCooldown(this, 58);
            }
            case 3 -> { // Zincir Celmesi (4.5 blok alcak supurme + havaya kaldirma)
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 1.4F, 0.7F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(4.6, 2.0, 4.6), e -> e != player && e.isAlive())) {
                    e.hurt(sl.damageSources().playerAttack(player), 13.0F * st.mult);
                    e.setDeltaMovement(0, 0.92, 0);
                    e.hurtMarked = true;
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 1));
                }
                player.getCooldowns().addCooldown(this, 45);
            }
            case 4 -> { // Golge Zincir Kafesi
                st.cageCenter = player.position();
                st.cageTicks = 18;
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.3F, 0.65F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5.5), e -> e != player && e.isAlive())) {
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 5));
                }
                player.getCooldowns().addCooldown(this, 72);
            }
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player player) || !(level instanceof ServerLevel sl)) return;
        KState st = stateOf(player);

        // Sol tik 4 blok zincir saldirisi tespiti
        if (selected) {
            boolean swingStart = player.swinging && (!st.wasSwinging || player.swingTime < st.lastSwingTime);
            if (swingStart) {
                executeChainSweep(sl, player, stack);
            }
            st.wasSwinging = player.swinging;
            st.lastSwingTime = player.swingTime;
        } else {
            st.wasSwinging = false;
        }

        // 1. Cift Zincir Kasirgasi tick
        if (st.spinTicks > 0) {
            st.spinTicks--;
            if (st.spinTicks % 4 == 0) {
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHAIN_STEP, SoundSource.PLAYERS, 0.85F, 1.35F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(4.6, 2.2, 4.6), e -> e != player && e.isAlive())) {
                    if (e.distanceToSqr(player) <= 4.6 * 4.6) {
                        e.invulnerableTime = 0;
                        e.hurt(sl.damageSources().playerAttack(player), 5.2F * st.mult);
                        Vec3 tang = e.position().subtract(player.position());
                        Vec3 spinPull = new Vec3(-tang.z, 0.05, tang.x).normalize().scale(0.3);
                        e.setDeltaMovement(e.getDeltaMovement().scale(0.5).add(spinPull));
                        e.hurtMarked = true;
                    }
                }
            }
        }

        // 2. Kizil Bicici tick
        if (st.flurryTicks > 0) {
            st.flurryTicks--;
            if (st.flurryTicks % 3 == 0) {
                Vec3 center = player.position().add(player.getLookAngle().multiply(1, 0, 1).normalize().scale(2.6));
                sl.playSound(null, center.x, center.y, center.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.9F, 1.5F + sl.random.nextFloat() * 0.4F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate(3.4), e -> e != player && e.isAlive())) {
                    e.invulnerableTime = 0;
                    e.hurt(sl.damageSources().playerAttack(player), 4.2F * st.mult);
                }
            }
            if (st.flurryTicks == 0) {
                Vec3 slam = player.position().add(player.getLookAngle().multiply(1, 0, 1).normalize().scale(3.0));
                sl.playSound(null, slam.x, slam.y, slam.z, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.3F, 0.75F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(slam, slam).inflate(4.2), e -> e != player && e.isAlive())) {
                    e.invulnerableTime = 0;
                    e.hurt(sl.damageSources().magic(), 12.0F * st.mult);
                    e.knockback(1.1, player.getX() - e.getX(), player.getZ() - e.getZ());
                }
            }
        }

        // 3. Golge Zincir Kafesi patlamasi
        if (st.cageTicks > 0 && st.cageCenter != null) {
            st.cageTicks--;
            if (st.cageTicks == 0) {
                Vec3 c = st.cageCenter;
                sl.playSound(null, c.x, c.y, c.z, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.1F, 1.4F);
                sl.playSound(null, c.x, c.y, c.z, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS, 1.5F, 0.6F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(5.6), e -> e != player && e.isAlive())) {
                    e.invulnerableTime = 0;
                    e.hurt(sl.damageSources().magic(), 20.0F * st.mult);
                    Vec3 pull = c.subtract(e.position()).normalize().scale(0.8);
                    e.setDeltaMovement(pull.x, 0.4, pull.z);
                    e.hurtMarked = true;
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§5⛓ Çift Zincirli Orak — 4+ Blok Zincir Menzili"));
        tooltip.add(Component.literal("§7Sol Tık: §f4 Blok 360° Zincir Kombo"));
        tooltip.add(Component.literal("§7Shift + Sağ Tık: §f5 Kusarigama Stili Değiştir"));
        tooltip.add(Component.literal("§7Sağ Tık: §dÖzel Zincir Hareketi"));
    }
}
