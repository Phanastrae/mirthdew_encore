package phanastrae.mirthdew_encore.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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
        addAllSpellCardsToGroup(MIRTHDEW_ENCORE_KEY);
        addAllSpellCardsToGroup(ItemGroups.COMBAT);
        addMirthdewVialsToGroup(ItemGroups.FOOD_AND_DRINK);

        addItemsToGroup(ItemGroups.FUNCTIONAL,
                MirthdewEncoreItems.DREAMSEED,
                MirthdewEncoreItems.SLUMBERSOCKET);

        addItemToGroupAfter(Items.SCULK_SENSOR, ItemGroups.NATURAL,
                MirthdewEncoreItems.VERIC_DREAMSNARE);
        addItemToGroup(ItemGroups.NATURAL,
                MirthdewEncoreItems.DREAMSEED);

        addItemToGroupAfter(Items.SCULK_SHRIEKER, ItemGroups.REDSTONE,
                MirthdewEncoreItems.VERIC_DREAMSNARE);

        addItemToGroup(ItemGroups.SPAWN_EGGS,
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
        ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register(entries -> {
            addMirthdewVials(entries, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
        });
    }

    private static void addMirthdewVials(ItemGroup.Entries entries, ItemGroup.StackVisibility visibility) {
        for (int i = 0; i <= 4; i++) {
            ItemStack itemStack = new ItemStack(MirthdewEncoreItems.MIRTHDEW_VIAL);
            itemStack.set(MirthdewEncoreDataComponentTypes.MIRTHDEW_VIAL_AMPLIFIER, i);
            entries.add(itemStack, visibility);
        }
    }

    public static void addItemToGroup(RegistryKey<ItemGroup> groupKey, ItemConvertible item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.add(item));
    }

    public static void addItemsToGroup(RegistryKey<ItemGroup> groupKey, ItemConvertible... items) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> {
            for(ItemConvertible item : items) {
                entries.add(item);
            }
        });
    }

    public static void addItemToGroupAfter(ItemConvertible after, RegistryKey<ItemGroup> groupKey, Item item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addAfter(after, item));
    }
}
