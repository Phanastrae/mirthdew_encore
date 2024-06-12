package phanastrae.mirthlight_encore.dreamtwirl;

import net.minecraft.data.client.VariantSettings;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DreamtwirlRoomType {

    public final int sizeX;
    public final int sizeY;
    public final int sizeZ;
    private final List<Door> doorList = new ArrayList<>();
    private final List<Door>[] doorListsByDirection;

    public DreamtwirlRoomType(int sizeX, int sizeY, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.doorListsByDirection = new List[6];
        for(int i = 0; i < 6; i++) {
            this.doorListsByDirection[i] = new ArrayList<>();
        }
    }

    public DreamtwirlRoomType getMirroredX() {
        DreamtwirlRoomType mirroredRoomType = new DreamtwirlRoomType(this.sizeX, this.sizeY, this.sizeZ);
        for(Door door : this.doorList) {
            Vec3i mirrorPos = new Vec3i(this.sizeX - 1 - door.pos.getX(), door.pos.getY(), door.pos.getZ());
            Direction mirrorDirection = door.direction.getOpposite();
            Door mirrorDoor = new Door(mirrorPos, mirrorDirection);
            mirroredRoomType.addDoor(mirrorDoor);
        }
        return mirroredRoomType;
    }

    public DreamtwirlRoomType getMirroredZ() {
        DreamtwirlRoomType mirroredRoomType = new DreamtwirlRoomType(this.sizeX, this.sizeY, this.sizeZ);
        for(Door door : this.doorList) {
            Vec3i mirrorPos = new Vec3i(door.pos.getX(), door.pos.getY(), this.sizeZ - 1 - door.pos.getZ());
            Direction mirrorDirection = door.direction.getOpposite();
            Door mirrorDoor = new Door(mirrorPos, mirrorDirection);
            mirroredRoomType.addDoor(mirrorDoor);
        }
        return mirroredRoomType;
    }

    public DreamtwirlRoomType getRotated(VariantSettings.Rotation rotation) {
        DreamtwirlRoomType rotatedType;
        if(rotation == VariantSettings.Rotation.R0 || rotation == VariantSettings.Rotation.R180) {
            rotatedType = new DreamtwirlRoomType(this.sizeX, this.sizeY, this.sizeZ);
        } else {
            rotatedType = new DreamtwirlRoomType(this.sizeZ, this.sizeY, this.sizeX);
        }
        for(Door door : this.doorList) {
            int doorX = door.pos.getX();
            int doorY = door.pos.getY();
            int doorZ = door.pos.getZ();
            int doorXNeg = this.sizeX - 1 - doorX;
            int doorZNeg = this.sizeZ - 1 - doorZ;

            Vec3i rotatedPos;
            switch (rotation) {
                case R90 -> rotatedPos = new Vec3i(doorZ, doorY, doorXNeg);
                case R180 -> rotatedPos = new Vec3i(doorXNeg, doorY, doorZNeg);
                case R270 -> rotatedPos = new Vec3i(doorZNeg, doorY, doorX);
                default -> rotatedPos = new Vec3i(doorX, doorY, doorZ);
            }
            Direction rotatedDirection = door.direction.rotateYCounterclockwise();
            Door rotatedDoor = new Door(rotatedPos, rotatedDirection);

            rotatedType.addDoor(rotatedDoor);
        }
        return rotatedType;
    }

    @Nullable
    public Door getRandomDoor(Random random) {
        if(this.doorList.isEmpty()) {
            return null;
        } else {
            int n = random.nextInt(this.doorList.size());
            return this.doorList.get(n);
        }
    }

    @Nullable
    public Door getRandomDoor(Random random, Direction direction) {
        List<Door> doorList = this.getDoorListForDirection(direction);
        if(doorList.isEmpty()) {
            return null;
        } else {
            int n = random.nextInt(doorList.size());
            return doorList.get(n);
        }
    }

    public DreamtwirlRoomType addDoor(int x, int y, int z, Direction direction) {
        return this.addDoor(new Door(new Vec3i(x, y, z), direction));
    }

    public DreamtwirlRoomType addDoor(Door door) {
        this.doorList.add(door);
        this.getDoorListForDirection(door.direction).add(door);
        return this;
    }

    public List<Door> getDoorListForDirection(Direction direction) {
        return this.doorListsByDirection[direction.getId()];
    }

    public boolean hasDoorFacing(Direction direction) {
        return !this.getDoorListForDirection(direction).isEmpty();
    }

    public static class Door {
        public final Vec3i pos;
        public final Direction direction;

        public Door(Vec3i pos, Direction direction) {
            this.pos = pos;
            this.direction = direction;
        }
    }
}
