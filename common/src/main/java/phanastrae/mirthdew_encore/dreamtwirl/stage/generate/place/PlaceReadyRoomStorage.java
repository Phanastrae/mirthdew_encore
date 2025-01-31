package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.DoorNode;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.RoomGraph;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

import java.util.List;
import java.util.Map;

public class PlaceReadyRoomStorage {

    private final List<PlaceReadyRoom> rooms = new ObjectArrayList<>();

    public void addRoom(Room room) {
        this.rooms.add(new PlaceReadyRoom(room));
    }

    public void removeRoom(Room room) {
        this.rooms.remove(room);
    }

    public void addRooms(List<Room> list) {
        list.forEach(this::addRoom);
    }

    public void addConnections(RoomGraph graph) {
        Map<DoorNode, PlaceReadyRoom> map = new Object2ObjectOpenHashMap<>();
        for(PlaceReadyRoom room : this.rooms) {
            for(RoomDoor door : room.getPrefab().getDoors()) {
                graph.getNode(door)
                        .ifPresent(node -> map.put(node, room));
            }
        }

        map.keySet()
                .forEach(doorNode -> doorNode.getEdgesOut()
                        .forEach(edge -> {
                            DoorNode end = edge.getEnd();
                            if(map.containsKey(end)) {
                                map.get(doorNode).addToPlaceAfter(map.get(end));
                            }
                        })
                );
    }

    public void enableEntranceSpawning() {
        for(PlaceReadyRoom room : this.rooms) {
            if(room.getPrefab().getRoomSource().getRoomType().isEntrance()) {
                room.setCanPlace(true);
                room.setIsEntrance(true);
            }
        }
    }

    public List<PlaceReadyRoom> getRooms() {
        return rooms;
    }
}
