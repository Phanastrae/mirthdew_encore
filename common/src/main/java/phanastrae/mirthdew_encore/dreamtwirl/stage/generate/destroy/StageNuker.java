package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.destroy;

import io.netty.buffer.Unpooled;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlLevelAttachment;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.world.biome.MirthdewEncoreBiomes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class StageNuker {

    public static void clear(ServerLevel level, DreamtwirlStage stage) {
        DreamtwirlLevelAttachment DTWA = DreamtwirlLevelAttachment.fromLevel(level);
        if(DTWA == null) {
            MirthdewEncore.LOGGER.warn("Someone tried to clear a non-Dreamtwirl region???? how???");
            return;
        }
        // TODO this is a dubiously functional and safe debug command, delete or improve before release
        ChunkPos regionChunkPos = ChunkPos.minFromRegion(stage.getRegionPos().regionX, stage.getRegionPos().regionZ);

        PalettedContainer<BlockState> airBlockStateContainer = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
        PalettedContainer<BlockState> barrierBlockStateContainer = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, MirthdewEncoreBlocks.DREAMTWIRL_BARRIER.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
        Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
        PalettedContainer<Holder<Biome>> biomeContainer = new PalettedContainer<>(
                biomeRegistry.asHolderIdMap(), biomeRegistry.getHolderOrThrow(MirthdewEncoreBiomes.DREAMTWIRL), PalettedContainer.Strategy.SECTION_BIOMES
        );

        FriendlyByteBuf airBuf = new FriendlyByteBuf(Unpooled.buffer());
        airBuf.writeShort(0);
        airBlockStateContainer.write(airBuf);
        biomeContainer.write(airBuf);

        // preload chunks or something
        // TODO check this makes sense and also actually works
        ServerChunkCache chunkSource = level.getChunkSource();
        for(int i = 1; i < 31; i++) {
            for (int j = 1; j < 31; j++) {
                ChunkPos chunkPos = new ChunkPos(regionChunkPos.x + i, regionChunkPos.z + j);
                chunkSource.getChunkFuture(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
            }
        }

        ThreadedLevelLightEngine serverLightingProvider = level.getChunkSource().getLightEngine();
        for(int i = 1; i < 31; i++) {
            for(int j = 1; j < 31; j++) {

                ChunkPos chunkPos = new ChunkPos(regionChunkPos.x + i, regionChunkPos.z + j);
                ChunkAccess chunk = chunkSource.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
                if(chunk instanceof LevelChunk levelChunk) {
                    int sectionsCleared = 0;
                    LevelChunkSection[] sections = levelChunk.getSections();
                    for(int s = 0; s < sections.length; s++) {
                        LevelChunkSection section = sections[s];
                        if(section.hasOnlyAir()) continue;
                        airBuf.resetReaderIndex();
                        section.read(airBuf);
                        section.recalcBlockCounts();
                        sectionsCleared++;

                        SectionPos chunkSectionPos = SectionPos.of(chunkPos, s);
                        serverLightingProvider.updateSectionStatus(chunkSectionPos, true);
                        serverLightingProvider.queueSectionData(LightLayer.BLOCK, chunkSectionPos, null);
                        serverLightingProvider.queueSectionData(LightLayer.SKY, chunkSectionPos, null);
                    }
                    if(sectionsCleared == 0) {
                        continue;
                    }

                    List<Heightmap.Types> heightmapTypes = new ArrayList<>();
                    for(Map.Entry<Heightmap.Types, Heightmap> entry : chunk.getHeightmaps()) {
                        heightmapTypes.add(entry.getKey());
                    }
                    EnumSet<Heightmap.Types> enumSet = EnumSet.copyOf(heightmapTypes);
                    Heightmap.primeHeightmaps(chunk, enumSet);

                    levelChunk.clearAllBlockEntities();
                    levelChunk.setUnsaved(true);

                    List<ServerPlayer> l = level.getChunkSource().chunkMap.getPlayers(chunkPos, false);
                    l.forEach(serverPlayerEntity -> {
                        PlayerChunkSender chunkDataSender = serverPlayerEntity.connection.chunkSender;
                        chunkDataSender.dropChunk(serverPlayerEntity, chunkPos);
                        chunkDataSender.markChunkPendingToSend(levelChunk);
                    });

                    // TODO clear pathnodetypecache if necessary? probably not though
                }
            }
        }
    }
}
