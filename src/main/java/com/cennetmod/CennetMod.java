package com.cennetmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CennetMod.MODID)
public class CennetMod {
    public static final String MODID = "cennetmod";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> YARIK_ASASI = ITEMS.register("yarik_asasi",
            () -> new YarikAsasi(new Item.Properties().durability(64).stacksTo(1)));

    @SuppressWarnings("removal")
    public CennetMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        bus.addListener(this::tabIcerigi);
    }

    private void tabIcerigi(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) e.accept(YARIK_ASASI);
    }
}
