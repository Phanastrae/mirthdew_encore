package phanastrae.mirthdew_encore.card_spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

import static phanastrae.mirthdew_encore.component.SpellEffectComponentTypes.*;

public record SpellCast(RegistryEntry<CardSpell> cardSpellEntry, List<SpellCast> subCasts) {
    public static final Codec<SpellCast> CODEC = Codec.recursive(
            "MirthdewEncoreSpellCast",
            spellCastCodec -> RecordCodecBuilder.create(
            instance -> instance.group(
                            CardSpell.ENTRY_CODEC.fieldOf("card_spell").forGetter(SpellCast::cardSpellEntry),
                            spellCastCodec.listOf().fieldOf("sub_casts").forGetter(SpellCast::subCasts)
                    )
                    .apply(instance, SpellCast::new)
            )
    );
    public static final PacketCodec<RegistryByteBuf, SpellCast> PACKET_CODEC = PacketCodec.recursive(
            spellCastPacketCodec -> PacketCodec.tuple(
                    CardSpell.ENTRY_PACKET_CODEC,
                    SpellCast::cardSpellEntry,
                    spellCastPacketCodec.collect(PacketCodecs.toList()),
                    SpellCast::subCasts,
                    SpellCast::new
            )
    );

    public SpellInfoCollector castSpell(ServerWorld world, Entity user) {
        SpellInfoCollector spellInfoCollector = new SpellInfoCollector();

        if(user instanceof PlayerEntity player) {
            spellInfoCollector.setMirth(PlayerEntityMirthData.fromPlayer(player).getMirth());
        }

        Random random = user.getRandom();
        SoundEvent soundEvent = SoundEvents.ENTITY_BREEZE_SHOOT;
        world.playSound(null, user.getBlockPos(), soundEvent, SoundCategory.NEUTRAL, 0.4F, 0.7F + 1.2F * random.nextFloat());

        this.castSpellSingle(spellInfoCollector, world, user);

        if(user instanceof PlayerEntity player) {
            PlayerEntityMirthData.fromPlayer(player).setMirth(spellInfoCollector.getMirth());
        }

        return spellInfoCollector;
    }

    public void castSpellSingle(SpellInfoCollector spellInfoCollector, ServerWorld world, Entity user) {
        CardSpell cardSpell = this.cardSpellEntry.value();

        spellInfoCollector.addCastDelay(cardSpell.definition().castDelayMs());
        spellInfoCollector.addRechargeDelay(cardSpell.definition().rechargeDelayMs());
        if(spellInfoCollector.tryConsumeMirth(cardSpell.definition().mirthCost())) {
            spellInfoCollector.markSuccess();
            cardSpell.getEffect(CAST_NEXT).forEach(castNextEffect -> castNextEffect.castSpell(spellInfoCollector, world, user, this.subCasts));

            cardSpell.getEffect(FIRE_ENTITY).forEach(fireEntityEffect -> fireEntityEffect.castSpell(world, user));
            cardSpell.getEffect(RUN_FUNCTION).forEach(runFunctionEffect -> runFunctionEffect.castSpell(world, user));
            cardSpell.getEffect(EXPLODE).forEach(explodeEffect -> explodeEffect.apply(world, user));
        } else {
            spellInfoCollector.markFailure();
        }
    }

    public static class SpellInfoCollector {
        private long mirth;
        private int castDelayMs;
        private int rechargeDelayMs;
        private boolean hadSuccess = false;
        private boolean hadFailure = false;

        public void addCastDelay(int timeMs) {
            this.castDelayMs += timeMs;
        }
        public void addRechargeDelay(int timeMs) {
            this.rechargeDelayMs += timeMs;
        }

        public int getCastDelayMs() {
            return this.castDelayMs;
        }

        public int getRechargeDelayMs() {
            return this.rechargeDelayMs;
        }

        public void setMirth(long value) {
            this.mirth = value;
        }

        public long getMirth() {
            return this.mirth;
        }

        public boolean tryConsumeMirth(long value) {
            if(value <= this.mirth) {
                this.mirth -= value;
                return true;
            } else {
                return false;
            }
        }

        public void markSuccess() {
            this.hadSuccess = true;
        }

        public void markFailure() {
            this.hadFailure = true;
        }

        public boolean getHadSuccess() {
            return this.hadSuccess;
        }

        public boolean getHadFailure() {
            return this.hadFailure;
        }
    }

    public static class Builder {

        private RegistryEntry<CardSpell> cardSpell;
        private final List<SpellCast> children;
        private final List<SpellCast.Builder> unbuiltChildren;

        public Builder(RegistryEntry<CardSpell> cardSpell) {
            this.cardSpell = cardSpell;
            this.children = new ArrayList<>();
            this.unbuiltChildren = new ArrayList<>();
        }

        public void addChild(SpellCast.Builder spellCast) {
            this.unbuiltChildren.add(spellCast);
        }

        public boolean hasFreeSlots() {
            int targetSlots = this.cardSpell.value().definition().inputCount();
            int currentSlots = this.children.size() + this.unbuiltChildren.size();
            return currentSlots < targetSlots;
        }

        public SpellCast build() {
            List<SpellCast> casts = new ArrayList<>(this.children);
            for(SpellCast.Builder builder : this.unbuiltChildren) {
                casts.add(builder.build());
            }
            return new SpellCast(this.cardSpell, casts);
        }
    }
}
