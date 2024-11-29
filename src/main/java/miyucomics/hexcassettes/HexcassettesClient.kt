package miyucomics.hexcassettes

import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.fabricmc.api.ClientModInitializer

class HexcassettesClient : ClientModInitializer {
	override fun onInitializeClient() {
		HexcassettesNetworking.clientInit()
	}
}