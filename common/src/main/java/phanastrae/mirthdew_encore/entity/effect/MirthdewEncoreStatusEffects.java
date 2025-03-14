package phanastrae.mirthdew_encore.entity.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class MirthdewEncoreStatusEffects {

    public static final MobEffect DREAMY_DIET = new DreamyDietStatusEffect(MobEffectCategory.NEUTRAL, 0xFF0B3E70);
    public static Holder<MobEffect> DREAMY_DIET_ENTRY;

    public static final MobEffect MIRTHFUL = new MirthfulStatusEffect(MobEffectCategory.BENEFICIAL, 0xFFFF3FDF);
    public static Holder<MobEffect> MIRTHFUL_ENTRY;

    public static void init(HolderRegisterHelper hrh) {
        DREAMY_DIET_ENTRY = hrh.register("dreamy_diet", DREAMY_DIET);
        MIRTHFUL_ENTRY = hrh.register("mirthful", MIRTHFUL);
    }

    @FunctionalInterface
    public interface HolderRegisterHelper {
        Holder<MobEffect> register(String name, MobEffect mobEffect);
    }
}
