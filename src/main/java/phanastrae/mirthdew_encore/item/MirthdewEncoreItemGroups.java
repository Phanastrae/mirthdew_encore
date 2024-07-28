package phanastrae.mirthdew_encore.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.card_spell.CardSpell;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;

public class MirthdewEncoreItemGroups {

    public static final CreativeModeTab MIRTHDEW_ENCORE_GROUP = FabricItemGroup.builder()
            .icon(MirthdewEncoreItems.SPELL_CARD::getDefaultInstance)
            .title(Component.translatable("itemGroup.mirthdew_encore"))
            .build();
    public static final ResourceKey<CreativeModeTab> MIRTHDEW_ENCORE_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), MirthdewEncore.id("mirthdew_encore"));

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MirthdewEncore.id("mirthdew_encore"), MIRTHDEW_ENCORE_GROUP);
    }

    public static void setupEntires() {
        addMirthdewVialsToGroup(MIRTHDEW_ENCORE_KEY);
        addMirthdewVialsToGroup(CreativeModeTabs.FOOD_AND_DRINKS);
        addAllSpellCardsToGroup(MIRTHDEW_ENCORE_KEY);
        addAllSpellCardsToGroup(CreativeModeTabs.COMBAT);

        addAfter(Items.PUMPKIN_PIE, CreativeModeTabs.FOOD_AND_DRINKS,
                MirthdewEncoreItems.SPECTRAL_CANDY);

        add(CreativeModeTabs.FUNCTIONAL_BLOCKS,
                MirthdewEncoreItems.DREAMSEED,
                MirthdewEncoreItems.SLUMBERSOCKET,
                MirthdewEncoreItems.SLUMBERING_EYE);

        addAfter(Items.SCULK_SENSOR, CreativeModeTabs.NATURAL_BLOCKS,
                MirthdewEncoreItems.VERIC_DREAMSNARE,
                MirthdewEncoreItems.DREAMSEED);

        addAfter(Items.SCULK_SHRIEKER, CreativeModeTabs.REDSTONE_BLOCKS,
                MirthdewEncoreItems.VERIC_DREAMSNARE);

        addAfter(Items.ENDER_EYE, CreativeModeTabs.TOOLS_AND_UTILITIES,
                MirthdewEncoreItems.SLUMBERING_EYE);

        add(CreativeModeTabs.SPAWN_EGGS,
                MirthdewEncoreItems.DREAMSPECK_SPAWN_EGG);
    }

    private static void addAllSpellCardsToGroup(ResourceKey<CreativeModeTab> itemGroupKey) {
        ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register(entries -> {
            CreativeModeTab.ItemDisplayParameters displayContext = entries.getContext();
            displayContext.holders().lookup(MirthdewEncoreRegistries.CARD_SPELL_KEY).ifPresent(registryWrapper -> addAllSpellCards(entries, registryWrapper));
        });
    }

    private static void addAllSpellCards(CreativeModeTab.Output entries, HolderLookup<CardSpell> registryWrapper) {
        registryWrapper.listElements()
                .map(
                        SpellCardSingularItem::forCardSpell
                )
                .forEach(itemStack -> {
                    if(!itemStack.isEmpty()) {
                        entries.accept(itemStack);
                    }
                });
    }

    private static void addMirthdewVialsToGroup(ResourceKey<CreativeModeTab> itemGroupKey) {
        ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register(MirthdewEncoreItemGroups::addMirthdewVials);
    }

    private static void addMirthdewVials(FabricItemGroupEntries entries) {
        ItemStack prevStack = Items.OMINOUS_BOTTLE.getDefaultInstance();
        prevStack.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, 4);
        for (int i = 0; i <= 4; i++) {
            ItemStack itemStack = new ItemStack(MirthdewEncoreItems.MIRTHDEW_VIAL);
            itemStack.set(MirthdewEncoreDataComponentTypes.MIRTHDEW_VIAL_AMPLIFIER, i);

            entries.addAfter(prevStack, itemStack);
            prevStack = itemStack;
        }
    }

    public static void add(ResourceKey<CreativeModeTab> groupKey, ItemLike item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.accept(item));
    }

    public static void add(ResourceKey<CreativeModeTab> groupKey, ItemLike... items) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> {
            for(ItemLike item : items) {
                entries.accept(item);
            }
        });
    }

    public static void addAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, Item item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addAfter(after, item));
    }

    public static void addAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, Item... items) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addAfter(after, items));
    }
}
