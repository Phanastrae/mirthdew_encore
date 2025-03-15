package phanastrae.mirthdew_encore.dreamtwirl.stage.design;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.StageAreaData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.collision_map.CollisionMapEntry;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.ParentedRoomDoor;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.SourcedRoom;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSource;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSourceCollection;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomCategory;

import java.util.List;
import java.util.Optional;

public class StageDesignGenerator {
    public static final String KEY_STAGE_SEED = "stage_seed";
    public static final String KEY_ROOM_SOURCE_COLLECTION = "room_sources";
    public static final String KEY_STEPS = "steps";
    public static final String KEY_DESIGN_DATA = "design_data";

    public static final int TOTAL_STEPS = 5000;
    public static final int STEPS_PER_TICK = 50;

    private final ServerLevel serverLevel;
    private final StageAreaData stageAreaData;
    private final long stageSeed;

    private final RandomSource random;
    private final RoomSourceCollection roomSourceCollection;
    private final StageDesignData designData;
    int steps = 0;

    public StageDesignGenerator(StageAreaData stageAreaData, ServerLevel serverLevel, long dreamtwirlSeed, RoomSourceCollection roomSourceCollection) {
        this.stageAreaData = stageAreaData;
        this.serverLevel = serverLevel;
        this.stageSeed = dreamtwirlSeed;

        this.random = RandomSource.create(dreamtwirlSeed);
        this.roomSourceCollection = roomSourceCollection;
        this.designData = new StageDesignData(this.stageAreaData);
    }

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        StructurePieceSerializationContext spsContext = StructurePieceSerializationContext.fromLevel(this.serverLevel);

        nbt.putLong(KEY_STAGE_SEED, this.stageSeed);
        nbt.put(KEY_ROOM_SOURCE_COLLECTION, this.roomSourceCollection.writeNbt(new CompoundTag(), registries, spsContext));

        nbt.putInt(KEY_STEPS, this.steps);

