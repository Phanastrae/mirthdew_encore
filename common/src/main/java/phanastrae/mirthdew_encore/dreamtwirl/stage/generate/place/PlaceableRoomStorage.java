package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.DoorNode;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.RoomGraph;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlaceableRoomStorage {
    public static final String KEY_NEXT_ROOM_ID = "next_room_id";
    public static final String KEY_ROOM_LIST = "room_list";

    private final List<PlaceableRoom> rooms = new ObjectArrayList<>();
    private int nextRoomId = 0;

    public PlaceableRoomStorage() {
    }

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        nbt.putInt(KEY_NEXT_ROOM_ID, this.nextRoomId);

        ListTag list = new ListTag();
        int index = 0;
        for(PlaceableRoom room : this.rooms) {
            CompoundTag roomTag = new CompoundTag();
            room.writeNbt(roomTag, registries, spsContext);
            list.add(index, roomTag);

            index++;
        }
        nbt.put(KEY_ROOM_LIST, list);

        return nbt;
    }

    public CompoundTag readNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        if(nbt.contains(KEY_NEXT_ROOM_ID, Tag.TAG_INT)) {
            this.nextRoomId = nbt.getInt(KEY_NEXT_ROOM_ID);
        }

        this.rooms.clear();
        if(nbt.contains(KEY_ROOM_LIST, Tag.TAG_LIST)) {
            ListTag listTag = nbt.getList(KEY_ROOM_LIST, Tag.TAG_COMPOUND);

            for(int i = 0; i < listTag.size(); i++) {
                CompoundTag roomTag = listTag.getCompound(i);
                PlaceableRoom room = PlaceableRoom.fromNbt(roomTag, registries, spsContext);

                if(room != null) {
                    this.rooms.add(room);
                } else {
                    // TODO handle case?
                }
            }
        }

        return nbt;
    }

    public void addRoom(Room room) {
        this.rooms.add(new PlaceableRoom(room, this.nextRoomId));
        this.nextRoomId++;
    }

    public void addRooms(List<Room> list) {
        list.forEach(this::addRoom);
    }

    public void addConnections(RoomGraph graph) {
        Map<DoorNode, PlaceableRoom> map = new Object2ObjectOpenHashMap<>();
        for (PlaceableRoom room : this.rooms) {
            for (RoomDoor door : room.getRoom().getDoors()) {
                graph.getNode(door)
                        .ifPresent(node -> map.put(node, room));
            }
        }

        map.forEach((doorNode, room) -> doorNode.getEdgesOut().forEach(edge -> {
            DoorNode end = edge.getEnd();
            if (map.containsKey(end)) {
                PlaceableRoom targetRoom = map.get(end);
                RoomDoor startDoor = doorNode.getSourcedDoor().getDoor();
                RoomDoor endDoor = end.getSourcedDoor().getDoor();
                room.addLychsealDoorEntry(startDoor, endDoor, targetRoom);
            }
        }));
    }

    public void beginEntrancePlacement() {
        for(PlaceableRoom room : this.rooms) {
            if(room.getRoom().getRoomType().isEntrance()) {
                room.beginPlacementFromCenter(true);
            }
        }
    }

    public void startSpawningRoom(int roomId, BlockPos spawnStart, boolean shouldForceLoadChunks) {
        Optional<PlaceableRoom> roomOptional = this.getRoom(roomId);
        if(roomOptional.isEmpty()) return;
        PlaceableRoom room = roomOptional.get();

        room.beginPlacement(spawnStart, shouldForceLoadChunks);
    }

    public List<PlaceableRoom> getRooms() {
        return rooms;
    }

    public Optional<PlaceableRoom> getRoom(int id) {
        // TODO optimise
        for(PlaceableRoom room : this.rooms) {
            if(room.getRoomId() == id) {
                return Optional.of(room);
            }
        }

        return Optional.empty();
    }
}
