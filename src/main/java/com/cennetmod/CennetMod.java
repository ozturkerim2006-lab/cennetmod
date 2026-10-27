package com.cennetmod;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
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
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    // Eşyalar
    public static final RegistryObject<Item> YARIK_ASASI = ITEMS.register("yarik_asasi",
            () -> new YarikAsasi(new Item.Properties().durability(64).stacksTo(1)));
    public static final RegistryObject<Item> YILDIZ_KRISTALI = ITEMS.register("yildiz_kristali",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> NUR_KARTI = ITEMS.register("nur_kapisi_kimlik_karti",
            () -> new NurKarti(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    // Bloklar
    public static final RegistryObject<Block> KARANLIK_GOVDE = BLOCKS.register("karanlik_govde",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)));
    public static final RegistryObject<Block> KARANLIK_YAPRAK = BLOCKS.register("karanlik_yaprak",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).lightLevel(s -> 4)));
    public static final RegistryObject<Block> AY_CEVHERI = BLOCKS.register("ay_cevheri",
            () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE).lightLevel(s -> 7)));

    // Nur Başkenti blokları
    public static final RegistryObject<Block> BULUT_BLOGU = BLOCKS.register("bulut_blogu",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)));
    public static final RegistryObject<Block> BULUT_TUGLASI = BLOCKS.register("bulut_tuglasi",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));
    public static final RegistryObject<Block> KUTSAL_SUTUN = BLOCKS.register("kutsal_sutun",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_PILLAR)));
    public static final RegistryObject<Block> NUR_FENERI = BLOCKS.register("nur_feneri",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SEA_LANTERN)));

    public static final RegistryObject<Item> BULUT_BLOGU_ITEM = ITEMS.register("bulut_blogu",
            () -> new BlockItem(BULUT_BLOGU.get(), new Item.Properties()));
    public static final RegistryObject<Item> BULUT_TUGLASI_ITEM = ITEMS.register("bulut_tuglasi",
            () -> new BlockItem(BULUT_TUGLASI.get(), new Item.Properties()));
    public static final RegistryObject<Item> KUTSAL_SUTUN_ITEM = ITEMS.register("kutsal_sutun",
            () -> new BlockItem(KUTSAL_SUTUN.get(), new Item.Properties()));
    public static final RegistryObject<Item> NUR_FENERI_ITEM = ITEMS.register("nur_feneri",
            () -> new BlockItem(NUR_FENERI.get(), new Item.Properties()));

    // Şehir üreticisi
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> NUR_BASKENT =
            FEATURES.register("nur_baskent", NurBaskentFeature::new);

    // Blok eşyaları
    public static final RegistryObject<Item> KARANLIK_GOVDE_ITEM = ITEMS.register("karanlik_govde",
            () -> new BlockItem(KARANLIK_GOVDE.get(), new Item.Properties()));
    public static final RegistryObject<Item> KARANLIK_YAPRAK_ITEM = ITEMS.register("karanlik_yaprak",
            () -> new BlockItem(KARANLIK_YAPRAK.get(), new Item.Properties()));
    public static final RegistryObject<Item> AY_CEVHERI_ITEM = ITEMS.register("ay_cevheri",
            () -> new BlockItem(AY_CEVHERI.get(), new Item.Properties()));

    // Varlıklar
    public static final RegistryObject<EntityType<AngelSoldier>> MELEK = ENTITIES.register("melek_asker",
            () -> EntityType.Builder.<AngelSoldier>of(AngelSoldier::new, MobCategory.MISC)
                    .sized(1.8f, 6.0f).clientTrackingRange(10).build("melek_asker"));

    @SuppressWarnings("removal")
    public CennetMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        FEATURES.register(bus);
        bus.addListener(this::tabIcerigi);
        bus.addListener(this::nitelikler);
    }

    private void nitelikler(EntityAttributeCreationEvent e) {
        e.put(MELEK.get(), AngelSoldier.nitelikler().build());
    }

    private void tabIcerigi(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) { e.accept(YARIK_ASASI); e.accept(NUR_KARTI); }
        if (e.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            e.accept(KARANLIK_GOVDE_ITEM);
            e.accept(KARANLIK_YAPRAK_ITEM);
            e.accept(AY_CEVHERI_ITEM);
        }
        if (e.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            e.accept(BULUT_BLOGU_ITEM);
            e.accept(BULUT_TUGLASI_ITEM);
            e.accept(KUTSAL_SUTUN_ITEM);
            e.accept(NUR_FENERI_ITEM);
        }
        if (e.getTabKey() == CreativeModeTabs.INGREDIENTS) e.accept(YILDIZ_KRISTALI);
    }
}
