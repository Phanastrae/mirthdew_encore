package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSource;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Room {
    private final RoomSource prefabSet;
    private final PiecesContainer piecesContainer;
    private final RoomObjects roomObjects;

    private BoundingBox boundingBox;
    private boolean bbNeedsRecalc = false;

    public Room(RoomSource prefabSet, PiecesContainer piecesContainer, RoomObjects roomObjects) {
        this.prefabSet = prefabSet;
        this.piecesContainer = piecesContainer;
        this.roomObjects = roomObjects;

        this.boundingBox = this.piecesContainer.calculateBoundingBox();

        this.setObjectParents();
    }

    public void setObjectParents() {
        this.roomObjects.doors.forEach(door -> door.setParentRoom(this));
    }

    public Optional<RoomDoor> getRandomEmptyDoor(RandomSource random) {
        return getRandomDoorMatching(random, door -> !door.isConnected());
    }

    public Optional<RoomDoor> getRandomEmptyExit(RandomSource random) {
        return getRandomDoorMatching(random, door -> !door.isConnected() && door.getDoorType().isExit);
    }

    public Optional<RoomDoor> getRandomEmptyEntranceMatching(RandomSource random, FrontAndTop orientation) {
        // TODO account for the multiple up and down orientations?
        Direction targetOrientation = orientation.front().getOpposite();

        return getRandomDoorMatching(random, door -> !door.isConnected() && door.getDoorType().isEntrance && door.getOrientation().front().equals(targetOrientation));
    }

    public Optional<RoomDoor> getRandomDoorMatching(RandomSource random, Predicate<RoomDoor> predicate) {
        List<RoomDoor> valid = this.getDoors().stream().filter(predicate).toList();
        return getRandomDoorFrom(valid, random);
    }

    public static Optional<RoomDoor> getRandomDoorFrom(List<RoomDoor> doors, RandomSource random) {
        if(doors.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(doors.get(random.nextInt(doors.size())));
        }
    }

    public void translateToMatchDoor(RoomDoor thisDoor, RoomDoor targetDoor, StageDesignData designData) {
        BlockPos targetPos = targetDoor.getPos().relative(targetDoor.getOrientation().front());
        BlockPos currentPos = thisDoor.getPos();
        this.translate(targetPos.subtract(currentPos), designData);
    }

    public void centerAt(Vec3i vec3i, StageDesignData designData) {
        this.translate(vec3i.subtract(this.getBoundingBox().getCenter()), designData);
    }

    public void translate(Vec3i vec3i, StageDesignData designData) {
        this.translate(vec3i.getX(), vec3i.getY(), vec3i.getZ(), designData);
    }

    public void translate(int x, int y, int z, StageDesignData designData) {
        this.translate(x, y, z);
        designData.getCollisionMap().updateRoom(this);
    }

    public void translate(int x, int y, int z) {
        this.getPiecesContainer().pieces().forEach(structurePiece -> structurePiece.move(x, y, z));
        this.roomObjects.translate(x, y, z);

        // TODO is the recalc the best way to do this? is it cheaper to just calc here?
        //this.bbNeedsRecalc = true;
        this.boundingBox = this.boundingBox.moved(x, y, z);
    }

    public RoomSource getRoomSource() {
        return prefabSet;
    }

    public PiecesContainer getPiecesContainer() {
        return piecesContainer;
    }

    public BoundingBox getBoundingBox() {
        if(this.bbNeedsRecalc) {
            this.boundingBox = this.piecesContainer.calculateBoundingBox();
            this.bbNeedsRecalc = false;
        }
        return this.boundingBox;
    }

    public List<RoomDoor> getDoors() {
        return this.roomObjects.doors;
    }

    public List<RoomLychseal> getLychseals() {
        return this.roomObjects.seals;
    }

    public Optional<RoomLychseal> getUnplacedLychseal(BlockPos pos) {
        return this.roomObjects.getUnplacedLychseal(pos);
    }

    public static class RoomObjects {

        private final List<RoomDoor> doors;
        private final List<RoomLychseal> seals;

        public RoomObjects(List<RoomDoor> doors, List<RoomLychseal> seals) {
            this.doors = doors;
            this.seals = seals;
        }

        public void translate(int x, int y, int z) {
            this.getDoors().forEach(door -> door.translate(x, y, z));
            this.getLychseals().forEach(door -> door.translate(x, y, z));
        }

        public List<RoomDoor> getDoors() {
            return doors;
        }

        public List<RoomLychseal> getLychseals() {
            return seals;
        }

        public Optional<RoomLychseal> getUnplacedLychseal(BlockPos pos) {
            for(RoomLychseal lychseal : this.seals) {
                if(!lychseal.isPlaced()) {
                    if (lychseal.getPos().equals(pos)) {
                        lychseal.setPlaced(true);
                        return Optional.of(lychseal);
                    }
                }
            }
            return Optional.empty();
        }
    }
}
