package phanastrae.mirthdew_encore.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
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
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlWorldAttachment;
import phanastrae.mirthdew_encore.duck.EntityDuckInterface;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreEntityTypeTags;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityDuckInterface {

    @Shadow
    private static List<VoxelShape> findCollisionsForMovement(@Nullable Entity entity, World world, List<VoxelShape> regularCollisions, Box movingEntityBoundingBox) {
        return List.of();
    }

    @Shadow
    private static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
        return Vec3d.ZERO;
    }

    @Unique
    private MirthdewEncoreEntityAttachment mirthdew_encore$entityAttachment;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mirthdew_encore$init(EntityType type, World world, CallbackInfo ci) {
        this.mirthdew_encore$entityAttachment = new MirthdewEncoreEntityAttachment((Entity)(Object)this);
    }

    @Inject(method = "baseTick", at = @At("RETURN"))
    private void mirthdew_encore$tick(CallbackInfo ci) {
        this.mirthdew_encore$entityAttachment.tick();
    }

    @ModifyVariable(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "STORE"), ordinal = 1)
    private Vec3d mirthdew_encore$adjustMovementForCollisions(Vec3d value, @Local(ordinal = 0) Vec3d movement, @Local(ordinal = 0) List<VoxelShape> voxelShapeList) {
        Entity thisEntity = (Entity) (Object) this;
        Box box = thisEntity.getBoundingBox();

        if(movement.lengthSquared() != 0.0) {
            if(Compat.USE_DREAMSPECK_COLLISION_COMPAT) {
                // manually call findCollisionsForMovement
                if (thisEntity.getType().isIn(MirthdewEncoreEntityTypeTags.USES_DREAMSPECK_COLLISION)) {
                    List<VoxelShape> list = findCollisionsForMovement(thisEntity, thisEntity.getWorld(), voxelShapeList, box.stretch(movement));
                    value = adjustMovementForCollisions(movement, box, list);
                }
            }

            // dreamtwirl border collisions
            ImmutableList.Builder<VoxelShape> builder = new ImmutableList.Builder<>();
            DreamtwirlWorldAttachment.findBorderCollision(thisEntity, thisEntity.getWorld(), builder);
            List<VoxelShape> list = builder.build();
            if(!list.isEmpty()) {
                value = adjustMovementForCollisions(movement, box, list);
            }
        }
        return value;
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    private void mirthdew_encore$writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if(nbt.contains("MirthdewEncore", NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbtCompound = nbt.getCompound("MirthdewEncore");
            this.mirthdew_encore$entityAttachment.writeNbt(nbtCompound);
        } else {
            NbtCompound nbtCompound = new NbtCompound();
            this.mirthdew_encore$entityAttachment.writeNbt(nbtCompound);
            nbt.put("MirthdewEncore", nbtCompound);
        }
    }

    @Inject(method = "readNbt", at = @At("RETURN"))
    private void mirthdew_encore$readNbt(NbtCompound nbt, CallbackInfo ci) {
        if(nbt.contains("MirthdewEncore", NbtElement.COMPOUND_TYPE)) {
            NbtCompound nbtCompound = nbt.getCompound("MirthdewEncore");
            this.mirthdew_encore$entityAttachment.readNbt(nbtCompound);
        }
    }

    @Override
    public MirthdewEncoreEntityAttachment mirthdew_encore$getAttachment() {
        return mirthdew_encore$entityAttachment;
    }
}
