package phanastrae.mirthdew_encore.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registry;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.card_spell.effect.CastNextEffect;
import phanastrae.mirthdew_encore.card_spell.effect.FireEntityEffect;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;

import java.util.List;

public class SpellEffectComponentTypes {
    public static final Codec<ComponentType<?>> COMPONENT_TYPE_CODEC = Codec.lazyInitialized(MirthdewEncoreRegistries.SPELL_EFFECT_COMPONENT_TYPE::getCodec);
    public static final Codec<ComponentMap> COMPONENT_MAP_CODEC = ComponentMap.createCodec(COMPONENT_TYPE_CODEC);

    public static final ComponentType<List<CastNextEffect>> CAST_NEXT =
            ComponentType.<List<CastNextEffect>>builder().codec(CastNextEffect.CODEC.codec().listOf()).build();

    public static final ComponentType<List<FireEntityEffect>> FIRE_ENTITY =
            ComponentType.<List<FireEntityEffect>>builder().codec(FireEntityEffect.CODEC.codec().listOf()).build();

    public static void init() {
        register("cast_next", CAST_NEXT);
        register("fire_entity", FIRE_ENTITY);
    }

    private static <T> ComponentType<T> register(String id, ComponentType<T> value) {
        return Registry.register(MirthdewEncoreRegistries.SPELL_EFFECT_COMPONENT_TYPE, MirthdewEncore.id(id), value);
    }
}
