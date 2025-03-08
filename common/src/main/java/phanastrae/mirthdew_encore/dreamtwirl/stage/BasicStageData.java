package phanastrae.mirthdew_encore.dreamtwirl.stage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import phanastrae.mirthdew_encore.util.RegionPos;

public class BasicStageData {
    public static final String KEY_ID = "Id";
    public static final String KEY_TIMESTAMP = "Timestamp";

    private final long id;
    private final RegionPos regionPos;
    private final long timestamp;

    public BasicStageData(long id, long timestamp) {
        this.id = id;
        this.regionPos = new RegionPos(id);
        this.timestamp = timestamp;
    }

    public CompoundTag writeNbt(CompoundTag nbt) {
        nbt.putLong(KEY_ID, this.getId());
        nbt.putLong(KEY_TIMESTAMP, this.getTimestamp());
        return nbt;
    }

    public static BasicStageData fromNbt(Level level, CompoundTag nbt) {
        long id = nbt.getLong(KEY_ID);
        long timestamp = nbt.getLong(KEY_TIMESTAMP);
        return new BasicStageData(id, timestamp);
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
