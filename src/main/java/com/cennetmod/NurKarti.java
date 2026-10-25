package com.cennetmod;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Nur Kapısı Kimlik Kartı: envanterde taşındığı sürece Nur Krallığı'na geçişe izin verir. */
public class NurKarti extends Item {
    public NurKarti(Properties p) { super(p); }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }
}
