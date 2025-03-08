package phanastrae.mirthdew_encore.dreamtwirl.stage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import phanastrae.mirthdew_encore.util.RegionPos;

public class BasicStageData {
    public static final String KEY_ID = "Id";
    public static final String KEY_TIMESTAMP = "Timestamp";
    public static final String KEY_DELETING_SELF = "is_deleting_self";

    private final long id;
    private final RegionPos regionPos;
    private final long timestamp;

    private boolean isDeletingSelf;

    public BasicStageData(long id, long timestamp) {
        this.id = id;
        this.regionPos = new RegionPos(id);
        this.timestamp = timestamp;

        this.isDeletingSelf = false;
    }

    public CompoundTag writeNbt(CompoundTag nbt) {
        nbt.putLong(KEY_ID, this.getId());
        nbt.putLong(KEY_TIMESTAMP, this.getTimestamp());

        nbt.putBoolean(KEY_DELETING_SELF, this.isDeletingSelf);

        return nbt;
    }

    public static BasicStageData fromNbt(CompoundTag nbt) {
        long id = nbt.getLong(KEY_ID);
        long timestamp = nbt.getLong(KEY_TIMESTAMP);

        BasicStageData bsd = new BasicStageData(id, timestamp);

        if(nbt.contains(KEY_DELETING_SELF, Tag.TAG_BYTE)) {
            bsd.isDeletingSelf = nbt.getBoolean(KEY_DELETING_SELF);
        } else {
            bsd.isDeletingSelf = false;
        }

        return bsd;
    }

    public void setDeletingSelf(boolean deletingSelf) {
        this.isDeletingSelf = deletingSelf;
    }

    public boolean isDeletingSelf() {
        return this.isDeletingSelf;
    }

    public long getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public RegionPos getRegionPos() {
        return regionPos;
    }

    public long getAgeInTicks(long levelTime) {
        return levelTime - this.timestamp;
    }

    public Component getAgeTextComponentFromLevelTime(long levelTime) {
        return BasicStageData.getAgeTextComponent(this.getAgeInTicks(levelTime));
    }

    public static Component getAgeTextComponent(long ageInTicks) {
        long ageInSeconds = ageInTicks / 20;
        long ageInMinutes = ageInSeconds / 60;
        long ageInHours = ageInMinutes / 60;

        if(ageInHours >= 1) {
            return Component.translatable("mirthdew_encore.displays.hours_minutes", ageInHours, ageInMinutes % 60);
        } else if(ageInMinutes >= 1) {
            return Component.translatable("mirthdew_encore.displays.minutes_seconds", ageInMinutes, ageInSeconds % 60);
        } else if(ageInSeconds >= 1) {
            return Component.translatable("mirthdew_encore.displays.seconds", ageInSeconds);
        } else {
            return Component.translatable("mirthdew_encore.displays.ticks", ageInTicks);
        }
    }
}
