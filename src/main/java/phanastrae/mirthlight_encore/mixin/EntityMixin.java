package phanastrae.mirthlight_encore.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
import phanastrae.mirthlight_encore.dreamtwirl.DreamtwirlEntityAttachment;
import phanastrae.mirthlight_encore.dreamtwirl.DreamtwirlWorldAttachment;
import phanastrae.mirthlight_encore.duck.EntityDuckInterface;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin implements EntityDuckInterface {

    @Unique
    private DreamtwirlEntityAttachment mirthlight_encore$dreamtwirlAttachment;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mirthlight_encore$init(EntityType type, World world, CallbackInfo ci) {
        this.mirthlight_encore$dreamtwirlAttachment = new DreamtwirlEntityAttachment((Entity)(Object)this);
    }

    @Inject(method = "baseTick", at = @At("RETURN"))
    private void mirthlight_encore$tick(CallbackInfo ci) {
        this.mirthlight_encore$dreamtwirlAttachment.tick();
    }

    @Inject(method = "findCollisionsForMovement", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;build()Lcom/google/common/collect/ImmutableList;", shift = At.Shift.BEFORE))
    private static void mirthlight_encore$dreamTwirlBorderCollision(@Nullable Entity entity, World world, List<VoxelShape> regularCollisions, Box movingEntityBoundingBox, CallbackInfoReturnable<List<VoxelShape>> cir, @Local(ordinal = 0) ImmutableList.Builder<VoxelShape> builder) {
        DreamtwirlWorldAttachment.findBorderCollision(entity, world, builder);
    }

    @Override
    public DreamtwirlEntityAttachment mirthlight_encore$getDreamtwirlAttachment() {
        return mirthlight_encore$dreamtwirlAttachment;
    }
}
