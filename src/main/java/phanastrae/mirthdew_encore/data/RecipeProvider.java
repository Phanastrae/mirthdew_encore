package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {

    public RecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, MirthdewEncoreItems.SLUMBERSOCKET, 1)
                .input('B', Items.IRON_BARS)
                .input('C', Items.CRYING_OBSIDIAN)
                .input('D', Items.POLISHED_DEEPSLATE)
                .input('G', Items.GLOWSTONE)
                .input('S', Items.SCULK)
                .pattern("DDD")
                .pattern("SBS")
                .pattern("CGC")
                .criterion(
                        hasItem(Items.SCULK),
                        conditionsFromItem(Items.SCULK)
                )
                .offerTo(exporter);
    }
}
