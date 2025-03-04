package phanastrae.mirthdew_encore.dreamtwirl.stage.design;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.StageAreaData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.collision_map.CollisionMapEntry;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.DoorNode;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.SourcedRoom;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.SourcedRoomDoor;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSource;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSourceCollection;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;

import java.util.List;
import java.util.Optional;

public class StageDesignGenerator {
    private final ServerLevel serverLevel;

    private final long stageSeed;
    private final RandomSource random;

    private final StageAreaData stageAreaData;
    private final RoomSourceCollection roomSourceCollection;
    private final StageDesignData designData;

    boolean entrancePlaced = false;
    int remainingEntrancePlaceAttempts = 50;
    int remainingRoomPlaceAttempts = 25000;

    public StageDesignGenerator(DreamtwirlStage stage, ServerLevel serverLevel, long dreamtwirlSeed, RoomSourceCollection roomSourceCollection) {
        this.serverLevel = serverLevel;

        this.stageSeed = dreamtwirlSeed;
        this.random = RandomSource.create(dreamtwirlSeed);

        this.stageAreaData = stage.getStageAreaData();
        this.roomSourceCollection = roomSourceCollection;
        this.designData = new StageDesignData(this.stageAreaData);
    }

    public boolean tick() {
        for(int i = 0; i < 400; i++) {
            boolean done = this.tickSingle();

            if(done) {
                return true;
            }
        }

        return false;
    }

    public boolean tickSingle() {
        if(!this.entrancePlaced) {
            this.attemptEntrancePlacement();
            return false;
        }
        if(this.remainingRoomPlaceAttempts > 0) {
            this.attemptRoomPlacement();
            return false;
        }

        // return true if done
        return true;
    }

    public void attemptEntrancePlacement() {
        this.remainingEntrancePlaceAttempts--;

        Optional<RoomSource> entrance = this.roomSourceCollection.getEntrance(this.random);
        if(entrance.isEmpty()) {
            // failed to find entrance room source
            return;
        }
        BlockPos entranceBlockPos = this.stageAreaData.offsetCenterBlockPos(-128, -this.serverLevel.getHeight() / 4, 0);
        if(!this.tryAddRoomCenteredAt(entrance.get(), entranceBlockPos, true)) {
            // failed to place entrance room
            return;
        }

        this.entrancePlaced = true;
    }

    public void attemptRoomPlacement() {
        this.remainingRoomPlaceAttempts -= 5;

        ///this.sprawl();
        this.addBranch();
    }

    public StageDesignData getDesignData() {
        return designData;
    }

    public Optional<SourcedRoom> tryGetRoomFromSource(RoomSource roomSource) {
        return roomSource.tryGetRoom(this.stageSeed, this.stageAreaData.getStageChunkCenter(), this.random, this.serverLevel);
    }

    public Optional<SourcedRoom> tryGetRandomRoom() {
        Optional<RoomSource> roomSourceOptional = this.roomSourceCollection.getRandomRoomSource(this.random);
        if(roomSourceOptional.isPresent()) {
            RoomSource roomSource = roomSourceOptional.get();
            return this.tryGetRoomFromSource(roomSource);
        }
        return Optional.empty();
    }

    public Optional<SourcedRoom> tryGetRandomRoomOfType(RoomType.Category category) {
        Optional<RoomSource> roomSourceOptional = this.roomSourceCollection.getRandomMatching(this.random, source -> source.getRoomType().category().equals(category));
        if(roomSourceOptional.isPresent()) {
            RoomSource roomSource = roomSourceOptional.get();
            return this.tryGetRoomFromSource(roomSource);
        }
        return Optional.empty();
    }

