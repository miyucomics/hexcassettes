package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import miyucomics.hexcassettes.HexcassettesAPI
import miyucomics.hexcassettes.HexcassettesUtils

class OpForetell : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
		val label = HexcassettesUtils.shortenLabel(args[0].display().string)
		val queuedHexes = HexcassettesAPI.getPlayerState(ctx.caster).queuedHexes
		if (queuedHexes.containsKey(label))
			return HexcassettesAPI.getPlayerState(ctx.caster).queuedHexes[label]!!.delay.asActionResult
		return listOf(NullIota())
	}
}