package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexcassettes.CastingUtils.cast
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

class PlayerState {
	val queuedHexes: MutableList<QueuedHex> = mutableListOf()

	fun tick(player: ServerPlayerEntity) {
		queuedHexes.forEach { hex ->
			hex.delay -= 1
			if (hex.delay == 0)
				hex.cast(player)
		}
	}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		val serializedHexes = NbtList()
		queuedHexes.forEach { queuedHex -> serializedHexes.add(queuedHex.serialize()) }
		compound.putList("hexes", serializedHexes)
		return NbtCompound()
	}

	companion object {
		fun deserialize(compound: NbtCompound): PlayerState {
			val state = PlayerState()
			compound.getList("hexes", NbtElement.LIST_TYPE.toInt()).forEach { hex -> state.queuedHexes.add(QueuedHex.deserialize(hex as NbtCompound)) }
			return state
		}
	}
}

data class QueuedHex(val hex: NbtCompound, var delay: Int) {
	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putInt("delay", delay)
		compound.putCompound("hex", hex)
		return compound
	}

	fun cast(player: ServerPlayerEntity) {
		val world = player.world as ServerWorld
		cast(world, player, (HexIotaTypes.deserialize(hex, world) as ListIota).list.toList())
	}

	companion object {
		fun deserialize(compound: NbtCompound) = QueuedHex(compound.getCompound("hex"), compound.getInt("delay"))
	}
}