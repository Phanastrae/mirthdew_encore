package phanastrae.mirthdew_encore.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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

    public void writeNbt(NbtCompound nbt) {
        if(this.entitySleepData != null) {
            NbtCompound sleepData = new NbtCompound();
            this.entitySleepData.writeNbt(sleepData);
            nbt.put("SleepData", sleepData);
        }
    }

    public void readNbt(NbtCompound nbt) {
        if(this.entitySleepData != null) {
            if(nbt.contains("SleepData", NbtElement.COMPOUND_TYPE)) {
                NbtCompound sleepData = nbt.getCompound("SleepData");
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
