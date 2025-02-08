package phanastrae.mirthdew_encore.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import phanastrae.mirthdew_encore.data.MirthdewEncoreBlockFamilies;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

import java.util.function.BiConsumer;

import static net.minecraft.data.models.BlockModelGenerators.createRotatedVariant;
import static phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks.*;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators BMG) {
        MirthdewEncoreBlockFamilies
                .getAllMirthdewEncoreFamilies()
                .filter(BlockFamily::shouldGenerateModel)
                .forEach(blockFamily -> BMG.family(blockFamily.getBaseBlock()).generateFor(blockFamily));

        forMultiple(BMG, BlockModelGenerators::createTrivialCube,
                GREATER_ACHERUNE,
                ACHERUNE_HOLLOW,
                
                BACCHENITE_BLOCK,

                DECIDRHEUM_LEAVES,

                FROSTED_REVERIME,
                ROSENGLACE,

                CHALKTISSUE,
                FLAKING_CHALKTISSUE,
                SUNSLAKED_CHALKTISSUE,

                GACHERIMM_PSYRITE_ORE,
                SCARABRIM_PSYRITE_ORE,
                SUNSLAKED_PSYRITE_ORE,
                RAW_PSYRITE_BLOCK,

                GREATER_ACHERUNE_MARKER
        );

        forMultiple(BMG, BlockModelGenerators::createRotatedVariantBlock,
                ONYXSCALE
        );

        forMultiple(BMG, ModelProvider::generateLattice,
                CLINKERA_LATTICE,
                DECIDRHEUM_LATTICE,
                PSYRITE_BARS,
                PSYRITE_LATTICE
        );

        generateSlumberveil(BMG, SLUMBERVEIL);

        BMG.createRotatedPillarWithHorizontalVariant(GACHERIMM, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
        BMG.createRotatedPillarWithHorizontalVariant(PSYRITE_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);

        BMG.createAirLikeBlock(DREAMTWIRL_BARRIER, Items.BARRIER);

        generateGrassLike(BMG, SUNFLECKED_SCARABRIM, SCARABRIM);
        for(Block block : MirthdewEncoreBlockFamilies.NOVACLAGS) {
            generatePillarGrassLike(BMG, block, GACHERIMM);
        }
        generateGrassLike(BMG, RHEUMDAUBED_ONYXSCALE, ONYXSCALE);

        BMG.woodProvider(DECIDRHEUM_LOG).logWithHorizontal(DECIDRHEUM_LOG).wood(DECIDRHEUM_WOOD);
        BMG.woodProvider(STRIPPED_DECIDRHEUM_LOG).logWithHorizontal(STRIPPED_DECIDRHEUM_LOG).wood(STRIPPED_DECIDRHEUM_WOOD);

        BMG.createPlant(DECIDRHEUM_SAPLING, POTTED_DECIDRHEUM_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);

        BMG.createNetherRoots(RHEUMBRISTLES, POTTED_RHEUMBRISTLES);
        BMG.createPlant(SOULSPOT_MUSHRHEUM, POTTED_SOULSPOT_MUSHRHEUM, BlockModelGenerators.TintState.NOT_TINTED);

        BMG.createNetherRoots(ORANGE_FOGHAIR, POTTED_ORANGE_FOGHAIR);
        BMG.createNetherRoots(LIME_FOGHAIR, POTTED_LIME_FOGHAIR);
        BMG.createNetherRoots(CYAN_FOGHAIR, POTTED_CYAN_FOGHAIR);
        BMG.createNetherRoots(MAGNETA_FOGHAIR, POTTED_MAGNETA_FOGHAIR);

        // unguishale
        TextureMapping unguishaleMapping = TexturedModel.CUBE.get(UNGUISHALE).getMapping();
        ResourceLocation unguishaleModel = TexturedModel.CUBE.create(UNGUISHALE, BMG.modelOutput);
        BMG.blockStateOutput.accept(createRotatedVariant(UNGUISHALE, unguishaleModel));

        generateStairs(BMG, UNGUISHALE_STAIRS, unguishaleMapping);
        generateSlab(BMG, UNGUISHALE_SLAB, unguishaleMapping, unguishaleModel);
        generateWall(BMG, UNGUISHALE_WALL, unguishaleMapping);

        // vesperbile
        BMG.createNonTemplateModelBlock(VESPERBILE);

        // door marker
        createJigsawEsque(BMG, DOOR_MARKER);
    }

    @Override
    public void generateItemModels(ItemModelGenerators IMG) {
        generateFlat(IMG,
                MirthdewEncoreItems.SLEEPY_EYE,
                MirthdewEncoreItems.SLUMBERING_EYE,

                MirthdewEncoreItems.BACCHENITE_SHARD,

                MirthdewEncoreItems.CLINKERA_SCRAPS,

                MirthdewEncoreItems.RAW_PSYRITE,
                MirthdewEncoreItems.PSYRITE_INGOT,
                MirthdewEncoreItems.PSYRITE_NUGGET,

                MirthdewEncoreItems.VESPERBILE_BUCKET,

                MirthdewEncoreItems.SPELL_CARD,
                MirthdewEncoreItems.SPELL_DECK,

                MirthdewEncoreItems.MIRTHDEW_VIAL,
                MirthdewEncoreItems.SPECTRAL_CANDY
        );
    }

    private static void generateFlat(ItemModelGenerators IMG, Item... items) {
        for(Item item : items) {
            generateFlat(IMG, item);
        }
    }

    private static void generateFlat(ItemModelGenerators IMG, Item item) {
        IMG.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }

    private static void forMultiple(BlockModelGenerators BMG, BiConsumer<BlockModelGenerators, Block> function, Block... blocks) {
        for(Block block : blocks) {
            function.accept(BMG, block);
        }
    }

    private static void generateStairs(BlockModelGenerators BMG, Block block, TextureMapping textureMapping) {
        ResourceLocation resourceLocation = ModelTemplates.STAIRS_INNER.create(block, textureMapping, BMG.modelOutput);
        ResourceLocation resourceLocation2 = ModelTemplates.STAIRS_STRAIGHT.create(block, textureMapping, BMG.modelOutput);
        ResourceLocation resourceLocation3 = ModelTemplates.STAIRS_OUTER.create(block, textureMapping, BMG.modelOutput);
        BMG.blockStateOutput.accept(BlockModelGenerators.createStairs(block, resourceLocation, resourceLocation2, resourceLocation3));
        BMG.delegateItemModel(block, resourceLocation2);
    }

    public static void generateSlab(BlockModelGenerators BMG, Block block, TextureMapping textureMapping, ResourceLocation doubleModelLocation) {
        ResourceLocation resourceLocation = ModelTemplates.SLAB_BOTTOM.create(block, textureMapping, BMG.modelOutput);
        ResourceLocation resourceLocation2 = ModelTemplates.SLAB_TOP.create(block, textureMapping, BMG.modelOutput);
        BMG.blockStateOutput.accept(BlockModelGenerators.createSlab(block, resourceLocation, resourceLocation2, doubleModelLocation));
        BMG.delegateItemModel(block, resourceLocation);
    }

    public static void generateWall(BlockModelGenerators BMG, Block block, TextureMapping textureMapping) {
        ResourceLocation resourceLocation = ModelTemplates.WALL_POST.create(block, textureMapping, BMG.modelOutput);
        ResourceLocation resourceLocation2 = ModelTemplates.WALL_LOW_SIDE.create(block, textureMapping, BMG.modelOutput);
        ResourceLocation resourceLocation3 = ModelTemplates.WALL_TALL_SIDE.create(block, textureMapping, BMG.modelOutput);
        BMG.blockStateOutput.accept(BlockModelGenerators.createWall(block, resourceLocation, resourceLocation2, resourceLocation3));
        ResourceLocation resourceLocation4 = ModelTemplates.WALL_INVENTORY.create(block, textureMapping, BMG.modelOutput);
        BMG.delegateItemModel(block, resourceLocation4);
    }

    private static void generateSlumberveil(BlockModelGenerators BMG, Block block) {
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

    private static void generateGrassLike(BlockModelGenerators BMG, Block block, Block baseBlock) {
        TextureMapping textureMapping = new TextureMapping()
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(baseBlock))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"));
        BMG.blockStateOutput.accept(createRotatedVariant(block, ModelTemplates.CUBE_BOTTOM_TOP.create(block, textureMapping, BMG.modelOutput)));
    }

    private static void generatePillarGrassLike(BlockModelGenerators BMG, Block block, Block baseBlock) {
        TextureMapping textureMapping = new TextureMapping()
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(baseBlock, "_top"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"));
        BMG.blockStateOutput.accept(createRotatedVariant(block, ModelTemplates.CUBE_BOTTOM_TOP.create(block, textureMapping, BMG.modelOutput)));
    }

    private static void generateLattice(BlockModelGenerators BMG, Block block) {
        TextureMapping textureMapping = new TextureMapping()
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block))
                .put(TextureSlot.PANE, TextureMapping.getBlockTexture(block))
                .put(TextureSlot.EDGE, TextureMapping.getBlockTexture(block, "_edge"));

        ResourceLocation resourceLocation = MirthdewEncoreModelTemplates.LATTICE_POST_ENDS.create(block, textureMapping, BMG.modelOutput);
        ResourceLocation resourceLocation2 = MirthdewEncoreModelTemplates.LATTICE_POST.create(block, textureMapping, BMG.modelOutput);
        ResourceLocation resourceLocation3 = MirthdewEncoreModelTemplates.LATTICE_CAP.create(block, textureMapping, BMG.modelOutput);
        ResourceLocation resourceLocation4 = MirthdewEncoreModelTemplates.LATTICE_CAP_ALT.create(block, textureMapping, BMG.modelOutput);
        ResourceLocation resourceLocation5 = MirthdewEncoreModelTemplates.LATTICE_SIDE.create(block, textureMapping, BMG.modelOutput);
        ResourceLocation resourceLocation6 = MirthdewEncoreModelTemplates.LATTICE_SIDE_ALT.create(block, textureMapping, BMG.modelOutput);
        BMG.blockStateOutput
                .accept(
                        MultiPartGenerator.multiPart(block)
                                .with(Variant.variant().with(VariantProperties.MODEL, resourceLocation))
                                .with(
                                        Condition.condition()
                                                .term(BlockStateProperties.NORTH, false)
                                                .term(BlockStateProperties.EAST, false)
                                                .term(BlockStateProperties.SOUTH, false)
                                                .term(BlockStateProperties.WEST, false),
                                        Variant.variant().with(VariantProperties.MODEL, resourceLocation2)
                                )
                                .with(
                                        Condition.condition()
                                                .term(BlockStateProperties.NORTH, true)
                                                .term(BlockStateProperties.EAST, false)
                                                .term(BlockStateProperties.SOUTH, false)
                                                .term(BlockStateProperties.WEST, false),
                                        Variant.variant().with(VariantProperties.MODEL, resourceLocation3)
                                )
                                .with(
                                        Condition.condition()
                                                .term(BlockStateProperties.NORTH, false)
                                                .term(BlockStateProperties.EAST, true)
                                                .term(BlockStateProperties.SOUTH, false)
                                                .term(BlockStateProperties.WEST, false),
                                        Variant.variant().with(VariantProperties.MODEL, resourceLocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                )
                                .with(
                                        Condition.condition()
                                                .term(BlockStateProperties.NORTH, false)
                                                .term(BlockStateProperties.EAST, false)
                                                .term(BlockStateProperties.SOUTH, true)
                                                .term(BlockStateProperties.WEST, false),
                                        Variant.variant().with(VariantProperties.MODEL, resourceLocation4)
                                )
                                .with(
                                        Condition.condition()
                                                .term(BlockStateProperties.NORTH, false)
                                                .term(BlockStateProperties.EAST, false)
                                                .term(BlockStateProperties.SOUTH, false)
                                                .term(BlockStateProperties.WEST, true),
                                        Variant.variant().with(VariantProperties.MODEL, resourceLocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                )
                                .with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, resourceLocation5))
                                .with(
                                        Condition.condition().term(BlockStateProperties.EAST, true),
                                        Variant.variant().with(VariantProperties.MODEL, resourceLocation5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                )
                                .with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, resourceLocation6))
                                .with(
                                        Condition.condition().term(BlockStateProperties.WEST, true),
                                        Variant.variant().with(VariantProperties.MODEL, resourceLocation6).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                )
                );
        BMG.createSimpleFlatItemModel(block);
    }

    private void createJigsawEsque(BlockModelGenerators BMG, Block block) {
        ResourceLocation textureTop = TextureMapping.getBlockTexture(block, "_top");
        ResourceLocation textureBottom = TextureMapping.getBlockTexture(block, "_bottom");
        ResourceLocation textureSide = TextureMapping.getBlockTexture(block, "_side");
        ResourceLocation textureLock = TextureMapping.getBlockTexture(block, "_lock");

        TextureMapping textureMapping = new TextureMapping()
                .put(TextureSlot.DOWN, textureSide)
                .put(TextureSlot.WEST, textureSide)
                .put(TextureSlot.EAST, textureSide)
                .put(TextureSlot.PARTICLE, textureTop)
                .put(TextureSlot.NORTH, textureTop)
                .put(TextureSlot.SOUTH, textureBottom)
                .put(TextureSlot.UP, textureLock);

        ResourceLocation resourceLocation5 = ModelTemplates.CUBE_DIRECTIONAL.create(block, textureMapping, BMG.modelOutput);

        BMG.blockStateOutput
                .accept(
                        MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, resourceLocation5))
                                .with(PropertyDispatch.property(BlockStateProperties.ORIENTATION).generate(frontAndTop -> BMG.applyRotation(frontAndTop, Variant.variant())))
                );
    }
}
