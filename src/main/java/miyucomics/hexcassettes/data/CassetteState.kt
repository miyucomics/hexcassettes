package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.casting.math.HexPattern
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity

class CassetteState {
	var ownedSlots = 0
	val queuedHexes: MutableMap<HexPattern, QueuedHex> = mutableMapOf()
	private var previouslyActiveSlots: Set<String> = emptySet()

	fun sync(player: ServerPlayerEntity) {
		val buf = PacketByteBufs.create()
		buf.writeInt(ownedSlots)
		val keys = queuedHexes.keys
		buf.writeInt(keys.size)
		keys.forEach { buf.writeString(HexcassettesMain.serializeKey(it)) }
		ServerPlayNetworking.send(player, HexcassettesNetworking.SYNC_CASSETTES, buf)
	}

	fun tick(player: ServerPlayerEntity) {
		queuedHexes.forEach { (_, queuedHex) -> queuedHex.delay -= 1 }

		val iterator = queuedHexes.iterator()
		while (iterator.hasNext()) {
			val (pattern, queuedHex) = iterator.next()
			if (queuedHex.delay <= 0) {
				iterator.remove()
				queuedHex.cast(player, pattern)
			}
		}

		val activeIndices = queuedHexes.keys.map { HexcassettesMain.serializeKey(it) }.toSet()
		if (previouslyActiveSlots != activeIndices)
			sync(player)
		previouslyActiveSlots = activeIndices
	}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putInt("owned", ownedSlots)
		val hexes = NbtCompound()
		queuedHexes.forEach { (pattern, queuedHex) -> hexes.put(HexcassettesMain.serializeKey(pattern), queuedHex.serialize()) }
		compound.put("hexes", hexes)
		return compound
	}

	companion object {
		@JvmStatic
		fun deserialize(compound: NbtCompound): CassetteState {
			val state = CassetteState()
			state.ownedSlots = compound.getInt("owned")

			val hexes = compound.getCompound("hexes")
			hexes.keys.forEach { pattern ->
				val hexCompound = hexes.getCompound(pattern)
				val hex = QueuedHex.deserialize(hexCompound.getCompound("hex"))
				state.queuedHexes[HexcassettesMain.deserializeKey(pattern)] = hex
			}

			return state
		}
	}
}