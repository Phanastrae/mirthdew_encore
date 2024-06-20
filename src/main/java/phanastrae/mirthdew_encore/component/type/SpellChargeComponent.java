package phanastrae.mirthdew_encore.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.card_spell.SpellCast;

import java.util.ArrayList;
import java.util.List;

public record SpellChargeComponent(long cooldownStart, int cooldownLength, int maxCasts, List<SpellCast> spellCasts, int storedRechargeDelayMs) {
    public static final Codec<SpellChargeComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.LONG.optionalFieldOf("cooldownStart", 0L).forGetter(SpellChargeComponent::cooldownStart),
                            Codec.INT.optionalFieldOf("cooldownLength", 0).forGetter(SpellChargeComponent::cooldownLength),
                            Codec.INT.optionalFieldOf("max_casts", 0).forGetter(SpellChargeComponent::maxCasts),
                            SpellCast.CODEC.listOf().optionalFieldOf("spell_casts", List.of()).forGetter(SpellChargeComponent::spellCasts),
                            Codec.INT.optionalFieldOf("stored_recharge_delay_ms", 0).forGetter(SpellChargeComponent::storedRechargeDelayMs)
                    )
                    .apply(instance, SpellChargeComponent::new)
    );
    public static final PacketCodec<RegistryByteBuf, SpellChargeComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_LONG,
            SpellChargeComponent::cooldownStart,
            PacketCodecs.INTEGER,
            SpellChargeComponent::cooldownLength,
            PacketCodecs.INTEGER,
            SpellChargeComponent::maxCasts,
            SpellCast.PACKET_CODEC.collect(PacketCodecs.toList()),
            SpellChargeComponent::spellCasts,
            PacketCodecs.INTEGER,
            SpellChargeComponent::storedRechargeDelayMs,
            SpellChargeComponent::new
    );

    public boolean isEmpty() {
        return this.spellCasts.isEmpty();
    }

    public boolean isDisabled(long worldTime) {
        return this.cooldownLength > 0 && worldTime < this.cooldownStart + this.cooldownLength;
    }

    public float getRemainingCooldown(long worldTime, float tickDelta) {
        if(this.cooldownLength <= 0) {
            return 0.0F;
        } else {
            int cooldownProgress = Math.clamp(worldTime - this.cooldownStart, 0, this.cooldownLength);
            float cooldownProgressRelative = (cooldownProgress + tickDelta) / this.cooldownLength;
            return Math.clamp(1.0F - cooldownProgressRelative, 0.0F, 1.0F);
        }
    }

    public static class Builder {
        long cooldownStart;
        int cooldownLength;
        int maxCasts;
        final List<SpellCast> spellCasts;
        int storedRechargeDelayMs;

        public Builder(List<SpellCast> spellCastList) {
            this.cooldownStart = 0;
            this.cooldownLength = 0;
            this.maxCasts = spellCastList.size();
            this.spellCasts = new ArrayList<>(spellCastList);
            this.storedRechargeDelayMs = 0;
        }

        public Builder(SpellChargeComponent spellChargeComponent) {
            this.cooldownStart = spellChargeComponent.cooldownStart;
            this.cooldownLength = spellChargeComponent.cooldownLength;
            this.maxCasts = spellChargeComponent.maxCasts;
            this.spellCasts = new ArrayList<>(spellChargeComponent.spellCasts);
            this.storedRechargeDelayMs = spellChargeComponent.storedRechargeDelayMs;
        }

        public void setCooldown(long start, int length) {
            this.cooldownStart = start;
            this.cooldownLength = length;
        }

        public void add(SpellCast spellCast) {
            this.spellCasts.add(spellCast);
        }

        public void addAll(List<SpellCast> spellCast) {
            this.spellCasts.addAll(spellCast);
        }

        public boolean isEmpty() {
            return this.spellCasts.isEmpty();
        }

        @Nullable
        public SpellCast removeFirst() {
            if(this.spellCasts.isEmpty()) {
                return null;
            } else {
                return this.spellCasts.removeFirst();
            }
        }

        public void addRechargeDelay(int timeMs) {
            this.storedRechargeDelayMs += timeMs;
        }

        public int removeRechargeDelay() {
            int storedDelay = this.storedRechargeDelayMs;
            this.storedRechargeDelayMs = 0;
            return storedDelay;
        }

        public SpellChargeComponent build() {
            return new SpellChargeComponent(cooldownStart, cooldownLength, maxCasts, spellCasts, storedRechargeDelayMs);
        }
    }
}
