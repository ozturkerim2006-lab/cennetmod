package com.cennetmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Sinematik ışınlanmanın istemci tarafı: mor sis, kamera titremesi, FOV, ekran morlaşması ve beyaz flaş. */
@Mod.EventBusSubscriber(modid = CennetMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    private static float lastAlpha = 0f;
    private static boolean lastWhite = false;
    private static long lastNs = 0L;

    /** Asa kullanılıyorsa kaç tick'tir, kullanılmıyorsa -1. */
    private static int ticks() {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null || !p.isUsingItem() || !(p.getUseItem().getItem() instanceof YarikAsasi)) return -1;
        return p.getTicksUsingItem();
    }

    @SubscribeEvent
    public static void sis(ViewportEvent.ComputeFogColor e) {
        int t = ticks();
        if (t < 50) return;
        float k = Math.min(1f, (t - 50) / 60f);
        e.setRed(e.getRed() + (0.45f - e.getRed()) * k);
        e.setGreen(e.getGreen() + (0.05f - e.getGreen()) * k);
        e.setBlue(e.getBlue() + (0.75f - e.getBlue()) * k);
    }

    @SubscribeEvent
    public static void kamera(ViewportEvent.ComputeCameraAngles e) {
        int t = ticks();
        if (t < 130) return;
        float a = Math.min(1f, (t - 130) / 80f);
        double time = t + e.getPartialTick();
        e.setRoll((float) (e.getRoll() + Math.sin(time * 2.3) * 1.2 * a));
        e.setPitch((float) (e.getPitch() + Math.sin(time * 3.1) * 0.5 * a));
        e.setYaw((float) (e.getYaw() + Math.cos(time * 2.7) * 0.5 * a));
    }

    @SubscribeEvent
    public static void fov(ViewportEvent.ComputeFov e) {
        int t = ticks();
        if (t < 130) return;
        float a = Math.min(1f, (t - 130) / (YarikAsasi.SURE - 130f));
        e.setFOV(e.getFOV() + 20.0 * a * a);
    }

    @SubscribeEvent
    public static void ekran(RenderGuiEvent.Post e) {
        int t = ticks();
        long now = System.nanoTime();
        if (t >= 0) {
            float a;
            boolean white = false;
            if (t < 100) {
                a = 0f;
            } else if (t < YarikAsasi.SURE - 24) {
                a = 0.45f * (t - 100) / (YarikAsasi.SURE - 124f);
            } else {
                a = 0.45f + 0.55f * (t - (YarikAsasi.SURE - 24)) / 24f;
                white = true;
            }
            lastAlpha = a;
            lastWhite = white;
        } else {
            float dt = lastNs == 0L ? 0f : (now - lastNs) / 1e9f;
            lastAlpha = Math.max(0f, lastAlpha - dt / 1.5f); // varışta beyazdan yavaşça açılır
        }
        lastNs = now;
        if (lastAlpha <= 0.01f) return;
        int al = (int) (Math.min(1f, lastAlpha) * 255);
        int rgb = lastWhite ? 0xFFFFFF : 0x3A0F66;
        e.getGuiGraphics().fill(0, 0, e.getWindow().getGuiScaledWidth(), e.getWindow().getGuiScaledHeight(), (al << 24) | rgb);
    }
}
