package com.cennetmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Sinematik ışınlanmanın istemci tarafı: mor sis, kamera titremesi ve görüş açısı. (Beyaz flaş sunucudan FLASH parçacığıyla gelir.) */
@Mod.EventBusSubscriber(modid = CennetMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

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
}
