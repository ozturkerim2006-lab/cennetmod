package com.cennetmod;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
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

@Mod("cennetmod")
public class CennetMod {
   public static final String MODID = "cennetmod";
   public static final DeferredRegister<Item> ITEMS;
   public static final DeferredRegister<Block> BLOCKS;
   public static final DeferredRegister<Feature<?>> FEATURES;
   public static final DeferredRegister<EntityType<?>> ENTITIES;
   public static final RegistryObject<Item> YARIK_ASASI;
   public static final RegistryObject<Item> YILDIZ_KRISTALI;
   public static final RegistryObject<Item> NUR_KARTI;
   public static final RegistryObject<Item> BATI_KARTI;
   public static final RegistryObject<Item> PEGASUS_BILETI;
   public static final RegistryObject<Item> ZIFIRI_MESALE;
   public static final RegistryObject<Block> KARANLIK_GOVDE;
   public static final RegistryObject<Block> KARANLIK_YAPRAK;
   public static final RegistryObject<Block> AY_CEVHERI;
   public static final RegistryObject<Block> BULUT_BLOGU;
   public static final RegistryObject<Block> BULUT_TUGLASI;
   public static final RegistryObject<Block> KUTSAL_SUTUN;
   public static final RegistryObject<Block> NUR_FENERI;
   public static final RegistryObject<Item> BULUT_BLOGU_ITEM;
   public static final RegistryObject<Item> BULUT_TUGLASI_ITEM;
   public static final RegistryObject<Item> KUTSAL_SUTUN_ITEM;
   public static final RegistryObject<Item> NUR_FENERI_ITEM;
   public static final RegistryObject<Feature<NoneFeatureConfiguration>> NUR_BASKENT;
   public static final RegistryObject<Item> KARANLIK_GOVDE_ITEM;
   public static final RegistryObject<Item> KARANLIK_YAPRAK_ITEM;
   public static final RegistryObject<Item> AY_CEVHERI_ITEM;
   public static final RegistryObject<EntityType<AngelSoldier>> MELEK;

   public CennetMod() {
      IEventBus var1 = FMLJavaModLoadingContext.get().getModEventBus();
      BLOCKS.register(var1);
      ITEMS.register(var1);
      ENTITIES.register(var1);
      FEATURES.register(var1);
      var1.addListener(this::nitelikler);
      var1.addListener(this::tabIcerigi);
   }

   private void nitelikler(EntityAttributeCreationEvent var1) {
      var1.put((EntityType)MELEK.get(), AngelSoldier.nitelikler().build());
   }

   private void tabIcerigi(BuildCreativeModeTabContentsEvent var1) {
      if (var1.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
         var1.accept(YARIK_ASASI);
         var1.accept(NUR_KARTI);
         var1.accept(BATI_KARTI);
         var1.accept(PEGASUS_BILETI);
         var1.accept(ZIFIRI_MESALE);
      }

      if (var1.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
         var1.accept(KARANLIK_GOVDE_ITEM);
         var1.accept(KARANLIK_YAPRAK_ITEM);
         var1.accept(AY_CEVHERI_ITEM);
      }

      if (var1.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
         var1.accept(BULUT_BLOGU_ITEM);
         var1.accept(BULUT_TUGLASI_ITEM);
         var1.accept(KUTSAL_SUTUN_ITEM);
         var1.accept(NUR_FENERI_ITEM);
      }

      if (var1.getTabKey() == CreativeModeTabs.INGREDIENTS) {
         var1.accept(YILDIZ_KRISTALI);
      }

   }

