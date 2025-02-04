package phanastrae.mirthdew_encore.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.block.entity.DoorMarkerBlockEntity;
import phanastrae.mirthdew_encore.client.gui.screens.inventory.DoorMarkerEditScreen;
import phanastrae.mirthdew_encore.duck.PlayerDuckInterface;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreFluidTags;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer implements PlayerDuckInterface {

    @Shadow public Input input;

    @Shadow @Final protected Minecraft minecraft;

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0))
    private void mirthdewEncore$goDownInVesperbile(CallbackInfo ci) {
        if (this.getFluidHeight(MirthdewEncoreFluidTags.VESPERBILE) > 0.0 && this.input.shiftKeyDown && this.isAffectedByFluids()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.14F, 0.0));
        }
    }

    @Override
    public void mirthdew_encore$openDoorMarkerBlock(DoorMarkerBlockEntity doorMarkerBlockEntity) {
        this.minecraft.setScreen(new DoorMarkerEditScreen(doorMarkerBlockEntity));
    }
}
