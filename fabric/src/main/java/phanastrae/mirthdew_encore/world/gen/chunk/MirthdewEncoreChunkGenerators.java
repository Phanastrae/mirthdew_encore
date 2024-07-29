package phanastrae.mirthdew_encore.world.gen.chunk;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreChunkGenerators {
    public static void init() {
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, MirthdewEncore.id("dreamtwirl"), DreamtwirlChunkGenerator.CODEC);
    }
}
