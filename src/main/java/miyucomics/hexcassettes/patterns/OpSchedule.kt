package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.data.HexcassettesAPI
import miyucomics.hexcassettes.data.SilentMarker
import miyucomics.hexcassettes.inits.HexcassettesAdvancements

class OpSchedule : ConstMediaAction {
	override val argc = 3
	override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
		args.getList(0, argc)
		val delay = args.getPositiveInt(1, argc)
		val label = args[2].display().string
		if ((ctx as SilentMarker).isDelayCast())
			HexcassettesAdvancements.QUINE.trigger(ctx.caster)
		val shortened = if (label.length > HexcassettesMain.MAX_LABEL_LENGTH) { label.substring(0, HexcassettesMain.MAX_LABEL_LENGTH) } else { label }
		HexcassettesAPI.scheduleHex(ctx.caster, args[0] as ListIota, delay, shortened)
		return emptyList()
	}
}