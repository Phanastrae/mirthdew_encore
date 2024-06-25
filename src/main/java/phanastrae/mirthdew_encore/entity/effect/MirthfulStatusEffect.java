package phanastrae.mirthdew_encore.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import phanastrae.mirthdew_encore.card_spell.PlayerEntityMirthData;

public class MirthfulStatusEffect extends StatusEffect {

    protected MirthfulStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!entity.getWorld().isClient() && amplifier >= 0 && entity instanceof PlayerEntity player) {
            long addMirth = 16L << Math.clamp(amplifier, 0, 58);
            long maxMirth = 2048L * (1L << Math.clamp(amplifier * 2L, 0, 51));
            PlayerEntityMirthData.fromPlayer(player).addMirth(addMirth, maxMirth);
        }
        return super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
