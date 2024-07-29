package phanastrae.mirthdew_encore.item;

import phanastrae.mirthdew_encore.card_spell.CardSpell;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.type.CardSpellComponent;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class SpellCardSingularItem extends SpellCardAbstractItem {

    public SpellCardSingularItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        CardSpellComponent cardSpellComponent = stack.get(MirthdewEncoreDataComponentTypes.CARD_SPELL);
        if(cardSpellComponent != null) {
            cardSpellComponent.addToTooltip(context, tooltip::add, type);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        Component spellCardText = Component.translatable("item.mirthdew_encore.spell_card");
        CardSpellComponent cardSpellComponent = stack.get(MirthdewEncoreDataComponentTypes.CARD_SPELL);
        if(cardSpellComponent != null) {
            return Component.translatable("item.mirthdew_encore.spell_card.card_of_spell",
                    spellCardText,
                    cardSpellComponent.cardSpell().value().description().copy().withStyle(ChatFormatting.AQUA)
            );
        } else {
            return spellCardText;
        }
    }

    public static ItemStack forCardSpell(Holder.Reference<CardSpell> cardSpell) {
        ItemStack itemStack = new ItemStack(MirthdewEncoreItems.SPELL_CARD);
        itemStack.set(MirthdewEncoreDataComponentTypes.CARD_SPELL, new CardSpellComponent(cardSpell, true));
        return itemStack;
    }
}
