package com.cennetmod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.lang.reflect.Field;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = "cennetmod", value = Dist.CLIENT)
public class OzelEfektSistemi {
    // 3D VFX turleri
    private static final int FX_SLASH_RIBBON = 0;
    private static final int FX_CHAIN_WHIRL = 1;
    private static final int FX_CHAIN_HOOK = 2;
    private static final int FX_RING_WAVE = 3;
    private static final int FX_BEAM_LANCE = 4;
    private static final int FX_HEX_SHIELD = 5;
    private static final int FX_DIM_CUTS = 6;
    private static final int FX_CHAIN_CAGE = 7;
    private static final int FX_CRESCENT_PROJ = 8;
    private static final int FX_SWORD_SPIN_CLEAVE = 9; // Kilic vucut donusu + 85 derece dikey yarik!
    private static final int FX_RIFT_PORTAL = 10;      // Yarik Asasi sade 3D boyut yarigi!

    // Karakter dovus animasyonu turleri
    private static final int ANIM_NONE = 0;
    private static final int ANIM_SW_COMBO1 = 1;
    private static final int ANIM_SW_COMBO2 = 2;
    private static final int ANIM_SW_COMBO3 = 3;
    private static final int ANIM_SW_COMBO4 = 4;
    private static final int ANIM_KU_COMBO1 = 5;
    private static final int ANIM_KU_COMBO2 = 6;
    private static final int ANIM_KU_COMBO3 = 7;
    private static final int ANIM_KU_COMBO4 = 8;
    private static final int ANIM_SW_SPIN_85 = 10;     // 3 tur vucut donusu + 85 derece indirme
    private static final int ANIM_KU_WHIRL = 11;       // Cift orak kasirga donusu
    private static final int ANIM_PARRY = 12;          // Savusturma / Muhur
    private static final int ANIM_THRUST = 13;         // Mizrak / Yildirim hamlesi
    private static final int ANIM_SLAM = 14;           // Meteor / Zincir celmesi
    private static final int ANIM_HOOK = 15;           // Yilan kancasi atis
    private static final int ANIM_FLURRY = 16;         // Kizil bicici hizli cift kesik
    private static final int ANIM_RIFT_CAST = 17;      // Yarik asasi isinlanma pozu

    private static class Vfx {
        int kind;
        Vec3 origin, dir;
        double startSec, durSec;
        float radius, tiltDeg;
        int r, g, b;
        boolean followPlayer;
        Vfx(int kind, Vec3 o, Vec3 d, double dur, float rad, float tilt, int r, int g, int b, boolean follow) {
            this.kind = kind; this.origin = o; this.dir = d;
            this.startSec = now(); this.durSec = dur;
            this.radius = rad; this.tiltDeg = tilt;
            this.r = r; this.g = g; this.b = b; this.followPlayer = follow;
        }
    }

    private static final List<Vfx> FX_LIST = new ArrayList<>();
    private static boolean wasSwinging = false;
    private static int lastSwingTime = 0;
    private static int localCombo = 0;
    private static double lastComboSec = 0;

    private static int activeAnim = ANIM_NONE;
    private static double animStartSec = 0;
    private static double animDurSec = 0.30;

    private static Field layersField = null;
    private static boolean layersSearched = false;

    private static double now() {
        return System.nanoTime() * 1.0e-9;
    }

    private static void playAnim(int kind, double dur) {
        activeAnim = kind;
        animStartSec = now();
        animDurSec = dur;
    }

    private static boolean isAnimPlaying() {
        return activeAnim != ANIM_NONE && (now() - animStartSec) < animDurSec;
    }

    @SuppressWarnings("unchecked")
    private static List<RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>> getLayers(PlayerRenderer renderer) {
        if (!layersSearched) {
            layersSearched = true;
            for (Field f : LivingEntityRenderer.class.getDeclaredFields()) {
                if (List.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    layersField = f;
                    break;
                }
            }
        }
        if (layersField != null) {
            try {
                return (List<RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>>) layersField.get(renderer);
            } catch (Exception ignored) {}
        }
        return Collections.emptyList();
    }

