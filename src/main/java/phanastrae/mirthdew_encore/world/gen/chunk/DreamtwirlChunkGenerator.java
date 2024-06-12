package phanastrae.mirthdew_encore.world.gen.chunk;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.world.biome.MirthdewEncoreBiomes;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DreamtwirlChunkGenerator extends ChunkGenerator {
    public static final MapCodec<DreamtwirlChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(RegistryOps.getEntryCodec(MirthdewEncoreBiomes.DREAMTWIRL)).apply(instance, instance.stable(DreamtwirlChunkGenerator::new))
    );

    protected static final BlockState DREAMTWIRL_BARRIER = MirthdewEncoreBlocks.DREAMTWIRL_BARRIER.getDefaultState();

    public DreamtwirlChunkGenerator(RegistryEntry.Reference<Biome> biomeEntry) {
        super(new FixedBiomeSource(biomeEntry));
    }


    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        if(isBorderChunk(chunk)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            ChunkPos chunkPos = chunk.getPos();
            int cx = chunkPos.x;
            int cz = chunkPos.z;

            int bottomY = world.getBottomY();
            int topY = world.getTopY();

            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    for(int y = bottomY; y < topY; y++) {
                        int x = ChunkSectionPos.getOffsetPos(cx, i);
                        int z = ChunkSectionPos.getOffsetPos(cz, j);
                        world.setBlockState(mutable.set(x, y, z), DREAMTWIRL_BARRIER, Block.NOTIFY_LISTENERS);
                    }
                }
            }
        }
    }

    public boolean isBorderChunk(Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int cx = chunkPos.x & 0x1F;
        int cz = chunkPos.z & 0x1F;

        if(cx == 0 || cx == 31) {
            return true;
        } else if (cz == 0 || cz == 31) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 128;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getWorldHeight() {
        return 256;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }
}
