package phanastrae.mirthdew_encore.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registry;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.card_spell.effect.CastNextEffect;
import phanastrae.mirthdew_encore.card_spell.effect.ExplodeEffect;
import phanastrae.mirthdew_encore.card_spell.effect.FireEntityEffect;
import phanastrae.mirthdew_encore.card_spell.effect.RunFunctionEffect;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;

import java.util.List;

public class SpellEffectComponentTypes {
    public static final Codec<ComponentType<?>> COMPONENT_TYPE_CODEC = Codec.lazyInitialized(MirthdewEncoreRegistries.SPELL_EFFECT_COMPONENT_TYPE::getCodec);
    public static final Codec<ComponentMap> COMPONENT_MAP_CODEC = ComponentMap.createCodec(COMPONENT_TYPE_CODEC);

    public static final ComponentType<List<CastNextEffect>> CAST_NEXT =
            ComponentType.<List<CastNextEffect>>builder().codec(CastNextEffect.CODEC.codec().listOf()).build();

    public static final ComponentType<List<ExplodeEffect>> EXPLODE =
            ComponentType.<List<ExplodeEffect>>builder().codec(ExplodeEffect.CODEC.codec().listOf()).build();

    public static final ComponentType<List<FireEntityEffect>> FIRE_ENTITY =
            ComponentType.<List<FireEntityEffect>>builder().codec(FireEntityEffect.CODEC.codec().listOf()).build();

    public static final ComponentType<List<RunFunctionEffect>> RUN_FUNCTION =
            ComponentType.<List<RunFunctionEffect>>builder().codec(RunFunctionEffect.CODEC.codec().listOf()).build();

    public static void init() {
        register("cast_next", CAST_NEXT);
        register("explode", EXPLODE);
        register("fire_entity", FIRE_ENTITY);
        register("run_function", RUN_FUNCTION);
    }

    private static <T> ComponentType<T> register(String id, ComponentType<T> value) {
        return Registry.register(MirthdewEncoreRegistries.SPELL_EFFECT_COMPONENT_TYPE, MirthdewEncore.id(id), value);
    }
}
