package phanastrae.mirthdew_encore.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.card_spell.CardSpell;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;
import phanastrae.mirthdew_encore.services.XPlatInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class MirthdewEncoreItemGroups {
    private static final ResourceKey<CreativeModeTab> BUILDING_BLOCKS = createKey("building_blocks");
    private static final ResourceKey<CreativeModeTab> COLORED_BLOCKS = createKey("colored_blocks");
    private static final ResourceKey<CreativeModeTab> NATURAL_BLOCKS = createKey("natural_blocks");
    private static final ResourceKey<CreativeModeTab> FUNCTIONAL_BLOCKS = createKey("functional_blocks");
    private static final ResourceKey<CreativeModeTab> REDSTONE_BLOCKS = createKey("redstone_blocks");
    private static final ResourceKey<CreativeModeTab> TOOLS_AND_UTILITIES = createKey("tools_and_utilities");
    private static final ResourceKey<CreativeModeTab> COMBAT = createKey("combat");
    private static final ResourceKey<CreativeModeTab> FOOD_AND_DRINKS = createKey("food_and_drinks");
    private static final ResourceKey<CreativeModeTab> INGREDIENTS = createKey("ingredients");
    private static final ResourceKey<CreativeModeTab> SPAWN_EGGS = createKey("spawn_eggs");
    private static final ResourceKey<CreativeModeTab> OP_BLOCKS = createKey("op_blocks");
    private static final ResourceKey<CreativeModeTab> INVENTORY = createKey("inventory");

    private static final List<ItemStack> QUEUED_TAB_ITEMS = new ArrayList<>();

    private static ResourceKey<CreativeModeTab> createKey(String name) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.withDefaultNamespace(name));
    }

    public static final CreativeModeTab MIRTHDEW_ENCORE_GROUP = XPlatInterface.INSTANCE.createCreativeModeTabBuilder()
            .icon(MirthdewEncoreItems.SPELL_CARD::getDefaultInstance)
            .title(Component.translatable("itemGroup.mirthdew_encore"))
            .build();
    public static final ResourceKey<CreativeModeTab> MIRTHDEW_ENCORE_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), MirthdewEncore.id("mirthdew_encore"));

    public static void init(BiConsumer<ResourceLocation, CreativeModeTab> r) {
        r.accept(id("mirthdew_encore"), MIRTHDEW_ENCORE_GROUP);
    }

    public static void addItemToMirthdewEncoreGroup(ItemLike item) {
        addItemToMirthdewEncoreGroup(new ItemStack(item));
    }

    public static void addItemToMirthdewEncoreGroup(ItemStack itemStack) {
        QUEUED_TAB_ITEMS.add(itemStack);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }

    public static void setupEntires(Helper helper) {
        ItemStack prevStack = Items.OMINOUS_BOTTLE.getDefaultInstance();
        prevStack.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, 4);
        addMirthdewVialsToGroup(helper, FOOD_AND_DRINKS, prevStack);

        addAllSpellCardsToGroup(helper, COMBAT);

        addQueuedItems(helper);
        addMirthdewVialsToGroup(helper, MIRTHDEW_ENCORE_KEY, null);
        addAllSpellCardsToGroup(helper, MIRTHDEW_ENCORE_KEY);

        helper.addAfter(Items.PUMPKIN_PIE, FOOD_AND_DRINKS,
                MirthdewEncoreItems.SPECTRAL_CANDY);

        helper.add(FUNCTIONAL_BLOCKS,
                MirthdewEncoreItems.DREAMSEED,
                MirthdewEncoreItems.SLUMBERSOCKET,
                MirthdewEncoreItems.SLUMBERING_EYE);

        helper.addAfter(Items.SCULK_SENSOR, NATURAL_BLOCKS,
                MirthdewEncoreItems.VERIC_DREAMSNARE,
                MirthdewEncoreItems.DREAMSEED);

        helper.addAfter(Items.SCULK_SHRIEKER, REDSTONE_BLOCKS,
                MirthdewEncoreItems.VERIC_DREAMSNARE);

        helper.addAfter(Items.ENDER_EYE, TOOLS_AND_UTILITIES,
                MirthdewEncoreItems.SLUMBERING_EYE);

        helper.add(SPAWN_EGGS,
                MirthdewEncoreItems.DREAMSPECK_SPAWN_EGG);
    }

    private static void addQueuedItems(Helper helper) {
        helper.add(MIRTHDEW_ENCORE_KEY, QUEUED_TAB_ITEMS);
    }

    private static void addAllSpellCardsToGroup(Helper helper, ResourceKey<CreativeModeTab> groupKey) {
        helper.forTabRun(groupKey, ((itemDisplayParameters, output) -> {
            itemDisplayParameters.holders().lookup(MirthdewEncoreRegistries.CARD_SPELL_KEY).ifPresent(registryWrapper -> addAllSpellCards(output, registryWrapper));
        }));
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

    private static void addMirthdewVialsToGroup(Helper helper, ResourceKey<CreativeModeTab> itemGroupKey, @Nullable ItemStack prevStack) {
        for (int i = 0; i <= 4; i++) {
            ItemStack itemStack = new ItemStack(MirthdewEncoreItems.MIRTHDEW_VIAL);
            itemStack.set(MirthdewEncoreDataComponentTypes.MIRTHDEW_VIAL_AMPLIFIER, i);

            if(prevStack == null) {
                helper.add(itemGroupKey, itemStack);
            } else {
                helper.addAfter(prevStack, itemGroupKey, itemStack);
            }
            prevStack = itemStack;
        }
    }

    public static abstract class Helper {
        public abstract void add(ResourceKey<CreativeModeTab> groupKey, ItemLike item);

        public abstract void add(ResourceKey<CreativeModeTab> groupKey, ItemLike... items);

        public abstract void add(ResourceKey<CreativeModeTab> groupKey, ItemStack item);

        public abstract void add(ResourceKey<CreativeModeTab> groupKey, Collection<ItemStack> items);

        public abstract void addAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, ItemLike item);

        public abstract void addAfter(ItemStack after, ResourceKey<CreativeModeTab> groupKey, ItemStack item);

        public abstract void addAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, ItemLike... items);

        public abstract void forTabRun(ResourceKey<CreativeModeTab> groupKey, BiConsumer<CreativeModeTab.ItemDisplayParameters, CreativeModeTab.Output> biConsumer);
    }
}
