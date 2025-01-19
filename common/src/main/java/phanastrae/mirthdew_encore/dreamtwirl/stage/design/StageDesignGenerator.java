package phanastrae.mirthdew_encore.dreamtwirl.stage.design;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.StageAreaData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.collision_map.CollisionMapEntry;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.DoorNode;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSource;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSourceCollection;

import java.util.List;
import java.util.Optional;

public class StageDesignGenerator {
    private final ServerLevel serverLevel;

    private final long stageSeed;
    private final RandomSource random;

    private final StageAreaData stageAreaData;
    private final RoomSourceCollection roomSourceCollection;
    private final StageDesignData designData;

    public StageDesignGenerator(DreamtwirlStage stage, ServerLevel serverLevel, long dreamtwirlSeed, RoomSourceCollection roomSourceCollection) {
        this.serverLevel = serverLevel;

        this.stageSeed = dreamtwirlSeed;
        this.random = RandomSource.create(dreamtwirlSeed);

        this.stageAreaData = stage.getStageAreaData();
        this.roomSourceCollection = roomSourceCollection;
        this.designData = new StageDesignData(this.stageAreaData);
    }

    public void generate() {
        // get entrance room source
        Optional<RoomSource> entrance = this.roomSourceCollection.getEntrance(this.random);
        if(entrance.isEmpty()) {
            MirthdewEncore.LOGGER.info("Failed to generate dreamtwirl entrance!");
            return;
        }

        // place entrance
        BlockPos entranceBlockPos = this.stageAreaData.offsetCenterBlockPos(-128, -this.serverLevel.getHeight() / 4, 0);
        if(!this.tryAddRoomCenteredAt(entrance.get(), entranceBlockPos, true)) {
            MirthdewEncore.LOGGER.info("Something went wrong trying to place the dreamtwirl entrance!!");
            return;
        }

        // place lots of rooms
        for(int i = 0; i < 25000; i++) {
            this.sprawl(this.random);
        }

    }

    public StageDesignData getDesignData() {
        return designData;
    }

    public Optional<Room> tryGetRoomFromSource(RoomSource roomSource) {
        return roomSource.tryGetRoom(this.stageSeed, this.stageAreaData.getStageChunkCenter(), this.random, this.serverLevel);
    }

    public Optional<Room> tryGetRandomRoom() {
        Optional<RoomSource> roomSourceOptional = this.roomSourceCollection.getRandomPrefabSet(this.random);
        if(roomSourceOptional.isPresent()) {
            RoomSource roomSource = roomSourceOptional.get();
            return this.tryGetRoomFromSource(roomSource);
        }
        return Optional.empty();
    }

    public boolean tryAddRoomCenteredAt(RoomSource roomSource, BlockPos roomCenter, boolean allowPositionAdjustment) {
        Optional<Room> roomOptional = this.tryGetRoomFromSource(roomSource);
        if(roomOptional.isPresent()) {
            Room room = roomOptional.get();

            room.centerAt(roomCenter, this.designData);
            if(allowPositionAdjustment) {
                if(this.tryAdjustPositionUntilValid(room, null)) {
                    this.designData.addRoom(room);
                    return true;
                }
            } else {
                if(this.isLocationValid(room)) {
                    this.designData.addRoom(room);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean tryAddRoomMatchingDoor(Room room, RoomDoor roomDoor, RoomDoor targetDoor) {
        room.translateToMatchDoor(roomDoor, targetDoor, this.designData);
        if(this.isLocationValid(room)) {
            this.designData.addRoom(room);
            return true;
        } else {
            return false;
        }
    }

    public boolean tryAddRoomMatchingDoorAndConnect(Room room, RoomDoor roomDoor, RoomDoor targetDoor) {
        if(roomDoor.isConnected() || targetDoor.isConnected()) {
            return false;
        }

        if(tryAddRoomMatchingDoor(room, roomDoor, targetDoor)) {
            roomDoor.setConnected(true);
            targetDoor.setConnected(true);
            this.designData.getRoomGraph().addNodesWithEdge(roomDoor, targetDoor);
            this.designData.getRoomGraph().addNodesWithEdge(targetDoor, roomDoor);
            return true;
        } else {
            return false;
        }
    }

    public void sprawl(RandomSource random) {
        // get a random empty door
        Optional<DoorNode> doorNodeOptional = this.designData.getRoomGraph().getRandomUnfilledExitDoorNode(random);
        if(doorNodeOptional.isEmpty()) return;
        DoorNode doorNode = doorNodeOptional.get();
        RoomDoor door = doorNode.getDoor();

        // create a new room
        Optional<Room> newRoomOptional = this.tryGetRandomRoom();
        if(newRoomOptional.isEmpty()) return;
        Room newRoom = newRoomOptional.get();

        // get the corresponding door for the new room
        Optional<RoomDoor> newDoorOptional = newRoom.getRandomEmptyDoorMatching(random, door.getOrientation());
        if(newDoorOptional.isEmpty()) {
            // if failed to add, return to roomsource
            newRoom.getRoomSource().acceptDiscardedRoom(newRoom);
            return;
        }
        RoomDoor newDoor = newDoorOptional.get();

        // try to connect and add room
        if(tryAddRoomMatchingDoorAndConnect(newRoom, newDoor, door)) {
            //wiggle the output around a little so they aren't just touching
            //Direction direction = door.getOrientation().front();
            //Vec3i vector = direction.getNormal();
            //newRoom.translate(vector.multiply(2).offset(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1));
        } else {
            // if failed to add, return to source
            newRoom.getRoomSource().acceptDiscardedRoom(newRoom);
        }
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
