package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.casting.CastingHarness
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
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
		val hand = if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty && player.getStackInHand(Hand.OFF_HAND).isEmpty) Hand.OFF_HAND else Hand.MAIN_HAND
		val harness = CastingHarness(CastingContext(player, hand, CastingContext.CastSource.PACKAGED_HEX))
		(harness.ctx as SilentMarker).delayCast()
		harness.stack = mutableListOf()
		val actualHex = HexIotaTypes.deserialize(hex, player.getWorld())
		if (actualHex is ListIota)
			harness.executeIotas(actualHex.list.toList(), player.getWorld())
	}

	companion object {
		fun deserialize(compound: NbtCompound) = QueuedHex(compound.getCompound("hex"), compound.getInt("delay"))
	}
}