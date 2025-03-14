package phanastrae.mirthdew_encore.card_spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import phanastrae.mirthdew_encore.component.SpellEffectComponentTypes;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;

public record CardSpell(Component description, CardSpell.Definition definition, DataComponentMap effects) {
    public static final Codec<CardSpell> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ComponentSerialization.CODEC.fieldOf("description").forGetter(CardSpell::description),
                            CardSpell.Definition.CODEC.forGetter(CardSpell::definition),
                            SpellEffectComponentTypes.COMPONENT_MAP_CODEC.optionalFieldOf("effects", DataComponentMap.EMPTY).forGetter(CardSpell::effects)
                    )
                    .apply(instance, CardSpell::new)
    );
    public static final Codec<Holder<CardSpell>> ENTRY_CODEC = RegistryFixedCodec.create(MirthdewEncoreRegistries.CARD_SPELL_KEY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<CardSpell>> ENTRY_PACKET_CODEC = ByteBufCodecs.holderRegistry(MirthdewEncoreRegistries.CARD_SPELL_KEY);

    public <T> List<T> getEffect(DataComponentType<List<T>> type) {
        return this.effects.getOrDefault(type, List.of());
    }

    public record Definition(
            int mirthCost,
            int castDelayMs,
            int rechargeDelayMs,
            int inputCount
    ) {
        public static final MapCodec<CardSpell.Definition> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.INT.optionalFieldOf("mirth_cost", 0).forGetter(CardSpell.Definition::mirthCost),
                                Codec.INT.optionalFieldOf("cast_delay_ms", 0).forGetter(CardSpell.Definition::castDelayMs),
                                Codec.INT.optionalFieldOf("recharge_delay_ms", 0).forGetter(CardSpell.Definition::rechargeDelayMs),
                                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("input_count", 0).forGetter(CardSpell.Definition::inputCount)
                        )
                        .apply(instance, CardSpell.Definition::new)
        );
    }
}
