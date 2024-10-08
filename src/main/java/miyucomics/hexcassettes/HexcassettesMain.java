package miyucomics.hexcassettes;

import miyucomics.hexcassettes.inits.HexcassettesAdvancements;
import miyucomics.hexcassettes.inits.HexcassettesNetworking;
import miyucomics.hexcassettes.inits.HexcassettesPatterns;
import miyucomics.hexcassettes.inits.HexcassettesSounds;
import net.fabricmc.api.ModInitializer;

public class HexcassettesMain implements ModInitializer {
	public static final String MOD_ID = "hexcassettes";
	public static final int MAX_CASSETTES = 6;
	public static final int MAX_LABEL_LENGTH = 20;

	@Override
	public void onInitialize() {
		HexcassettesAdvancements.init();
		HexcassettesNetworking.init();
		HexcassettesPatterns.init();
		HexcassettesSounds.init();
	}
}