package miyucomics.hexcassettes

import at.petrak.hexcasting.api.misc.DiscoveryHandlers
import at.petrak.hexcasting.api.misc.HexDamageSources
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.spell.casting.CastingHarness
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import at.petrak.hexcasting.api.utils.compareMediaItem
import at.petrak.hexcasting.api.utils.extractMedia
import net.minecraft.util.Identifier
import kotlin.math.ceil
import kotlin.math.max

object HexcassettesUtils {
	fun id(string: String) = Identifier(HexcassettesMain.MOD_ID, string)
	fun shortenLabel(label: String) = label.substring(0, HexcassettesMain.MAX_LABEL_LENGTH.coerceAtMost(label.length))

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
}