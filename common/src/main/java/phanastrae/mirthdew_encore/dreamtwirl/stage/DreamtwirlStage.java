package phanastrae.mirthdew_encore.dreamtwirl.stage;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlWorldAttachment;
import phanastrae.mirthdew_encore.util.RegionPos;
import phanastrae.mirthdew_encore.world.biome.MirthdewEncoreBiomes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class DreamtwirlStage {

    private final Level world;
    private final long id;
    private final RegionPos regionPos;
    private final long timestamp;
    private boolean markDirty = false;

    private final RoomStorage roomStorage;

    private final BoundingBox areaBox;
    private final ChunkPos stageChunkCenter;

    public DreamtwirlStage(Level world, long id, long timestamp) {
        this.world = world;
        this.id = id;
        this.regionPos = new RegionPos(id);
        this.timestamp = timestamp;
        this.roomStorage = new RoomStorage(this);


        BlockPos stageBlockCenter = new BlockPos(this.regionPos.getCenterX(), 128, this.regionPos.getCenterZ());
        this.stageChunkCenter = new ChunkPos(stageBlockCenter);

        BlockPos minPos = new BlockPos(this.regionPos.worldX + 16, this.getWorld().getMinBuildHeight(), this.regionPos.worldZ + 16);
        BlockPos maxPos = new BlockPos(this.regionPos.worldX + 16 * 31 - 1, this.getWorld().getMaxBuildHeight() - 1, this.regionPos.worldZ + 16 * 31 - 1);
        this.areaBox = new BoundingBox(minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX(), maxPos.getY(), maxPos.getZ());
    }

    public static boolean isIdAllowed(long id) {
        RegionPos rp = new RegionPos(id);
        return ((rp.regionX & 0x1) == 0) && ((rp.regionZ & 0x1) == 0);
    }

    public void tick(ServerLevel world) {
        // TODO tweak, optimise, etc.
        RandomSource random = world.getRandom();

        List<DreamtwirlRoom> removeRooms = new ObjectArrayList<>();
        for(DreamtwirlRoom room : this.roomStorage.getRooms()) {
            if (random.nextInt(45) != 0) {
                continue;
            }

            if (room.canSpawn() && RoomPlacer.placeRoom(room, world, this.stageChunkCenter, this.areaBox)) {
                removeRooms.add(room);
                room.setNeighborsCanSpawn();
                this.markDirty();

                BoundingBox box = room.getBoundingBox();

                world.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                        0.5 * (box.minX() + box.maxX()),
                        0.5 * (box.minY() + box.maxY()),
                        0.5 * (box.minZ() + box.maxZ()),
                        600,
                        0.5 * (box.maxX() - box.minX()),
                        0.5 * (box.maxY() - box.minY()),
                        0.5 * (box.maxZ() - box.minZ()),
                        0.15);
            }
        }
        this.roomStorage.getRooms().removeAll(removeRooms);


        /*
        for(int i = 0; i < 30; i++) {
            DreamtwirlRoom room = this.roomStorage.removeRoom(random);
            if (room != null) {
                BoundingBox bb = room.getBoundingBox();
                AABB roomAABB = new AABB(bb.minX(), bb.minY(), bb.minZ(), bb.maxX(), bb.maxY(), bb.maxZ());
                roomAABB = roomAABB.inflate(16);
                boolean hit = false;
                for(ServerPlayer player : world.players()) {
                    if(player.getBoundingBox().intersects(roomAABB)) {
                        hit = true;
                        break;
                    }
                }


                //boolean hit = world.getRandom().nextInt(15) == 0;
                boolean hit = true;
                if(hit && room.canSpawn() && this.roomStorage.placeRoom(room, world)) {
                    room.setNeighborsCanSpawn();
                    this.markDirty();

                    BoundingBox box = room.getBoundingBox();

                    world.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                            0.5 * (box.minX() + box.maxX()),
                            0.5 * (box.minY() + box.maxY()),
                            0.5 * (box.minZ() + box.maxZ()),
                            600,
                            0.5 * (box.maxX() - box.minX()),
                            0.5 * (box.maxY() - box.minY()),
                            0.5 * (box.maxZ() - box.minZ()),
                            0.15);
                } else {
                    this.roomStorage.addRoom(room);
                }
            }
        }

         */

    }

    public void generate(ServerLevel serverLevel) {
        DreamtwirlStageGenerator dreamtwirlStageGenerator = new DreamtwirlStageGenerator(this.roomStorage, serverLevel);
        dreamtwirlStageGenerator.generate();
        dreamtwirlStageGenerator.transferRoomGroups();
        this.markDirty();
    }

    public void clear(ServerLevel serverWorld) {
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(serverWorld);
        if(DTWA == null) {
            MirthdewEncore.LOGGER.warn("Someone tried to clear a non Dreamtwirl region???? how???");
            return;
        }
        // TODO this is a dubiously functional and safe debug command, delete or improve before release
        ChunkPos regionChunkPos = ChunkPos.minFromRegion(this.regionPos.regionX, this.regionPos.regionZ);

        PalettedContainer<BlockState> airBlockStateContainer = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
        PalettedContainer<BlockState> barrierBlockStateContainer = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, MirthdewEncoreBlocks.DREAMTWIRL_BARRIER.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
        Registry<Biome> biomeRegistry = serverWorld.registryAccess().registryOrThrow(Registries.BIOME);
        PalettedContainer<Holder<Biome>> biomeContainer = new PalettedContainer<>(
                biomeRegistry.asHolderIdMap(), biomeRegistry.getHolderOrThrow(MirthdewEncoreBiomes.DREAMTWIRL), PalettedContainer.Strategy.SECTION_BIOMES
        );

        FriendlyByteBuf airBuf = new FriendlyByteBuf(Unpooled.buffer());
        airBuf.writeShort(0);
        airBlockStateContainer.write(airBuf);
        biomeContainer.write(airBuf);

        // preload chunks or something
        // TODO check this makes sense and also actually works
        ServerChunkCache chunkSource = serverWorld.getChunkSource();
        for(int i = 1; i < 31; i++) {
            for (int j = 1; j < 31; j++) {
                ChunkPos chunkPos = new ChunkPos(regionChunkPos.x + i, regionChunkPos.z + j);
                chunkSource.getChunkFuture(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
            }
        }

        ThreadedLevelLightEngine serverLightingProvider = serverWorld.getChunkSource().getLightEngine();
        for(int i = 1; i < 31; i++) {
            for(int j = 1; j < 31; j++) {

                ChunkPos chunkPos = new ChunkPos(regionChunkPos.x + i, regionChunkPos.z + j);
                ChunkAccess chunk = chunkSource.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
                if(chunk instanceof LevelChunk worldChunk) {
                    int sectionsCleared = 0;
                    LevelChunkSection[] sections = worldChunk.getSections();
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

                    worldChunk.clearAllBlockEntities();
                    worldChunk.setUnsaved(true);

                    List<ServerPlayer> l = serverWorld.getChunkSource().chunkMap.getPlayers(chunkPos, false);
                    l.forEach(serverPlayerEntity -> {
                        PlayerChunkSender chunkDataSender = serverPlayerEntity.connection.chunkSender;
                        chunkDataSender.dropChunk(serverPlayerEntity, chunkPos);
                        chunkDataSender.markChunkPendingToSend(worldChunk);
                    });

                    // TODO clear pathnodetypecache if necessary? probably not though
                }
            }
        }
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

    public Level getWorld() {
        return this.world;
    }

    public CompoundTag writeNbt(CompoundTag nbt) {
        nbt.putLong("Id", this.getId());
        nbt.putLong("Timestamp", this.getTimestamp());
        return nbt;
    }

    public static DreamtwirlStage fromNbt(Level world, CompoundTag nbt) {
        long id = nbt.getLong("Id");
        long timestamp = nbt.getLong("Timestamp");
        return new DreamtwirlStage(world, id, timestamp);
    }
}
