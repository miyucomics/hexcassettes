package miyucomics.hexcassettes

import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexcassettes.data.PlayerState
import miyucomics.hexcassettes.data.QueuedHex
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.PersistentState
import net.minecraft.world.World
import java.util.UUID

class HexcassettesAPI : PersistentState() {
	private val players: HashMap<UUID, PlayerState> = HashMap()

	override fun writeNbt(nbt: NbtCompound): NbtCompound {
		players.forEach { (uuid: UUID, player: PlayerState) -> nbt.put(uuid.toString(), player.serialize()) }
		return nbt
	}

	companion object {
		private fun createFromNbt(nbt: NbtCompound): HexcassettesAPI {
			val state = HexcassettesAPI()
			nbt.keys.forEach { uuid -> state.players[UUID.fromString(uuid)] =
				PlayerState.deserialize(nbt.getCompound(uuid))
			}
			return state
		}

		private fun getServerState(server: MinecraftServer): HexcassettesAPI {
			val persistentStateManager = server.getWorld(World.OVERWORLD)!!.persistentStateManager
			val state = persistentStateManager.getOrCreate(Companion::createFromNbt, ::HexcassettesAPI, HexcassettesMain.MOD_ID)
			state.markDirty()
			return state
		}

		@JvmStatic
		fun getPlayerState(player: PlayerEntity): PlayerState {
			val state = getServerState(player.server!!)
			return state.players.computeIfAbsent(player.uuid) { PlayerState() }
		}

		fun removeAllQueued(player: ServerPlayerEntity) {
			val state = getPlayerState(player)
			state.queuedHexes.clear()

			val buf = PacketByteBufs.create()
			buf.writeInt(state.ownedCassettes)
			buf.writeInt(0)
			ServerPlayNetworking.send(player, HexcassettesNetworking.SYNC_CASSETTES, buf)
		}

		fun scheduleHex(player: ServerPlayerEntity, hex: ListIota, delay: Int, label: String) {
			val state = getPlayerState(player)
			val queuedHex = QueuedHex(HexIotaTypes.serialize(hex), delay, label)
			state.queuedHexes.add(queuedHex)

			val buf = PacketByteBufs.create()
			buf.writeUuid(queuedHex.uuid)
			buf.writeString(label)
			ServerPlayNetworking.send(player, HexcassettesNetworking.CASSETTE_ADD, buf)
		}

		fun removeWithLabel(player: ServerPlayerEntity, label: String) {
			val hexes = getPlayerState(player).queuedHexes
			hexes.forEach { hex ->
				if (hex.label == label) {
					val buf = PacketByteBufs.create()
					buf.writeUuid(hex.uuid)
					ServerPlayNetworking.send(player, HexcassettesNetworking.CASSETTE_REMOVE, buf)
				}
			}
			hexes.removeIf { hex -> hex.label == label }
		}

		fun syncToClient(player: ServerPlayerEntity) {
			val playerState = getPlayerState(player)
			val buf = PacketByteBufs.create()
			buf.writeInt(playerState.ownedCassettes)
			buf.writeInt(playerState.queuedHexes.size)
			for (queuedHex in playerState.queuedHexes) {
				buf.writeUuid(queuedHex.uuid)
				buf.writeString(queuedHex.label)
			}
			ServerPlayNetworking.send(player, HexcassettesNetworking.SYNC_CASSETTES, buf)
		}
	}
}