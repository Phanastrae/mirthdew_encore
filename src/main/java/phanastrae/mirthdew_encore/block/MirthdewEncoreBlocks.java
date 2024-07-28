package phanastrae.mirthdew_encore.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreBlocks {

    public static final Block DREAMTWIRL_BARRIER = new DreamtwirlBarrierBlock(createSettings()
            .strength(-1.0F, 3600000.8F)
            .mapColor(MapColor.NONE)
            .noLootTable()
            .isValidSpawn(Blocks::never)
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
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor(Blocks::never)
            .isSuffocating(Blocks::never)
            .isViewBlocking(Blocks::never));

    public static final Block SLUMBERVEIL = new SlumberveilBlock(createSettings()
            .noCollission()
            .sound(SoundType.WOOL)
            .lightLevel(state -> 13)
            .pushReaction(PushReaction.DESTROY)
            .replaceable()
    );

    public static void init() {
        register(DREAMTWIRL_BARRIER, "dreamtwirl_barrier");
        register(VERIC_DREAMSNARE, "veric_dreamsnare");
        register(DREAMSEED, "dreamseed");
        register(SLUMBERSOCKET, "slumbersocket");
        register(SLUMBERVEIL, "slumberveil");
    }

    private static void register(Block block, String name) {
        ResourceLocation identifier = MirthdewEncore.id(name);
        Registry.register(BuiltInRegistries.BLOCK, identifier, block);
    }

    private static BlockBehaviour.Properties createSettings() {
        return BlockBehaviour.Properties.of();
    }
}
