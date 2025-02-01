package phanastrae.mirthdew_encore.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.type.FoodWhenFullProperties;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;
import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;
import phanastrae.mirthdew_encore.fluid.MirthdewEncoreFluids;

import java.util.function.BiConsumer;

public class MirthdewEncoreItems {

    public static final Item DREAMSPECK_SPAWN_EGG = new SpawnEggItem(MirthdewEncoreEntityTypes.DREAMSPECK, 0xEDABF5, 0xA1507E, settings());

    public static final Item VERIC_DREAMSNARE = blockOf(MirthdewEncoreBlocks.VERIC_DREAMSNARE);
    public static final Item DREAMSEED = blockOf(MirthdewEncoreBlocks.DREAMSEED, settings().rarity(Rarity.UNCOMMON).fireResistant());
    public static final Item SLUMBERSOCKET = blockOf(MirthdewEncoreBlocks.SLUMBERSOCKET);

    public static final Item BACCHENITE_BLOCK = blockOf(MirthdewEncoreBlocks.BACCHENITE_BLOCK);

    public static final Item BACCHENITE_BRICKS = blockOf(MirthdewEncoreBlocks.BACCHENITE_BRICKS);
    public static final Item BACCHENITE_BRICK_STAIRS = blockOf(MirthdewEncoreBlocks.BACCHENITE_BRICK_STAIRS);
    public static final Item BACCHENITE_BRICK_SLAB = blockOf(MirthdewEncoreBlocks.BACCHENITE_BRICK_SLAB);
    public static final Item BACCHENITE_BRICK_WALL = blockOf(MirthdewEncoreBlocks.BACCHENITE_BRICK_WALL);

    public static final Item BACCHENITE_TILES = blockOf(MirthdewEncoreBlocks.BACCHENITE_TILES);
    public static final Item BACCHENITE_TILE_STAIRS = blockOf(MirthdewEncoreBlocks.BACCHENITE_TILE_STAIRS);
    public static final Item BACCHENITE_TILE_SLAB = blockOf(MirthdewEncoreBlocks.BACCHENITE_TILE_SLAB);
    public static final Item BACCHENITE_TILE_WALL = blockOf(MirthdewEncoreBlocks.BACCHENITE_TILE_WALL);

    public static final Item UNGUISHALE = blockOf(MirthdewEncoreBlocks.UNGUISHALE);
    public static final Item UNGUISHALE_STAIRS = blockOf(MirthdewEncoreBlocks.UNGUISHALE_STAIRS);
    public static final Item UNGUISHALE_SLAB = blockOf(MirthdewEncoreBlocks.UNGUISHALE_SLAB);
    public static final Item UNGUISHALE_WALL = blockOf(MirthdewEncoreBlocks.UNGUISHALE_WALL);

    public static final Item UNGUISHALE_BRICKS = blockOf(MirthdewEncoreBlocks.UNGUISHALE_BRICKS);
    public static final Item UNGUISHALE_BRICK_STAIRS = blockOf(MirthdewEncoreBlocks.UNGUISHALE_BRICK_STAIRS);
    public static final Item UNGUISHALE_BRICK_SLAB = blockOf(MirthdewEncoreBlocks.UNGUISHALE_BRICK_SLAB);
    public static final Item UNGUISHALE_BRICK_WALL = blockOf(MirthdewEncoreBlocks.UNGUISHALE_BRICK_WALL);

    public static final Item UNGUISHALE_TILES = blockOf(MirthdewEncoreBlocks.UNGUISHALE_TILES);
    public static final Item UNGUISHALE_TILE_STAIRS = blockOf(MirthdewEncoreBlocks.UNGUISHALE_TILE_STAIRS);
    public static final Item UNGUISHALE_TILE_SLAB = blockOf(MirthdewEncoreBlocks.UNGUISHALE_TILE_SLAB);
    public static final Item UNGUISHALE_TILE_WALL = blockOf(MirthdewEncoreBlocks.UNGUISHALE_TILE_WALL);

