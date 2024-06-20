package phanastrae.mirthdew_encore.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import phanastrae.mirthdew_encore.card_spell.CardSpell;

import java.util.function.Consumer;

public record CardSpellComponent(RegistryEntry<CardSpell> cardSpell, boolean showInTooltip) implements TooltipAppender {
    public static final Codec<CardSpellComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            CardSpell.ENTRY_CODEC.fieldOf("card_spell").forGetter(CardSpellComponent::cardSpell),
                            Codec.BOOL.optionalFieldOf("show_in_tooltip", Boolean.TRUE).forGetter(CardSpellComponent::showInTooltip)
                    )
                    .apply(instance, CardSpellComponent::new)
    );
    public static final PacketCodec<RegistryByteBuf, CardSpellComponent> PACKET_CODEC = PacketCodec.tuple(
            CardSpell.ENTRY_PACKET_CODEC,
            CardSpellComponent::cardSpell,
            PacketCodecs.BOOL,
            CardSpellComponent::showInTooltip,
            CardSpellComponent::new
    );

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if(!this.showInTooltip) return;
        boolean advanced = type.isAdvanced();
        CardSpell cardSpell = this.cardSpell.value();
        CardSpell.Definition definition = cardSpell.definition();

        addValueToTooltip(tooltip, "mirthdew_encore.card_spell.tooltip.name", cardSpell.description(), Formatting.AQUA);
        addIntValueToTooltip(tooltip, "mirthdew_encore.card_spell.tooltip.mirth_cost", definition.mirthCost(), Formatting.LIGHT_PURPLE, Formatting.DARK_GRAY, Formatting.YELLOW, true);
        addIntValueToTooltip(tooltip, "mirthdew_encore.card_spell.tooltip.cast_delay_ms", definition.castDelayMs(), Formatting.RED, Formatting.DARK_GRAY, Formatting.GREEN, advanced);
        addIntValueToTooltip(tooltip, "mirthdew_encore.card_spell.tooltip.recharge_delay_ms", definition.rechargeDelayMs(), Formatting.RED, Formatting.DARK_GRAY, Formatting.GREEN, advanced);
        if(advanced) {
            addIntValueToTooltip(tooltip, "mirthdew_encore.card_spell.tooltip.input_count", definition.inputCount(), Formatting.AQUA, Formatting.DARK_GRAY, Formatting.DARK_RED, true);
        }
    }

    public void addIntValueToTooltip(Consumer<Text> tooltip, String translationKey, int value, Formatting positiveColor, Formatting zeroColor, Formatting negativeColor, boolean showIfZero) {
        if(value != 0 || showIfZero) {
            Formatting color = value > 0 ? positiveColor : (value == 0 ? zeroColor : negativeColor);
            addValueToTooltip(tooltip, translationKey, Text.literal(String.valueOf(value)), color);
        }
    }

    public void addValueToTooltip(Consumer<Text> tooltip, String translationKey, Text text, Formatting color) {
        Text valueText = text.copy().formatted(color);
        MutableText mutableText = Text.translatable(translationKey, valueText);
        Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
        tooltip.accept(mutableText);
    }

    public CardSpellComponent withShowInTooltip(boolean showInTooltip) {
        return new CardSpellComponent(this.cardSpell, showInTooltip);
    }
}
