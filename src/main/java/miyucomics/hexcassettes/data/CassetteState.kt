package miyucomics.hexcassettes.data

import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity

class CassetteState {
	var ownedSlots = 0
	val queuedHexes: MutableList<QueuedHex?> = MutableList(HexcassettesMain.MAX_CASSETTES) { null }
	private var previouslyActiveSlots: Set<Int> = emptySet()

	fun sync(player: ServerPlayerEntity) {
		val buf = PacketByteBufs.create()
		buf.writeInt(ownedSlots)
		val occupied = queuedHexes.mapIndexedNotNull { index, hex -> if (hex != null) index else null }.toSet()
		buf.writeInt(occupied.size)
		occupied.forEach { buf.writeInt(it) }
		ServerPlayNetworking.send(player, HexcassettesNetworking.SYNC_CASSETTES, buf)
	}

	fun tick(player: ServerPlayerEntity) {
		val activeIndices = queuedHexes.mapIndexedNotNull { index, hex -> if (hex != null) index else null }.toSet()
		if (previouslyActiveSlots != activeIndices)
			sync(player)
		previouslyActiveSlots = activeIndices

		for (hex in queuedHexes) {
			if (hex != null) {
				hex.delay -= 1
			}
		}

		for (i in queuedHexes.indices) {
			val hex = queuedHexes[i]
			if (hex != null && hex.delay <= 0) {
				queuedHexes[i] = null
				hex.cast(player, i)
			}
		}
	}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putInt("owned", ownedSlots)

		val hexes = NbtList()
		queuedHexes.forEachIndexed { index, queuedHex ->
			if (queuedHex != null) {
				val hex = NbtCompound()
				hex.putInt("index", index)
				hex.put("hex", queuedHex.serialize())
				hexes.add(hex)
			}
		}

		compound.put("hexes", hexes)
		return compound
	}

	companion object {
		@JvmStatic
		fun deserialize(compound: NbtCompound): CassetteState {
			val state = CassetteState()
			state.ownedSlots = compound.getInt("owned")

			val hexes = compound.getList("hexes", NbtElement.COMPOUND_TYPE.toInt())
			hexes.forEach { element ->
				val hexCompound = element as NbtCompound
				val index = hexCompound.getInt("index")
				val hex = QueuedHex.deserialize(hexCompound.getCompound("hex"))

				if (index in state.queuedHexes.indices)
					state.queuedHexes[index] = hex
			}

			return state
		}
	}
}