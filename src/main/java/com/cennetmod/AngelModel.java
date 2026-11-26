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

public class AngelModel extends EntityModel<AngelSoldier> {
   public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("cennetmod", "melek_asker"), "main");
   private final ModelPart root;
   private final ModelPart head;
   private final ModelPart hairHat;
   private final ModelPart crown;
   private final ModelPart helmetCrest;
   private final ModelPart leftEar;
   private final ModelPart rightEar;
   private final ModelPart halo;
   private final ModelPart body;
   private final ModelPart chestArmor;
   private final ModelPart belt;
   private final ModelPart cape;
   private final ModelPart quiver;
   private final ModelPart rArm;
   private final ModelPart rPauldron;
   private final ModelPart lArm;
   private final ModelPart lPauldron;
   private final ModelPart spear;
   private final ModelPart elfBow;
   private final ModelPart mageStaff;
   private final ModelPart battleAxe;
   private final ModelPart shield;
   private final ModelPart rLeg;
   private final ModelPart lLeg;
   private final ModelPart lWingInner;
   private final ModelPart lWingOuter;
   private final ModelPart rWingInner;
   private final ModelPart rWingOuter;
   private final ModelPart pegasusRoot;
   private final ModelPart pHead;
   private final ModelPart pLegFL;
   private final ModelPart pLegFR;
   private final ModelPart pLegBL;
   private final ModelPart pLegBR;
   private final ModelPart pWheelL;
   private final ModelPart pWheelR;
   private final ModelPart pWheelBL;
   private final ModelPart pWheelBR;
   private final ModelPart eyeClusterRoot;

   public AngelModel(ModelPart var1) {
      this.root = var1;
      this.head = var1.getChild("head");
      this.hairHat = this.head.getChild("hair_hat");
      this.crown = this.head.getChild("crown");
      this.helmetCrest = this.head.getChild("helmet_crest");
      this.leftEar = this.head.getChild("left_ear");
      this.rightEar = this.head.getChild("right_ear");
      this.halo = var1.getChild("halo");
      this.body = var1.getChild("body");
      this.chestArmor = this.body.getChild("chest_armor");
      this.belt = this.body.getChild("belt");
      this.cape = this.body.getChild("cape");
      this.quiver = this.body.getChild("quiver");
      this.rArm = var1.getChild("right_arm");
      this.rPauldron = this.rArm.getChild("right_pauldron");
      this.spear = this.rArm.getChild("spear");
      this.elfBow = this.rArm.getChild("elf_bow");
      this.mageStaff = this.rArm.getChild("mage_staff");
      this.battleAxe = this.rArm.getChild("battle_axe");
      this.lArm = var1.getChild("left_arm");
      this.lPauldron = this.lArm.getChild("left_pauldron");
      this.shield = this.lArm.getChild("shield");
      this.rLeg = var1.getChild("right_leg");
      this.lLeg = var1.getChild("left_leg");
      this.lWingInner = var1.getChild("left_wing_inner");
      this.lWingOuter = this.lWingInner.getChild("left_wing_outer");
      this.rWingInner = var1.getChild("right_wing_inner");
      this.rWingOuter = this.rWingInner.getChild("right_wing_outer");
      this.pegasusRoot = var1.getChild("pegasus_root");
      this.pHead = this.pegasusRoot.getChild("p_head");
      this.pLegFL = this.pegasusRoot.getChild("p_leg_fl");
      this.pLegFR = this.pegasusRoot.getChild("p_leg_fr");
      this.pLegBL = this.pegasusRoot.getChild("p_leg_bl");
      this.pLegBR = this.pegasusRoot.getChild("p_leg_br");
      this.pWheelL = this.pegasusRoot.getChild("p_wheel_l");
      this.pWheelR = this.pegasusRoot.getChild("p_wheel_r");
      this.pWheelBL = this.pegasusRoot.getChild("p_wheel_bl");
      this.pWheelBR = this.pegasusRoot.getChild("p_wheel_br");
      this.eyeClusterRoot = var1.getChild("eye_cluster_root");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      PartDefinition var2 = var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -14.0F, -7.0F, 14.0F, 14.0F, 14.0F), PartPose.offset(0.0F, -24.0F, 0.0F));
      var2.addOrReplaceChild("hair_hat", CubeListBuilder.create().texOffs(60, 0).addBox(-7.0F, -14.0F, -7.0F, 14.0F, 14.0F, 14.0F, new CubeDeformation(0.55F)).texOffs(120, 0).addBox(-6.0F, -2.0F, 5.0F, 12.0F, 10.0F, 3.0F), PartPose.ZERO);
      var2.addOrReplaceChild("crown", CubeListBuilder.create().texOffs(0, 32).addBox(-7.5F, -18.0F, -7.5F, 15.0F, 5.0F, 15.0F, new CubeDeformation(0.25F)), PartPose.ZERO);
      var2.addOrReplaceChild("helmet_crest", CubeListBuilder.create().texOffs(64, 32).addBox(-1.5F, -22.0F, -7.0F, 3.0F, 7.0F, 16.0F), PartPose.ZERO);
      var2.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(108, 32).addBox(0.0F, -2.0F, -1.0F, 6.0F, 3.0F, 2.0F).texOffs(108, 38).addBox(5.0F, -3.0F, -0.5F, 4.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(6.5F, -8.5F, -1.0F, -0.15F, -0.38F, -0.18F));
      var2.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(126, 32).addBox(-6.0F, -2.0F, -1.0F, 6.0F, 3.0F, 2.0F).texOffs(126, 38).addBox(-9.0F, -3.0F, -0.5F, 4.0F, 2.0F, 1.0F), PartPose.offsetAndRotation(-6.5F, -8.5F, -1.0F, -0.15F, 0.38F, 0.18F));
      var1.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(156, 0).addBox(-7.0F, -1.0F, -7.0F, 14.0F, 2.0F, 2.0F).texOffs(156, 5).addBox(-7.0F, -1.0F, 5.0F, 14.0F, 2.0F, 2.0F).texOffs(156, 10).addBox(-7.0F, -1.0F, -5.0F, 2.0F, 2.0F, 10.0F).texOffs(182, 10).addBox(5.0F, -1.0F, -5.0F, 2.0F, 2.0F, 10.0F).texOffs(156, 24).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.offset(0.0F, -45.0F, 0.0F));
      PartDefinition var3 = var1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 54).addBox(-7.0F, 0.0F, -4.0F, 14.0F, 24.0F, 8.0F), PartPose.offset(0.0F, -24.0F, 0.0F));
      var3.addOrReplaceChild("chest_armor", CubeListBuilder.create().texOffs(46, 54).addBox(-7.5F, 1.0F, -4.5F, 15.0F, 13.0F, 9.0F, new CubeDeformation(0.35F)), PartPose.ZERO);
      var3.addOrReplaceChild("belt", CubeListBuilder.create().texOffs(96, 54).addBox(-7.5F, 14.5F, -4.5F, 15.0F, 8.0F, 9.0F, new CubeDeformation(0.25F)), PartPose.ZERO);
      var3.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(146, 54).addBox(-6.5F, 0.0F, 0.0F, 13.0F, 26.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 1.0F, 4.2F, 0.14F, 0.0F, 0.0F));
      var3.addOrReplaceChild("quiver", CubeListBuilder.create().texOffs(178, 54).addBox(-2.5F, 0.0F, -2.0F, 5.0F, 15.0F, 4.0F).texOffs(198, 54).addBox(-1.5F, -4.0F, -1.0F, 3.0F, 4.0F, 2.0F), PartPose.offsetAndRotation(3.2F, 2.0F, 5.2F, 0.08F, 0.0F, -0.36F));
      PartDefinition var4 = var1.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 88).addBox(-5.0F, -2.0F, -3.0F, 6.0F, 24.0F, 6.0F).texOffs(108, 88).addBox(-5.5F, 10.0F, -3.5F, 7.0F, 8.0F, 7.0F, new CubeDeformation(0.2F)), PartPose.offset(-10.0F, -22.0F, 0.0F));
      var4.addOrReplaceChild("right_pauldron", CubeListBuilder.create().texOffs(52, 88).addBox(-6.0F, -3.5F, -4.0F, 7.0F, 7.0F, 8.0F, new CubeDeformation(0.35F)), PartPose.rotation(0.0F, 0.0F, -0.14F));
      var4.addOrReplaceChild("spear", CubeListBuilder.create().texOffs(0, 192).addBox(-1.0F, -2.0F, -24.0F, 2.0F, 2.0F, 42.0F).texOffs(90, 192).addBox(-3.0F, -3.0F, -26.0F, 6.0F, 4.0F, 2.0F).texOffs(108, 192).addBox(-1.0F, -4.0F, -36.0F, 2.0F, 6.0F, 10.0F), PartPose.offset(-2.0F, 19.0F, 0.0F));
      var4.addOrReplaceChild("elf_bow", CubeListBuilder.create().texOffs(136, 192).addBox(-1.0F, -4.0F, -3.0F, 2.0F, 8.0F, 2.0F).texOffs(146, 192).addBox(-1.0F, -14.0F, -1.0F, 2.0F, 10.0F, 2.0F).texOffs(156, 192).addBox(-1.0F, 4.0F, -1.0F, 2.0F, 10.0F, 2.0F).texOffs(166, 192).addBox(-0.5F, -13.0F, 1.0F, 1.0F, 26.0F, 1.0F).texOffs(172, 192).addBox(-0.5F, -1.0F, -14.0F, 1.0F, 2.0F, 15.0F), PartPose.offset(-2.0F, 18.0F, -2.0F));
      var4.addOrReplaceChild("mage_staff", CubeListBuilder.create().texOffs(0, 192).addBox(-1.0F, -2.0F, -22.0F, 2.0F, 2.0F, 38.0F).texOffs(186, 192).addBox(-3.5F, -4.5F, -28.0F, 7.0F, 7.0F, 6.0F).texOffs(214, 192).addBox(-2.0F, -3.0F, -33.0F, 4.0F, 4.0F, 5.0F), PartPose.offset(-2.0F, 19.0F, 0.0F));
      var4.addOrReplaceChild("battle_axe", CubeListBuilder.create().texOffs(0, 192).addBox(-1.0F, -2.0F, -22.0F, 2.0F, 2.0F, 36.0F).texOffs(210, 206).addBox(-1.5F, -9.0F, -25.0F, 3.0F, 16.0F, 9.0F).texOffs(236, 206).addBox(-1.0F, -3.0F, -30.0F, 2.0F, 4.0F, 5.0F), PartPose.offset(-2.0F, 19.0F, 0.0F));
      PartDefinition var5 = var1.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(26, 88).addBox(-1.0F, -2.0F, -3.0F, 6.0F, 24.0F, 6.0F).texOffs(138, 88).addBox(-1.5F, 10.0F, -3.5F, 7.0F, 8.0F, 7.0F, new CubeDeformation(0.2F)), PartPose.offset(10.0F, -22.0F, 0.0F));
      var5.addOrReplaceChild("left_pauldron", CubeListBuilder.create().texOffs(80, 88).addBox(-1.0F, -3.5F, -4.0F, 7.0F, 7.0F, 8.0F, new CubeDeformation(0.35F)), PartPose.rotation(0.0F, 0.0F, 0.14F));
      var5.addOrReplaceChild("shield", CubeListBuilder.create().texOffs(170, 88).addBox(0.0F, -10.0F, -6.0F, 2.0F, 20.0F, 12.0F).texOffs(200, 88).addBox(1.5F, -4.0F, -4.0F, 2.0F, 8.0F, 8.0F), PartPose.offset(4.5F, 13.0F, -1.0F));
      var1.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 122).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 24.0F, 6.0F).texOffs(52, 122).addBox(-3.5F, 11.0F, -3.5F, 7.0F, 13.0F, 7.0F, new CubeDeformation(0.25F)), PartPose.offset(-3.8F, 0.0F, 0.0F));
      var1.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(26, 122).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 24.0F, 6.0F).texOffs(82, 122).addBox(-3.5F, 11.0F, -3.5F, 7.0F, 13.0F, 7.0F, new CubeDeformation(0.25F)), PartPose.offset(3.8F, 0.0F, 0.0F));
      PartDefinition var6 = var1.addOrReplaceChild("left_wing_inner", CubeListBuilder.create().texOffs(0, 156).addBox(0.0F, -2.0F, 0.0F, 15.0F, 3.0F, 3.0F).texOffs(0, 164).addBox(0.0F, 1.0F, 0.5F, 15.0F, 18.0F, 2.0F), PartPose.offsetAndRotation(2.5F, -21.0F, 4.0F, 0.16F, -0.36F, -0.14F));
      var6.addOrReplaceChild("left_wing_outer", CubeListBuilder.create().texOffs(38, 156).addBox(0.0F, -2.0F, 0.0F, 20.0F, 2.0F, 2.0F).texOffs(38, 162).addBox(0.0F, 0.0F, 0.5F, 20.0F, 22.0F, 2.0F), PartPose.offsetAndRotation(15.0F, 0.0F, 0.0F, 0.04F, -0.22F, -0.08F));
      PartDefinition var7 = var1.addOrReplaceChild("right_wing_inner", CubeListBuilder.create().texOffs(88, 156).addBox(-15.0F, -2.0F, 0.0F, 15.0F, 3.0F, 3.0F).texOffs(88, 164).addBox(-15.0F, 1.0F, 0.5F, 15.0F, 18.0F, 2.0F), PartPose.offsetAndRotation(-2.5F, -21.0F, 4.0F, 0.16F, 0.36F, 0.14F));
      var7.addOrReplaceChild("right_wing_outer", CubeListBuilder.create().texOffs(126, 156).addBox(-20.0F, -2.0F, 0.0F, 20.0F, 2.0F, 2.0F).texOffs(126, 162).addBox(-20.0F, 0.0F, 0.5F, 20.0F, 22.0F, 2.0F), PartPose.offsetAndRotation(-15.0F, 0.0F, 0.0F, 0.04F, 0.22F, 0.08F));
      PartDefinition var8 = var1.addOrReplaceChild("pegasus_root", CubeListBuilder.create().texOffs(0, 236).addBox(-16.0F, -8.0F, -13.0F, 32.0F, 2.0F, 2.0F).texOffs(0, 236).addBox(-16.0F, -9.0F, 12.0F, 32.0F, 2.0F, 2.0F).texOffs(0, 236).addBox(-12.0F, -9.0F, -19.0F, 2.0F, 2.0F, 38.0F).texOffs(0, 236).addBox(10.0F, -9.0F, -19.0F, 2.0F, 2.0F, 38.0F).texOffs(20, 240).addBox(14.5F, -6.0F, -4.5F, 3.5F, 1.0F, 9.0F).texOffs(20, 240).addBox(-18.0F, -6.0F, -4.5F, 3.5F, 1.0F, 9.0F).texOffs(0, 0).addBox(-15.0F, -9.0F, -19.0F, 30.0F, 2.0F, 38.0F).texOffs(0, 58).addBox(-13.5F, -9.6F, -17.5F, 27.0F, 1.0F, 35.0F).texOffs(0, 62).addBox(3.0F, -14.0F, 6.0F, 10.0F, 5.0F, 10.5F).texOffs(0, 74).addBox(3.0F, -22.0F, 14.5F, 10.0F, 8.0F, 2.5F).texOffs(0, 238).addBox(2.0F, -16.8F, 6.5F, 1.5F, 3.0F, 9.5F).texOffs(0, 62).addBox(-13.0F, -14.0F, 6.0F, 10.0F, 5.0F, 10.5F).texOffs(0, 74).addBox(-13.0F, -22.0F, 14.5F, 10.0F, 8.0F, 2.5F).texOffs(0, 238).addBox(-3.5F, -16.8F, 6.5F, 1.5F, 3.0F, 9.5F).texOffs(0, 62).addBox(3.0F, -14.0F, -16.5F, 10.0F, 5.0F, 10.5F).texOffs(0, 74).addBox(3.0F, -22.0F, -17.0F, 10.0F, 8.0F, 2.5F).texOffs(0, 238).addBox(2.0F, -16.8F, -16.0F, 1.5F, 3.0F, 9.5F).texOffs(0, 62).addBox(-13.0F, -14.0F, -16.5F, 10.0F, 5.0F, 10.5F).texOffs(0, 74).addBox(-13.0F, -22.0F, -17.0F, 10.0F, 8.0F, 2.5F).texOffs(0, 238).addBox(-3.5F, -16.8F, -16.0F, 1.5F, 3.0F, 9.5F).texOffs(0, 12).addBox(-15.0F, -21.0F, -19.0F, 30.0F, 12.0F, 2.0F).texOffs(0, 12).addBox(-15.0F, -21.0F, 17.0F, 30.0F, 12.0F, 2.0F).texOffs(20, 28).addBox(-10.0F, -18.0F, 19.0F, 20.0F, 9.0F, 5.0F).texOffs(0, 4).addBox(13.0F, -21.0F, -19.0F, 2.0F, 12.0F, 38.0F).texOffs(0, 4).addBox(-15.0F, -21.0F, -19.0F, 2.0F, 12.0F, 38.0F).texOffs(10, 238).addBox(14.6F, -20.0F, -5.0F, 1.0F, 11.0F, 10.0F).texOffs(10, 238).addBox(-15.6F, -20.0F, -5.0F, 1.0F, 11.0F, 10.0F).texOffs(0, 236).addBox(12.5F, -36.0F, -19.0F, 3.0F, 15.0F, 3.0F).texOffs(0, 236).addBox(-15.5F, -36.0F, -19.0F, 3.0F, 15.0F, 3.0F).texOffs(0, 236).addBox(12.5F, -36.0F, 16.0F, 3.0F, 15.0F, 3.0F).texOffs(0, 236).addBox(-15.5F, -36.0F, 16.0F, 3.0F, 15.0F, 3.0F).texOffs(12, 236).addBox(13.0F, -36.0F, -6.5F, 2.0F, 15.0F, 2.0F).texOffs(12, 236).addBox(13.0F, -36.0F, 4.5F, 2.0F, 15.0F, 2.0F).texOffs(12, 236).addBox(-15.0F, -36.0F, -6.5F, 2.0F, 15.0F, 2.0F).texOffs(12, 236).addBox(-15.0F, -36.0F, 4.5F, 2.0F, 15.0F, 2.0F).texOffs(162, 100).addBox(13.2F, -36.0F, -16.0F, 1.5F, 4.0F, 32.0F).texOffs(162, 100).addBox(-14.7F, -36.0F, -16.0F, 1.5F, 4.0F, 32.0F).texOffs(162, 104).addBox(-12.5F, -36.0F, -18.8F, 25.0F, 4.0F, 1.5F).texOffs(162, 104).addBox(-12.5F, -36.0F, 17.3F, 25.0F, 4.0F, 1.5F).texOffs(0, 98).addBox(-16.5F, -38.5F, -20.5F, 33.0F, 2.5F, 41.0F).texOffs(8, 102).addBox(-13.5F, -41.0F, -17.5F, 27.0F, 2.5F, 35.0F).texOffs(20, 108).addBox(-7.5F, -43.0F, -10.5F, 15.0F, 2.0F, 21.0F).texOffs(0, 238).addBox(-2.0F, -46.0F, -2.0F, 4.0F, 3.0F, 4.0F).texOffs(220, 104).addBox(15.0F, -33.0F, -20.5F, 3.0F, 6.0F, 3.0F).texOffs(220, 104).addBox(-18.0F, -33.0F, -20.5F, 3.0F, 6.0F, 3.0F).texOffs(220, 104).addBox(15.0F, -33.0F, 17.5F, 3.0F, 6.0F, 3.0F).texOffs(220, 104).addBox(-18.0F, -33.0F, 17.5F, 3.0F, 6.0F, 3.0F).texOffs(0, 236).addBox(-5.5F, -16.0F, -40.0F, 1.5F, 2.0F, 21.0F).texOffs(0, 236).addBox(4.0F, -16.0F, -40.0F, 1.5F, 2.0F, 21.0F).texOffs(0, 238).addBox(-6.5F, -16.5F, -26.0F, 13.0F, 2.5F, 2.0F).texOffs(0, 212).addBox(-5.0F, -22.0F, -54.0F, 10.0F, 10.0F, 20.0F).texOffs(60, 212).addBox(-5.5F, -22.5F, -47.0F, 11.0F, 6.0F, 10.0F).texOffs(102, 212).addBox(-2.0F, -21.0F, -34.0F, 4.0F, 14.0F, 4.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
      var8.addOrReplaceChild("p_head", CubeListBuilder.create().texOffs(120, 212).addBox(-3.0F, -14.0F, -4.0F, 6.0F, 14.0F, 7.0F).texOffs(148, 212).addBox(-2.5F, -13.0F, -9.0F, 5.0F, 5.0F, 5.0F).texOffs(170, 212).addBox(-1.0F, -16.0F, 2.0F, 2.0F, 14.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -18.0F, -52.0F, 0.42F, 0.0F, 0.0F));
      var8.addOrReplaceChild("p_leg_fl", CubeListBuilder.create().texOffs(182, 212).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F), PartPose.offset(3.5F, -12.0F, -51.0F));
      var8.addOrReplaceChild("p_leg_fr", CubeListBuilder.create().texOffs(182, 212).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F), PartPose.offset(-3.5F, -12.0F, -51.0F));
      var8.addOrReplaceChild("p_leg_bl", CubeListBuilder.create().texOffs(182, 212).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F), PartPose.offset(3.5F, -12.0F, -37.0F));
      var8.addOrReplaceChild("p_leg_br", CubeListBuilder.create().texOffs(182, 212).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F), PartPose.offset(-3.5F, -12.0F, -37.0F));
      var8.addOrReplaceChild("p_wheel_l", CubeListBuilder.create().texOffs(196, 212).addBox(-1.0F, -7.0F, -7.0F, 2.0F, 14.0F, 14.0F), PartPose.offset(15.8F, -7.0F, -13.0F));
      var8.addOrReplaceChild("p_wheel_r", CubeListBuilder.create().texOffs(196, 212).addBox(-1.0F, -7.0F, -7.0F, 2.0F, 14.0F, 14.0F), PartPose.offset(-15.8F, -7.0F, -13.0F));
      var8.addOrReplaceChild("p_wheel_bl", CubeListBuilder.create().texOffs(196, 212).addBox(-1.0F, -8.0F, -8.0F, 2.0F, 16.0F, 16.0F), PartPose.offset(15.8F, -8.0F, 12.5F));
      var8.addOrReplaceChild("p_wheel_br", CubeListBuilder.create().texOffs(196, 212).addBox(-1.0F, -8.0F, -8.0F, 2.0F, 16.0F, 16.0F), PartPose.offset(-15.8F, -8.0F, 12.5F));
      var1.addOrReplaceChild("eye_cluster_root", CubeListBuilder.create().texOffs(0, 180).addBox(-6.5F, -8.0F, -9.2F, 6.0F, 4.0F, 2.0F).texOffs(24, 180).addBox(1.5F, -16.0F, -9.2F, 5.0F, 3.0F, 2.0F).texOffs(0, 196).addBox(-3.5F, -25.0F, -9.2F, 7.0F, 4.0F, 2.0F).texOffs(0, 180).addBox(-7.0F, -34.0F, -9.2F, 6.0F, 4.0F, 2.0F).texOffs(24, 180).addBox(1.0F, -43.0F, -9.2F, 5.0F, 3.0F, 2.0F).texOffs(0, 196).addBox(-4.0F, -52.0F, -9.2F, 7.0F, 4.0F, 2.0F).texOffs(0, 180).addBox(0.5F, -61.0F, -9.2F, 6.0F, 4.0F, 2.0F).texOffs(24, 180).addBox(-6.5F, -70.0F, -9.2F, 5.0F, 3.0F, 2.0F).texOffs(0, 196).addBox(-3.0F, -80.0F, -9.2F, 7.0F, 4.0F, 2.0F).texOffs(0, 180).addBox(0.5F, -10.0F, 7.2F, 6.0F, 4.0F, 2.0F).texOffs(24, 180).addBox(-6.5F, -19.0F, 7.2F, 5.0F, 3.0F, 2.0F).texOffs(0, 196).addBox(-3.5F, -28.0F, 7.2F, 7.0F, 4.0F, 2.0F).texOffs(0, 180).addBox(1.0F, -37.0F, 7.2F, 6.0F, 4.0F, 2.0F).texOffs(24, 180).addBox(-6.0F, -46.0F, 7.2F, 5.0F, 3.0F, 2.0F).texOffs(0, 196).addBox(-3.0F, -55.0F, 7.2F, 7.0F, 4.0F, 2.0F).texOffs(0, 180).addBox(-6.5F, -64.0F, 7.2F, 6.0F, 4.0F, 2.0F).texOffs(24, 180).addBox(1.5F, -73.0F, 7.2F, 5.0F, 3.0F, 2.0F).texOffs(0, 196).addBox(-3.5F, -82.0F, 7.2F, 7.0F, 4.0F, 2.0F).texOffs(48, 180).addBox(-9.2F, -9.0F, -6.5F, 2.0F, 4.0F, 6.0F).texOffs(72, 180).addBox(-9.2F, -18.0F, 1.5F, 2.0F, 3.0F, 5.0F).texOffs(28, 196).addBox(-9.2F, -27.0F, -3.5F, 2.0F, 4.0F, 7.0F).texOffs(48, 180).addBox(-9.2F, -36.0F, 0.5F, 2.0F, 4.0F, 6.0F).texOffs(72, 180).addBox(-9.2F, -45.0F, -6.5F, 2.0F, 3.0F, 5.0F).texOffs(28, 196).addBox(-9.2F, -54.0F, -3.0F, 2.0F, 4.0F, 7.0F).texOffs(48, 180).addBox(-9.2F, -63.0F, -6.0F, 2.0F, 4.0F, 6.0F).texOffs(72, 180).addBox(-9.2F, -72.0F, 1.0F, 2.0F, 3.0F, 5.0F).texOffs(28, 196).addBox(-9.2F, -81.0F, -3.5F, 2.0F, 4.0F, 7.0F).texOffs(48, 180).addBox(7.2F, -11.0F, 0.5F, 2.0F, 4.0F, 6.0F).texOffs(72, 180).addBox(7.2F, -20.0F, -6.5F, 2.0F, 3.0F, 5.0F).texOffs(28, 196).addBox(7.2F, -29.0F, -3.5F, 2.0F, 4.0F, 7.0F).texOffs(48, 180).addBox(7.2F, -38.0F, -6.5F, 2.0F, 4.0F, 6.0F).texOffs(72, 180).addBox(7.2F, -47.0F, 1.5F, 2.0F, 3.0F, 5.0F).texOffs(28, 196).addBox(7.2F, -56.0F, -3.5F, 2.0F, 4.0F, 7.0F).texOffs(48, 180).addBox(7.2F, -65.0F, 0.5F, 2.0F, 4.0F, 6.0F).texOffs(72, 180).addBox(7.2F, -74.0F, -6.0F, 2.0F, 3.0F, 5.0F).texOffs(28, 196).addBox(7.2F, -83.0F, -3.0F, 2.0F, 4.0F, 7.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
      return LayerDefinition.create(var0, 256, 256);
   }

   public void setupAnim(AngelSoldier var1, float var2, float var3, float var4, float var5, float var6) {
      int var7 = var1.getPoz();
      boolean var8 = var7 >= 50 && var7 < 60;
      boolean var9 = var7 == 61;
      boolean var10 = var7 == 60;
      boolean var11 = var7 == 62;
      boolean var12 = var7 == 63 || var7 == 65;
      this.pegasusRoot.visible = var8;
      this.eyeClusterRoot.visible = var9;
      boolean var13 = !var8 && !var9;
      this.head.visible = var13;
      this.body.visible = var13;
      this.rArm.visible = var13;
      this.lArm.visible = var13;
      this.rLeg.visible = var13;
      this.lLeg.visible = var13;
      if (var9) {
         this.halo.visible = false;
         this.lWingInner.visible = false;
         this.rWingInner.visible = false;
         boolean var29 = (int)var4 % 44 >= 41;
         this.eyeClusterRoot.visible = !var29;
         this.eyeClusterRoot.yRot = 0.0F;
         this.eyeClusterRoot.xRot = 0.0F;
      } else if (var12) {
         this.halo.visible = false;
         this.lWingInner.visible = false;
         this.rWingInner.visible = false;
         this.crown.visible = false;
         this.helmetCrest.visible = false;
         this.leftEar.visible = false;
         this.rightEar.visible = false;
         this.quiver.visible = false;
         this.rPauldron.visible = false;
         this.lPauldron.visible = false;
         this.spear.visible = false;
         this.elfBow.visible = false;
         this.mageStaff.visible = false;
         this.battleAxe.visible = false;
         this.shield.visible = false;
         this.head.y = -24.0F;
         this.body.y = -24.0F;
         this.body.xRot = 0.04F;
         this.head.yRot = Mth.clamp(var5, -55.0F, 55.0F) * ((float)Math.PI / 180F);
         this.head.xRot = Mth.clamp(var6, -35.0F, 35.0F) * ((float)Math.PI / 180F);
         this.head.zRot = 0.0F;
         float var28 = var7 == 63 ? 0.28F : 0.02F;
         float var31 = 0.12F;
         this.rLeg.xRot = Mth.cos(var4 * var31) * var28;
         this.lLeg.xRot = -Mth.cos(var4 * var31) * var28;
         this.rArm.xRot = -Mth.cos(var4 * var31) * var28 * 0.6F;
         this.lArm.xRot = Mth.cos(var4 * var31) * var28 * 0.6F;
         this.rArm.yRot = 0.0F;
         this.lArm.yRot = 0.0F;
         this.rArm.zRot = 0.04F;
         this.lArm.zRot = -0.04F;
      } else if (var10) {
         this.halo.visible = false;
         this.lWingInner.visible = false;
         this.rWingInner.visible = false;
         this.crown.visible = false;
         this.helmetCrest.visible = false;
         this.leftEar.visible = false;
         this.rightEar.visible = false;
         this.quiver.visible = false;
         this.rPauldron.visible = false;
         this.lPauldron.visible = false;
         this.spear.visible = false;
         this.elfBow.visible = false;
         this.mageStaff.visible = false;
         this.battleAxe.visible = false;
         this.shield.visible = false;
         this.head.y = -22.5F;
         this.body.y = -22.5F;
         this.body.xRot = 0.28F;
         this.head.yRot = Mth.clamp(var5, -50.0F, 50.0F) * ((float)Math.PI / 180F);
         this.head.xRot = 0.15F + Mth.sin(var4 * 0.18F) * 0.08F;
         this.head.zRot = Mth.sin(var4 * 0.12F) * 0.1F;
         this.rArm.xRot = -1.38F + Mth.sin(var4 * 0.25F) * 0.28F;
         this.lArm.xRot = -1.38F - Mth.sin(var4 * 0.25F) * 0.28F;
         this.rArm.yRot = -0.15F;
         this.lArm.yRot = 0.15F;
         this.rArm.zRot = 0.08F;
         this.lArm.zRot = -0.08F;
         this.rLeg.xRot = Mth.cos(var4 * 0.22F) * 0.45F;
         this.lLeg.xRot = -Mth.cos(var4 * 0.22F) * 0.45F;
      } else if (var11) {
         this.halo.visible = false;
         this.lWingInner.visible = false;
         this.rWingInner.visible = false;
         this.crown.visible = false;
         this.helmetCrest.visible = false;
         this.leftEar.visible = false;
         this.rightEar.visible = false;
         this.quiver.visible = false;
         this.rPauldron.visible = false;
         this.lPauldron.visible = false;
         this.spear.visible = false;
         this.elfBow.visible = false;
         this.mageStaff.visible = false;
         this.battleAxe.visible = false;
         this.shield.visible = false;
         this.head.y = -24.0F;
         this.body.y = -24.0F;
         this.body.xRot = 0.0F;
         this.head.yRot = Mth.clamp(var5, -65.0F, 65.0F) * ((float)Math.PI / 180F);
         this.head.xRot = Mth.clamp(var6, -40.0F, 40.0F) * ((float)Math.PI / 180F);
         this.head.zRot = 0.16F * Mth.sin(var4 * 0.05F);
         this.rArm.xRot = 0.03F;
         this.lArm.xRot = -0.03F;
         this.rArm.yRot = 0.0F;
         this.lArm.yRot = 0.0F;
         this.rArm.zRot = 0.04F;
         this.lArm.zRot = -0.04F;
         this.rLeg.xRot = 0.0F;
         this.lLeg.xRot = 0.0F;
      } else {
         this.head.zRot = 0.0F;
         if (var8) {
            this.halo.visible = false;
            this.lWingInner.visible = true;
            this.rWingInner.visible = true;
            this.lWingInner.y = 3.0F;
            this.rWingInner.y = 3.0F;
            this.lWingInner.z = -45.0F;
            this.rWingInner.z = -45.0F;
            boolean var27 = var7 == 50 || var7 == 52;
            float var30 = var27 ? 0.36F : 0.1F;
            float var32 = var27 ? 0.58F : 0.16F;
            float var33 = Mth.sin(var4 * var30) * var32;
            float var34 = Mth.sin(var4 * var30 - 0.6F) * var32 * 0.85F;
            this.lWingInner.xRot = 0.25F;
            this.rWingInner.xRot = 0.25F;
            this.lWingInner.yRot = -0.3F - var33;
            this.lWingOuter.yRot = -0.18F - var34;
            this.rWingInner.yRot = 0.3F + var33;
            this.rWingOuter.yRot = 0.18F + var34;
            this.lWingInner.zRot = -0.35F + var33 * 0.5F;
            this.rWingInner.zRot = 0.35F - var33 * 0.5F;
            float var35 = var27 ? 0.42F : 0.06F;
            float var36 = var27 ? 0.68F : 0.04F;
            this.pHead.xRot = (var7 == 52 ? 0.15F : 0.42F) + Mth.sin(var4 * var35) * (var27 ? 0.14F : 0.04F);
            this.pLegFL.xRot = Mth.cos(var4 * var35) * var36;
            this.pLegFR.xRot = Mth.cos(var4 * var35 + 0.6F) * var36;
            this.pLegBL.xRot = -Mth.cos(var4 * var35) * var36;
            this.pLegBR.xRot = -Mth.cos(var4 * var35 + 0.6F) * var36;
            float var37 = var27 ? var4 * 0.45F : 0.0F;
            this.pWheelL.xRot = var37;
            this.pWheelR.xRot = var37;
            this.pWheelBL.xRot = var37;
            this.pWheelBR.xRot = var37;
         } else {
            this.lWingInner.y = -21.0F;
            this.rWingInner.y = -21.0F;
            this.lWingInner.z = 4.0F;
            this.rWingInner.z = 4.0F;
            this.lWingInner.xRot = 0.16F;
            this.rWingInner.xRot = 0.16F;
            this.head.yRot = Mth.clamp(var5, -45.0F, 45.0F) * ((float)Math.PI / 180F);
            this.head.xRot = Mth.clamp(var6, -30.0F, 30.0F) * ((float)Math.PI / 180F);
            this.head.y = -24.0F;
            this.body.y = -24.0F;
            this.body.xRot = 0.0F;
            this.rLeg.y = 0.0F;
            this.lLeg.y = 0.0F;
            boolean var14 = var7 < 20;
            boolean var15 = var7 == 20 || var7 == 22;
            boolean var16 = var7 == 21 || var7 == 23;
            boolean var17 = var15 || var16;
            boolean var18 = var7 >= 30 && var7 < 40;
            boolean var19 = var7 >= 40;
            this.lWingInner.visible = var14;
            this.rWingInner.visible = var14;
            this.halo.visible = var14;
            this.leftEar.visible = var17;
            this.rightEar.visible = var17;
            this.quiver.visible = var15;
            this.crown.visible = var14 || var16 || var18;
            this.helmetCrest.visible = var7 < 10 || var18 || var19;
            this.rPauldron.visible = var7 < 10 || var18 || var19 || var16;
            this.lPauldron.visible = var7 < 10 || var18 || var19 || var16;
            this.spear.visible = var7 < 10 || var18;
            this.elfBow.visible = var15;
            this.mageStaff.visible = var16 || var7 == 10 || var7 == 11;
            this.battleAxe.visible = var19;
            this.shield.visible = var7 < 10 || var18 || var19;
            this.halo.y = -45.0F + Mth.sin(var4 * 0.1F) * 1.1F;
            this.halo.yRot = var4 * 0.035F;
            float var20 = var7 == 10 ? 0.22F : 0.12F;
            float var21 = var7 == 10 ? 0.46F : 0.24F;
            float var22 = Mth.sin(var4 * var20) * var21;
            float var23 = Mth.sin(var4 * var20 - 0.65F) * var21 * 0.85F;
            this.lWingInner.yRot = -0.34F - var22;
            this.lWingOuter.yRot = -0.2F - var23;
            this.rWingInner.yRot = 0.34F + var22;
            this.rWingOuter.yRot = 0.2F + var23;
            this.lWingInner.zRot = -0.12F + var22 * 0.25F;
            this.rWingInner.zRot = 0.12F - var22 * 0.25F;
            this.cape.xRot = 0.14F + Mth.sin(var4 * 0.09F) * 0.06F;
            if (var7 < 20) {
               if (var7 == 10) {
                  float var39 = Mth.sin(var4 * 0.11F);
                  this.body.xRot = 0.38F + var39 * 0.06F;
                  this.rLeg.xRot = 0.42F + Mth.sin(var4 * 0.14F) * 0.12F;
                  this.lLeg.xRot = 0.42F - Mth.sin(var4 * 0.14F) * 0.12F;
                  this.rArm.xRot = -0.35F + var39 * 0.12F;
                  this.lArm.xRot = -0.25F - var39 * 0.1F;
                  this.rArm.zRot = 0.28F;
                  this.lArm.zRot = -0.28F;
                  this.rArm.yRot = 0.0F;
                  this.lArm.yRot = 0.0F;
                  this.cape.xRot = 0.55F + Mth.abs(var39) * 0.22F;
               } else if (var7 == 11) {
                  float var38 = Mth.sin(var4 * 0.08F);
                  this.rLeg.xRot = 0.12F + var38 * 0.05F;
                  this.lLeg.xRot = 0.08F - var38 * 0.05F;
                  this.rArm.xRot = -0.48F + var38 * 0.05F;
                  this.lArm.xRot = -0.32F - var38 * 0.05F;
                  this.rArm.yRot = -0.1F;
                  this.lArm.yRot = 0.2F;
                  this.rArm.zRot = 0.1F;
                  this.lArm.zRot = -0.1F;
               } else {
                  this.rLeg.xRot = Mth.sin(var4 * 0.05F) * 0.03F;
                  this.lLeg.xRot = -Mth.sin(var4 * 0.05F) * 0.03F;
                  this.lArm.xRot = -0.45F;
                  this.lArm.yRot = 0.25F;
                  this.lArm.zRot = -0.08F;
                  if (var7 == 1) {
                     this.rArm.xRot = -1.25F;
                     this.rArm.yRot = -0.55F;
                     this.rArm.zRot = 0.1F;
                  } else if (var7 == 2) {
                     this.rArm.xRot = -0.95F;
                     this.rArm.yRot = -0.25F;
                     this.rArm.zRot = 0.0F;
                     this.head.xRot = 0.32F;
                  } else if (var7 == 3) {
                     this.rArm.xRot = -2.25F;
                     this.rArm.yRot = 0.0F;
                     this.rArm.zRot = 0.18F;
                  } else if (var7 == 4) {
                     this.rArm.xRot = -1.15F;
                     this.rArm.yRot = -0.15F;
                     this.rArm.zRot = 0.0F;
                  } else {
                     this.rArm.xRot = -0.42F + Mth.sin(var4 * 0.06F) * 0.04F;
                     this.rArm.yRot = 0.0F;
                     this.rArm.zRot = 0.06F;
                  }

               }
            } else {
               boolean var24 = var7 == 20 || var7 == 21 || var7 == 30 || var7 == 40 || var7 == 41;
               float var25 = var7 == 41 ? 0.34F : 0.16F;
               float var26 = var7 == 41 ? 0.72F : (var24 ? 0.42F : 0.04F);
               this.rLeg.xRot = Mth.cos(var4 * var25) * var26;
               this.lLeg.xRot = -Mth.cos(var4 * var25) * var26;
               this.rLeg.zRot = 0.0F;
               this.lLeg.zRot = 0.0F;
               this.cape.xRot = (var24 ? 0.28F : 0.14F) + Mth.abs(Mth.sin(var4 * var25)) * 0.18F;
               if (var7 == 41) {
                  this.rArm.xRot = -1.75F + Mth.sin(var4 * 0.55F) * 0.95F;
                  this.rArm.yRot = -0.18F;
                  this.rArm.zRot = 0.12F;
                  this.lArm.xRot = -0.78F + Mth.cos(var4 * 0.35F) * 0.25F;
                  this.lArm.yRot = 0.35F;
                  this.lArm.zRot = -0.15F;
               } else if (var15) {
                  this.rArm.xRot = -0.72F + Mth.sin(var4 * 0.08F) * 0.06F;
                  this.rArm.yRot = -0.12F;
                  this.rArm.zRot = 0.06F;
                  this.lArm.xRot = -Mth.cos(var4 * var25) * var26 * 0.65F;
                  this.lArm.yRot = 0.0F;
                  this.lArm.zRot = -0.06F;
               } else if (var16) {
                  this.rArm.xRot = -0.58F + Mth.sin(var4 * 0.07F) * 0.05F;
                  this.rArm.yRot = 0.0F;
                  this.rArm.zRot = 0.06F;
                  this.lArm.xRot = -0.25F - Mth.cos(var4 * var25) * var26 * 0.5F;
                  this.lArm.yRot = 0.15F;
                  this.lArm.zRot = -0.06F;
               } else if (var18) {
                  this.rArm.xRot = -0.52F + Mth.cos(var4 * var25) * var26 * 0.25F;
                  this.rArm.yRot = 0.0F;
                  this.rArm.zRot = 0.05F;
                  this.lArm.xRot = -0.48F;
                  this.lArm.yRot = 0.28F;
                  this.lArm.zRot = -0.08F;
               } else {
                  this.rArm.xRot = -0.62F + Mth.cos(var4 * var25) * var26 * 0.45F;
                  this.rArm.yRot = -0.1F;
                  this.rArm.zRot = 0.08F;
                  this.lArm.xRot = -0.45F - Mth.cos(var4 * var25) * var26 * 0.35F;
                  this.lArm.yRot = 0.22F;
                  this.lArm.zRot = -0.08F;
               }

            }
         }
      }
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
      int var6 = this.eyeClusterRoot.visible ? 15728880 : var3;
      this.root.render(var1, var2, var6, var4, var5);
   }
}