    public static final Item CLINKERA_PLANKS = blockOf(MirthdewEncoreBlocks.CLINKERA_PLANKS);
    public static final Item CLINKERA_STAIRS = blockOf(MirthdewEncoreBlocks.CLINKERA_STAIRS);
    public static final Item CLINKERA_SLAB = blockOf(MirthdewEncoreBlocks.CLINKERA_SLAB);
    public static final Item CLINKERA_FENCE = blockOf(MirthdewEncoreBlocks.CLINKERA_FENCE);
    public static final Item CLINKERA_FENCE_GATE = blockOf(MirthdewEncoreBlocks.CLINKERA_FENCE_GATE);
    public static final Item CLINKERA_LATTICE = blockOf(MirthdewEncoreBlocks.CLINKERA_LATTICE);
    public static final Item CLINKERA_DOOR = new DoubleHighBlockItem(MirthdewEncoreBlocks.CLINKERA_DOOR, new Item.Properties());
    public static final Item CLINKERA_TRAPDOOR = blockOf(MirthdewEncoreBlocks.CLINKERA_TRAPDOOR);
    public static final Item CLINKERA_PRESSURE_PLATE = blockOf(MirthdewEncoreBlocks.CLINKERA_PRESSURE_PLATE);
    public static final Item CLINKERA_BUTTON = blockOf(MirthdewEncoreBlocks.CLINKERA_BUTTON);

    public static final Item ONYXSCALE = blockOf(MirthdewEncoreBlocks.ONYXSCALE);
    public static final Item RHEUMDAUBED_ONYXSCALE = blockOf(MirthdewEncoreBlocks.RHEUMDAUBED_ONYXSCALE);

    public static final Item RHEUMBRISTLES = blockOf(MirthdewEncoreBlocks.RHEUMBRISTLES);
    public static final Item SOULSPOT_MUSHRHEUM = blockOf(MirthdewEncoreBlocks.SOULSPOT_MUSHRHEUM);

