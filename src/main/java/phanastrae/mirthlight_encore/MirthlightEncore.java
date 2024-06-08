package phanastrae.mirthlight_encore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phanastrae.mirthlight_encore.block.MirthlightEncoreBlocks;
import phanastrae.mirthlight_encore.dreamtwirl.DreamtwirlWorldAttachment;
import phanastrae.mirthlight_encore.world.gen.chunk.MirthlightEncoreChunkGenerators;

public class MirthlightEncore implements ModInitializer {
	public static final String MOD_ID = "mirthlight_encore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
    	return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		MirthlightEncoreBlocks.init();

		MirthlightEncoreChunkGenerators.init();

		ServerTickEvents.START_WORLD_TICK.register((DreamtwirlWorldAttachment::tickWorld));
	}
}