package com.cennetmod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Melek asker modeli. Ölçek: 64 birim boy x 1.5 (renderer) = 96 px = 6 blok. */
public class AngelModel extends EntityModel<AngelSoldier> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(CennetMod.MODID, "melek_asker"), "main");

    private final ModelPart root, head, body, rArm, lArm, rLeg, lLeg, wingR, wingL;

    public AngelModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rArm = root.getChild("right_arm");
        this.lArm = root.getChild("left_arm");
        this.rLeg = root.getChild("right_leg");
        this.lLeg = root.getChild("left_leg");
        this.wingR = root.getChild("wing_r");
        this.wingL = root.getChild("wing_l");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition r = mesh.getRoot();

        r.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 32).addBox(-8, -24, -4, 16, 24, 8)
                .texOffs(0, 64).addBox(-8, -24, -4, 16, 12, 8, new CubeDeformation(0.6f)),
                PartPose.offset(0, 0, 0));

        PartDefinition head = r.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8, -16, -8, 16, 16, 16),
                PartPose.offset(0, -24, 0));
        head.addOrReplaceChild("halo", CubeListBuilder.create()
                .texOffs(64, 84).addBox(-10, 0, -10, 20, 2, 3)
                .texOffs(64, 84).addBox(-10, 0, 7, 20, 2, 3)
                .texOffs(64, 84).addBox(-10, 0, -7, 3, 2, 14)
                .texOffs(64, 84).addBox(7, 0, -7, 3, 2, 14),
                PartPose.offset(0, -22, 0));

        PartDefinition ra = r.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-6, -4, -4, 8, 24, 8),
                PartPose.offset(-10, -20, 0));
        ra.addOrReplaceChild("pauldron", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-7, -7, -5, 10, 6, 10), PartPose.ZERO);

        PartDefinition la = r.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(96, 0).addBox(-2, -4, -4, 8, 24, 8),
                PartPose.offset(10, -20, 0));
        la.addOrReplaceChild("pauldron", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-3, -7, -5, 10, 6, 10), PartPose.ZERO);

        r.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(64, 32).addBox(-4, 0, -4, 8, 24, 8), PartPose.offset(-4, 0, 0));
        r.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(96, 32).addBox(-4, 0, -4, 8, 24, 8), PartPose.offset(4, 0, 0));

        // kanatlar (üç sıra tüy)
        r.addOrReplaceChild("wing_l", CubeListBuilder.create()
                .texOffs(0, 84).addBox(2, -14, 0, 26, 10, 1)
                .texOffs(0, 96).addBox(4, -4, 0, 24, 8, 1)
                .texOffs(0, 106).addBox(6, 4, 0, 20, 8, 1),
                PartPose.offset(4, -20, 4.5f));
        r.addOrReplaceChild("wing_r", CubeListBuilder.create()
                .texOffs(0, 84).addBox(-28, -14, 0, 26, 10, 1)
                .texOffs(0, 96).addBox(-28, -4, 0, 24, 8, 1)
                .texOffs(0, 106).addBox(-26, 4, 0, 20, 8, 1),
                PartPose.offset(-4, -20, 4.5f));

        // mızrak (yere dikili)
        r.addOrReplaceChild("spear", CubeListBuilder.create()
                .texOffs(120, 64).addBox(-1, -34, -7, 2, 58, 2)
                .texOffs(104, 64).addBox(-3, -48, -6.5f, 6, 14, 1),
                PartPose.offset(-12, 0, 0));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(AngelSoldier e, float limbSwing, float limbSwingAmount, float age, float netHeadYaw, float headPitch) {
        int poz = e.getPoz();
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        head.xRot = headPitch * Mth.DEG_TO_RAD;

        rArm.xRot = -0.25f; rArm.zRot = 0.05f; rArm.yRot = 0f;
        lArm.xRot = 0.05f + Mth.sin(age * 0.05f) * 0.02f; lArm.zRot = -0.08f; lArm.yRot = 0f;

        float flap = Mth.sin(age * 0.07f) * 0.09f;
        wingL.zRot = 0.12f + flap; wingL.yRot = -0.25f;
        wingR.zRot = -0.12f - flap; wingR.yRot = 0.25f;

        switch (poz) {
            case 1 -> { // dur: sol el ileri uzanır
                lArm.xRot = -1.5f + Mth.sin(age * 0.2f) * 0.03f;
                lArm.zRot = 0f;
            }
            case 2 -> { // inceleme: iki el önde, baş eğik
                lArm.xRot = -1.05f; lArm.zRot = -0.25f;
                rArm.xRot = -1.0f; rArm.zRot = 0.25f;
                head.xRot += 0.45f;
                wingL.zRot += 0.1f; wingR.zRot -= 0.1f;
            }
            case 3 -> { // karşılama: baş eğilir, el yana açılır
                head.xRot += 0.35f;
                lArm.xRot = -0.35f; lArm.zRot = -1.0f;
                wingL.zRot += 0.25f; wingR.zRot -= 0.25f;
            }
            default -> { }
        }
        lLeg.xRot = 0f;
        rLeg.xRot = 0f;
    }

    @Override
    public void renderToBuffer(PoseStack ps, VertexConsumer vc, int light, int overlay, int color) {
        root.render(ps, vc, light, overlay, color);
    }
}
