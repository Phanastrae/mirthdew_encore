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
import net.minecraft.world.level.block.Blocks;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.data.MirthdewEncoreBlockFamilies;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;
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

        // smelting
        smeltingResultFromBase(exporter, GACHERIMM, ROUGH_GACHERIMM);

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(UNGUISHALE), RecipeCategory.FOOD, CLINKERA_SCRAPS, 0.1F, 200)
                .unlockedBy("has_unguishale", has(UNGUISHALE))
                .save(exporter);
        mirthdewCookRecipes(exporter, "smoking", RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new, 100);
        mirthdewCookRecipes(exporter, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING_RECIPE, CampfireCookingRecipe::new, 600);

        oreSmelting(exporter, PSYRITE_SMELTABLES, RecipeCategory.MISC, PSYRITE_INGOT, 0.7F, 200, "psyrite_ingot");
        oreBlasting(exporter, PSYRITE_SMELTABLES, RecipeCategory.MISC, PSYRITE_INGOT, 0.7F, 100, "psyrite_ingot");

        // TODO stonecutter recipes
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
