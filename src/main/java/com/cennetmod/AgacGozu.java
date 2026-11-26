package com.cennetmod;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AgacGozu extends Monster {
    private int bakisSayaci = 0;

    public AgacGozu(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 15;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            LivingEntity hedef = this.getTarget();
            if (hedef != null && hedef.isAlive() && this.hasLineOfSight(hedef) && this.distanceTo(hedef) <= 16.0F) {
                this.getLookControl().setLookAt(hedef, 30.0F, 30.0F);
                bakisSayaci++;
                if (bakisSayaci >= 40) { // Her 2 saniyede bir bakış saldırısı
                    hedef.hurt(this.damageSources().magic(), 4.0F);
                    hedef.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0));
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SMOKE, hedef.getX(), hedef.getY() + 1.0, hedef.getZ(), 10, 0.3, 0.5, 0.3, 0.02);
                    }
                    bakisSayaci = 0;
                }
            } else {
                bakisSayaci = 0;
            }
        }
    }
}
