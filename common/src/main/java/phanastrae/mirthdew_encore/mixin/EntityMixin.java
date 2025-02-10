package phanastrae.mirthdew_encore.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.compat.Compat;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlLevelAttachment;
import phanastrae.mirthdew_encore.duck.EntityDuckInterface;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreEntityTypeTags;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityDuckInterface {

    @Shadow
    private static List<VoxelShape> collectColliders(@Nullable Entity entity, Level world, List<VoxelShape> regularCollisions, AABB movingEntityBoundingBox) {
        return List.of();
    }

    @Shadow
    private static Vec3 collideWithShapes(Vec3 movement, AABB entityBoundingBox, List<VoxelShape> collisions) {
        return Vec3.ZERO;
    }

    @Unique
    private MirthdewEncoreEntityAttachment mirthdew_encore$entityAttachment;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mirthdew_encore$init(EntityType type, Level world, CallbackInfo ci) {
        this.mirthdew_encore$entityAttachment = new MirthdewEncoreEntityAttachment((Entity)(Object)this);
    }

    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void mirthdew_encore$writeNbt(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        if(nbt.contains("MirthdewEncore", Tag.TAG_COMPOUND)) {
            CompoundTag nbtCompound = nbt.getCompound("MirthdewEncore");
            this.mirthdew_encore$entityAttachment.writeNbt(nbtCompound);
        } else {
            CompoundTag nbtCompound = new CompoundTag();
            this.mirthdew_encore$entityAttachment.writeNbt(nbtCompound);
            nbt.put("MirthdewEncore", nbtCompound);
        }
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void mirthdew_encore$readNbt(CompoundTag nbt, CallbackInfo ci) {
        if(nbt.contains("MirthdewEncore", Tag.TAG_COMPOUND)) {
            CompoundTag nbtCompound = nbt.getCompound("MirthdewEncore");
            this.mirthdew_encore$entityAttachment.readNbt(nbtCompound);
        }
    }

    @Inject(method = "baseTick", at = @At("RETURN"))
    private void mirthdew_encore$tick(CallbackInfo ci) {
        this.mirthdew_encore$entityAttachment.tick();
    }

    @ModifyVariable(method = "collide", at = @At(value = "STORE"), ordinal = 1)
    private Vec3 mirthdew_encore$collide(Vec3 value, @Local(ordinal = 0) Vec3 movement, @Local(ordinal = 0) List<VoxelShape> voxelShapeList) {
        Entity thisEntity = (Entity) (Object) this;
        AABB box = thisEntity.getBoundingBox();

        if(movement.lengthSqr() != 0.0) {
            if(Compat.USE_DREAMSPECK_COLLISION_COMPAT) {
                // manually call findCollisionsForMovement
                if (thisEntity.getType().is(MirthdewEncoreEntityTypeTags.USES_DREAMSPECK_COLLISION)) {
                    List<VoxelShape> list = collectColliders(thisEntity, thisEntity.level(), voxelShapeList, box.expandTowards(movement));
                    value = collideWithShapes(movement, box, list);
                }
            }

            // dreamtwirl border collisions
            ImmutableList.Builder<VoxelShape> builder = new ImmutableList.Builder<>();
            DreamtwirlLevelAttachment.findBorderCollision(thisEntity, thisEntity.level(), builder);
            List<VoxelShape> list = builder.build();
            if(!list.isEmpty()) {
                value = collideWithShapes(value, box, list);
            }
        }
        return value;
    }

    @Inject(method = "getGravity", at = @At("HEAD"), cancellable = true)
    private void mirthdew_encore$cancelGravity(CallbackInfoReturnable<Double> cir) {
        if(this.mirthdew_encore$entityAttachment.shouldCancelGravity()) {
            cir.setReturnValue(0.0);
        }
    }

    @Override
    public MirthdewEncoreEntityAttachment mirthdew_encore$getAttachment() {
        return mirthdew_encore$entityAttachment;
    }
}
