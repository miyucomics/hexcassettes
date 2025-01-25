package miyucomics.hexcassettes.inits

import miyucomics.hexcassettes.HexcassettesAPI
import miyucomics.hexcassettes.HexcassettesUtils.id
import miyucomics.hexcassettes.client.ClientStorage
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object HexcassettesNetworking {
	val CASSETTE_REMOVE: Identifier = id("cassette_remove")
	val SYNC_CASSETTES: Identifier = id("sync_cassettes")

	@JvmStatic
	fun clientInit() {
		ClientPlayConnectionEvents.JOIN.register { _, _, _ -> ClientPlayNetworking.send(SYNC_CASSETTES, PacketByteBufs.empty()) }
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

	@JvmStatic
	fun init() {
		ServerPlayNetworking.registerGlobalReceiver(CASSETTE_REMOVE) { _, player, _, packet, _ ->
			val label = packet.readString()
			val state = HexcassettesAPI.getPlayerState(player).queuedHexes
			state.remove(label)
		}

		ServerPlayNetworking.registerGlobalReceiver(SYNC_CASSETTES) { _, player, _, _, _ -> HexcassettesAPI.sendSyncPacket(player) }
		ServerPlayerEvents.AFTER_RESPAWN.register { _, player, alive -> if (!alive) HexcassettesAPI.dequeueAll(player) }
	}
}