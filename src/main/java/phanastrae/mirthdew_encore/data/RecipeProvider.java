package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {

    public RecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MirthdewEncoreItems.SLUMBERSOCKET, 1)
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
    }
}
