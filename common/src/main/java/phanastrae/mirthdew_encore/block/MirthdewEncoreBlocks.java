package phanastrae.mirthdew_encore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.fluid.MirthdewEncoreFluids;

import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

public class MirthdewEncoreBlocks {

    public static final Block DREAMTWIRL_BARRIER = new DreamtwirlBarrierBlock(createSettings()
            .strength(-1.0F, 3600000.8F)
            .mapColor(MapColor.NONE)
            .noLootTable()
            .isValidSpawn(MirthdewEncoreBlocks::never)
            .noTerrainParticles()
            .pushReaction(PushReaction.BLOCK)
    );

    public static final Block VERIC_DREAMSNARE = new VericDreamsnareBlock(createSettings()
            .strength(2.0F, 2.0F)
            .mapColor(MapColor.COLOR_CYAN)
            .sound(SoundType.SCULK)
            .lightLevel(constant(4))
    );

    public static final Block DREAMSEED = new DreamseedBlock(createSettings()
            .strength(2.0F, 8.0F)
            .mapColor(MapColor.COLOR_MAGENTA)
            .sound(SoundType.WART_BLOCK)
            .lightLevel(constant(5))
    );

    public static final Block SLUMBERSOCKET = new SlumbersocketBlock((createSettings())
            .strength(30.0F, 1200.0F)
            .mapColor(MapColor.DEEPSLATE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
            .lightLevel(state -> state.getValue(SlumbersocketBlock.DREAMING) ? 9 : 13)
            .noOcclusion()
            .isValidSpawn(MirthdewEncoreBlocks::never)
            .isRedstoneConductor(MirthdewEncoreBlocks::never)
            .isSuffocating(MirthdewEncoreBlocks::never)
            .isViewBlocking(MirthdewEncoreBlocks::never)
    );

    public static final Block SLUMBERVEIL = new SlumberveilBlock(createSettings()
            .noCollission()
            .sound(SoundType.WOOL)
            .lightLevel(constant(13))
            .pushReaction(PushReaction.DESTROY)
            .replaceable()
    );

    public static Block UNGUISHALE = new Block(createSettings()
            .mapColor(MapColor.COLOR_LIGHT_GRAY)
            .instrument(NoteBlockInstrument.SNARE)
            .strength(0.6F, 0.4F)
            .sound(SoundType.TUFF)
            .requiresCorrectToolForDrops()
    );

    public static Block UNGUISHALE_STAIRS = stairsOf(UNGUISHALE);
    public static Block UNGUISHALE_SLAB = slabOf(UNGUISHALE);
    public static Block UNGUISHALE_WALL = wallOf(UNGUISHALE);

    public static Block UNGUISHALE_BRICKS = new Block(createSettings()
            .mapColor(MapColor.COLOR_LIGHT_GRAY)
            .instrument(NoteBlockInstrument.SNARE)
            .strength(0.8F, 0.6F)
            .sound(SoundType.TUFF_BRICKS)
            .requiresCorrectToolForDrops()
    );

    public static Block UNGUISHALE_BRICK_STAIRS = stairsOf(UNGUISHALE_BRICKS);
    public static Block UNGUISHALE_BRICK_SLAB = slabOf(UNGUISHALE_BRICKS);
    public static Block UNGUISHALE_BRICK_WALL = wallOf(UNGUISHALE_BRICKS);

    public static Block UNGUISHALE_TILES = new Block(createSettings()
            .mapColor(MapColor.COLOR_LIGHT_GRAY)
            .instrument(NoteBlockInstrument.SNARE)
            .strength(0.8F, 0.6F)
            .sound(SoundType.TUFF_BRICKS)
            .requiresCorrectToolForDrops()
    );

    public static Block UNGUISHALE_TILE_STAIRS = stairsOf(UNGUISHALE_TILES);
    public static Block UNGUISHALE_TILE_SLAB = slabOf(UNGUISHALE_TILES);
    public static Block UNGUISHALE_TILE_WALL = wallOf(UNGUISHALE_TILES);

    public static final Block CLINKERA_PLANKS = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
            .strength(2.0F, 3.0F)
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.WOOD)
    );

    public static BlockSetType CLINKERA_BLOCKSET = new BlockSetType("mirthdew_encore:clinkera");
    public static WoodType CLINKERA_WOODSET = new WoodType("mirthdew_encore:clinkera", CLINKERA_BLOCKSET);

    public static Block CLINKERA_STAIRS = stairsOf(CLINKERA_PLANKS);
    public static Block CLINKERA_SLAB = slabOf(CLINKERA_PLANKS);
    public static Block CLINKERA_FENCE = fenceOf(CLINKERA_PLANKS);
    public static Block CLINKERA_FENCE_GATE = fenceGateOf(CLINKERA_WOODSET, CLINKERA_PLANKS);
    public static Block CLINKERA_LATTICE = new LatticeBlock(copyShallow(CLINKERA_PLANKS).noOcclusion());
    public static Block CLINKERA_DOOR = doorOf(CLINKERA_BLOCKSET, CLINKERA_PLANKS, 2.5F);
    public static Block CLINKERA_TRAPDOOR = trapdoorOf(CLINKERA_BLOCKSET, CLINKERA_PLANKS, 2.5F);
    public static Block CLINKERA_PRESSURE_PLATE = pressurePlateOf(CLINKERA_BLOCKSET, CLINKERA_PLANKS);
    public static Block CLINKERA_BUTTON = buttonOf(CLINKERA_BLOCKSET, 30, CLINKERA_PLANKS);

    public static Block ONYXSCALE = new Block(createSettings()
            .mapColor(MapColor.COLOR_BLACK)
            .instrument(NoteBlockInstrument.SNARE)
            .strength(0.9F, 0.6F)
            .sound(SoundType.TUFF)
    );

    public static Block RHEUMDAUBED_ONYXSCALE = new RheumdaubedOnyxscaleBlock(createSettings()
            .mapColor(MapColor.TERRACOTTA_YELLOW)
            .instrument(NoteBlockInstrument.SNARE)
            .strength(1.3F, 0.9F)
            .sound(SoundType.TUFF)
            .randomTicks()
    );
    public static final Block RHEUMBRISTLES = new RheumbristlesBlock(
            createSettings()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .replaceable()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.ROOTS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final Block POTTED_RHEUMBRISTLES = flowerPot(RHEUMBRISTLES);
    public static final Block SOULSPOT_MUSHRHEUM = new SoulspotMushrheumBlock(
            createSettings()
                    .mapColor(MapColor.COLOR_ORANGE)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .hasPostProcess(MirthdewEncoreBlocks::always)
                    .pushReaction(PushReaction.DESTROY)
    );
    public static final Block POTTED_SOULSPOT_MUSHRHEUM = flowerPot(SOULSPOT_MUSHRHEUM);

    public static final Block DECIDRHEUM_LOG = new CustomLogBlock(createSettings()
            .mapColor(MapColor.COLOR_BLUE)
            .strength(1.5F, 2.0F)
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.WOOD)
    );

    public static final Block DECIDRHEUM_WOOD = new CustomLogBlock(createSettings()
            .mapColor(MapColor.COLOR_BLUE)
            .strength(1.5F, 2.0F)
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.WOOD)
    );

    public static final Block STRIPPED_DECIDRHEUM_LOG = new CustomLogBlock(createSettings()
            .mapColor(MapColor.COLOR_BLUE)
            .strength(1.5F, 2.0F)
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.WOOD)
    );

    public static final Block STRIPPED_DECIDRHEUM_WOOD = new CustomLogBlock(createSettings()
            .mapColor(MapColor.COLOR_BLUE)
            .strength(1.5F, 2.0F)
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.WOOD)
    );

    public static final Block DECIDRHEUM_PLANKS = new Block(createSettings()
            .mapColor(MapColor.COLOR_BLUE)
            .strength(2.0F, 3.0F)
            .instrument(NoteBlockInstrument.BASS)
            .sound(SoundType.WOOD)
    );

    public static BlockSetType DECIDRHEUM_BLOCKSET = new BlockSetType("mirthdew_encore:decidrheum");
    public static WoodType DECIDRHEUM_WOODSET = new WoodType("mirthdew_encore:decidrheum", DECIDRHEUM_BLOCKSET);

    public static Block DECIDRHEUM_STAIRS = stairsOf(DECIDRHEUM_PLANKS);
    public static Block DECIDRHEUM_SLAB = slabOf(DECIDRHEUM_PLANKS);
    public static Block DECIDRHEUM_FENCE = fenceOf(DECIDRHEUM_PLANKS);
    public static Block DECIDRHEUM_FENCE_GATE = fenceGateOf(DECIDRHEUM_WOODSET, DECIDRHEUM_PLANKS);
    public static Block DECIDRHEUM_LATTICE = new LatticeBlock(copyShallow(DECIDRHEUM_PLANKS).noOcclusion());
    public static Block DECIDRHEUM_DOOR = doorOf(DECIDRHEUM_BLOCKSET, DECIDRHEUM_PLANKS, 2.5F);
    public static Block DECIDRHEUM_TRAPDOOR = trapdoorOf(DECIDRHEUM_BLOCKSET, DECIDRHEUM_PLANKS, 2.5F);
    public static Block DECIDRHEUM_PRESSURE_PLATE = pressurePlateOf(DECIDRHEUM_BLOCKSET, DECIDRHEUM_PLANKS);
    public static Block DECIDRHEUM_BUTTON = buttonOf(DECIDRHEUM_BLOCKSET, 30, DECIDRHEUM_PLANKS);

    public static Block DECIDRHEUM_LEAVES = new DecidrheumLeavesBlock(createSettings()
            .mapColor(MapColor.TERRACOTTA_ORANGE)
            .strength(0.2F, 0.2F)
            .randomTicks()
            .sound(SoundType.MUD)
            .noOcclusion()
            .isValidSpawn(MirthdewEncoreBlocks::never)
            .isSuffocating(MirthdewEncoreBlocks::never)
            .isViewBlocking(MirthdewEncoreBlocks::never)
            .pushReaction(PushReaction.DESTROY)
            .isRedstoneConductor(MirthdewEncoreBlocks::never)
    );

    public static Block DECIDRHEUM_SAPLING = new DecidrheumSaplingBlock(DecidrheumSaplingBlock.DECIDRHEUM, createSettings()
            .mapColor(MapColor.COLOR_YELLOW)
            .noCollission()
            .randomTicks()
            .instabreak()
            .sound(SoundType.GRASS)
            .pushReaction(PushReaction.DESTROY)
    );

    public static Block POTTED_DECIDRHEUM_SAPLING = flowerPot(DECIDRHEUM_SAPLING);

    public static Block GACHERIMM = new RotatedPillarBlock(createSettings()
            .mapColor(MapColor.TERRACOTTA_PURPLE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(0.9F, 0.9F)
            .sound(SoundType.DEEPSLATE)
            .requiresCorrectToolForDrops()
    );

    public static Block ORANGE_NOVACLAG = novaclag(MapColor.COLOR_ORANGE);
    public static Block LIME_NOVACLAG = novaclag(MapColor.COLOR_LIGHT_GREEN);
    public static Block CYAN_NOVACLAG = novaclag(MapColor.COLOR_CYAN);
    public static Block MAGNETA_NOVACLAG = novaclag(MapColor.COLOR_MAGENTA);

    public static Block ORANGE_FOGHAIR = foghair(MapColor.COLOR_ORANGE);
    public static Block LIME_FOGHAIR = foghair(MapColor.COLOR_LIGHT_GREEN);
    public static Block CYAN_FOGHAIR = foghair(MapColor.COLOR_CYAN);
    public static Block MAGNETA_FOGHAIR = foghair(MapColor.COLOR_MAGENTA);

    public static Block POTTED_ORANGE_FOGHAIR = flowerPot(ORANGE_FOGHAIR, 9);
    public static Block POTTED_LIME_FOGHAIR = flowerPot(LIME_FOGHAIR, 9);
    public static Block POTTED_CYAN_FOGHAIR = flowerPot(CYAN_FOGHAIR, 9);
    public static Block POTTED_MAGNETA_FOGHAIR = flowerPot(MAGNETA_FOGHAIR, 9);

    public static Block ROUGH_GACHERIMM = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_PURPLE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(0.9F, 0.9F)
            .sound(SoundType.DEEPSLATE)
            .requiresCorrectToolForDrops()
    );

    public static Block ROUGH_GACHERIMM_STAIRS = stairsOf(ROUGH_GACHERIMM);
    public static Block ROUGH_GACHERIMM_SLAB = slabOf(ROUGH_GACHERIMM);
    public static Block ROUGH_GACHERIMM_WALL = wallOf(ROUGH_GACHERIMM);

    public static Block GACHERIMM_BRICKS = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_PURPLE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(0.9F, 0.9F)
            .sound(SoundType.DEEPSLATE)
            .requiresCorrectToolForDrops()
    );

    public static Block GACHERIMM_BRICK_STAIRS = stairsOf(GACHERIMM_BRICKS);
    public static Block GACHERIMM_BRICK_SLAB = slabOf(GACHERIMM_BRICKS);
    public static Block GACHERIMM_BRICK_WALL = wallOf(GACHERIMM_BRICKS);

    public static Block GACHERIMM_TILES = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_PURPLE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(0.9F, 0.9F)
            .sound(SoundType.DEEPSLATE)
            .requiresCorrectToolForDrops()
    );

    public static Block GACHERIMM_TILE_STAIRS = stairsOf(GACHERIMM_TILES);
    public static Block GACHERIMM_TILE_SLAB = slabOf(GACHERIMM_TILES);
    public static Block GACHERIMM_TILE_WALL = wallOf(GACHERIMM_TILES);

    public static Block POLISHED_GACHERIMM = new Block(createSettings()
            .mapColor(MapColor.COLOR_PURPLE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(0.9F, 0.9F)
            .sound(SoundType.DEEPSLATE)
    );

    public static Block POLISHED_GACHERIMM_STAIRS = stairsOf(POLISHED_GACHERIMM);
    public static Block POLISHED_GACHERIMM_SLAB = slabOf(POLISHED_GACHERIMM);
    public static Block POLISHED_GACHERIMM_WALL = wallOf(POLISHED_GACHERIMM);

    public static Block CUT_POLISHED_GACHERIMM = new Block(createSettings()
            .mapColor(MapColor.COLOR_PURPLE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(0.9F, 0.9F)
            .sound(SoundType.DEEPSLATE)
    );

    public static Block CUT_POLISHED_GACHERIMM_STAIRS = stairsOf(CUT_POLISHED_GACHERIMM);
    public static Block CUT_POLISHED_GACHERIMM_SLAB = slabOf(CUT_POLISHED_GACHERIMM);

    public static Block REVERIME = new Block(createSettings()
            .mapColor(MapColor.ICE)
            .strength(2.2F, 1.3F)
            .friction(0.85F)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
    );

    public static Block REVERIME_STAIRS = stairsOf(REVERIME);
    public static Block REVERIME_SLAB = slabOf(REVERIME);
    public static Block REVERIME_WALL = wallOf(REVERIME);

    public static Block FROSTED_REVERIME = new Block(createSettings()
            .mapColor(MapColor.ICE)
            .strength(2.5F, 1.8F)
            .friction(0.93F)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
    );

    public static Block REVERIME_BRICKS = new Block(createSettings()
            .mapColor(MapColor.ICE)
            .strength(2.2F, 1.3F)
            .friction(0.85F)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
    );

    public static Block REVERIME_BRICK_STAIRS = stairsOf(REVERIME_BRICKS);
    public static Block REVERIME_BRICK_SLAB = slabOf(REVERIME_BRICKS);
    public static Block REVERIME_BRICK_WALL = wallOf(REVERIME_BRICKS);

    public static Block REVERIME_TILES = new Block(createSettings()
            .mapColor(MapColor.ICE)
            .strength(2.2F, 1.3F)
            .friction(0.85F)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
    );

    public static Block REVERIME_TILE_STAIRS = stairsOf(REVERIME_TILES);
    public static Block REVERIME_TILE_SLAB = slabOf(REVERIME_TILES);
    public static Block REVERIME_TILE_WALL = wallOf(REVERIME_TILES);

    public static Block POLISHED_REVERIME = new Block(createSettings()
            .mapColor(MapColor.ICE)
            .strength(2.2F, 1.3F)
            .friction(0.9F)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
    );

    public static Block POLISHED_REVERIME_STAIRS = stairsOf(POLISHED_REVERIME);
    public static Block POLISHED_REVERIME_SLAB = slabOf(POLISHED_REVERIME);
    public static Block POLISHED_REVERIME_WALL = wallOf(POLISHED_REVERIME);

    public static Block POLISHED_REVERIME_BRICKS = new Block(createSettings()
            .mapColor(MapColor.ICE)
            .strength(2.2F, 1.3F)
            .friction(0.9F)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
    );

    public static Block POLISHED_REVERIME_BRICK_STAIRS = stairsOf(POLISHED_REVERIME_BRICKS);
    public static Block POLISHED_REVERIME_BRICK_SLAB = slabOf(POLISHED_REVERIME_BRICKS);
    public static Block POLISHED_REVERIME_BRICK_WALL = wallOf(POLISHED_REVERIME_BRICKS);

    public static Block CUT_POLISHED_REVERIME = new Block(createSettings()
            .mapColor(MapColor.ICE)
            .strength(2.2F, 1.3F)
            .friction(0.9F)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
    );

    public static Block CUT_POLISHED_REVERIME_STAIRS = stairsOf(CUT_POLISHED_REVERIME);
    public static Block CUT_POLISHED_REVERIME_SLAB = slabOf(CUT_POLISHED_REVERIME);

    public static Block ROSENGLACE = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_PINK)
            .strength(3.5F, 2.2F)
            .friction(0.98F)
            .sound(SoundType.GLASS)
            .requiresCorrectToolForDrops()
    );

    public static Block SCARABRIM = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_BLACK)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(1.8F, 3.0F)
            .sound(SoundType.DRIPSTONE_BLOCK)
            .requiresCorrectToolForDrops()
    );

    public static Block SCARABRIM_STAIRS = stairsOf(SCARABRIM);
    public static Block SCARABRIM_SLAB = slabOf(SCARABRIM);
    public static Block SCARABRIM_WALL = wallOf(SCARABRIM);

    public static Block POLISHED_SCARABRIM = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_BLACK)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(1.8F, 3.0F)
            .sound(SoundType.DRIPSTONE_BLOCK)
            .requiresCorrectToolForDrops()
    );

    public static Block POLISHED_SCARABRIM_STAIRS = stairsOf(POLISHED_SCARABRIM);
    public static Block POLISHED_SCARABRIM_SLAB = slabOf(POLISHED_SCARABRIM);
    public static Block POLISHED_SCARABRIM_WALL = wallOf(POLISHED_SCARABRIM);

    public static Block SCARABRIM_BRICKS = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_BLACK)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(1.8F, 3.0F)
            .sound(SoundType.DRIPSTONE_BLOCK)
            .requiresCorrectToolForDrops()
    );

    public static Block SCARABRIM_BRICK_STAIRS = stairsOf(SCARABRIM_BRICKS);
    public static Block SCARABRIM_BRICK_SLAB = slabOf(SCARABRIM_BRICKS);
    public static Block SCARABRIM_BRICK_WALL = wallOf(SCARABRIM_BRICKS);

    public static Block SUNFLECKED_SCARABRIM = new SunfleckedScarabrimBlock(createSettings()
            .mapColor(MapColor.COLOR_YELLOW)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(1.2F, 1.7F)
            .sound(SoundType.DRIPSTONE_BLOCK)
            .lightLevel(constant(11))
            .randomTicks()
            .requiresCorrectToolForDrops()
    );

    public static Block CHALKTISSUE = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .strength(2.3F, 2.5F)
            .sound(SoundType.MUD)
    );

    public static Block FLAKING_CHALKTISSUE = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .strength(2.3F, 2.5F)
            .sound(SoundType.MUD)
    );

    public static Block SUNSLAKED_CHALKTISSUE = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .strength(2.3F, 2.5F)
            .sound(SoundType.MUD)
    );

    public static Block GACHERIMM_PSYRITE_ORE = new DropExperienceBlock(ConstantInt.of(0), createSettings()
            .mapColor(MapColor.TERRACOTTA_PURPLE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(1.5F, 2.0F)
            .sound(SoundType.DEEPSLATE)
            .requiresCorrectToolForDrops()
    );

    public static Block SCARABRIM_PSYRITE_ORE = new DropExperienceBlock(ConstantInt.of(0), createSettings()
            .mapColor(MapColor.TERRACOTTA_BLACK)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(1.8F, 3.0F)
            .sound(SoundType.DRIPSTONE_BLOCK)
            .requiresCorrectToolForDrops()
    );

    public static Block SUNSLAKED_PSYRITE_ORE = new DropExperienceBlock(ConstantInt.of(0), createSettings()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .strength(2.3F, 2.5F)
            .sound(SoundType.MUD)
    );

    public static Block RAW_PSYRITE_BLOCK = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_PINK)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(5.0F, 6.0F)
            .requiresCorrectToolForDrops()
    );

    public static BlockSetType PSYRITE_BLOCKSET = new BlockSetType("mirthdew_encore:psyrite");
    public static Block PSYRITE_BLOCK = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_PINK)
            .strength(3.0F, 6.0F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.COPPER)
    );

    public static Block CUT_PSYRITE = new Block(fullCopy(PSYRITE_BLOCK));
    public static Block CUT_PSYRITE_STAIRS = stairsOf(PSYRITE_BLOCK);
    public static Block CUT_PSYRITE_SLAB = slabOf(PSYRITE_BLOCK);

    public static Block CHISELED_PSYRITE = new Block(fullCopy(PSYRITE_BLOCK));

    public static Block PSYRITE_PILLAR = new RotatedPillarBlock(fullCopy(PSYRITE_BLOCK));

    public static final Block PSYRITE_GRATE = new CustomWaterloggedTransparentBlock(createSettings()
            .mapColor(PSYRITE_BLOCK.defaultMapColor())
            .strength(3.0F, 6.0F)
            .sound(SoundType.COPPER_GRATE)
            .isValidSpawn(MirthdewEncoreBlocks::never)
            .isRedstoneConductor(MirthdewEncoreBlocks::never)
            .isSuffocating(MirthdewEncoreBlocks::never)
            .isViewBlocking(MirthdewEncoreBlocks::never)
            .requiresCorrectToolForDrops()
            .noOcclusion()
    );

    public static Block PSYRITE_GRATE_SLAB = new TransparentSlabBlock(copyShallow(PSYRITE_GRATE));

    public static final Block PSYRITE_DOOR = new CustomDoorBlock(PSYRITE_BLOCKSET, createSettings()
            .mapColor(PSYRITE_BLOCK.defaultMapColor())
            .strength(3.0F, 6.0F)
            .pushReaction(PushReaction.DESTROY)
            .requiresCorrectToolForDrops()
            .noOcclusion()
    );

    public static final Block PSYRITE_TRAPDOOR = new CustomTrapDoorBlock(PSYRITE_BLOCKSET, createSettings()
            .mapColor(PSYRITE_BLOCK.defaultMapColor())
            .strength(3.0F, 6.0F)
            .isValidSpawn(MirthdewEncoreBlocks::never)
            .requiresCorrectToolForDrops()
            .noOcclusion()
    );

    public static final Block PSYRITE_BARS = new LatticeBlock(
            createSettings()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
    );

    public static final Block PSYRITE_LATTICE = new LatticeBlock(
            createSettings()
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER_GRATE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
    );

    public static Block VESPERBILE = new VesperbileLiquidBlock(
            MirthdewEncoreFluids.VESPERBILE,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_MAGENTA)
                    .replaceable()
                    .noCollission()
                    .randomTicks()
                    .strength(100.0F)
                    .lightLevel(blockState -> blockState.getValue(VesperbileLiquidBlock.EMITS_LIGHT) ? 9 : 0)
                    .pushReaction(PushReaction.DESTROY)
                    .noLootTable()
                    .liquid()
                    .sound(SoundType.EMPTY)
    );

    public static void init(BiConsumer<ResourceLocation, Block> r) {
        r.accept(id("dreamtwirl_barrier"), DREAMTWIRL_BARRIER);

        r.accept(id("veric_dreamsnare"), VERIC_DREAMSNARE);
        r.accept(id("dreamseed"), DREAMSEED);
        r.accept(id("slumbersocket"), SLUMBERSOCKET);
        r.accept(id("slumberveil"), SLUMBERVEIL);

        r.accept(id("unguishale"), UNGUISHALE);
        r.accept(id("unguishale_stairs"), UNGUISHALE_STAIRS);
        r.accept(id("unguishale_slab"), UNGUISHALE_SLAB);
        r.accept(id("unguishale_wall"), UNGUISHALE_WALL);

        r.accept(id("unguishale_bricks"), UNGUISHALE_BRICKS);
        r.accept(id("unguishale_brick_stairs"), UNGUISHALE_BRICK_STAIRS);
        r.accept(id("unguishale_brick_slab"), UNGUISHALE_BRICK_SLAB);
        r.accept(id("unguishale_brick_wall"), UNGUISHALE_BRICK_WALL);

        r.accept(id("unguishale_tiles"), UNGUISHALE_TILES);
        r.accept(id("unguishale_tile_stairs"), UNGUISHALE_TILE_STAIRS);
        r.accept(id("unguishale_tile_slab"), UNGUISHALE_TILE_SLAB);
        r.accept(id("unguishale_tile_wall"), UNGUISHALE_TILE_WALL);

        r.accept(id("clinkera_planks"), CLINKERA_PLANKS);
        r.accept(id("clinkera_stairs"), CLINKERA_STAIRS);
        r.accept(id("clinkera_slab"), CLINKERA_SLAB);
        r.accept(id("clinkera_fence"), CLINKERA_FENCE);
        r.accept(id("clinkera_fence_gate"), CLINKERA_FENCE_GATE);
        r.accept(id("clinkera_lattice"), CLINKERA_LATTICE);
        r.accept(id("clinkera_door"), CLINKERA_DOOR);
        r.accept(id("clinkera_trapdoor"), CLINKERA_TRAPDOOR);
        r.accept(id("clinkera_pressure_plate"), CLINKERA_PRESSURE_PLATE);
        r.accept(id("clinkera_button"), CLINKERA_BUTTON);

        r.accept(id("onyxscale"), ONYXSCALE);
        r.accept(id("rheumdaubed_onyxscale"), RHEUMDAUBED_ONYXSCALE);

        r.accept(id("rheumbristles"), RHEUMBRISTLES);
        r.accept(id("potted_rheumbristles"), POTTED_RHEUMBRISTLES);
        r.accept(id("soulspot_mushrheum"), SOULSPOT_MUSHRHEUM);
        r.accept(id("potted_soulspot_mushrheum"), POTTED_SOULSPOT_MUSHRHEUM);

        r.accept(id("decidrheum_log"), DECIDRHEUM_LOG);
        r.accept(id("decidrheum_wood"), DECIDRHEUM_WOOD);
        r.accept(id("stripped_decidrheum_log"), STRIPPED_DECIDRHEUM_LOG);
        r.accept(id("stripped_decidrheum_wood"), STRIPPED_DECIDRHEUM_WOOD);

        r.accept(id("decidrheum_planks"), DECIDRHEUM_PLANKS);
        r.accept(id("decidrheum_stairs"), DECIDRHEUM_STAIRS);
        r.accept(id("decidrheum_slab"), DECIDRHEUM_SLAB);
        r.accept(id("decidrheum_fence"), DECIDRHEUM_FENCE);
        r.accept(id("decidrheum_fence_gate"), DECIDRHEUM_FENCE_GATE);
        r.accept(id("decidrheum_lattice"), DECIDRHEUM_LATTICE);
        r.accept(id("decidrheum_door"), DECIDRHEUM_DOOR);
        r.accept(id("decidrheum_trapdoor"), DECIDRHEUM_TRAPDOOR);
        r.accept(id("decidrheum_pressure_plate"), DECIDRHEUM_PRESSURE_PLATE);
        r.accept(id("decidrheum_button"), DECIDRHEUM_BUTTON);

        r.accept(id("decidrheum_leaves"), DECIDRHEUM_LEAVES);
        r.accept(id("decidrheum_sapling"), DECIDRHEUM_SAPLING);
        r.accept(id("potted_decidrheum_sapling"), POTTED_DECIDRHEUM_SAPLING);

        r.accept(id("gacherimm"), GACHERIMM);

        r.accept(id("orange_novaclag"), ORANGE_NOVACLAG);
        r.accept(id("lime_novaclag"), LIME_NOVACLAG);
        r.accept(id("cyan_novaclag"), CYAN_NOVACLAG);
        r.accept(id("magenta_novaclag"), MAGNETA_NOVACLAG);

        r.accept(id("orange_foghair"), ORANGE_FOGHAIR);
        r.accept(id("lime_foghair"), LIME_FOGHAIR);
        r.accept(id("cyan_foghair"), CYAN_FOGHAIR);
        r.accept(id("magenta_foghair"), MAGNETA_FOGHAIR);
        r.accept(id("potted_orange_foghair"), POTTED_ORANGE_FOGHAIR);
        r.accept(id("potted_lime_foghair"), POTTED_LIME_FOGHAIR);
        r.accept(id("potted_cyan_foghair"), POTTED_CYAN_FOGHAIR);
        r.accept(id("potted_magenta_foghair"), POTTED_MAGNETA_FOGHAIR);

        r.accept(id("rough_gacherimm"), ROUGH_GACHERIMM);
        r.accept(id("rough_gacherimm_stairs"), ROUGH_GACHERIMM_STAIRS);
        r.accept(id("rough_gacherimm_slab"), ROUGH_GACHERIMM_SLAB);
        r.accept(id("rough_gacherimm_wall"), ROUGH_GACHERIMM_WALL);

        r.accept(id("gacherimm_bricks"), GACHERIMM_BRICKS);
        r.accept(id("gacherimm_brick_stairs"), GACHERIMM_BRICK_STAIRS);
        r.accept(id("gacherimm_brick_slab"), GACHERIMM_BRICK_SLAB);
        r.accept(id("gacherimm_brick_wall"), GACHERIMM_BRICK_WALL);

        r.accept(id("gacherimm_tiles"), GACHERIMM_TILES);
        r.accept(id("gacherimm_tile_stairs"), GACHERIMM_TILE_STAIRS);
        r.accept(id("gacherimm_tile_slab"), GACHERIMM_TILE_SLAB);
        r.accept(id("gacherimm_tile_wall"), GACHERIMM_TILE_WALL);

        r.accept(id("polished_gacherimm"), POLISHED_GACHERIMM);
        r.accept(id("polished_gacherimm_stairs"), POLISHED_GACHERIMM_STAIRS);
        r.accept(id("polished_gacherimm_slab"), POLISHED_GACHERIMM_SLAB);
        r.accept(id("polished_gacherimm_wall"), POLISHED_GACHERIMM_WALL);

        r.accept(id("cut_polished_gacherimm"), CUT_POLISHED_GACHERIMM);
        r.accept(id("cut_polished_gacherimm_stairs"), CUT_POLISHED_GACHERIMM_STAIRS);
        r.accept(id("cut_polished_gacherimm_slab"), CUT_POLISHED_GACHERIMM_SLAB);

        r.accept(id("reverime"), REVERIME);
        r.accept(id("reverime_stairs"), REVERIME_STAIRS);
        r.accept(id("reverime_slab"), REVERIME_SLAB);
        r.accept(id("reverime_wall"), REVERIME_WALL);

        r.accept(id("frosted_reverime"), FROSTED_REVERIME);

        r.accept(id("reverime_bricks"), REVERIME_BRICKS);
        r.accept(id("reverime_brick_stairs"), REVERIME_BRICK_STAIRS);
        r.accept(id("reverime_brick_slab"), REVERIME_BRICK_SLAB);
        r.accept(id("reverime_brick_wall"), REVERIME_BRICK_WALL);

        r.accept(id("reverime_tiles"), REVERIME_TILES);
        r.accept(id("reverime_tile_stairs"), REVERIME_TILE_STAIRS);
        r.accept(id("reverime_tile_slab"), REVERIME_TILE_SLAB);
        r.accept(id("reverime_tile_wall"), REVERIME_TILE_WALL);

        r.accept(id("polished_reverime"), POLISHED_REVERIME);
        r.accept(id("polished_reverime_stairs"), POLISHED_REVERIME_STAIRS);
        r.accept(id("polished_reverime_slab"), POLISHED_REVERIME_SLAB);
        r.accept(id("polished_reverime_wall"), POLISHED_REVERIME_WALL);

        r.accept(id("polished_reverime_bricks"), POLISHED_REVERIME_BRICKS);
        r.accept(id("polished_reverime_brick_stairs"), POLISHED_REVERIME_BRICK_STAIRS);
        r.accept(id("polished_reverime_brick_slab"), POLISHED_REVERIME_BRICK_SLAB);
        r.accept(id("polished_reverime_brick_wall"), POLISHED_REVERIME_BRICK_WALL);

        r.accept(id("cut_polished_reverime"), CUT_POLISHED_REVERIME);
        r.accept(id("cut_polished_reverime_stairs"), CUT_POLISHED_REVERIME_STAIRS);
        r.accept(id("cut_polished_reverime_slab"), CUT_POLISHED_REVERIME_SLAB);

        r.accept(id("rosenglace"), ROSENGLACE);

        r.accept(id("scarabrim"), SCARABRIM);
        r.accept(id("scarabrim_stairs"), SCARABRIM_STAIRS);
        r.accept(id("scarabrim_slab"), SCARABRIM_SLAB);
        r.accept(id("scarabrim_wall"), SCARABRIM_WALL);

        r.accept(id("polished_scarabrim"), POLISHED_SCARABRIM);
        r.accept(id("polished_scarabrim_stairs"), POLISHED_SCARABRIM_STAIRS);
        r.accept(id("polished_scarabrim_slab"), POLISHED_SCARABRIM_SLAB);
        r.accept(id("polished_scarabrim_wall"), POLISHED_SCARABRIM_WALL);

        r.accept(id("scarabrim_bricks"), SCARABRIM_BRICKS);
        r.accept(id("scarabrim_brick_stairs"), SCARABRIM_BRICK_STAIRS);
        r.accept(id("scarabrim_brick_slab"), SCARABRIM_BRICK_SLAB);
        r.accept(id("scarabrim_brick_wall"), SCARABRIM_BRICK_WALL);

        r.accept(id("sunflecked_scarabrim"), SUNFLECKED_SCARABRIM);

        r.accept(id("chalktissue"), CHALKTISSUE);
        r.accept(id("flaking_chalktissue"), FLAKING_CHALKTISSUE);
        r.accept(id("sunslaked_chalktissue"), SUNSLAKED_CHALKTISSUE);

        r.accept(id("gacherimm_psyrite_ore"), GACHERIMM_PSYRITE_ORE);
        r.accept(id("scarabrim_psyrite_ore"), SCARABRIM_PSYRITE_ORE);
        r.accept(id("sunslaked_psyrite_ore"), SUNSLAKED_PSYRITE_ORE);

        r.accept(id("raw_psyrite_block"), RAW_PSYRITE_BLOCK);
        r.accept(id("psyrite_block"), PSYRITE_BLOCK);
        r.accept(id("cut_psyrite"), CUT_PSYRITE);
        r.accept(id("cut_psyrite_stairs"), CUT_PSYRITE_STAIRS);
        r.accept(id("cut_psyrite_slab"), CUT_PSYRITE_SLAB);
        r.accept(id("chiseled_psyrite"), CHISELED_PSYRITE);
        r.accept(id("psyrite_pillar"), PSYRITE_PILLAR);
        r.accept(id("psyrite_grate"), PSYRITE_GRATE);
        r.accept(id("psyrite_grate_slab"), PSYRITE_GRATE_SLAB);
        r.accept(id("psyrite_door"), PSYRITE_DOOR);
        r.accept(id("psyrite_trapdoor"), PSYRITE_TRAPDOOR);
        r.accept(id("psyrite_bars"), PSYRITE_BARS);
        r.accept(id("psyrite_lattice"), PSYRITE_LATTICE);

        r.accept(id("vesperbile"), VESPERBILE);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }

    private static BlockBehaviour.Properties createSettings() {
        return BlockBehaviour.Properties.of();
    }

    protected static BlockBehaviour.Properties copyShallow(BlockBehaviour settings) {
        return BlockBehaviour.Properties.ofLegacyCopy(settings);
    }

    protected static BlockBehaviour.Properties fullCopy(BlockBehaviour settings) {
        return BlockBehaviour.Properties.ofFullCopy(settings);
    }

    private static Boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> entity) {
        return false;
    }

    private static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }

    private static ToIntFunction<BlockState> constant(int level) {
        return blockState -> level;
    }

    protected static StairBlock stairsOf(Block block) {
        return new CustomStairBlock(block.defaultBlockState(), copyShallow(block));
    }

    protected static SlabBlock slabOf(BlockBehaviour block) {
        return new SlabBlock(copyShallow(block));
    }

    protected static WallBlock wallOf(BlockBehaviour block) {
        return new WallBlock(copyShallow(block).forceSolidOn());
    }

    protected static FenceBlock fenceOf(BlockBehaviour block) {
        return new FenceBlock(copyShallow(block));
    }

    protected static FenceGateBlock fenceGateOf(WoodType woodType, BlockBehaviour block) {
        return new FenceGateBlock(woodType, copyShallow(block).forceSolidOn());
    }

    protected static DoorBlock doorOf(BlockSetType blockSetType, BlockBehaviour block, float strength) {
        return new CustomDoorBlock(blockSetType, copyShallow(block).strength(strength).noOcclusion().pushReaction(PushReaction.DESTROY));
    }

    protected static TrapDoorBlock trapdoorOf(BlockSetType blockSetType, BlockBehaviour block, float strength) {
        return new CustomTrapDoorBlock(blockSetType, copyShallow(block).strength(strength).noOcclusion().isValidSpawn(MirthdewEncoreBlocks::never));
    }

    protected static PressurePlateBlock pressurePlateOf(BlockSetType blockSetType, BlockBehaviour block) {
        return new CustomPressurePlateBlock(blockSetType, copyShallow(block).strength(0.5F).forceSolidOn().noCollission().pushReaction(PushReaction.DESTROY));
    }

    protected static ButtonBlock buttonOf(BlockSetType blockSetType, int ticksToStayPressed, BlockBehaviour block) {
        return new CustomButtonBlock(blockSetType, ticksToStayPressed, copyShallow(block).strength(0.5F).noCollission().pushReaction(PushReaction.DESTROY));
    }

    private static Block flowerPot(Block potted) {
        return new FlowerPotBlock(potted, createSettings().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY));
    }

    private static Block flowerPot(Block potted, int lightLevel) {
        return new FlowerPotBlock(potted, createSettings().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY).lightLevel(constant(lightLevel)));
    }

    private static Block novaclag(MapColor color) {
        return new NovaclagBlock(createSettings()
                .mapColor(color)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(0.9F, 0.9F)
                .sound(SoundType.MOSS)
                .lightLevel(constant(9))
                .randomTicks()
                .requiresCorrectToolForDrops()
        );
    }

    private static Block foghair(MapColor color) {
        return new FoghairBlock(createSettings()
                .mapColor(color)
                .replaceable()
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .pushReaction(PushReaction.DESTROY)
                .lightLevel(constant(11))
        );
    }
}
