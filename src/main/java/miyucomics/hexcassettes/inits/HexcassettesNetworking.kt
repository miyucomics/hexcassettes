package miyucomics.hexcassettes.inits

import miyucomics.hexcassettes.HexcassettesAPI
import miyucomics.hexcassettes.HexcassettesUtils
import miyucomics.hexcassettes.client.ClientStorage
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object HexcassettesNetworking {
	val CASSETTE_ADD: Identifier = HexcassettesUtils.id("cassette_changes")
	val CASSETTE_REMOVE: Identifier = HexcassettesUtils.id("cassette_remove")
	val SYNC_CASSETTES: Identifier = HexcassettesUtils.id("sync_cassettes")

	fun init() {
		ServerPlayNetworking.registerGlobalReceiver(CASSETTE_REMOVE) { _, player, _, packet, _ ->
			val label = packet.readString()
			val state = HexcassettesAPI.getPlayerState(player).queuedHexes
			state.remove(label)
		}

		ServerPlayNetworking.registerGlobalReceiver(SYNC_CASSETTES) { _, player, _, _, _ -> HexcassettesAPI.syncToClient(player) }
		ServerPlayerEvents.AFTER_RESPAWN.register { _, player, _ -> HexcassettesAPI.removeAllQueued(player) }
	}

	fun clientInit() {
		ClientPlayConnectionEvents.JOIN.register { _, _, _ -> ClientPlayNetworking.send(SYNC_CASSETTES, PacketByteBufs.empty()) }
		ClientPlayNetworking.registerGlobalReceiver(CASSETTE_ADD) { _, _, packet, _ ->
			val string = packet.readString()
			ClientStorage.labels[string] = Text.literal(string)
		}
		ClientPlayNetworking.registerGlobalReceiver(CASSETTE_REMOVE) { _, _, packet, _ -> ClientStorage.labels.remove(packet.readString()) }
		ClientPlayNetworking.registerGlobalReceiver(SYNC_CASSETTES) { _, _, packet, _ ->
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