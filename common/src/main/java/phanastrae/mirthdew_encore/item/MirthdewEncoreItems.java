package phanastrae.mirthdew_encore.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;

import java.util.function.BiConsumer;

public class MirthdewEncoreItems {

    public static final Item DREAMSPECK_SPAWN_EGG = new SpawnEggItem(MirthdewEncoreEntityTypes.DREAMSPECK, 0xEDABF5, 0xA1507E, settings());

    public static final Item VERIC_DREAMSNARE = blockOf(MirthdewEncoreBlocks.VERIC_DREAMSNARE);
    public static final Item DREAMSEED = blockOf(MirthdewEncoreBlocks.DREAMSEED, settings().rarity(Rarity.UNCOMMON).fireResistant());
    public static final Item SLUMBERSOCKET = blockOf(MirthdewEncoreBlocks.SLUMBERSOCKET);
    public static final Item REVERIME = blockOf(MirthdewEncoreBlocks.REVERIME);
    public static final Item FROSTED_REVERIME = blockOf(MirthdewEncoreBlocks.FROSTED_REVERIME);
    public static final Item POLISHED_REVERIME = blockOf(MirthdewEncoreBlocks.POLISHED_REVERIME);
    public static final Item REVERIME_BRICKS = blockOf(MirthdewEncoreBlocks.REVERIME_BRICKS);
    public static final Item REVERIME_TILES = blockOf(MirthdewEncoreBlocks.REVERIME_TILES);
    public static final Item ROSENGLACE = blockOf(MirthdewEncoreBlocks.ROSENGLACE);
    public static final Item SCARABRIM = blockOf(MirthdewEncoreBlocks.SCARABRIM);
    public static final Item SUNFLECKED_SCARABRIM = blockOf(MirthdewEncoreBlocks.SUNFLECKED_SCARABRIM);

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
        rwig.accept(id("reverime"), REVERIME);
        rwig.accept(id("frosted_reverime"), FROSTED_REVERIME);
        rwig.accept(id("polished_reverime"), POLISHED_REVERIME);
        rwig.accept(id("reverime_bricks"), REVERIME_BRICKS);
        rwig.accept(id("reverime_tiles"), REVERIME_TILES);
        rwig.accept(id("rosenglace"), ROSENGLACE);
        rwig.accept(id("scarabrim"), SCARABRIM);
        rwig.accept(id("sunflecked_scarabrim"), SUNFLECKED_SCARABRIM);
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

    public static BlockItem blockOf(Block block) {
        return blockOf(block, settings());
    }

    public static BlockItem blockOf(Block block, Item.Properties properties) {
        return new BlockItem(block, properties);
    }
}