    public static final Item DECIDRHEUM_LOG = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_LOG);
    public static final Item DECIDRHEUM_WOOD = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_WOOD);
    public static final Item STRIPPED_DECIDRHEUM_LOG = blockOf(MirthdewEncoreBlocks.STRIPPED_DECIDRHEUM_LOG);
    public static final Item STRIPPED_DECIDRHEUM_WOOD = blockOf(MirthdewEncoreBlocks.STRIPPED_DECIDRHEUM_WOOD);

    public static final Item DECIDRHEUM_PLANKS = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_PLANKS);
    public static final Item DECIDRHEUM_STAIRS = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_STAIRS);
    public static final Item DECIDRHEUM_SLAB = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_SLAB);
    public static final Item DECIDRHEUM_FENCE = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_FENCE);
    public static final Item DECIDRHEUM_FENCE_GATE = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_FENCE_GATE);
    public static final Item DECIDRHEUM_LATTICE = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_LATTICE);
    public static final Item DECIDRHEUM_DOOR = new DoubleHighBlockItem(MirthdewEncoreBlocks.DECIDRHEUM_DOOR, new Item.Properties());
    public static final Item DECIDRHEUM_TRAPDOOR = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_TRAPDOOR);
    public static final Item DECIDRHEUM_PRESSURE_PLATE = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_PRESSURE_PLATE);
    public static final Item DECIDRHEUM_BUTTON = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_BUTTON);
    public static final Item DECIDRHEUM_LEAVES = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_LEAVES);
    public static final Item DECIDRHEUM_SAPLING = blockOf(MirthdewEncoreBlocks.DECIDRHEUM_SAPLING);

    public static final Item GACHERIMM = blockOf(MirthdewEncoreBlocks.GACHERIMM);

    public static final Item ORANGE_NOVACLAG = blockOf(MirthdewEncoreBlocks.ORANGE_NOVACLAG);
    public static final Item LIME_NOVACLAG = blockOf(MirthdewEncoreBlocks.LIME_NOVACLAG);
    public static final Item CYAN_NOVACLAG = blockOf(MirthdewEncoreBlocks.CYAN_NOVACLAG);
    public static final Item MAGNETA_NOVACLAG = blockOf(MirthdewEncoreBlocks.MAGNETA_NOVACLAG);

    public static final Item ORANGE_FOGHAIR = blockOf(MirthdewEncoreBlocks.ORANGE_FOGHAIR);
    public static final Item LIME_FOGHAIR = blockOf(MirthdewEncoreBlocks.LIME_FOGHAIR);
    public static final Item CYAN_FOGHAIR = blockOf(MirthdewEncoreBlocks.CYAN_FOGHAIR);
    public static final Item MAGNETA_FOGHAIR = blockOf(MirthdewEncoreBlocks.MAGNETA_FOGHAIR);

    public static final Item ROUGH_GACHERIMM = blockOf(MirthdewEncoreBlocks.ROUGH_GACHERIMM);
    public static final Item ROUGH_GACHERIMM_STAIRS = blockOf(MirthdewEncoreBlocks.ROUGH_GACHERIMM_STAIRS);
    public static final Item ROUGH_GACHERIMM_SLAB = blockOf(MirthdewEncoreBlocks.ROUGH_GACHERIMM_SLAB);
    public static final Item ROUGH_GACHERIMM_WALL = blockOf(MirthdewEncoreBlocks.ROUGH_GACHERIMM_WALL);

    public static final Item GACHERIMM_BRICKS = blockOf(MirthdewEncoreBlocks.GACHERIMM_BRICKS);
    public static final Item GACHERIMM_BRICK_STAIRS = blockOf(MirthdewEncoreBlocks.GACHERIMM_BRICK_STAIRS);
    public static final Item GACHERIMM_BRICK_SLAB = blockOf(MirthdewEncoreBlocks.GACHERIMM_BRICK_SLAB);
    public static final Item GACHERIMM_BRICK_WALL = blockOf(MirthdewEncoreBlocks.GACHERIMM_BRICK_WALL);

    public static final Item GACHERIMM_TILES = blockOf(MirthdewEncoreBlocks.GACHERIMM_TILES);
    public static final Item GACHERIMM_TILE_STAIRS = blockOf(MirthdewEncoreBlocks.GACHERIMM_TILE_STAIRS);
    public static final Item GACHERIMM_TILE_SLAB = blockOf(MirthdewEncoreBlocks.GACHERIMM_TILE_SLAB);
    public static final Item GACHERIMM_TILE_WALL = blockOf(MirthdewEncoreBlocks.GACHERIMM_TILE_WALL);

    public static final Item POLISHED_GACHERIMM = blockOf(MirthdewEncoreBlocks.POLISHED_GACHERIMM);
    public static final Item POLISHED_GACHERIMM_STAIRS = blockOf(MirthdewEncoreBlocks.POLISHED_GACHERIMM_STAIRS);
    public static final Item POLISHED_GACHERIMM_SLAB = blockOf(MirthdewEncoreBlocks.POLISHED_GACHERIMM_SLAB);
    public static final Item POLISHED_GACHERIMM_WALL = blockOf(MirthdewEncoreBlocks.POLISHED_GACHERIMM_WALL);

    public static final Item CUT_POLISHED_GACHERIMM = blockOf(MirthdewEncoreBlocks.CUT_POLISHED_GACHERIMM);
    public static final Item CUT_POLISHED_GACHERIMM_STAIRS = blockOf(MirthdewEncoreBlocks.CUT_POLISHED_GACHERIMM_STAIRS);
    public static final Item CUT_POLISHED_GACHERIMM_SLAB = blockOf(MirthdewEncoreBlocks.CUT_POLISHED_GACHERIMM_SLAB);

    public static final Item REVERIME = blockOf(MirthdewEncoreBlocks.REVERIME);
    public static final Item REVERIME_STAIRS = blockOf(MirthdewEncoreBlocks.REVERIME_STAIRS);
    public static final Item REVERIME_SLAB = blockOf(MirthdewEncoreBlocks.REVERIME_SLAB);
    public static final Item REVERIME_WALL = blockOf(MirthdewEncoreBlocks.REVERIME_WALL);

    public static final Item FROSTED_REVERIME = blockOf(MirthdewEncoreBlocks.FROSTED_REVERIME);

    public static final Item REVERIME_BRICKS = blockOf(MirthdewEncoreBlocks.REVERIME_BRICKS);
    public static final Item REVERIME_BRICK_STAIRS = blockOf(MirthdewEncoreBlocks.REVERIME_BRICK_STAIRS);
    public static final Item REVERIME_BRICK_SLAB = blockOf(MirthdewEncoreBlocks.REVERIME_BRICK_SLAB);
    public static final Item REVERIME_BRICK_WALL = blockOf(MirthdewEncoreBlocks.REVERIME_BRICK_WALL);

    public static final Item REVERIME_TILES = blockOf(MirthdewEncoreBlocks.REVERIME_TILES);
    public static final Item REVERIME_TILE_STAIRS = blockOf(MirthdewEncoreBlocks.REVERIME_TILE_STAIRS);
    public static final Item REVERIME_TILE_SLAB = blockOf(MirthdewEncoreBlocks.REVERIME_TILE_SLAB);
    public static final Item REVERIME_TILE_WALL = blockOf(MirthdewEncoreBlocks.REVERIME_TILE_WALL);

    public static final Item POLISHED_REVERIME = blockOf(MirthdewEncoreBlocks.POLISHED_REVERIME);
    public static final Item POLISHED_REVERIME_STAIRS = blockOf(MirthdewEncoreBlocks.POLISHED_REVERIME_STAIRS);
    public static final Item POLISHED_REVERIME_SLAB = blockOf(MirthdewEncoreBlocks.POLISHED_REVERIME_SLAB);
    public static final Item POLISHED_REVERIME_WALL = blockOf(MirthdewEncoreBlocks.POLISHED_REVERIME_WALL);

    public static final Item POLISHED_REVERIME_BRICKS = blockOf(MirthdewEncoreBlocks.POLISHED_REVERIME_BRICKS);
    public static final Item POLISHED_REVERIME_BRICK_STAIRS = blockOf(MirthdewEncoreBlocks.POLISHED_REVERIME_BRICK_STAIRS);
    public static final Item POLISHED_REVERIME_BRICK_SLAB = blockOf(MirthdewEncoreBlocks.POLISHED_REVERIME_BRICK_SLAB);
    public static final Item POLISHED_REVERIME_BRICK_WALL = blockOf(MirthdewEncoreBlocks.POLISHED_REVERIME_BRICK_WALL);

    public static final Item CUT_POLISHED_REVERIME = blockOf(MirthdewEncoreBlocks.CUT_POLISHED_REVERIME);
    public static final Item CUT_POLISHED_REVERIME_STAIRS = blockOf(MirthdewEncoreBlocks.CUT_POLISHED_REVERIME_STAIRS);
    public static final Item CUT_POLISHED_REVERIME_SLAB = blockOf(MirthdewEncoreBlocks.CUT_POLISHED_REVERIME_SLAB);

    public static final Item ROSENGLACE = blockOf(MirthdewEncoreBlocks.ROSENGLACE);

    public static final Item SCARABRIM = blockOf(MirthdewEncoreBlocks.SCARABRIM);
    public static final Item SCARABRIM_STAIRS = blockOf(MirthdewEncoreBlocks.SCARABRIM_STAIRS);
    public static final Item SCARABRIM_SLAB = blockOf(MirthdewEncoreBlocks.SCARABRIM_SLAB);
    public static final Item SCARABRIM_WALL = blockOf(MirthdewEncoreBlocks.SCARABRIM_WALL);

    public static final Item POLISHED_SCARABRIM = blockOf(MirthdewEncoreBlocks.POLISHED_SCARABRIM);
    public static final Item POLISHED_SCARABRIM_STAIRS = blockOf(MirthdewEncoreBlocks.POLISHED_SCARABRIM_STAIRS);
    public static final Item POLISHED_SCARABRIM_SLAB = blockOf(MirthdewEncoreBlocks.POLISHED_SCARABRIM_SLAB);
    public static final Item POLISHED_SCARABRIM_WALL = blockOf(MirthdewEncoreBlocks.POLISHED_SCARABRIM_WALL);

    public static final Item SCARABRIM_BRICKS = blockOf(MirthdewEncoreBlocks.SCARABRIM_BRICKS);
    public static final Item SCARABRIM_BRICK_STAIRS = blockOf(MirthdewEncoreBlocks.SCARABRIM_BRICK_STAIRS);
    public static final Item SCARABRIM_BRICK_SLAB = blockOf(MirthdewEncoreBlocks.SCARABRIM_BRICK_SLAB);
    public static final Item SCARABRIM_BRICK_WALL = blockOf(MirthdewEncoreBlocks.SCARABRIM_BRICK_WALL);

    public static final Item SUNFLECKED_SCARABRIM = blockOf(MirthdewEncoreBlocks.SUNFLECKED_SCARABRIM);

    public static final Item CHALKTISSUE = blockOf(MirthdewEncoreBlocks.CHALKTISSUE);
    public static final Item FLAKING_CHALKTISSUE = blockOf(MirthdewEncoreBlocks.FLAKING_CHALKTISSUE);
    public static final Item SUNSLAKED_CHALKTISSUE = blockOf(MirthdewEncoreBlocks.SUNSLAKED_CHALKTISSUE);

    public static final Item GACHERIMM_PSYRITE_ORE = blockOf(MirthdewEncoreBlocks.GACHERIMM_PSYRITE_ORE);
    public static final Item SCARABRIM_PSYRITE_ORE = blockOf(MirthdewEncoreBlocks.SCARABRIM_PSYRITE_ORE);
    public static final Item SUNSLAKED_PSYRITE_ORE = blockOf(MirthdewEncoreBlocks.SUNSLAKED_PSYRITE_ORE);
    public static final Item RAW_PSYRITE_BLOCK = blockOf(MirthdewEncoreBlocks.RAW_PSYRITE_BLOCK);
    public static final Item PSYRITE_BLOCK = blockOf(MirthdewEncoreBlocks.PSYRITE_BLOCK);
    public static final Item CUT_PSYRITE = blockOf(MirthdewEncoreBlocks.CUT_PSYRITE);
    public static final Item CUT_PSYRITE_STAIRS = blockOf(MirthdewEncoreBlocks.CUT_PSYRITE_STAIRS);
    public static final Item CUT_PSYRITE_SLAB = blockOf(MirthdewEncoreBlocks.CUT_PSYRITE_SLAB);
    public static final Item CHISELED_PSYRITE = blockOf(MirthdewEncoreBlocks.CHISELED_PSYRITE);
    public static final Item PSYRITE_PILLAR = blockOf(MirthdewEncoreBlocks.PSYRITE_PILLAR);
    public static final Item PSYRITE_GRATE = blockOf(MirthdewEncoreBlocks.PSYRITE_GRATE);
    public static final Item PSYRITE_GRATE_SLAB = blockOf(MirthdewEncoreBlocks.PSYRITE_GRATE_SLAB);
    public static final Item PSYRITE_DOOR = blockOf(MirthdewEncoreBlocks.PSYRITE_DOOR);
    public static final Item PSYRITE_TRAPDOOR = blockOf(MirthdewEncoreBlocks.PSYRITE_TRAPDOOR);
    public static final Item PSYRITE_BARS = blockOf(MirthdewEncoreBlocks.PSYRITE_BARS);
    public static final Item PSYRITE_LATTICE = blockOf(MirthdewEncoreBlocks.PSYRITE_LATTICE);

    public static final Item DOOR_MARKER = blockOf(MirthdewEncoreBlocks.DOOR_MARKER, settings().rarity(Rarity.EPIC));


    public static final Item VESPERBILE_BUCKET = new BucketItem(MirthdewEncoreFluids.VESPERBILE, settings().craftRemainder(Items.BUCKET).stacksTo(1));

    public static final Item BACCHENITE_SHARD = new Item(settings());

    public static final FoodProperties CLINKERA_SCRAPS_FOOD = new FoodProperties.Builder().nutrition(1).saturationModifier(0.1F).alwaysEdible().fast().build();
    public static final FoodProperties PSYRITE_NUGGET_FOOD = new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).alwaysEdible().build();

    public static final Item CLINKERA_SCRAPS = new Item(settings().food(CLINKERA_SCRAPS_FOOD));

    public static final Item RAW_PSYRITE = new Item(settings());
    public static final Item PSYRITE_INGOT = new Item(settings());
    public static final Item PSYRITE_NUGGET = new Item(settings().food(PSYRITE_NUGGET_FOOD));

    public static final Item MIRTHDEW_VIAL = new MirthdewVialItem(
            settings().food(MirthdewVialItem.FOOD_COMPONENT).component(MirthdewEncoreDataComponentTypes.MIRTHDEW_VIAL_AMPLIFIER, 0).rarity(Rarity.UNCOMMON));

    public static final Item SLUMBERING_EYE = new SlumberingEyeItem(settings().stacksTo(1).rarity(Rarity.UNCOMMON));

    public static final FoodProperties SPECTRAL_CANDY_FOOD_COMPONENT = new FoodProperties.Builder().nutrition(1).saturationModifier(4F).alwaysEdible().build();
    public static final Item SPECTRAL_CANDY = new Item(settings()
            .food(SPECTRAL_CANDY_FOOD_COMPONENT)
            .component(
                    MirthdewEncoreDataComponentTypes.FOOD_WHEN_FULL,
                    new FoodWhenFullProperties.Builder().effect(
                            new MobEffectInstance(
                                    MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY, 3000, 2
                            ), 1)
                            .build()
            )
    );

    public static final Item SPELL_CARD = new SpellCardSingularItem(settings().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final Item SPELL_DECK = new SpellCardDeckItem(settings().stacksTo(1).rarity(Rarity.RARE));

    public static void init(BiConsumer<ResourceLocation, Item> r) {
        BiConsumer<ResourceLocation, Item> rwig = (rl, i) -> { // register with item group
            r.accept(rl, i);
            MirthdewEncoreCreativeModeTabs.addItemToMirthdewEncoreTab(i);
        };

        rwig.accept(id("dreamspeck_spawn_egg"), DREAMSPECK_SPAWN_EGG);
        rwig.accept(id("veric_dreamsnare"), VERIC_DREAMSNARE);
        rwig.accept(id("dreamseed"), DREAMSEED);
        rwig.accept(id("slumbersocket"), SLUMBERSOCKET);

        rwig.accept(id("bacchenite_shard"), BACCHENITE_SHARD);
        rwig.accept(id("bacchenite_block"), BACCHENITE_BLOCK);

        rwig.accept(id("bacchenite_bricks"), BACCHENITE_BRICKS);
        rwig.accept(id("bacchenite_brick_stairs"), BACCHENITE_BRICK_STAIRS);
        rwig.accept(id("bacchenite_brick_slab"), BACCHENITE_BRICK_SLAB);
        rwig.accept(id("bacchenite_brick_wall"), BACCHENITE_BRICK_WALL);

        rwig.accept(id("bacchenite_tiles"), BACCHENITE_TILES);
        rwig.accept(id("bacchenite_tile_stairs"), BACCHENITE_TILE_STAIRS);
        rwig.accept(id("bacchenite_tile_slab"), BACCHENITE_TILE_SLAB);
        rwig.accept(id("bacchenite_tile_wall"), BACCHENITE_TILE_WALL);

        rwig.accept(id("unguishale"), UNGUISHALE);
        rwig.accept(id("unguishale_stairs"), UNGUISHALE_STAIRS);
        rwig.accept(id("unguishale_slab"), UNGUISHALE_SLAB);
        rwig.accept(id("unguishale_wall"), UNGUISHALE_WALL);

        rwig.accept(id("unguishale_bricks"), UNGUISHALE_BRICKS);
        rwig.accept(id("unguishale_brick_stairs"), UNGUISHALE_BRICK_STAIRS);
        rwig.accept(id("unguishale_brick_slab"), UNGUISHALE_BRICK_SLAB);
        rwig.accept(id("unguishale_brick_wall"), UNGUISHALE_BRICK_WALL);

        rwig.accept(id("unguishale_tiles"), UNGUISHALE_TILES);
        rwig.accept(id("unguishale_tile_stairs"), UNGUISHALE_TILE_STAIRS);
        rwig.accept(id("unguishale_tile_slab"), UNGUISHALE_TILE_SLAB);
        rwig.accept(id("unguishale_tile_wall"), UNGUISHALE_TILE_WALL);

        rwig.accept(id("clinkera_scraps"), CLINKERA_SCRAPS);
        rwig.accept(id("clinkera_planks"), CLINKERA_PLANKS);
        rwig.accept(id("clinkera_stairs"), CLINKERA_STAIRS);
        rwig.accept(id("clinkera_slab"), CLINKERA_SLAB);
        rwig.accept(id("clinkera_fence"), CLINKERA_FENCE);
        rwig.accept(id("clinkera_fence_gate"), CLINKERA_FENCE_GATE);
        rwig.accept(id("clinkera_lattice"), CLINKERA_LATTICE);
        rwig.accept(id("clinkera_door"), CLINKERA_DOOR);
        rwig.accept(id("clinkera_trapdoor"), CLINKERA_TRAPDOOR);
        rwig.accept(id("clinkera_pressure_plate"), CLINKERA_PRESSURE_PLATE);
        rwig.accept(id("clinkera_button"), CLINKERA_BUTTON);

        rwig.accept(id("onyxscale"), ONYXSCALE);
        rwig.accept(id("rheumdaubed_onyxscale"), RHEUMDAUBED_ONYXSCALE);

        rwig.accept(id("rheumbristles"), RHEUMBRISTLES);
        rwig.accept(id("soulspot_mushrheum"), SOULSPOT_MUSHRHEUM);

        rwig.accept(id("decidrheum_log"), DECIDRHEUM_LOG);
        rwig.accept(id("decidrheum_wood"), DECIDRHEUM_WOOD);
        rwig.accept(id("stripped_decidrheum_log"), STRIPPED_DECIDRHEUM_LOG);
        rwig.accept(id("stripped_decidrheum_wood"), STRIPPED_DECIDRHEUM_WOOD);

        rwig.accept(id("decidrheum_planks"), DECIDRHEUM_PLANKS);
        rwig.accept(id("decidrheum_stairs"), DECIDRHEUM_STAIRS);
        rwig.accept(id("decidrheum_slab"), DECIDRHEUM_SLAB);
        rwig.accept(id("decidrheum_fence"), DECIDRHEUM_FENCE);
        rwig.accept(id("decidrheum_fence_gate"), DECIDRHEUM_FENCE_GATE);
        rwig.accept(id("decidrheum_lattice"), DECIDRHEUM_LATTICE);
        rwig.accept(id("decidrheum_door"), DECIDRHEUM_DOOR);
        rwig.accept(id("decidrheum_trapdoor"), DECIDRHEUM_TRAPDOOR);
        rwig.accept(id("decidrheum_pressure_plate"), DECIDRHEUM_PRESSURE_PLATE);
        rwig.accept(id("decidrheum_button"), DECIDRHEUM_BUTTON);

        rwig.accept(id("decidrheum_leaves"), DECIDRHEUM_LEAVES);
        rwig.accept(id("decidrheum_sapling"), DECIDRHEUM_SAPLING);

        rwig.accept(id("gacherimm"), GACHERIMM);

        rwig.accept(id("orange_novaclag"), ORANGE_NOVACLAG);
        rwig.accept(id("lime_novaclag"), LIME_NOVACLAG);
        rwig.accept(id("cyan_novaclag"), CYAN_NOVACLAG);
        rwig.accept(id("magenta_novaclag"), MAGNETA_NOVACLAG);

        rwig.accept(id("orange_foghair"), ORANGE_FOGHAIR);
        rwig.accept(id("lime_foghair"), LIME_FOGHAIR);
        rwig.accept(id("cyan_foghair"), CYAN_FOGHAIR);
        rwig.accept(id("magenta_foghair"), MAGNETA_FOGHAIR);

        rwig.accept(id("rough_gacherimm"), ROUGH_GACHERIMM);
        rwig.accept(id("rough_gacherimm_stairs"), ROUGH_GACHERIMM_STAIRS);
        rwig.accept(id("rough_gacherimm_slab"), ROUGH_GACHERIMM_SLAB);
        rwig.accept(id("rough_gacherimm_wall"), ROUGH_GACHERIMM_WALL);

        rwig.accept(id("gacherimm_bricks"), GACHERIMM_BRICKS);
        rwig.accept(id("gacherimm_brick_stairs"), GACHERIMM_BRICK_STAIRS);
        rwig.accept(id("gacherimm_brick_slab"), GACHERIMM_BRICK_SLAB);
        rwig.accept(id("gacherimm_brick_wall"), GACHERIMM_BRICK_WALL);

        rwig.accept(id("gacherimm_tiles"), GACHERIMM_TILES);
        rwig.accept(id("gacherimm_tile_stairs"), GACHERIMM_TILE_STAIRS);
        rwig.accept(id("gacherimm_tile_slab"), GACHERIMM_TILE_SLAB);
        rwig.accept(id("gacherimm_tile_wall"), GACHERIMM_TILE_WALL);

        rwig.accept(id("polished_gacherimm"), POLISHED_GACHERIMM);
        rwig.accept(id("polished_gacherimm_stairs"), POLISHED_GACHERIMM_STAIRS);
        rwig.accept(id("polished_gacherimm_slab"), POLISHED_GACHERIMM_SLAB);
        rwig.accept(id("polished_gacherimm_wall"), POLISHED_GACHERIMM_WALL);

        rwig.accept(id("cut_polished_gacherimm"), CUT_POLISHED_GACHERIMM);
        rwig.accept(id("cut_polished_gacherimm_stairs"), CUT_POLISHED_GACHERIMM_STAIRS);
        rwig.accept(id("cut_polished_gacherimm_slab"), CUT_POLISHED_GACHERIMM_SLAB);

        rwig.accept(id("reverime"), REVERIME);
        rwig.accept(id("reverime_stairs"), REVERIME_STAIRS);
        rwig.accept(id("reverime_slab"), REVERIME_SLAB);
        rwig.accept(id("reverime_wall"), REVERIME_WALL);

        rwig.accept(id("frosted_reverime"), FROSTED_REVERIME);

        rwig.accept(id("reverime_bricks"), REVERIME_BRICKS);
        rwig.accept(id("reverime_brick_stairs"), REVERIME_BRICK_STAIRS);
        rwig.accept(id("reverime_brick_slab"), REVERIME_BRICK_SLAB);
        rwig.accept(id("reverime_brick_wall"), REVERIME_BRICK_WALL);

        rwig.accept(id("reverime_tiles"), REVERIME_TILES);
        rwig.accept(id("reverime_tile_stairs"), REVERIME_TILE_STAIRS);
        rwig.accept(id("reverime_tile_slab"), REVERIME_TILE_SLAB);
        rwig.accept(id("reverime_tile_wall"), REVERIME_TILE_WALL);

        rwig.accept(id("polished_reverime"), POLISHED_REVERIME);
        rwig.accept(id("polished_reverime_stairs"), POLISHED_REVERIME_STAIRS);
        rwig.accept(id("polished_reverime_slab"), POLISHED_REVERIME_SLAB);
        rwig.accept(id("polished_reverime_wall"), POLISHED_REVERIME_WALL);

        rwig.accept(id("polished_reverime_bricks"), POLISHED_REVERIME_BRICKS);
        rwig.accept(id("polished_reverime_brick_stairs"), POLISHED_REVERIME_BRICK_STAIRS);
        rwig.accept(id("polished_reverime_brick_slab"), POLISHED_REVERIME_BRICK_SLAB);
        rwig.accept(id("polished_reverime_brick_wall"), POLISHED_REVERIME_BRICK_WALL);

        rwig.accept(id("cut_polished_reverime"), CUT_POLISHED_REVERIME);
        rwig.accept(id("cut_polished_reverime_stairs"), CUT_POLISHED_REVERIME_STAIRS);
        rwig.accept(id("cut_polished_reverime_slab"), CUT_POLISHED_REVERIME_SLAB);

        rwig.accept(id("rosenglace"), ROSENGLACE);

        rwig.accept(id("scarabrim"), SCARABRIM);
        rwig.accept(id("scarabrim_stairs"), SCARABRIM_STAIRS);
        rwig.accept(id("scarabrim_slab"), SCARABRIM_SLAB);
        rwig.accept(id("scarabrim_wall"), SCARABRIM_WALL);

        rwig.accept(id("polished_scarabrim"), POLISHED_SCARABRIM);
        rwig.accept(id("polished_scarabrim_stairs"), POLISHED_SCARABRIM_STAIRS);
        rwig.accept(id("polished_scarabrim_slab"), POLISHED_SCARABRIM_SLAB);
        rwig.accept(id("polished_scarabrim_wall"), POLISHED_SCARABRIM_WALL);

        rwig.accept(id("scarabrim_bricks"), SCARABRIM_BRICKS);
        rwig.accept(id("scarabrim_brick_stairs"), SCARABRIM_BRICK_STAIRS);
        rwig.accept(id("scarabrim_brick_slab"), SCARABRIM_BRICK_SLAB);
        rwig.accept(id("scarabrim_brick_wall"), SCARABRIM_BRICK_WALL);

        rwig.accept(id("sunflecked_scarabrim"), SUNFLECKED_SCARABRIM);

        rwig.accept(id("chalktissue"), CHALKTISSUE);
        rwig.accept(id("flaking_chalktissue"), FLAKING_CHALKTISSUE);
        rwig.accept(id("sunslaked_chalktissue"), SUNSLAKED_CHALKTISSUE);

        rwig.accept(id("gacherimm_psyrite_ore"), GACHERIMM_PSYRITE_ORE);
        rwig.accept(id("scarabrim_psyrite_ore"), SCARABRIM_PSYRITE_ORE);
        rwig.accept(id("sunslaked_psyrite_ore"), SUNSLAKED_PSYRITE_ORE);

        rwig.accept(id("raw_psyrite"), RAW_PSYRITE);
        rwig.accept(id("raw_psyrite_block"), RAW_PSYRITE_BLOCK);
        rwig.accept(id("psyrite_nugget"), PSYRITE_NUGGET);
        rwig.accept(id("psyrite_ingot"), PSYRITE_INGOT);

        rwig.accept(id("psyrite_block"), PSYRITE_BLOCK);
        rwig.accept(id("cut_psyrite"), CUT_PSYRITE);
        rwig.accept(id("cut_psyrite_stairs"), CUT_PSYRITE_STAIRS);
        rwig.accept(id("cut_psyrite_slab"), CUT_PSYRITE_SLAB);
        rwig.accept(id("chiseled_psyrite"), CHISELED_PSYRITE);
        rwig.accept(id("psyrite_pillar"), PSYRITE_PILLAR);
        rwig.accept(id("psyrite_grate"), PSYRITE_GRATE);
        rwig.accept(id("psyrite_grate_slab"), PSYRITE_GRATE_SLAB);
        rwig.accept(id("psyrite_door"), PSYRITE_DOOR);
        rwig.accept(id("psyrite_trapdoor"), PSYRITE_TRAPDOOR);
        rwig.accept(id("psyrite_bars"), PSYRITE_BARS);
        rwig.accept(id("psyrite_lattice"), PSYRITE_LATTICE);


        rwig.accept(id("vesperbile_bucket"), VESPERBILE_BUCKET);


        r.accept(id("mirthdew_vial"), MIRTHDEW_VIAL);
        rwig.accept(id("slumbering_eye"), SLUMBERING_EYE);
        rwig.accept(id("spectral_candy"), SPECTRAL_CANDY);
        r.accept(id("spell_card"), SPELL_CARD);
        r.accept(id("spell_deck"), SPELL_DECK);

        rwig.accept(id("door_marker"), DOOR_MARKER);
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
