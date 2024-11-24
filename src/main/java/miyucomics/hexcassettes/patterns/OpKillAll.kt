package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import miyucomics.hexcassettes.HexcassettesAPI

class OpKillAll : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
		HexcassettesAPI.dequeueAll(ctx.caster)
		return emptyList()
	}
}