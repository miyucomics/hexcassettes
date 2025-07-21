package miyucomics.hexcassettes.inits

import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.PlayerEntityMinterface
import miyucomics.hexcassettes.client.ClientStorage
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object HexcassettesNetworking {
	val CASSETTE_REMOVE: Identifier = HexcassettesMain.id("cassette_remove")
	val SYNC_CASSETTES: Identifier = HexcassettesMain.id("sync_cassettes")

	@JvmStatic
	fun clientInit() {
		ClientPlayConnectionEvents.JOIN.register { _, _, _ -> ClientPlayNetworking.send(SYNC_CASSETTES, PacketByteBufs.empty()) }

		ClientPlayNetworking.registerGlobalReceiver(SYNC_CASSETTES) { client, _, packet, _ ->
			val owned = packet.readInt()
			val count = packet.readInt()
			val cassettes = mutableListOf<Text>()
			repeat (count) { cassettes.add(HexcassettesMain.deserializeKey(packet.readString())) }
			client.execute {
				ClientStorage.ownedCassettes = owned
				ClientStorage.activeCassettes = cassettes
			}
		}
	}

	@JvmStatic
	fun init() {
		ServerPlayNetworking.registerGlobalReceiver(CASSETTE_REMOVE) { _, player, _, packet, _ -> (player as PlayerEntityMinterface).getCassetteState().hexes.remove(packet.readString()) }
		ServerPlayNetworking.registerGlobalReceiver(SYNC_CASSETTES) { _, player, _, _, _ -> (player as PlayerEntityMinterface).getCassetteState().sync(player) }
	}
}