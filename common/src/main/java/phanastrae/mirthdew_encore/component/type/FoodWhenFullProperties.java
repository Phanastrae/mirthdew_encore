package phanastrae.mirthdew_encore.component.type;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.TooltipProvider;
import phanastrae.mirthdew_encore.mixin.FoodPropertiesPossibleEffectAccessor;

import java.util.List;
import java.util.function.Consumer;

public record FoodWhenFullProperties(List<FoodProperties.PossibleEffect> effects, boolean showInTooltip) implements TooltipProvider {
    public static final Codec<FoodWhenFullProperties> DIRECT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            FoodProperties.PossibleEffect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(FoodWhenFullProperties::effects),
                            Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.TRUE).forGetter(FoodWhenFullProperties::showInTooltip)
                    )
                    .apply(instance, FoodWhenFullProperties::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, FoodWhenFullProperties> DIRECT_STREAM_CODEC = StreamCodec.composite(
            FoodProperties.PossibleEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),
            FoodWhenFullProperties::effects,
            ByteBufCodecs.BOOL,
            FoodWhenFullProperties::showInTooltip,
            FoodWhenFullProperties::new
    );

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag type) {
        if(this.showInTooltip) {
            tooltip.accept(Component.translatable("item.modifiers.mirthdew_encore.food_when_full").withStyle(ChatFormatting.GRAY));
            for(FoodProperties.PossibleEffect effect : this.effects) {
                if(effect.probability() == 1.0) {
                    List<MobEffectInstance> list = List.of(effect.effect());
                    PotionContents.addPotionTooltip(list, tooltip, 1.0F, context.tickRate());
                }
            }
            for(FoodProperties.PossibleEffect effect : this.effects) {
                if(effect.probability() != 1.0) {
                    float probability = effect.probability();
                    int percentage = Mth.floor(probability * 100);

                    tooltip.accept(Component.translatable("item.modifiers.mirthdew_encore.percentage_chance", percentage).withStyle(ChatFormatting.AQUA));
                    List<MobEffectInstance> list = List.of(effect.effect());
                    PotionContents.addPotionTooltip(list, tooltip, 1.0F, context.tickRate());
                }
            }
        }
    }

    public static class Builder {
        private final ImmutableList.Builder<FoodProperties.PossibleEffect> effects = ImmutableList.builder();
        private boolean showInTooltip = true;

        public FoodWhenFullProperties.Builder effect(MobEffectInstance effect, float probability) {
            this.effects.add(FoodPropertiesPossibleEffectAccessor.invokeInit(effect, probability));
            return this;
        }

        public FoodWhenFullProperties.Builder showInTooltip(boolean showInTooltip) {
            this.showInTooltip = showInTooltip;
            return this;
        }

        public FoodWhenFullProperties build() {
            return new FoodWhenFullProperties(this.effects.build(), this.showInTooltip);
        }
    }
}
