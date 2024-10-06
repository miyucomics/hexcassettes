package miyucomics.hexcassettes.inits

import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.client.ClientStorage
import miyucomics.hexcassettes.data.HexcassettesAPI
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.Identifier

object HexcassettesNetworking {
	val CASSETTE_ADD: Identifier = HexcassettesMain.id("cassette_changes")
	val CASSETTE_REMOVE: Identifier = HexcassettesMain.id("cassette_remove")
	val SYNC_CASSETTES: Identifier = HexcassettesMain.id("sync_cassettes")

	@JvmStatic
	fun init() {
		ServerPlayNetworking.registerGlobalReceiver(CASSETTE_REMOVE) { _, player, _, packet, _ ->
			HexcassettesAPI.removeHex(player, packet.readUuid())
		}
	}

	@JvmStatic
	fun clientInit() {
		ClientPlayNetworking.registerGlobalReceiver(CASSETTE_ADD) { _, _, packet, _ ->
			val uuid = packet.readUuid()
			ClientStorage.indexToUUID.add(uuid)
			ClientStorage.UUIDToLabel[uuid] = packet.readString()
		}

		ClientPlayNetworking.registerGlobalReceiver(CASSETTE_REMOVE) { _, _, packet, _ ->
			val uuid = packet.readUuid()
			ClientStorage.UUIDToLabel.remove(uuid)
			ClientStorage.indexToUUID.remove(uuid)
		}

		ClientPlayNetworking.registerGlobalReceiver(SYNC_CASSETTES) { _, _, packet, _ ->
			val count = packet.readInt()
			ClientStorage.indexToUUID.clear()
			ClientStorage.UUIDToLabel.clear()
			for (i in 0 until count) {
				val uuid = packet.readUuid()
				ClientStorage.indexToUUID.add(uuid)
				ClientStorage.UUIDToLabel[uuid] = packet.readString()
			}
		}
	}
}