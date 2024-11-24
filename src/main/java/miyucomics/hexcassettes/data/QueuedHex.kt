package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand

data class QueuedHex(val hex: NbtCompound, var delay: Int) {
	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putCompound("hex", hex)
		compound.putInt("delay", delay)
		return compound
	}

	fun cast(player: ServerPlayerEntity) {
		val harness = IXplatAbstractions.INSTANCE.getHarness(player, Hand.MAIN_HAND)
		(harness.ctx as SilentMarker).delayCast()
		harness.stack = mutableListOf()
		harness.executeIotas((HexIotaTypes.deserialize(hex, player.getWorld()) as ListIota).list.toList(), player.getWorld())
	}

	companion object {
		fun deserialize(compound: NbtCompound) = QueuedHex(compound.getCompound("hex"), compound.getInt("delay"))
	}
}