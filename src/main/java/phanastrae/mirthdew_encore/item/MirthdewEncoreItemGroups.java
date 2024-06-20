package phanastrae.mirthdew_encore.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.card_spell.CardSpell;
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
        ItemGroupEvents.modifyEntriesEvent(MIRTHDEW_ENCORE_KEY).register(entries -> {
            ItemGroup.DisplayContext displayContext = entries.getContext();
            displayContext.lookup().getOptionalWrapper(MirthdewEncoreRegistries.CARD_SPELL_KEY).ifPresent(registryWrapper -> {
                addAllSpelLCards(entries, registryWrapper);
            });
        });
    }

    private static void addAllSpelLCards(
            ItemGroup.Entries entries, RegistryWrapper<CardSpell> registryWrapper
    ) {
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

    public static void addItemToGroup(RegistryKey<ItemGroup> groupKey, Item item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.add(item));
    }
}
