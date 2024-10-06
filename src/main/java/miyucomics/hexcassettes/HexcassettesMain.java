package miyucomics.hexcassettes;

import miyucomics.hexcassettes.inits.HexcassettesAdvancements;
import miyucomics.hexcassettes.inits.HexcassettesPatterns;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class HexcassettesMain implements ModInitializer {
	public static final String MOD_ID = "hexcassettes";

	@Override
	public void onInitialize() {
		HexcassettesAdvancements.init();
		HexcassettesPatterns.init();
	}

	public static Identifier id(String string) {
		return new Identifier(MOD_ID, string);
	}
}