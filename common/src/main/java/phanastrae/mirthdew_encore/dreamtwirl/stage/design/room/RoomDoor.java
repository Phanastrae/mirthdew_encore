package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room;

import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

import static phanastrae.mirthdew_encore.block.entity.DoorMarkerBlockEntity.KEY_DOOR_TYPE;
import static phanastrae.mirthdew_encore.block.entity.DoorMarkerBlockEntity.KEY_TARGET_LYCHSEAL;

public class RoomDoor {

    private BlockPos pos;
    private FrontAndTop orientation;
    private boolean connected = false;

    @Nullable
    private Room parentRoom;

    private final DoorType doorType;
    private final String targetLychseal;

    public RoomDoor(BlockPos pos, FrontAndTop orientation, CompoundTag nbt) {
        this.pos = pos;
        this.orientation = orientation;

        if(nbt.contains(KEY_DOOR_TYPE)) {
            this.doorType = DoorType.byName(nbt.getString(KEY_DOOR_TYPE))
                    .orElse(
                            DoorType.TWOWAY
                    );
        } else {
            this.doorType = DoorType.TWOWAY;
        }

        if(nbt.contains(KEY_TARGET_LYCHSEAL)) {
            this.targetLychseal = nbt.getString(KEY_TARGET_LYCHSEAL);
        } else {
            this.targetLychseal = "";
        }
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

    public String getTargetLychseal() {
        return targetLychseal;
    }

    public boolean hasTargetLychseal() {
        return !this.targetLychseal.isEmpty();
    }

    public void setParentRoom(@Nullable Room parentRoom) {
        this.parentRoom = parentRoom;
    }

    public @Nullable Room getParentRoom() {
        return this.parentRoom;
    }

    public enum DoorType implements StringRepresentable {
        ENTRANCE_ONLY("entrance", true, false),
        EXIT_ONLY("exit", false, true),
        TWOWAY("both", true, true);

        public final boolean isEntrance;
        public final boolean isExit;

        DoorType(String name, boolean isEntrance, boolean isExit) {
            this.name = name;
            this.isEntrance = isEntrance;
            this.isExit = isExit;
        }

        private final String name;

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static Optional<DoorType> byName(String name) {
            return Arrays.stream(values()).filter(p_59461_ -> p_59461_.getSerializedName().equals(name)).findFirst();
        }

        public Component getTranslatedName() {
            return Component.translatable("mirthdew_encore.door_type." + this.name);
        }
    }
}