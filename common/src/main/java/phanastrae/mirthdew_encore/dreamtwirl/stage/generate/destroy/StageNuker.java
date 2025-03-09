package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.destroy;

import io.netty.buffer.Unpooled;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.*;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.Heightmap;
import phanastrae.mirthdew_encore.util.RegionPos;
import phanastrae.mirthdew_encore.world.biome.MirthdewEncoreBiomes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class StageNuker {

    public static ChunkPos getChunkPosForProgress(RegionPos regionPos, int progress) {
        // convert chunkDeletionProgress to a chunk pos in the 30x30 inbounds area

        progress = progress % 900;

        int cx = progress % 30;
        int cz = progress / 30;

        ChunkPos minChunkPos = regionPos.getMinChunkPos();
        return new ChunkPos(minChunkPos.x + cx + 1, minChunkPos.z + cz + 1);
    }

    public static void tryPreLoadChunk(ServerLevel level, ChunkPos chunkPos) {
        ServerChunkCache chunkSource = level.getChunkSource();
        chunkSource.addRegionTicket(TicketType.UNKNOWN, chunkPos, 0, chunkPos);
    }

    public static boolean tryClearChunk(ServerLevel level, ChunkPos chunkPos) {
        // try to get the chunk
        ServerChunkCache chunkSource = level.getChunkSource();

        LevelChunk chunk = chunkSource.getChunkNow(chunkPos.x, chunkPos.z);
        if(chunk == null) {
            return false;
        }

        // start clearing chunk
        chunk.setUnsaved(true);

        // setup empty containers
        PalettedContainer<BlockState> airBlockStateContainer = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
        Registry<Biome> biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
        PalettedContainer<Holder<Biome>> biomeContainer = new PalettedContainer<>(
                biomeRegistry.asHolderIdMap(), biomeRegistry.getHolderOrThrow(MirthdewEncoreBiomes.DREAMTWIRL), PalettedContainer.Strategy.SECTION_BIOMES
        );

        FriendlyByteBuf airBuf = new FriendlyByteBuf(Unpooled.buffer());
        airBuf.writeShort(0);
        airBlockStateContainer.write(airBuf);
        biomeContainer.write(airBuf);

        // clear sections
        int sectionsCleared = 0;
        LevelChunkSection[] sections = chunk.getSections();
        for(int s = 0; s < sections.length; s++) {
            LevelChunkSection section = sections[s];
            if(section.hasOnlyAir()) continue;
            airBuf.resetReaderIndex();
            section.read(airBuf);
            section.recalcBlockCounts();
            sectionsCleared++;

            SectionPos chunkSectionPos = SectionPos.of(chunkPos, s);
            ThreadedLevelLightEngine serverLightingProvider = level.getChunkSource().getLightEngine();
            serverLightingProvider.updateSectionStatus(chunkSectionPos, true);
            serverLightingProvider.queueSectionData(LightLayer.BLOCK, chunkSectionPos, null);
            serverLightingProvider.queueSectionData(LightLayer.SKY, chunkSectionPos, null);
        }
        if(sectionsCleared == 0) {
            return true;
        }

        // reset heightmaps
        List<Heightmap.Types> heightmapTypes = new ArrayList<>();
        for(Map.Entry<Heightmap.Types, Heightmap> entry : chunk.getHeightmaps()) {
            heightmapTypes.add(entry.getKey());
        }
        EnumSet<Heightmap.Types> enumSet = EnumSet.copyOf(heightmapTypes);
        Heightmap.primeHeightmaps(chunk, enumSet);

        // clear block entities
        chunk.clearAllBlockEntities();

        // send new chunk data to any players that are (somehow) in range
        List<ServerPlayer> l = level.getChunkSource().chunkMap.getPlayers(chunkPos, false);
        l.forEach(serverPlayerEntity -> {
            PlayerChunkSender chunkDataSender = serverPlayerEntity.connection.chunkSender;
            chunkDataSender.dropChunk(serverPlayerEntity, chunkPos);
            chunkDataSender.markChunkPendingToSend(chunk);
        });
        return true;
    }
}
