package phanastrae.mirthdew_encore.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import phanastrae.mirthdew_encore.data.MirthdewEncoreBlockFamilies;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks.*;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    protected BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        forEachBlockInFamilies(this::dropSelf,
                MirthdewEncoreBlockFamilies.BACCHENITE_BRICKS,
                MirthdewEncoreBlockFamilies.BACCHENITE_TILES,

                MirthdewEncoreBlockFamilies.UNGUISHALE,
                MirthdewEncoreBlockFamilies.UNGUISHALE_BRICKS,
                MirthdewEncoreBlockFamilies.UNGUISHALE_TILES,

                MirthdewEncoreBlockFamilies.ROUGH_GACHERIMM,
                MirthdewEncoreBlockFamilies.GACHERIMM_BRICKS,
                MirthdewEncoreBlockFamilies.GACHERIMM_TILES,
                MirthdewEncoreBlockFamilies.POLISHED_GACHERIMM,
                MirthdewEncoreBlockFamilies.CUT_POLISHED_GACHERIMM,

                MirthdewEncoreBlockFamilies.REVERIME,
                MirthdewEncoreBlockFamilies.REVERIME_BRICKS,
                MirthdewEncoreBlockFamilies.REVERIME_TILES,
                MirthdewEncoreBlockFamilies.POLISHED_REVERIME,
                MirthdewEncoreBlockFamilies.POLISHED_REVERIME_BRICKS,
                MirthdewEncoreBlockFamilies.CUT_POLISHED_REVERIME,

                MirthdewEncoreBlockFamilies.SCARABRIM,
                MirthdewEncoreBlockFamilies.POLISHED_SCARBRIM,
                MirthdewEncoreBlockFamilies.SCARABRIM_BRICKS,

                MirthdewEncoreBlockFamilies.CUT_PSYRITE,
                MirthdewEncoreBlockFamilies.PSYRITE_GRATE
        );

        addLootForFamily(MirthdewEncoreBlockFamilies.CLINKERA_PLANKS);
        addLootForFamily(MirthdewEncoreBlockFamilies.DECIDRHEUM_PLANKS);
        addLootForFamily(MirthdewEncoreBlockFamilies.PSYRITE_BLOCK);

        dropSelf(
                SLUMBERSOCKET,

                ACHERUNE_HOLLOW,
                WAKESIDE_RUNE,

                BACCHENITE_BLOCK,

                CLINKERA_LATTICE,

                ONYXSCALE,
                RHEUMBRISTLES,
                SOULSPOT_MUSHRHEUM,

                DECIDRHEUM_LOG,
                DECIDRHEUM_WOOD,
                STRIPPED_DECIDRHEUM_LOG,
                STRIPPED_DECIDRHEUM_WOOD,
                DECIDRHEUM_SAPLING,

                DECIDRHEUM_LATTICE,

                ORANGE_FOGHAIR,
                LIME_FOGHAIR,
                CYAN_FOGHAIR,
                MAGNETA_FOGHAIR,

                ROSENGLACE,

                CHALKTISSUE,
                FLAKING_CHALKTISSUE,
                SUNSLAKED_CHALKTISSUE,

                RAW_PSYRITE_BLOCK,
                PSYRITE_PILLAR,
                PSYRITE_BARS,
                PSYRITE_LATTICE
        );

        dropPottedContents(POTTED_ORANGE_FOGHAIR);
        dropPottedContents(POTTED_LIME_FOGHAIR);
        dropPottedContents(POTTED_CYAN_FOGHAIR);
        dropPottedContents(POTTED_MAGNETA_FOGHAIR);

        dropPottedContents(POTTED_RHEUMBRISTLES);
        dropPottedContents(POTTED_SOULSPOT_MUSHRHEUM);

        dropWhenSilkTouch(
                DREAMSEED,
                VERIC_DREAMSNARE
        );

        add(SLUMBERVEIL, noDrop());

        add(RHEUMDAUBED_ONYXSCALE, createSingleItemTableWithSilkTouch(RHEUMDAUBED_ONYXSCALE, ONYXSCALE));

        add(GACHERIMM, createSingleItemTableWithSilkTouch(GACHERIMM, ROUGH_GACHERIMM));
        for(Block block : MirthdewEncoreBlockFamilies.NOVACLAGS) {
            add(block, createSingleItemTableWithSilkTouch(block, ROUGH_GACHERIMM));
        }

        add(FROSTED_REVERIME, createSingleItemTableWithSilkTouch(FROSTED_REVERIME, REVERIME));

        add(SUNFLECKED_SCARABRIM, createSingleItemTableWithSilkTouch(SUNFLECKED_SCARABRIM, SCARABRIM));

        add(DECIDRHEUM_LEAVES, createLeavesDrops(DECIDRHEUM_LEAVES, DECIDRHEUM_SAPLING, BlockLootTableProvider.NORMAL_LEAVES_SAPLING_CHANCES));

        add(GACHERIMM_PSYRITE_ORE, this::createPsyriteOreDrops);
        add(SCARABRIM_PSYRITE_ORE, this::createPsyriteOreDrops);
        add(SUNSLAKED_PSYRITE_ORE, this::createPsyriteOreDrops);
    }

    private void dropSelf(Block... blocks) {
        for(Block block : blocks) {
            dropSelf(block);
        }
    }

    private void dropWhenSilkTouch(Block... blocks) {
        for(Block block : blocks) {
            dropWhenSilkTouch(block);
        }
    }

    private void forEachBlockInFamilies(Consumer<Block> consumer, BlockFamily... families) {
        for(BlockFamily family : families) {
            forEachBlockInFamily(family, consumer);
        }
    }

    private void addLootForFamily(BlockFamily family) {
        this.dropSelf(family.getBaseBlock());
        for(BlockFamily.Variant variant : BlockFamily.Variant.values()) {
            if(variant != BlockFamily.Variant.DOOR) {
                Block block = family.get(variant);
                if(block != null) {
                    this.dropSelf(block);
                }
            }
        }
        Block door = family.get(BlockFamily.Variant.DOOR);
        if(door != null) {
            add(door, createDoorTable(door));
        }
    }

    private void forEachBlockInFamily(BlockFamily family, Consumer<Block> consumer) {
        consumer.accept(family.getBaseBlock());
        for(Block block : family.getVariants().values()) {
            consumer.accept(block);
        }
    }

    public LootTable.Builder createPsyriteOreDrops(Block block) {
        HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(
                block,
                this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(MirthdewEncoreItems.RAW_PSYRITE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }
}
