package miyucomics.hexcassettes;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class HexcassettesMain implements ModInitializer {
	public static final String MOD_ID = "hexcassettes";

	@Override
	public void onInitialize() {

	}

	public static Identifier id(String string) {
		return new Identifier(MOD_ID, string);
	}
}