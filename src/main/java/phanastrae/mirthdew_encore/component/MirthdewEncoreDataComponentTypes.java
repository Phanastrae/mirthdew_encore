package phanastrae.mirthdew_encore.component;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.component.type.CardSpellComponent;
import phanastrae.mirthdew_encore.component.type.LocationComponent;
import phanastrae.mirthdew_encore.component.type.SpellChargeComponent;
import phanastrae.mirthdew_encore.component.type.SpellDeckContentsComponent;

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

    public static void init() {
        register(CARD_SPELL, "card_spell");
        register(SPELL_CHARGE, "spell_charge");
        register(SPELL_DECK_CONTENTS, "spell_deck_contents");
        register(MIRTHDEW_VIAL_AMPLIFIER, "mirthdew_vial_amplifier");
        register(LOCATION_COMPONENT, "location_component");
    }

    private static void register(DataComponentType<?> componentType, String name) {
        ResourceLocation identifier = MirthdewEncore.id(name);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, identifier, componentType);
    }
}
