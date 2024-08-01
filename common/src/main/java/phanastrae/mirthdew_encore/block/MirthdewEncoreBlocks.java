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

public class MirthdewEncoreBlocks {

    public static final Block DREAMTWIRL_BARRIER = new DreamtwirlBarrierBlock(createSettings()
            .strength(-1.0F, 3600000.8F)
            .mapColor(MapColor.NONE)
            .noLootTable()
            .isValidSpawn(MirthdewEncoreBlocks::never)
            .noTerrainParticles()
            .pushReaction(PushReaction.BLOCK));

    public static final Block VERIC_DREAMSNARE = new VericDreamsnareBlock(createSettings()
            .strength(2.0F, 2.0F)
            .mapColor(MapColor.COLOR_CYAN)
            .sound(SoundType.SCULK)
            .lightLevel(state -> 4));

    public static final Block DREAMSEED = new DreamseedBlock(createSettings()
            .strength(2.0F, 8.0F)
            .mapColor(MapColor.COLOR_MAGENTA)
            .sound(SoundType.WART_BLOCK)
            .lightLevel(state -> 5));

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
            .isViewBlocking(MirthdewEncoreBlocks::never));

    public static final Block SLUMBERVEIL = new SlumberveilBlock(createSettings()
            .noCollission()
            .sound(SoundType.WOOL)
            .lightLevel(state -> 13)
            .pushReaction(PushReaction.DESTROY)
            .replaceable()
    );

    public static void init(BiConsumer<ResourceLocation, Block> r) {
        r.accept(id("dreamtwirl_barrier"), DREAMTWIRL_BARRIER);
        r.accept(id("veric_dreamsnare"), VERIC_DREAMSNARE);
        r.accept(id("dreamseed"), DREAMSEED);
        r.accept(id("slumbersocket"), SLUMBERSOCKET);
        r.accept(id("slumberveil"), SLUMBERVEIL);
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
}