    private static void quad(VertexConsumer vc, Matrix4f m,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             int r, int g, int b, int a) {
        if (a <= 2) return;
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(m, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(m, x3, y3, z3).setColor(r, g, b, a);
        vc.addVertex(m, x4, y4, z4).setColor(r, g, b, a);
        vc.addVertex(m, x4, y4, z4).setColor(r, g, b, a);
        vc.addVertex(m, x3, y3, z3).setColor(r, g, b, a);
        vc.addVertex(m, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a);
    }

    private static void spawnSwingVfx(Player p, ItemStack stack) {
        double t = now();
        if (t - lastComboSec > 3.2) localCombo = 0;
        localCombo = (localCombo % 4) + 1;
        lastComboSec = t;

        Vec3 o = p.position().add(0, 1.15, 0);
        Vec3 look = p.getLookAngle().normalize();
        float tilt = switch (localCombo) {
            case 1 -> 18.0F;
            case 2 -> -38.0F;
            case 3 -> 48.0F;
            default -> 85.0F;
        };

        if (stack.getItem() instanceof TwinKusarigama) {
            playAnim(ANIM_KU_COMBO1 + (localCombo - 1), localCombo == 3 ? 0.44 : 0.36);
            FX_LIST.add(new Vfx(FX_SLASH_RIBBON, o, look, 0.36, 4.3F, tilt == 85.0F ? 22.0F : tilt, 170, 70, 255, true));
            FX_LIST.add(new Vfx(FX_CHAIN_WHIRL, o, look, 0.38, 4.2F, tilt == 85.0F ? -22.0F : tilt, 190, 95, 255, true));
            if (localCombo == 4) {
                FX_LIST.add(new Vfx(FX_SLASH_RIBBON, o, look, 0.42, 4.7F, -25.0F, 220, 120, 255, true));
                FX_LIST.add(new Vfx(FX_RING_WAVE, p.position().add(0, 0.25, 0), look, 0.45, 4.8F, 0, 165, 65, 255, false));
            }
        } else if (stack.getItem() instanceof YildizKilici) {
            int st = YildizKilici.getStyle(stack);
            playAnim(st == 4 ? ANIM_THRUST : (ANIM_SW_COMBO1 + (localCombo - 1)), localCombo == 4 ? 0.44 : 0.36);
            int[] col = switch (st) {
                case 0 -> new int[]{255, 225, 80};
                case 1 -> new int[]{90, 235, 255};
                case 2 -> new int[]{230, 245, 255};
                case 3 -> new int[]{255, 115, 35};
                case 4 -> new int[]{110, 195, 255};
                case 5 -> new int[]{120, 160, 255};
                default -> new int[]{195, 80, 255};
            };
            FX_LIST.add(new Vfx(FX_SLASH_RIBBON, o, look, 0.34, st == 1 ? 4.2F : 3.4F, tilt, col[0], col[1], col[2], true));
            if (st == 4) {
                FX_LIST.add(new Vfx(FX_BEAM_LANCE, p.getEyePosition().add(0, -0.25, 0), look, 0.24, 5.5F, 0, col[0], col[1], col[2], false));
            }
            if (localCombo == 4) {
                FX_LIST.add(new Vfx(FX_RING_WAVE, p.position().add(0, 0.25, 0), look, 0.45, 4.4F, 0, col[0], col[1], col[2], false));
            }
        }
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if (!event.getLevel().isClientSide()) return;
        Player p = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (p.getCooldowns().isOnCooldown(stack.getItem())) return;

        Vec3 o = p.position().add(0, 1.15, 0);
        Vec3 eye = p.getEyePosition().add(0, -0.2, 0);
        Vec3 look = p.getLookAngle().normalize();

        if (stack.getItem() instanceof YarikAsasi) {
            playAnim(ANIM_RIFT_CAST, 1.35);
            FX_LIST.add(new Vfx(FX_RIFT_PORTAL, o.add(look.scale(2.2)), look, 1.65, 2.4F, 0, 130, 215, 255, false));
            return;
        }

        if (p.isShiftKeyDown()) return;

        if (stack.getItem() instanceof TwinKusarigama) {
            int st = TwinKusarigama.getStyle(stack);
            switch (st) {
                case 0 -> {
                    playAnim(ANIM_KU_WHIRL, 2.4);
                    FX_LIST.add(new Vfx(FX_CHAIN_WHIRL, o, look, 2.4, 4.6F, 12.0F, 185, 75, 255, true));
                    FX_LIST.add(new Vfx(FX_CHAIN_WHIRL, o, look, 2.4, 4.3F, -18.0F, 130, 50, 240, true));
                }
                case 1 -> {
                    playAnim(ANIM_HOOK, 0.55);
                    FX_LIST.add(new Vfx(FX_CHAIN_HOOK, eye, look, 0.55, 12.0F, 0, 195, 90, 255, false));
                }
                case 2 -> {
                    playAnim(ANIM_FLURRY, 1.40);
                    FX_LIST.add(new Vfx(FX_DIM_CUTS, o.add(look.scale(3.0)), look, 1.40, 3.8F, 0, 240, 60, 160, false));
                }
                case 3 -> {
                    playAnim(ANIM_KU_COMBO3, 0.55);
                    FX_LIST.add(new Vfx(FX_RING_WAVE, p.position().add(0, 0.25, 0), look, 0.55, 4.8F, 0, 145, 95, 255, false));
                    FX_LIST.add(new Vfx(FX_CHAIN_WHIRL, p.position().add(0, 0.45, 0), look, 0.48, 4.6F, 0, 190, 90, 255, true));
                }
                case 4 -> {
                    playAnim(ANIM_PARRY, 1.0);
                    FX_LIST.add(new Vfx(FX_CHAIN_CAGE, p.position().add(0, 1.2, 0), look, 1.05, 5.4F, 0, 165, 55, 255, false));
                }
            }
        } else if (stack.getItem() instanceof YildizKilici) {
            int st = YildizKilici.getStyle(stack);
            switch (st) {
                case 0 -> {
                    playAnim(ANIM_THRUST, 0.50);
                    FX_LIST.add(new Vfx(FX_BEAM_LANCE, eye, look, 0.65, 9.5F, 0, 255, 230, 80, false));
                    FX_LIST.add(new Vfx(FX_SLASH_RIBBON, o, look, 0.45, 4.0F, 22.0F, 255, 240, 120, true));
                }
                case 1 -> {
                    playAnim(ANIM_SW_COMBO1, 0.45);
                    FX_LIST.add(new Vfx(FX_CRESCENT_PROJ, eye.add(look.scale(1.2)), look, 1.10, 3.4F, 0, 90, 235, 255, false));
                }
                case 2 -> { // KASIRGA DANSI: 1.2 sn vucut donusu + 0.6 sn 85 derece dikey yarik!
                    playAnim(ANIM_SW_SPIN_85, 1.85);
                    FX_LIST.add(new Vfx(FX_SWORD_SPIN_CLEAVE, o, look, 1.85, 4.6F, 85.0F, 130, 235, 255, true));
                }
                case 3 -> {
                    playAnim(ANIM_SLAM, 1.05);
                    FX_LIST.add(new Vfx(FX_RING_WAVE, p.position().add(0, 0.3, 0), look, 0.90, 6.2F, 0, 255, 115, 30, false));
                }
                case 4 -> {
                    playAnim(ANIM_THRUST, 0.55);
                    FX_LIST.add(new Vfx(FX_BEAM_LANCE, eye, look, 0.55, 14.0F, 0, 95, 215, 255, false));
                }
                case 5 -> {
                    playAnim(ANIM_PARRY, 1.20);
                    FX_LIST.add(new Vfx(FX_HEX_SHIELD, o, look, 1.20, 1.85F, 0, 110, 210, 255, true));
                }
                case 6 -> {
                    playAnim(ANIM_FLURRY, 1.40);
                    FX_LIST.add(new Vfx(FX_DIM_CUTS, eye.add(look.scale(6.0)), look, 1.40, 4.5F, 0, 195, 75, 255, false));
                }
            }
        }
    }

    // =========================================================
    // 3. SAHIS TAM VUCUT KARAKTER DOVUS ANIMASYONLARI
    // =========================================================
    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) return;
        if (!(event.getEntity() instanceof AbstractClientPlayer acp)) return;
        if (acp != mc.player) return;
        if (acp.isPassenger() || acp.isSwimming() || acp.isFallFlying() || acp.isSleeping()) return;

        ItemStack held = acp.getMainHandItem();
        boolean holdingKus = held.getItem() instanceof TwinKusarigama;
        boolean playing = isAnimPlaying();
        if (!playing && !holdingKus) return;

        float pt = event.getPartialTick();
        PoseStack pose = event.getPoseStack();
        MultiBufferSource buf = event.getMultiBufferSource();
        int light = event.getPackedLight();
        PlayerRenderer renderer = event.getRenderer();
        PlayerModel<AbstractClientPlayer> model = renderer.getModel();

        float elapsed = (float)(now() - animStartSec);
        float p = playing ? Mth.clamp(elapsed / (float) animDurSec, 0.0F, 1.0F) : 0.0F;
        float s = Mth.sin(p * (float) Math.PI);

        // Vucut donusu, egimi ve sicrama hesabi
        float bodySpinDeg = 0.0F;
        float bodyLeanDeg = 0.0F;
        float bodyRollDeg = 0.0F;
        float hopY = 0.0F;

