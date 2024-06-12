package phanastrae.mirthdew_encore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlWorldAttachment;
import phanastrae.mirthdew_encore.server.command.MirthdewEncoreCommands;
import phanastrae.mirthdew_encore.world.gen.chunk.MirthdewEncoreChunkGenerators;

public class MirthdewEncore implements ModInitializer {
	public static final String MOD_ID = "mirthdew_encore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
    	return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		MirthdewEncoreBlocks.init();

		MirthdewEncoreChunkGenerators.init();

		ServerTickEvents.START_WORLD_TICK.register((DreamtwirlWorldAttachment::tickWorld));
		CommandRegistrationCallback.EVENT.register(MirthdewEncoreCommands::registerCommands);
	}
}