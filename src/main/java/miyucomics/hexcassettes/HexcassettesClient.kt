package miyucomics.hexcassettes

import miyucomics.hexcassettes.client.CassetteScreen
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import org.lwjgl.glfw.GLFW

class HexcassettesClient : ClientModInitializer {
	private val openCassettesKeybind = KeyBinding("key.hexcassettes.view_cassettes", GLFW.GLFW_KEY_I, "key.categories.hexcassettes")

	override fun onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(openCassettesKeybind)
		ClientTickEvents.END_CLIENT_TICK.register { client ->
			if (openCassettesKeybind.isPressed && client.currentScreen !is CassetteScreen)
				client.setScreen(CassetteScreen())
		}

		HexcassettesNetworking.clientInit()
	}
}