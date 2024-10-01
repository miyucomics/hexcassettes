package miyucomics.hexcassettes

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexcassettes.CastingUtils.castFromInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

class PlayerState {
	val hexes: List<QueuedHex> = mutableListOf()

	fun tick(player: PlayerEntity) {
		hexes.forEach { hex ->
			hex.delay -= 1
			if (hex.delay == 0)
				castFromInventory(player.world as ServerWorld, player as ServerPlayerEntity, hex.hex.list.toList())
		}
	}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		return NbtCompound()
	}

	companion object {
		fun deserialize(compound: NbtCompound): PlayerState {
			return PlayerState()
		}
	}
}

data class QueuedHex(val hex: ListIota, var delay: Int) {
	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putCompound("hex", HexIotaTypes.serialize(hex))
		compound.putInt("delay", delay)
		return compound
	}
}