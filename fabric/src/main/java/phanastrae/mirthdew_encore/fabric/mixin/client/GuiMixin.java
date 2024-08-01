package phanastrae.mirthdew_encore.fabric.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.client.MirthdewEncoreClient;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderHotbarAndDecorations", at = @At(value = "RETURN"))
    private void mirthdew_encore$renderMirthAmount(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        MirthdewEncoreClient.renderMirthOverlay(Minecraft.getInstance(), context);
    }
}
