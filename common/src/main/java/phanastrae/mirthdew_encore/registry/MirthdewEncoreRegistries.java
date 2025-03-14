package phanastrae.mirthdew_encore.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.card_spell.CardSpell;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista.VistaType;

public class MirthdewEncoreRegistries {
    public static final ResourceKey<Registry<CardSpell>> CARD_SPELL_KEY = of("card_spell");
    public static final ResourceKey<Registry<DataComponentType<?>>> SPELL_EFFECT_COMPONENT_TYPE_KEY = of("spell_effect_component_type");
    public static final ResourceKey<Registry<RoomType>> ROOM_TYPE_KEY = of("room_type");
    public static final ResourceKey<Registry<VistaType>> VISTA_TYPE_KEY = of("vista_type");

    public static final WritableRegistry<DataComponentType<?>> SPELL_EFFECT_COMPONENT_TYPE = new MappedRegistry<>(SPELL_EFFECT_COMPONENT_TYPE_KEY, Lifecycle.stable(), false);

    public static void registerRegistries(Helper helper) {
        helper.register(SPELL_EFFECT_COMPONENT_TYPE);
    }

    public static void registerSynced(SyncedHelper helper) {
        helper.register(CARD_SPELL_KEY, CardSpell.CODEC);
        helper.register(ROOM_TYPE_KEY, RoomType.CODEC);
        helper.register(VISTA_TYPE_KEY, VistaType.CODEC);
    }

    private static <T> ResourceKey<Registry<T>> of(String id) {
        return ResourceKey.createRegistryKey(MirthdewEncore.id(id));
    }

    @FunctionalInterface
    public interface Helper {
        <T> void register(WritableRegistry<T> registry);
    }

    @FunctionalInterface
    public interface SyncedHelper {
        <T> void register(ResourceKey<Registry<T>> registryKey, Codec<T> codec);
    }
}
