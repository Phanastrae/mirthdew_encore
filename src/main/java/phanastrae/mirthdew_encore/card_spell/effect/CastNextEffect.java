package phanastrae.mirthdew_encore.card_spell.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;
import phanastrae.mirthdew_encore.card_spell.SpellCast;

import java.util.List;

public record CastNextEffect(int targetIndex) {
    public static final MapCodec<CastNextEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codecs.NONNEGATIVE_INT.optionalFieldOf("targetIndex", 0).forGetter(CastNextEffect::targetIndex)
                    )
                    .apply(instance, CastNextEffect::new)
    );

    public void castSpell(SpellCast.DelayCollector delayCollector, World world, Entity user, List<SpellCast> children) {
        if(this.targetIndex < 0) return;
        if(this.targetIndex >= children.size()) return;

        SpellCast spellCast = children.get(this.targetIndex);
        spellCast.castSpellSingle(delayCollector, world, user);
    }
}