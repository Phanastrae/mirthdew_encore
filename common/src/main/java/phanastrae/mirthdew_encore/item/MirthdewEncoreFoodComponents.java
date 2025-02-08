package phanastrae.mirthdew_encore.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import phanastrae.mirthdew_encore.component.type.FoodWhenFullProperties;
import phanastrae.mirthdew_encore.entity.effect.MirthdewEncoreStatusEffects;

public class MirthdewEncoreFoodComponents {

    public static final FoodProperties OCULAR_SOPORSTEW = new FoodProperties
            .Builder()
            .nutrition(7)
            .saturationModifier(8.5F)
            .effect(
                    new MobEffectInstance(
                            MobEffects.BLINDNESS, 100, 0
                    ), 1F
            )
            .effect(
                    new MobEffectInstance(
                            MobEffects.DARKNESS, 100, 0
                    ), 1F
            )
            .effect(
                    new MobEffectInstance(
                            MobEffects.WEAKNESS, 600, 1
                    ), 0.8F
            )
            .effect(
                    new MobEffectInstance(
                            MobEffects.POISON, 80, 3
                    ), 0.4F
            )
            .effect(
                    new MobEffectInstance(
                            MobEffects.DIG_SLOWDOWN, 160, 1
                    ), 0.4F
            )
            .effect(
                    new MobEffectInstance(
                            MobEffects.HUNGER, 160, 1
                    ), 0.3F
            )
            .alwaysEdible()
            .build();

    public static final FoodProperties CLINKERA_SCRAPS = new FoodProperties.Builder()
            .nutrition(1)
            .saturationModifier(0.1F)
            .alwaysEdible()
            .fast()
            .build();

    public static final FoodProperties PSYRITE_NUGGET = new FoodProperties.Builder()
            .nutrition(3)
            .saturationModifier(0.3F)
            .alwaysEdible()
            .build();

    public static final FoodProperties MIRTHDEW_VIAL = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.5F)
            .build();

    public static final FoodProperties SPECTRAL_CANDY = new FoodProperties.Builder()
            .nutrition(1)
            .saturationModifier(4F)
            .alwaysEdible()
            .build();

    public static final FoodWhenFullProperties SPECTRAL_CANDY_WHEN_FULL =
            new FoodWhenFullProperties.Builder()
                    .effect(
                            new MobEffectInstance(
                                    MirthdewEncoreStatusEffects.DREAMY_DIET_ENTRY, 3000, 2
                            ), 1)
                    .build();
}
