package miyucomics.hexcassettes

import miyucomics.hexcassettes.client.ClientStorage
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.text.Text

class HexcassettesClient : ClientModInitializer {
	override fun onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register { _, _, _ -> ClientPlayNetworking.send(HexcassettesMain.SYNC_CASSETTES, PacketByteBufs.empty()) }
		ClientPlayNetworking.registerGlobalReceiver(HexcassettesMain.SYNC_CASSETTES) { _, _, packet, _ ->
			ClientStorage.ownedCassettes = packet.readInt()
			ClientStorage.labels.clear()
			val count = packet.readInt()
			for (i in 0 until count) {
				val string = packet.readString()
				ClientStorage.labels[string] = Text.literal(string)
			}
		}
	}
}