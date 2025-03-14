package phanastrae.mirthdew_encore.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.card_spell.PlayerEntityMirthData;
import phanastrae.mirthdew_encore.entity.PlayerEntityHungerData;
import phanastrae.mirthdew_encore.network.packet.FoodDebtUpdatePayload;
import phanastrae.mirthdew_encore.network.packet.MirthUpdatePayload;
import phanastrae.mirthdew_encore.services.XPlatInterface;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    @Unique private int mirthdew_encore$syncedFoodLevelDebt = -1;
    @Unique private long mirthdew_encore$syncedMirth = -1;

    public ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void mirthdew_encore$restoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        this.mirthdew_encore$syncedFoodLevelDebt = -1;
        this.mirthdew_encore$syncedMirth = -1;
    }

    @Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;sendActivePlayerEffects(Lnet/minecraft/server/level/ServerPlayer;)V", shift = At.Shift.AFTER))
    private void mirthdew_encore$afterChangingDimension(DimensionTransition teleportTarget, CallbackInfoReturnable<Entity> cir) {
        this.mirthdew_encore$syncedFoodLevelDebt = -1;
        this.mirthdew_encore$syncedMirth = -1;
    }

    @Inject(method = "doTick", at = @At("RETURN"))
    private void mirthdew_encore$playerTick(CallbackInfo ci) {
        ServerPlayer thisEntity = (ServerPlayer)(Object)this;
        PlayerEntityHungerData hungerData = PlayerEntityHungerData.fromPlayer(thisEntity);
        int foodLevelDebt = hungerData.getFoodLevelDebt();
        if(foodLevelDebt != this.mirthdew_encore$syncedFoodLevelDebt) {
            XPlatInterface.INSTANCE.sendPayload(thisEntity, new FoodDebtUpdatePayload(foodLevelDebt));
            this.mirthdew_encore$syncedFoodLevelDebt = foodLevelDebt;
        }

        PlayerEntityMirthData mirthData = PlayerEntityMirthData.fromPlayer(thisEntity);
        long mirth = mirthData.getMirth();
        if(mirth != this.mirthdew_encore$syncedMirth) {
            XPlatInterface.INSTANCE.sendPayload(thisEntity, new MirthUpdatePayload(mirth));
            this.mirthdew_encore$syncedMirth = mirth;
        }
    }
}
