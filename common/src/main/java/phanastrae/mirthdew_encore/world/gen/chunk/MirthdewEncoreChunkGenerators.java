package phanastrae.mirthdew_encore.world.gen.chunk;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import phanastrae.mirthdew_encore.MirthdewEncore;

import java.util.function.BiConsumer;

public class MirthdewEncoreChunkGenerators {
    public static void init(BiConsumer<ResourceLocation, MapCodec<? extends ChunkGenerator>> r) {
        r.accept(id("dreamtwirl"), DreamtwirlChunkGenerator.CODEC);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }
}
