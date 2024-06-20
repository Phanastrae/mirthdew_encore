package phanastrae.mirthdew_encore.card_spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;
import phanastrae.mirthdew_encore.component.SpellEffectComponentTypes;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;

import java.util.List;

public record CardSpell(Text description, CardSpell.Definition definition, ComponentMap effects) {
    public static final Codec<CardSpell> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            TextCodecs.CODEC.fieldOf("description").forGetter(CardSpell::description),
                            CardSpell.Definition.CODEC.forGetter(CardSpell::definition),
                            SpellEffectComponentTypes.COMPONENT_MAP_CODEC.optionalFieldOf("effects", ComponentMap.EMPTY).forGetter(CardSpell::effects)
                    )
                    .apply(instance, CardSpell::new)
    );
    public static final Codec<RegistryEntry<CardSpell>> ENTRY_CODEC = RegistryFixedCodec.of(MirthdewEncoreRegistries.CARD_SPELL_KEY);
    public static final PacketCodec<RegistryByteBuf, RegistryEntry<CardSpell>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(MirthdewEncoreRegistries.CARD_SPELL_KEY);

    public <T> List<T> getEffect(ComponentType<List<T>> type) {
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
                                Codecs.NONNEGATIVE_INT.optionalFieldOf("input_count", 0).forGetter(CardSpell.Definition::inputCount)
                        )
                        .apply(instance, CardSpell.Definition::new)
        );
    }
}
