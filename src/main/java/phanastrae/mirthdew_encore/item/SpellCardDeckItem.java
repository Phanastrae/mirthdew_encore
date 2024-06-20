package phanastrae.mirthdew_encore.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import phanastrae.mirthdew_encore.component.type.CardSpellComponent;
import phanastrae.mirthdew_encore.component.type.SpellDeckContentsComponent;

import java.util.List;

import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.CARD_SPELL;
import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.SPELL_DECK_CONTENTS;

public class SpellCardDeckItem extends SpellCardAbstractItem{

    public SpellCardDeckItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        SpellDeckContentsComponent spellDeckContents = stack.get(SPELL_DECK_CONTENTS);
        addStackToTooltip(stack, 1, tooltip);
        if (spellDeckContents != null) {
            int id = 2;
            for(ItemStack stack1 : spellDeckContents.iterate()) {
                addStackToTooltip(stack1, id, tooltip);
                id++;
            }
        }
    }

    public static void addStackToTooltip(ItemStack stack, int id, List<Text> tooltip) {
        CardSpellComponent cardSpellComponent = stack.get(CARD_SPELL);
        Text nameText;
        if(cardSpellComponent != null) {
            nameText = cardSpellComponent.cardSpell().value().description().copy().formatted(Formatting.AQUA).formatted(Formatting.ITALIC);
        } else {
            nameText = MirthdewEncoreItems.SPELL_CARD.getName().copy().formatted(Formatting.GRAY).formatted(Formatting.STRIKETHROUGH);
        }
        tooltip.add(Text.translatable("item.mirthdew_encore.spell_deck.id_card", id, nameText).formatted(Formatting.GOLD));
    }

    @Override
    public Text getName(ItemStack stack) {
        SpellDeckContentsComponent deckContentsComponent = stack.get(SPELL_DECK_CONTENTS);
        int cards = 1;
        if(deckContentsComponent != null) {
            cards += deckContentsComponent.size();
        }
        return Text.translatable("item.mirthdew_encore.spell_deck.deck_of_cards",
                this.getName(),
                cards
        );
    }
}
