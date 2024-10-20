package miyucomics.hexcassettes

import miyucomics.hexcassettes.inits.HexcassettesNetworking.clientInit
import net.fabricmc.api.ClientModInitializer

class HexcassettesClient : ClientModInitializer {
	override fun onInitializeClient() {
		clientInit()
	}
}