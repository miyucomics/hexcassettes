package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import miyucomics.hexcassettes.HexcassettesAPI
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity

class PlayerState {
	var ownedCassettes = 0
	val queuedHexes: MutableMap<String, QueuedHex> = mutableMapOf()
	private var previouslyActiveCassettes: MutableSet<String> = mutableSetOf()

	fun tick(player: ServerPlayerEntity) {
		if (previouslyActiveCassettes != queuedHexes.keys)
			HexcassettesAPI.sendSyncPacket(player)
		previouslyActiveCassettes = queuedHexes.keys.toMutableSet()

		queuedHexes.forEach { (label, hex) ->
			hex.delay -= 1
			if (hex.delay <= 0) {
				val buf = PacketByteBufs.create()
				buf.writeString(label)
				hex.cast(player)
			}
		}

		val iterator = queuedHexes.iterator()
		while (iterator.hasNext())
			if (iterator.next().value.delay <= 0)
				iterator.remove()
	}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putInt("owned", ownedCassettes)
		val hexes = NbtList()
		queuedHexes.forEach { (label, queuedHex) ->
			val hex = NbtCompound()
			hex.putString("label", label)
			hex.putCompound("hex", queuedHex.serialize())
			hexes.add(hex)
		}
		compound.putList("hexes", hexes)
		return compound
	}

	companion object {
		fun deserialize(compound: NbtCompound): PlayerState {
			val state = PlayerState()
			state.ownedCassettes = compound.getInt("owned")
			compound.getList("hexes", NbtElement.COMPOUND_TYPE.toInt()).forEach { hex ->
				state.queuedHexes[hex.asCompound.getString("label")] = QueuedHex.deserialize(hex.asCompound.getCompound("hex"))
			}
			return state
		}
	}
}