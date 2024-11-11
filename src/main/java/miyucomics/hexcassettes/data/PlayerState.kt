package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity

class PlayerState {
	var ownedCassettes = 0
	val queuedHexes: MutableMap<String, QueuedHex> = mutableMapOf()

	fun tick(player: ServerPlayerEntity) {
		queuedHexes.forEach { (label, hex) ->
			hex.delay -= 1
			if (hex.delay <= 0) {
				val buf = PacketByteBufs.create()
				buf.writeString(label)
				ServerPlayNetworking.send(player, HexcassettesNetworking.CASSETTE_REMOVE, buf)
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
		val serializedHexes = NbtList()
		queuedHexes.forEach { (label, queuedHex) ->
			val queued = NbtCompound()
			queued.putString("label", label)
			queued.putCompound("hex", queuedHex.serialize())
			serializedHexes.add(queued)
		}
		compound.putList("hexes", serializedHexes)
		return compound
	}

	companion object {
		fun deserialize(compound: NbtCompound): PlayerState {
			val state = PlayerState()
			state.ownedCassettes = compound.getInt("owned")
			val serializedHexes = compound.getList("hexes", NbtElement.COMPOUND_TYPE.toInt())
			serializedHexes.forEach { hex -> state.queuedHexes[hex.asCompound.getString("label")] = QueuedHex.deserialize(hex.asCompound.getCompound("hex")) }
			return state
		}
	}
}