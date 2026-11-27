package com.cennetmod;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class GokselMelekModel extends HumanoidModel<GokselMelekBoss> {
    public GokselMelekModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(GokselMelekBoss e, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(e, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        int anim = e.getAnimState();
        float t = ageInTicks;

        this.body.xRot = 0.0F;
        this.rightLeg.xRot = 0.18F + Mth.sin(t * 0.12F) * 0.06F;
        this.leftLeg.xRot = 0.28F - Mth.sin(t * 0.12F) * 0.06F;
        this.rightLeg.zRot = 0.04F;
        this.leftLeg.zRot = -0.04F;

        switch (anim) {
            case GokselMelekBoss.ANIM_INTRO -> {
                float p = Mth.clamp(e.getAnimTick() / 55.0F, 0.0F, 1.0F);
                this.head.xRot = -0.30F * p;
                this.rightArm.xRot = -0.45F * p;
                this.leftArm.xRot = -0.45F * p;
                this.rightArm.zRot = 1.25F * p;
                this.leftArm.zRot = -1.25F * p;
            }
            case GokselMelekBoss.ANIM_BARRAGE -> {
                this.rightArm.xRot = -1.45F + Mth.sin(t * 0.50F) * 0.30F;
                this.leftArm.xRot = -1.45F - Mth.sin(t * 0.50F) * 0.30F;
            }
            case GokselMelekBoss.ANIM_DOMAIN -> {
                this.head.xRot = -0.38F;
                this.rightArm.xRot = -2.35F;
                this.leftArm.xRot = -2.35F;
                this.rightArm.zRot = 0.45F;
                this.leftArm.zRot = -0.45F;
            }
            case GokselMelekBoss.ANIM_PHASE2_BREAK -> {
                this.head.xRot = -0.42F;
                this.rightArm.xRot = -0.35F;
                this.leftArm.xRot = -0.35F;
                this.rightArm.zRot = 1.45F;
                this.leftArm.zRot = -1.45F;
            }
            case GokselMelekBoss.ANIM_BLINK_DASH -> {
                this.rightArm.xRot = -1.15F;
                this.leftArm.xRot = 0.65F;
                this.rightLeg.xRot = 0.55F;
                this.leftLeg.xRot = 0.45F;
            }
            case GokselMelekBoss.ANIM_MIRROR_RING -> {
                this.rightArm.xRot = -1.25F;
                this.leftArm.xRot = -1.25F;
                this.rightArm.zRot = 0.85F;
                this.leftArm.zRot = -0.85F;
            }
            default -> {
                this.rightArm.xRot = -0.25F + Mth.sin(t * 0.1F) * 0.10F;
                this.leftArm.xRot = -0.15F - Mth.sin(t * 0.1F) * 0.10F;
                this.rightArm.zRot = 0.22F;
                this.leftArm.zRot = -0.22F;
            }
        }
        this.hat.copyFrom(this.head);
    }
}
