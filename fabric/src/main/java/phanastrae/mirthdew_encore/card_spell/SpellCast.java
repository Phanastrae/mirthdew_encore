package phanastrae.mirthdew_encore.card_spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import static phanastrae.mirthdew_encore.component.SpellEffectComponentTypes.*;

public record SpellCast(Holder<CardSpell> cardSpellEntry, List<SpellCast> subCasts) {
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
    public static final StreamCodec<RegistryFriendlyByteBuf, SpellCast> PACKET_CODEC = StreamCodec.recursive(
            spellCastPacketCodec -> StreamCodec.composite(
                    CardSpell.ENTRY_PACKET_CODEC,
                    SpellCast::cardSpellEntry,
                    spellCastPacketCodec.apply(ByteBufCodecs.list()),
                    SpellCast::subCasts,
                    SpellCast::new
            )
    );

    public SpellInfoCollector castSpell(ServerLevel world, Entity user) {
        SpellInfoCollector spellInfoCollector = new SpellInfoCollector();

        if(user instanceof Player player) {
            spellInfoCollector.setMirth(PlayerEntityMirthData.fromPlayer(player).getMirth());
        }

        RandomSource random = user.getRandom();
        SoundEvent soundEvent = SoundEvents.BREEZE_SHOOT;
        world.playSound(null, user.blockPosition(), soundEvent, SoundSource.NEUTRAL, 0.4F, 0.7F + 1.2F * random.nextFloat());

        this.castSpellSingle(spellInfoCollector, world, user);

        if(user instanceof Player player) {
            PlayerEntityMirthData.fromPlayer(player).setMirth(spellInfoCollector.getMirth());
        }

        return spellInfoCollector;
    }

    public void castSpellSingle(SpellInfoCollector spellInfoCollector, ServerLevel world, Entity user) {
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

        private Holder<CardSpell> cardSpell;
        private final List<SpellCast> children;
        private final List<SpellCast.Builder> unbuiltChildren;

        public Builder(Holder<CardSpell> cardSpell) {
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
