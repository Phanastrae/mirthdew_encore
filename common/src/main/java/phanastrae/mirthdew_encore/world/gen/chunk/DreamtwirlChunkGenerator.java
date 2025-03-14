package phanastrae.mirthdew_encore.world.gen.chunk;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.world.biome.MirthdewEncoreBiomes;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

public class DreamtwirlChunkGenerator extends ChunkGenerator {
    public static final MapCodec<DreamtwirlChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(RegistryOps.retrieveElement(MirthdewEncoreBiomes.DREAMTWIRL)).apply(instance, instance.stable(DreamtwirlChunkGenerator::new))
    );

    protected static final BlockState DREAMTWIRL_BARRIER = MirthdewEncoreBlocks.DREAMTWIRL_BARRIER.defaultBlockState();

    public DreamtwirlChunkGenerator(Holder.Reference<Biome> biomeEntry) {
        super(new FixedBiomeSource(biomeEntry));
    }


    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState noiseConfig, ChunkAccess chunk) {
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess chunk, StructureManager structureAccessor) {
        if(isBorderChunk(chunk)) {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            ChunkPos chunkPos = chunk.getPos();
            int cx = chunkPos.x;
            int cz = chunkPos.z;

            int bottomY = world.getMinBuildHeight();
            int topY = world.getMaxBuildHeight();

            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    for(int y = bottomY; y < topY; y++) {
                        int x = SectionPos.sectionToBlockCoord(cx, i);
                        int z = SectionPos.sectionToBlockCoord(cz, j);
                        world.setBlock(mutable.set(x, y, z), DREAMTWIRL_BARRIER, Block.UPDATE_CLIENTS);
                    }
                }
            }
        }
    }

    public boolean isBorderChunk(ChunkAccess chunk) {
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
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState noiseConfig, StructureManager structureAccessor, ChunkAccess chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState noiseConfig) {
        return 128;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState noiseConfig) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> text, RandomState noiseConfig, BlockPos pos) {
    }

    @Override
    public void applyCarvers(WorldGenRegion chunkRegion, long seed, RandomState noiseConfig, BiomeManager biomeAccess, StructureManager structureAccessor, ChunkAccess chunk, GenerationStep.Carving carverStep) {
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getGenDepth() {
        return 256;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }
}
