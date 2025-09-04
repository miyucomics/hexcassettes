package miyucomics.hexcassettes

import miyucomics.hexcassettes.client.CassetteScreen
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW

class HexcassettesClient : ClientModInitializer {
	override fun onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(CASSETTE_KEYBIND)
		HexcassettesNetworking.clientInit()

		ClientTickEvents.END_CLIENT_TICK.register { client ->
			if (CASSETTE_KEYBIND.wasPressed() && client.currentScreen == null)
				client.setScreen(CassetteScreen())
		}
	}

	companion object {
		val CASSETTE_KEYBIND = KeyBinding("key.hexcassettes.ponder_cassette", GLFW.GLFW_KEY_G, "key.categories.hexcassettes")
	}
}