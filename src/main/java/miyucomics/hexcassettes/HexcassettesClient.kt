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

		ClientTickEvents.END_CLIENT_TICK.register { client ->
			if (CASSETTE_KEYBIND.isPressed && client.currentScreen == null) {
				client.setScreen(CassetteScreen())
			}
		}

		HexcassettesNetworking.clientInit()
	}

	companion object {
		val CASSETTE_KEYBIND = KeyBinding("key.hexcassettes.view_cassettes", GLFW.GLFW_KEY_I, "key.categories.hexcassettes")
	}
}