package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room;

import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;

public class RoomDoor {

    private BlockPos pos;
    private FrontAndTop orientation;
    private boolean connected = false;

    private DoorType doorType;

    public RoomDoor(BlockPos pos, FrontAndTop orientation, CompoundTag nbt) {
        this.pos = pos;
        this.orientation = orientation;

        // TODO handle any custom nbt
        this.doorType = DoorType.TWOWAY;
    }

    public void translate(int x, int y, int z) {
        this.pos = this.pos.offset(x, y, z);
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isConnected() {
        return connected;
    }

    public FrontAndTop getOrientation() {
        return orientation;
    }

    public BlockPos getPos() {
        return pos;
    }

    public DoorType getDoorType() {
        return doorType;
    }

    public enum DoorType {
        ENTRANCE_ONLY(true, false),
        EXIT_ONLY(false, true),
        TWOWAY(true, true);

        public final boolean isEntrance;
        public final boolean isExit;

        DoorType(boolean isEntrance, boolean isExit) {
            this.isEntrance = isEntrance;
            this.isExit = isExit;
        }
    }
}