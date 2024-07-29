package phanastrae.mirthdew_encore.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.EntityDreamtwirlData;
import phanastrae.mirthdew_encore.duck.EntityDuckInterface;

public class MirthdewEncoreEntityAttachment {
    private final Entity entity;
    private final EntityDreamtwirlData entityDreamtwirlData;
    @Nullable private final EntitySleepData entitySleepData;

    public MirthdewEncoreEntityAttachment(Entity entity) {
        this.entity = entity;
        this.entityDreamtwirlData = new EntityDreamtwirlData(entity);
        this.entitySleepData = (entity instanceof LivingEntity livingEntity) ? new EntitySleepData(livingEntity) : null;
    }

    public void writeNbt(CompoundTag nbt) {
        if(this.entitySleepData != null) {
            CompoundTag sleepData = new CompoundTag();
            this.entitySleepData.writeNbt(sleepData);
            nbt.put("SleepData", sleepData);
        }
    }

    public void readNbt(CompoundTag nbt) {
        if(this.entitySleepData != null) {
            if(nbt.contains("SleepData", Tag.TAG_COMPOUND)) {
                CompoundTag sleepData = nbt.getCompound("SleepData");
                this.entitySleepData.readNbt(sleepData);
            }
        }
    }

    public void tick() {
        this.entityDreamtwirlData.tick();
        if(this.entitySleepData != null) {
            this.entitySleepData.tick();
        }
    }

    public EntityDreamtwirlData getDreamtwirlEntityData() {
        return this.entityDreamtwirlData;
    }

    @Nullable
    public EntitySleepData getEntitySleepData() {
        return this.entitySleepData;
    }

    public static MirthdewEncoreEntityAttachment fromEntity(Entity entity) {
        return ((EntityDuckInterface)entity).mirthdew_encore$getAttachment();
    }
}
