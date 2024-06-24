package phanastrae.mirthdew_encore.entity.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreStatusEffects {

    public static final StatusEffect DREAMY_DIET = new DreamyDietStatusEffect(StatusEffectCategory.NEUTRAL, 0xFF0B3E70);
    public static RegistryEntry<StatusEffect> DREAMY_DIET_ENTRY;

    public static final StatusEffect MIRTHFUL = new MirthfulStatusEffect(StatusEffectCategory.BENEFICIAL, 0xFFFF3FDF);
    public static RegistryEntry<StatusEffect> MIRTHFUL_ENTRY;

    public static void init() {
        DREAMY_DIET_ENTRY = register(DREAMY_DIET, "dreamy_diet");
        MIRTHFUL_ENTRY = register(MIRTHFUL, "mirthful");
    }

    private static RegistryEntry.Reference<StatusEffect> register(StatusEffect effect, String name) {
        Identifier identifier = MirthdewEncore.id(name);
        return Registry.registerReference(Registries.STATUS_EFFECT, identifier, effect);
    }
}
