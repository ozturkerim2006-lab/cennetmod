package com.cennetmod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = "cennetmod", value = Dist.CLIENT)
public class OzelEfektSistemi {
    private static final int FX_SLASH_RIBBON = 0;
    private static final int FX_CHAIN_WHIRL = 1;
    private static final int FX_CHAIN_HOOK = 2;
    private static final int FX_RING_WAVE = 3;
    private static final int FX_BEAM_LANCE = 4;
    private static final int FX_HEX_SHIELD = 5;
    private static final int FX_DIM_CUTS = 6;
    private static final int FX_CHAIN_CAGE = 7;
    private static final int FX_CRESCENT_PROJ = 8;

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

    // Birinci sahis dovus animasyonu durumu
    private static int handAnimKind = 0; // 1..4 kombo, 10=spin, 11=parry, 12=thrust, 13=slam, 14=whip
    private static double handAnimStart = 0;
    private static double handAnimDur = 0.25;

    private static double now() {
        return System.nanoTime() * 1.0e-9;
    }

    private static void playHandAnim(int kind, double dur) {
        handAnimKind = kind;
        handAnimStart = now();
        handAnimDur = dur;
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
            default -> 0.0F;
        };
        playHandAnim(localCombo, 0.30);

        if (stack.getItem() instanceof TwinKusarigama) {
            // 4 blok etrafa cift mor zincir + kristal agirlik + parlayan mor kesik seridi!
            FX_LIST.add(new Vfx(FX_SLASH_RIBBON, o, look, 0.36, 4.3F, tilt, 170, 70, 255, true));
            FX_LIST.add(new Vfx(FX_CHAIN_WHIRL, o, look, 0.38, 4.2F, tilt, 190, 95, 255, true));
            if (localCombo == 4) {
                FX_LIST.add(new Vfx(FX_SLASH_RIBBON, o, look.scale(-1), 0.42, 4.7F, -tilt, 220, 120, 255, true));
                FX_LIST.add(new Vfx(FX_RING_WAVE, p.position().add(0, 0.25, 0), look, 0.45, 4.8F, 0, 165, 65, 255, false));
            }
        } else if (stack.getItem() instanceof YildizKilici) {
            int st = YildizKilici.getStyle(stack);
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
                playHandAnim(12, 0.26);
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
        if (p.isShiftKeyDown() || p.getCooldowns().isOnCooldown(stack.getItem())) return;

        Vec3 o = p.position().add(0, 1.15, 0);
        Vec3 eye = p.getEyePosition().add(0, -0.2, 0);
        Vec3 look = p.getLookAngle().normalize();

        if (stack.getItem() instanceof TwinKusarigama) {
            int st = TwinKusarigama.getStyle(stack);
            switch (st) {
                case 0 -> { // Cift Zincir Kasirgasi (2.4 sn)
                    playHandAnim(10, 2.4);
                    FX_LIST.add(new Vfx(FX_CHAIN_WHIRL, o, look, 2.4, 4.6F, 12.0F, 185, 75, 255, true));
                    FX_LIST.add(new Vfx(FX_CHAIN_WHIRL, o, look, 2.4, 4.3F, -18.0F, 130, 50, 240, true));
                }
                case 1 -> { // Yilan Kancasi (12 blok zincir atisi)
                    playHandAnim(14, 0.50);
                    FX_LIST.add(new Vfx(FX_CHAIN_HOOK, eye, look, 0.55, 12.0F, 0, 195, 90, 255, false));
                }
                case 2 -> { // Kizil Bicici (Capraz kesikler)
                    playHandAnim(10, 1.35);
                    FX_LIST.add(new Vfx(FX_DIM_CUTS, o.add(look.scale(3.0)), look, 1.35, 3.8F, 0, 240, 60, 160, false));
                }
                case 3 -> { // Zincir Celmesi
                    playHandAnim(13, 0.45);
                    FX_LIST.add(new Vfx(FX_RING_WAVE, p.position().add(0, 0.25, 0), look, 0.55, 4.8F, 0, 145, 95, 255, false));
                    FX_LIST.add(new Vfx(FX_CHAIN_WHIRL, p.position().add(0, 0.45, 0), look, 0.48, 4.6F, 0, 190, 90, 255, true));
                }
                case 4 -> { // Golge Zincir Kafesi
                    playHandAnim(11, 0.95);
                    FX_LIST.add(new Vfx(FX_CHAIN_CAGE, p.position().add(0, 1.2, 0), look, 1.05, 5.4F, 0, 165, 55, 255, false));
                }
            }
        } else if (stack.getItem() instanceof YildizKilici) {
            int st = YildizKilici.getStyle(stack);
            switch (st) {
                case 0 -> {
                    playHandAnim(12, 0.45);
                    FX_LIST.add(new Vfx(FX_BEAM_LANCE, eye, look, 0.65, 9.5F, 0, 255, 230, 80, false));
                    FX_LIST.add(new Vfx(FX_SLASH_RIBBON, o, look, 0.45, 4.0F, 22.0F, 255, 240, 120, true));
                }
                case 1 -> {
                    playHandAnim(1, 0.40);
                    FX_LIST.add(new Vfx(FX_CRESCENT_PROJ, eye.add(look.scale(1.2)), look, 1.10, 3.4F, 0, 90, 235, 255, false));
                }
                case 2 -> {
                    playHandAnim(10, 2.0);
                    FX_LIST.add(new Vfx(FX_CHAIN_WHIRL, o, look, 2.0, 4.2F, 15.0F, 210, 240, 255, true));
                }
                case 3 -> {
                    playHandAnim(13, 0.95);
                    FX_LIST.add(new Vfx(FX_RING_WAVE, p.position().add(0, 0.3, 0), look, 0.90, 6.2F, 0, 255, 115, 30, false));
                }
                case 4 -> {
                    playHandAnim(12, 0.55);
                    FX_LIST.add(new Vfx(FX_BEAM_LANCE, eye, look, 0.55, 14.0F, 0, 95, 215, 255, false));
                }
                case 5 -> {
                    playHandAnim(11, 1.20);
                    FX_LIST.add(new Vfx(FX_HEX_SHIELD, o, look, 1.20, 1.85F, 0, 110, 210, 255, true));
                }
                case 6 -> {
                    playHandAnim(14, 1.35);
                    FX_LIST.add(new Vfx(FX_DIM_CUTS, eye.add(look.scale(6.0)), look, 1.40, 4.5F, 0, 195, 75, 255, false));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof YildizKilici) && !(stack.getItem() instanceof TwinKusarigama)) return;

        double elapsed = now() - handAnimStart;
        if (elapsed < 0 || elapsed > handAnimDur) return;
        float p = (float) Mth.clamp(elapsed / handAnimDur, 0.0, 1.0);
        float s = Mth.sin(p * (float) Math.PI);
        PoseStack pose = event.getPoseStack();

        switch (handAnimKind) {
            case 1 -> { // Sagdan sola genis kesik
                pose.translate(-0.55F * s, 0.12F * s, -0.35F * s);
                pose.mulPose(Axis.YP.rotationDegrees(75.0F * s));
                pose.mulPose(Axis.ZP.rotationDegrees(55.0F * s));
                pose.mulPose(Axis.XP.rotationDegrees(-25.0F * s));
            }
            case 2 -> { // Soldan saga ters yukari kesik
                pose.translate(-0.35F + 0.70F * p, -0.15F + 0.35F * p, -0.30F * s);
                pose.mulPose(Axis.YP.rotationDegrees(-70.0F * s));
                pose.mulPose(Axis.ZP.rotationDegrees(-65.0F * s));
            }
            case 3 -> { // Hizli capraz donus
                pose.translate(-0.25F * s, 0.18F * s, -0.45F * s);
                pose.mulPose(Axis.XP.rotationDegrees(-65.0F * s));
                pose.mulPose(Axis.ZP.rotationDegrees(180.0F * p));
            }
            case 4 -> { // Tepeden agir bitirici darbe
                float slam = p < 0.35F ? (p / 0.35F) : (1.0F - (p - 0.35F) / 0.65F);
                pose.translate(-0.20F * s, 0.32F * slam - 0.38F * (1.0F - slam), -0.55F * s);
                pose.mulPose(Axis.XP.rotationDegrees(50.0F * slam - 115.0F * (1.0F - slam)));
            }
            case 10 -> { // Kasirga / Cift Zincir yuksek hizli donus
                pose.translate(-0.25F, -0.05F, -0.35F);
                pose.mulPose(Axis.YP.rotationDegrees((float)(elapsed * 920.0)));
                pose.mulPose(Axis.ZP.rotationDegrees(35.0F));
            }
            case 11 -> { // Savusturma / Muhur blok pozu
                pose.translate(-0.32F * s, 0.10F * s, -0.25F * s);
                pose.mulPose(Axis.ZP.rotationDegrees(62.0F * s));
                pose.mulPose(Axis.YP.rotationDegrees(45.0F * s));
            }
            case 12 -> { // Mizrak / Yildirim ileri saplama
                float thrust = p < 0.25F ? -(p / 0.25F) * 0.25F : Mth.sin(((p - 0.25F) / 0.75F) * (float) Math.PI) * 0.85F;
                pose.translate(0.0F, 0.05F, -thrust);
                pose.mulPose(Axis.XP.rotationDegrees(-78.0F * s));
            }
            case 13 -> { // Yere cakma
                pose.translate(-0.15F * s, 0.25F * Mth.cos(p * (float) Math.PI), -0.45F * s);
                pose.mulPose(Axis.XP.rotationDegrees(-95.0F * s));
            }
            case 14 -> { // Zincir firlatma / Boyut kesigi geri tepmesi
                pose.translate(-0.30F * s, 0.12F * s, -0.60F * s);
                pose.mulPose(Axis.XP.rotationDegrees(-55.0F * s));
                pose.mulPose(Axis.ZP.rotationDegrees(-40.0F * s));
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft mc = Minecraft.getInstance();
        Player p = mc.player;
        if (p == null || mc.level == null) return;

        // Sol tik saldiri tespiti (Anlik 3D efekt uretimi)
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
        PoseStack pose = event.getPoseStack();
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
            }
        }

        pose.popPose();
        buf.endBatch(RenderType.lightning());
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
            // Dis renkli aura seridi
            quad(vc, m, (float)o1.x,(float)o1.y,(float)o1.z, (float)o2.x,(float)o2.y,(float)o2.z, (float)i2.x,(float)i2.y,(float)i2.z, (float)i1.x,(float)i1.y,(float)i1.z, r, g, b, a);
            // Parlak beyaz ic cekirdek
            Vec3 c1 = o.add(d1.scale(rad - w1 * 0.35)), c2 = o.add(d2.scale(rad - w2 * 0.35));
            quad(vc, m, (float)o1.x,(float)o1.y,(float)o1.z, (float)o2.x,(float)o2.y,(float)o2.z, (float)c2.x,(float)c2.y,(float)c2.z, (float)c1.x,(float)c1.y,(float)c1.z, 255, 245, 255, (int)(a * 0.9F));
        }
    }

