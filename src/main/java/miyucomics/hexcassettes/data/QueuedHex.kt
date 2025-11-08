package miyucomics.hexcassettes.data

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.utils.putCompound
import gay.`object`.hexdebug.core.api.HexDebugCoreAPI
import miyucomics.hexcassettes.CassetteCastEnv
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import java.util.*

data class QueuedHex(val hex: NbtCompound, var delay: Int, val depth: Int = 10, var debugSessionId: UUID? = null) {
	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		compound.putCompound("hex", hex)
		compound.putInt("delay", delay)
		compound.putInt("depth", depth)
		debugSessionId?.let { compound.putUuid("debugSessionId", it) }
		return compound
	}

	fun cast(player: ServerPlayerEntity, key: String) {
		val hand = if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty && player.getStackInHand(Hand.OFF_HAND).isEmpty) Hand.OFF_HAND else Hand.MAIN_HAND
		val env = CassetteCastEnv(player, hand, key, depth)
		val image = CastingImage()

		val hexIota = IotaType.deserialize(hex, player.serverWorld) as? ListIota ?: return
		val iotas = hexIota.list.toList()

		if (debugSessionId == null) {
			// cast normally
			CastingVM(image, env).queueExecuteAndWrapIotas(iotas, player.serverWorld)
		} else {
			// try to start debugging, or fail silently if the debug session no longer exists
			debugSessionId
				?.let { HexDebugCoreAPI.INSTANCE.getDebugEnv(player, it) }
				?.let { HexDebugCoreAPI.INSTANCE.startDebuggingIotas(it, env, iotas, image) }
		}
	}

	// if we have a debug session id but HexDebug doesn't know about it, the debug session has been terminated
	// so cancel the cassette immediately
	fun stillValid(player: ServerPlayerEntity) =
		debugSessionId == null || HexDebugCoreAPI.INSTANCE.getDebugEnv(player, debugSessionId!!) != null

	companion object {
		fun deserialize(compound: NbtCompound) =
			QueuedHex(
				compound.getCompound("hex"),
				compound.getInt("delay"),
				compound.getInt("depth"),
				if (compound.contains("debugSessionId")) compound.getUuid("debugSessionId") else null,
			)
	}
}
