package phanastrae.mirthdew_encore.entity.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.entity.PlayerEntityHungerData;

public class DreamyDietStatusEffect extends MobEffect {
    public static final ResourceLocation FOOD_EMPTY_DREAMY_DIET_TEXTURE = MirthdewEncore.id("hud/food_empty_dreamy_diet");
    public static final ResourceLocation FOOD_HALF_DREAMY_DIET_TEXTURE = MirthdewEncore.id("hud/food_half_dreamy_diet");
    public static final ResourceLocation FOOD_FULL_DREAMY_DIET_TEXTURE = MirthdewEncore.id("hud/food_full_dreamy_diet");

    public static final ResourceLocation FOOD_DEBT_FULL_TEXTURE = MirthdewEncore.id("hud/food_debt_full");
    public static final ResourceLocation FOOD_DEBT_HALF_TEXTURE = MirthdewEncore.id("hud/food_debt_half");

    protected DreamyDietStatusEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        if(entity instanceof Player player) {
            PlayerEntityHungerData.fromPlayer(player).onStartDreamyDieting();
        }
        super.onEffectStarted(entity, amplifier);
    }
}
