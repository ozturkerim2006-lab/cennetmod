package com.cennetmod;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

import java.util.List;

public class YarikAsasi extends Item {
    public static final ResourceKey<Level> CENNET = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(CennetMod.MODID, "cennet_karanligi"));
    public static final String TAG = "yarik_phantom";
    public static final int SURE = 200; // 10 sn

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
            player.sendSystemMessage(Component.literal("Yıldızlara seslendin...").withStyle(ChatFormatting.WHITE));
            level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.5f, 0.6f);
            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1f, 0.5f);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (level.isClientSide || !(entity instanceof ServerPlayer sp)) return;
        ServerLevel sl = (ServerLevel) level;
        int t = SURE - remaining;
        double x = sp.getX(), y = sp.getY(), z = sp.getZ();

        // Faz 1: yükselen parçacıklar
        if (t < 60 && t % 2 == 0) {
            sl.sendParticles(ParticleTypes.ENCHANT, x, y + 1, z, 8, 0.6, 0.8, 0.6, 0.5);
            sl.sendParticles(ParticleTypes.END_ROD, x, y + 0.2, z, 2, 0.4, 0.1, 0.4, 0.05);
        }
        // Faz 2: yıldızlar kabul etti
        if (t == 60) {
            sp.sendSystemMessage(Component.literal("Yıldızlar kabul etti").withStyle(ChatFormatting.RED));
            level.playSound(null, sp.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 2f, 0.8f);
            level.playSound(null, sp.blockPosition(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS, 0.8f, 0.7f);
        }
        // Faz 3: yarık + phantomlar
        if (t >= 80) {
            double cy = y + 6;
            double r = Math.min(4, (t - 80) / 15.0);
            for (int i = 0; i < 12; i++) {
                double a = t * 0.15 + i * Math.PI / 6;
                sl.sendParticles(ParticleTypes.REVERSE_PORTAL, x + Math.cos(a) * r, cy, z + Math.sin(a) * r, 1, 0, 0, 0, 0);
            }
            sl.sendParticles(ParticleTypes.END_ROD, x, cy, z, 2, 0.3, 0.3, 0.3, 0.02);
            if (t == 80) {
                level.playSound(null, sp.blockPosition(), SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 0.6f, 1f);
                for (int i = 0; i < 5; i++) {
                    Phantom p = EntityType.PHANTOM.create(sl);
                    if (p == null) continue;
                    p.setNoAi(true);
                    p.setInvulnerable(true);
                    p.setSilent(true);
                    p.addTag(TAG);
                    p.setPos(x, cy, z);
                    sl.addFreshEntity(p);
                }
            }
            if (t % 40 == 0)
                level.playSound(null, sp.blockPosition(), SoundEvents.PORTAL_AMBIENT, SoundSource.PLAYERS, 0.8f, 0.9f);

            List<Phantom> list = sl.getEntitiesOfClass(Phantom.class, sp.getBoundingBox().inflate(30),
                    ph -> ph.getTags().contains(TAG));
            for (int i = 0; i < list.size(); i++) {
                double a = t * 0.12 + i * (2 * Math.PI / list.size());
                list.get(i).moveTo(x + Math.cos(a) * 6, cy + Math.sin(t * 0.1 + i), z + Math.sin(a) * 6,
                        (float) Math.toDegrees(a) + 90f, 0);
            }
        }
        // Faz 4: yükselme
        if (t >= 120) {
            sp.setDeltaMovement(0, 0.12, 0);
            sp.hurtMarked = true;
            sp.fallDistance = 0;
            if (t % 10 == 0)
                level.playSound(null, sp.blockPosition(), SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 1f, 0.5f + (t - 120) / 80f);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer sp) {
            temizle((ServerLevel) level, sp);
            boolean cennetteydi = level.dimension().equals(CENNET);
            ServerLevel hedef = sp.server.getLevel(cennetteydi ? Level.OVERWORLD : CENNET);
            if (hedef != null) {
                BlockPos baz = cennetteydi ? hedef.getSharedSpawnPos() : BlockPos.ZERO;
                hedef.getChunk(baz.getX() >> 4, baz.getZ() >> 4); // chunk'ı yükle
                BlockPos yuzey = hedef.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, baz);
                sp.teleportTo(hedef, yuzey.getX() + 0.5, yuzey.getY() + 1, yuzey.getZ() + 0.5, sp.getYRot(), 0);
                hedef.playSound(null, sp.blockPosition(), SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 1f, 1f);
                hedef.playSound(null, sp.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1f, 0.6f);
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
        if (!l.isClientSide && e instanceof ServerPlayer sp) temizle((ServerLevel) l, sp);
    }

    private void temizle(ServerLevel l, ServerPlayer sp) {
        l.getEntitiesOfClass(Phantom.class, sp.getBoundingBox().inflate(40), p -> p.getTags().contains(TAG))
                .forEach(Entity::discard);
    }
}
