package phanastrae.mirthdew_encore.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.dreamtwirl.EntityDreamtwirlData;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreBlockTags;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreEntityTypeTags;

@Mixin(CollisionGetter.class)
public interface CollisionGetterMixin {

    @ModifyReturnValue(method = "borderCollision", at = @At("RETURN"))
    private VoxelShape mirthdew_encore$addDreamtwirlBorderCollisions(VoxelShape original, Entity entity) {
        if(entity.level().equals(this)) {
            return EntityDreamtwirlData.addCollisionsTo(original, entity);
        } else {
            return original;
        }
    }

    @Inject(method = "getBlockCollisions", at = @At("HEAD"), cancellable = true)
    private void mirthdew_encore$overrideDreamspeckCollisions(@Nullable Entity entity, AABB box, CallbackInfoReturnable<Iterable<VoxelShape>> cir) {
        if(entity != null && entity.getType().is(MirthdewEncoreEntityTypeTags.USES_DREAMSPECK_COLLISION)) {
            CollisionGetter thisWorld = (CollisionGetter)this;
            Iterable<VoxelShape> iterable = () -> new BlockCollisions<>(thisWorld, entity, box, false, (pos, voxelShape) -> {
                boolean collide = false;

                BlockState state = thisWorld.getBlockState(pos);
                if(state.is(MirthdewEncoreBlockTags.DREAMSPECK_OPAQUE)) {
                    collide = true;
                } else if(state.is(Blocks.MOVING_PISTON)) {
                    BlockEntity blockEntity = thisWorld.getBlockEntity(pos);
                    if(blockEntity instanceof PistonMovingBlockEntity pistonBlockEntity) {
                        if(pistonBlockEntity.getMovedState().is(MirthdewEncoreBlockTags.DREAMSPECK_OPAQUE)) {
                            collide = true;
                        }
                    }
                }

                if(collide) {
                    return voxelShape;
                } else {
                    return Shapes.empty();
                }
            });
            cir.setReturnValue(iterable);
        }
    }
}
