package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexcassettes.CastingUtils.cast
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
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
			nbt.keys.forEach { uuid -> state.players[UUID.fromString(uuid)] = PlayerState.deserialize(nbt.getCompound(uuid)) }
			return state
		}

		@JvmStatic
		fun getServerState(server: MinecraftServer): HexcassettesAPI {
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

		@JvmStatic
		fun removeAllQueued(player: ServerPlayerEntity) {
			val state = getPlayerState(player)
			state.queuedHexes.clear()

			val buf = PacketByteBufs.create()
			buf.writeInt(0)
			ServerPlayNetworking.send(player, HexcassettesNetworking.SYNC_CASSETTES, buf)
		}

		@JvmStatic
		fun removeHex(player: ServerPlayerEntity, uuid: UUID) {
			val state = getPlayerState(player).queuedHexes
			state.removeIf { hex -> hex.uuid == uuid }
		}

		@JvmStatic
		fun scheduleHex(player: ServerPlayerEntity, hex: ListIota, delay: Int, label: String) {
			val state = getPlayerState(player)
			val queuedHex = QueuedHex(HexIotaTypes.serialize(hex), delay, label)
			state.queuedHexes.add(queuedHex)

			val buf = PacketByteBufs.create()
			buf.writeUuid(queuedHex.uuid)
			buf.writeString(label)
			ServerPlayNetworking.send(player, HexcassettesNetworking.CASSETTE_ADD, buf)
		}
	}
}

class PlayerState {
	val queuedHexes: MutableList<QueuedHex> = mutableListOf()

	fun tick(player: ServerPlayerEntity) {
		val count = queuedHexes.size - 1
		for (i in 0..count) {
			queuedHexes[i].delay -= 1
			if (queuedHexes[i].delay == 0)
				queuedHexes[i].cast(player)
		}
		queuedHexes.removeIf { hex -> hex.delay <= 0 }
	}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		val serializedHexes = NbtList()
		queuedHexes.forEach { queuedHex -> serializedHexes.add(queuedHex.serialize()) }
		compound.putList("hexes", serializedHexes)
		return compound
	}

	companion object {
		fun deserialize(compound: NbtCompound): PlayerState {
			val state = PlayerState()
			val serializedHexes = compound.getList("hexes", NbtElement.COMPOUND_TYPE.toInt())
			serializedHexes.forEach { hex -> state.queuedHexes.add(QueuedHex.deserialize(hex as NbtCompound)) }
			return state
		}
	}
}

data class QueuedHex(val hex: NbtCompound, var delay: Int, val label: String, val uuid: UUID) {
	constructor(hex: NbtCompound, delay: Int, label: String): this(hex, delay, label, UUID.randomUUID())

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putCompound("hex", hex)
		compound.putInt("delay", delay)
		return compound
	}

	fun cast(player: ServerPlayerEntity) {
		cast(player.getWorld(), player, hex)
		val buf = PacketByteBufs.create()
		buf.writeUuid(uuid)
		ServerPlayNetworking.send(player, HexcassettesNetworking.CASSETTE_REMOVE, buf)
	}

	companion object {
		fun deserialize(compound: NbtCompound) = QueuedHex(compound.getCompound("hex"), compound.getInt("delay"), compound.getString("label"))
	}
}