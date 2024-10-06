package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import miyucomics.hexcassettes.data.QueuedHex
import miyucomics.hexcassettes.data.StateStorage

class OpSchedule : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
		args.getList(0, argc)
		val delay = args.getPositiveInt(1, argc)
		return Triple(Spell(args[0] as ListIota, delay), 0, listOf())
	}

	private data class Spell(val hex: ListIota, val delay: Int) : RenderedSpell {
		override fun cast(ctx: CastingContext) {
			val state = StateStorage.getPlayerState(ctx.caster)
			state.queuedHexes.add(QueuedHex(hex, delay))
		}
	}
}