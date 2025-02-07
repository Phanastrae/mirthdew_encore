package phanastrae.mirthdew_encore.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.data.MirthdewEncoreBlockFamilies;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreBlockTags;

import java.util.concurrent.CompletableFuture;

import static phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks.*;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        addFamiliesToTag(BlockTags.MINEABLE_WITH_PICKAXE,
                MirthdewEncoreBlockFamilies.BACCHENITE_BRICKS,
                MirthdewEncoreBlockFamilies.BACCHENITE_TILES,
                MirthdewEncoreBlockFamilies.UNGUISHALE,
                MirthdewEncoreBlockFamilies.UNGUISHALE_BRICKS,
                MirthdewEncoreBlockFamilies.UNGUISHALE_TILES,
                MirthdewEncoreBlockFamilies.ROUGH_GACHERIMM,
                MirthdewEncoreBlockFamilies.POLISHED_GACHERIMM,
                MirthdewEncoreBlockFamilies.GACHERIMM_TILES,
                MirthdewEncoreBlockFamilies.REVERIME,
                MirthdewEncoreBlockFamilies.POLISHED_REVERIME,
                MirthdewEncoreBlockFamilies.REVERIME_BRICKS,
                MirthdewEncoreBlockFamilies.REVERIME_TILES,
                MirthdewEncoreBlockFamilies.SCARABRIM,
                MirthdewEncoreBlockFamilies.POLISHED_SCARBRIM,
                MirthdewEncoreBlockFamilies.SCARABRIM_BRICKS
        );
        addFamiliesToTag(BlockTags.MINEABLE_WITH_AXE,
                MirthdewEncoreBlockFamilies.CLINKERA_PLANKS,
                MirthdewEncoreBlockFamilies.DECIDRHEUM_PLANKS,
                MirthdewEncoreBlockFamilies.PSYRITE_BLOCK,
                MirthdewEncoreBlockFamilies.CUT_PSYRITE,
                MirthdewEncoreBlockFamilies.PSYRITE_GRATE
        );
        addTagsForFamilies(false, true,
                MirthdewEncoreBlockFamilies.BACCHENITE_BRICKS,
                MirthdewEncoreBlockFamilies.BACCHENITE_TILES,
                MirthdewEncoreBlockFamilies.UNGUISHALE,
                MirthdewEncoreBlockFamilies.UNGUISHALE_BRICKS,
                MirthdewEncoreBlockFamilies.UNGUISHALE_TILES,
                MirthdewEncoreBlockFamilies.ROUGH_GACHERIMM,
                MirthdewEncoreBlockFamilies.GACHERIMM_BRICKS,
                MirthdewEncoreBlockFamilies.GACHERIMM_TILES,
                MirthdewEncoreBlockFamilies.POLISHED_GACHERIMM,
                MirthdewEncoreBlockFamilies.CUT_POLISHED_GACHERIMM,
                MirthdewEncoreBlockFamilies.REVERIME,
                MirthdewEncoreBlockFamilies.REVERIME_BRICKS,
                MirthdewEncoreBlockFamilies.REVERIME_TILES,
                MirthdewEncoreBlockFamilies.POLISHED_REVERIME,
                MirthdewEncoreBlockFamilies.POLISHED_REVERIME_BRICKS,
                MirthdewEncoreBlockFamilies.CUT_POLISHED_REVERIME,
                MirthdewEncoreBlockFamilies.SCARABRIM,
                MirthdewEncoreBlockFamilies.POLISHED_SCARBRIM,
                MirthdewEncoreBlockFamilies.SCARABRIM_BRICKS,
                MirthdewEncoreBlockFamilies.PSYRITE_BLOCK,
                MirthdewEncoreBlockFamilies.CUT_PSYRITE,
                MirthdewEncoreBlockFamilies.PSYRITE_GRATE
        );
        addTagsForFamilies(true, false,
                MirthdewEncoreBlockFamilies.CLINKERA_PLANKS,
                MirthdewEncoreBlockFamilies.DECIDRHEUM_PLANKS
        );

        // mineable
        FabricTagBuilder mineableWithPickaxe = getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        SLUMBERSOCKET,

                        ACHERUNE_HOLLOW,

                        BACCHENITE_BLOCK,

                        UNGUISHALE,

                        GACHERIMM,

                        FROSTED_REVERIME,
                        ROSENGLACE,

                        SUNFLECKED_SCARABRIM,

                        GACHERIMM_PSYRITE_ORE,
                        SCARABRIM_PSYRITE_ORE
                );
        for(Block block : MirthdewEncoreBlockFamilies.NOVACLAGS) {
            mineableWithPickaxe.add(block);
        }

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(
                        DREAMSEED,

                        CLINKERA_LATTICE,
                        SOULSPOT_MUSHRHEUM,

                        DECIDRHEUM_LOG,
                        DECIDRHEUM_WOOD,
                        STRIPPED_DECIDRHEUM_LOG,
                        STRIPPED_DECIDRHEUM_WOOD,
                        DECIDRHEUM_LATTICE,

                        RAW_PSYRITE_BLOCK,
                        PSYRITE_PILLAR,
                        PSYRITE_BARS,
                        PSYRITE_LATTICE
                );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(
                        ONYXSCALE,
                        RHEUMDAUBED_ONYXSCALE,

                        CHALKTISSUE,
                        FLAKING_CHALKTISSUE,
                        SUNSLAKED_CHALKTISSUE,

                        SUNSLAKED_PSYRITE_ORE
                );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_HOE)
                .add(
                        DREAMSEED,
                        VERIC_DREAMSNARE,
                        DECIDRHEUM_LEAVES
                );

        getOrCreateTagBuilder(BlockTags.SWORD_EFFICIENT)
                .add(
                        RHEUMBRISTLES,
                        SOULSPOT_MUSHRHEUM,
                        DECIDRHEUM_LEAVES,

                        ORANGE_FOGHAIR,
                        LIME_FOGHAIR,
                        CYAN_FOGHAIR,
                        MAGNETA_FOGHAIR
                );

        // needs tool
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(
                        SLUMBERSOCKET
                );

        // categories
        getOrCreateTagBuilder(BlockTags.PORTALS)
                .add(
                        SLUMBERVEIL
                );

        getOrCreateTagBuilder(BlockTags.LOGS)
                .add(
                        DECIDRHEUM_LOG,
                        DECIDRHEUM_WOOD,
                        STRIPPED_DECIDRHEUM_LOG,
                        STRIPPED_DECIDRHEUM_WOOD
                );

        getOrCreateTagBuilder(BlockTags.PLANKS)
                .add(
                        CLINKERA_PLANKS,
                        DECIDRHEUM_PLANKS
                );

        getOrCreateTagBuilder(BlockTags.LEAVES)
                .add(
                        DECIDRHEUM_LEAVES
                );

        getOrCreateTagBuilder(BlockTags.FLOWER_POTS)
                .add(
                        POTTED_RHEUMBRISTLES,
                        POTTED_SOULSPOT_MUSHRHEUM,

                        POTTED_DECIDRHEUM_SAPLING,

                        POTTED_ORANGE_FOGHAIR,
                        POTTED_LIME_FOGHAIR,
                        POTTED_CYAN_FOGHAIR,
                        POTTED_MAGNETA_FOGHAIR
                );

        getOrCreateTagBuilder(BlockTags.SAPLINGS)
                .add(
                        DECIDRHEUM_SAPLING
                );

        // behaviours
        getOrCreateTagBuilder(BlockTags.UNSTABLE_BOTTOM_CENTER)
                .add(
                        CLINKERA_FENCE_GATE,
                        DECIDRHEUM_FENCE_GATE
                );

        getOrCreateTagBuilder(BlockTags.MOB_INTERACTABLE_DOORS)
                .add(
                        CLINKERA_DOOR,
                        DECIDRHEUM_DOOR
                );

        getOrCreateTagBuilder(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS)
                .add(
                        DREAMTWIRL_BARRIER
                );

        getOrCreateTagBuilder(BlockTags.ENCHANTMENT_POWER_PROVIDER)
                .add(
                        DREAMSEED
                );

        getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE)
                .add(
                        GREATER_ACHERUNE,
                        DREAMTWIRL_BARRIER,
                        DOOR_MARKER,
                        GREATER_ACHERUNE_MARKER
                );

        getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE)
                .add(
                        GREATER_ACHERUNE,
                        DREAMTWIRL_BARRIER,
                        DOOR_MARKER,
                        GREATER_ACHERUNE_MARKER
                );

        getOrCreateTagBuilder(BlockTags.SNAPS_GOAT_HORN)
                .add(
                        DECIDRHEUM_LOG
                );

        getOrCreateTagBuilder(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)
                .add(
                        DREAMTWIRL_BARRIER
                );

        getOrCreateTagBuilder(BlockTags.WALL_POST_OVERRIDE)
                .add(
                        CLINKERA_PRESSURE_PLATE,
                        DECIDRHEUM_PRESSURE_PLATE
                );

        getOrCreateTagBuilder(BlockTags.COMPLETES_FIND_TREE_TUTORIAL)
                .add(
                        DECIDRHEUM_LOG,
                        DECIDRHEUM_WOOD,
                        STRIPPED_DECIDRHEUM_LOG,
                        STRIPPED_DECIDRHEUM_WOOD,
                        DECIDRHEUM_LEAVES
                );

        getOrCreateTagBuilder(BlockTags.REPLACEABLE_BY_TREES)
                .add(
                        RHEUMBRISTLES,
                        DECIDRHEUM_LEAVES,

                        ORANGE_FOGHAIR,
                        LIME_FOGHAIR,
                        CYAN_FOGHAIR,
                        MAGNETA_FOGHAIR
                );

        getOrCreateTagBuilder(BlockTags.COMBINATION_STEP_SOUND_BLOCKS)
                .add(
                        RHEUMBRISTLES,

                        ORANGE_FOGHAIR,
                        LIME_FOGHAIR,
                        CYAN_FOGHAIR,
                        MAGNETA_FOGHAIR
                );

        // convention tags
        getOrCreateTagBuilder(ConventionalBlockTags.COBBLESTONES)
                .add(
                        ROUGH_GACHERIMM,
                        SCARABRIM
                );

        getOrCreateTagBuilder(ConventionalBlockTags.ORES)
                .add(
                        GACHERIMM_PSYRITE_ORE,
                        SCARABRIM_PSYRITE_ORE,
                        SUNSLAKED_PSYRITE_ORE
                );

        getOrCreateTagBuilder(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED)
                .add(
                        GREATER_ACHERUNE
                );

        // custom
        getOrCreateTagBuilder(MirthdewEncoreBlockTags.DREAMSPECK_OPAQUE)
                .addOptionalTag(MirthdewEncoreBlockTags.IS_XP_MANIPULATOR)
                .addOptionalTag(MirthdewEncoreBlockTags.IS_NETHERITE)
                .addOptionalTag(MirthdewEncoreBlockTags.IS_PURPUR)
                .addOptionalTag(MirthdewEncoreBlockTags.IS_SCULK)
                .addOptionalTag(MirthdewEncoreBlockTags.IS_SOUL_FILLED)
                .addOptionalTag(MirthdewEncoreBlockTags.IS_NETHER_WART);

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.IS_NETHER_WART)
                .addOptionalTag(BlockTags.WART_BLOCKS)
                .add(
                        Blocks.NETHER_WART,
                        Blocks.RED_NETHER_BRICKS,
                        Blocks.RED_NETHER_BRICK_SLAB,
                        Blocks.RED_NETHER_BRICK_STAIRS,
                        Blocks.RED_NETHER_BRICK_WALL
                );

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.IS_NETHERITE)
                .addOptionalTag(ConventionalBlockTags.NETHERITE_SCRAP_ORES)
                .addOptionalTag(ConventionalBlockTags.STORAGE_BLOCKS_NETHERITE)
                .add(
                        Blocks.ANCIENT_DEBRIS,
                        Blocks.NETHERITE_BLOCK
                );

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.IS_PURPUR)
                .addOptionalTag(BlockTags.SHULKER_BOXES)
                .addOptionalTag(ConventionalBlockTags.SHULKER_BOXES)
                .add(
                        Blocks.PURPUR_BLOCK,
                        Blocks.PURPUR_PILLAR,
                        Blocks.PURPUR_SLAB,
                        Blocks.PURPUR_STAIRS
                );

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.IS_SCULK)
                .add(
                        Blocks.SCULK,
                        Blocks.SCULK_CATALYST,
                        Blocks.SCULK_SENSOR,
                        Blocks.CALIBRATED_SCULK_SENSOR,
                        Blocks.SCULK_SHRIEKER,
                        Blocks.SCULK_VEIN,
                        VERIC_DREAMSNARE
        );

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.IS_SOUL_FILLED)
                .addOptionalTag(BlockTags.SOUL_SPEED_BLOCKS)
                .addOptionalTag(BlockTags.SOUL_FIRE_BASE_BLOCKS)
                .add(
                    Blocks.SOUL_FIRE,
                    Blocks.SOUL_LANTERN
                );

        getOrCreateTagBuilder(MirthdewEncoreBlockTags.IS_XP_MANIPULATOR)
                .addOptionalTag(BlockTags.ENCHANTMENT_POWER_PROVIDER)
                .add(
                        Blocks.ENCHANTING_TABLE,
                        Blocks.GRINDSTONE,
                        Blocks.ANVIL,
                        Blocks.CHIPPED_ANVIL,
                        Blocks.DAMAGED_ANVIL
        );

        FabricTagBuilder novaclag = getOrCreateTagBuilder(MirthdewEncoreBlockTags.NOVA_CLAG);
        for(Block block : MirthdewEncoreBlockFamilies.NOVACLAGS) {
            novaclag.add(block);
        }
    }

    public void addFamiliesToTag(TagKey<Block> tag, BlockFamily... families) {
        for(BlockFamily family : families) {
            addFamilyToTag(family, tag);
        }
    }

    public void addFamilyToTag(BlockFamily family, TagKey<Block> tag) {
        FabricTagBuilder builder = getOrCreateTagBuilder(tag);
        builder.add(family.getBaseBlock());
        for(Block block : family.getVariants().values()) {
            builder.add(block);
        }
    }

    public void addTagsForFamilies(boolean isWooden, boolean isStone, BlockFamily... families) {
        for(BlockFamily family : families) {
            addTagsForFamily(family, isWooden, isStone);
        }
    }

    public void addTagsForFamily(BlockFamily family, boolean isWooden, boolean isStone) {
        addBlockToTags(family, BlockFamily.Variant.BUTTON, isWooden, isStone, BlockTags.BUTTONS, BlockTags.WOODEN_BUTTONS, BlockTags.STONE_BUTTONS);
        addBlockToTags(family, BlockFamily.Variant.PRESSURE_PLATE, isWooden, isStone, BlockTags.PRESSURE_PLATES, BlockTags.WOODEN_PRESSURE_PLATES, BlockTags.STONE_PRESSURE_PLATES);

        addBlockToTags(family, BlockFamily.Variant.STAIRS, isWooden, BlockTags.STAIRS, BlockTags.WOODEN_STAIRS);
        addBlockToTags(family, BlockFamily.Variant.SLAB, isWooden, BlockTags.SLABS, BlockTags.WOODEN_SLABS);
        addBlockToTags(family, BlockFamily.Variant.FENCE, isWooden, BlockTags.FENCES, BlockTags.WOODEN_FENCES);
        addBlockToTags(family, BlockFamily.Variant.DOOR, isWooden, BlockTags.DOORS, BlockTags.WOODEN_DOORS);
        addBlockToTags(family, BlockFamily.Variant.TRAPDOOR, isWooden, BlockTags.TRAPDOORS, BlockTags.WOODEN_TRAPDOORS);

        addBlockToTags(family, BlockFamily.Variant.FENCE_GATE, BlockTags.FENCE_GATES);
        addBlockToTags(family, BlockFamily.Variant.WALL, BlockTags.WALLS);
    }

    public void addBlockToTags(BlockFamily family, BlockFamily.Variant variant, TagKey<Block> tag) {
        Block block = family.get(variant);
        if(block != null) {
            getOrCreateTagBuilder(tag).add(block);
        }
    }

    public void addBlockToTags(BlockFamily family, BlockFamily.Variant variant, boolean isWooden, TagKey<Block> baseTag, TagKey<Block> woodTag) {
        addBlockToTags(family.get(variant), isWooden, baseTag, woodTag);
    }

    public void addBlockToTags(@Nullable Block block, boolean isWooden, TagKey<Block> baseTag, TagKey<Block> woodTag) {
        if(block != null) {
            getOrCreateTagBuilder(baseTag).add(block);
            if (isWooden) {
                getOrCreateTagBuilder(woodTag).add(block);
            }
        }
    }

    public void addBlockToTags(BlockFamily family, BlockFamily.Variant variant, boolean isWooden, boolean isStone, TagKey<Block> baseTag, TagKey<Block> woodTag, TagKey<Block> stoneTag) {
        addBlockToTags(family.get(variant), isWooden, isStone, baseTag, woodTag, stoneTag);
    }

    public void addBlockToTags(@Nullable Block block, boolean isWooden, boolean isStone, TagKey<Block> baseTag, TagKey<Block> woodTag, TagKey<Block> stoneTag) {
        if(block != null) {
            getOrCreateTagBuilder(baseTag).add(block);
            if (isWooden) {
                getOrCreateTagBuilder(woodTag).add(block);
            }
            if(isStone) {
                getOrCreateTagBuilder(stoneTag).add(block);
            }
        }
    }
}
