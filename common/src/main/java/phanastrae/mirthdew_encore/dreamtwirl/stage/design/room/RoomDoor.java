package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Optional;

import static phanastrae.mirthdew_encore.block.entity.DoorMarkerBlockEntity.KEY_DOOR_TYPE;
import static phanastrae.mirthdew_encore.block.entity.DoorMarkerBlockEntity.KEY_TARGET_LYCHSEAL;

public class RoomDoor {
    public static final StringRepresentable.StringRepresentableCodec<DoorType> DOOR_TYPE_CODEC = StringRepresentable.fromEnum(DoorType::values);
    public static final StringRepresentable.StringRepresentableCodec<FrontAndTop> FRONT_AND_TOP_CODEC = StringRepresentable.fromEnum(FrontAndTop::values);

    public static final Codec<RoomDoor> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.INT.optionalFieldOf("door_id", 0).forGetter(RoomDoor::getDoorId),
                            Codec.LONG.optionalFieldOf("room_id", 0L).forGetter(RoomDoor::getRoomId),
                            DOOR_TYPE_CODEC.optionalFieldOf("door_type", DoorType.TWOWAY).forGetter(RoomDoor::getDoorType),
                            Codec.STRING.optionalFieldOf("target_lychseal", "").forGetter(RoomDoor::getTargetLychseal),
                            BlockPos.CODEC.fieldOf("pos").forGetter(RoomDoor::getPos),
                            FRONT_AND_TOP_CODEC.fieldOf("orientation").forGetter(RoomDoor::getOrientation),
                            Codec.BOOL.optionalFieldOf("connected", false).forGetter(RoomDoor::isConnected)
                    )
                    .apply(instance, RoomDoor::new)
    );

    private final int doorId;
    private final long roomId;
    private final DoorType doorType;
    private final String targetLychseal;
    private BlockPos pos;
    private FrontAndTop orientation;
    private boolean connected;

    public RoomDoor(int doorId, long roomId, DoorType doorType, String targetLychseal, BlockPos pos, FrontAndTop orientation, boolean connected) {
        this.doorId = doorId;
        this.roomId = roomId;
        this.doorType = doorType;
        this.targetLychseal = targetLychseal;
        this.pos = pos;
        this.orientation = orientation;
        this.connected = connected;
    }

    public static RoomDoor fromNbt(int doorId, long roomId, CompoundTag nbt, BlockPos pos, FrontAndTop orientation, boolean connected) {
        DoorType doorType;
        if(nbt.contains(KEY_DOOR_TYPE)) {
            doorType = DoorType.byName(nbt.getString(KEY_DOOR_TYPE))
                    .orElse(
                            DoorType.TWOWAY
                    );
        } else {
            doorType = DoorType.TWOWAY;
        }

        String targetLychseal;
        if(nbt.contains(KEY_TARGET_LYCHSEAL)) {
            targetLychseal = nbt.getString(KEY_TARGET_LYCHSEAL);
        } else {
            targetLychseal = "";
        }

        return new RoomDoor(doorId, roomId, doorType, targetLychseal, pos, orientation, connected);
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

    public int getDoorId() {
        return doorId;
    }

    public long getRoomId() {
        return roomId;
    }

    public RoomDoorId getRoomDoorId() {
        return new RoomDoorId(this.doorId, this.roomId);
    }

    public record RoomDoorId(int doorId, long roomId) {

        @Override
        public boolean equals(Object o) {
            if(o == this) {
                return true;
            } else if(o instanceof RoomDoorId ot) {
                return this.doorId == ot.doorId && this.roomId == ot.roomId;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Long.hashCode(this.roomId ^ (((long)this.doorId << 3) * 137L));
        }
    }

    public enum DoorType implements StringRepresentable {
        ENTRANCE_ONLY("entrance", true, false),
        EXIT_ONLY("exit", false, true),
        TWOWAY("both", true, true);

        private final String name;
        public final boolean isEntrance;
        public final boolean isExit;

        DoorType(String name, boolean isEntrance, boolean isExit) {
            this.name = name;
            this.isEntrance = isEntrance;
            this.isExit = isExit;
        }

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