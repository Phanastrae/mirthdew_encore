package phanastrae.mirthdew_encore.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.GameMode;
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

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow private ItemStack currentStack;

    @Shadow @Nullable protected abstract PlayerEntity getCameraPlayer();

    @Shadow public abstract TextRenderer getTextRenderer();

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

    @Inject(method = "renderMainHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
    private void mirthdew_encore$renderMirthAmount(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            return;
        }

        if(this.currentStack.isEmpty()) return;

        PlayerEntity player = this.getCameraPlayer();
        if(player == null) return;

        if(this.currentStack.getItem() instanceof SpellCardAbstractItem) {
            long mirth = PlayerEntityMirthData.fromPlayer(player).getMirth();

            Formatting color = mirth == 0 ? Formatting.RED : Formatting.LIGHT_PURPLE;
            MutableText mutableText = Text.translatable("gui.mirthdew_encore.mirth", Text.of(String.valueOf(mirth)).copy().formatted(color)).formatted(Formatting.AQUA);

            int width = this.getTextRenderer().getWidth(mutableText);
            int x = (context.getScaledWindowWidth() - width) / 2;
            int y = context.getScaledWindowHeight() - 59;
            if (!this.client.interactionManager.hasStatusBars()) {
                y += 14;
            }

            y -= 14;

            context.drawTextWithBackground(this.getTextRenderer(), mutableText, x, y, width, ColorHelper.Argb.withAlpha(255, -1));
        }
    }
}
