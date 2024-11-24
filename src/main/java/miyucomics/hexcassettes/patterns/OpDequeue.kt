package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import miyucomics.hexcassettes.HexcassettesAPI
import miyucomics.hexcassettes.HexcassettesMain

class OpDequeue : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
		val label = args[0].display().string
		HexcassettesAPI.dequeueByName(ctx.caster, label.substring(HexcassettesMain.MAX_LABEL_LENGTH.coerceAtMost(label.length)))
		return emptyList()
	}
}