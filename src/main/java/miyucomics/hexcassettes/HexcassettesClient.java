package miyucomics.hexcassettes;

import miyucomics.hexcassettes.inits.HexcassettesNetworking;
import net.fabricmc.api.ClientModInitializer;

public class HexcassettesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HexcassettesNetworking.clientInit();
	}
}