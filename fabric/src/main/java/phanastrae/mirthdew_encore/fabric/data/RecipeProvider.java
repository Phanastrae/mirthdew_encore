package phanastrae.mirthdew_encore.fabric.data;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.*;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.data.MirthdewEncoreBlockFamilies;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreItemTags;

import java.util.concurrent.CompletableFuture;

import static phanastrae.mirthdew_encore.item.MirthdewEncoreItems.*;

public class RecipeProvider extends FabricRecipeProvider {
    private static final ImmutableList<ItemLike> PSYRITE_SMELTABLES = ImmutableList.of(
            GACHERIMM_PSYRITE_ORE,
            SCARABRIM_PSYRITE_ORE,
            SUNSLAKED_PSYRITE_ORE,
            RAW_PSYRITE
    );

    public RecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        generateForEnabledBlockFamilies(exporter, FeatureFlagSet.of(FeatureFlags.VANILLA));

        // crafting
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SLUMBERSOCKET, 1)
                .define('B', Items.IRON_BARS)
                .define('C', Items.CRYING_OBSIDIAN)
                .define('D', Items.POLISHED_DEEPSLATE)
                .define('G', Items.GLOWSTONE)
                .define('S', Items.SCULK)
                .pattern("DDD")
                .pattern("SBS")
                .pattern("CGC")
                .unlockedBy(
                        getHasName(Items.SCULK),
                        has(Items.SCULK)
                )
                .save(exporter);

        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, BACCHENITE_BLOCK, BACCHENITE_SHARD);
        savePolished(BACCHENITE_BRICKS, BACCHENITE_BLOCK, exporter);
        savePolished(BACCHENITE_TILES, BACCHENITE_BRICKS, exporter);

        savePolished(UNGUISHALE_BRICKS, UNGUISHALE, exporter);
        savePolished(UNGUISHALE_TILES, UNGUISHALE_BRICKS, exporter);

        savePolished(CLINKERA_PLANKS, CLINKERA_SCRAPS, exporter);

        savePolished(GACHERIMM_BRICKS, ROUGH_GACHERIMM, exporter);
        savePolished(GACHERIMM_TILES, GACHERIMM_BRICKS, exporter);
        savePolished(POLISHED_GACHERIMM, GACHERIMM, exporter);

        saveCoarseDirtEsque(FROSTED_REVERIME, REVERIME, POLISHED_REVERIME, exporter);

        savePolished(REVERIME_BRICKS, REVERIME, exporter);
        savePolished(REVERIME_TILES, REVERIME_BRICKS, exporter);
        savePolished(POLISHED_REVERIME, FROSTED_REVERIME, exporter);
        savePolished(POLISHED_REVERIME_BRICKS, POLISHED_REVERIME, exporter);
        savePolished(CUT_POLISHED_REVERIME, POLISHED_REVERIME_BRICKS, exporter);

        savePolished(SCARABRIM_BRICKS, POLISHED_SCARABRIM, exporter);

        woodFromLogs(exporter, DECIDRHEUM_WOOD, DECIDRHEUM_LOG);
        woodFromLogs(exporter, STRIPPED_DECIDRHEUM_WOOD, STRIPPED_DECIDRHEUM_LOG);
        planksFromLogs(exporter, DECIDRHEUM_PLANKS, MirthdewEncoreItemTags.DECIDRHEUM_LOGS, 4);

        saveLattice(CLINKERA_LATTICE, CLINKERA_PLANKS, exporter, 12);
        saveLattice(DECIDRHEUM_LATTICE, DECIDRHEUM_PLANKS, exporter, 12);

        nineBlockStorageRecipes(exporter, RecipeCategory.MISC, RAW_PSYRITE, RecipeCategory.BUILDING_BLOCKS, RAW_PSYRITE_BLOCK);
        nineBlockStorageRecipesWithCustomPacking(
                exporter, RecipeCategory.MISC, PSYRITE_NUGGET, RecipeCategory.MISC, PSYRITE_INGOT, "psyrite_ingot_from_nuggets", "psyrite_ingot"
        );
        nineBlockStorageRecipesRecipesWithCustomUnpacking(
                exporter,
                RecipeCategory.MISC,
                PSYRITE_INGOT,
                RecipeCategory.BUILDING_BLOCKS,
                PSYRITE_BLOCK,
                getSimpleRecipeName(PSYRITE_INGOT),
                getItemName(PSYRITE_INGOT)
        );
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, PSYRITE_PILLAR, 2)
                .define('#', PSYRITE_BLOCK)
                .pattern("#")
                .pattern("#")
                .unlockedBy("has_psyrite_block", has(PSYRITE_BLOCK))
                .unlockedBy("has_psyrite_pillar", has(PSYRITE_PILLAR))
                .save(exporter);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PSYRITE_BARS, 16)
                .define('#', PSYRITE_INGOT)
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_psyrite_ingot", has(PSYRITE_INGOT))
                .save(exporter);
        saveLattice(PSYRITE_LATTICE, PSYRITE_INGOT, exporter, 12);
        grate(exporter, MirthdewEncoreBlocks.PSYRITE_GRATE, MirthdewEncoreBlocks.PSYRITE_BLOCK);

        // smelting, smoking, blasting
        smeltingResultFromBase(exporter, GACHERIMM, ROUGH_GACHERIMM);

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(UNGUISHALE), RecipeCategory.FOOD, CLINKERA_SCRAPS, 0.1F, 200)
                .unlockedBy("has_unguishale", has(UNGUISHALE))
                .save(exporter);
        mirthdewCookRecipes(exporter, "smoking", RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new, 100);
        mirthdewCookRecipes(exporter, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, CampfireCookingRecipe::new, 600);

        oreSmelting(exporter, PSYRITE_SMELTABLES, RecipeCategory.MISC, PSYRITE_INGOT, 0.7F, 200, "psyrite_ingot");
        oreBlasting(exporter, PSYRITE_SMELTABLES, RecipeCategory.MISC, PSYRITE_INGOT, 0.7F, 100, "psyrite_ingot");

        // stonecutting
        scBlocks(exporter, BACCHENITE_BRICKS, 1, BACCHENITE_BLOCK);
        scBlocks(exporter, BACCHENITE_BRICK_STAIRS, 1, BACCHENITE_BRICKS, BACCHENITE_BLOCK);
        scBlocks(exporter, BACCHENITE_BRICK_SLAB, 2, BACCHENITE_BRICKS, BACCHENITE_BLOCK);
        scWalls(exporter, BACCHENITE_BRICK_WALL, 1, BACCHENITE_BRICKS, BACCHENITE_BLOCK);

        scBlocks(exporter, BACCHENITE_TILES, 1, BACCHENITE_BLOCK);
        scBlocks(exporter, BACCHENITE_TILE_STAIRS, 1, BACCHENITE_TILES, BACCHENITE_BRICKS, BACCHENITE_BLOCK);
        scBlocks(exporter, BACCHENITE_TILE_SLAB, 2, BACCHENITE_TILES, BACCHENITE_BRICKS, BACCHENITE_BLOCK);
        scWalls(exporter, BACCHENITE_TILE_WALL, 1, BACCHENITE_TILES, BACCHENITE_BRICKS, BACCHENITE_BLOCK);

        scBlocks(exporter, UNGUISHALE_STAIRS, 1, UNGUISHALE);
        scBlocks(exporter, UNGUISHALE_SLAB, 2, UNGUISHALE);
        scWalls(exporter, UNGUISHALE_WALL, 1, UNGUISHALE);

        scBlocks(exporter, UNGUISHALE_BRICKS, 1, UNGUISHALE);
        scBlocks(exporter, UNGUISHALE_BRICK_STAIRS, 1, UNGUISHALE_BRICKS, UNGUISHALE);
        scBlocks(exporter, UNGUISHALE_BRICK_SLAB, 2, UNGUISHALE_BRICKS, UNGUISHALE);
        scWalls(exporter, UNGUISHALE_BRICK_WALL, 1, UNGUISHALE_BRICKS, UNGUISHALE);

        scBlocks(exporter, UNGUISHALE_TILES, 1, UNGUISHALE_BRICKS, UNGUISHALE);
        scBlocks(exporter, UNGUISHALE_TILE_STAIRS, 1, UNGUISHALE_TILES, UNGUISHALE_BRICKS, UNGUISHALE);
        scBlocks(exporter, UNGUISHALE_TILE_SLAB, 2, UNGUISHALE_TILES, UNGUISHALE_BRICKS, UNGUISHALE);
        scWalls(exporter, UNGUISHALE_TILE_WALL, 1, UNGUISHALE_TILES, UNGUISHALE_BRICKS, UNGUISHALE);

        scBlocks(exporter, ROUGH_GACHERIMM_STAIRS, 1, ROUGH_GACHERIMM);
        scBlocks(exporter, ROUGH_GACHERIMM_SLAB, 2, ROUGH_GACHERIMM);
        scWalls(exporter, ROUGH_GACHERIMM_WALL, 1, ROUGH_GACHERIMM);

        scBlocks(exporter, GACHERIMM_BRICKS, 1, ROUGH_GACHERIMM);
        scBlocks(exporter, GACHERIMM_BRICK_STAIRS, 1, GACHERIMM_BRICKS, ROUGH_GACHERIMM);
        scBlocks(exporter, GACHERIMM_BRICK_SLAB, 2, GACHERIMM_BRICKS, ROUGH_GACHERIMM);
        scWalls(exporter, GACHERIMM_BRICK_WALL, 1, GACHERIMM_BRICKS, ROUGH_GACHERIMM);

        scBlocks(exporter, GACHERIMM_TILES, 1, GACHERIMM_BRICKS, ROUGH_GACHERIMM);
        scBlocks(exporter, GACHERIMM_TILE_STAIRS, 1, GACHERIMM_TILES, GACHERIMM_BRICKS, ROUGH_GACHERIMM);
        scBlocks(exporter, GACHERIMM_TILE_SLAB, 2, GACHERIMM_TILES, GACHERIMM_BRICKS, ROUGH_GACHERIMM);
        scWalls(exporter, GACHERIMM_TILE_WALL, 1, GACHERIMM_TILES, GACHERIMM_BRICKS, ROUGH_GACHERIMM);

        scBlocks(exporter, POLISHED_GACHERIMM, 1, GACHERIMM);
        scBlocks(exporter, POLISHED_GACHERIMM_STAIRS, 1, POLISHED_GACHERIMM, GACHERIMM);
        scBlocks(exporter, POLISHED_GACHERIMM_SLAB, 2, POLISHED_GACHERIMM, GACHERIMM);
        scWalls(exporter, POLISHED_GACHERIMM_WALL, 1, POLISHED_GACHERIMM, GACHERIMM);

        // gacherimm is cheap, so cut gacherimm is 1:1 like sandstone, not 1:4 like copper
        scBlocks(exporter, CUT_POLISHED_GACHERIMM, 1, POLISHED_GACHERIMM, GACHERIMM);
        scBlocks(exporter, CUT_POLISHED_GACHERIMM_STAIRS, 1, CUT_POLISHED_GACHERIMM, POLISHED_GACHERIMM, GACHERIMM);
        scBlocks(exporter, CUT_POLISHED_GACHERIMM_SLAB, 2, CUT_POLISHED_GACHERIMM, POLISHED_GACHERIMM, GACHERIMM);

        scBlocks(exporter, REVERIME_STAIRS, 1, REVERIME);
        scBlocks(exporter, REVERIME_SLAB, 2, REVERIME);
        scWalls(exporter, REVERIME_WALL, 1, REVERIME);

        scBlocks(exporter, REVERIME_BRICKS, 1, REVERIME);
        scBlocks(exporter, REVERIME_BRICK_STAIRS, 1, REVERIME_BRICKS, REVERIME);
        scBlocks(exporter, REVERIME_BRICK_SLAB, 2, REVERIME_BRICKS, REVERIME);
        scWalls(exporter, REVERIME_BRICK_WALL, 1, REVERIME_BRICKS, REVERIME);

        scBlocks(exporter, REVERIME_TILES, 1, REVERIME_BRICKS, REVERIME);
        scBlocks(exporter, REVERIME_TILE_STAIRS, 1, REVERIME_TILES, REVERIME_BRICKS, REVERIME);
        scBlocks(exporter, REVERIME_TILE_SLAB, 2, REVERIME_TILES, REVERIME_BRICKS, REVERIME);
        scWalls(exporter, REVERIME_TILE_WALL, 1, REVERIME_TILES, REVERIME_BRICKS, REVERIME);

        // let players use the stonecutter to skip crafting frosted reverime and just turn reverime directly into polished reverime
        scBlocks(exporter, POLISHED_REVERIME, 1, FROSTED_REVERIME, REVERIME);
        scBlocks(exporter, POLISHED_REVERIME_STAIRS, 1, POLISHED_REVERIME, FROSTED_REVERIME, REVERIME);
        scBlocks(exporter, POLISHED_REVERIME_SLAB, 2, POLISHED_REVERIME, FROSTED_REVERIME, REVERIME);
        scWalls(exporter, POLISHED_REVERIME_WALL, 1, POLISHED_REVERIME, FROSTED_REVERIME, REVERIME);

        // reverime is cheap, so cut reverime is 1:1 like sandstone, not 1:4 like copper
        scBlocks(exporter, CUT_POLISHED_REVERIME, 1, POLISHED_REVERIME, FROSTED_REVERIME, REVERIME);
        scBlocks(exporter, CUT_POLISHED_REVERIME_STAIRS, 1, CUT_POLISHED_REVERIME, POLISHED_REVERIME, FROSTED_REVERIME, REVERIME);
        scBlocks(exporter, CUT_POLISHED_REVERIME_SLAB, 2, CUT_POLISHED_REVERIME, POLISHED_REVERIME, FROSTED_REVERIME, REVERIME);

        scBlocks(exporter, SCARABRIM_STAIRS, 1, SCARABRIM);
        scBlocks(exporter, SCARABRIM_SLAB, 2, SCARABRIM);
        scWalls(exporter, SCARABRIM_WALL, 1, SCARABRIM);

        scBlocks(exporter, POLISHED_SCARABRIM, 1, SCARABRIM);
        scBlocks(exporter, POLISHED_SCARABRIM_STAIRS, 1, POLISHED_SCARABRIM, SCARABRIM);
        scBlocks(exporter, POLISHED_SCARABRIM_SLAB, 2, POLISHED_SCARABRIM, SCARABRIM);
        scWalls(exporter, POLISHED_SCARABRIM_WALL, 1, POLISHED_SCARABRIM, SCARABRIM);

        scBlocks(exporter, SCARABRIM_BRICKS, 1, POLISHED_SCARABRIM, SCARABRIM);
        scBlocks(exporter, SCARABRIM_BRICK_STAIRS, 1, SCARABRIM_BRICKS, POLISHED_SCARABRIM, SCARABRIM);
        scBlocks(exporter, SCARABRIM_BRICK_SLAB, 2, SCARABRIM_BRICKS, POLISHED_SCARABRIM, SCARABRIM);
        scWalls(exporter, SCARABRIM_BRICK_WALL, 1, SCARABRIM_BRICKS, POLISHED_SCARABRIM, SCARABRIM);

        // psyrite -> cut psyrite is 1:4 like copper
        scBlocks(exporter, CUT_PSYRITE, 4, PSYRITE_BLOCK);

        scBlocks(exporter, CUT_PSYRITE_STAIRS, 1, CUT_PSYRITE);
        scBlocks(exporter, CUT_PSYRITE_SLAB, 2, CUT_PSYRITE);
        scBlocks(exporter, CHISELED_PSYRITE, 1, CUT_PSYRITE);

        scBlocks(exporter, CUT_PSYRITE_STAIRS, 4, PSYRITE_BLOCK);
        scBlocks(exporter, CUT_PSYRITE_SLAB, 8, PSYRITE_BLOCK);
        scBlocks(exporter, CHISELED_PSYRITE, 4, PSYRITE_BLOCK);

        // psyrite -> psyrite grate is 1:4 like copper
        scBlocks(exporter, PSYRITE_GRATE, 4, PSYRITE_BLOCK);
        scBlocks(exporter, PSYRITE_GRATE_SLAB, 2, PSYRITE_GRATE);
        scBlocks(exporter, PSYRITE_GRATE_SLAB, 8, PSYRITE_BLOCK);
    }

    public static void scBlocks(RecipeOutput recipeOutput, ItemLike result, int amount, ItemLike... materials) {
        for(ItemLike material : materials) {
            scBuildingBlock(recipeOutput, result, material, amount);
        }
    }

    public static void scWalls(RecipeOutput recipeOutput, ItemLike result, int amount, ItemLike... materials) {
        for(ItemLike material : materials) {
            scDecoration(recipeOutput, result, material, amount);
        }
    }

    public static void scBuildingBlock(RecipeOutput recipeOutput, ItemLike result, ItemLike material, int amount) {
        // use this for non-walls
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, result, material, amount);
    }

    public static void scDecoration(RecipeOutput recipeOutput, ItemLike result, ItemLike material, int amount) {
        // use this for walls
        stonecutterResultFromBase(recipeOutput, RecipeCategory.DECORATIONS, result, material, amount);
    }

    public static <T extends AbstractCookingRecipe> void mirthdewCookRecipes(
            RecipeOutput recipeOutput, String cookingMethod, RecipeSerializer<T> cookingSerializer, AbstractCookingRecipe.Factory<T> recipeFactory, int cookingTime
    ) {
        simpleCookingRecipe(recipeOutput, cookingMethod, cookingSerializer, recipeFactory, cookingTime, UNGUISHALE, CLINKERA_SCRAPS, 0.1F);
    }

    public static void generateForEnabledBlockFamilies(RecipeOutput recipeOutput, FeatureFlagSet enabledFeatures) {
        MirthdewEncoreBlockFamilies
                .getAllMirthdewEncoreFamilies()
                .filter(BlockFamily::shouldGenerateRecipe)
                .forEach(blockFamily -> generateRecipes(recipeOutput, blockFamily, enabledFeatures));
    }

    private static void savePolished(ItemLike polished, ItemLike material, RecipeOutput exporter) {
        polished(exporter, RecipeCategory.BUILDING_BLOCKS, polished, material);
    }

    private static void saveLattice(ItemLike lattice, ItemLike material, RecipeOutput exporter, int count) {
        latticeBuilder(lattice, Ingredient.of(material), count)
                .unlockedBy(getHasName(material), has(material))
                .save(exporter);
    }

    private static RecipeBuilder latticeBuilder(ItemLike lattice, Ingredient material, int count) {
        return ShapedRecipeBuilder
                .shaped(RecipeCategory.DECORATIONS, lattice, count)
                .define('#', material)
                .pattern("# #")
                .pattern(" # ")
                .pattern("# #");
    }

    private static void saveCoarseDirtEsque(ItemLike lattice, ItemLike material, ItemLike secondaryMaterial, RecipeOutput exporter) {
        coarseDirtEsqueBuilder(lattice, Ingredient.of(material), Ingredient.of(secondaryMaterial))
                .unlockedBy(getHasName(secondaryMaterial), has(secondaryMaterial))
                .save(exporter);
    }

    private static RecipeBuilder coarseDirtEsqueBuilder(ItemLike output, Ingredient material, Ingredient secondaryMaterial) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 4)
                .define('D', material)
                .define('G', secondaryMaterial)
                .pattern("DG")
                .pattern("GD");
    }
}
