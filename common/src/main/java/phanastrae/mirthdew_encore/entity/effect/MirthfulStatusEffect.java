package phanastrae.mirthdew_encore.entity.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import phanastrae.mirthdew_encore.card_spell.PlayerEntityMirthData;

public class MirthfulStatusEffect extends MobEffect {

    protected MirthfulStatusEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(!entity.level().isClientSide() && amplifier >= 0 && entity instanceof Player player) {
            long addMirth = 16L << Math.clamp(amplifier, 0, 58);
            long maxMirth = 2048L * (1L << Math.clamp(amplifier * 2L, 0, 51));
            PlayerEntityMirthData.fromPlayer(player).addMirth(addMirth, maxMirth);
        }
        return super.applyEffectTick(entity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
