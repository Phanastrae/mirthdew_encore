package phanastrae.mirthdew_encore.neoforge.services;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import phanastrae.mirthdew_encore.services.XPlatInterface;

public class XPlatNeoForge implements XPlatInterface {

    @Override
    public String getLoader() {
        return "neoforge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public void sendPayload(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, payload);
    }

    @Override
    public CreativeModeTab.Builder createCreativeModeTabBuilder() {
        return CreativeModeTab.builder();
    }
}
