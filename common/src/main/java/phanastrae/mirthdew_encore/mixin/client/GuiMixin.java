package phanastrae.mirthdew_encore.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.entity.PlayerEntityHungerData;

import static phanastrae.mirthdew_encore.entity.effect.DreamyDietStatusEffect.*;
import static phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;getSaturationLevel()F", shift = At.Shift.BEFORE))
    private void mirthdew_encore$dreamyDietSwapOutTextures(GuiGraphics context, Player player, int top, int right, CallbackInfo ci,
                                                             @Local(ordinal = 0) LocalRef<ResourceLocation> idLocRef1, @Local(ordinal = 1) LocalRef<ResourceLocation> idLocRef2, @Local(ordinal = 2) LocalRef<ResourceLocation> idLocRef3) {
        if(player.hasEffect(DREAMY_DIET_ENTRY)) {
            idLocRef1.set(FOOD_EMPTY_DREAMY_DIET_TEXTURE);
            idLocRef2.set(FOOD_HALF_DREAMY_DIET_TEXTURE);
            idLocRef3.set(FOOD_FULL_DREAMY_DIET_TEXTURE);
        }
    }

    @Inject(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", shift = At.Shift.AFTER))
    private void mirthdew_encore$renderFoodDebt(GuiGraphics context, Player player, int top, int right, CallbackInfo ci,
                                                @Local(ordinal = 3) int foodIconId, @Local(ordinal = 4) int topOffset, @Local(ordinal = 5) int rightOffset) {
        PlayerEntityHungerData hungerData = PlayerEntityHungerData.fromPlayer(player);
        int foodDebt = hungerData.getFoodLevelDebt();
        if(foodDebt > 0) {
            if(20 - 2 * foodIconId <= foodDebt) {
                context.blitSprite(FOOD_DEBT_FULL_TEXTURE, rightOffset, topOffset, 9, 9);
            } else if(19 - 2 * foodIconId == foodDebt) {
                context.blitSprite(FOOD_DEBT_HALF_TEXTURE, rightOffset, topOffset, 9, 9);
            }
        }
    }

    @Inject(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I", shift = At.Shift.AFTER))
    private void mirthdew_encore$dreamyDietPreventWiggling(GuiGraphics context, Player player, int top, int right, CallbackInfo ci,
                                                           @Local(ordinal = 4) LocalIntRef topOffsetRef) {
        if(player.hasEffect(DREAMY_DIET_ENTRY)) {
            topOffsetRef.set(top);
        }
    }
}
