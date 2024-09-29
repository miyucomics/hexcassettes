package miyucomics.hexcassettes

import at.petrak.hexcasting.api.misc.DiscoveryHandlers
import at.petrak.hexcasting.api.misc.HexDamageSources
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.spell.casting.CastingHarness
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import at.petrak.hexcasting.api.utils.compareMediaItem
import at.petrak.hexcasting.api.utils.extractMedia
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import kotlin.math.ceil
import kotlin.math.max

object CastingUtils {
	@JvmStatic
	fun takeMediaFromInventory(harness: CastingHarness, cost: Int): Int {
		var remainingCost = cost

		for (source in DiscoveryHandlers.collectMediaHolders(harness).sortedWith(Comparator(::compareMediaItem).reversed())) {
			remainingCost -= extractMedia(source, remainingCost, simulate = false)
			if (remainingCost <= 0)
				break
		}

		if (remainingCost > 0) {
			val mediaToHealth = HexConfig.common().mediaToHealthRate()
			val requiredBloodMedia = max(remainingCost.toDouble() / mediaToHealth, 0.5)
			val availableBloodMedia = harness.ctx.caster.health * mediaToHealth
			Mishap.trulyHurt(harness.ctx.caster, HexDamageSources.OVERCAST, requiredBloodMedia.toFloat())
			remainingCost -= ceil(availableBloodMedia - (harness.ctx.caster.health * mediaToHealth)).toInt()
		}

		return remainingCost
	}

	@JvmStatic
	fun castFromInventory(world: ServerWorld, user: ServerPlayerEntity, hex: List<Iota>): CastingHarness {
		val harness = IXplatAbstractions.INSTANCE.getHarness(user, Hand.MAIN_HAND)
		(harness.ctx as SilentMarker).delayCast()
		harness.stack = mutableListOf()
		harness.executeIotas(hex, world)
		return harness
	}
}