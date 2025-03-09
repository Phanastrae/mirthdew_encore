package phanastrae.mirthdew_encore.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlLevelAttachment;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

    @Shadow public ServerPlayer player;

    @Inject(method = "handleInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/border/WorldBorder;isWithinBounds(Lnet/minecraft/core/BlockPos;)Z"), cancellable = true)
    private void mirthdew_encore$cancelEntityInteraction(ServerboundInteractPacket packet, CallbackInfo ci, @Local(ordinal = 0) Entity entity) {
        if(DreamtwirlLevelAttachment.positionsAreInSeparateDreamtwirls(this.player.level(), this.player.position(), entity.position())) {
            ci.cancel();
        }
    }
}
