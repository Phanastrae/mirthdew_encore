package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;
import phanastrae.mirthdew_encore.structure.intermediate.BoxedContainer;
import phanastrae.mirthdew_encore.structure.intermediate.IntermediateGenLevel;
import phanastrae.mirthdew_encore.structure.intermediate.IntermediateStructureStorage;

import java.util.List;
import java.util.Optional;

public class PlaceableRoom {
    public static final String KEY_ROOM_DATA = "room_data";
    public static final String KEY_ROOM_ID = "room_id";
    public static final String KEY_ROOM_PLACED = "room_placed";
    public static final String KEY_LYCHSEAL_DOOR_ENTRIES = "lychseal_door_entries";
    public static final String KEY_PLACEMENT_DATA = "placement_data";

    private final Room room;
    private final int roomId;
    private boolean roomPlaced = false;
    private final List<LychsealDoorEntry> lychsealDoorEntries = new ObjectArrayList<>();
    @Nullable
    private PlacementData placementData;

    public PlaceableRoom(Room room, int roomId) {
        this.room = room;
        this.roomId = roomId;
    }

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        nbt.put(KEY_ROOM_DATA, this.room.writeNbt(new CompoundTag(), registries, spsContext));
        nbt.putInt(KEY_ROOM_ID, this.roomId);

        nbt.putBoolean(KEY_ROOM_PLACED, this.roomPlaced);

        RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);
        LychsealDoorEntry.CODEC.listOf()
                .encodeStart(registryops, this.lychsealDoorEntries)
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to encode lychseal-door entries for Placeable Room: '{}'", st))
                .ifPresent(bpdTag -> nbt.put(KEY_LYCHSEAL_DOOR_ENTRIES, bpdTag));

        if(this.placementData != null) {
            nbt.put(KEY_PLACEMENT_DATA, this.placementData.writeNbt(new CompoundTag(), registries));
        }
        return nbt;
    }

    public static @Nullable PlaceableRoom fromNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext, Level level) {
        Room room;
        if(nbt.contains(KEY_ROOM_DATA, Tag.TAG_COMPOUND)) {
            room = Room.fromNbt(nbt.getCompound(KEY_ROOM_DATA), registries, spsContext);
            if(room == null) {
                return null;
            }
        } else {
            return null;
        }

        int roomId;
        if(nbt.contains(KEY_ROOM_ID, Tag.TAG_INT)) {
            roomId = nbt.getInt(KEY_ROOM_ID);
        } else {
            return null;
        }

        PlaceableRoom placeableRoom = new PlaceableRoom(room, roomId);

        if(nbt.contains(KEY_ROOM_PLACED, Tag.TAG_BYTE)) {
            placeableRoom.roomPlaced = nbt.getBoolean(KEY_ROOM_PLACED);
        }

        RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);
        LychsealDoorEntry.CODEC.listOf()
                .parse(registryops, nbt.get(KEY_LYCHSEAL_DOOR_ENTRIES))
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to parse lychseal-door entries for Placeable Room: '{}'", st))
                .ifPresent(placeableRoom::addEntries);

        if(nbt.contains(KEY_PLACEMENT_DATA, Tag.TAG_COMPOUND)) {
            placeableRoom.placementData = PlacementData.fromNbt(nbt.getCompound(KEY_PLACEMENT_DATA), registries, level);
        } else {
            placeableRoom.placementData = null;
        }

        return placeableRoom;
    }

    public void beginPlacementFromCenter(boolean forceLoadChunks) {
        this.beginPlacement(this.room.getBoundingBox().getCenter(), forceLoadChunks);
    }

    public void beginPlacement(BlockPos startPos, boolean forceLoadChunks) {
        if(!this.roomPlaced) {
            if (this.placementData == null) {
                this.placementData = new PlacementData(startPos, forceLoadChunks);
            }
        }
    }

    public void tick(ServerLevel level, PlaceableRoomStorage roomStorage, BoundingBox stageBB, DreamtwirlStage stage) {
        if(this.placementData != null) {
            if(!this.placementData.storageFilled) {
                this.placementData.createStructure(this, level, stageBB);

                stage.setDirty();
            }

            if(this.placementData.spawnTime <= this.placementData.maxSpawnTime) {
                if(this.placementData.placeForTime(this, roomStorage, level, this.placementData.spawnTime)) {
                    this.placementData.spawnTime++;

                    stage.setDirty();
                }
            } else {
                if(this.placementData.place(level)) {
                    this.placementData.intermediateStructureStorage = null;
                    this.placementData.storageFilled = false;
                    this.roomPlaced = true;

                    // empty lychseals should already all be open, but do it again just in case
                    this.openEmptyLychseals(roomStorage);

                    stage.setDirty();
                }
            }
        }
    }

    public boolean shouldTick() {
        return this.placementData != null && !this.roomPlaced;
    }

    public boolean openLychseal(PlaceableRoomStorage roomStorage, String lychsealName) {
        boolean opened = false;
        for(LychsealDoorEntry entry : this.lychsealDoorEntries) {
            if(entry.lychsealName.equals(lychsealName)) {
                roomStorage.startSpawningRoom(entry.endRoomId, entry.endPos, false);
                opened = true;
            }
        }
        return opened;
    }

    public void openEmptyLychseals(PlaceableRoomStorage roomStorage) {
        this.openLychseal(roomStorage, "");
    }

    public void addLychsealDoorEntry(RoomDoor startDoor, RoomDoor targetDoor, PlaceableRoom room) {
        this.lychsealDoorEntries.add(new LychsealDoorEntry(startDoor.getTargetLychseal(), startDoor.getPos().immutable(), targetDoor.getPos().immutable(), room.getRoomId()));
    }

    public void addEntries(List<LychsealDoorEntry> entries) {
        this.lychsealDoorEntries.addAll(entries);
    }

    public Room getRoom() {
        return room;
    }

    public int getRoomId() {
        return roomId;
    }

    public record LychsealDoorEntry(String lychsealName, BlockPos startPos, BlockPos endPos, int endRoomId) {
        public static final Codec<LychsealDoorEntry> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                Codec.STRING.optionalFieldOf("lychseal_name", "").forGetter(LychsealDoorEntry::lychsealName),
                                BlockPos.CODEC.fieldOf("start").forGetter(LychsealDoorEntry::startPos),
                                BlockPos.CODEC.fieldOf("end").forGetter(LychsealDoorEntry::endPos),
                                Codec.INT.fieldOf("end_room_id").forGetter(LychsealDoorEntry::endRoomId)
                        )
                        .apply(instance, LychsealDoorEntry::new)
        );
    }

    public static class PlacementData {
        public static final String KEY_PLACEMENT_ORIGIN = "placement_origin";
        public static final String KEY_SHOULD_FORCE_LOAD = "force_load_chunks";
        public static final String KEY_SPAWN_TIME = "spawn_time";
        public static final String KEY_MAX_SPAWN_TIME = "max_spawn_time";
        public static final String KEY_STORAGE = "storage";
        public static final String KEY_STORAGE_FILLED = "storage_filled";

        private final BlockPos placementOrigin;
        private final boolean shouldForceLoadChunks;

        private int spawnTime = 0;
        private int maxSpawnTime = 0;

        @Nullable
        private IntermediateStructureStorage intermediateStructureStorage;
        private boolean storageFilled = false;

        public PlacementData(BlockPos placementOrigin, boolean shouldForceLoadChunks) {
            this.placementOrigin = placementOrigin;
            this.shouldForceLoadChunks = shouldForceLoadChunks;
        }

        public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
            RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);

            BlockPos.CODEC
                    .encodeStart(registryops, this.placementOrigin)
                    .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to encode placement origin entries for Placement Data: '{}'", st))
                    .ifPresent(bpdTag -> nbt.put(KEY_PLACEMENT_ORIGIN, bpdTag));

            nbt.putBoolean(KEY_SHOULD_FORCE_LOAD, this.shouldForceLoadChunks);

            nbt.putInt(KEY_SPAWN_TIME, spawnTime);
            nbt.putInt(KEY_MAX_SPAWN_TIME, maxSpawnTime);

            nbt.putBoolean(KEY_STORAGE_FILLED, this.storageFilled);

            if(this.intermediateStructureStorage != null) {
                nbt.put(KEY_STORAGE, this.intermediateStructureStorage.writeNbt(new CompoundTag(), registries));
            }
            return nbt;
        }

        public static @Nullable PlacementData fromNbt(CompoundTag nbt, HolderLookup.Provider registries, Level level) {
            RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);

            if(!nbt.contains(KEY_PLACEMENT_ORIGIN)) {
                return null;
            }
            Optional<BlockPos> posOptional = BlockPos.CODEC
                    .parse(registryops, nbt.get(KEY_PLACEMENT_ORIGIN))
                    .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to parse placement origin entries for Placement Data: '{}'", st));
            if(posOptional.isEmpty()) return null;
            BlockPos pos = posOptional.get();

            boolean shouldForceLoad = false;
            if(nbt.contains(KEY_SHOULD_FORCE_LOAD, Tag.TAG_BYTE)) {
                shouldForceLoad = nbt.getBoolean(KEY_SHOULD_FORCE_LOAD);
            }

            PlacementData data = new PlacementData(pos, shouldForceLoad);

            if(nbt.contains(KEY_SPAWN_TIME, Tag.TAG_INT)) {
                data.spawnTime = nbt.getInt(KEY_SPAWN_TIME);
            }
            if(nbt.contains(KEY_MAX_SPAWN_TIME, Tag.TAG_INT)) {
                data.maxSpawnTime = nbt.getInt(KEY_MAX_SPAWN_TIME);
            }

            if(nbt.contains(KEY_STORAGE_FILLED, Tag.TAG_BYTE)) {
                data.storageFilled = nbt.getBoolean(KEY_STORAGE_FILLED);
            }

            if(nbt.contains(KEY_STORAGE, Tag.TAG_COMPOUND)) {
                data.intermediateStructureStorage = new IntermediateStructureStorage();
                data.intermediateStructureStorage.readNbt(nbt.getCompound(KEY_STORAGE), registries, level);
            } else {
                data.intermediateStructureStorage = null;
            }

            return data;
        }

        public boolean placeForTime(PlaceableRoom room, PlaceableRoomStorage roomStorage, ServerLevel level, int time) {
            if(!this.storageFilled || this.intermediateStructureStorage == null) {
                return false;
            } else {
                // TODO limit to loaded chunks

                RandomSource random = level.getRandom();

                // place blocks
                this.intermediateStructureStorage.forEachContainer((sectionPos, boxedContainer) -> {
                    BoundingBox box = boxedContainer.getBox();
                    if(box == null) return;

                    BoxedContainer fragile = this.intermediateStructureStorage.getFragileContainer(sectionPos);
                    if(fragile == null) {
                        fragile = new BoxedContainer();
                        this.intermediateStructureStorage.addFragileContainer(sectionPos, fragile);
                    }

                    int mx = sectionPos.minBlockX();
                    int my = sectionPos.minBlockY();
                    int mz = sectionPos.minBlockZ();

                    BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                    for(int x = box.minX(); x <= box.maxX(); x++) {
                        for(int y = box.minY(); y <= box.maxY(); y++) {
                            for(int z = box.minZ(); z <= box.maxZ(); z++) {
                                BlockState state = boxedContainer.get(x, y, z);

                                if(!state.is(Blocks.STRUCTURE_VOID)) {
                                    mutableBlockPos.set(mx + x, my + y, mz + z);

                                    int targetTimeFoam = RoomActivePlacement.getTimeToReachPos(this.placementOrigin, mutableBlockPos, false, mx + x, mz + z);
                                    int targetTimeBlock = RoomActivePlacement.getTimeToReachPos(this.placementOrigin, mutableBlockPos, true, mx + x, mz + z);
                                    // TODO optimise
                                    if(time == targetTimeBlock || time == targetTimeFoam) {
                                        BlockState targetState = boxedContainer.get(x, y, z);
                                        if(targetState.is(Blocks.STRUCTURE_VOID)) {
                                            continue;
                                        }
                                        BlockState oldState = level.getBlockState(mutableBlockPos);

                                        if(time == targetTimeBlock) {
                                            // place block
                                            if(!targetState.isAir() || !oldState.isAir()) {
                                                if(RoomActivePlacement.isStateFragile(targetState, level, mutableBlockPos)) {
                                                    fragile.set(x, y, z, targetState);
                                                } else {
                                                    RoomActivePlacement.setBlock(level, mutableBlockPos, targetState, true);
                                                    RoomActivePlacement.playBlockPlaceEffects(level, random, mutableBlockPos);
                                                }
                                            }
                                        } else {
                                            // place foam
                                            if(!targetState.isAir() || !oldState.isAir()) {
                                                BlockState newState = MirthdewEncoreBlocks.MEMORY_FOAM.defaultBlockState();
                                                RoomActivePlacement.setBlock(level, mutableBlockPos, newState, true);
                                                RoomActivePlacement.playFoamPlaceEffects(level, random, mutableBlockPos);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
                for(LychsealDoorEntry entry : room.lychsealDoorEntries) {
                    if(entry.lychsealName.isEmpty()) {
                        BlockPos doorPos = entry.startPos();
                        if (RoomActivePlacement.getTimeToReachPos(this.placementOrigin, doorPos, false, doorPos.getX(), doorPos.getZ()) == time) {
                            roomStorage.startSpawningRoom(entry.endRoomId, entry.endPos, false);
                        }
                    }
                }

                return true;
            }
        }

        public boolean place(ServerLevel level) {
            if(!this.storageFilled || this.intermediateStructureStorage == null) {
                return false;
            } else {
                // TODO limit to loaded chunks

                RandomSource random = level.getRandom();

                // place blocks
                this.intermediateStructureStorage.forEachFragileContainer((sectionPos, boxedContainer) -> {
                    BoundingBox box = boxedContainer.getBox();
                    if(box == null) return;

                    int mx = sectionPos.minBlockX();
                    int my = sectionPos.minBlockY();
                    int mz = sectionPos.minBlockZ();

                    BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                    for(int x = box.minX(); x <= box.maxX(); x++) {
                        for(int y = box.minY(); y <= box.maxY(); y++) {
                            for(int z = box.minZ(); z <= box.maxZ(); z++) {
                                BlockState state = boxedContainer.get(x, y, z);

                                if(!state.is(Blocks.STRUCTURE_VOID)) {
                                    mutableBlockPos.set(mx + x, my + y, mz + z);

                                    RoomActivePlacement.setBlock(level, mutableBlockPos, state, false);
                                    RoomActivePlacement.playBlockPlaceEffects(level, random, mutableBlockPos);
                                }
                            }
                        }
                    }
                });

                // place block entities
                this.intermediateStructureStorage.forEachBlockEntity(((blockPos, blockEntity) -> {
                    if(level.getBlockState(blockPos).getBlock().equals(blockEntity.getBlockState().getBlock())) {
                        level.setBlockEntity(blockEntity);
                    }
                }));

                // update blocks
                this.intermediateStructureStorage.forEachContainer(((sectionPos, boxedContainer) -> {
                    BoundingBox box = boxedContainer.getBox();
                    if(box == null) return;

                    int mx = sectionPos.minBlockX();
                    int my = sectionPos.minBlockY();
                    int mz = sectionPos.minBlockZ();

                    BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                    for(int x = box.minX(); x <= box.maxX(); x++) {
                        for(int y = box.minY(); y <= box.maxY(); y++) {
                            for(int z = box.minZ(); z <= box.maxZ(); z++) {
                                BlockState state = boxedContainer.get(x, y, z);

                                if(!state.is(Blocks.STRUCTURE_VOID)) {
                                    mutableBlockPos.set(mx + x, my + y, mz + z);

                                    level.blockUpdated(mutableBlockPos, state.getBlock());
                                    if (state.hasAnalogOutputSignal()) {
                                        level.updateNeighbourForOutputSignal(mutableBlockPos, state.getBlock());
                                    }
                                    RoomActivePlacement.tryUpdateSelf(level, mutableBlockPos, state);
                                }
                            }
                        }
                    }
                }));

                // place entities
                this.intermediateStructureStorage.forEachEntity(level::addFreshEntity);

                return true;
            }
        }

        public void createStructure(PlaceableRoom room, ServerLevel level, BoundingBox stageBB) {
            if(this.intermediateStructureStorage == null) {
                this.intermediateStructureStorage = new IntermediateStructureStorage();
            }
            IntermediateGenLevel igl = new IntermediateGenLevel(this.intermediateStructureStorage, level);
            if(RoomPrePlacement.placeStructure(room.room, level, igl, stageBB, this.shouldForceLoadChunks, room.getRoomId())) {
                this.storageFilled = true;

                BoundingBox storageBox = intermediateStructureStorage.calculateBoundingBox();
                if(storageBox == null || this.placementOrigin == null) {
                    this.maxSpawnTime = 0;
                } else {
                    this.maxSpawnTime = 0;
                    BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
                    for(int i = 0; i < 8; i++) {
                        int x = ((i & 0x1) == 0) ? storageBox.minX() : storageBox.maxX();
                        int y = ((i & 0x2) == 0) ? storageBox.minY() : storageBox.maxY();
                        int z = ((i & 0x4) == 0) ? storageBox.minZ() : storageBox.maxZ();
                        pos.set(x, y, z);

                        int candidateMaxSpawnTime = RoomActivePlacement.getTimeToReachPos(this.placementOrigin, pos, true, RoomActivePlacement.MAX_NOISE_DELAY_TICKS);
                        if(candidateMaxSpawnTime > this.maxSpawnTime) {
                            this.maxSpawnTime = candidateMaxSpawnTime;
                        }
                    }
                }
            }
        }

        public BlockPos getPlacementOrigin() {
            return placementOrigin;
        }

        public int getMaxSpawnTime() {
            return maxSpawnTime;
        }

        public int getSpawnTime() {
            return spawnTime;
        }

        public @Nullable IntermediateStructureStorage getIntermediateStructureStorage() {
            return intermediateStructureStorage;
        }
    }
}
