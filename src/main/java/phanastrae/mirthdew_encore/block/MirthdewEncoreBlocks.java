package phanastrae.mirthdew_encore.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreBlocks {

    public static final Block DREAMTWIRL_BARRIER = new DreamtwirlBarrierBlock(createSettings()
            .strength(-1.0F, 3600000.8F)
            .mapColor(MapColor.CLEAR)
            .dropsNothing()
            .allowsSpawning(Blocks::never)
            .noBlockBreakParticles()
            .pistonBehavior(PistonBehavior.BLOCK));

    public static final Block VERIC_DREAMSNARE = new VericDreamsnareBlock(createSettings()
            .strength(2.0F, 2.0F)
            .mapColor(MapColor.CYAN)
            .sounds(BlockSoundGroup.SCULK)
            .luminance(state -> 4));

    public static final Block DREAMSEED = new DreamseedBlock(createSettings()
            .strength(2.0F, 8.0F)
            .mapColor(MapColor.MAGENTA)
            .sounds(BlockSoundGroup.WART_BLOCK)
            .luminance(state -> 5));

    public static final Block SLUMBERSOCKET = new SlumbersocketBlock((createSettings())
            .strength(30.0F, 1200.0F)
            .mapColor(MapColor.DEEPSLATE_GRAY)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresTool()
            .sounds(BlockSoundGroup.DEEPSLATE)
            .luminance(state -> state.get(SlumbersocketBlock.DREAMING) ? 9 : 13)
            .nonOpaque()
            .allowsSpawning(Blocks::never)
            .solidBlock(Blocks::never)
            .suffocates(Blocks::never)
            .blockVision(Blocks::never));

    public static final Block SLUMBERVEIL = new SlumberveilBlock(createSettings()
            .noCollision()
            .sounds(BlockSoundGroup.WOOL)
            .luminance(state -> 13)
            .pistonBehavior(PistonBehavior.DESTROY)
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
        Identifier identifier = MirthdewEncore.id(name);
        Registry.register(Registries.BLOCK, identifier, block);
    }

    private static AbstractBlock.Settings createSettings() {
        return AbstractBlock.Settings.create();
    }
}
