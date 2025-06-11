package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexcassettes.PlayerEntityMinterface

class OpDequeue : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env !is PlayerBasedCastEnv)
			throw MishapBadCaster()
		return SpellAction.Result(Spell(args.getPattern(0, argc)), 0, listOf())
	}

	private data class Spell(val pattern: HexPattern) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			(env.castingEntity as PlayerEntityMinterface).getCassetteState().queuedHexes.remove(pattern)
		}
	}
}