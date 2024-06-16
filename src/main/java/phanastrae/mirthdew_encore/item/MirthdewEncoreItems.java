package phanastrae.mirthdew_encore.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import phanastrae.mirthdew_encore.MirthdewEncore;

import static phanastrae.mirthdew_encore.item.MirthdewEncoreItemGroups.MIRTHDEW_ENCORE_KEY;

public class MirthdewEncoreItems {

    public static final Item SPELL_CARD = new SpellCardSingularItem(settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item SPELL_DECK = new SpellCardDeckItem(settings().maxCount(1).rarity(Rarity.RARE));

    public static void init() {
        regWithIG(SPELL_CARD, "spell_card");
        register(SPELL_DECK, "spell_deck");
    }

    private static void regWithIG(Item item, String name) {
        register(item, name);
        MirthdewEncoreItemGroups.addItemToGroup(MIRTHDEW_ENCORE_KEY, item);
    }

    private static void register(Item item, String name) {
        Identifier identifier = MirthdewEncore.id(name);
        Registry.register(Registries.ITEM, identifier, item);
    }

    public static Item.Settings settings() {
        return new Item.Settings();
    }
}
