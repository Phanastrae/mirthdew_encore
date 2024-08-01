package phanastrae.mirthdew_encore.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;

import java.util.function.BiConsumer;

public class MirthdewEncoreItems {

    public static final Item DREAMSPECK_SPAWN_EGG = new SpawnEggItem(MirthdewEncoreEntityTypes.DREAMSPECK, 0xEDABF5, 0xA1507E, settings());
    public static final Item VERIC_DREAMSNARE = new BlockItem(MirthdewEncoreBlocks.VERIC_DREAMSNARE, settings());
    public static final Item DREAMSEED = new BlockItem(MirthdewEncoreBlocks.DREAMSEED, settings().rarity(Rarity.UNCOMMON).fireResistant());
    public static final Item SLUMBERSOCKET = new BlockItem(MirthdewEncoreBlocks.SLUMBERSOCKET, settings());

    public static final Item MIRTHDEW_VIAL = new MirthdewVialItem(
            settings().food(MirthdewVialItem.FOOD_COMPONENT).component(MirthdewEncoreDataComponentTypes.MIRTHDEW_VIAL_AMPLIFIER, 0).rarity(Rarity.UNCOMMON));

    public static final Item SLUMBERING_EYE = new SlumberingEyeItem(settings().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final Item SPECTRAL_CANDY = new SpectralCandyItem(settings().food(SpectralCandyItem.FOOD_COMPONENT));

    public static final Item SPELL_CARD = new SpellCardSingularItem(settings().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final Item SPELL_DECK = new SpellCardDeckItem(settings().stacksTo(1).rarity(Rarity.RARE));

    public static void init(BiConsumer<ResourceLocation, Item> r) {
        BiConsumer<ResourceLocation, Item> rwig = (rl, i) -> { // register with item group
            r.accept(rl, i);
            MirthdewEncoreItemGroups.addItemToMirthdewEncoreGroup(i);
        };

        rwig.accept(id("dreamspeck_spawn_egg"), DREAMSPECK_SPAWN_EGG);
        rwig.accept(id("veric_dreamsnare"), VERIC_DREAMSNARE);
        rwig.accept(id("dreamseed"), DREAMSEED);
        rwig.accept(id("slumbersocket"), SLUMBERSOCKET);
        r.accept(id("mirthdew_vial"), MIRTHDEW_VIAL);
        rwig.accept(id("slumbering_eye"), SLUMBERING_EYE);
        rwig.accept(id("spectral_candy"), SPECTRAL_CANDY);
        r.accept(id("spell_card"), SPELL_CARD);
        r.accept(id("spell_deck"), SPELL_DECK);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }

    public static Item.Properties settings() {
        return new Item.Properties();
    }
}
