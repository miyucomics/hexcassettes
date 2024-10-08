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
		val isQuining = (ctx as SilentMarker).isDelayCast()
		if (isQuining)
			HexcassettesAdvancements.QUINE.trigger(ctx.caster)

		// if running quinishly, this hex will die and the new one appears in its place
		val limit = if (isQuining) HexcassettesMain.MAX_CASSETTES  + 1 else HexcassettesMain.MAX_CASSETTES
		if (HexcassettesAPI.getPlayerState(ctx.caster).queuedHexes.size >= limit)
			throw TooManyCassettesMishap()

		args.getList(0, argc)
		val delay = args.getPositiveInt(1, argc)
		val label = args[2].display().string
		val shortened = if (label.length > HexcassettesMain.MAX_LABEL_LENGTH) { label.substring(0, HexcassettesMain.MAX_LABEL_LENGTH) } else { label }
		HexcassettesAPI.scheduleHex(ctx.caster, args[0] as ListIota, delay, shortened)
		return emptyList()
	}
}