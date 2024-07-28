package phanastrae.mirthdew_encore.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.component.type.SpellChargeComponent;
import phanastrae.mirthdew_encore.component.type.SpellDeckContentsComponent;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.SPELL_CHARGE;
import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.SPELL_DECK_CONTENTS;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow public abstract void fill(RenderType layer, int x1, int y1, int x2, int y2, int color);

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
    private void mirthdew_encore$renderSpellDeckCount(Font textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci,
                                                @Local(ordinal = 0, argsOnly = true) LocalRef<String> stringLocalRef) {
        if(stack.is(MirthdewEncoreItems.SPELL_DECK) && stack.getCount() == 1) {
            SpellDeckContentsComponent spellDeckContentsComponent = stack.get(SPELL_DECK_CONTENTS);
            if (spellDeckContentsComponent != null) {
                int size = 1 + spellDeckContentsComponent.size();
                stringLocalRef.set(ChatFormatting.AQUA.toString() + size);
            }
        }
    }

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", shift = At.Shift.BEFORE))
    private void mirthdew_encore$renderSpellCooldown(Font textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        ClientLevel world = this.minecraft.level;
        if(world != null) {
            SpellChargeComponent spellChargeComponent = stack.get(SPELL_CHARGE);
            if(spellChargeComponent != null) {
                long worldTime = world.getGameTime();
                float tickDelta = this.minecraft.getTimer().getGameTimeDeltaPartialTick(true);

                float cooldown = spellChargeComponent.getRemainingCooldown(worldTime, tickDelta);

                if (cooldown > 0.0F) {
                    int y1 = y + Mth.floor(16.0F * (1.0F - cooldown));
                    int y2 = y1 + Mth.ceil(16.0F * cooldown);
                    this.fill(RenderType.guiOverlay(), x, y1, x + 16, y2, 0x7FFF9FFF);
                }
            }
        }
    }
}