    private static void drawChainWhirl(VertexConsumer vc, Matrix4f m, Vec3 o, float rad, float tiltDeg, float elapsed, int r, int g, int b, int a) {
        double tilt = Math.toRadians(tiltDeg);
        float baseAng = elapsed * 14.0F;
        // 2 karsit zincir kolu + ucunda donen mor kristal agirlik + enerji izi
        for (int arm = 0; arm < 2; arm++) {
            double ang = baseAng + arm * Math.PI;
            Vec3 dir = new Vec3(Math.cos(ang), Math.sin(ang) * Math.sin(tilt), Math.sin(ang) * Math.cos(tilt)).normalize();
            Vec3 perp = new Vec3(-dir.z, 0.15, dir.x).normalize().scale(0.08);
            // Zincir halkalari
            int links = 10;
            for (int l = 0; l < links; l++) {
                double d1 = rad * (l / (double) links);
                double d2 = rad * ((l + 0.72) / (double) links);
                Vec3 p1 = o.add(dir.scale(d1)), p2 = o.add(dir.scale(d2));
                int cr = (l % 2 == 0) ? 190 : r;
                int cg = (l % 2 == 0) ? 185 : g;
                int cb = (l % 2 == 0) ? 215 : b;
                quad(vc, m,
                    (float)(p1.x - perp.x),(float)(p1.y - perp.y),(float)(p1.z - perp.z),
                    (float)(p2.x - perp.x),(float)(p2.y - perp.y),(float)(p2.z - perp.z),
                    (float)(p2.x + perp.x),(float)(p2.y + perp.y),(float)(p2.z + perp.z),
                    (float)(p1.x + perp.x),(float)(p1.y + perp.y),(float)(p1.z + perp.z),
                    cr, cg, cb, a);
            }
            // Uctaki mor kristal agirlik (Elmas oktahedron)
            Vec3 tip = o.add(dir.scale(rad));
            float s = 0.28F;
            quad(vc, m, (float)tip.x,(float)(tip.y+s),(float)tip.z, (float)(tip.x+s),(float)tip.y,(float)tip.z, (float)tip.x,(float)(tip.y-s),(float)tip.z, (float)(tip.x-s),(float)tip.y,(float)tip.z, 245, 200, 255, a);
            quad(vc, m, (float)tip.x,(float)(tip.y+s),(float)tip.z, (float)tip.x,(float)tip.y,(float)(tip.z+s), (float)tip.x,(float)(tip.y-s),(float)tip.z, (float)tip.x,(float)tip.y,(float)(tip.z-s), r, g, b, a);
            // Arka mor enerji izi (140 derecelik kavis)
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
            double a1 = i * Math.PI / 3.0 + t * 1.5;
            double a2 = (i + 1) * Math.PI / 3.0 + t * 1.5;
            Vec3 v1 = c.add(right.scale(Math.cos(a1) * rad)).add(up.scale(Math.sin(a1) * rad));
            Vec3 v2 = c.add(right.scale(Math.cos(a2) * rad)).add(up.scale(Math.sin(a2) * rad));
            Vec3 i1 = c.add(right.scale(Math.cos(a1) * (rad * 0.78))).add(up.scale(Math.sin(a1) * (rad * 0.78)));
            Vec3 i2 = c.add(right.scale(Math.cos(a2) * (rad * 0.78))).add(up.scale(Math.sin(a2) * (rad * 0.78)));
            quad(vc, m, (float)v1.x,(float)v1.y,(float)v1.z, (float)v2.x,(float)v2.y,(float)v2.z, (float)i2.x,(float)i2.y,(float)i2.z, (float)i1.x,(float)i1.y,(float)i1.z, r, g, b, a);
        }
    }

    private static void drawDimCuts(VertexConsumer vc, Matrix4f m, Vec3 c, float rad, float prog, int r, int g, int b, int a) {
        for (int k = 0; k < 10; k++) {
            double a1 = k * 1.37 + prog * 1.2;
            double a2 = k * 2.13;
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
