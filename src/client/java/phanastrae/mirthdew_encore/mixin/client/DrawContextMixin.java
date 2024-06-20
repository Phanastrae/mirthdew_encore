package phanastrae.mirthdew_encore.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
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

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color);

    @Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
    private void mirthdew_encore$spellDeckCount(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci,
                                                @Local(ordinal = 0, argsOnly = true) LocalRef<String> stringLocalRef) {
        if(stack.isOf(MirthdewEncoreItems.SPELL_DECK) && stack.getCount() == 1) {
            SpellDeckContentsComponent spellDeckContentsComponent = stack.get(SPELL_DECK_CONTENTS);
            if (spellDeckContentsComponent != null) {
                int size = 1 + spellDeckContentsComponent.size();
                stringLocalRef.set(Formatting.AQUA.toString() + size);
            }
        }
    }

    @Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift = At.Shift.BEFORE))
    private void mirthdew_encore$drawSpellCooldown(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        ClientWorld world = this.client.world;
        if(world != null) {
            SpellChargeComponent spellChargeComponent = stack.get(SPELL_CHARGE);
            if(spellChargeComponent != null) {
                long worldTime = world.getTime();
                float tickDelta = this.client.getRenderTickCounter().getTickDelta(true);

                float cooldown = spellChargeComponent.getRemainingCooldown(worldTime, tickDelta);

                if (cooldown > 0.0F) {
                    int y1 = y + MathHelper.floor(16.0F * (1.0F - cooldown));
                    int y2 = y1 + MathHelper.ceil(16.0F * cooldown);
                    this.fill(RenderLayer.getGuiOverlay(), x, y1, x + 16, y2, 0x7FFF9FFF);
                }
            }
        }
    }
}
