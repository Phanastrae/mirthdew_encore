package phanastrae.mirthdew_encore.component;

import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.component.type.CardSpellComponent;
import phanastrae.mirthdew_encore.component.type.LocationComponent;
import phanastrae.mirthdew_encore.component.type.SpellChargeComponent;
import phanastrae.mirthdew_encore.component.type.SpellDeckContentsComponent;

public class MirthdewEncoreDataComponentTypes {

    public static final ComponentType<CardSpellComponent> CARD_SPELL =
            ComponentType.<CardSpellComponent>builder().codec(CardSpellComponent.CODEC).packetCodec(CardSpellComponent.PACKET_CODEC).build();

    public static final ComponentType<SpellChargeComponent> SPELL_CHARGE =
            ComponentType.<SpellChargeComponent>builder().codec(SpellChargeComponent.CODEC).packetCodec(SpellChargeComponent.PACKET_CODEC).cache().build();

    public static final ComponentType<SpellDeckContentsComponent> SPELL_DECK_CONTENTS =
            ComponentType.<SpellDeckContentsComponent>builder().codec(SpellDeckContentsComponent.CODEC).packetCodec(SpellDeckContentsComponent.PACKET_CODEC).cache().build();

    public static final ComponentType<LocationComponent> LOCATION_COMPONENT =
            ComponentType.<LocationComponent>builder().codec(LocationComponent.CODEC).packetCodec(LocationComponent.PACKET_CODEC).cache().build();

    public static final ComponentType<Integer> MIRTHDEW_VIAL_AMPLIFIER =
            ComponentType.<Integer>builder().codec(Codecs.rangedInt(0, 15)).packetCodec(PacketCodecs.VAR_INT).build();

    public static void init() {
        register(CARD_SPELL, "card_spell");
        register(SPELL_CHARGE, "spell_charge");
        register(SPELL_DECK_CONTENTS, "spell_deck_contents");
        register(MIRTHDEW_VIAL_AMPLIFIER, "mirthdew_vial_amplifier");
        register(LOCATION_COMPONENT, "location_component");
    }

    private static void register(ComponentType<?> componentType, String name) {
        Identifier identifier = MirthdewEncore.id(name);
        Registry.register(Registries.DATA_COMPONENT_TYPE, identifier, componentType);
    }
}
