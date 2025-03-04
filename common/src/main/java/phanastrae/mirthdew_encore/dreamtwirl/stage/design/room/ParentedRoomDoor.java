package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room;

public class ParentedRoomDoor {

    private final RoomDoor roomDoor;
    private final Room room;

    public ParentedRoomDoor(RoomDoor roomDoor, Room room) {
        this.roomDoor = roomDoor;
        this.room = room;
    }

    public RoomDoor getDoor() {
        return roomDoor;
    }

    public Room getRoom() {
        return room;
    }
}
