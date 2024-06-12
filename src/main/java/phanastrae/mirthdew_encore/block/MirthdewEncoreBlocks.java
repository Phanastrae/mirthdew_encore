package phanastrae.mirthdew_encore.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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

    public static void init() {
        register(DREAMTWIRL_BARRIER, "dreamtwirl_barrier");
    }

    private static void register(Block block, String name) {
        Identifier identifier = MirthdewEncore.id(name);
        Registry.register(Registries.BLOCK, identifier, block);
    }

    private static AbstractBlock.Settings createSettings() {
        return AbstractBlock.Settings.create();
    }
}
