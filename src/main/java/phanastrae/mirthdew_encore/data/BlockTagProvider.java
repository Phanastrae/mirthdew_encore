package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreBlockTags;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public BlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS)
                .add(MirthdewEncoreBlocks.DREAMTWIRL_BARRIER);

        getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE)
                .add(MirthdewEncoreBlocks.DREAMTWIRL_BARRIER);

        getOrCreateTagBuilder(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)
                .add(MirthdewEncoreBlocks.DREAMTWIRL_BARRIER);

        getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE)
                .add(MirthdewEncoreBlocks.DREAMTWIRL_BARRIER);

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.DREAMSPECK_OPAQUE)
                .addOptionalTag(MirthdewEncoreBlockTags.IS_XP_MANIPULATOR)
                .addOptionalTag(MirthdewEncoreBlockTags.IS_NETHERITE)
                .addOptionalTag(MirthdewEncoreBlockTags.IS_PURPUR)
                .addOptionalTag(MirthdewEncoreBlockTags.IS_SCULK)
                .addOptionalTag(MirthdewEncoreBlockTags.IS_SOUL_FILLED)
                .addOptionalTag(MirthdewEncoreBlockTags.IS_NETHER_WART);

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.IS_NETHER_WART)
                .addOptionalTag(BlockTags.WART_BLOCKS)
                .add(Blocks.NETHER_WART)
                .add(Blocks.RED_NETHER_BRICKS)
                .add(Blocks.RED_NETHER_BRICK_SLAB)
                .add(Blocks.RED_NETHER_BRICK_STAIRS)
                .add(Blocks.RED_NETHER_BRICK_WALL);

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.IS_NETHERITE)
                .add(Blocks.ANCIENT_DEBRIS)
                .addOptionalTag(ConventionalBlockTags.NETHERITE_SCRAP_ORES)
                .add(Blocks.NETHERITE_BLOCK)
                .addOptionalTag(ConventionalBlockTags.STORAGE_BLOCKS_NETHERITE);

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.IS_PURPUR)
                .add(Blocks.PURPUR_BLOCK)
                .add(Blocks.PURPUR_PILLAR)
                .add(Blocks.PURPUR_SLAB)
                .add(Blocks.PURPUR_STAIRS)
                .addOptionalTag(BlockTags.SHULKER_BOXES)
                .addOptionalTag(ConventionalBlockTags.SHULKER_BOXES);

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.IS_SCULK)
                .add(Blocks.SCULK)
                .add(Blocks.SCULK_CATALYST)
                .add(Blocks.SCULK_SENSOR)
                .add(Blocks.CALIBRATED_SCULK_SENSOR)
                .add(Blocks.SCULK_SHRIEKER)
                .add(Blocks.SCULK_VEIN);

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.IS_SOUL_FILLED)
                .addOptionalTag(BlockTags.SOUL_SPEED_BLOCKS)
                .addOptionalTag(BlockTags.SOUL_FIRE_BASE_BLOCKS)
                .add(Blocks.SOUL_FIRE)
                .add(Blocks.SOUL_LANTERN);

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.IS_XP_MANIPULATOR)
                .add(Blocks.ENCHANTING_TABLE)
                .addOptionalTag(BlockTags.ENCHANTMENT_POWER_PROVIDER)
                .add(Blocks.GRINDSTONE)
                .add(Blocks.ANVIL)
                .add(Blocks.CHIPPED_ANVIL)
                .add(Blocks.DAMAGED_ANVIL);
    }
}
