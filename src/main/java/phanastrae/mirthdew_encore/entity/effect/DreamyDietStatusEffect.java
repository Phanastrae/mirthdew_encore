package phanastrae.mirthdew_encore.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.entity.PlayerEntityHungerData;

public class DreamyDietStatusEffect extends StatusEffect {
    public static final Identifier FOOD_EMPTY_DREAMY_DIET_TEXTURE = MirthdewEncore.id("hud/food_empty_dreamy_diet");
    public static final Identifier FOOD_HALF_DREAMY_DIET_TEXTURE = MirthdewEncore.id("hud/food_half_dreamy_diet");
    public static final Identifier FOOD_FULL_DREAMY_DIET_TEXTURE = MirthdewEncore.id("hud/food_full_dreamy_diet");

    public static final Identifier FOOD_DEBT_FULL_TEXTURE = MirthdewEncore.id("hud/food_debt_full");
    public static final Identifier FOOD_DEBT_HALF_TEXTURE = MirthdewEncore.id("hud/food_debt_half");

    protected DreamyDietStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        if(entity instanceof PlayerEntity player) {
            PlayerEntityHungerData.fromPlayer(player).onStartDreamyDieting();
        }
        super.onApplied(entity, amplifier);
    }
}
