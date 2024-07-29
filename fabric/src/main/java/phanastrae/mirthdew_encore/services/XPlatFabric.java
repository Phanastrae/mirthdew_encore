package phanastrae.mirthdew_encore.services;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.BiConsumer;

public class XPlatFabric implements XPlatInterface {
    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public void registerEntityAttributes(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
        FabricDefaultAttributeRegistry.register(type, builder);
    }

    @Override
    public <T> void registerSyncedDynamicRegistry(ResourceKey<? extends Registry<T>> key, Codec<T> codec) {
        DynamicRegistries.registerSynced(key, codec);
    }

    @Override
    public <T extends CustomPacketPayload> CustomPacketPayload.TypeAndCodec<? super RegistryFriendlyByteBuf, T> registerPlayS2CPayload(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        return PayloadTypeRegistry.playS2C().register(id, codec);
    }

    @Override
    public FriendlyByteBuf createFriendlyByteBuf() {
        return PacketByteBufs.create();
    }

    @Override
    public CreativeModeTab.Builder createCreativeModeTabBuilder() {
        return FabricItemGroup.builder();
    }

    @Override
    public void sendPayload(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void creativeTabAdd(ResourceKey<CreativeModeTab> groupKey, ItemLike item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.accept(item));
    }

    @Override
    public void creativeTabAdd(ResourceKey<CreativeModeTab> groupKey, ItemLike... items) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> {
            for(ItemLike item : items) {
                entries.accept(item);
            }
        });
    }

    @Override
    public void creativeTabAddAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, ItemLike item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addAfter(after, item));
    }

    @Override
    public void creativeTabAddAfter(ItemStack after, ResourceKey<CreativeModeTab> groupKey, ItemStack item) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addAfter(after, item));
    }

    @Override
    public void creativeTabAddAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, ItemLike... items) {
        ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addAfter(after, items));
    }

    @Override
    public void forTabRun(ResourceKey<CreativeModeTab> itemGroupKey, BiConsumer<CreativeModeTab.ItemDisplayParameters, CreativeModeTab.Output> biConsumer) {
        ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register(entries -> {
            CreativeModeTab.ItemDisplayParameters displayContext = entries.getContext();
            biConsumer.accept(displayContext, entries);
        });
    }
}
