package phanastrae.mirthlight_encore.world.gen.chunk;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import phanastrae.mirthlight_encore.MirthlightEncore;

public class MirthlightEncoreChunkGenerators {
    public static void init() {
        Registry.register(Registries.CHUNK_GENERATOR, MirthlightEncore.id("dreamtwirl"), DreamtwirlChunkGenerator.CODEC);
    }
}
