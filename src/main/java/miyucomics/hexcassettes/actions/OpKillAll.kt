package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexcassettes.PlayerEntityMinterface
import miyucomics.hexcassettes.data.CassetteState
import net.minecraft.server.network.ServerPlayerEntity

class OpKillAll : SpellAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val caster = env.castingEntity
		if (caster !is PlayerEntityMinterface)
			throw MishapBadCaster()
		return SpellAction.Result(Spell(caster.getCassetteState()), 0, listOf())
	}

	private data class Spell(val state: CassetteState) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			state.hexes.clear()
		}
	}
}