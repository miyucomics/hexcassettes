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
		val shortened = if (label.length > HexcassettesMain.MAX_LABEL_LENGTH) {
			label.substring(0, HexcassettesMain.MAX_LABEL_LENGTH)
		} else {
			label
		}
		HexcassettesAPI.removeWithLabel(ctx.caster, shortened)
		return emptyList()
	}
}