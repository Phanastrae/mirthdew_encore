package phanastrae.mirthdew_encore.card_spell.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import phanastrae.mirthdew_encore.card_spell.SpellCast;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;

public record CastNextEffect(int targetIndex) {
    public static final MapCodec<CastNextEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("target_index", 0).forGetter(CastNextEffect::targetIndex)
                    )
                    .apply(instance, CastNextEffect::new)
    );

    public void castSpell(SpellCast.SpellInfoCollector spellInfoCollector, ServerLevel world, Entity user, List<SpellCast> children) {
        if(0 <= this.targetIndex && this.targetIndex < children.size()) {
            SpellCast spellCast = children.get(this.targetIndex);
            spellCast.castSpellSingle(spellInfoCollector, world, user);
        }
    }
}