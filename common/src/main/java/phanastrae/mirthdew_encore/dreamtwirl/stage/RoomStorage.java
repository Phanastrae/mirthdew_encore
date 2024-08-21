package phanastrae.mirthdew_encore.dreamtwirl.stage;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.List;

public class RoomStorage {

    private final DreamtwirlStage stage;
    private final RegionPos regionPos;

    private final List<DreamtwirlRoom> rooms = new ObjectArrayList<>();

    public RoomStorage(DreamtwirlStage stage) {
        this.stage = stage;
        this.regionPos = stage.getRegionPos();
    }

    public RegionPos getRegionPos() {
        return this.regionPos;
    }

    public void addRoom(DreamtwirlRoom room) {
        this.rooms.add(room);
    }

    public void removeRoom(DreamtwirlRoom room) {
        this.rooms.remove(room);
    }

    public void addRooms(DreamtwirlRoomGroup roomGroup) {
        roomGroup.getRooms().forEach(this::addRoom);
    }

    public void addRooms(List<DreamtwirlRoomGroup> roomGroups) {
        roomGroups.forEach(this::addRooms);
    }

    public List<DreamtwirlRoom> getRooms() {
        return rooms;
    }
}
