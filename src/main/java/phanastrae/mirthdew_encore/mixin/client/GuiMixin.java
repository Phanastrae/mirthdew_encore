package phanastrae.mirthdew_encore.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.card_spell.PlayerEntityMirthData;
import phanastrae.mirthdew_encore.entity.PlayerEntityHungerData;
import phanastrae.mirthdew_encore.item.SpellCardAbstractItem;

import static phanastrae.mirthdew_encore.entity.effect.DreamyDietStatusEffect.*;
import static phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow private ItemStack lastToolHighlight;

    @Shadow @Nullable protected abstract Player getCameraPlayer();

    @Shadow public abstract Font getFont();

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

    @Inject(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V", shift = At.Shift.AFTER))
    private void mirthdew_encore$renderMirthAmount(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }

        if(this.lastToolHighlight.isEmpty()) return;

        Player player = this.getCameraPlayer();
        if(player == null) return;

        if(this.lastToolHighlight.getItem() instanceof SpellCardAbstractItem) {
            long mirth = PlayerEntityMirthData.fromPlayer(player).getMirth();

            ChatFormatting color = mirth == 0 ? ChatFormatting.RED : ChatFormatting.LIGHT_PURPLE;
            MutableComponent mutableText = Component.translatable("gui.mirthdew_encore.mirth", Component.nullToEmpty(String.valueOf(mirth)).copy().withStyle(color)).withStyle(ChatFormatting.AQUA);

            int width = this.getFont().width(mutableText);
            int x = (context.guiWidth() - width) / 2;
            int y = context.guiHeight() - 59;
            if (!this.minecraft.gameMode.canHurtPlayer()) {
                y += 14;
            }

            y -= 14;

            context.drawStringWithBackdrop(this.getFont(), mutableText, x, y, width, FastColor.ARGB32.color(255, -1));
        }
    }
}
