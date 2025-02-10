package phanastrae.mirthdew_encore.fabric.services;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import phanastrae.mirthdew_encore.services.XPlatInterface;

public class XPlatFabric implements XPlatInterface {

    @Override
    public String getLoader() {
        return "fabric";
    }

    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public void sendPayload(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload) {
        for(ServerPlayer serverPlayer : PlayerLookup.tracking(entity)) {
            XPlatInterface.INSTANCE.sendPayload(serverPlayer, payload);
        }
    }

    @Override
    public CreativeModeTab.Builder createCreativeModeTabBuilder() {
        return FabricItemGroup.builder();
    }
}
