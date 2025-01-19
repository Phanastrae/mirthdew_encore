package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room;

import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Optional;

import static phanastrae.mirthdew_encore.block.entity.DoorMarkerBlockEntity.DOOR_TYPE;

public class RoomDoor {

    private BlockPos pos;
    private FrontAndTop orientation;
    private boolean connected = false;

    private DoorType doorType;

    public RoomDoor(BlockPos pos, FrontAndTop orientation, CompoundTag nbt) {
        this.pos = pos;
        this.orientation = orientation;

        // TODO handle any custom nbt
        this.doorType = RoomDoor.DoorType.byName(nbt.getString(DOOR_TYPE))
                .orElse(
                        RoomDoor.DoorType.TWOWAY
                );
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