   static {
      ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "cennetmod");
      BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "cennetmod");
      FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, "cennetmod");
      ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "cennetmod");
      YARIK_ASASI = ITEMS.register("yarik_asasi", () -> new YarikAsasi((new Item.Properties()).durability(64).stacksTo(1)));
      YILDIZ_KRISTALI = ITEMS.register("yildiz_kristali", () -> new Item(new Item.Properties()));
      NUR_KARTI = ITEMS.register("nur_kapisi_kimlik_karti", () -> new NurKarti((new Item.Properties()).stacksTo(1).rarity(Rarity.EPIC).fireResistant()));
      BATI_KARTI = ITEMS.register("bati_sehri_giris_karti", () -> new BatiKarti((new Item.Properties()).stacksTo(1).rarity(Rarity.EPIC).fireResistant()));
      PEGASUS_BILETI = ITEMS.register("pegasus_bileti", () -> new PegasusBileti((new Item.Properties()).stacksTo(1).rarity(Rarity.EPIC).fireResistant()));
      ZIFIRI_MESALE = ITEMS.register("zifiri_mesale", () -> new ZifiriMesale((new Item.Properties()).stacksTo(1).rarity(Rarity.RARE).fireResistant()));
      KARANLIK_GOVDE = BLOCKS.register("karanlik_govde", () -> new RotatedPillarBlock(Properties.ofFullCopy(Blocks.OAK_LOG)));
      KARANLIK_YAPRAK = BLOCKS.register("karanlik_yaprak", () -> new LeavesBlock(Properties.ofFullCopy(Blocks.OAK_LEAVES)));
      AY_CEVHERI = BLOCKS.register("ay_cevheri", () -> new DropExperienceBlock(UniformInt.of(2, 5), Properties.ofFullCopy(Blocks.DIAMOND_ORE).lightLevel((var0) -> 7)));
      BULUT_BLOGU = BLOCKS.register("bulut_blogu", () -> new Block(Properties.ofFullCopy(Blocks.WHITE_WOOL)));
      BULUT_TUGLASI = BLOCKS.register("bulut_tuglasi", () -> new Block(Properties.ofFullCopy(Blocks.STONE_BRICKS)));
      KUTSAL_SUTUN = BLOCKS.register("kutsal_sutun", () -> new RotatedPillarBlock(Properties.ofFullCopy(Blocks.QUARTZ_PILLAR)));
      NUR_FENERI = BLOCKS.register("nur_feneri", () -> new Block(Properties.ofFullCopy(Blocks.SEA_LANTERN)));
      BULUT_BLOGU_ITEM = ITEMS.register("bulut_blogu", () -> new BlockItem((Block)BULUT_BLOGU.get(), new Item.Properties()));
      BULUT_TUGLASI_ITEM = ITEMS.register("bulut_tuglasi", () -> new BlockItem((Block)BULUT_TUGLASI.get(), new Item.Properties()));
      KUTSAL_SUTUN_ITEM = ITEMS.register("kutsal_sutun", () -> new BlockItem((Block)KUTSAL_SUTUN.get(), new Item.Properties()));
      NUR_FENERI_ITEM = ITEMS.register("nur_feneri", () -> new BlockItem((Block)NUR_FENERI.get(), new Item.Properties()));
      NUR_BASKENT = FEATURES.register("nur_baskent", () -> new NurBaskentFeature());
      KARANLIK_GOVDE_ITEM = ITEMS.register("karanlik_govde", () -> new BlockItem((Block)KARANLIK_GOVDE.get(), new Item.Properties()));
      KARANLIK_YAPRAK_ITEM = ITEMS.register("karanlik_yaprak", () -> new BlockItem((Block)KARANLIK_YAPRAK.get(), new Item.Properties()));
      AY_CEVHERI_ITEM = ITEMS.register("ay_cevheri", () -> new BlockItem((Block)AY_CEVHERI.get(), new Item.Properties()));
      ZIFIRI_TAPINAK = FEATURES.register("zifiri_bataklik_tapinagi", () -> new ZifiriTapinakFeature(net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration.CODEC));
      YILDIZ_KILICI = ITEMS.register("yildiz_kilici", YildizKilici::new);
      CAMUR_YARATIGI = ENTITIES.register("camur_yaratigi", () -> EntityType.Builder.of(CamurYaratigi::new, MobCategory.MONSTER).sized(0.8F, 1.8F).build("camur_yaratigi"));
      AGAC_GOZU = ENTITIES.register("agac_gozu", () -> EntityType.Builder.of(AgacGozu::new, MobCategory.MONSTER).sized(1.0F, 1.0F).build("agac_gozu"));
      SIYAH_FIGUR = ENTITIES.register("siyah_figur", () -> EntityType.Builder.of(SiyahFigur::new, MobCategory.MONSTER).sized(0.6F, 2.0F).build("siyah_figur"));
      GOKSEL_MELEK = ENTITIES.register("goksel_melek", () -> EntityType.Builder.of(GokselMelekBoss::new, MobCategory.MONSTER).sized(1.0F, 2.4F).build("goksel_melek"));
      CAMUR_YARATIGI_YUMURTA = ITEMS.register("camur_yaratigi_spawn_egg", () -> new ForgeSpawnEggItem(CAMUR_YARATIGI, 0x3b2f2f, 0x6b4f3a, new Item.Properties()));
      AGAC_GOZU_YUMURTA = ITEMS.register("agac_gozu_spawn_egg", () -> new ForgeSpawnEggItem(AGAC_GOZU, 0x1b3b1b, 0xff2222, new Item.Properties()));
      SIYAH_FIGUR_YUMURTA = ITEMS.register("siyah_figur_spawn_egg", () -> new ForgeSpawnEggItem(SIYAH_FIGUR, 0x0a0a0a, 0x440066, new Item.Properties()));
      GOKSEL_MELEK_YUMURTA = ITEMS.register("goksel_melek_spawn_egg", () -> new ForgeSpawnEggItem(GOKSEL_MELEK, 0xffffff, 0xffd700, new Item.Properties()));
      MELEK = ENTITIES.register("melek_asker", () -> Builder.of(AngelSoldier::new, MobCategory.MISC).sized(1.8F, 6.0F).clientTrackingRange(10).build("melek_asker"));
   }
    public static RegistryObject<Feature<?>> ZIFIRI_TAPINAK;
    public static RegistryObject<Item> YILDIZ_KILICI;
    public static RegistryObject<EntityType<CamurYaratigi>> CAMUR_YARATIGI;
    public static RegistryObject<EntityType<AgacGozu>> AGAC_GOZU;
    public static RegistryObject<EntityType<SiyahFigur>> SIYAH_FIGUR;
    public static RegistryObject<EntityType<GokselMelekBoss>> GOKSEL_MELEK;
    public static RegistryObject<Item> CAMUR_YARATIGI_YUMURTA;
    public static RegistryObject<Item> AGAC_GOZU_YUMURTA;
    public static RegistryObject<Item> SIYAH_FIGUR_YUMURTA;
    public static RegistryObject<Item> GOKSEL_MELEK_YUMURTA;
}
