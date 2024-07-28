package phanastrae.mirthdew_encore.item;

import phanastrae.mirthdew_encore.component.type.CardSpellComponent;
import phanastrae.mirthdew_encore.component.type.SpellDeckContentsComponent;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.CARD_SPELL;
import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.SPELL_DECK_CONTENTS;

public class SpellCardDeckItem extends SpellCardAbstractItem{

    public SpellCardDeckItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
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

    public static void addStackToTooltip(ItemStack stack, int id, List<Component> tooltip) {
        CardSpellComponent cardSpellComponent = stack.get(CARD_SPELL);
        Component nameText;
        if(cardSpellComponent != null) {
            nameText = cardSpellComponent.cardSpell().value().description().copy().withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC);
        } else {
            nameText = MirthdewEncoreItems.SPELL_CARD.getDescription().copy().withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.STRIKETHROUGH);
        }
        tooltip.add(Component.translatable("item.mirthdew_encore.spell_deck.id_card", id, nameText).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public Component getName(ItemStack stack) {
        SpellDeckContentsComponent deckContentsComponent = stack.get(SPELL_DECK_CONTENTS);
        int cards = 1;
        if(deckContentsComponent != null) {
            cards += deckContentsComponent.size();
        }
        return Component.translatable("item.mirthdew_encore.spell_deck.deck_of_cards",
                this.getDescription(),
                cards
        );
    }
}
