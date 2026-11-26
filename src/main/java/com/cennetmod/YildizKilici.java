package com.cennetmod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class YildizKilici extends SwordItem {
    private static final String[] STIL_ADLARI = {
        "§e⚡ I. Yıldırım Iaijutsu",
        "§b🌙 II. Hilal Kesen",
        "§f🌀 III. Kasırga Dansı",
        "§6🔥 IV. Güneş Yarığı",
        "§3✨ V. Astral Mızrak",
        "§9🛡 VI. Ayna Savuşturma",
        "§5🌌 VII. Boyut Yaran"
    };

    private static final String[] STIL_ACIKLAMA = {
        "Şimşek Atılımı & Gecikmeli Kesik",
        "Geniş Hilal Dalgaları & Ay Dalgası",
        "Çekim Vuruşu & 2 sn Çelik Fırtına",
        "Alevli Ağır Darbe & Gökten İnen Yargı",
        "Uzun Delici Eskrim & 14 Blok Yıldız Işını",
        "Kristal Kalkan & Zaman Kırılması Kontrası",
        "Arkaya Işınlanma & 12 Kesikli Yıldız Kıyameti"
    };

    private static class CombatState {
        int combo = 0;
        long lastHitTick = 0;
        int whirlwindTicks = 0;
        int parryTicks = 0;
        boolean parryTriggered = false;
        int slamTimer = 0;
        boolean slamReady = false;
        int judgementTicks = 0;
        Vec3 judgementCenter = null;
        int delayedSlashTicks = 0;
        Vec3 dashStart = null;
        Vec3 dashEnd = null;
        float overdriveMult = 1.0F;
        final List<WaveSlash> waves = new ArrayList<>();
    }

    private static class WaveSlash {
        Vec3 pos;
        Vec3 dir;
        int age;
        int maxAge;
        float damage;
        final Set<UUID> hitIds = new HashSet<>();
        WaveSlash(Vec3 p, Vec3 d, int maxAge, float dmg) {
            this.pos = p; this.dir = d; this.maxAge = maxAge; this.damage = dmg;
        }
    }

    private static final Map<UUID, CombatState> STATES = new ConcurrentHashMap<>();

    public YildizKilici() {
        this(new Item.Properties());
    }

    public YildizKilici(Item.Properties props) {
        super(Tiers.NETHERITE, props
            .attributes(SwordItem.createAttributes(Tiers.NETHERITE, 7, -2.1F))
            .durability(4000)
            .fireResistant()
            .rarity(Rarity.EPIC));
    }

    private static CombatState stateOf(Player p) {
        return STATES.computeIfAbsent(p.getUUID(), k -> new CombatState());
    }

    private static int getStyle(Player p) {
        int s = p.getPersistentData().getInt("yk_style");
        return (s < 0 || s >= 7) ? 0 : s;
    }

    private static void setStyle(Player p, int s) {
        p.getPersistentData().putInt("yk_style", ((s % 7) + 7) % 7);
    }

    private static void drawSlashArc(ServerLevel sl, Vec3 origin, Vec3 look, double radius, double tiltDeg, ParticleOptions particle, int points) {
        Vec3 flat = new Vec3(look.x, 0, look.z);
        if (flat.lengthSqr() < 1.0E-4) flat = new Vec3(0, 0, 1);
        flat = flat.normalize();
        Vec3 right = new Vec3(-flat.z, 0, flat.x);
        double tilt = Math.toRadians(tiltDeg);
        for (int i = 0; i <= points; i++) {
            double ang = Math.toRadians(-75.0 + (150.0 * i / points));
            double ca = Math.cos(ang), sa = Math.sin(ang);
            Vec3 local = flat.scale(ca * radius).add(right.scale(sa * radius * Math.cos(tilt))).add(0, sa * radius * Math.sin(tilt), 0);
            Vec3 pt = origin.add(local);
            sl.sendParticles(particle, pt.x, pt.y, pt.z, 1, 0.02, 0.02, 0.02, 0.01);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean res = super.hurtEnemy(stack, target, attacker);
        if (!(attacker instanceof Player player) || !(player.level() instanceof ServerLevel sl)) return res;

        CombatState st = stateOf(player);
        long now = sl.getGameTime();
        if (now - st.lastHitTick > 70) st.combo = 0;
        st.combo = (st.combo % 4) + 1;
        st.lastHitTick = now;

        int style = getStyle(player);
        Vec3 eye = player.getEyePosition().add(0, -0.3, 0);
        Vec3 look = player.getLookAngle();
        boolean finisher = (st.combo == 4);
        double tilt = switch (st.combo) {
            case 1 -> 12.0;
            case 2 -> -35.0;
            case 3 -> 40.0;
            default -> 0.0;
        };

        switch (style) {
            case 0 -> { // Yildirim Iaijutsu
                drawSlashArc(sl, eye, look, 2.8, tilt, ParticleTypes.ELECTRIC_SPARK, 14);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
                sl.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.4F + st.combo * 0.1F);
                if (finisher) {
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(sl);
                    if (bolt != null) {
                        bolt.moveTo(target.getX(), target.getY(), target.getZ());
                        bolt.setVisualOnly(true);
                        sl.addFreshEntity(bolt);
                    }
                    target.hurt(sl.damageSources().magic(), 10.0F);
                    sl.sendParticles(ParticleTypes.FLASH, target.getX(), target.getY() + 1, target.getZ(), 1, 0, 0, 0, 0);
                }
            }
            case 1 -> { // Hilal Kesen
                drawSlashArc(sl, eye, look, 3.6, tilt, ParticleTypes.END_ROD, 18);
                drawSlashArc(sl, eye, look, 3.3, tilt, ParticleTypes.SOUL_FIRE_FLAME, 12);
                sl.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.2F, 0.85F);
                AABB area = target.getBoundingBox().inflate(finisher ? 4.2 : 2.8);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive())) {
                    if (e != target) e.hurt(sl.damageSources().playerAttack(player), finisher ? 11.0F : 5.5F);
                }
                if (finisher) {
                    drawSlashArc(sl, eye, look.scale(-1), 3.6, -tilt, ParticleTypes.END_ROD, 16);
                    sl.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), target.getY() + 1, target.getZ(), 5, 1.2, 0.4, 1.2, 0);
                }
            }
            case 2 -> { // Kasirga Dansi
                drawSlashArc(sl, eye, look, 3.0, tilt * 1.4, ParticleTypes.CLOUD, 16);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3.5), e -> e != player && e.isAlive())) {
                    Vec3 pull = target.position().subtract(e.position()).normalize().scale(0.35);
                    e.setDeltaMovement(e.getDeltaMovement().add(pull.x, finisher ? 0.65 : 0.15, pull.z));
                    e.hurtMarked = true;
                }
                if (finisher) {
                    target.hurt(sl.damageSources().playerAttack(player), 9.0F);
                    sl.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundSource.PLAYERS, 1.2F, 1.3F);
                }
            }
            case 3 -> { // Gunes Yarigi
                drawSlashArc(sl, eye, look, 3.0, finisher ? 85.0 : tilt, ParticleTypes.FLAME, 16);
                target.setRemainingFireTicks(100);
                if (finisher) {
                    target.hurt(sl.damageSources().magic(), 12.0F);
                    for (int y = 0; y < 8; y++) {
                        sl.sendParticles(ParticleTypes.FLAME, target.getX(), target.getY() + y * 0.5, target.getZ(), 12, 0.6, 0.2, 0.6, 0.08);
                    }
                    sl.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.3F, 0.7F);
                }
            }
            case 4 -> { // Astral Mizrak
                for (double d = 1.0; d <= 5.5; d += 0.45) {
                    Vec3 pt = eye.add(look.scale(d));
                    sl.sendParticles(ParticleTypes.ENCHANTED_HIT, pt.x, pt.y, pt.z, 2, 0.05, 0.05, 0.05, 0.02);
                }
                sl.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.6F);
                if (finisher) {
                    target.invulnerableTime = 0;
                    target.hurt(sl.damageSources().magic(), 11.0F);
                    sl.sendParticles(ParticleTypes.END_ROD, target.getX(), target.getY() + 1, target.getZ(), 20, 0.4, 0.6, 0.4, 0.15);
                }
            }
            case 5 -> { // Ayna Savusturma
                drawSlashArc(sl, eye, look, 2.7, tilt, ParticleTypes.GLOW, 14);
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 35, 0, false, false));
                if (finisher) {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 3));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 1));
                    target.hurt(sl.damageSources().magic(), 8.0F);
                    sl.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.3F, 1.1F);
                }
            }
            case 6 -> { // Boyut Yaran
                drawSlashArc(sl, eye, look, 3.2, tilt, ParticleTypes.DRAGON_BREATH, 16);
                sl.sendParticles(ParticleTypes.PORTAL, target.getX(), target.getY() + 1, target.getZ(), 10, 0.4, 0.6, 0.4, 0.2);
                if (st.combo == 3) {
                    Vec3 behind = target.position().subtract(target.getLookAngle().multiply(1, 0, 1).normalize().scale(2.2));
                    if (sl.isEmptyBlock(net.minecraft.core.BlockPos.containing(behind))) {
                        player.teleportTo(behind.x, target.getY(), behind.z);
                        sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.9F, 1.4F);
                    }
                } else if (finisher) {
                    target.hurt(sl.damageSources().magic(), 13.0F);
                    sl.sendParticles(ParticleTypes.SONIC_BOOM, target.getX(), target.getY() + 1, target.getZ(), 1, 0, 0, 0, 0);
                    sl.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.PLAYERS, 1.2F, 0.6F);
                }
            }
        }

        String bar = STIL_ADLARI[style] + "  §7|  §f⚔ Kombo: §e" + st.combo + "/4" + (st.combo >= 3 ? " §d★ OVERDRIVE HAZIR!" : "");
        player.displayClientMessage(Component.literal(bar), true);
        return res;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        ServerLevel sl = (ServerLevel) level;

        // Shift + Sag Tik: Stil Degistir
        if (player.isShiftKeyDown()) {
            int next = (getStyle(player) + 1) % 7;
            setStyle(player, next);
            sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 0.9F + next * 0.12F);
            sl.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.1, player.getZ(), 18, 0.5, 0.6, 0.5, 0.08);
            player.displayClientMessage(Component.literal(STIL_ADLARI[next] + " §7— §f" + STIL_ACIKLAMA[next]), true);
            player.getCooldowns().addCooldown(this, 6);
            return InteractionResultHolder.success(stack);
        }

        CombatState st = stateOf(player);
        int style = getStyle(player);
        boolean overdrive = (sl.getGameTime() - st.lastHitTick <= 70 && st.combo >= 3);
        float mult = overdrive ? 1.5F : 1.0F;
        if (overdrive) {
            st.combo = 0;
            sl.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1, player.getZ(), 1, 0, 0, 0, 0);
            player.displayClientMessage(Component.literal("§d★ OVERDRIVE! " + STIL_ADLARI[style]), true);
        }

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();

        switch (style) {
            case 0 -> { // Simsek Atilimi + Gecikmeli Kesik
                Vec3 start = player.position().add(0, 1, 0);
                Vec3 dash = new Vec3(look.x, Math.max(-0.15, look.y * 0.45), look.z).normalize().scale(2.4);
                player.setDeltaMovement(dash);
                player.hurtMarked = true;
                player.fallDistance = 0;
                Vec3 end = start.add(look.scale(9.0));
                st.dashStart = start;
                st.dashEnd = end;
                st.delayedSlashTicks = 12;
                st.overdriveMult = mult;
                for (double d = 0; d <= 9.0; d += 0.4) {
                    Vec3 pt = start.add(look.scale(d));
                    sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, pt.x, pt.y, pt.z, 3, 0.15, 0.15, 0.15, 0.05);
                }
                AABB box = new AABB(start, end).inflate(1.8);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive())) {
                    e.hurt(sl.damageSources().playerAttack(player), 11.0F * mult);
                }
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.3F, 1.8F);
                player.getCooldowns().addCooldown(this, 36);
            }
            case 1 -> { // Ay Dalgasi
                st.waves.add(new WaveSlash(eye.add(look.scale(1.2)), look.scale(1.15), 22, 13.0F * mult));
                if (overdrive) {
                    Vec3 left = look.yRot(0.28F).scale(1.1);
                    Vec3 right = look.yRot(-0.28F).scale(1.1);
                    st.waves.add(new WaveSlash(eye.add(look.scale(1.2)), left, 20, 10.0F));
                    st.waves.add(new WaveSlash(eye.add(look.scale(1.2)), right, 20, 10.0F));
                }
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.4F, 0.65F);
                player.getCooldowns().addCooldown(this, 32);
            }
            case 2 -> { // Celik Firtina (2 sn donen kasirga)
                st.whirlwindTicks = overdrive ? 55 : 40;
                st.overdriveMult = mult;
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8F, 1.6F);
                player.getCooldowns().addCooldown(this, 75);
            }
            case 3 -> { // Gokten Inen Yargi (Meteor Slam)
                player.setDeltaMovement(look.x * 0.9, 1.15, look.z * 0.9);
                player.hurtMarked = true;
                st.slamTimer = 14;
                st.slamReady = true;
                st.overdriveMult = mult;
                sl.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY(), player.getZ(), 25, 0.5, 0.2, 0.5, 0.12);
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.2F, 0.9F);
                player.getCooldowns().addCooldown(this, 65);
            }
            case 4 -> { // Yildiz Delisi (14 Blok Isin)
                for (double d = 1.0; d <= 14.0; d += 0.45) {
                    Vec3 pt = eye.add(look.scale(d));
                    sl.sendParticles(ParticleTypes.END_ROD, pt.x, pt.y, pt.z, 3, 0.08, 0.08, 0.08, 0.01);
                    if ((int)(d * 2) % 4 == 0) sl.sendParticles(ParticleTypes.SONIC_BOOM, pt.x, pt.y, pt.z, 1, 0, 0, 0, 0);
                    for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(pt, pt).inflate(1.3), e -> e != player && e.isAlive())) {
                        e.hurt(sl.damageSources().magic(), 15.0F * mult);
                        e.knockback(0.6, -look.x, -look.z);
                    }
                }
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.1F, 1.5F);
                player.getCooldowns().addCooldown(this, 50);
            }
            case 5 -> { // Mutlak Savusturma (Parry)
                st.parryTicks = 24;
                st.parryTriggered = false;
                st.overdriveMult = mult;
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 26, 3, false, false));
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.2F, 1.3F);
                player.displayClientMessage(Component.literal("§9🛡 Ayna Savuşturma Aktif!"), true);
                player.getCooldowns().addCooldown(this, 55);
            }
            case 6 -> { // Yildiz Kiyameti (Judgement Cut)
                Vec3 center = eye.add(look.scale(6.0));
                List<LivingEntity> targets = sl.getEntitiesOfClass(LivingEntity.class, new AABB(eye, eye.add(look.scale(12))).inflate(2.5), e -> e != player && e.isAlive());
                if (!targets.isEmpty()) center = targets.get(0).position().add(0, 1.0, 0);
                st.judgementCenter = center;
                st.judgementTicks = overdrive ? 36 : 26;
                st.overdriveMult = mult;
                sl.playSound(null, center.x, center.y, center.z, SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.3F, 0.7F);
                player.getCooldowns().addCooldown(this, 85);
            }
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player player) || !(level instanceof ServerLevel sl)) return;
        CombatState st = STATES.get(player.getUUID());
        if (st == null) return;

        // 1. Gecikmeli Simsek Kesigi
        if (st.delayedSlashTicks > 0) {
            st.delayedSlashTicks--;
            if (st.delayedSlashTicks == 0 && st.dashStart != null && st.dashEnd != null) {
                Vec3 dir = st.dashEnd.subtract(st.dashStart);
                double len = dir.length();
                Vec3 n = dir.normalize();
                for (double d = 0; d <= len; d += 0.45) {
                    Vec3 pt = st.dashStart.add(n.scale(d));
                    sl.sendParticles(ParticleTypes.SONIC_BOOM, pt.x, pt.y, pt.z, 1, 0, 0, 0, 0);
                    sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, pt.x, pt.y, pt.z, 6, 0.3, 0.3, 0.3, 0.15);
                }
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(st.dashStart, st.dashEnd).inflate(2.4), e -> e != player && e.isAlive())) {
                    e.invulnerableTime = 0;
                    e.hurt(sl.damageSources().magic(), 12.0F * st.overdriveMult);
                }
                sl.playSound(null, st.dashEnd.x, st.dashEnd.y, st.dashEnd.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.9F, 1.5F);
            }
        }

        // 2. Hilal Ay Dalgalari
        if (!st.waves.isEmpty()) {
            Iterator<WaveSlash> it = st.waves.iterator();
            while (it.hasNext()) {
                WaveSlash w = it.next();
                w.pos = w.pos.add(w.dir);
                w.age++;
                drawSlashArc(sl, w.pos, w.dir.normalize(), 2.8, 0.0, ParticleTypes.END_ROD, 12);
                drawSlashArc(sl, w.pos, w.dir.normalize(), 2.5, 0.0, ParticleTypes.SOUL_FIRE_FLAME, 8);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(w.pos, w.pos).inflate(2.4), e -> e != player && e.isAlive())) {
                    if (w.hitIds.add(e.getUUID())) {
                        e.hurt(sl.damageSources().magic(), w.damage);
                        e.knockback(0.9, -w.dir.x, -w.dir.z);
                    }
                }
                if (w.age >= w.maxAge) it.remove();
            }
        }

        // 3. Celik Firtina (Kasirga)
        if (st.whirlwindTicks > 0) {
            st.whirlwindTicks--;
            double angle = (40 - st.whirlwindTicks) * 0.75;
            for (int k = 0; k < 4; k++) {
                double a = angle + k * (Math.PI / 2.0);
                double r = 3.2;
                sl.sendParticles(ParticleTypes.CLOUD, player.getX() + Math.cos(a) * r, player.getY() + 0.8 + (k % 2) * 0.6, player.getZ() + Math.sin(a) * r, 2, 0.1, 0.1, 0.1, 0.02);
                sl.sendParticles(ParticleTypes.END_ROD, player.getX() + Math.cos(a) * (r - 0.6), player.getY() + 1.0, player.getZ() + Math.sin(a) * (r - 0.6), 1, 0, 0, 0, 0);
            }
            if (st.whirlwindTicks % 4 == 0) {
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.7F, 1.3F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(4.0), e -> e != player && e.isAlive())) {
                    e.invulnerableTime = 0;
                    e.hurt(sl.damageSources().playerAttack(player), 4.5F * st.overdriveMult);
                    Vec3 pull = player.position().subtract(e.position()).normalize().scale(0.25);
                    e.setDeltaMovement(e.getDeltaMovement().add(pull.x, 0.08, pull.z));
                }
            }
        }

        // 4. Meteor Slam
        if (st.slamReady) {
            player.fallDistance = 0;
            if (st.slamTimer > 0) {
                st.slamTimer--;
                if (st.slamTimer == 0) {
                    player.setDeltaMovement(player.getLookAngle().x * 0.6, -1.7, player.getLookAngle().z * 0.6);
                    player.hurtMarked = true;
                }
            } else if (player.onGround() || player.isInWater()) {
                st.slamReady = false;
                for (int i = 0; i < 36; i++) {
                    double a = i * (Math.PI * 2 / 36);
                    for (double r = 1.5; r <= 6.0; r += 1.5) {
                        sl.sendParticles(ParticleTypes.FLAME, player.getX() + Math.cos(a) * r, player.getY() + 0.3, player.getZ() + Math.sin(a) * r, 2, 0.1, 0.2, 0.1, 0.06);
                    }
                }
                sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, player.getX(), player.getY() + 0.5, player.getZ(), 2, 1.5, 0.3, 1.5, 0);
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.PLAYERS, 1.4F, 0.85F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(6.0), e -> e != player && e.isAlive())) {
                    e.hurt(sl.damageSources().magic(), 18.0F * st.overdriveMult);
                    e.setRemainingFireTicks(140);
                    e.setDeltaMovement(e.position().subtract(player.position()).normalize().scale(1.1).add(0, 0.65, 0));
                    e.hurtMarked = true;
                }
            }
        }

        // 5. Ayna Savusturma (Parry Kontra)
        if (st.parryTicks > 0) {
            st.parryTicks--;
            double a = st.parryTicks * 0.5;
            for (int i = 0; i < 4; i++) {
                double ang = a + i * (Math.PI / 2.0);
                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX() + Math.cos(ang) * 1.4, player.getY() + 1.0, player.getZ() + Math.sin(ang) * 1.4, 1, 0, 0, 0, 0);
            }
            if (!st.parryTriggered && player.hurtTime >= 8) {
                st.parryTriggered = true;
                st.parryTicks = 0;
                player.heal(8.0F);
                sl.sendParticles(ParticleTypes.SONIC_BOOM, player.getX(), player.getY() + 1.0, player.getZ(), 1, 0, 0, 0, 0);
                sl.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1.0, player.getZ(), 1, 0, 0, 0, 0);
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.4F, 1.3F);
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.6F);
                player.displayClientMessage(Component.literal("§b✦ MÜKEMMEL SAVUŞTURMA! ZAMAN KIRILDI!"), true);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5.5), e -> e != player && e.isAlive())) {
                    e.hurt(sl.damageSources().magic(), 22.0F * st.overdriveMult);
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 4));
                    e.knockback(1.4, player.getX() - e.getX(), player.getZ() - e.getZ());
                }
            }
        }

        // 6. Yildiz Kiyameti (Judgement Cut)
        if (st.judgementTicks > 0 && st.judgementCenter != null) {
            st.judgementTicks--;
            Vec3 c = st.judgementCenter;
            if (st.judgementTicks % 2 == 0) {
                double rx = (sl.random.nextDouble() - 0.5) * 2.0;
                double ry = (sl.random.nextDouble() - 0.5) * 1.2;
                double rz = (sl.random.nextDouble() - 0.5) * 2.0;
                Vec3 dir = new Vec3(rx, ry, rz).normalize();
                for (double t = -3.2; t <= 3.2; t += 0.35) {
                    Vec3 pt = c.add(dir.scale(t));
                    sl.sendParticles(ParticleTypes.DRAGON_BREATH, pt.x, pt.y, pt.z, 1, 0, 0, 0, 0.01);
                    sl.sendParticles(ParticleTypes.END_ROD, pt.x, pt.y, pt.z, 1, 0, 0, 0, 0);
                }
                sl.playSound(null, c.x, c.y, c.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.8F, 1.5F + sl.random.nextFloat() * 0.5F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(4.2), e -> e != player && e.isAlive())) {
                    e.invulnerableTime = 0;
                    e.hurt(sl.damageSources().magic(), 3.2F * st.overdriveMult);
                    e.setDeltaMovement(0, 0.08, 0);
                }
            }
            if (st.judgementTicks == 0) {
                sl.sendParticles(ParticleTypes.SONIC_BOOM, c.x, c.y, c.z, 2, 0.5, 0.5, 0.5, 0);
                sl.sendParticles(ParticleTypes.REVERSE_PORTAL, c.x, c.y, c.z, 45, 1.5, 1.5, 1.5, 0.2);
                sl.playSound(null, c.x, c.y, c.z, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.4F, 0.7F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(4.8), e -> e != player && e.isAlive())) {
                    e.invulnerableTime = 0;
                    e.hurt(sl.damageSources().magic(), 14.0F * st.overdriveMult);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§d✦ 7 Göksel Dövüş Stili & Kombo Sistemi"));
        tooltip.add(Component.literal("§7Shift + Sağ Tık: §fStil Değiştir"));
        tooltip.add(Component.literal("§7Sağ Tık: §fÖzel Stil Yeteneği"));
        tooltip.add(Component.literal("§7Kombo 3-4 + Sağ Tık: §dOVERDRIVE (%150 Güç)"));
    }
}
