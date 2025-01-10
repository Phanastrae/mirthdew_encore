package phanastrae.mirthdew_encore.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.component.type.*;

import java.util.function.BiConsumer;

public class MirthdewEncoreDataComponentTypes {

    public static final DataComponentType<CardSpellComponent> CARD_SPELL =
            DataComponentType.<CardSpellComponent>builder().persistent(CardSpellComponent.CODEC).networkSynchronized(CardSpellComponent.PACKET_CODEC).build();

    public static final DataComponentType<SpellChargeComponent> SPELL_CHARGE =
            DataComponentType.<SpellChargeComponent>builder().persistent(SpellChargeComponent.CODEC).networkSynchronized(SpellChargeComponent.PACKET_CODEC).cacheEncoding().build();

    public static final DataComponentType<SpellDeckContentsComponent> SPELL_DECK_CONTENTS =
            DataComponentType.<SpellDeckContentsComponent>builder().persistent(SpellDeckContentsComponent.CODEC).networkSynchronized(SpellDeckContentsComponent.PACKET_CODEC).cacheEncoding().build();

    public static final DataComponentType<LocationComponent> LOCATION_COMPONENT =
            DataComponentType.<LocationComponent>builder().persistent(LocationComponent.CODEC).networkSynchronized(LocationComponent.PACKET_CODEC).cacheEncoding().build();

    public static final DataComponentType<Integer> MIRTHDEW_VIAL_AMPLIFIER =
            DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, 15)).networkSynchronized(ByteBufCodecs.VAR_INT).build();

    public static final DataComponentType<FoodWhenFullProperties> FOOD_WHEN_FULL =
            DataComponentType.<FoodWhenFullProperties>builder().persistent(FoodWhenFullProperties.DIRECT_CODEC).networkSynchronized(FoodWhenFullProperties.DIRECT_STREAM_CODEC).cacheEncoding().build();

    public static void init(BiConsumer<ResourceLocation, DataComponentType<?>> r) {
        r.accept(id("card_spell"), CARD_SPELL);
        r.accept(id("spell_charge"), SPELL_CHARGE);
        r.accept(id("spell_deck_contents"), SPELL_DECK_CONTENTS);
        r.accept(id("mirthdew_vial_amplifier"), MIRTHDEW_VIAL_AMPLIFIER);
        r.accept(id("location_component"), LOCATION_COMPONENT);
        r.accept(id("food_when_full"), FOOD_WHEN_FULL);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }
}
