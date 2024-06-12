package phanastrae.mirthdew_encore.world.gen.chunk;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreChunkGenerators {
    public static void init() {
        Registry.register(Registries.CHUNK_GENERATOR, MirthdewEncore.id("dreamtwirl"), DreamtwirlChunkGenerator.CODEC);
    }
}
