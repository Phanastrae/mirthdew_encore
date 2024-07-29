package phanastrae.mirthdew_encore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlWorldAttachment;
import phanastrae.mirthdew_encore.server.command.MirthdewEncoreCommands;

public class MirthdewEncoreFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		MirthdewEncore.commonInit();

		ServerTickEvents.START_WORLD_TICK.register(DreamtwirlWorldAttachment::tickWorld);
		CommandRegistrationCallback.EVENT.register(MirthdewEncoreCommands::registerCommands);
	}
}