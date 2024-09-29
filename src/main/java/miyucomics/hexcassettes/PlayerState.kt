package miyucomics.hexcassettes

import at.petrak.hexcasting.api.spell.iota.Iota
import miyucomics.hexcassettes.CastingUtils.castFromInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

class PlayerState {
	private val hexes: MutableList<QueuedHex> = mutableListOf()

	fun queueHex(hex: List<Iota>, duration: Int) {
		hexes.add(QueuedHex(hex, duration))
	}

	fun tick(player: PlayerEntity) {
		hexes.forEach { hex ->
			hex.duration -= 1
			if (hex.duration == 0)
				castFromInventory(player.world as ServerWorld, player as ServerPlayerEntity, hex.hex)
		}
		hexes.removeIf { hex -> hex.duration <= 0 }
	}

	fun serialize(): NbtCompound {
		return NbtCompound()
	}

	companion object {
		fun deserialize(compound: NbtCompound): PlayerState {
			return PlayerState()
		}
	}
}

data class QueuedHex(val hex: List<Iota>, var duration: Int)