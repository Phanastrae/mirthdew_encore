package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.DoorNode;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.RoomGraph;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PlaceReadyRoomStorage {

    private final List<PlaceReadyRoom> rooms = new ObjectArrayList<>();
    private int nextRoomId = 0;

    public void addRoom(Room room) {
        this.rooms.add(new PlaceReadyRoom(room, this.nextRoomId));
        this.nextRoomId++;
    }

    public void addRooms(List<Room> list) {
        list.forEach(this::addRoom);
    }

    public void addConnections(RoomGraph graph) {
        Map<DoorNode, PlaceReadyRoom> map = new Object2ObjectOpenHashMap<>();
        for (PlaceReadyRoom room : this.rooms) {
            for (RoomDoor door : room.getRoom().getDoors()) {
                graph.getNode(door)
                        .ifPresent(node -> map.put(node, room));
            }
        }

        map.forEach((doorNode, room) -> doorNode.getEdgesOut().forEach(edge -> {
            DoorNode end = edge.getEnd();
            if (map.containsKey(end)) {
                room.addToPlaceAfter(doorNode.getDoor().getTargetLychseal(), map.get(end));
            }
        }));
    }

    public void enableEntranceSpawning() {
        for(PlaceReadyRoom room : this.rooms) {
            if(room.getRoom().getRoomSource().getRoomType().isEntrance()) {
                room.setCanPlace(true);
                room.setIsEntrance(true);
            }
        }
    }

    public List<PlaceReadyRoom> getRooms() {
        return rooms;
    }

    public Optional<PlaceReadyRoom> getRoom(int id) {
        // TODO optimise
        for(PlaceReadyRoom room : this.rooms) {
            if(room.getRoomId() == id) {
                return Optional.of(room);
            }
        }

        return Optional.empty();
    }
}
