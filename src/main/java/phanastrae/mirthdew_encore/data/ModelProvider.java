package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerBuiltinWithParticle(MirthdewEncoreBlocks.DREAMTWIRL_BARRIER, Items.BARRIER);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        registerGenerated(itemModelGenerator, MirthdewEncoreItems.SPELL_CARD);
        registerGenerated(itemModelGenerator, MirthdewEncoreItems.SPELL_DECK);
    }

    private static void registerGenerated(ItemModelGenerator itemModelGenerator, Item item) {
        itemModelGenerator.register(item, Models.GENERATED);
    }
}
