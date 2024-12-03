package phanastrae.mirthdew_encore.fabric.data;

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
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

import static net.minecraft.data.models.BlockModelGenerators.createRotatedVariant;
import static net.minecraft.data.models.BlockModelGenerators.createSimpleBlock;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators BMG) {
        BMG.createAirLikeBlock(MirthdewEncoreBlocks.DREAMTWIRL_BARRIER, Items.BARRIER);
        this.createSlumberveil(BMG, MirthdewEncoreBlocks.SLUMBERVEIL);

        createTrivialCubes(BMG,
                MirthdewEncoreBlocks.REVERIME,
                MirthdewEncoreBlocks.FROSTED_REVERIME,
                MirthdewEncoreBlocks.POLISHED_REVERIME,
                MirthdewEncoreBlocks.REVERIME_BRICKS,
                MirthdewEncoreBlocks.REVERIME_TILES,
                MirthdewEncoreBlocks.ROSENGLACE,
                MirthdewEncoreBlocks.SCARABRIM);

        generateGrassLike(BMG, MirthdewEncoreBlocks.SUNFLECKED_SCARABRIM, MirthdewEncoreBlocks.SCARABRIM);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        generateFlat(itemModelGenerator, MirthdewEncoreItems.SPELL_CARD);
        generateFlat(itemModelGenerator, MirthdewEncoreItems.SPELL_DECK);
        generateFlat(itemModelGenerator, MirthdewEncoreItems.MIRTHDEW_VIAL);
        generateFlat(itemModelGenerator, MirthdewEncoreItems.SPECTRAL_CANDY);
        generateFlat(itemModelGenerator, MirthdewEncoreItems.SLUMBERING_EYE);
    }

    private static void generateFlat(ItemModelGenerators itemModelGenerator, Item item) {
        itemModelGenerator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }

    private void createTrivialCubes(BlockModelGenerators BMG, Block... blocks) {
        for(Block block : blocks) {
            BMG.createTrivialCube(block);
        }
    }

    private void createSlumberveil(BlockModelGenerators BMG, Block block) {
        BMG.blockStateOutput
                .accept(
                        MultiVariantGenerator.multiVariant(block)
                                .with(
                                        PropertyDispatch.property(BlockStateProperties.HORIZONTAL_AXIS)
                                                .select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block, "_ns")))
                                                .select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block, "_ew")))
                                )
                );
    }

    private void generateGrassLike(BlockModelGenerators BMG, Block block, Block baseBlock) {
        TextureMapping textureMapping = new TextureMapping()
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(baseBlock))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"));
        BMG.blockStateOutput.accept(createRotatedVariant(block, ModelTemplates.CUBE_BOTTOM_TOP.create(block, textureMapping, BMG.modelOutput)));
    }
}
