package phanastrae.mirthdew_encore.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.card_spell.effect.CastNextEffect;
import phanastrae.mirthdew_encore.card_spell.effect.ExplodeEffect;
import phanastrae.mirthdew_encore.card_spell.effect.FireEntityEffect;
import phanastrae.mirthdew_encore.card_spell.effect.RunFunctionEffect;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;

import java.util.List;
import java.util.function.BiConsumer;

public class SpellEffectComponentTypes {
    public static final Codec<DataComponentType<?>> COMPONENT_TYPE_CODEC = Codec.lazyInitialized(MirthdewEncoreRegistries.SPELL_EFFECT_COMPONENT_TYPE::byNameCodec);
    public static final Codec<DataComponentMap> COMPONENT_MAP_CODEC = DataComponentMap.makeCodec(COMPONENT_TYPE_CODEC);

    public static final DataComponentType<List<CastNextEffect>> CAST_NEXT =
            DataComponentType.<List<CastNextEffect>>builder().persistent(CastNextEffect.CODEC.codec().listOf()).build();

    public static final DataComponentType<List<ExplodeEffect>> EXPLODE =
            DataComponentType.<List<ExplodeEffect>>builder().persistent(ExplodeEffect.CODEC.codec().listOf()).build();

    public static final DataComponentType<List<FireEntityEffect>> FIRE_ENTITY =
            DataComponentType.<List<FireEntityEffect>>builder().persistent(FireEntityEffect.CODEC.codec().listOf()).build();

    public static final DataComponentType<List<RunFunctionEffect>> RUN_FUNCTION =
            DataComponentType.<List<RunFunctionEffect>>builder().persistent(RunFunctionEffect.CODEC.codec().listOf()).build();

    public static void init(BiConsumer<ResourceLocation, DataComponentType<?>> r) {
        r.accept(id("cast_next"), CAST_NEXT);
        r.accept(id("explode"), EXPLODE);
        r.accept(id("fire_entity"), FIRE_ENTITY);
        r.accept(id("run_function"), RUN_FUNCTION);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }
}
