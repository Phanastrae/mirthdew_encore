package phanastrae.mirthlight_encore.dreamtwirl;

import net.minecraft.nbt.NbtCompound;

public class DreamtwirlStage {

    private final long id;
    private final long timestamp;
    private boolean markDirty = false;

    public DreamtwirlStage(long id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public void tick() {

    }

    public void markDirty() {
        this.markDirty(true);
    }

    public void markDirty(boolean value) {
        this.markDirty = value;
    }

    public boolean isDirty() {
        return this.markDirty;
    }

    public long getId() {
        return this.id;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putLong("Id", this.getId());
        nbt.putLong("Timestamp", this.getTimestamp());
        return nbt;
    }

    public static DreamtwirlStage fromNbt(NbtCompound nbt) {
        long id = nbt.getLong("Id");
        long timestamp = nbt.getLong("Timestamp");
        return new DreamtwirlStage(id, timestamp);
    }
}
