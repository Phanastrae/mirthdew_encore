package phanastrae.mirthdew_encore.services;

import com.mojang.serialization.Codec;
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

public interface XPlatInterface {
    boolean isModLoaded(String modId);

    void registerEntityAttributes(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder);

    <T> void registerSyncedDynamicRegistry(ResourceKey<? extends Registry<T>> key, Codec<T> codec);

    <T extends CustomPacketPayload> CustomPacketPayload.TypeAndCodec<? super RegistryFriendlyByteBuf, T> registerPlayS2CPayload(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec);

    FriendlyByteBuf createFriendlyByteBuf();

    CreativeModeTab.Builder createCreativeModeTabBuilder();

    void sendPayload(ServerPlayer player, CustomPacketPayload payload);


    void creativeTabAdd(ResourceKey<CreativeModeTab> groupKey, ItemLike item);
    void creativeTabAdd(ResourceKey<CreativeModeTab> groupKey, ItemLike... items);
    void creativeTabAddAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, ItemLike item);
    void creativeTabAddAfter(ItemStack after, ResourceKey<CreativeModeTab> groupKey, ItemStack item);
    void creativeTabAddAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, ItemLike... items);

    void forTabRun(ResourceKey<CreativeModeTab> itemGroupKey, BiConsumer<CreativeModeTab.ItemDisplayParameters, CreativeModeTab.Output> biConsumer);
}
