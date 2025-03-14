package phanastrae.mirthdew_encore;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.compat.Compat;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.component.SpellEffectComponentTypes;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;
import phanastrae.mirthdew_encore.fluid.MirthdewEncoreFluids;
import phanastrae.mirthdew_encore.item.MirthdewEncoreCreativeModeTabs;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;
import phanastrae.mirthdew_encore.particle.MirthdewEncoreParticleTypes;
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
        // spell effect component types
        rla.addRegistryListener(MirthdewEncoreRegistries.SPELL_EFFECT_COMPONENT_TYPE, SpellEffectComponentTypes::init);

        // data components
        rla.addRegistryListener(BuiltInRegistries.DATA_COMPONENT_TYPE, MirthdewEncoreDataComponentTypes::init);

        // creative mode tabs
        rla.addRegistryListener(BuiltInRegistries.CREATIVE_MODE_TAB, MirthdewEncoreCreativeModeTabs::init);
        // fluids
        rla.addRegistryListener(BuiltInRegistries.FLUID, MirthdewEncoreFluids::init);
        // blocks
        rla.addRegistryListener(BuiltInRegistries.BLOCK, MirthdewEncoreBlocks::init);
        // items
        rla.addRegistryListener(BuiltInRegistries.ITEM, MirthdewEncoreItems::init);

        // block entity types
        rla.addRegistryListener(BuiltInRegistries.BLOCK_ENTITY_TYPE, MirthdewEncoreBlockEntityTypes::init);
        // entity types
        rla.addRegistryListener(BuiltInRegistries.ENTITY_TYPE, MirthdewEncoreEntityTypes::init);

        // particle types
        rla.addRegistryListener(BuiltInRegistries.PARTICLE_TYPE, MirthdewEncoreParticleTypes::init);

        // chunk generators
        rla.addRegistryListener(BuiltInRegistries.CHUNK_GENERATOR, MirthdewEncoreChunkGenerators::init);
    }

    public static void commonInit() {
        Compat.init();
    }

    public interface RegistryListenerAdder {
        <T> void addRegistryListener(Registry<T> registry, Consumer<BiConsumer<ResourceLocation, T>> source);
    }

    public static void addTooltips(ItemStack stack, Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag) {
        addToTooltip(stack, MirthdewEncoreDataComponentTypes.FOOD_WHEN_FULL, tooltipContext, componentConsumer, tooltipFlag);
    }

    private static <T extends TooltipProvider> void addToTooltip(
            ItemStack stack, DataComponentType<T> component, Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag
    ) {
        T tooltipProvider = (T)stack.get(component);
        if (tooltipProvider != null) {
            tooltipProvider.addToTooltip(context, tooltipAdder, tooltipFlag);
        }
    }
}