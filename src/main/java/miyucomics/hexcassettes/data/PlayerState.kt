package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.utils.putList
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity

class PlayerState {
	var ownedCassettes = 0
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
		compound.putInt("owned", ownedCassettes)
		val serializedHexes = NbtList()
		queuedHexes.forEach { queuedHex -> serializedHexes.add(queuedHex.serialize()) }
		compound.putList("hexes", serializedHexes)
		return compound
	}

	companion object {
		fun deserialize(compound: NbtCompound): PlayerState {
			val state = PlayerState()
			state.ownedCassettes = compound.getInt("owned")
			val serializedHexes = compound.getList("hexes", NbtElement.COMPOUND_TYPE.toInt())
			serializedHexes.forEach { hex -> state.queuedHexes.add(QueuedHex.deserialize(hex as NbtCompound)) }
			return state
		}
	}
}