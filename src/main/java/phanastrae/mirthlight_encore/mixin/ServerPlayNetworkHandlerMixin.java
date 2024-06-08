package phanastrae.mirthlight_encore.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthlight_encore.dreamtwirl.DreamtwirlWorldAttachment;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onPlayerInteractEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/border/WorldBorder;contains(Lnet/minecraft/util/math/BlockPos;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void mirthlight_encore$cancelEntityInteraction(PlayerInteractEntityC2SPacket packet, CallbackInfo ci, @Local(ordinal = 0) Entity entity) {
        if(DreamtwirlWorldAttachment.positionsAreInSeperateDreamtwirls(this.player.getWorld(), this.player.getPos(), entity.getPos())) {
            ci.cancel();
        }
    }
}
