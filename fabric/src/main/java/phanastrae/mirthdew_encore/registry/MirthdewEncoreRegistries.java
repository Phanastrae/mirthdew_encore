package phanastrae.mirthdew_encore.registry;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.card_spell.CardSpell;

public class MirthdewEncoreRegistries {
    public static final ResourceKey<Registry<CardSpell>> CARD_SPELL_KEY = of("card_spell");
    public static final ResourceKey<Registry<DataComponentType<?>>> SPELL_EFFECT_COMPONENT_TYPE_KEY = of("spell_effect_component_type");

    public static final Registry<DataComponentType<?>> SPELL_EFFECT_COMPONENT_TYPE = new MappedRegistry<>(SPELL_EFFECT_COMPONENT_TYPE_KEY, Lifecycle.stable(), false);

    public static void init() {
        DynamicRegistries.registerSynced(CARD_SPELL_KEY, CardSpell.CODEC);
    }

    private static <T> ResourceKey<Registry<T>> of(String id) {
        return ResourceKey.createRegistryKey(MirthdewEncore.id(id));
    }
}
