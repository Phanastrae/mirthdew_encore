package phanastrae.mirthdew_encore.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators BSMG) {
        BSMG.createAirLikeBlock(MirthdewEncoreBlocks.DREAMTWIRL_BARRIER, Items.BARRIER);
        this.registerSlumberveil(BSMG);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        registerGenerated(itemModelGenerator, MirthdewEncoreItems.SPELL_CARD);
        registerGenerated(itemModelGenerator, MirthdewEncoreItems.SPELL_DECK);
        registerGenerated(itemModelGenerator, MirthdewEncoreItems.MIRTHDEW_VIAL);
        registerGenerated(itemModelGenerator, MirthdewEncoreItems.SPECTRAL_CANDY);
        registerGenerated(itemModelGenerator, MirthdewEncoreItems.SLUMBERING_EYE);
    }

    private static void registerGenerated(ItemModelGenerators itemModelGenerator, Item item) {
        itemModelGenerator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }

    private void registerSlumberveil(BlockModelGenerators BSMG) {
        BSMG.blockStateOutput
                .accept(
                        MultiVariantGenerator.multiVariant(MirthdewEncoreBlocks.SLUMBERVEIL)
                                .with(
                                        PropertyDispatch.property(BlockStateProperties.HORIZONTAL_AXIS)
                                                .select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(MirthdewEncoreBlocks.SLUMBERVEIL, "_ns")))
                                                .select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(MirthdewEncoreBlocks.SLUMBERVEIL, "_ew")))
                                )
                );
    }
}
