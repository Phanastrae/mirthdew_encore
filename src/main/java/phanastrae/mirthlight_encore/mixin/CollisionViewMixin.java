package phanastrae.mirthlight_encore.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phanastrae.mirthlight_encore.dreamtwirl.DreamtwirlEntityAttachment;

@Mixin(CollisionView.class)
public interface CollisionViewMixin {

    @ModifyReturnValue(method = "getWorldBorderCollisions", at = @At("RETURN"))
    private VoxelShape mirthlight_encore$addDreamtwirlCollisions(VoxelShape original, Entity entity) {
        return DreamtwirlEntityAttachment.addCollisionsTo(original, entity);
    }
}
