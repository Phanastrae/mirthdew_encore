package phanastrae.mirthdew_encore.data;

import com.google.common.collect.Maps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MirthdewEncoreBlockFamilies {
    private static final Map<Block, BlockFamily> MIRTHDEW_MAP = Maps.newHashMap();
    private static final String RECIPE_GROUP_PREFIX_WOODEN = "wooden";
    private static final String RECIPE_UNLOCKED_BY_HAS_PLANKS = "has_planks";

    public static final BlockFamily BACCHENITE_BRICKS = familyBuilder(MirthdewEncoreBlocks.BACCHENITE_BRICKS)
            .stairs(MirthdewEncoreBlocks.BACCHENITE_BRICK_STAIRS)
            .slab(MirthdewEncoreBlocks.BACCHENITE_BRICK_SLAB)
            .wall(MirthdewEncoreBlocks.BACCHENITE_BRICK_WALL)
            .getFamily();
    public static final BlockFamily BACCHENITE_TILES = familyBuilder(MirthdewEncoreBlocks.BACCHENITE_TILES)
            .stairs(MirthdewEncoreBlocks.BACCHENITE_TILE_STAIRS)
            .slab(MirthdewEncoreBlocks.BACCHENITE_TILE_SLAB)
            .wall(MirthdewEncoreBlocks.BACCHENITE_TILE_WALL)
            .getFamily();

    public static final BlockFamily UNGUISHALE = familyBuilder(MirthdewEncoreBlocks.UNGUISHALE)
            .stairs(MirthdewEncoreBlocks.UNGUISHALE_STAIRS)
            .slab(MirthdewEncoreBlocks.UNGUISHALE_SLAB)
            .wall(MirthdewEncoreBlocks.UNGUISHALE_WALL)
            .dontGenerateModel()
            .getFamily();
    public static final BlockFamily UNGUISHALE_BRICKS = familyBuilder(MirthdewEncoreBlocks.UNGUISHALE_BRICKS)
            .stairs(MirthdewEncoreBlocks.UNGUISHALE_BRICK_STAIRS)
            .slab(MirthdewEncoreBlocks.UNGUISHALE_BRICK_SLAB)
            .wall(MirthdewEncoreBlocks.UNGUISHALE_BRICK_WALL)
            .getFamily();
    public static final BlockFamily UNGUISHALE_TILES = familyBuilder(MirthdewEncoreBlocks.UNGUISHALE_TILES)
            .stairs(MirthdewEncoreBlocks.UNGUISHALE_TILE_STAIRS)
            .slab(MirthdewEncoreBlocks.UNGUISHALE_TILE_SLAB)
            .wall(MirthdewEncoreBlocks.UNGUISHALE_TILE_WALL)
            .getFamily();

    public static final BlockFamily CLINKERA_PLANKS = familyBuilder(MirthdewEncoreBlocks.CLINKERA_PLANKS)
            .button(MirthdewEncoreBlocks.CLINKERA_BUTTON)
            .fence(MirthdewEncoreBlocks.CLINKERA_FENCE)
            .fenceGate(MirthdewEncoreBlocks.CLINKERA_FENCE_GATE)
            .pressurePlate(MirthdewEncoreBlocks.CLINKERA_PRESSURE_PLATE)
            .slab(MirthdewEncoreBlocks.CLINKERA_SLAB)
            .stairs(MirthdewEncoreBlocks.CLINKERA_STAIRS)
            .door(MirthdewEncoreBlocks.CLINKERA_DOOR)
            .trapdoor(MirthdewEncoreBlocks.CLINKERA_TRAPDOOR)
            .recipeGroupPrefix(RECIPE_GROUP_PREFIX_WOODEN)
            .recipeUnlockedBy(RECIPE_UNLOCKED_BY_HAS_PLANKS)
            .getFamily();

    public static final BlockFamily ROUGH_GACHERIMM = familyBuilder(MirthdewEncoreBlocks.ROUGH_GACHERIMM)
            .stairs(MirthdewEncoreBlocks.ROUGH_GACHERIMM_STAIRS)
            .slab(MirthdewEncoreBlocks.ROUGH_GACHERIMM_SLAB)
            .wall(MirthdewEncoreBlocks.ROUGH_GACHERIMM_WALL)
            .getFamily();
    public static final BlockFamily GACHERIMM_BRICKS = familyBuilder(MirthdewEncoreBlocks.GACHERIMM_BRICKS)
            .stairs(MirthdewEncoreBlocks.GACHERIMM_BRICK_STAIRS)
            .slab(MirthdewEncoreBlocks.GACHERIMM_BRICK_SLAB)
            .wall(MirthdewEncoreBlocks.GACHERIMM_BRICK_WALL)
            .getFamily();
    public static final BlockFamily GACHERIMM_TILES = familyBuilder(MirthdewEncoreBlocks.GACHERIMM_TILES)
            .stairs(MirthdewEncoreBlocks.GACHERIMM_TILE_STAIRS)
            .slab(MirthdewEncoreBlocks.GACHERIMM_TILE_SLAB)
            .wall(MirthdewEncoreBlocks.GACHERIMM_TILE_WALL)
            .getFamily();
    public static final BlockFamily POLISHED_GACHERIMM = familyBuilder(MirthdewEncoreBlocks.POLISHED_GACHERIMM)
            .stairs(MirthdewEncoreBlocks.POLISHED_GACHERIMM_STAIRS)
            .slab(MirthdewEncoreBlocks.POLISHED_GACHERIMM_SLAB)
            .wall(MirthdewEncoreBlocks.POLISHED_GACHERIMM_WALL)
            .cut(MirthdewEncoreBlocks.CUT_POLISHED_GACHERIMM)
            .getFamily();
    public static final BlockFamily CUT_POLISHED_GACHERIMM = familyBuilder(MirthdewEncoreBlocks.CUT_POLISHED_GACHERIMM)
            .stairs(MirthdewEncoreBlocks.CUT_POLISHED_GACHERIMM_STAIRS)
            .slab(MirthdewEncoreBlocks.CUT_POLISHED_GACHERIMM_SLAB)
            .getFamily();

    public static final BlockFamily REVERIME = familyBuilder(MirthdewEncoreBlocks.REVERIME)
            .stairs(MirthdewEncoreBlocks.REVERIME_STAIRS)
            .slab(MirthdewEncoreBlocks.REVERIME_SLAB)
            .wall(MirthdewEncoreBlocks.REVERIME_WALL)
            .getFamily();
    public static final BlockFamily REVERIME_BRICKS = familyBuilder(MirthdewEncoreBlocks.REVERIME_BRICKS)
            .stairs(MirthdewEncoreBlocks.REVERIME_BRICK_STAIRS)
            .slab(MirthdewEncoreBlocks.REVERIME_BRICK_SLAB)
            .wall(MirthdewEncoreBlocks.REVERIME_BRICK_WALL)
            .getFamily();
    public static final BlockFamily REVERIME_TILES = familyBuilder(MirthdewEncoreBlocks.REVERIME_TILES)
            .stairs(MirthdewEncoreBlocks.REVERIME_TILE_STAIRS)
            .slab(MirthdewEncoreBlocks.REVERIME_TILE_SLAB)
            .wall(MirthdewEncoreBlocks.REVERIME_TILE_WALL)
            .getFamily();
    public static final BlockFamily POLISHED_REVERIME = familyBuilder(MirthdewEncoreBlocks.POLISHED_REVERIME)
            .stairs(MirthdewEncoreBlocks.POLISHED_REVERIME_STAIRS)
            .slab(MirthdewEncoreBlocks.POLISHED_REVERIME_SLAB)
            .wall(MirthdewEncoreBlocks.POLISHED_REVERIME_WALL)
            .getFamily();
    public static final BlockFamily POLISHED_REVERIME_BRICKS = familyBuilder(MirthdewEncoreBlocks.POLISHED_REVERIME_BRICKS)
            .stairs(MirthdewEncoreBlocks.POLISHED_REVERIME_BRICK_STAIRS)
            .slab(MirthdewEncoreBlocks.POLISHED_REVERIME_BRICK_SLAB)
            .wall(MirthdewEncoreBlocks.POLISHED_REVERIME_BRICK_WALL)
            .getFamily();
    public static final BlockFamily CUT_POLISHED_REVERIME = familyBuilder(MirthdewEncoreBlocks.CUT_POLISHED_REVERIME)
            .stairs(MirthdewEncoreBlocks.CUT_POLISHED_REVERIME_STAIRS)
            .slab(MirthdewEncoreBlocks.CUT_POLISHED_REVERIME_SLAB)
            .getFamily();

    public static final BlockFamily SCARABRIM = familyBuilder(MirthdewEncoreBlocks.SCARABRIM)
            .stairs(MirthdewEncoreBlocks.SCARABRIM_STAIRS)
            .slab(MirthdewEncoreBlocks.SCARABRIM_SLAB)
            .wall(MirthdewEncoreBlocks.SCARABRIM_WALL)
            .polished(MirthdewEncoreBlocks.POLISHED_SCARABRIM)
            .getFamily();
    public static final BlockFamily POLISHED_SCARBRIM = familyBuilder(MirthdewEncoreBlocks.POLISHED_SCARABRIM)
            .stairs(MirthdewEncoreBlocks.POLISHED_SCARABRIM_STAIRS)
            .slab(MirthdewEncoreBlocks.POLISHED_SCARABRIM_SLAB)
            .wall(MirthdewEncoreBlocks.POLISHED_SCARABRIM_WALL)
            .getFamily();
    public static final BlockFamily SCARABRIM_BRICKS = familyBuilder(MirthdewEncoreBlocks.SCARABRIM_BRICKS)
            .stairs(MirthdewEncoreBlocks.SCARABRIM_BRICK_STAIRS)
            .slab(MirthdewEncoreBlocks.SCARABRIM_BRICK_SLAB)
            .wall(MirthdewEncoreBlocks.SCARABRIM_BRICK_WALL)
            .getFamily();

    public static final BlockFamily DECIDRHEUM_PLANKS = familyBuilder(MirthdewEncoreBlocks.DECIDRHEUM_PLANKS)
            .button(MirthdewEncoreBlocks.DECIDRHEUM_BUTTON)
            .fence(MirthdewEncoreBlocks.DECIDRHEUM_FENCE)
            .fenceGate(MirthdewEncoreBlocks.DECIDRHEUM_FENCE_GATE)
            .pressurePlate(MirthdewEncoreBlocks.DECIDRHEUM_PRESSURE_PLATE)
            .slab(MirthdewEncoreBlocks.DECIDRHEUM_SLAB)
            .stairs(MirthdewEncoreBlocks.DECIDRHEUM_STAIRS)
            .door(MirthdewEncoreBlocks.DECIDRHEUM_DOOR)
            .trapdoor(MirthdewEncoreBlocks.DECIDRHEUM_TRAPDOOR)
            .recipeGroupPrefix(RECIPE_GROUP_PREFIX_WOODEN)
            .recipeUnlockedBy(RECIPE_UNLOCKED_BY_HAS_PLANKS)
            .getFamily();

    public static final BlockFamily PSYRITE_BLOCK = familyBuilder(MirthdewEncoreBlocks.PSYRITE_BLOCK)
            .cut(MirthdewEncoreBlocks.CUT_PSYRITE)
            .door(MirthdewEncoreBlocks.PSYRITE_DOOR)
            .trapdoor(MirthdewEncoreBlocks.PSYRITE_TRAPDOOR)
            .getFamily();
    public static final BlockFamily CUT_PSYRITE = familyBuilder(MirthdewEncoreBlocks.CUT_PSYRITE)
            .stairs(MirthdewEncoreBlocks.CUT_PSYRITE_STAIRS)
            .slab(MirthdewEncoreBlocks.CUT_PSYRITE_SLAB)
            .chiseled(MirthdewEncoreBlocks.CHISELED_PSYRITE)
            .getFamily();
    public static final BlockFamily PSYRITE_GRATE = familyBuilder(MirthdewEncoreBlocks.PSYRITE_GRATE)
            .slab(MirthdewEncoreBlocks.PSYRITE_GRATE_SLAB)
            .getFamily();


    public static List<Block> NOVACLAGS = List.of(
            MirthdewEncoreBlocks.ORANGE_NOVACLAG,
            MirthdewEncoreBlocks.LIME_NOVACLAG,
            MirthdewEncoreBlocks.CYAN_NOVACLAG,
            MirthdewEncoreBlocks.MAGNETA_NOVACLAG
    );

    public static BlockFamily.Builder familyBuilder(Block baseBlock) {
        BlockFamily.Builder builder = new BlockFamily.Builder(baseBlock);
        BlockFamily blockFamily = MIRTHDEW_MAP.put(baseBlock, builder.getFamily());
        if (blockFamily != null) {
            throw new IllegalStateException("Duplicate family definition for " + BuiltInRegistries.BLOCK.getKey(baseBlock));
        } else {
            return builder;
        }
    }

    public static Stream<BlockFamily> getAllMirthdewEncoreFamilies() {
        return MIRTHDEW_MAP.values().stream();
    }
}
