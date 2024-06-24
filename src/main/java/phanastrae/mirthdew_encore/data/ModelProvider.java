package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator BSMG) {
        BSMG.registerBuiltinWithParticle(MirthdewEncoreBlocks.DREAMTWIRL_BARRIER, Items.BARRIER);
        this.registerSlumberveil(BSMG);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        registerGenerated(itemModelGenerator, MirthdewEncoreItems.SPELL_CARD);
        registerGenerated(itemModelGenerator, MirthdewEncoreItems.SPELL_DECK);
    }

    private static void registerGenerated(ItemModelGenerator itemModelGenerator, Item item) {
        itemModelGenerator.register(item, Models.GENERATED);
    }

    private void registerSlumberveil(BlockStateModelGenerator BSMG) {
        BSMG.blockStateCollector
                .accept(
                        VariantsBlockStateSupplier.create(MirthdewEncoreBlocks.SLUMBERVEIL)
                                .coordinate(
                                        BlockStateVariantMap.create(Properties.HORIZONTAL_AXIS)
                                                .register(Direction.Axis.X, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(MirthdewEncoreBlocks.SLUMBERVEIL, "_ns")))
                                                .register(Direction.Axis.Z, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(MirthdewEncoreBlocks.SLUMBERVEIL, "_ew")))
                                )
                );
    }
}
