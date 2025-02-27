package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room;

import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;

import static phanastrae.mirthdew_encore.block.entity.LychsealMarkerBlockEntity.KEY_LYCHSEAL_NAME;

public class RoomLychseal {

    private BlockPos pos;
    private FrontAndTop orientation;

    private final String lychsealName;
    private boolean isPlaced = false;

    public RoomLychseal(BlockPos pos, FrontAndTop orientation, CompoundTag nbt) {
        this.pos = pos;
        this.orientation = orientation;

        if(nbt.contains(KEY_LYCHSEAL_NAME)) {
            this.lychsealName = nbt.getString(KEY_LYCHSEAL_NAME);
        } else {
            this.lychsealName = "";
        }
    }

    public void translate(int x, int y, int z) {
        this.pos = this.pos.offset(x, y, z);
    }

    public FrontAndTop getOrientation() {
        return orientation;
    }

    public BlockPos getPos() {
        return pos;
    }

    public String getLychsealName() {
        return lychsealName;
    }

    public void setPlaced(boolean placed) {
        isPlaced = placed;
    }

    public boolean isPlaced() {
        return isPlaced;
    }
}
