package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.getPositiveIntUnder
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.PlayerEntityMinterface

class OpForetell : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		if (env !is PlayerBasedCastEnv)
			throw MishapBadCaster()
		val queuedHexes = (env.castingEntity as PlayerEntityMinterface).getCassetteState().queuedHexes
		val index = args.getPositiveIntUnder(0, HexcassettesMain.MAX_CASSETTES, argc)
		if (queuedHexes[index] != null)
			return listOf(DoubleIota(queuedHexes[index]!!.delay.toDouble()))
		return listOf(NullIota())
	}
}