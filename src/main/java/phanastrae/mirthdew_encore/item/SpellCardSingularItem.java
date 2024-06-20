package phanastrae.mirthdew_encore.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import phanastrae.mirthdew_encore.card_spell.CardSpell;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.type.CardSpellComponent;

import java.util.List;

public class SpellCardSingularItem extends SpellCardAbstractItem {

    public SpellCardSingularItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        CardSpellComponent cardSpellComponent = stack.get(MirthdewEncoreDataComponentTypes.CARD_SPELL);
        if(cardSpellComponent != null) {
            cardSpellComponent.appendTooltip(context, tooltip::add, type);
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        Text spellCardText = Text.translatable("item.mirthdew_encore.spell_card");
        CardSpellComponent cardSpellComponent = stack.get(MirthdewEncoreDataComponentTypes.CARD_SPELL);
        if(cardSpellComponent != null) {
            return Text.translatable("item.mirthdew_encore.spell_card.card_of_spell",
                    spellCardText,
                    cardSpellComponent.cardSpell().value().description().copy().formatted(Formatting.AQUA)
            );
        } else {
            return spellCardText;
        }
    }

    public static ItemStack forCardSpell(RegistryEntry.Reference<CardSpell> cardSpell) {
        ItemStack itemStack = new ItemStack(MirthdewEncoreItems.SPELL_CARD);
        itemStack.set(MirthdewEncoreDataComponentTypes.CARD_SPELL, new CardSpellComponent(cardSpell, true));
        return itemStack;
    }
}
