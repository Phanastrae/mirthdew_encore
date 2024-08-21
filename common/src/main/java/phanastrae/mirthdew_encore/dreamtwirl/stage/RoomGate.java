package phanastrae.mirthdew_encore.dreamtwirl.stage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;

public class RoomGate {
    private BlockPos pos;
    private FrontAndTop orientation;
    private boolean filled = false;

    public RoomGate(BlockPos pos, FrontAndTop orientation) {
        this.pos = pos;
        this.orientation = orientation;
    }

    public void translate(int x, int y, int z) {
        this.pos = this.pos.offset(x, y, z);
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public boolean isFilled() {
        return filled;
    }

    public FrontAndTop getOrientation() {
        return orientation;
    }

    public BlockPos getPos() {
        return pos;
    }
}