        nbt.put(KEY_DESIGN_DATA, this.designData.writeNbt(new CompoundTag(), registries, spsContext));
        return nbt;
    }

    public static StageDesignGenerator fromNbt(CompoundTag nbt, HolderLookup.Provider registries, StageAreaData stageAreaData, ServerLevel serverLevel) {
        StructurePieceSerializationContext spsContext = StructurePieceSerializationContext.fromLevel(serverLevel);

        long seed;
        if(nbt.contains(KEY_STAGE_SEED, Tag.TAG_LONG)) {
            seed = nbt.getLong(KEY_STAGE_SEED);
        } else {
            seed = serverLevel.random.nextLong();
        }

        RoomSourceCollection sources = new RoomSourceCollection(new ObjectArrayList<>());
        if(nbt.contains(KEY_ROOM_SOURCE_COLLECTION, Tag.TAG_COMPOUND)) {
            sources.readNbt(nbt.getCompound(KEY_ROOM_SOURCE_COLLECTION), registries, spsContext);
        }

        StageDesignGenerator SDG = new StageDesignGenerator(stageAreaData, serverLevel, seed, sources);

        if(nbt.contains(KEY_STEPS, Tag.TAG_INT)) {
            SDG.steps = nbt.getInt(KEY_STEPS);
        }
        if(nbt.contains(KEY_DESIGN_DATA, Tag.TAG_COMPOUND)) {
            SDG.designData.readNbt(nbt.getCompound(KEY_DESIGN_DATA), registries, spsContext);
        }

        return SDG;
    }

    public boolean tick() {
        for(int i = 0; i < STEPS_PER_TICK; i++) {
            this.steps++;
            // setup random every tick to allow RNG to persist through world reloads
            this.setupRandom();

            boolean done = this.tickSingle();
            if(done) {
                return true;
            }
        }

        return false;
    }

    public void setupRandom() {
        this.random.setSeed(this.stageSeed + this.steps * 312871L);
    }

    public boolean tickSingle() {
        if(this.designData.getEntranceCount() == 0) {
            this.attemptEntrancePlacement();
            return false;
        }
        if(this.steps <= TOTAL_STEPS) {
            //this.sprawl();
            this.addBranch();
            return false;
        }

        // return true if done
        return true;
    }

    public boolean attemptEntrancePlacement() {
        Optional<RoomSource> entrance = this.roomSourceCollection.getEntrance(this.random);
        if(entrance.isEmpty()) {
            // failed to find entrance room source
            return false;
        }
        BlockPos entranceBlockPos = this.stageAreaData.offsetCenterBlockPos(-128, -this.serverLevel.getHeight() / 4, 0);
        if(this.tryAddRoomCenteredAt(entrance.get(), entranceBlockPos, true)) {
            return true;
        } else {
            // failed to place entrance room
            return false;
        }
    }

    public StageDesignData getDesignData() {
        return designData;
    }

    public Optional<SourcedRoom> tryGetRoomFromSource(RoomSource roomSource) {
        return roomSource.tryGetRoom(this.stageSeed, this.stageAreaData.getStageChunkCenter(), this.random, this.serverLevel, this.designData);
    }

    public Optional<SourcedRoom> tryGetRandomRoomOfType(RoomCategory category) {
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
                    this.designData.addRoom(sourcedRoom.getRoom(), this.random);
                    return true;
                }
            } else {
                if(this.isLocationValid(room)) {
                    this.designData.addRoom(sourcedRoom.getRoom(), this.random);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean tryAddRoomMatchingDoor(Room room, RoomDoor roomDoor, RoomDoor targetDoor) {
        room.translateToMatchDoor(roomDoor, targetDoor, this.designData);
        if(this.isLocationValid(room)) {
            this.designData.addRoom(room, this.random);
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
            this.designData.getRoomGraph().addNodesWithEdge(roomDoor.getRoomDoorId(), targetDoor.getRoomDoorId(), this.random);
            this.designData.getRoomGraph().addNodesWithEdge(targetDoor.getRoomDoorId(), roomDoor.getRoomDoorId(), this.random);
            return true;
        } else {
            return false;
        }
    }

    public boolean addBranch() {
        // get random starting door
        Optional<RoomDoor.RoomDoorId> doorNodeOptional = this.designData.getRoomGraph().getRandomUnfilledExitDoorNode(this.designData, this.random);
        if(doorNodeOptional.isEmpty()) return false;
        RoomDoor.RoomDoorId doorNode = doorNodeOptional.get();

        Room room = this.designData.getRoom(doorNode.roomId());
        RoomDoor door = this.designData.getDoor(doorNode);
        if(door == null) return false;

        Branch branch = new Branch(new ParentedRoomDoor(door, room));
        if(branch.tryAddRoom(2, RoomCategory.GATE, this)) {
            for(int i = 0; i < 5; i++) {
                branch.tryAddRoom(4, RoomCategory.PATH, this);
            }
            if(branch.getRoomCount() >= 2) {
                if(branch.tryAddRoom(10, RoomCategory.LARGE, this)) {
                    return true;
                }
            }
        }

        branch.destroyAllRooms(this);
        return false;
    }

    public static class Branch {

        private final ParentedRoomDoor start;
        private List<SourcedRoom> rooms = new ObjectArrayList<>();

        public Branch(ParentedRoomDoor start) {
            this.start = start;
        }

        public boolean tryAddRoom(int attempts, RoomCategory category, StageDesignGenerator sdg) {
            for(int i = 0; i < attempts; i++) {
                ParentedRoomDoor rootDoor;
                if(this.rooms.isEmpty()) {
                    rootDoor = this.start;
                } else {
                    SourcedRoom last = this.rooms.getLast();
                    Optional<ParentedRoomDoor> doorOptional = last.getRandomEmptyExit(sdg.random);
                    if(doorOptional.isEmpty()) continue;
                    rootDoor = doorOptional.get();
                }

                Optional<SourcedRoom> newRoomOptional = sdg.tryGetRandomRoomOfType(category);
                if (newRoomOptional.isEmpty()) continue;
                SourcedRoom newRoom = newRoomOptional.get();

                if (sdg.tryAttachRoomToDoor(rootDoor.getDoor(), newRoom)) {
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
        Optional<RoomDoor.RoomDoorId> doorNodeOptional = this.designData.getRoomGraph().getRandomUnfilledExitDoorNode(this.designData, this.random);
        if(doorNodeOptional.isEmpty()) return false;
        RoomDoor.RoomDoorId doorNode = doorNodeOptional.get();

        RoomDoor door = this.designData.getDoor(doorNode);
        if(door == null) return false;

        //int distanceFromEntrance = doorNode.getDistanceInfo().getDistanceFromEntrance();
        int distanceFromEntrance = 0; // TODO tidy whatever is going on here
        int d = distanceFromEntrance % 10;
        RoomCategory target = d == 9 ? RoomCategory.LARGE : (d == 1 ? RoomCategory.GATE : RoomCategory.PATH);

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

    public boolean tryAttachRoomToDoor(RoomDoor door, SourcedRoom newRoom) {
        // get the corresponding door for the new room
        Optional<RoomDoor> newDoorOptional = newRoom.getRandomEmptyEntranceMatching(this.random, door.getOrientation());
        if(newDoorOptional.isEmpty()) {
            // if failed to add, return to roomsource
            return false;
        }
        RoomDoor newDoor = newDoorOptional.get();

        // try to connect and add room
        if(tryAddRoomMatchingDoorAndConnect(newRoom.getRoom(), newDoor, door)) {
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
