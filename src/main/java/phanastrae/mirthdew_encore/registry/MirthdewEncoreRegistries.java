package phanastrae.mirthdew_encore.registry;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.card_spell.CardSpell;

public class MirthdewEncoreRegistries {
    public static final RegistryKey<Registry<CardSpell>> CARD_SPELL_KEY = of("card_spell");
    public static final RegistryKey<Registry<ComponentType<?>>> SPELL_EFFECT_COMPONENT_TYPE_KEY = of("spell_effect_component_type");

    public static final Registry<ComponentType<?>> SPELL_EFFECT_COMPONENT_TYPE = new SimpleRegistry<>(SPELL_EFFECT_COMPONENT_TYPE_KEY, Lifecycle.stable(), false);

    public static void init() {
        DynamicRegistries.registerSynced(CARD_SPELL_KEY, CardSpell.CODEC);
    }

    private static <T> RegistryKey<Registry<T>> of(String id) {
        return RegistryKey.ofRegistry(MirthdewEncore.id(id));
    }
}
