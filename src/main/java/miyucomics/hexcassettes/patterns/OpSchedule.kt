package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexcassettes.advancements.QuineCriterion
import miyucomics.hexcassettes.data.QueuedHex
import miyucomics.hexcassettes.data.SilentMarker
import miyucomics.hexcassettes.data.StateStorage
import miyucomics.hexcassettes.inits.HexcassettesAdvancements

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
			if ((ctx as SilentMarker).isDelayCast())
				HexcassettesAdvancements.QUINE.trigger(ctx.caster)
			state.queuedHexes.add(QueuedHex(HexIotaTypes.serialize(hex), delay))
		}
	}
}