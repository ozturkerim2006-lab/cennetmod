package com.cennetmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Asa kullanılırken (3. saniyeden sonra) ufuk/sis rengini moraya çevirir. */
@Mod.EventBusSubscriber(modid = CennetMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void sis(ViewportEvent.ComputeFogColor e) {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null || !p.isUsingItem() || !(p.getUseItem().getItem() instanceof YarikAsasi)) return;
        int t = p.getTicksUsingItem();
        if (t < 60) return;
        float k = Math.min(1f, (t - 60) / 40f);
        e.setRed(e.getRed() + (0.45f - e.getRed()) * k);
        e.setGreen(e.getGreen() + (0.05f - e.getGreen()) * k);
        e.setBlue(e.getBlue() + (0.75f - e.getBlue()) * k);
    }
}
