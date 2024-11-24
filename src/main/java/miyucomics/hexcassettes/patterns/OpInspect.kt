package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexcassettes.HexcassettesAPI
import miyucomics.hexcassettes.HexcassettesUtils

class OpInspect : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
		val label = HexcassettesUtils.shortenLabel(args[0].display().string)
		val queuedHexes = HexcassettesAPI.getPlayerState(ctx.caster).queuedHexes
		if (queuedHexes.containsKey(label))
			return listOf(HexIotaTypes.deserialize(HexcassettesAPI.getPlayerState(ctx.caster).queuedHexes[label]!!.hex, ctx.world))
		return listOf(NullIota())
	}
}