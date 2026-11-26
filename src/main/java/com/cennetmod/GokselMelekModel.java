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

        // Varsayilan suzulme bacak pozu
        this.rightLeg.xRot = 0.22F + Mth.sin(t * 0.12F) * 0.08F;
        this.leftLeg.xRot = 0.35F - Mth.sin(t * 0.12F) * 0.08F;
        this.rightLeg.zRot = 0.06F;
        this.leftLeg.zRot = -0.06F;

        switch (anim) {
            case GokselMelekBoss.ANIM_INTRO -> {
                float p = Mth.clamp(e.getAnimTick() / 55.0F, 0.0F, 1.0F);
                this.head.xRot = -0.35F * p;
                this.rightArm.xRot = -0.5F * p;
                this.leftArm.xRot = -0.5F * p;
                this.rightArm.zRot = 1.35F * p;
                this.leftArm.zRot = -1.35F * p;
            }
            case GokselMelekBoss.ANIM_BARRAGE -> {
                this.rightArm.xRot = -1.55F + Mth.sin(t * 0.55F) * 0.35F;
                this.leftArm.xRot = -1.55F - Mth.sin(t * 0.55F) * 0.35F;
                this.rightArm.yRot = -0.2F;
                this.leftArm.yRot = 0.2F;
            }
            case GokselMelekBoss.ANIM_DOMAIN -> {
                this.head.xRot = -0.45F;
                this.rightArm.xRot = -2.45F;
                this.leftArm.xRot = -2.45F;
                this.rightArm.zRot = 0.55F + Mth.sin(t * 0.3F) * 0.1F;
                this.leftArm.zRot = -0.55F - Mth.sin(t * 0.3F) * 0.1F;
            }
            case GokselMelekBoss.ANIM_PHASE2_BREAK -> {
                int at = e.getAnimTick();
                if (at < 25) {
                    this.rightArm.xRot = -1.2F;
                    this.leftArm.xRot = -1.2F;
                    this.rightArm.yRot = 0.7F;
                    this.leftArm.yRot = -0.7F;
                } else {
                    this.head.xRot = -0.5F;
                    this.rightArm.xRot = -0.4F;
                    this.leftArm.xRot = -0.4F;
                    this.rightArm.zRot = 1.55F;
                    this.leftArm.zRot = -1.55F;
                }
            }
            case GokselMelekBoss.ANIM_BLINK_DASH -> {
                this.body.xRot = 0.4F;
                this.rightArm.xRot = 0.9F;
                this.leftArm.xRot = 0.9F;
                this.rightArm.zRot = 0.45F;
                this.leftArm.zRot = -0.45F;
                this.rightLeg.xRot = 0.75F;
                this.leftLeg.xRot = 0.65F;
            }
            case GokselMelekBoss.ANIM_MIRROR_RING -> {
                this.rightArm.xRot = -1.3F;
                this.leftArm.xRot = -1.3F;
                this.rightArm.zRot = 0.95F;
                this.leftArm.zRot = -0.95F;
            }
            default -> {
                this.rightArm.xRot = -0.15F + Mth.sin(t * 0.1F) * 0.12F;
                this.leftArm.xRot = -0.15F - Mth.sin(t * 0.1F) * 0.12F;
                this.rightArm.zRot = 0.25F + Mth.cos(t * 0.1F) * 0.06F;
                this.leftArm.zRot = -0.25F - Mth.cos(t * 0.1F) * 0.06F;
            }
        }
        this.hat.copyFrom(this.head);
    }
}
