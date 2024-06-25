package phanastrae.mirthdew_encore.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.card_spell.CardSpell;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;

public class MirthdewEncoreItemGroups {

    public static final ItemGroup MIRTHDEW_ENCORE_GROUP = FabricItemGroup.builder()
            .icon(MirthdewEncoreItems.SPELL_CARD::getDefaultStack)
            .displayName(Text.translatable("itemGroup.mirthdew_encore"))
            .build();
    public static final RegistryKey<ItemGroup> MIRTHDEW_ENCORE_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), MirthdewEncore.id("mirthdew_encore"));

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, MirthdewEncore.id("mirthdew_encore"), MIRTHDEW_ENCORE_GROUP);
    }

    public static void setupEntires() {
        addMirthdewVialsToGroup(MIRTHDEW_ENCORE_KEY);
        addMirthdewVialsToGroup(ItemGroups.FOOD_AND_DRINK);
        addAllSpellCardsToGroup(MIRTHDEW_ENCORE_KEY);
        addAllSpellCardsToGroup(ItemGroups.COMBAT);

        addAfter(Items.PUMPKIN_PIE, ItemGroups.FOOD_AND_DRINK,
                MirthdewEncoreItems.SPECTRAL_CANDY);

        add(ItemGroups.FUNCTIONAL,
                MirthdewEncoreItems.DREAMSEED,
                MirthdewEncoreItems.SLUMBERSOCKET,
                MirthdewEncoreItems.SLUMBERING_EYE);

        addAfter(Items.SCULK_SENSOR, ItemGroups.NATURAL,
                MirthdewEncoreItems.VERIC_DREAMSNARE,
                MirthdewEncoreItems.DREAMSEED);

        addAfter(Items.SCULK_SHRIEKER, ItemGroups.REDSTONE,
                MirthdewEncoreItems.VERIC_DREAMSNARE);

        addAfter(Items.ENDER_EYE, ItemGroups.TOOLS,
                MirthdewEncoreItems.SLUMBERING_EYE);

        add(ItemGroups.SPAWN_EGGS,
                MirthdewEncoreItems.DREAMSPECK_SPAWN_EGG);
    }

    private static void addAllSpellCardsToGroup(RegistryKey<ItemGroup> itemGroupKey) {
        ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register(entries -> {
            ItemGroup.DisplayContext displayContext = entries.getContext();
            displayContext.lookup().getOptionalWrapper(MirthdewEncoreRegistries.CARD_SPELL_KEY).ifPresent(registryWrapper -> addAllSpellCards(entries, registryWrapper));
        });
    }

    private static void addAllSpellCards(ItemGroup.Entries entries, RegistryWrapper<CardSpell> registryWrapper) {
        registryWrapper.streamEntries()
                .map(
                        SpellCardSingularItem::forCardSpell
                )
                .forEach(itemStack -> {
                    if(!itemStack.isEmpty()) {
                        entries.add(itemStack);
                    }
                });
    }

    private static void addMirthdewVialsToGroup(RegistryKey<ItemGroup> itemGroupKey) {
        ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register(MirthdewEncoreItemGroups::addMirthdewVials);
    }

    private static void addMirthdewVials(FabricItemGroupEntries entries) {
        ItemStack prevStack = Items.OMINOUS_BOTTLE.getDefaultStack();
        prevStack.set(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, 4);
        for (int i = 0; i <= 4; i++) {
            ItemStack itemStack = new ItemStack(MirthdewEncoreItems.MIRTHDEW_VIAL);
            itemStack.set(MirthdewEncoreDataComponentTypes.MIRTHDEW_VIAL_AMPLIFIER, i);

            entries.addAfter(prevStack, itemStack);
            prevStack = itemStack;
        }
    }

    public static void add(RegistryKey<ItemGroup> groupKey, ItemConvertible item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.add(item));
    }

    public static void add(RegistryKey<ItemGroup> groupKey, ItemConvertible... items) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> {
            for(ItemConvertible item : items) {
                entries.add(item);
            }
        });
    }

    public static void addAfter(ItemConvertible after, RegistryKey<ItemGroup> groupKey, Item item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addAfter(after, item));
    }

    public static void addAfter(ItemConvertible after, RegistryKey<ItemGroup> groupKey, Item... items) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addAfter(after, items));
    }
}
