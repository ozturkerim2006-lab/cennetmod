package com.cennetmod;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ZifiriTapinakFeature extends Feature<NoneFeatureConfiguration> {

    public ZifiriTapinakFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        if (!level.getBlockState(origin.below()).isSolid()) {
            return false;
        }

        Block ayCevheri = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("cennetmod", "ay_cevheri"));
        Block karanlikGovde = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("cennetmod", "karanlik_govde"));

        // 5x5 zemin platformu
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                level.setBlock(origin.offset(x, -1, z), Blocks.MUD_BRICKS.defaultBlockState(), 2);
                level.setBlock(origin.offset(x, 0, z), Blocks.AIR.defaultBlockState(), 2);
            }
        }

        // 4 köşe sütunu
        int[][] koseler = {{-2, -2}, {2, -2}, {-2, 2}, {2, 2}};
        for (int[] k : koseler) {
            for (int y = 0; y < 4; y++) {
                level.setBlock(origin.offset(k[0], y, k[1]), karanlikGovde.defaultBlockState(), 2);
            }
            level.setBlock(origin.offset(k[0], 4, k[1]), Blocks.SOUL_LANTERN.defaultBlockState(), 2);
        }

        // Merkez sunak ve Ay Cevheri ödülü
        level.setBlock(origin, Blocks.CHISELED_DEEPSLATE.defaultBlockState(), 2);
        level.setBlock(origin.above(), ayCevheri.defaultBlockState(), 2);

        return true;
    }
}