        if (playing) {
            switch (activeAnim) {
                case ANIM_SW_COMBO1 -> { bodySpinDeg = Mth.lerp(p, -42.0F, 52.0F); bodyLeanDeg = 14.0F * s; }
                case ANIM_SW_COMBO2 -> { bodySpinDeg = Mth.lerp(p, 48.0F, -45.0F); bodyLeanDeg = -10.0F * s; hopY = 0.12F * s; }
                case ANIM_SW_COMBO3 -> { bodySpinDeg = -32.0F * s; bodyLeanDeg = 22.0F * s; }
                case ANIM_SW_COMBO4 -> {
                    float up = p < 0.38F ? (p / 0.38F) : (1.0F - (p - 0.38F) / 0.62F);
                    hopY = 0.32F * up;
                    bodyLeanDeg = p < 0.38F ? -18.0F * up : 34.0F * ((p - 0.38F) / 0.62F);
                }
                case ANIM_KU_COMBO1 -> { bodySpinDeg = Mth.lerp(p, -55.0F, 65.0F); bodyLeanDeg = 16.0F * s; }
                case ANIM_KU_COMBO2 -> { bodySpinDeg = Mth.lerp(p, 55.0F, -65.0F); bodyLeanDeg = 16.0F * s; }
                case ANIM_KU_COMBO3 -> { bodySpinDeg = p * 360.0F; bodyLeanDeg = 24.0F; hopY = -0.15F * s; }
                case ANIM_KU_COMBO4 -> { hopY = 0.38F * s; bodyLeanDeg = 20.0F * s; }
                case ANIM_SW_SPIN_85 -> {
                    if (p < 0.65F) {
                        float sp = p / 0.65F;
                        bodySpinDeg = sp * 1080.0F; // 3 tam tur vucut + kilic donusu!
                        bodyLeanDeg = 14.0F;
                    } else if (p < 0.80F) {
                        float up = (p - 0.65F) / 0.15F;
                        hopY = 0.65F * up;
                        bodyLeanDeg = -22.0F * up;
                    } else {
                        float dn = (p - 0.80F) / 0.20F;
                        hopY = 0.65F * (1.0F - dn);
                        bodyLeanDeg = Mth.lerp(dn, -22.0F, 36.0F);
                    }
                }
                case ANIM_KU_WHIRL -> { bodySpinDeg = elapsed * 840.0F; bodyLeanDeg = 18.0F; }
                case ANIM_PARRY -> { bodySpinDeg = -28.0F * s; bodyLeanDeg = 10.0F * s; }
                case ANIM_THRUST -> { bodySpinDeg = -45.0F * s; bodyLeanDeg = 26.0F * s; }
                case ANIM_SLAM -> {
                    float up = p < 0.45F ? (p / 0.45F) : (1.0F - (p - 0.45F) / 0.55F);
                    hopY = 0.35F * up;
                    bodyLeanDeg = p < 0.45F ? -20.0F * up : 38.0F * ((p - 0.45F) / 0.55F);
                }
                case ANIM_HOOK -> { bodySpinDeg = Mth.lerp(p, -35.0F, 35.0F); bodyLeanDeg = 18.0F * s; }
                case ANIM_FLURRY -> { bodySpinDeg = Mth.sin(elapsed * 26.0F) * 42.0F; bodyLeanDeg = 22.0F; }
                case ANIM_RIFT_CAST -> { hopY = 0.28F * s; bodyLeanDeg = -8.0F * s; }
            }
        }

        pose.pushPose();
        float bodyYaw = Mth.rotLerp(pt, acp.yBodyRotO, acp.yBodyRot);
        float headYaw = Mth.rotLerp(pt, acp.yHeadRotO, acp.yHeadRot);
        float netHeadYaw = headYaw - bodyYaw;
        float headPitch = Mth.lerp(pt, acp.xRotO, acp.getXRot());
        float ageInTicks = acp.tickCount + pt;
        float limbSwing = acp.walkAnimation.position(pt);
        float limbAmt = acp.walkAnimation.speed(pt);

