package phanastrae.mirthdew_encore.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import phanastrae.mirthdew_encore.dreamtwirl.EntityDreamtwirlData;
import phanastrae.mirthdew_encore.duck.EntityDuckInterface;

public class MirthdewEncoreEntityAttachment {

    private final Entity entity;
    private final EntityDreamtwirlData entityDreamtwirlData;

    public MirthdewEncoreEntityAttachment(Entity entity) {
        this.entity = entity;
        this.entityDreamtwirlData = new EntityDreamtwirlData(entity);
    }

    public void tick() {
        this.entityDreamtwirlData.tick();
    }

    public void writeNbt(NbtCompound nbt) {
    }

    public void readNbt(NbtCompound nbt) {
    }

    public EntityDreamtwirlData getDreamtwirlEntityData() {
        return this.entityDreamtwirlData;
    }

    public static MirthdewEncoreEntityAttachment fromEntity(Entity entity) {
        return ((EntityDuckInterface)entity).mirthdew_encore$getAttachment();
    }
}
