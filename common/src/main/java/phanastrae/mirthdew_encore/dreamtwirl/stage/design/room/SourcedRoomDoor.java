package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room;

public class SourcedRoomDoor {

    private final RoomDoor roomDoor;
    private final SourcedRoom sourcedRoom;

    public SourcedRoomDoor(RoomDoor roomDoor, SourcedRoom sourcedRoom) {
        this.roomDoor = roomDoor;
        this.sourcedRoom = sourcedRoom;
    }

    public RoomDoor getDoor() {
        return roomDoor;
    }

    public SourcedRoom getSourcedRoom() {
        return sourcedRoom;
    }
}
