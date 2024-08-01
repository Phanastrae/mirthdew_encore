package phanastrae.mirthdew_encore;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.compat.Compat;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.SpellEffectComponentTypes;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItemGroups;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;
import phanastrae.mirthdew_encore.world.gen.chunk.MirthdewEncoreChunkGenerators;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MirthdewEncore {
    public static final String MOD_ID = "mirthdew_encore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void registriesInit(RegistryListenerAdder rla) {
        rla.addRegistryListener(MirthdewEncoreRegistries.SPELL_EFFECT_COMPONENT_TYPE, SpellEffectComponentTypes::init);

        rla.addRegistryListener(BuiltInRegistries.DATA_COMPONENT_TYPE, MirthdewEncoreDataComponentTypes::init);

        rla.addRegistryListener(BuiltInRegistries.CREATIVE_MODE_TAB, MirthdewEncoreItemGroups::init);
        rla.addRegistryListener(BuiltInRegistries.ITEM, MirthdewEncoreItems::init);

        rla.addRegistryListener(BuiltInRegistries.BLOCK, MirthdewEncoreBlocks::init);
        rla.addRegistryListener(BuiltInRegistries.BLOCK_ENTITY_TYPE, MirthdewEncoreBlockEntityTypes::init);

        rla.addRegistryListener(BuiltInRegistries.ENTITY_TYPE, MirthdewEncoreEntityTypes::init);

        rla.addRegistryListener(BuiltInRegistries.CHUNK_GENERATOR, MirthdewEncoreChunkGenerators::init);
    }

    public static void commonInit() {
        Compat.init();
    }

    public interface RegistryListenerAdder {
        <T> void addRegistryListener(Registry<T> registry, Consumer<BiConsumer<ResourceLocation, T>> source);
    }
}