package com.cennetmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Sinematik ışınlanmanın istemci tarafı: mor sis, kamera titremesi ve görüş açısı. (Beyaz flaş sunucudan FLASH parçacığıyla gelir.) */
@Mod.EventBusSubscriber(modid = CennetMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    private static final ResourceLocation EFEKT =
            ResourceLocation.fromNamespaceAndPath(CennetMod.MODID, "shaders/post/cennet.json");
    private static boolean yuklu = false;
    private static boolean basarisiz = false;
    private static float yumusak = 0f;

    /** Mor-turkuaz renk tonu ve kontrast shader'ı: Cennet Karanlığı'nda sürekli, ışınlanmada kademeli güçlenir. */
    @SubscribeEvent
    public static void shader(ViewportEvent.ComputeFov e) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (p == null || basarisiz) return;

        float hedef = p.level().dimension().equals(YarikAsasi.CENNET) ? 0.55f : 0f;
        int t = ticks();
        if (t >= 60) {
            float k = Math.min(1f, (t - 60) / (YarikAsasi.SURE - 80f));
            hedef = Math.max(hedef, 0.3f + 0.7f * k);
        }
        yumusak += (hedef - yumusak) * 0.06f;

        if (hedef > 0.01f || yumusak > 0.01f) {
            if (!yuklu) {
                mc.gameRenderer.loadEffect(EFEKT);
                if (mc.gameRenderer.currentEffect() == null) { basarisiz = true; return; }
                yuklu = true;
            }
            PostChain pc = mc.gameRenderer.currentEffect();
            if (pc != null) pc.setUniform("Intensity", yumusak);
        } else if (yuklu) {
            mc.gameRenderer.shutdownEffect();
            yuklu = false;
        }
    }

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