        pose.translate(0.0F, hopY, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw + bodySpinDeg));
        if (bodyLeanDeg != 0.0F) pose.mulPose(Axis.XP.rotationDegrees(-bodyLeanDeg));
        if (bodyRollDeg != 0.0F) pose.mulPose(Axis.ZP.rotationDegrees(bodyRollDeg));
        pose.scale(-1.0F, -1.0F, 1.0F);
        pose.scale(0.9375F, 0.9375F, 0.9375F);
        pose.translate(0.0F, -1.501F, 0.0F);

        model.setAllVisible(true);
        model.hat.visible = acp.isModelPartShown(PlayerModelPart.HAT);
        model.jacket.visible = acp.isModelPartShown(PlayerModelPart.JACKET);
        model.leftPants.visible = acp.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
        model.rightPants.visible = acp.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
        model.leftSleeve.visible = acp.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
        model.rightSleeve.visible = acp.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
        model.crouching = acp.isCrouching();
        model.attackTime = 0.0F;
        model.rightArmPose = HumanoidModel.ArmPose.ITEM;
        model.leftArmPose = holdingKus ? HumanoidModel.ArmPose.ITEM : HumanoidModel.ArmPose.EMPTY;
        model.setupAnim(acp, limbSwing, limbAmt, ageInTicks, playing ? 0.0F : netHeadYaw, headPitch);

        // Uzuv (kol/bacak/govde) dovus pozlari
        if (playing) {
            switch (activeAnim) {
                case ANIM_SW_COMBO1 -> {
                    model.rightArm.xRot = -1.45F;
                    model.rightArm.yRot = Mth.lerp(p, -1.25F, 1.15F);
                    model.rightArm.zRot = 0.45F;
                    model.leftArm.xRot = -0.35F;
                    model.leftArm.zRot = -0.65F;
                    model.rightLeg.xRot = -0.45F * s;
                    model.leftLeg.xRot = 0.55F * s;
                }
                case ANIM_SW_COMBO2 -> {
                    model.rightArm.xRot = Mth.lerp(p, -0.25F, -2.35F);
                    model.rightArm.yRot = Mth.lerp(p, 0.85F, -0.75F);
                    model.rightArm.zRot = 0.30F;
                    model.leftArm.xRot = -0.55F;
                    model.leftArm.zRot = -0.75F;
                }
                case ANIM_SW_COMBO3 -> {
                    model.rightArm.xRot = -1.62F;
                    model.rightArm.yRot = -0.15F;
                    model.leftArm.xRot = 0.55F;
                    model.leftArm.zRot = -0.55F;
                    model.rightLeg.xRot = -0.65F * s;
                    model.leftLeg.xRot = 0.72F * s;
                }
                case ANIM_SW_COMBO4 -> {
                    float down = p < 0.38F ? Mth.lerp(p / 0.38F, -1.0F, -2.95F) : Mth.lerp((p - 0.38F) / 0.62F, -2.95F, -0.25F);
                    model.rightArm.xRot = down;
                    model.leftArm.xRot = down;
                    model.rightArm.yRot = -0.35F;
                    model.leftArm.yRot = 0.35F;
                    model.rightLeg.xRot = -0.50F * s;
                    model.leftLeg.xRot = 0.50F * s;
                }
                case ANIM_KU_COMBO1 -> {
                    model.rightArm.xRot = -1.35F;
                    model.rightArm.yRot = Mth.lerp(p, -1.35F, 1.10F);
                    model.rightArm.zRot = 0.85F;
                    model.leftArm.xRot = -0.85F;
                    model.leftArm.yRot = 0.75F;
                    model.rightLeg.xRot = -0.55F * s;
                    model.leftLeg.xRot = 0.55F * s;
                }
                case ANIM_KU_COMBO2 -> {
                    model.leftArm.xRot = -1.35F;
                    model.leftArm.yRot = Mth.lerp(p, 1.35F, -1.10F);
                    model.leftArm.zRot = -0.85F;
                    model.rightArm.xRot = -0.85F;
                    model.rightArm.yRot = -0.75F;
                    model.leftLeg.xRot = -0.55F * s;
                    model.rightLeg.xRot = 0.55F * s;
                }
                case ANIM_KU_COMBO3 -> {
                    model.rightArm.xRot = -1.25F;
                    model.leftArm.xRot = -1.25F;
                    model.rightArm.zRot = 1.45F;
                    model.leftArm.zRot = -1.45F;
                    model.rightLeg.xRot = -0.75F;
                    model.leftLeg.xRot = 0.75F;
                }
                case ANIM_KU_COMBO4 -> {
                    float spread = Mth.lerp(p, -0.75F, 1.35F);
                    model.rightArm.xRot = -1.65F + p * 0.9F;
                    model.leftArm.xRot = -1.65F + p * 0.9F;
                    model.rightArm.zRot = spread;
                    model.leftArm.zRot = -spread;
                    model.rightLeg.xRot = 0.45F * s;
                    model.leftLeg.xRot = -0.45F * s;
                }
                case ANIM_SW_SPIN_85 -> {
                    if (p < 0.65F) {
                        // Kilici yana acip vucutla birlikte hizla donme pozu!
                        model.rightArm.xRot = -1.52F;
                        model.rightArm.zRot = 1.38F;
                        model.leftArm.xRot = -0.55F;
                        model.leftArm.zRot = -0.95F;
                        model.rightLeg.xRot = -0.42F;
                        model.leftLeg.xRot = 0.42F;
                    } else if (p < 0.80F) {
                        // Iki elle kilici tepeye kaldirma!
                        float up = (p - 0.65F) / 0.15F;
                        model.rightArm.xRot = Mth.lerp(up, -1.52F, -3.05F);
                        model.leftArm.xRot = Mth.lerp(up, -0.55F, -3.05F);
                        model.rightArm.yRot = -0.35F * up;
                        model.leftArm.yRot = 0.35F * up;
                        model.rightLeg.xRot = 0.35F;
                        model.leftLeg.xRot = 0.55F;
                    } else {
                        // 85 DERECE YUKARIDAN ASAGIYA CAT DIYE INDIRME!
                        float dn = (p - 0.80F) / 0.20F;
                        float ang = Mth.lerp(dn, -3.05F, -0.18F);
                        model.rightArm.xRot = ang;
                        model.leftArm.xRot = ang;
                        model.rightArm.yRot = -0.32F;
                        model.leftArm.yRot = 0.32F;
                        model.rightArm.zRot = 0.08F; // 85 derece dikey-hafif acili
                        model.rightLeg.xRot = -0.68F;
                        model.leftLeg.xRot = 0.68F;
                    }
                }
                case ANIM_KU_WHIRL -> {
                    model.rightArm.xRot = -1.45F;
                    model.leftArm.xRot = -1.45F;
                    model.rightArm.zRot = 1.48F;
                    model.leftArm.zRot = -1.48F;
                    model.rightLeg.xRot = -0.45F;
                    model.leftLeg.xRot = 0.45F;
                }
                case ANIM_PARRY -> {
                    model.rightArm.xRot = -1.15F;
                    model.leftArm.xRot = -1.15F;
                    model.rightArm.yRot = -0.75F;
                    model.leftArm.yRot = 0.75F;
                    model.rightLeg.xRot = -0.35F;
                    model.leftLeg.xRot = 0.35F;
                }
                case ANIM_THRUST -> {
                    model.rightArm.xRot = -1.62F;
                    model.rightArm.yRot = 0.0F;
                    model.leftArm.xRot = 0.75F;
                    model.leftArm.zRot = -0.45F;
                    model.rightLeg.xRot = -0.72F * s;
                    model.leftLeg.xRot = 0.78F * s;
                }
                case ANIM_SLAM -> {
                    float ang = p < 0.45F ? -2.95F : Mth.lerp((p - 0.45F) / 0.55F, -2.95F, -0.25F);
                    model.rightArm.xRot = ang;
                    model.leftArm.xRot = ang;
                    model.rightArm.yRot = -0.32F;
                    model.leftArm.yRot = 0.32F;
                }
                case ANIM_HOOK -> {
                    model.rightArm.xRot = -1.58F;
                    model.rightArm.yRot = Mth.lerp(p, 0.25F, -0.55F);
                    model.leftArm.xRot = -0.75F;
                    model.leftArm.zRot = -0.85F;
                }
                case ANIM_FLURRY -> {
                    float alt = Mth.sin(elapsed * 32.0F);
                    model.rightArm.xRot = -1.45F + alt * 0.85F;
                    model.leftArm.xRot = -1.45F - alt * 0.85F;
                    model.rightArm.yRot = -0.55F + alt * 0.75F;
                    model.leftArm.yRot = 0.55F + alt * 0.75F;
                }
                case ANIM_RIFT_CAST -> {
                    model.rightArm.xRot = -2.10F * s;
                    model.leftArm.xRot = -0.90F * s;
                    model.leftArm.zRot = -0.65F * s;
                }
            }
        } else if (holdingKus) {
            // Twin Kusarigama bosta iken cift orak hazir shinobi durusu
            model.rightArm.xRot = -0.42F;
            model.rightArm.zRot = 0.24F;
            model.leftArm.xRot = -0.48F;
            model.leftArm.zRot = -0.28F;
        }

        model.hat.copyFrom(model.head);
        model.jacket.copyFrom(model.body);
        model.rightSleeve.copyFrom(model.rightArm);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightPants.copyFrom(model.rightLeg);
        model.leftPants.copyFrom(model.leftLeg);

        int overlay = LivingEntityRenderer.getOverlayCoords(acp, 0.0F);
        VertexConsumer vc = buf.getBuffer(RenderType.entityTranslucent(renderer.getTextureLocation(acp)));
        model.renderToBuffer(pose, vc, light, overlay);

        for (RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> layer : getLayers(renderer)) {
            layer.render(pose, buf, light, acp, limbSwing, limbAmt, pt, ageInTicks, playing ? 0.0F : netHeadYaw, headPitch);
        }

        // Twin Kusarigama eldeyken sol elde ikinci orak cizimi
        if (holdingKus && acp.getOffhandItem().isEmpty()) {
            pose.pushPose();
            model.translateToHand(HumanoidArm.LEFT, pose);
            pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
            pose.mulPose(Axis.YP.rotationDegrees(180.0F));
            pose.translate(-0.0625F, 0.125F, -0.625F);
            mc.getItemRenderer().renderStatic(
                acp, held, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, true, pose, buf, acp.level(), light, OverlayTexture.NO_OVERLAY, acp.getId()
            );
            pose.popPose();
        }

        pose.popPose();
        event.setCanceled(true);
    }

    // 1. SAHIS EL ANIMASYONLARI
    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof YildizKilici) && !(stack.getItem() instanceof TwinKusarigama)) return;
        if (!isAnimPlaying()) return;

        float elapsed = (float)(now() - animStartSec);
        float p = Mth.clamp(elapsed / (float) animDurSec, 0.0F, 1.0F);
        float s = Mth.sin(p * (float) Math.PI);
        PoseStack pose = event.getPoseStack();

        switch (activeAnim) {
            case ANIM_SW_COMBO1, ANIM_KU_COMBO1 -> {
                pose.translate(-0.55F * s, 0.12F * s, -0.35F * s);
                pose.mulPose(Axis.YP.rotationDegrees(75.0F * s));
                pose.mulPose(Axis.ZP.rotationDegrees(55.0F * s));
            }
            case ANIM_SW_COMBO2, ANIM_KU_COMBO2 -> {
                pose.translate(-0.35F + 0.70F * p, -0.15F + 0.35F * p, -0.30F * s);
                pose.mulPose(Axis.YP.rotationDegrees(-70.0F * s));
                pose.mulPose(Axis.ZP.rotationDegrees(-65.0F * s));
            }
            case ANIM_SW_COMBO3, ANIM_KU_COMBO3 -> {
                pose.translate(-0.25F * s, 0.18F * s, -0.45F * s);
                pose.mulPose(Axis.XP.rotationDegrees(-65.0F * s));
                pose.mulPose(Axis.ZP.rotationDegrees(180.0F * p));
            }
            case ANIM_SW_COMBO4, ANIM_KU_COMBO4, ANIM_SLAM -> {
                float slam = p < 0.38F ? (p / 0.38F) : (1.0F - (p - 0.38F) / 0.62F);
                pose.translate(-0.20F * s, 0.32F * slam - 0.38F * (1.0F - slam), -0.55F * s);
                pose.mulPose(Axis.XP.rotationDegrees(50.0F * slam - 115.0F * (1.0F - slam)));
            }
            case ANIM_SW_SPIN_85 -> {
                if (p < 0.65F) {
                    pose.translate(-0.25F, -0.05F, -0.35F);
                    pose.mulPose(Axis.YP.rotationDegrees(elapsed * 980.0F));
                    pose.mulPose(Axis.ZP.rotationDegrees(20.0F));
                } else {
                    float k = (p - 0.65F) / 0.35F;
                    float up = k < 0.42F ? (k / 0.42F) : (1.0F - (k - 0.42F) / 0.58F);
                    pose.translate(-0.12F, 0.42F * up - 0.45F * (1.0F - up), -0.55F * (1.0F - up));
                    pose.mulPose(Axis.XP.rotationDegrees(55.0F * up - 120.0F * (1.0F - up)));
                }
            }
            case ANIM_KU_WHIRL, ANIM_FLURRY -> {
                pose.translate(-0.25F, -0.05F, -0.35F);
                pose.mulPose(Axis.YP.rotationDegrees(elapsed * 920.0F));
                pose.mulPose(Axis.ZP.rotationDegrees(35.0F));
            }
            case ANIM_PARRY -> {
                pose.translate(-0.32F * s, 0.10F * s, -0.25F * s);
                pose.mulPose(Axis.ZP.rotationDegrees(62.0F * s));
                pose.mulPose(Axis.YP.rotationDegrees(45.0F * s));
            }
            case ANIM_THRUST, ANIM_HOOK -> {
                pose.translate(0.0F, 0.05F, -0.78F * s);
                pose.mulPose(Axis.XP.rotationDegrees(-78.0F * s));
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft mc = Minecraft.getInstance();
        Player p = mc.player;
        if (p == null || mc.level == null) return;

        ItemStack held = p.getMainHandItem();
        if (held.getItem() instanceof YildizKilici || held.getItem() instanceof TwinKusarigama) {
            boolean swingStart = p.swinging && (!wasSwinging || p.swingTime < lastSwingTime);
            if (swingStart) spawnSwingVfx(p, held);
            wasSwinging = p.swinging;
            lastSwingTime = p.swingTime;
        } else {
            wasSwinging = false;
        }

        double tNow = now();
        FX_LIST.removeIf(fx -> (tNow - fx.startSec) >= fx.durSec);
        if (FX_LIST.isEmpty()) return;

        Vec3 cam = event.getCamera().getPosition();
        PoseStack pose = new PoseStack();
        MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
        VertexConsumer vc = buf.getBuffer(RenderType.lightning());

        pose.pushPose();
        pose.translate(-cam.x, -cam.y, -cam.z);
        Matrix4f mat = pose.last().pose();

        for (Vfx fx : FX_LIST) {
            float prog = (float) Mth.clamp((tNow - fx.startSec) / fx.durSec, 0.0, 1.0);
            float fade = 1.0F - prog * prog;
            Vec3 base = fx.followPlayer ? p.position().add(0, 1.10, 0) : fx.origin;
            int alpha = (int) (220 * fade);

            switch (fx.kind) {
                case FX_SLASH_RIBBON -> drawRibbonArc(vc, mat, base, fx.dir, fx.radius, fx.tiltDeg, prog, fx.r, fx.g, fx.b, alpha);
                case FX_CHAIN_WHIRL -> drawChainWhirl(vc, mat, base, fx.radius, fx.tiltDeg, (float)(tNow - fx.startSec), fx.r, fx.g, fx.b, alpha);
                case FX_CHAIN_HOOK -> drawChainHook(vc, mat, fx.origin, fx.dir, fx.radius, prog, fx.r, fx.g, fx.b, alpha);
                case FX_RING_WAVE -> drawShockRing(vc, mat, base, fx.radius * (0.2F + 0.85F * prog), fx.r, fx.g, fx.b, alpha);
                case FX_BEAM_LANCE -> drawBeamLance(vc, mat, fx.origin, fx.dir, fx.radius, prog, fx.r, fx.g, fx.b, alpha);
                case FX_HEX_SHIELD -> drawHexShield(vc, mat, base.add(p.getLookAngle().normalize().scale(1.6)), p.getLookAngle().normalize(), fx.radius, (float)(tNow - fx.startSec), fx.r, fx.g, fx.b, alpha);
                case FX_DIM_CUTS -> drawDimCuts(vc, mat, fx.origin, fx.radius, prog, fx.r, fx.g, fx.b, alpha);
                case FX_CHAIN_CAGE -> drawChainCage(vc, mat, fx.origin, fx.radius, prog, fx.r, fx.g, fx.b, alpha);
                case FX_CRESCENT_PROJ -> drawRibbonArc(vc, mat, fx.origin.add(fx.dir.scale(prog * 22.0)), fx.dir, fx.radius, 0, 0.55F, fx.r, fx.g, fx.b, alpha);
                case FX_SWORD_SPIN_CLEAVE -> drawSwordSpinAnd85Cleave(vc, mat, base, p.getLookAngle().normalize(), fx.radius, prog, (float)(tNow - fx.startSec), fx.r, fx.g, fx.b);
                case FX_RIFT_PORTAL -> drawRiftPortal(vc, mat, fx.origin, fx.dir, fx.radius, prog, fx.r, fx.g, fx.b);
            }
        }

        pose.popPose();
        buf.endBatch(RenderType.lightning());
    }

    private static void drawSwordSpinAnd85Cleave(VertexConsumer vc, Matrix4f m, Vec3 o, Vec3 look, float rad, float prog, float elapsed, int r, int g, int b) {
        if (prog < 0.65F) {
            // 1. Faz: Saf kilic yatay donus seritleri (ZINCIRSIZ!)
            float ang = elapsed * 18.0F;
            Vec3 spinDir = new Vec3(Math.cos(ang), 0, Math.sin(ang));
            drawRibbonArc(vc, m, o, spinDir, rad * 0.88F, 6.0F, 0.55F, r, g, b, 200);
            drawRibbonArc(vc, m, o.add(0, 0.25, 0), spinDir.scale(-1), rad * 0.72F, -6.0F, 0.55F, 220, 248, 255, 165);
        } else if (prog >= 0.78F) {
            // 2. Faz: 85 DERECE YUKARIDAN ASAGIYA DEVASA ZIRH DELEN KESIK!
            float cp = (prog - 0.78F) / 0.22F;
            int a = (int)(245 * (1.0F - cp * cp));
            Vec3 flat = new Vec3(look.x, 0, look.z);
            if (flat.lengthSqr() < 1e-4) flat = new Vec3(0, 0, 1);
            flat = flat.normalize();
            Vec3 right = new Vec3(-flat.z, 0, flat.x);
            double tilt = Math.toRadians(85.0);
            int segs = 22;
            double startA = Math.toRadians(95.0);
            double endA = Math.toRadians(-35.0);
            float rBig = rad * 1.25F;
            for (int i = 0; i < segs; i++) {
                float f1 = i / (float) segs, f2 = (i + 1) / (float) segs;
                double a1 = Mth.lerp(f1, startA, endA), a2 = Mth.lerp(f2, startA, endA);
                float w1 = Mth.sin(f1 * (float) Math.PI) * 1.45F;
                float w2 = Mth.sin(f2 * (float) Math.PI) * 1.45F;
                Vec3 d1 = flat.scale(Math.cos(a1)).add(right.scale(Math.sin(a1) * Math.cos(tilt))).add(0, Math.sin(a1) * Math.sin(tilt), 0);
                Vec3 d2 = flat.scale(Math.cos(a2)).add(right.scale(Math.sin(a2) * Math.cos(tilt))).add(0, Math.sin(a2) * Math.sin(tilt), 0);
                Vec3 o1 = o.add(d1.scale(rBig)), i1 = o.add(d1.scale(rBig - w1));
                Vec3 o2 = o.add(d2.scale(rBig)), i2 = o.add(d2.scale(rBig - w2));
                quad(vc, m, (float)o1.x,(float)o1.y,(float)o1.z, (float)o2.x,(float)o2.y,(float)o2.z, (float)i2.x,(float)i2.y,(float)i2.z, (float)i1.x,(float)i1.y,(float)i1.z, 90, 215, 255, a);
                Vec3 c1 = o.add(d1.scale(rBig - w1 * 0.40)), c2 = o.add(d2.scale(rBig - w2 * 0.40));
                quad(vc, m, (float)o1.x,(float)o1.y,(float)o1.z, (float)o2.x,(float)o2.y,(float)o2.z, (float)c2.x,(float)c2.y,(float)c2.z, (float)c1.x,(float)c1.y,(float)c1.z, 255, 255, 255, a);
            }
            // Yere carpma sok halkasi
            drawShockRing(vc, m, o.add(flat.scale(2.8)).add(0, -0.85, 0), 1.2F + cp * 4.5F, 135, 235, 255, a);
        }
    }

    private static void drawRiftPortal(VertexConsumer vc, Matrix4f m, Vec3 c, Vec3 look, float rad, float prog, int r, int g, int b) {
        float open = prog < 0.25F ? (prog / 0.25F) : (prog > 0.75F ? (1.0F - prog) / 0.25F : 1.0F);
        int a = (int)(205 * open);
        Vec3 right = new Vec3(-look.z, 0, look.x);
        if (right.lengthSqr() < 1e-4) right = new Vec3(1, 0, 0);
        right = right.normalize();
        float h = rad * 1.35F * open;
        float w = rad * 0.42F * open;
        // Dikey boyut yarigi (ic beyaz + dis mavi-mor)
        Vec3 top = c.add(0, h, 0), bot = c.add(0, -h * 0.75, 0);
        Vec3 rgt = c.add(right.scale(w)), lft = c.subtract(right.scale(w));
        quad(vc, m, (float)top.x,(float)top.y,(float)top.z, (float)rgt.x,(float)rgt.y,(float)rgt.z, (float)bot.x,(float)bot.y,(float)bot.z, (float)lft.x,(float)lft.y,(float)lft.z, r, g, b, a);
        Vec3 rgtI = c.add(right.scale(w * 0.45)), lftI = c.subtract(right.scale(w * 0.45));
        quad(vc, m, (float)top.x,(float)top.y,(float)top.z, (float)rgtI.x,(float)rgtI.y,(float)rgtI.z, (float)bot.x,(float)bot.y,(float)bot.z, (float)lftI.x,(float)lftI.y,(float)lftI.z, 240, 250, 255, a);
        drawHexShield(vc, m, c, look, rad * 0.85F * open, prog * 3.0F, 170, 120, 255, (int)(a * 0.75F));
    }

    private static void drawRibbonArc(VertexConsumer vc, Matrix4f m, Vec3 o, Vec3 look, float rad, float tiltDeg, float prog, int r, int g, int b, int a) {
        Vec3 flat = new Vec3(look.x, 0, look.z);
        if (flat.lengthSqr() < 1e-4) flat = new Vec3(0, 0, 1);
        flat = flat.normalize();
        Vec3 right = new Vec3(-flat.z, 0, flat.x);
        double tilt = Math.toRadians(tiltDeg);
        int segs = 22;
        double startA = Math.toRadians(-110.0 + prog * 35.0);
        double endA = Math.toRadians(-110.0 + Math.min(230.0, 50.0 + prog * 230.0));
        for (int i = 0; i < segs; i++) {
            float f1 = i / (float) segs, f2 = (i + 1) / (float) segs;
            double a1 = Mth.lerp(f1, startA, endA), a2 = Mth.lerp(f2, startA, endA);
            float w1 = Mth.sin(f1 * (float) Math.PI) * 0.95F;
            float w2 = Mth.sin(f2 * (float) Math.PI) * 0.95F;
            Vec3 d1 = flat.scale(Math.cos(a1)).add(right.scale(Math.sin(a1) * Math.cos(tilt))).add(0, Math.sin(a1) * Math.sin(tilt), 0);
            Vec3 d2 = flat.scale(Math.cos(a2)).add(right.scale(Math.sin(a2) * Math.cos(tilt))).add(0, Math.sin(a2) * Math.sin(tilt), 0);
            Vec3 o1 = o.add(d1.scale(rad)), i1 = o.add(d1.scale(rad - w1));
            Vec3 o2 = o.add(d2.scale(rad)), i2 = o.add(d2.scale(rad - w2));
            quad(vc, m, (float)o1.x,(float)o1.y,(float)o1.z, (float)o2.x,(float)o2.y,(float)o2.z, (float)i2.x,(float)i2.y,(float)i2.z, (float)i1.x,(float)i1.y,(float)i1.z, r, g, b, a);
            Vec3 c1 = o.add(d1.scale(rad - w1 * 0.35)), c2 = o.add(d2.scale(rad - w2 * 0.35));
            quad(vc, m, (float)o1.x,(float)o1.y,(float)o1.z, (float)o2.x,(float)o2.y,(float)o2.z, (float)c2.x,(float)c2.y,(float)c2.z, (float)c1.x,(float)c1.y,(float)c1.z, 255, 245, 255, (int)(a * 0.9F));
        }
    }

    private static void drawChainWhirl(VertexConsumer vc, Matrix4f m, Vec3 o, float rad, float tiltDeg, float elapsed, int r, int g, int b, int a) {
        double tilt = Math.toRadians(tiltDeg);
        float baseAng = elapsed * 14.0F;
        for (int arm = 0; arm < 2; arm++) {
            double ang = baseAng + arm * Math.PI;
            Vec3 dir = new Vec3(Math.cos(ang), Math.sin(ang) * Math.sin(tilt), Math.sin(ang) * Math.cos(tilt)).normalize();
            Vec3 perp = new Vec3(-dir.z, 0.15, dir.x).normalize().scale(0.08);
            int links = 10;
            for (int l = 0; l < links; l++) {
                double d1 = rad * (l / (double) links), d2 = rad * ((l + 0.72) / (double) links);
                Vec3 p1 = o.add(dir.scale(d1)), p2 = o.add(dir.scale(d2));
                quad(vc, m,
                    (float)(p1.x - perp.x),(float)(p1.y - perp.y),(float)(p1.z - perp.z),
                    (float)(p2.x - perp.x),(float)(p2.y - perp.y),(float)(p2.z - perp.z),
                    (float)(p2.x + perp.x),(float)(p2.y + perp.y),(float)(p2.z + perp.z),
                    (float)(p1.x + perp.x),(float)(p1.y + perp.y),(float)(p1.z + perp.z),
                    l % 2 == 0 ? 190 : r, l % 2 == 0 ? 185 : g, l % 2 == 0 ? 215 : b, a);
            }
            Vec3 tip = o.add(dir.scale(rad));
            float s = 0.28F;
            quad(vc, m, (float)tip.x,(float)(tip.y+s),(float)tip.z, (float)(tip.x+s),(float)tip.y,(float)tip.z, (float)tip.x,(float)(tip.y-s),(float)tip.z, (float)(tip.x-s),(float)tip.y,(float)tip.z, 245, 200, 255, a);
            quad(vc, m, (float)tip.x,(float)(tip.y+s),(float)tip.z, (float)tip.x,(float)tip.y,(float)(tip.z+s), (float)tip.x,(float)(tip.y-s),(float)tip.z, (float)tip.x,(float)tip.y,(float)(tip.z-s), r, g, b, a);
            int trailSegs = 14;
            for (int i = 0; i < trailSegs; i++) {
                float f1 = i / (float) trailSegs, f2 = (i + 1) / (float) trailSegs;
                double a1 = ang - f1 * 2.4, a2 = ang - f2 * 2.4;
                Vec3 v1 = new Vec3(Math.cos(a1), Math.sin(a1) * Math.sin(tilt), Math.sin(a1) * Math.cos(tilt)).normalize();
                Vec3 v2 = new Vec3(Math.cos(a2), Math.sin(a2) * Math.sin(tilt), Math.sin(a2) * Math.cos(tilt)).normalize();
                float w1 = (1.0F - f1) * 0.72F, w2 = (1.0F - f2) * 0.72F;
                Vec3 o1 = o.add(v1.scale(rad)), i1 = o.add(v1.scale(rad - w1));
                Vec3 o2 = o.add(v2.scale(rad)), i2 = o.add(v2.scale(rad - w2));
                quad(vc, m, (float)o1.x,(float)o1.y,(float)o1.z, (float)o2.x,(float)o2.y,(float)o2.z, (float)i2.x,(float)i2.y,(float)i2.z, (float)i1.x,(float)i1.y,(float)i1.z, r, g, b, (int)(a * (1.0F - f1)));
            }
        }
    }

    private static void drawChainHook(VertexConsumer vc, Matrix4f m, Vec3 o, Vec3 dir, float maxLen, float prog, int r, int g, int b, int a) {
        float reach = (prog < 0.45F ? prog / 0.45F : (1.0F - prog) / 0.55F) * maxLen;
        Vec3 perp = new Vec3(-dir.z, 0.1, dir.x);
        if (perp.lengthSqr() < 1e-4) perp = new Vec3(1, 0, 0);
        perp = perp.normalize().scale(0.11);
        int links = Math.max(4, (int)(reach * 2.2F));
        for (int i = 0; i < links; i++) {
            Vec3 p1 = o.add(dir.scale(reach * (i / (float) links)));
            Vec3 p2 = o.add(dir.scale(reach * ((i + 0.75F) / (float) links)));
            quad(vc, m,
                (float)(p1.x-perp.x),(float)(p1.y-perp.y),(float)(p1.z-perp.z),
                (float)(p2.x-perp.x),(float)(p2.y-perp.y),(float)(p2.z-perp.z),
                (float)(p2.x+perp.x),(float)(p2.y+perp.y),(float)(p2.z+perp.z),
                (float)(p1.x+perp.x),(float)(p1.y+perp.y),(float)(p1.z+perp.z),
                i % 2 == 0 ? 220 : r, i % 2 == 0 ? 210 : g, 255, a);
        }
        Vec3 tip = o.add(dir.scale(reach));
        float s = 0.35F;
        quad(vc, m, (float)tip.x,(float)(tip.y+s),(float)tip.z, (float)(tip.x+s),(float)tip.y,(float)tip.z, (float)tip.x,(float)(tip.y-s),(float)tip.z, (float)(tip.x-s),(float)tip.y,(float)tip.z, 255, 220, 255, a);
    }

    private static void drawShockRing(VertexConsumer vc, Matrix4f m, Vec3 o, float rad, int r, int g, int b, int a) {
        int segs = 28;
        float thick = 0.55F;
        for (int i = 0; i < segs; i++) {
            double a1 = i * Math.PI * 2 / segs, a2 = (i + 1) * Math.PI * 2 / segs;
            float c1 = (float)Math.cos(a1), s1 = (float)Math.sin(a1);
            float c2 = (float)Math.cos(a2), s2 = (float)Math.sin(a2);
            quad(vc, m,
                (float)o.x + c1 * rad, (float)o.y, (float)o.z + s1 * rad,
                (float)o.x + c2 * rad, (float)o.y, (float)o.z + s2 * rad,
                (float)o.x + c2 * (rad - thick), (float)o.y + 0.15F, (float)o.z + s2 * (rad - thick),
                (float)o.x + c1 * (rad - thick), (float)o.y + 0.15F, (float)o.z + s1 * (rad - thick),
                r, g, b, a);
        }
    }

    private static void drawBeamLance(VertexConsumer vc, Matrix4f m, Vec3 o, Vec3 dir, float len, float prog, int r, int g, int b, int a) {
        Vec3 p1 = new Vec3(-dir.z, 0, dir.x);
        if (p1.lengthSqr() < 1e-4) p1 = new Vec3(1, 0, 0);
        p1 = p1.normalize();
        Vec3 p2 = dir.cross(p1).normalize();
        float w = 0.36F * (1.0F - prog * 0.5F);
        Vec3 end = o.add(dir.scale(len));
        for (Vec3 ax : new Vec3[]{p1.scale(w), p2.scale(w)}) {
            quad(vc, m,
                (float)(o.x-ax.x),(float)(o.y-ax.y),(float)(o.z-ax.z),
                (float)(end.x-ax.x),(float)(end.y-ax.y),(float)(end.z-ax.z),
                (float)(end.x+ax.x),(float)(end.y+ax.y),(float)(end.z+ax.z),
                (float)(o.x+ax.x),(float)(o.y+ax.y),(float)(o.z+ax.z),
                r, g, b, a);
            Vec3 core = ax.scale(0.45);
            quad(vc, m,
                (float)(o.x-core.x),(float)(o.y-core.y),(float)(o.z-core.z),
                (float)(end.x-core.x),(float)(end.y-core.y),(float)(end.z-core.z),
                (float)(end.x+core.x),(float)(end.y+core.y),(float)(end.z+core.z),
                (float)(o.x+core.x),(float)(o.y+core.y),(float)(o.z+core.z),
                255, 255, 255, a);
        }
    }

    private static void drawHexShield(VertexConsumer vc, Matrix4f m, Vec3 c, Vec3 look, float rad, float t, int r, int g, int b, int a) {
        Vec3 right = new Vec3(-look.z, 0, look.x);
        if (right.lengthSqr() < 1e-4) right = new Vec3(1, 0, 0);
        right = right.normalize();
        Vec3 up = right.cross(look).normalize();
        for (int i = 0; i < 6; i++) {
            double a1 = i * Math.PI / 3.0 + t * 1.5, a2 = (i + 1) * Math.PI / 3.0 + t * 1.5;
            Vec3 v1 = c.add(right.scale(Math.cos(a1) * rad)).add(up.scale(Math.sin(a1) * rad));
            Vec3 v2 = c.add(right.scale(Math.cos(a2) * rad)).add(up.scale(Math.sin(a2) * rad));
            Vec3 i1 = c.add(right.scale(Math.cos(a1) * (rad * 0.78))).add(up.scale(Math.sin(a1) * (rad * 0.78)));
            Vec3 i2 = c.add(right.scale(Math.cos(a2) * (rad * 0.78))).add(up.scale(Math.sin(a2) * (rad * 0.78)));
            quad(vc, m, (float)v1.x,(float)v1.y,(float)v1.z, (float)v2.x,(float)v2.y,(float)v2.z, (float)i2.x,(float)i2.y,(float)i2.z, (float)i1.x,(float)i1.y,(float)i1.z, r, g, b, a);
        }
    }

    private static void drawDimCuts(VertexConsumer vc, Matrix4f m, Vec3 c, float rad, float prog, int r, int g, int b, int a) {
        for (int k = 0; k < 10; k++) {
            double a1 = k * 1.37 + prog * 1.2, a2 = k * 2.13;
            Vec3 d = new Vec3(Math.cos(a1) * Math.cos(a2), Math.sin(a2) * 0.7, Math.sin(a1) * Math.cos(a2)).normalize();
            Vec3 side = new Vec3(-d.z, 0.25, d.x).normalize().scale(0.14);
            Vec3 p1 = c.subtract(d.scale(rad)), p2 = c.add(d.scale(rad));
            quad(vc, m,
                (float)(p1.x-side.x),(float)(p1.y-side.y),(float)(p1.z-side.z),
                (float)(p2.x-side.x),(float)(p2.y-side.y),(float)(p2.z-side.z),
                (float)(p2.x+side.x),(float)(p2.y+side.y),(float)(p2.z+side.z),
                (float)(p1.x+side.x),(float)(p1.y+side.y),(float)(p1.z+side.z),
                k % 2 == 0 ? 255 : r, k % 2 == 0 ? 235 : g, 255, a);
        }
    }

    private static void drawChainCage(VertexConsumer vc, Matrix4f m, Vec3 c, float rad, float prog, int r, int g, int b, int a) {
        float curR = rad * (1.0F - prog * 0.85F);
        drawShockRing(vc, m, c.add(0, -0.9, 0), curR, r, g, b, a);
        drawShockRing(vc, m, c.add(0, 0.9, 0), curR, 210, 120, 255, a);
        for (int i = 0; i < 6; i++) {
            double ang = i * (Math.PI / 3.0) + prog * 2.5;
            Vec3 p1 = c.add(Math.cos(ang) * curR, -1.2, Math.sin(ang) * curR);
            Vec3 p2 = c.add(Math.cos(ang + Math.PI * 0.7) * curR, 1.8, Math.sin(ang + Math.PI * 0.7) * curR);
            Vec3 s = new Vec3(0.10, 0, 0.10);
            quad(vc, m,
                (float)(p1.x-s.x),(float)p1.y,(float)(p1.z-s.z),
                (float)(p2.x-s.x),(float)p2.y,(float)(p2.z-s.z),
                (float)(p2.x+s.x),(float)p2.y,(float)(p2.z+s.z),
                (float)(p1.x+s.x),(float)p1.y,(float)(p1.z+s.z),
                r, g, b, a);
        }
    }
}
