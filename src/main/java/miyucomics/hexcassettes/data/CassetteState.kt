package miyucomics.hexcassettes.data

import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import java.util.concurrent.ConcurrentHashMap

class CassetteState {
	var owned = 0
	val hexes: MutableMap<String, QueuedHex> = ConcurrentHashMap()
	private var previouslyActiveSlots: Set<String> = emptySet()

	fun sync(player: ServerPlayerEntity) {
		val buf = PacketByteBufs.create()
		buf.writeInt(owned)
		val keys = hexes.keys
		buf.writeInt(keys.size)
		keys.forEach { buf.writeString(it) }
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

		val activeIndices = hexes.keys.map { it }.toSet()
		if (previouslyActiveSlots != activeIndices)
			sync(player)
		previouslyActiveSlots = activeIndices
	}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putInt("owned", owned)
		val serialized = NbtCompound()
		hexes.forEach { (pattern, hex) -> serialized.put(pattern, hex.serialize()) }
		compound.put("hexes", serialized)
		return compound
	}

	companion object {
		@JvmStatic
		fun deserialize(compound: NbtCompound) = CassetteState().also {
			it.owned = compound.getInt("owned")
			val hexes = compound.getCompound("hexes")
			hexes.keys.forEach { key -> it.hexes[key] = QueuedHex.deserialize(hexes.getCompound(key)) }
		}
	}
}