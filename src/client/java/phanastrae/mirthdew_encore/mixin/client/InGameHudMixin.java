package phanastrae.mirthdew_encore.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.entity.PlayerEntityHungerData;

import static phanastrae.mirthdew_encore.entity.effect.DreamyDietStatusEffect.*;
import static phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;getSaturationLevel()F", shift = At.Shift.BEFORE))
    private void mirthdew_encore$dreamyDietTextureSwap(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci,
                                                             @Local(ordinal = 0) LocalRef<Identifier> idLocRef1, @Local(ordinal = 1) LocalRef<Identifier> idLocRef2, @Local(ordinal = 2) LocalRef<Identifier> idLocRef3) {
        if(player.hasStatusEffect(DREAMY_DIET_ENTRY)) {
            idLocRef1.set(FOOD_EMPTY_DREAMY_DIET_TEXTURE);
            idLocRef2.set(FOOD_HALF_DREAMY_DIET_TEXTURE);
            idLocRef3.set(FOOD_FULL_DREAMY_DIET_TEXTURE);
        }
    }

    @Inject(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V", shift = At.Shift.AFTER))
    private void mirthdew_encore$renderFoodDebt(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci,
                                                @Local(ordinal = 3) int foodIconId, @Local(ordinal = 4) int topOffset, @Local(ordinal = 5) int rightOffset) {
        PlayerEntityHungerData hungerData = PlayerEntityHungerData.fromPlayer(player);
        int foodDebt = hungerData.getFoodLevelDebt();
        if(foodDebt > 0) {
            if(20 - 2 * foodIconId <= foodDebt) {
                context.drawGuiTexture(FOOD_DEBT_FULL_TEXTURE, rightOffset, topOffset, 9, 9);
            } else if(19 - 2 * foodIconId == foodDebt) {
                context.drawGuiTexture(FOOD_DEBT_HALF_TEXTURE, rightOffset, topOffset, 9, 9);
            }
        }
    }

    @Inject(method = "renderFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I", shift = At.Shift.AFTER))
    private void mirthdew_encore$dreamyDietPreventWiggling(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci,
                                                           @Local(ordinal = 4) LocalIntRef topOffsetRef) {
        if(player.hasStatusEffect(DREAMY_DIET_ENTRY)) {
            topOffsetRef.set(top);
        }
    }
}
