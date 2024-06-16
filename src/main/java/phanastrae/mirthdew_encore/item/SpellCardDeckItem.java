package phanastrae.mirthdew_encore.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import phanastrae.mirthdew_encore.component.type.SpellDeckContentsComponent;

import java.util.List;

import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.SPELL_DECK_CONTENTS;

public class SpellCardDeckItem extends SpellCardAbstractItem{

    public SpellCardDeckItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        SpellDeckContentsComponent spellDeckContents = stack.get(SPELL_DECK_CONTENTS);
        if (spellDeckContents != null) {
            int id = 2;
            for(ItemStack stack1 : spellDeckContents.iterate()) {
                tooltip.add(Text.translatable("item.mirthdew_encore.spell_deck.id_card", id, stack1.getName()));
                id++;
            }
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        return Text.translatable("item.mirthdew_encore.spell_deck.deck_of_card", Text.translatable("item.mirthdew_encore.spell_deck"), super.getName(stack));
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey(stack);
    }
}
