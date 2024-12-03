package phanastrae.mirthdew_encore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import phanastrae.mirthdew_encore.MirthdewEncore;

import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

public class MirthdewEncoreBlocks {

    public static final Block DREAMSEED = new DreamseedBlock(createSettings()
            .strength(2.0F, 8.0F)
            .mapColor(MapColor.COLOR_MAGENTA)
            .sound(SoundType.WART_BLOCK)
            .lightLevel(constant(5))
    );

    public static final Block DREAMTWIRL_BARRIER = new DreamtwirlBarrierBlock(createSettings()
            .strength(-1.0F, 3600000.8F)
            .mapColor(MapColor.NONE)
            .noLootTable()
            .isValidSpawn(MirthdewEncoreBlocks::never)
            .noTerrainParticles()
            .pushReaction(PushReaction.BLOCK)
    );

    public static Block FROSTED_REVERIME = new Block(createSettings()
            .mapColor(MapColor.ICE)
            .strength(2.5F, 1.8F)
            .friction(0.93F)
            .sound(SoundType.GLASS)
    );

    public static Block POLISHED_REVERIME = new Block(createSettings()
            .mapColor(MapColor.ICE)
            .strength(2.2F, 1.3F)
            .friction(0.7F)
            .sound(SoundType.GLASS)
    );

    public static Block ROSENGLACE = new Block(createSettings()
            .mapColor(MapColor.TERRACOTTA_PINK)
            .strength(3.5F, 2.2F)
            .friction(0.98F)
            .sound(SoundType.GLASS)
    );

    public static Block REVERIME = new Block(createSettings()
            .mapColor(MapColor.ICE)
            .strength(2.2F, 1.3F)
            .friction(0.85F)
            .sound(SoundType.GLASS)
    );

    public static Block REVERIME_BRICKS = new Block(createSettings()
            .mapColor(MapColor.ICE)
            .strength(2.2F, 1.3F)
            .friction(0.7F)
            .sound(SoundType.GLASS)
    );

    public static Block REVERIME_TILES = new Block(createSettings()
            .mapColor(MapColor.ICE)
            .strength(2.2F, 1.3F)
            .friction(0.7F)
            .sound(SoundType.GLASS)
    );

    public static Block SCARABRIM = new Block(createSettings()
            .mapColor(MapColor.COLOR_BLACK)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(1.0F, 1.7F)
            .sound(SoundType.DRIPSTONE_BLOCK)
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

    public static Block SUNFLECKED_SCARABRIM = new Block(createSettings()
            .mapColor(MapColor.COLOR_YELLOW)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .strength(1.2F, 1.7F)
            .sound(SoundType.DRIPSTONE_BLOCK)
            .lightLevel(constant(11))
    );

    public static final Block VERIC_DREAMSNARE = new VericDreamsnareBlock(createSettings()
            .strength(2.0F, 2.0F)
            .mapColor(MapColor.COLOR_CYAN)
            .sound(SoundType.SCULK)
            .lightLevel(constant(4))
    );

    public static void init(BiConsumer<ResourceLocation, Block> r) {
        r.accept(id("dreamtwirl_barrier"), DREAMTWIRL_BARRIER);
        r.accept(id("veric_dreamsnare"), VERIC_DREAMSNARE);
        r.accept(id("dreamseed"), DREAMSEED);
        r.accept(id("slumbersocket"), SLUMBERSOCKET);
        r.accept(id("slumberveil"), SLUMBERVEIL);
        r.accept(id("reverime"), REVERIME);
        r.accept(id("frosted_reverime"), FROSTED_REVERIME);
        r.accept(id("polished_reverime"), POLISHED_REVERIME);
        r.accept(id("reverime_bricks"), REVERIME_BRICKS);
        r.accept(id("reverime_tiles"), REVERIME_TILES);
        r.accept(id("rosenglace"), ROSENGLACE);
        r.accept(id("scarabrim"), SCARABRIM);
        r.accept(id("sunflecked_scarabrim"), SUNFLECKED_SCARABRIM);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }

    private static BlockBehaviour.Properties createSettings() {
        return BlockBehaviour.Properties.of();
    }

    private static Boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> entity) {
        return false;
    }

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }

    private static ToIntFunction<BlockState> constant(int level) {
        return blockState -> level;
    }
}
