package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.casting.math.HexPattern
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity

class CassetteState {
	var owned = 0
	val hexes: MutableMap<HexPattern, QueuedHex> = mutableMapOf()
	private var previouslyActiveSlots: Set<String> = emptySet()

	fun sync(player: ServerPlayerEntity) {
		val buf = PacketByteBufs.create()
		buf.writeInt(owned)
		val keys = hexes.keys
		buf.writeInt(keys.size)
		keys.forEach { buf.writeString(HexcassettesMain.serializeKey(it)) }
		ServerPlayNetworking.send(player, HexcassettesNetworking.SYNC_CASSETTES, buf)
	}

	fun tick(player: ServerPlayerEntity) {
		hexes.forEach { (_, hex) -> hex.delay -= 1 }

		val iterator = hexes.iterator()
		while (iterator.hasNext()) {
			val (pattern, hex) = iterator.next()
			if (hex.delay <= 0) {
				iterator.remove()
				hex.cast(player, pattern)
			}
		}

		val activeIndices = hexes.keys.map { HexcassettesMain.serializeKey(it) }.toSet()
		if (previouslyActiveSlots != activeIndices)
			sync(player)
		previouslyActiveSlots = activeIndices
	}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putInt("owned", owned)
		val serialized = NbtCompound()
		hexes.forEach { (pattern, hex) -> serialized.put(HexcassettesMain.serializeKey(pattern), hex.serialize()) }
		compound.put("hexes", serialized)
		return compound
	}

	companion object {
		@JvmStatic
		fun deserialize(compound: NbtCompound): CassetteState {
			val state = CassetteState()

			state.owned = compound.getInt("owned")
			val hexes = compound.getCompound("hexes")
			hexes.keys.forEach {
				state.hexes[HexcassettesMain.deserializeKey(it)] = QueuedHex.deserialize(hexes.getCompound(it))
			}

			return state
		}
	}
}