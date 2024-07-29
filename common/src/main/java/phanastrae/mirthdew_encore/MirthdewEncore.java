package phanastrae.mirthdew_encore;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.compat.Compat;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.SpellEffectComponentTypes;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;
import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItemGroups;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;
import phanastrae.mirthdew_encore.network.packet.MirthdewEncorePackets;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;
import phanastrae.mirthdew_encore.world.gen.chunk.MirthdewEncoreChunkGenerators;

public class MirthdewEncore {
    public static final String MOD_ID = "mirthdew_encore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {
        MirthdewEncoreRegistries.init();

        SpellEffectComponentTypes.init();
        MirthdewEncoreDataComponentTypes.init();

        MirthdewEncoreItemGroups.init();
        MirthdewEncoreItems.init();
        MirthdewEncoreItemGroups.setupEntires();

        MirthdewEncoreBlocks.init();
        MirthdewEncoreBlockEntityTypes.init();

        MirthdewEncoreEntityTypes.init();

        MirthdewEncoreStatusEffects.init();

        MirthdewEncoreChunkGenerators.init();

        MirthdewEncorePackets.init();

        Compat.init();
    }
}