package phanastrae.mirthdew_encore.data.worldgen.features;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;

import java.util.OptionalInt;

public class MirthdewEncoreTreeFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> DECIDRHEUM = createKey("decidrheum");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FANCY_DECIDRHEUM = createKey("fancy_decidrheum");

    public static void bootstrapTreeFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<Block> holderGetter = context.lookup(Registries.BLOCK);

        FeatureUtils.register(context, MirthdewEncoreTreeFeatures.DECIDRHEUM, Feature.TREE, createDecidrheum().build());
        FeatureUtils.register(context, MirthdewEncoreTreeFeatures.FANCY_DECIDRHEUM, Feature.TREE, createFancyDecidrheum().build());
    }

    private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(
            Block logBlock, Block leavesBlock, int baseHeight, int heightRandA, int heightRandB, int radius
    ) {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(logBlock),
                new StraightTrunkPlacer(baseHeight, heightRandA, heightRandB),
                BlockStateProvider.simple(leavesBlock),
                new BlobFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)
        );
    }

    private static TreeConfiguration.TreeConfigurationBuilder createDecidrheum() {
        return createStraightBlobTree(MirthdewEncoreBlocks.DECIDRHEUM_LOG, MirthdewEncoreBlocks.DECIDRHEUM_LEAVES, 4, 2, 0, 2)
                .ignoreVines()
                .dirt(BlockStateProvider.simple(MirthdewEncoreBlocks.ONYXSCALE));
    }

    private static TreeConfiguration.TreeConfigurationBuilder createFancyDecidrheum() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(MirthdewEncoreBlocks.DECIDRHEUM_LOG),
                new UpwardsBranchingTrunkPlacer(
                        6,
                        7,
                        2,
                        UniformInt.of(1, 4),
                        0.4F,
                        UniformInt.of(1, 4),
                        HolderSet.empty()
                ),
                BlockStateProvider.simple(MirthdewEncoreBlocks.DECIDRHEUM_LEAVES),
                new CherryFoliagePlacer(
                        ConstantInt.of(3),
                        ConstantInt.of(0),
                        ConstantInt.of(4),
                        0.35F,
                        0.5F,
                        0.35F,
                        0.25F),
                new TwoLayersFeatureSize(
                        0,
                        0,
                        0,
                        OptionalInt.of(4)
                )
        )
                .ignoreVines()
                .dirt(BlockStateProvider.simple(MirthdewEncoreBlocks.ONYXSCALE));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, MirthdewEncore.id(name));
    }
}
