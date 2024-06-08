package phanastrae.mirthlight_encore.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthlight_encore.dreamtwirl.DreamtwirlWorldAttachment;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "canPlayerModifyAt", at = @At("HEAD"), cancellable = true)
    private void mirthlight_encore$preventAdjacentDreamtwirlModification(PlayerEntity player, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(DreamtwirlWorldAttachment.positionsAreInSeperateDreamtwirls(player.getWorld(), player.getPos(), Vec3d.ofCenter(pos))) {
            cir.setReturnValue(false);
        }
    }
}
