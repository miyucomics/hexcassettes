package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.utils.putCompound
import miyucomics.hexcassettes.HexcassettesUtils
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity

data class QueuedHex(val hex: NbtCompound, var delay: Int) {
	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putCompound("hex", hex)
		compound.putInt("delay", delay)
		return compound
	}

	fun cast(player: ServerPlayerEntity) {
		HexcassettesUtils.cast(player.getWorld(), player, hex)
	}

	companion object {
		fun deserialize(compound: NbtCompound) = QueuedHex(compound.getCompound("hex"), compound.getInt("delay"))
	}
}