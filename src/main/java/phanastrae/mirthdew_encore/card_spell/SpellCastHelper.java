package phanastrae.mirthdew_encore.card_spell;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.type.CardSpellComponent;
import phanastrae.mirthdew_encore.component.type.SpellDeckContentsComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.CARD_SPELL;

public class SpellCastHelper {

    public static List<RegistryEntry<CardSpell>> spellListFromStack(ItemStack itemStack) {
        List<RegistryEntry<CardSpell>> spellList = new ArrayList<>();

        CardSpellComponent cardSpellComponent = itemStack.get(CARD_SPELL);
        if(cardSpellComponent != null) {
            spellList.add(cardSpellComponent.cardSpell());
        }

        SpellDeckContentsComponent deckContentsComponent = itemStack.get(MirthdewEncoreDataComponentTypes.SPELL_DECK_CONTENTS);
        if(deckContentsComponent != null) {
            for(ItemStack stack : deckContentsComponent.iterate()) {
                CardSpellComponent cardSpellComponent2 = stack.get(CARD_SPELL);
                if(cardSpellComponent2 != null) {
                    spellList.add((cardSpellComponent2.cardSpell()));
                }
            }
        }

        return spellList;
    }

    public static List<SpellCast> castListFromSpellList(List<RegistryEntry<CardSpell>> spellList) {
        List<SpellCast.Builder> builderList = new ArrayList<>();
        Stack<SpellCast.Builder> buildStack = new Stack<>();
        for(RegistryEntry<CardSpell> cardSpellEntry : spellList) {
            SpellCast.Builder builder = new SpellCast.Builder(cardSpellEntry);
            if(buildStack.empty()) {
                builderList.add(builder);
                buildStack.push(builder);
            } else {
                buildStack.peek().addChild(builder);
                buildStack.push(builder);
            }

            while(!buildStack.empty() && !buildStack.peek().hasFreeSlots()) {
                buildStack.pop();
            }
        }

        List<SpellCast> spellCasts = new ArrayList<>();
        for(SpellCast.Builder builder : builderList) {
            spellCasts.add(builder.build());
        }

        return spellCasts;
    }

    public static List<SpellCast> castListFromStack(ItemStack stack) {
        return castListFromSpellList(spellListFromStack(stack));
    }
}
