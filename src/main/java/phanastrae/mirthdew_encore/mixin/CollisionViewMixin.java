package phanastrae.mirthdew_encore.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.dreamtwirl.EntityDreamtwirlData;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreBlockTags;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreEntityTypeTags;

@Mixin(CollisionView.class)
public interface CollisionViewMixin {

    @ModifyReturnValue(method = "getWorldBorderCollisions", at = @At("RETURN"))
    private VoxelShape mirthdew_encore$addDreamtwirlCollisions(VoxelShape original, Entity entity) {
        if(entity.getWorld().equals(this)) {
            return EntityDreamtwirlData.addCollisionsTo(original, entity);
        } else {
            return original;
        }
    }

    @Inject(method = "getBlockCollisions", at = @At("HEAD"), cancellable = true)
    private void mirthdew_encore$overrideDreamspeckCollisions(@Nullable Entity entity, Box box, CallbackInfoReturnable<Iterable<VoxelShape>> cir) {
        if(entity != null && entity.getType().isIn(MirthdewEncoreEntityTypeTags.USES_DREAMSPECK_COLLISION)) {
            CollisionView thisWorld = (CollisionView)this;
            Iterable<VoxelShape> iterable = () -> new BlockCollisionSpliterator<>(thisWorld, entity, box, false, (pos, voxelShape) -> {
                boolean collide = false;

                BlockState state = thisWorld.getBlockState(pos);
                if(state.isIn(MirthdewEncoreBlockTags.DREAMSPECK_OPAQUE)) {
                    collide = true;
                } else if(state.isOf(Blocks.MOVING_PISTON)) {
                    BlockEntity blockEntity = thisWorld.getBlockEntity(pos);
                    if(blockEntity instanceof PistonBlockEntity pistonBlockEntity) {
                        if(pistonBlockEntity.getPushedBlock().isIn(MirthdewEncoreBlockTags.DREAMSPECK_OPAQUE)) {
                            collide = true;
                        }
                    }
                }

                if(collide) {
                    return voxelShape;
                } else {
                    return VoxelShapes.empty();
                }
            });
            cir.setReturnValue(iterable);
        }
    }
}
