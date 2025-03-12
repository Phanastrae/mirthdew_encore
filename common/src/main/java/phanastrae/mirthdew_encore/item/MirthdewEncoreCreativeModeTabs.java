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

import static net.minecraft.world.item.Items.*;
import static phanastrae.mirthdew_encore.item.MirthdewEncoreItems.*;

public class MirthdewEncoreCreativeModeTabs {
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

    public static final CreativeModeTab MIRTHDEW_ENCORE_TAB = XPlatInterface.INSTANCE.createCreativeModeTabBuilder()
            .icon(SPELL_CARD::getDefaultInstance)
            .title(Component.translatable("itemGroup.mirthdew_encore"))
            .build();
    public static final ResourceKey<CreativeModeTab> MIRTHDEW_ENCORE_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), MirthdewEncore.id("mirthdew_encore"));

    public static void init(BiConsumer<ResourceLocation, CreativeModeTab> r) {
        r.accept(id("mirthdew_encore"), MIRTHDEW_ENCORE_TAB);
    }

    public static void addItemToMirthdewEncoreTab(ItemLike item) {
        addItemToMirthdewEncoreTab(new ItemStack(item));
    }

    public static void addItemToMirthdewEncoreTab(ItemStack itemStack) {
        QUEUED_TAB_ITEMS.add(itemStack);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }

    public static void setupEntires(Helper helper) {
        // mirthdew tab
        addQueuedItems(helper);
        addMirthdewVialsToTab(helper, MIRTHDEW_ENCORE_KEY, VESPERBILE_BUCKET.getDefaultInstance());
        addAllSpellCardsToTab(helper, MIRTHDEW_ENCORE_KEY);

        // building blocks
        helper.addAfter(PURPUR_SLAB, BUILDING_BLOCKS,
                UNGUISHALE,
                UNGUISHALE_STAIRS,
                UNGUISHALE_SLAB,
                UNGUISHALE_WALL,

                UNGUISHALE_BRICKS,
                UNGUISHALE_BRICK_STAIRS,
                UNGUISHALE_BRICK_SLAB,
                UNGUISHALE_BRICK_WALL,

                UNGUISHALE_TILES,
                UNGUISHALE_TILE_STAIRS,
                UNGUISHALE_TILE_SLAB,
                UNGUISHALE_TILE_WALL,

                ONYXSCALE,

                GACHERIMM,

                ROUGH_GACHERIMM,
                ROUGH_GACHERIMM_STAIRS,
                ROUGH_GACHERIMM_SLAB,
                ROUGH_GACHERIMM_WALL,

                GACHERIMM_BRICKS,
                GACHERIMM_BRICK_STAIRS,
                GACHERIMM_BRICK_SLAB,
                GACHERIMM_BRICK_WALL,

                GACHERIMM_TILES,
                GACHERIMM_TILE_STAIRS,
                GACHERIMM_TILE_SLAB,
                GACHERIMM_TILE_WALL,

                POLISHED_GACHERIMM,
                POLISHED_GACHERIMM_STAIRS,
                POLISHED_GACHERIMM_SLAB,
                POLISHED_GACHERIMM_WALL,

                CUT_POLISHED_GACHERIMM,
                CUT_POLISHED_GACHERIMM_STAIRS,
                CUT_POLISHED_GACHERIMM_SLAB,

                REVERIME,
                REVERIME_STAIRS,
                REVERIME_SLAB,
                REVERIME_WALL,

                FROSTED_REVERIME,

                REVERIME_BRICKS,
                REVERIME_BRICK_STAIRS,
                REVERIME_BRICK_SLAB,
                REVERIME_BRICK_WALL,

                REVERIME_TILES,
                REVERIME_TILE_STAIRS,
                REVERIME_TILE_SLAB,
                REVERIME_TILE_WALL,

                POLISHED_REVERIME,
                POLISHED_REVERIME_STAIRS,
                POLISHED_REVERIME_SLAB,
                POLISHED_REVERIME_WALL,

                POLISHED_REVERIME_BRICKS,
                POLISHED_REVERIME_BRICK_STAIRS,
                POLISHED_REVERIME_BRICK_SLAB,
                POLISHED_REVERIME_BRICK_WALL,

                CUT_POLISHED_REVERIME,
                CUT_POLISHED_REVERIME_STAIRS,
                CUT_POLISHED_REVERIME_SLAB,

                SCARABRIM,
                SCARABRIM_STAIRS,
                SCARABRIM_SLAB,
                SCARABRIM_WALL,

                POLISHED_SCARABRIM,
                POLISHED_SCARABRIM_STAIRS,
                POLISHED_SCARABRIM_SLAB,
                POLISHED_SCARABRIM_WALL,

                SCARABRIM_BRICKS,
                SCARABRIM_BRICK_STAIRS,
                SCARABRIM_BRICK_SLAB,
                SCARABRIM_BRICK_WALL
        );
        helper.addAfter(WARPED_BUTTON, BUILDING_BLOCKS,
                CLINKERA_PLANKS,
                CLINKERA_STAIRS,
                CLINKERA_SLAB,
                CLINKERA_FENCE,
                CLINKERA_FENCE_GATE,
                CLINKERA_LATTICE,
                CLINKERA_DOOR,
                CLINKERA_PRESSURE_PLATE,
                CLINKERA_TRAPDOOR,
                CLINKERA_BUTTON,

                DECIDRHEUM_LOG,
                DECIDRHEUM_WOOD,
                STRIPPED_DECIDRHEUM_LOG,
                STRIPPED_DECIDRHEUM_WOOD,
                DECIDRHEUM_PLANKS,
                DECIDRHEUM_STAIRS,
                DECIDRHEUM_SLAB,
                DECIDRHEUM_FENCE,
                DECIDRHEUM_FENCE_GATE,
                DECIDRHEUM_LATTICE,
                DECIDRHEUM_DOOR,
                DECIDRHEUM_PRESSURE_PLATE,
                DECIDRHEUM_TRAPDOOR,
                DECIDRHEUM_BUTTON
        );
        helper.addAfter(WAXED_OXIDIZED_COPPER_BULB, BUILDING_BLOCKS,
                BACCHENITE_BLOCK,
                BACCHENITE_BRICKS,
                BACCHENITE_BRICK_STAIRS,
                BACCHENITE_BRICK_SLAB,
                BACCHENITE_BRICK_WALL,
                BACCHENITE_TILES,
                BACCHENITE_TILE_STAIRS,
                BACCHENITE_TILE_SLAB,
                BACCHENITE_TILE_WALL,

                PSYRITE_BLOCK,
                CUT_PSYRITE,
                CUT_PSYRITE_STAIRS,
                CUT_PSYRITE_SLAB,
                CHISELED_PSYRITE,
                PSYRITE_PILLAR,
                PSYRITE_GRATE,
                PSYRITE_GRATE_SLAB,
                PSYRITE_DOOR,
                PSYRITE_TRAPDOOR,
                PSYRITE_BARS,
                PSYRITE_LATTICE
        );

        // colored blocks
        helper.add(COLORED_BLOCKS,
                ORANGE_NOVACLAG,
                LIME_NOVACLAG,
                CYAN_NOVACLAG,
                MAGNETA_NOVACLAG,

                ORANGE_FOGHAIR,
                LIME_FOGHAIR,
                CYAN_FOGHAIR,
                MAGNETA_FOGHAIR
        );

        // natural blocks
        helper.addAfter(SCULK_SENSOR, NATURAL_BLOCKS,
                VERIC_DREAMSNARE,
                DREAMSEED
        );
        helper.addAfter(END_STONE, NATURAL_BLOCKS,
                UNGUISHALE,

                ONYXSCALE,
                RHEUMDAUBED_ONYXSCALE,

                GACHERIMM,
                ORANGE_NOVACLAG,
                LIME_NOVACLAG,
                CYAN_NOVACLAG,
                MAGNETA_NOVACLAG,

                REVERIME,
                FROSTED_REVERIME,
                ROSENGLACE,

                SCARABRIM,
                SUNFLECKED_SCARABRIM,
                CHALKTISSUE,
                FLAKING_CHALKTISSUE,
                SUNSLAKED_CHALKTISSUE
        );
        helper.addAfter(NETHER_SPROUTS, NATURAL_BLOCKS,
                RHEUMBRISTLES,

                ORANGE_FOGHAIR,
                LIME_FOGHAIR,
                CYAN_FOGHAIR,
                MAGNETA_FOGHAIR
        );
        helper.addAfter(WARPED_FUNGUS, NATURAL_BLOCKS,
                SOULSPOT_MUSHRHEUM
        );
        helper.addAfter(WARPED_STEM, NATURAL_BLOCKS,
                DECIDRHEUM_LOG
        );
        helper.addAfter(FLOWERING_AZALEA_LEAVES, NATURAL_BLOCKS,
                DECIDRHEUM_LEAVES
        );
        helper.addAfter(CHERRY_SAPLING, NATURAL_BLOCKS,
                DECIDRHEUM_SAPLING
        );
        helper.addAfter(ANCIENT_DEBRIS, NATURAL_BLOCKS,
                GACHERIMM_PSYRITE_ORE,
                SCARABRIM_PSYRITE_ORE,
                SUNSLAKED_PSYRITE_ORE
        );
        helper.addAfter(RAW_GOLD_BLOCK, NATURAL_BLOCKS,
                RAW_PSYRITE_BLOCK
        );
        helper.addAfter(AMETHYST_CLUSTER, NATURAL_BLOCKS,
                BACCHENITE_BLOCK
        );

        // functional blocks
        helper.add(FUNCTIONAL_BLOCKS,
                DREAMSEED,
                SLUMBERSOCKET,
                SLEEPY_EYE,
                SLUMBERING_EYE,

                GREATER_ACHERUNE,
                ACHERUNE_HOLLOW,
                WAKESIDE_RUNE,
                LYCHSEAL
        );

        // redstone blocks
        helper.addAfter(SCULK_SHRIEKER, REDSTONE_BLOCKS,
                VERIC_DREAMSNARE
        );

        // tools and utilities
        helper.addAfter(ENDER_EYE, TOOLS_AND_UTILITIES,
                SLEEPY_EYE,
                SLUMBERING_EYE,
                OCULAR_SOPORSTEW
        );
        helper.addAfter(MILK_BUCKET, TOOLS_AND_UTILITIES,
                VESPERBILE_BUCKET
        );

        // combat
        addAllSpellCardsToTab(helper, COMBAT);

        // food and drinks
        ItemStack prevStack = OMINOUS_BOTTLE.getDefaultInstance();
        prevStack.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, 4);
        addMirthdewVialsToTab(helper, FOOD_AND_DRINKS, prevStack);

        helper.addAfter(PUMPKIN_PIE, FOOD_AND_DRINKS,
                SPECTRAL_CANDY
        );
        helper.addAfter(DRIED_KELP, FOOD_AND_DRINKS,
                CLINKERA_SCRAPS
        );
        helper.addAfter(COOKED_RABBIT, FOOD_AND_DRINKS,
                PSYRITE_NUGGET
        );
        helper.addAfter(SPIDER_EYE, FOOD_AND_DRINKS,
                OCULAR_SOPORSTEW
        );

        // ingredients
        helper.addAfter(AMETHYST_SHARD, INGREDIENTS,
                BACCHENITE_SHARD
        );
        helper.addAfter(RAW_GOLD, INGREDIENTS,
                RAW_PSYRITE
        );
        helper.addAfter(GOLD_NUGGET, INGREDIENTS,
                PSYRITE_NUGGET
        );
        helper.addAfter(NETHERITE_INGOT, INGREDIENTS,
                PSYRITE_INGOT
        );

        // spawn eggs
        helper.add(SPAWN_EGGS,
                DREAMSPECK_SPAWN_EGG
        );

        // op blocks
        if(helper.operatorTabEnabled()) {
            helper.add(OP_BLOCKS,
                    DOOR_MARKER,
                    GREATER_ACHERUNE_MARKER,
                    LYCHSEAL_MARKER
            );
        }
    }

    private static void addQueuedItems(Helper helper) {
        helper.add(MIRTHDEW_ENCORE_KEY, QUEUED_TAB_ITEMS);
    }

    private static void addAllSpellCardsToTab(Helper helper, ResourceKey<CreativeModeTab> tabKey) {
        helper.forTabRun(tabKey, ((itemDisplayParameters, output) -> {
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

    private static void addMirthdewVialsToTab(Helper helper, ResourceKey<CreativeModeTab> tabKey, @Nullable ItemStack prevStack) {
        for (int i = 0; i <= 4; i++) {
            ItemStack itemStack = new ItemStack(MIRTHDEW_VIAL);
            itemStack.set(MirthdewEncoreDataComponentTypes.MIRTHDEW_VIAL_AMPLIFIER, i);

            if(prevStack == null) {
                helper.add(tabKey, itemStack);
            } else {
                helper.addAfter(prevStack, tabKey, itemStack);
            }
            prevStack = itemStack;
        }
    }

    public static abstract class Helper {
        public abstract void add(ResourceKey<CreativeModeTab> tabKey, ItemLike item);

        public abstract void add(ResourceKey<CreativeModeTab> tabKey, ItemLike... items);

        public abstract void add(ResourceKey<CreativeModeTab> tabKey, ItemStack item);

        public abstract void add(ResourceKey<CreativeModeTab> tabKey, Collection<ItemStack> items);

        public abstract void addAfter(ItemLike after, ResourceKey<CreativeModeTab> tabKey, ItemLike item);

        public abstract void addAfter(ItemStack after, ResourceKey<CreativeModeTab> tabKey, ItemStack item);

        public abstract void addAfter(ItemLike after, ResourceKey<CreativeModeTab> tabKey, ItemLike... items);

        public abstract void forTabRun(ResourceKey<CreativeModeTab> tabKey, BiConsumer<CreativeModeTab.ItemDisplayParameters, CreativeModeTab.Output> biConsumer);

        public abstract boolean operatorTabEnabled();
    }
}
