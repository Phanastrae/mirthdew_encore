package phanastrae.mirthdew_encore.dreamtwirl.stage.design;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.StageAreaData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.collision_map.RoomCollisionMap;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.RoomGraph;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.ParentedRoomDoor;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

import java.util.Map;
import java.util.Optional;

public class StageDesignData {
    public static final String KEY_NEXT_ROOM_ID = "next_room_id";
    public static final String KEY_ROOM_LIST = "rooms";
    public static final String KEY_ROOM_GRAPH = "room_graph";

    private long nextRoomId = 0L;
    private final Map<Long, Room> idToRoomMap;
    private int entranceCount = 0;

    private final RoomCollisionMap collisionMap;
    private final RoomGraph roomGraph;

    public StageDesignData(StageAreaData stageAreaData) {
        this.idToRoomMap = new Object2ObjectOpenHashMap<>();
        this.collisionMap = createCollisionMap(stageAreaData);
        this.roomGraph = new RoomGraph();
    }

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        nbt.putLong(KEY_NEXT_ROOM_ID, this.nextRoomId);

        ListTag rooms = new ListTag();
        for(Room room : this.idToRoomMap.values()) {
            rooms.add(room.writeNbt(new CompoundTag(), registries, spsContext));
        }
        nbt.put(KEY_ROOM_LIST, rooms);

        nbt.put(KEY_ROOM_GRAPH, this.roomGraph.writeNbt(new CompoundTag(), registries));

        return nbt;
    }

    public CompoundTag readNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        if(nbt.contains(KEY_NEXT_ROOM_ID, Tag.TAG_LONG)) {
            this.nextRoomId = nbt.getLong(KEY_NEXT_ROOM_ID);
        }

        this.idToRoomMap.clear();
        this.entranceCount = 0;
        this.collisionMap.clear();

        if(nbt.contains(KEY_ROOM_LIST, Tag.TAG_LIST)) {
            ListTag rooms = nbt.getList(KEY_ROOM_LIST, Tag.TAG_COMPOUND);
            for(int i = 0; i < rooms.size(); i++) {
                CompoundTag tag = rooms.getCompound(i);

                Room room = Room.fromNbt(tag, registries, spsContext);
                if(room != null) {
                    this.idToRoomMap.put(room.getRoomId(), room);
                    this.collisionMap.addRoom(room);
                    if(room.getRoomType().isEntrance()) {
                        this.entranceCount++;
                    }
                }
            }
        }

        if(nbt.contains(KEY_ROOM_GRAPH, Tag.TAG_COMPOUND)) {
            this.roomGraph.readNbt(nbt.getCompound(KEY_ROOM_GRAPH), registries);
        }

        return nbt;
    }

    public static RoomCollisionMap createCollisionMap(StageAreaData stageAreaData) {
        ChunkPos minCornerPos = stageAreaData.getRegionPos().getChunkPos(1, 1);
        return new RoomCollisionMap(SectionPos.of(minCornerPos.x, stageAreaData.getMinBuildHeight() >> 4, minCornerPos.z), 30, stageAreaData.getHeight() >> 4, 30);
    }

    public void addRoom(Room room, RandomSource random) {
        this.idToRoomMap.put(room.getRoomId(), room);
        this.collisionMap.addRoom(room);
        this.roomGraph.addRoom(this, room, random);

        if(room.getRoomType().isEntrance()) {
            this.entranceCount++;
        }
    }

    public void removeRoom(Room room) {
        this.idToRoomMap.remove(room);
        this.collisionMap.removeRoom(room);
        this.roomGraph.removeRoom(room);

        if(room.getRoomType().isEntrance()) {
            this.entranceCount--;
        }
    }

    public Optional<Room> getRandomRoom(RandomSource random) {
        if(this.idToRoomMap.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(this.idToRoomMap.get(random.nextInt(this.idToRoomMap.size())));
        }
    }

    public long getNextRoomId() {
        long l = this.nextRoomId;
        this.nextRoomId++;
        return l;
    }

    public Map<Long, Room> getIdToRoomMap() {
        return idToRoomMap;
    }

    public RoomCollisionMap getCollisionMap() {
        return collisionMap;
    }

    public RoomGraph getRoomGraph() {
        return roomGraph;
    }

    public int getEntranceCount() {
        return entranceCount;
    }

    public @Nullable Room getRoom(long roomId) {
        return this.idToRoomMap.getOrDefault(roomId, null);
    }

    public @Nullable RoomDoor getDoor(long roomId, int doorId) {
        Room room = this.getRoom(roomId);
        if(room != null) {
            return room.getDoor(doorId);
        } else {
            return null;
        }
    }

    public @Nullable RoomDoor getDoor(RoomDoor.RoomDoorId roomDoorId) {
        return getDoor(roomDoorId.roomId(), roomDoorId.doorId());
    }

    public @Nullable ParentedRoomDoor getParentedRoomDoor(RoomDoor.RoomDoorId doorId) {
        Room room = this.getRoom(doorId.roomId());
        if(room != null) {
            RoomDoor door = room.getDoor(doorId.doorId());
            if(door != null) {
                return new ParentedRoomDoor(door, room);
            }
        }
        return null;
    }
}
