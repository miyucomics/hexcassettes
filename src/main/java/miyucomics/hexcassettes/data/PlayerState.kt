package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import miyucomics.hexcassettes.CastingUtils.cast
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity

class PlayerState {
	val queuedHexes: MutableList<QueuedHex> = mutableListOf()

	fun tick(player: ServerPlayerEntity) {
		val count = queuedHexes.size - 1
		for (i in 0..count) {
			queuedHexes[i].delay -= 1
			if (queuedHexes[i].delay == 0)
				cast(player.getWorld(), player, queuedHexes[i].hex)
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
			serializedHexes.forEach { hex ->
				state.queuedHexes.add(QueuedHex.deserialize(hex as NbtCompound))
			}
			return state
		}
	}
}

data class QueuedHex(val hex: NbtCompound, var delay: Int) {
	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putCompound("hex", hex)
		compound.putInt("delay", delay)
		return compound
	}

	companion object {
		fun deserialize(compound: NbtCompound) = QueuedHex(compound.getCompound("hex"), compound.getInt("delay"))
	}
}