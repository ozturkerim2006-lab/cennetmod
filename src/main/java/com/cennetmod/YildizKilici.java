package com.cennetmod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class YildizKilici extends SwordItem {
    public static final String[] STIL_ADLARI = {
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
        boolean wasSwinging = false;
        int lastSwingTime = 0;
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
        Vec3 pos, dir;
        int age, maxAge;
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

    public static int getStyle(ItemStack stack) {
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        int s = cd != null ? cd.copyTag().getInt("yk_style") : 0;
        return (s < 0 || s >= 7) ? 0 : s;
    }

    public static void setStyle(ItemStack stack, int next) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("yk_style", ((next % 7) + 7) % 7);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static CombatState stateOf(Player p) {
        return STATES.computeIfAbsent(p.getUUID(), k -> new CombatState());
    }

    private void onComboSwing(ServerLevel sl, Player player, ItemStack stack) {
        CombatState st = stateOf(player);
        long now = sl.getGameTime();
        if (now - st.lastHitTick > 70) st.combo = 0;
        st.combo = (st.combo % 4) + 1;
        st.lastHitTick = now;

        int style = getStyle(stack);
        boolean finisher = (st.combo == 4);
        Vec3 look = player.getLookAngle().normalize();
        Vec3 hitCenter = player.position().add(0, 1.0, 0).add(look.scale(2.5));
        double reach = (style == 1 || style == 4) ? 3.8 : 3.2;

        sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F + st.combo * 0.12F);
        List<LivingEntity> hits = sl.getEntitiesOfClass(LivingEntity.class, new AABB(hitCenter, hitCenter).inflate(reach, 1.8, reach), e -> e != player && e.isAlive());
        if (hits.isEmpty()) return;

        LivingEntity primary = hits.get(0);
        for (LivingEntity target : hits) {
            float extra = finisher ? 8.5F : 3.0F;
            target.hurt(sl.damageSources().playerAttack(player), extra);
            switch (style) {
                case 0 -> {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
                    if (finisher && target == primary) {
                        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(sl);
                        if (bolt != null) { bolt.moveTo(target.getX(), target.getY(), target.getZ()); bolt.setVisualOnly(true); sl.addFreshEntity(bolt); }
                        target.hurt(sl.damageSources().magic(), 8.0F);
                    }
                }
                case 1 -> { if (finisher) target.hurt(sl.damageSources().magic(), 5.0F); }
                case 2 -> {
                    Vec3 pull = primary.position().subtract(target.position()).normalize().scale(0.3);
                    target.setDeltaMovement(target.getDeltaMovement().add(pull.x, finisher ? 0.62 : 0.14, pull.z));
                    target.hurtMarked = true;
                }
                case 3 -> {
                    target.setRemainingFireTicks(100);
                    if (finisher) target.hurt(sl.damageSources().magic(), 8.0F);
                }
                case 4 -> { if (finisher) { target.invulnerableTime = 0; target.hurt(sl.damageSources().magic(), 9.0F); } }
                case 5 -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 35, 0, false, false));
                    if (finisher) target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 70, 3));
                }
                case 6 -> {
                    if (st.combo == 3 && target == primary) {
                        Vec3 behind = target.position().subtract(target.getLookAngle().multiply(1, 0, 1).normalize().scale(2.2));
                        if (sl.isEmptyBlock(BlockPos.containing(behind))) {
                            player.teleportTo(behind.x, target.getY(), behind.z);
                            sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.9F, 1.4F);
                        }
                    } else if (finisher) {
                        target.hurt(sl.damageSources().magic(), 10.0F);
                    }
                }
            }
        }
        String bar = STIL_ADLARI[style] + "  §7|  §f⚔ Kombo: §e" + st.combo + "/4" + (st.combo >= 3 ? " §d★ OVERDRIVE HAZIR!" : "");
        player.displayClientMessage(Component.literal(bar), true);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            int next = (getStyle(stack) + 1) % 7;
            if (!level.isClientSide) {
                setStyle(stack, next);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 0.9F + next * 0.12F);
                player.displayClientMessage(Component.literal(STIL_ADLARI[next] + " §7— §f" + STIL_ACIKLAMA[next]), true);
                player.getCooldowns().addCooldown(this, 6);
            }
            return InteractionResultHolder.success(stack);
        }

        if (level.isClientSide) return InteractionResultHolder.success(stack);
        ServerLevel sl = (ServerLevel) level;
        CombatState st = stateOf(player);
        int style = getStyle(stack);
        boolean overdrive = (sl.getGameTime() - st.lastHitTick <= 70 && st.combo >= 3);
        float mult = overdrive ? 1.5F : 1.0F;
        if (overdrive) {
            st.combo = 0;
            player.displayClientMessage(Component.literal("§d★ OVERDRIVE! " + STIL_ADLARI[style]), true);
        }

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();

        switch (style) {
            case 0 -> {
                Vec3 start = player.position().add(0, 1, 0);
                player.setDeltaMovement(new Vec3(look.x, Math.max(-0.15, look.y * 0.45), look.z).normalize().scale(2.4));
                player.hurtMarked = true;
                player.fallDistance = 0;
                Vec3 end = start.add(look.scale(9.0));
                st.dashStart = start; st.dashEnd = end; st.delayedSlashTicks = 12; st.overdriveMult = mult;
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(start, end).inflate(1.9), e -> e != player && e.isAlive())) {
                    e.hurt(sl.damageSources().playerAttack(player), 11.0F * mult);
                }
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.3F, 1.8F);
                player.getCooldowns().addCooldown(this, 36);
            }
            case 1 -> {
                st.waves.add(new WaveSlash(eye.add(look.scale(1.2)), look.scale(1.15), 22, 13.0F * mult));
                if (overdrive) {
                    st.waves.add(new WaveSlash(eye.add(look.scale(1.2)), look.yRot(0.28F).scale(1.1), 20, 10.0F));
                    st.waves.add(new WaveSlash(eye.add(look.scale(1.2)), look.yRot(-0.28F).scale(1.1), 20, 10.0F));
                }
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.4F, 0.65F);
                player.getCooldowns().addCooldown(this, 32);
            }
            case 2 -> {
                st.whirlwindTicks = overdrive ? 55 : 40;
                st.overdriveMult = mult;
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8F, 1.6F);
                player.getCooldowns().addCooldown(this, 75);
            }
            case 3 -> {
                player.setDeltaMovement(look.x * 0.9, 1.15, look.z * 0.9);
                player.hurtMarked = true;
                st.slamTimer = 14; st.slamReady = true; st.overdriveMult = mult;
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.2F, 0.9F);
                player.getCooldowns().addCooldown(this, 65);
            }
            case 4 -> {
                for (double d = 1.0; d <= 14.0; d += 0.6) {
                    Vec3 pt = eye.add(look.scale(d));
                    for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(pt, pt).inflate(1.4), e -> e != player && e.isAlive())) {
                        e.hurt(sl.damageSources().magic(), 15.0F * mult);
                        e.knockback(0.6, -look.x, -look.z);
                    }
                }
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.1F, 1.5F);
                player.getCooldowns().addCooldown(this, 50);
            }
            case 5 -> {
                st.parryTicks = 24; st.parryTriggered = false; st.overdriveMult = mult;
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 26, 3, false, false));
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.2F, 1.3F);
                player.displayClientMessage(Component.literal("§9🛡 Ayna Savuşturma Aktif!"), true);
                player.getCooldowns().addCooldown(this, 55);
            }
            case 6 -> {
                Vec3 center = eye.add(look.scale(6.0));
                List<LivingEntity> targets = sl.getEntitiesOfClass(LivingEntity.class, new AABB(eye, eye.add(look.scale(12))).inflate(2.5), e -> e != player && e.isAlive());
                if (!targets.isEmpty()) center = targets.get(0).position().add(0, 1.0, 0);
                st.judgementCenter = center; st.judgementTicks = overdrive ? 36 : 26; st.overdriveMult = mult;
                sl.playSound(null, center.x, center.y, center.z, SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.3F, 0.7F);
                player.getCooldowns().addCooldown(this, 85);
            }
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player player) || !(level instanceof ServerLevel sl)) return;
        CombatState st = stateOf(player);

        if (selected) {
            boolean swingStart = player.swinging && (!st.wasSwinging || player.swingTime < st.lastSwingTime);
            if (swingStart) onComboSwing(sl, player, stack);
            st.wasSwinging = player.swinging;
            st.lastSwingTime = player.swingTime;
        } else {
            st.wasSwinging = false;
        }

        if (st.delayedSlashTicks > 0 && --st.delayedSlashTicks == 0 && st.dashStart != null && st.dashEnd != null) {
            for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(st.dashStart, st.dashEnd).inflate(2.4), e -> e != player && e.isAlive())) {
                e.invulnerableTime = 0;
                e.hurt(sl.damageSources().magic(), 12.0F * st.overdriveMult);
            }
            sl.playSound(null, st.dashEnd.x, st.dashEnd.y, st.dashEnd.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.9F, 1.5F);
        }

        if (!st.waves.isEmpty()) {
            Iterator<WaveSlash> it = st.waves.iterator();
            while (it.hasNext()) {
                WaveSlash w = it.next();
                w.pos = w.pos.add(w.dir);
                w.age++;
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(w.pos, w.pos).inflate(2.6), e -> e != player && e.isAlive())) {
                    if (w.hitIds.add(e.getUUID())) {
                        e.hurt(sl.damageSources().magic(), w.damage);
                        e.knockback(0.9, -w.dir.x, -w.dir.z);
                    }
                }
                if (w.age >= w.maxAge) it.remove();
            }
        }

        if (st.whirlwindTicks > 0) {
            st.whirlwindTicks--;
            if (st.whirlwindTicks % 4 == 0) {
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.7F, 1.3F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(4.2), e -> e != player && e.isAlive())) {
                    e.invulnerableTime = 0;
                    e.hurt(sl.damageSources().playerAttack(player), 4.8F * st.overdriveMult);
                }
            }
        }

        if (st.slamReady) {
            player.fallDistance = 0;
            if (st.slamTimer > 0) {
                if (--st.slamTimer == 0) {
                    player.setDeltaMovement(player.getLookAngle().x * 0.6, -1.7, player.getLookAngle().z * 0.6);
                    player.hurtMarked = true;
                }
            } else if (player.onGround() || player.isInWater()) {
                st.slamReady = false;
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.PLAYERS, 1.4F, 0.85F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(6.0), e -> e != player && e.isAlive())) {
                    e.hurt(sl.damageSources().magic(), 18.0F * st.overdriveMult);
                    e.setRemainingFireTicks(140);
                    e.setDeltaMovement(e.position().subtract(player.position()).normalize().scale(1.1).add(0, 0.65, 0));
                    e.hurtMarked = true;
                }
            }
        }

        if (st.parryTicks > 0) {
            st.parryTicks--;
            if (!st.parryTriggered && player.hurtTime >= 8) {
                st.parryTriggered = true; st.parryTicks = 0;
                player.heal(8.0F);
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.4F, 1.3F);
                sl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.6F);
                player.displayClientMessage(Component.literal("§b✦ MÜKEMMEL SAVUŞTURMA! ZAMAN KIRILDI!"), true);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5.5), e -> e != player && e.isAlive())) {
                    e.hurt(sl.damageSources().magic(), 22.0F * st.overdriveMult);
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 4));
                }
            }
        }

        if (st.judgementTicks > 0 && st.judgementCenter != null) {
            st.judgementTicks--;
            Vec3 c = st.judgementCenter;
            if (st.judgementTicks % 2 == 0) {
                sl.playSound(null, c.x, c.y, c.z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.8F, 1.5F + sl.random.nextFloat() * 0.5F);
                for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(4.2), e -> e != player && e.isAlive())) {
                    e.invulnerableTime = 0;
                    e.hurt(sl.damageSources().magic(), 3.4F * st.overdriveMult);
                    e.setDeltaMovement(0, 0.08, 0);
                }
            }
            if (st.judgementTicks == 0) {
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
        tooltip.add(Component.literal("§d✦ 7 Göksel Dövüş Stili & Özel 3D Efektler"));
        tooltip.add(Component.literal("§7Shift + Sağ Tık: §fStil Değiştir"));
        tooltip.add(Component.literal("§7Sağ Tık: §fÖzel Stil Yeteneği"));
        tooltip.add(Component.literal("§7Kombo 3-4 + Sağ Tık: §dOVERDRIVE (%150 Güç)"));
    }
}