    public boolean tryAddRoomCenteredAt(RoomSource roomSource, BlockPos roomCenter, boolean allowPositionAdjustment) {
        Optional<SourcedRoom> roomOptional = this.tryGetRoomFromSource(roomSource);
        if(roomOptional.isPresent()) {
            SourcedRoom sourcedRoom = roomOptional.get();
            Room room = sourcedRoom.getRoom();

            room.centerAt(roomCenter, this.designData);
            if(allowPositionAdjustment) {
                if(this.tryAdjustPositionUntilValid(room, null)) {
                    this.designData.addRoom(sourcedRoom);
                    return true;
                }
            } else {
                if(this.isLocationValid(room)) {
                    this.designData.addRoom(sourcedRoom);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean tryAddRoomMatchingDoor(SourcedRoom room, RoomDoor roomDoor, RoomDoor targetDoor) {
        room.getRoom().translateToMatchDoor(roomDoor, targetDoor, this.designData);
        if(this.isLocationValid(room.getRoom())) {
            this.designData.addRoom(room);
            return true;
        } else {
            return false;
        }
    }

    public boolean tryAddRoomMatchingDoorAndConnect(SourcedRoom room, SourcedRoomDoor roomDoor, SourcedRoomDoor targetDoor) {
        if(roomDoor.getDoor().isConnected() || targetDoor.getDoor().isConnected()) {
            return false;
        }

        if(tryAddRoomMatchingDoor(room, roomDoor.getDoor(), targetDoor.getDoor())) {
            roomDoor.getDoor().setConnected(true);
            targetDoor.getDoor().setConnected(true);
            this.designData.getRoomGraph().addNodesWithEdge(roomDoor, targetDoor);
            this.designData.getRoomGraph().addNodesWithEdge(targetDoor, roomDoor);
            return true;
        } else {
            return false;
        }
    }

    public boolean addBranch() {
        // get random starting door
        Optional<DoorNode> doorNodeOptional = this.designData.getRoomGraph().getRandomUnfilledExitDoorNode(this.random);
        if(doorNodeOptional.isEmpty()) return false;
        DoorNode doorNode = doorNodeOptional.get();

        Branch branch = new Branch(doorNode);
        if(branch.tryAddRoom(2, RoomType.Category.GATE, this)) {
            for(int i = 0; i < 5; i++) {
                branch.tryAddRoom(4, RoomType.Category.PATH, this);
            }
            if(branch.getRoomCount() >= 2) {
                if(branch.tryAddRoom(10, RoomType.Category.ROOM, this)) {
                    return true;
                }
            }
        }

        branch.destroyAllRooms(this);
        return false;
    }

    public static class Branch {

        private final DoorNode start;
        private List<SourcedRoom> rooms = new ObjectArrayList<>();

        public Branch(DoorNode start) {
            this.start = start;
        }

        public boolean tryAddRoom(int attempts, RoomType.Category category, StageDesignGenerator sdg) {
            for(int i = 0; i < attempts; i++) {
                SourcedRoomDoor rootDoor;
                if(this.rooms.isEmpty()) {
                    rootDoor = this.start.getSourcedDoor();
                } else {
                    SourcedRoom last = this.rooms.getLast();
                    Optional<SourcedRoomDoor> doorOptional = last.getRandomEmptyExit(sdg.random);
                    if(doorOptional.isEmpty()) continue;
                    rootDoor = doorOptional.get();
                }

                Optional<SourcedRoom> newRoomOptional = sdg.tryGetRandomRoomOfType(category);
                if (newRoomOptional.isEmpty()) continue;
                SourcedRoom newRoom = newRoomOptional.get();

                if (sdg.tryAttachRoomToDoor(rootDoor, newRoom)) {
                    this.rooms.add(newRoom);
                    return true;
                } else {
                    returnRoomToSource(newRoom);
                }
            }

            return false;
        }

        public void destroyAllRooms(StageDesignGenerator sdg) {
            this.rooms.forEach(room -> sdg.getDesignData().removeRoom(room.getRoom()));
            this.rooms.forEach(StageDesignGenerator::returnRoomToSource);
            this.rooms.clear();
        }

        public int getRoomCount() {
            return this.rooms.size();
        }
    }

    public boolean sprawl() {
        // get a random empty door
        Optional<DoorNode> doorNodeOptional = this.designData.getRoomGraph().getRandomUnfilledExitDoorNode(this.random);
        if(doorNodeOptional.isEmpty()) return false;
        DoorNode doorNode = doorNodeOptional.get();
        SourcedRoomDoor door = doorNode.getSourcedDoor();

        int distanceFromEntrance = doorNode.getDistanceInfo().getDistanceFromEntrance();
        int d = distanceFromEntrance % 10;
        RoomType.Category target = d == 9 ? RoomType.Category.ROOM : (d == 1 ? RoomType.Category.GATE : RoomType.Category.PATH);

        // create a new room
        Optional<SourcedRoom> newRoomOptional = this.tryGetRandomRoomOfType(target);
        if(newRoomOptional.isEmpty()) return false;
        SourcedRoom newRoom = newRoomOptional.get();

        if(tryAttachRoomToDoor(door, newRoom)) {
            return true;
        } else {
            returnRoomToSource(newRoom);
            return false;
        }
    }

    public boolean tryAttachRoomToDoor(SourcedRoomDoor door, SourcedRoom newRoom) {
        // get the corresponding door for the new room
        Optional<SourcedRoomDoor> newDoorOptional = newRoom.getRandomEmptyEntranceMatching(this.random, door.getDoor().getOrientation());
        if(newDoorOptional.isEmpty()) {
            // if failed to add, return to roomsource
            return false;
        }
        SourcedRoomDoor newDoor = newDoorOptional.get();

        // try to connect and add room
        if(tryAddRoomMatchingDoorAndConnect(newRoom, newDoor, door)) {
            //wiggle the output around a little so they aren't just touching
            //Direction direction = door.getOrientation().front();
            //Vec3i vector = direction.getNormal();
            //newRoom.translate(vector.multiply(2).offset(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1));
            return true;
        } else {
            // if failed to add, return to source
            return false;
        }
    }

    public static void returnRoomToSource(SourcedRoom room) {
        room.getRoomSource().acceptDiscardedRoom(room.getRoom());
    }

    public boolean isLocationValid(Room room) {
        if(!this.stageAreaData.isBoundingBoxInBounds(room.getBoundingBox())) {
            return false;
        } else if(this.designData.getCollisionMap().doesRoomIntersectOtherRooms(room)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean tryAdjustPositionUntilValid(Room room, @Nullable Direction preferredOffsetDirection) {
        Vec3i vector = preferredOffsetDirection == null ? null : preferredOffsetDirection.getNormal();
        for(int n = 0; n < 8; n++) {
            List<CollisionMapEntry> collisionRooms = this.designData.getCollisionMap().getIntersections(room.getBoundingBox(), room);

            boolean hadCollision = false;
            for(CollisionMapEntry collisionRoom : collisionRooms) {
                BoundingBox collisionBox = collisionRoom.getBoundingBox();

                for(int k = 0; k < 10; k++) {
                    if (room.getBoundingBox().intersects(collisionBox)) {
                        hadCollision = true;

                        int d = k * k;
                        BlockPos moveBy = new BlockPos(
                                this.random.nextInt(2 * d + 1) - d,
                                this.random.nextInt(2 * k + 1) - k,
                                this.random.nextInt(2 * d + 1) - d
                        );
                        if(vector != null) {
                            moveBy = moveBy.offset(vector.multiply(d));
                        }

                        room.translate(moveBy, this.designData);
                    } else {
                        break;
                    }
                }
            }
            if(!hadCollision) {
                return true;
            }
        }
        return isLocationValid(room);
    }
}
