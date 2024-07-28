package phanastrae.mirthdew_encore.item;

import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;

import static phanastrae.mirthdew_encore.item.MirthdewEncoreItemGroups.MIRTHDEW_ENCORE_KEY;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;

public class MirthdewEncoreItems {

    public static final Item DREAMSPECK_SPAWN_EGG = new SpawnEggItem(MirthdewEncoreEntityTypes.DREAM_SPECK, 0xEDABF5, 0xA1507E, settings());
    public static final Item VERIC_DREAMSNARE = new BlockItem(MirthdewEncoreBlocks.VERIC_DREAMSNARE, settings());
    public static final Item DREAMSEED = new BlockItem(MirthdewEncoreBlocks.DREAMSEED, settings().rarity(Rarity.UNCOMMON).fireResistant());
    public static final Item SLUMBERSOCKET = new BlockItem(MirthdewEncoreBlocks.SLUMBERSOCKET, settings());

    public static final Item MIRTHDEW_VIAL = new MirthdewVialItem(
            settings().food(MirthdewVialItem.FOOD_COMPONENT).component(MirthdewEncoreDataComponentTypes.MIRTHDEW_VIAL_AMPLIFIER, 0).rarity(Rarity.UNCOMMON));

    public static final Item SLUMBERING_EYE = new SlumberingEyeItem(settings().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final Item SPECTRAL_CANDY = new SpectralCandyItem(settings().food(SpectralCandyItem.FOOD_COMPONENT));

    public static final Item SPELL_CARD = new SpellCardSingularItem(settings().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final Item SPELL_DECK = new SpellCardDeckItem(settings().stacksTo(1).rarity(Rarity.RARE));

    public static void init() {
        regWithIG(DREAMSPECK_SPAWN_EGG, "dreamspeck_spawn_egg");
        regWithIG(VERIC_DREAMSNARE, "veric_dreamsnare");
        regWithIG(DREAMSEED, "dreamseed");
        regWithIG(SLUMBERSOCKET, "slumbersocket");
        register(MIRTHDEW_VIAL, "mirthdew_vial");
        regWithIG(SLUMBERING_EYE, "slumbering_eye");
        regWithIG(SPECTRAL_CANDY, "spectral_candy");
        register(SPELL_CARD, "spell_card");
        register(SPELL_DECK, "spell_deck");
    }

    private static void regWithIG(Item item, String name) {
        register(item, name);
        MirthdewEncoreItemGroups.add(MIRTHDEW_ENCORE_KEY, item);
    }

    private static void register(Item item, String name) {
        ResourceLocation identifier = MirthdewEncore.id(name);
        Registry.register(BuiltInRegistries.ITEM, identifier, item);
    }

    public static Item.Properties settings() {
        return new Item.Properties();
    }
}
