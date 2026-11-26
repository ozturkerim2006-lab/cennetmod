package com.cennetmod;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GokselMelekBoss extends Monster {
    public static final int ANIM_IDLE = 0;
    public static final int ANIM_INTRO = 1;
    public static final int ANIM_BARRAGE = 2;
    public static final int ANIM_DOMAIN = 3;
    public static final int ANIM_PHASE2_BREAK = 4;
    public static final int ANIM_BLINK_DASH = 5;
    public static final int ANIM_MIRROR_RING = 6;

    private static final String[] DIYALOGLAR = {
        "§e[Göksel Melek] §fÖlümlü... Kutsal göklere adım atmaya cüret mi ettin?",
        "§b[Göksel Melek] §fYıldızların ışığı seni kül edecek!",
        "§d[Göksel Melek] §cYETER! Aynalar kırılıyor, gerçek gazabımı gör!",
        "§e[Göksel Melek] §7Işığım... sönüyor..."
    };

    private static final EntityDataAccessor<Integer> DATA_ANIM = SynchedEntityData.defineId(GokselMelekBoss.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PHASE = SynchedEntityData.defineId(GokselMelekBoss.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ANIM_TICK = SynchedEntityData.defineId(GokselMelekBoss.class, EntityDataSerializers.INT);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
        Component.literal("✦ Göksel Melek ✦"),
        BossEvent.BossBarColor.YELLOW,
        BossEvent.BossBarOverlay.NOTCHED_10
    );

    private static class HomingOrb {
        Vec3 pos, vel;
        int age;
        UUID targetId;
        boolean cyan;
        HomingOrb(Vec3 p, Vec3 v, UUID t, boolean cyan) { this.pos = p; this.vel = v; this.targetId = t; this.cyan = cyan; }
    }

    private static class StarStrike {
        Vec3 pos;
        int delay;
        StarStrike(Vec3 p, int d) { this.pos = p; this.delay = d; }
    }

    private static class MirrorBeamNode {
        Vec3 pos, targetPos;
        int delay;
        MirrorBeamNode(Vec3 p, Vec3 tp, int d) { this.pos = p; this.targetPos = tp; this.delay = d; }
    }

    private final List<HomingOrb> orbs = new ArrayList<>();
    private final List<StarStrike> strikes = new ArrayList<>();
    private final List<MirrorBeamNode> mirrors = new ArrayList<>();

    private int attackCooldown = 35;
    private int dialogueIndex = 0;
    private int dashCount = 0;
    private boolean introDone = false;
    private boolean phase2Triggered = false;

    public GokselMelekBoss(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 500;
        this.setNoGravity(true);
        this.setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ANIM, ANIM_INTRO);
        builder.define(DATA_PHASE, 1);
        builder.define(DATA_ANIM_TICK, 0);
    }

    public int getAnimState() { return this.entityData.get(DATA_ANIM); }
    public int getPhase() { return this.entityData.get(DATA_PHASE); }
    public int getAnimTick() { return this.entityData.get(DATA_ANIM_TICK); }

    private void setAnim(int state) {
        this.entityData.set(DATA_ANIM, state);
        this.entityData.set(DATA_ANIM_TICK, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 48.0F));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public boolean causeFallDamage(float dist, float mult, DamageSource src) {
        return false;
    }

    private void broadcastDialogue(ServerLevel sl, String text, String title, String subtitle) {
        for (ServerPlayer sp : sl.getPlayers(p -> p.distanceToSqr(this) < 64 * 64)) {
            if (text != null && !text.isEmpty()) {
                sp.sendSystemMessage(Component.literal(text));
            }
            if (title != null || subtitle != null) {
                sp.connection.send(new ClientboundSetTitlesAnimationPacket(6, 42, 12));
                sp.connection.send(new ClientboundSetTitleTextPacket(Component.literal(title != null ? title : "")));
                sp.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(subtitle != null ? subtitle : "")));
            }
        }
    }

    private String nextDialogue() {
        if (DIYALOGLAR.length == 0) return "";
        String d = DIYALOGLAR[dialogueIndex % DIYALOGLAR.length];
        dialogueIndex++;
        return d;
    }

    private Vec3 findSafePosNear(ServerLevel sl, Vec3 desired, double baseY) {
        int bx = Mth.floor(desired.x);
        int bz = Mth.floor(desired.z);
        int by = Mth.floor(baseY);
        for (int dy = 0; dy <= 5; dy++) {
            BlockPos p = new BlockPos(bx, by + dy, bz);
            if (sl.isEmptyBlock(p) && sl.isEmptyBlock(p.above()) && sl.isEmptyBlock(p.above(2))) {
                return new Vec3(bx + 0.5, by + dy, bz + 0.5);
            }
        }
        return new Vec3(desired.x, baseY + 1.5, desired.z);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        int anim = getAnimState();
        if (anim == ANIM_INTRO || anim == ANIM_PHASE2_BREAK) {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 1.3, getZ(), 12, 0.6, 0.8, 0.6, 0.1);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.0F, 1.6F);
            }
            return false;
        }
        if (!phase2Triggered && this.getHealth() - amount <= this.getMaxHealth() * 0.5F) {
            this.setHealth(this.getMaxHealth() * 0.5F);
            startPhase2();
            return true;
        }
        return super.hurt(source, amount);
    }

    private void startPhase2() {
        phase2Triggered = true;
        this.entityData.set(DATA_PHASE, 2);
        setAnim(ANIM_PHASE2_BREAK);
        this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
        this.bossEvent.setName(Component.literal("✦ Göksel Melek — AYNA KIRILDI ✦"));
        if (this.level() instanceof ServerLevel sl) {
            String d = DIYALOGLAR.length >= 3 ? DIYALOGLAR[DIYALOGLAR.length - 2] : nextDialogue();
            broadcastDialogue(sl, d, "§d✦ AYNA KIRILIYOR ✦", "§bFAZ II — GÖKSEL GAZAP");
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 2.0F, 0.6F);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.2F, 1.3F);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            boolean p2 = getPhase() == 2;
            if (this.tickCount % 2 == 0) {
                double a = this.tickCount * 0.25;
                this.level().addParticle(
                    p2 ? ParticleTypes.ELECTRIC_SPARK : ParticleTypes.END_ROD,
                    getX() + Math.cos(a) * 1.1, getY() + 0.4 + (this.random.nextDouble() * 1.8), getZ() + Math.sin(a) * 1.1,
                    0, 0.04, 0
                );
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        ServerLevel sl = (ServerLevel) this.level();
        this.bossEvent.setProgress(Mth.clamp(this.getHealth() / this.getMaxHealth(), 0.0F, 1.0F));
        int anim = getAnimState();
        int at = getAnimTick() + 1;
        this.entityData.set(DATA_ANIM_TICK, at);

        // Aktif mermileri, yildirimlari ve ayna isinlarini isle
        tickOrbs(sl);
        tickStrikes(sl);
        tickMirrors(sl);

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            Player nearest = sl.getNearestPlayer(this, 48.0);
            if (nearest != null && !nearest.isCreative() && !nearest.isSpectator()) {
                this.setTarget(nearest);
                target = nearest;
            }
        }

        // 1. INTRO: Boyut Yarigindan Cikis (Videodaki sahne)
        if (!introDone) {
            if (anim != ANIM_INTRO) setAnim(ANIM_INTRO);
            this.setDeltaMovement(0, at < 35 ? 0.06 : 0.0, 0);
            if (at == 1) {
                broadcastDialogue(sl, DIYALOGLAR[0], "§b✦ GÖKSEL MELEK ✦", "§7Boyut Yarığı Açılıyor...");
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.8F, 0.8F);
            }
            // Dikey camgobegi yarik partikulleri
            for (double y = -0.8; y <= 3.6; y += 0.35) {
                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + y, getZ(), 2, 0.25, 0.05, 0.25, 0.04);
                sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + y, getZ(), 1, 0.12, 0.05, 0.12, 0.02);
            }
            if (at == 55) {
                sl.sendParticles(ParticleTypes.SONIC_BOOM, getX(), getY() + 1.5, getZ(), 1, 0, 0, 0, 0);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.3F, 1.1F);
                for (int i = 0; i < 40; i++) {
                    double ang = i * (Math.PI * 2 / 40);
                    sl.sendParticles(ParticleTypes.REVERSE_PORTAL, getX() + Math.cos(ang) * 4, getY() + 1.5, getZ() + Math.sin(ang) * 4, 2, 0.2, 0.8, 0.2, 0.15);
                }
            }
            if (at >= 80) {
                introDone = true;
                setAnim(ANIM_IDLE);
                attackCooldown = 20;
            }
            return;
        }

        // 2. FAZ 2 GECISI: Ayna Kirilmasi
        if (anim == ANIM_PHASE2_BREAK) {
            this.setDeltaMovement(0, at < 25 ? 0.08 : 0, 0);
            for (int i = 0; i < 16; i++) {
                double ang = i * (Math.PI * 2 / 16) + at * 0.1;
                double rad = at * 0.25;
                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX() + Math.cos(ang) * rad, getY() + 1.4, getZ() + Math.sin(ang) * rad, 1, 0, 0, 0, 0);
                sl.sendParticles(ParticleTypes.DRAGON_BREATH, getX() + Math.cos(ang) * (rad * 0.7), getY() + 1.4, getZ() + Math.sin(ang) * (rad * 0.7), 1, 0, 0, 0, 0);
            }
            if (at == 45) {
                sl.sendParticles(ParticleTypes.SONIC_BOOM, getX(), getY() + 1.5, getZ(), 2, 0.8, 0.8, 0.8, 0);
                sl.sendParticles(ParticleTypes.FLASH, getX(), getY() + 1.5, getZ(), 2, 0.5, 0.5, 0.5, 0);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 2.0F, 1.1F);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.6F, 1.0F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(10.0), e -> e != this && e.isAlive())) {
                    e.hurt(sl.damageSources().magic(), 14.0F);
                    e.knockback(1.8, getX() - e.getX(), getZ() - e.getZ());
                }
                setAnim(ANIM_IDLE);
                attackCooldown = 15;
            }
            return;
        }

        if (target == null) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.85));
            return;
        }

        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        boolean p2 = getPhase() == 2;

        // 3. AKTIF SALDIRI ANIMASYONLARI
        if (anim == ANIM_BARRAGE) {
            hoverAround(target, 10.0, 3.2);
            if (at % (p2 ? 5 : 7) == 0 && at <= 42) {
                int count = p2 ? 2 : 1;
                for (int c = 0; c < count; c++) {
                    Vec3 offset = new Vec3((random.nextDouble() - 0.5) * 4.5, 1.2 + random.nextDouble() * 2.2, (random.nextDouble() - 0.5) * 4.5);
                    Vec3 spawn = position().add(offset);
                    Vec3 initVel = offset.normalize().scale(0.45).add(0, 0.25, 0);
                    orbs.add(new HomingOrb(spawn, initVel, target.getUUID(), p2 && random.nextBoolean()));
                }
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.HOSTILE, 0.9F, 1.3F);
            }
            if (at >= 50) {
                setAnim(ANIM_IDLE);
                attackCooldown = p2 ? 24 : 38;
            }
            return;
        }

        if (anim == ANIM_DOMAIN) {
            hoverAround(target, 9.0, 5.5);
            for (int i = 0; i < 6; i++) {
                double ang = random.nextDouble() * Math.PI * 2;
                double rad = 2.0 + random.nextDouble() * 8.0;
                sl.sendParticles(ParticleTypes.END_ROD, getX() + Math.cos(ang) * rad, getY() + 3.0, getZ() + Math.sin(ang) * rad, 1, 0, -0.3, 0, 0.02);
            }
            if (at % (p2 ? 7 : 10) == 0 && at <= 50) {
                Vec3 sp = target.position().add((random.nextDouble() - 0.5) * 7.0, 0, (random.nextDouble() - 0.5) * 7.0);
                strikes.add(new StarStrike(sp, 22));
                if (at == 10 || at == 30) strikes.add(new StarStrike(target.position(), 24));
                sl.playSound(null, sp.x, sp.y, sp.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.2F, 1.5F);
            }
            if (at >= 60) {
                setAnim(ANIM_IDLE);
                attackCooldown = p2 ? 28 : 44;
            }
            return;
        }

        if (anim == ANIM_BLINK_DASH) {
            if (at == 1 || (p2 && (at == 18 || at == 35))) {
                double ang = random.nextDouble() * Math.PI * 2;
                Vec3 blinkPos = findSafePosNear(sl, target.position().add(Math.cos(ang) * 7.5, 0, Math.sin(ang) * 7.5), target.getY() + 1.0);
                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 1.2, getZ(), 18, 0.4, 0.9, 0.4, 0.12);
                this.teleportTo(blinkPos.x, blinkPos.y, blinkPos.z);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.HOSTILE, 1.4F, 1.2F);
                Vec3 dashDir = target.position().add(0, 0.8, 0).subtract(position()).normalize();
                this.setDeltaMovement(dashDir.scale(p2 ? 1.85 : 1.45));
                dashCount++;
            }
            sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, getX(), getY() + 1.2, getZ(), 6, 0.3, 0.5, 0.3, 0.05);
            if (this.distanceToSqr(target) < 9.0) {
                target.hurt(sl.damageSources().mobAttack(this), p2 ? 15.0F : 11.0F);
                sl.sendParticles(ParticleTypes.SONIC_BOOM, target.getX(), target.getY() + 1.0, target.getZ(), 1, 0, 0, 0, 0);
                sl.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 0.9F, 1.6F);
                target.knockback(1.1, getX() - target.getX(), getZ() - target.getZ());
            }
            if (at >= (p2 ? 48 : 22)) {
                this.setDeltaMovement(Vec3.ZERO);
                setAnim(ANIM_IDLE);
                attackCooldown = p2 ? 22 : 35;
            }
            return;
        }

        if (anim == ANIM_MIRROR_RING) {
            hoverAround(target, 11.0, 4.0);
            if (at == 5) {
                Vec3 tp = target.position().add(0, 1.0, 0);
                for (int i = 0; i < 4; i++) {
                    double ang = i * (Math.PI / 2.0) + random.nextDouble() * 0.5;
                    Vec3 nodePos = tp.add(Math.cos(ang) * 7.5, 1.2, Math.sin(ang) * 7.5);
                    mirrors.add(new MirrorBeamNode(nodePos, tp, 24));
                }
                sl.playSound(null, tp.x, tp.y, tp.z, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.3F, 1.4F);
            }
            if (at >= 38) {
                setAnim(ANIM_IDLE);
                attackCooldown = 26;
            }
            return;
        }

        // 4. BEKLEME & AKILLI SUZULME + YENI BUYU SECIMI
        hoverAround(target, 8.5, 2.8);
        if (attackCooldown > 0) {
            attackCooldown--;
        } else {
            if (random.nextInt(3) == 0 && DIYALOGLAR.length > 1) {
                broadcastDialogue(sl, nextDialogue(), null, null);
            }
            int roll = random.nextInt(p2 ? 4 : 3);
            if (roll == 0) setAnim(ANIM_BARRAGE);
            else if (roll == 1) setAnim(ANIM_DOMAIN);
            else if (roll == 2) setAnim(ANIM_BLINK_DASH);
            else setAnim(ANIM_MIRROR_RING);
        }
    }

    private void hoverAround(LivingEntity target, double idealDist, double idealHeight) {
        double angle = this.tickCount * 0.045;
        Vec3 goal = new Vec3(
            target.getX() + Math.cos(angle) * idealDist,
            target.getY() + idealHeight + Math.sin(this.tickCount * 0.1) * 0.6,
            target.getZ() + Math.sin(angle) * idealDist
        );
        Vec3 diff = goal.subtract(this.position());
        double spd = getPhase() == 2 ? 0.38 : 0.26;
        Vec3 move = diff.length() > 0.2 ? diff.normalize().scale(Math.min(spd, diff.length() * 0.15)) : Vec3.ZERO;
        this.setDeltaMovement(this.getDeltaMovement().scale(0.75).add(move.scale(0.35)));
    }

    private void tickOrbs(ServerLevel sl) {
        Iterator<HomingOrb> it = orbs.iterator();
        while (it.hasNext()) {
            HomingOrb o = it.next();
            o.age++;
            LivingEntity t = (sl.getEntity(o.targetId) instanceof LivingEntity le && le.isAlive()) ? le : getTarget();
            if (t != null && o.age < 55) {
                Vec3 desired = t.getEyePosition().add(0, -0.3, 0).subtract(o.pos).normalize().scale(getPhase() == 2 ? 0.85 : 0.68);
                o.vel = o.vel.scale(0.86).add(desired.scale(0.14));
            }
            o.pos = o.pos.add(o.vel);
            sl.sendParticles(o.cyan ? ParticleTypes.ELECTRIC_SPARK : ParticleTypes.DRAGON_BREATH, o.pos.x, o.pos.y, o.pos.z, 3, 0.08, 0.08, 0.08, 0.01);
            sl.sendParticles(ParticleTypes.END_ROD, o.pos.x, o.pos.y, o.pos.z, 1, 0, 0, 0, 0);

            boolean hit = false;
            for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(o.pos, o.pos).inflate(1.1), e -> e != this && e.isAlive())) {
                e.hurt(sl.damageSources().indirectMagic(this, this), getPhase() == 2 ? 9.0F : 7.0F);
                sl.sendParticles(ParticleTypes.EXPLOSION, o.pos.x, o.pos.y, o.pos.z, 1, 0, 0, 0, 0);
                hit = true;
                break;
            }
            if (hit || o.age > 85) it.remove();
        }
    }

    private void tickStrikes(ServerLevel sl) {
        Iterator<StarStrike> it = strikes.iterator();
        while (it.hasNext()) {
            StarStrike s = it.next();
            s.delay--;
            // Yerde uyari halkasi
            for (int i = 0; i < 12; i++) {
                double a = i * (Math.PI * 2 / 12);
                sl.sendParticles(ParticleTypes.END_ROD, s.pos.x + Math.cos(a) * 2.2, s.pos.y + 0.15, s.pos.z + Math.sin(a) * 2.2, 1, 0, 0, 0, 0);
            }
            if (s.delay <= 0) {
                for (double y = 0; y < 14; y += 0.5) {
                    sl.sendParticles(ParticleTypes.END_ROD, s.pos.x, s.pos.y + y, s.pos.z, 3, 0.3, 0.1, 0.3, 0.02);
                    sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, s.pos.x, s.pos.y + y, s.pos.z, 2, 0.4, 0.1, 0.4, 0.05);
                }
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(sl);
                if (bolt != null) {
                    bolt.moveTo(s.pos.x, s.pos.y, s.pos.z);
                    bolt.setVisualOnly(true);
                    sl.addFreshEntity(bolt);
                }
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(s.pos, s.pos).inflate(2.6, 5.0, 2.6), e -> e != this && e.isAlive())) {
                    e.hurt(sl.damageSources().magic(), 12.0F);
                }
                it.remove();
            }
        }
    }

    private void tickMirrors(ServerLevel sl) {
        Iterator<MirrorBeamNode> it = mirrors.iterator();
        while (it.hasNext()) {
            MirrorBeamNode m = it.next();
            m.delay--;
            for (double y = -0.8; y <= 1.4; y += 0.35) {
                sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, m.pos.x, m.pos.y + y, m.pos.z, 1, 0.1, 0.05, 0.1, 0.01);
            }
            if (m.delay <= 0) {
                Vec3 dir = m.targetPos.subtract(m.pos);
                double len = dir.length();
                Vec3 n = dir.normalize();
                for (double d = 0; d <= len + 4.0; d += 0.45) {
                    Vec3 pt = m.pos.add(n.scale(d));
                    sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pt.x, pt.y, pt.z, 2, 0.05, 0.05, 0.05, 0.01);
                    sl.sendParticles(ParticleTypes.END_ROD, pt.x, pt.y, pt.z, 1, 0, 0, 0, 0);
                    for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(pt, pt).inflate(1.1), e -> e != this && e.isAlive())) {
                        e.hurt(sl.damageSources().magic(), 11.0F);
                    }
                }
                sl.playSound(null, m.pos.x, m.pos.y, m.pos.z, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 0.7F, 1.7F);
                it.remove();
            }
        }
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (this.level() instanceof ServerLevel sl) {
            String lastD = DIYALOGLAR[DIYALOGLAR.length - 1];
            broadcastDialogue(sl, lastD, "§e✦ GÖKSEL MELEK YENİLDİ ✦", "§7Işık Gökyüzüne Dağıldı...");
            sl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 1.5, getZ(), 3, 1.2, 1.2, 1.2, 0);
            sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 1.5, getZ(), 80, 1.5, 1.5, 1.5, 0.25);
            sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 2.0F, 0.7F);
            this.spawnAtLocation(new ItemStack(CennetMod.YILDIZ_KRISTALI.get(), 5));
        }
    }
}
