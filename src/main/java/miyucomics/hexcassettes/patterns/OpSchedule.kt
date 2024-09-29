package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import miyucomics.hexcassettes.StateStorage

class OpSchedule : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
		val hex = args.getList(0, argc)
		val delay = args.getPositiveInt(1, argc)
		return Triple(Spell(hex.toList(), delay), 0, listOf())
	}

	private data class Spell(val hex: List<Iota>, val delay: Int) : RenderedSpell {
		override fun cast(ctx: CastingContext) {
			val state = StateStorage.getPlayerState(ctx.caster)
			state.queueHex(hex, delay)
		}
	}
}