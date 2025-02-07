package phanastrae.mirthdew_encore.dreamtwirl.stage.acherune;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class Acherune {
    public static final String KEY_POS_X = "x";
    public static final String KEY_POS_Y = "y";
    public static final String KEY_POS_Z = "z";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_ID = "id";

    private BlockPos pos;
    private final AcheruneId id;

    public Acherune(BlockPos pos, AcheruneId id) {
        this.pos = pos;
        this.id = id;
    }

    public CompoundTag writeNbt(CompoundTag nbt) {
        nbt.putInt(KEY_POS_X, this.pos.getX());
        nbt.putInt(KEY_POS_Y, this.pos.getY());
        nbt.putInt(KEY_POS_Z, this.pos.getZ());
        nbt.putLong(KEY_TIMESTAMP, this.id.timestamp());
        nbt.putLong(KEY_ID, this.id.id());

        return nbt;
    }

    public static Acherune fromNbt(CompoundTag nbt) {
        int x = nbt.getInt(KEY_POS_X);
        int y = nbt.getInt(KEY_POS_Y);
        int z = nbt.getInt(KEY_POS_Z);
        long timestamp = nbt.getLong(KEY_TIMESTAMP);
        long id = nbt.getLong(KEY_ID);
        BlockPos pos = new BlockPos(x, y, z);

        return new Acherune(pos, new AcheruneId(timestamp, id));
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }

    public AcheruneId getId() {
        return id;
    }

    public record AcheruneId(long timestamp, long id) {
        @Override
        public boolean equals(Object o) {
            if(o instanceof AcheruneId ot) {
                return this.timestamp == ot.timestamp && this.id == ot.id;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Long.hashCode(this.timestamp ^ this.id);
        }
    }
}
