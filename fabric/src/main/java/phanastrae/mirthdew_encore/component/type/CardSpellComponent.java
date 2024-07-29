package phanastrae.mirthdew_encore.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import phanastrae.mirthdew_encore.card_spell.CardSpell;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record CardSpellComponent(Holder<CardSpell> cardSpell, boolean showInTooltip) implements TooltipProvider {
    public static final Codec<CardSpellComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            CardSpell.ENTRY_CODEC.fieldOf("card_spell").forGetter(CardSpellComponent::cardSpell),
                            Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.TRUE).forGetter(CardSpellComponent::showInTooltip)
                    )
                    .apply(instance, CardSpellComponent::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, CardSpellComponent> PACKET_CODEC = StreamCodec.composite(
            CardSpell.ENTRY_PACKET_CODEC,
            CardSpellComponent::cardSpell,
            ByteBufCodecs.BOOL,
            CardSpellComponent::showInTooltip,
            CardSpellComponent::new
    );

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag type) {
        if(!this.showInTooltip) return;
        boolean advanced = type.isAdvanced();
        CardSpell cardSpell = this.cardSpell.value();
        CardSpell.Definition definition = cardSpell.definition();

        addValueToTooltip(tooltip, "mirthdew_encore.card_spell.tooltip.name", cardSpell.description(), ChatFormatting.AQUA);
        addIntValueToTooltip(tooltip, "mirthdew_encore.card_spell.tooltip.mirth_cost", definition.mirthCost(), ChatFormatting.LIGHT_PURPLE, ChatFormatting.DARK_GRAY, ChatFormatting.YELLOW, true);
        addIntValueToTooltip(tooltip, "mirthdew_encore.card_spell.tooltip.cast_delay_ms", definition.castDelayMs(), ChatFormatting.RED, ChatFormatting.DARK_GRAY, ChatFormatting.GREEN, advanced);
        addIntValueToTooltip(tooltip, "mirthdew_encore.card_spell.tooltip.recharge_delay_ms", definition.rechargeDelayMs(), ChatFormatting.RED, ChatFormatting.DARK_GRAY, ChatFormatting.GREEN, advanced);
        if(advanced) {
            addIntValueToTooltip(tooltip, "mirthdew_encore.card_spell.tooltip.input_count", definition.inputCount(), ChatFormatting.AQUA, ChatFormatting.DARK_GRAY, ChatFormatting.DARK_RED, true);
        }
    }

    public void addIntValueToTooltip(Consumer<Component> tooltip, String translationKey, int value, ChatFormatting positiveColor, ChatFormatting zeroColor, ChatFormatting negativeColor, boolean showIfZero) {
        if(value != 0 || showIfZero) {
            ChatFormatting color = value > 0 ? positiveColor : (value == 0 ? zeroColor : negativeColor);
            addValueToTooltip(tooltip, translationKey, Component.literal(String.valueOf(value)), color);
        }
    }

    public void addValueToTooltip(Consumer<Component> tooltip, String translationKey, Component text, ChatFormatting color) {
        Component valueText = text.copy().withStyle(color);
        MutableComponent mutableText = Component.translatable(translationKey, valueText);
        ComponentUtils.mergeStyles(mutableText, Style.EMPTY.withColor(ChatFormatting.GRAY));
        tooltip.accept(mutableText);
    }

    public CardSpellComponent withShowInTooltip(boolean showInTooltip) {
        return new CardSpellComponent(this.cardSpell, showInTooltip);
    }
}
