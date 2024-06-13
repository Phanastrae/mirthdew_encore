package phanastrae.mirthdew_encore.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.entity.PlayerEntityHungerData;
import phanastrae.mirthdew_encore.network.packet.FoodDebtUpdatePayload;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Unique private int mirthdew_encore$syncedFoodLevelDebt = -1;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void mirthdew_encore$copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        this.mirthdew_encore$syncedFoodLevelDebt = -1;
    }

    @Inject(method = "teleportTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendStatusEffects(Lnet/minecraft/server/network/ServerPlayerEntity;)V", shift = At.Shift.AFTER))
    private void mirthdew_encore$afterCrossDimTeleport(TeleportTarget teleportTarget, CallbackInfoReturnable<Entity> cir) {
        this.mirthdew_encore$syncedFoodLevelDebt = -1;
    }

    @Inject(method = "playerTick", at = @At("RETURN"))
    private void mirthdew_encore$playerTick(CallbackInfo ci) {
        ServerPlayerEntity thisEntity = (ServerPlayerEntity)(Object)this;
        PlayerEntityHungerData hungerData = PlayerEntityHungerData.fromPlayer(thisEntity);
        int foodLevelDebt = hungerData.getFoodLevelDebt();
        if(foodLevelDebt != this.mirthdew_encore$syncedFoodLevelDebt) {
            ServerPlayNetworking.send(thisEntity, new FoodDebtUpdatePayload(foodLevelDebt));
            this.mirthdew_encore$syncedFoodLevelDebt = foodLevelDebt;
        }
    }
}
