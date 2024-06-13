package phanastrae.mirthdew_encore.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlWorldAttachment;
import phanastrae.mirthdew_encore.duck.EntityDuckInterface;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin implements EntityDuckInterface {

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

    @Inject(method = "findCollisionsForMovement", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;build()Lcom/google/common/collect/ImmutableList;", shift = At.Shift.BEFORE))
    private static void mirthdew_encore$dreamTwirlBorderCollision(@Nullable Entity entity, World world, List<VoxelShape> regularCollisions, Box movingEntityBoundingBox, CallbackInfoReturnable<List<VoxelShape>> cir, @Local(ordinal = 0) ImmutableList.Builder<VoxelShape> builder) {
        DreamtwirlWorldAttachment.findBorderCollision(entity, world, builder);
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
