package phanastrae.mirthdew_encore.services;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;

public interface XPlatInterface {
    XPlatInterface INSTANCE = Services.load(XPlatInterface.class);

    String getLoader();

    boolean isModLoaded(String modId);

    void sendPayload(ServerPlayer player, CustomPacketPayload payload);

    void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload);

    CreativeModeTab.Builder createCreativeModeTabBuilder();
}
