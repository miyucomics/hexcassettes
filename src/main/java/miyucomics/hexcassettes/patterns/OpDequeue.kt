package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexcassettes.HexcassettesAPI
import miyucomics.hexcassettes.HexcassettesUtils
import net.minecraft.server.network.ServerPlayerEntity

class OpDequeue : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env !is PlayerBasedCastEnv)
			throw MishapBadCaster()
		return SpellAction.Result(Spell(env.castingEntity as ServerPlayerEntity, HexcassettesUtils.shortenLabel(args[0].display().string)), 0, listOf())
	}

	private data class Spell(val caster: ServerPlayerEntity, val label: String) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			HexcassettesAPI.dequeueByName(caster, label)
		}
	}
}