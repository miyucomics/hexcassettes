package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import miyucomics.hexcassettes.data.StateStorage

class OpKillAll : SpellAction {
	override val argc = 0
	override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
		return Triple(Spell(0), 0, listOf())
	}

	private data class Spell(val int: Int) : RenderedSpell {
		override fun cast(ctx: CastingContext) {
			val state = StateStorage.getPlayerState(ctx.caster)
			state.queuedHexes.clear()
		}
	}
}