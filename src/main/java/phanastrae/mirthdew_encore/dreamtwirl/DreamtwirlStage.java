package phanastrae.mirthdew_encore.dreamtwirl;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ChunkDataSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.*;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.util.RegionPos;
import phanastrae.mirthdew_encore.world.biome.MirthdewEncoreBiomes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class DreamtwirlStage {

    private final World world;
    private final long id;
    private final RegionPos regionPos;
    private final long timestamp;
    private boolean markDirty = false;

    public DreamtwirlStage(World world, long id, long timestamp) {
        this.world = world;
        this.id = id;
        this.regionPos = new RegionPos(id);
        this.timestamp = timestamp;
    }

    public static boolean isIdAllowed(long id) {
        RegionPos rp = new RegionPos(id);
        return ((rp.regionX & 0x1) == 0) && ((rp.regionZ & 0x1) == 0);
    }

    public void generate(ServerWorld serverWorld) {
        DreamtwirlStageGenerator dreamtwirlStageGenerator = new DreamtwirlStageGenerator(this, serverWorld);
        dreamtwirlStageGenerator.generate();
    }

    public void clear(ServerWorld serverWorld) {
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(serverWorld);
        if(DTWA == null) {
            MirthdewEncore.LOGGER.warn("Someone tried to clear a non Dreamtwirl region???? how???");
            return;
        }
        // TODO this is a dubiously functional and safe debug command, delete or improve before release
        ChunkPos regionChunkPos = ChunkPos.fromRegion(this.regionPos.regionX, this.regionPos.regionZ);

        PalettedContainer<BlockState> airBlockStateContainer = new PalettedContainer<>(Block.STATE_IDS, Blocks.AIR.getDefaultState(), PalettedContainer.PaletteProvider.BLOCK_STATE);
        PalettedContainer<BlockState> barrierBlockStateContainer = new PalettedContainer<>(Block.STATE_IDS, MirthdewEncoreBlocks.DREAMTWIRL_BARRIER.getDefaultState(), PalettedContainer.PaletteProvider.BLOCK_STATE);
        Registry<Biome> biomeRegistry = serverWorld.getRegistryManager().get(RegistryKeys.BIOME);
        PalettedContainer<RegistryEntry<Biome>> biomeContainer = new PalettedContainer<>(
                biomeRegistry.getIndexedEntries(), biomeRegistry.entryOf(MirthdewEncoreBiomes.DREAMTWIRL), PalettedContainer.PaletteProvider.BIOME
        );

        PacketByteBuf airBuf = PacketByteBufs.create();
        airBuf.writeShort(0);
        airBlockStateContainer.writePacket(airBuf);
        biomeContainer.writePacket(airBuf);

        ServerLightingProvider serverLightingProvider = serverWorld.getChunkManager().getLightingProvider();
        for(int i = 1; i < 31; i++) {
            for(int j = 1; j < 31; j++) {

                ChunkPos chunkPos = new ChunkPos(regionChunkPos.x + i, regionChunkPos.z + j);
                Chunk chunk = serverWorld.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
                if(chunk instanceof WorldChunk worldChunk) {
                    int sectionsCleared = 0;
                    ChunkSection[] sections = worldChunk.getSectionArray();
                    for(int s = 0; s < sections.length; s++) {
                        ChunkSection section = sections[s];
                        if(section.isEmpty()) continue;
                        airBuf.resetReaderIndex();
                        section.readDataPacket(airBuf);
                        section.calculateCounts();
                        sectionsCleared++;

                        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos, s);
                        serverLightingProvider.setSectionStatus(chunkSectionPos, true);
                        serverLightingProvider.enqueueSectionData(LightType.BLOCK, chunkSectionPos, null);
                        serverLightingProvider.enqueueSectionData(LightType.SKY, chunkSectionPos, null);
                    }
                    if(sectionsCleared == 0) {
                        continue;
                    }

                    List<Heightmap.Type> heightmapTypes = new ArrayList<>();
                    for(Map.Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
                        heightmapTypes.add(entry.getKey());
                    }
                    EnumSet<Heightmap.Type> enumSet = EnumSet.copyOf(heightmapTypes);
                    Heightmap.populateHeightmaps(chunk, enumSet);

                    worldChunk.clear();
                    worldChunk.setNeedsSaving(true);

                    List<ServerPlayerEntity> l = serverWorld.getChunkManager().chunkLoadingManager.getPlayersWatchingChunk(chunkPos, false);
                    l.forEach(serverPlayerEntity -> {
                        ChunkDataSender chunkDataSender = serverPlayerEntity.networkHandler.chunkDataSender;
                        chunkDataSender.unload(serverPlayerEntity, chunkPos);
                        chunkDataSender.add(worldChunk);
                    });

                    // TODO clear pathnodetypecache if necessary? probably not though
                }
            }
        }
    }

    public void tick(ServerWorld world) {

    }

    public void markDirty() {
        this.markDirty(true);
    }

    public void markDirty(boolean value) {
        this.markDirty = value;
    }

    public boolean isDirty() {
        return this.markDirty;
    }

    public long getId() {
        return this.id;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public RegionPos getRegionPos() {
        return this.regionPos;
    }

    public World getWorld() {
        return this.world;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putLong("Id", this.getId());
        nbt.putLong("Timestamp", this.getTimestamp());
        return nbt;
    }

    public static DreamtwirlStage fromNbt(World world, NbtCompound nbt) {
        long id = nbt.getLong("Id");
        long timestamp = nbt.getLong("Timestamp");
        return new DreamtwirlStage(world, id, timestamp);
    }
}
