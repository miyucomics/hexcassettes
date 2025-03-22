package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import miyucomics.hexcassettes.CassetteCastEnv

class OpSelfInfo : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment) = (env as? CassetteCastEnv)?.index?.asActionResult ?: listOf(NullIota())
}