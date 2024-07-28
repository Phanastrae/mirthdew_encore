package phanastrae.mirthdew_encore.entity.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class MirthdewEncoreStatusEffects {

    public static final MobEffect DREAMY_DIET = new DreamyDietStatusEffect(MobEffectCategory.NEUTRAL, 0xFF0B3E70);
    public static Holder<MobEffect> DREAMY_DIET_ENTRY;

    public static final MobEffect MIRTHFUL = new MirthfulStatusEffect(MobEffectCategory.BENEFICIAL, 0xFFFF3FDF);
    public static Holder<MobEffect> MIRTHFUL_ENTRY;

    public static void init() {
        DREAMY_DIET_ENTRY = register(DREAMY_DIET, "dreamy_diet");
        MIRTHFUL_ENTRY = register(MIRTHFUL, "mirthful");
    }

    private static Holder.Reference<MobEffect> register(MobEffect effect, String name) {
        ResourceLocation identifier = MirthdewEncore.id(name);
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, identifier, effect);
    }
}
