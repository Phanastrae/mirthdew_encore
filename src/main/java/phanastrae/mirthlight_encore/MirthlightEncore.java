package phanastrae.mirthlight_encore;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MirthlightEncore implements ModInitializer {
	public static final String MOD_ID = "mirthlight-encore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
    	return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {

	}
}