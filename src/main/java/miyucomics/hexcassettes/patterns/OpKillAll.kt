package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexcassettes.HexcassettesAPI
import net.minecraft.server.network.ServerPlayerEntity

class OpKillAll : SpellAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env !is PlayerBasedCastEnv)
			throw MishapBadCaster()
		return SpellAction.Result(Spell(env.castingEntity as ServerPlayerEntity), 0, listOf())
	}

	private data class Spell(val caster: ServerPlayerEntity) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			HexcassettesAPI.dequeueAll(caster)
		}
	}
}