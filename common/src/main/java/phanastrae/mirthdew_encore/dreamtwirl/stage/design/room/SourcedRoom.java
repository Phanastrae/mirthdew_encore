package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room;

import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.util.RandomSource;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSource;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class SourcedRoom {

    private final Room room;
    private final RoomSource roomSource;

    public SourcedRoom(Room room, RoomSource roomSource) {
        this.room = room;
        this.roomSource = roomSource;
    }

    public Optional<ParentedRoomDoor> getRandomEmptyDoor(RandomSource random) {
        return getRandomDoorMatching(random, door -> !door.isConnected());
    }

    public Optional<ParentedRoomDoor> getRandomEmptyExit(RandomSource random) {
        return getRandomDoorMatching(random, door -> !door.isConnected() && door.getDoorType().isExit);
    }

    public Optional<RoomDoor> getRandomEmptyEntranceMatching(RandomSource random, FrontAndTop orientation) {
        // TODO account for the multiple up and down orientations?
        Direction targetOrientation = orientation.front().getOpposite();

        return getRandomDoorMatching(random, door -> !door.isConnected() && door.getDoorType().isEntrance && door.getOrientation().front().equals(targetOrientation)).map(ParentedRoomDoor::getDoor);
    }

    public Optional<ParentedRoomDoor> getRandomDoorMatching(RandomSource random, Predicate<RoomDoor> predicate) {
        List<RoomDoor> valid = this.getRoom().getDoors().stream().filter(predicate).toList();
        return getRandomDoorFrom(valid, random);
    }

    public Optional<ParentedRoomDoor> getRandomDoorFrom(List<RoomDoor> doors, RandomSource random) {
        if(doors.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new ParentedRoomDoor(doors.get(random.nextInt(doors.size())), this.getRoom()));
        }
    }

    public Room getRoom() {
        return room;
    }

    public RoomSource getRoomSource() {
        return roomSource;
    }
}
