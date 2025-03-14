package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;

import static phanastrae.mirthdew_encore.block.entity.LychsealMarkerBlockEntity.KEY_LYCHSEAL_NAME;

public class RoomLychseal {
    public static final Codec<RoomLychseal> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.STRING.optionalFieldOf("lychseal_name", "").forGetter(RoomLychseal::getLychsealName),
                            BlockPos.CODEC.fieldOf("pos").forGetter(RoomLychseal::getPos),
                            RoomDoor.FRONT_AND_TOP_CODEC.fieldOf("orientation").forGetter(RoomLychseal::getOrientation),
                            Codec.BOOL.optionalFieldOf("connected", false).forGetter(RoomLychseal::isPlaced)
                    )
                    .apply(instance, RoomLychseal::new)
    );

    private final String lychsealName;
    private BlockPos pos;
    private FrontAndTop orientation;
    private boolean isPlaced = false;

    public RoomLychseal(String lychsealName, BlockPos pos, FrontAndTop orientation, boolean isPlaced) {
        this.lychsealName = lychsealName;
        this.pos = pos;
        this.orientation = orientation;
        this.isPlaced = isPlaced;
    }

    public static RoomLychseal fromNbt(CompoundTag nbt, BlockPos pos, FrontAndTop orientation, boolean isPlaced) {
        String lychsealName;
        if(nbt.contains(KEY_LYCHSEAL_NAME)) {
            lychsealName = nbt.getString(KEY_LYCHSEAL_NAME);
        } else {
            lychsealName = "";
        }

        return new RoomLychseal(lychsealName, pos, orientation, isPlaced);
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
