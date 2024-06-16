package phanastrae.mirthdew_encore.component;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.component.type.SpellDeckContentsComponent;

public class MirthdewEncoreDataComponentTypes {

    public static final ComponentType<SpellDeckContentsComponent> SPELL_DECK_CONTENTS = ComponentType.<SpellDeckContentsComponent>builder().codec(SpellDeckContentsComponent.CODEC).packetCodec(SpellDeckContentsComponent.PACKET_CODEC).cache().build();

    public static void init() {
        register(SPELL_DECK_CONTENTS, "spell_deck_contents");
    }

    private static void register(ComponentType<?> componentType, String name) {
        Identifier identifier = MirthdewEncore.id(name);
        Registry.register(Registries.DATA_COMPONENT_TYPE, identifier, componentType);
    }
}
