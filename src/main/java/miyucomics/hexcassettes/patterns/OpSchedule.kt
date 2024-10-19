package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.data.HexcassettesAPI
import miyucomics.hexcassettes.data.SilentMarker

class OpSchedule : ConstMediaAction {
	override val argc = 3
	override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
		val isQuining = (ctx as SilentMarker).isDelayCast()
		if (isQuining)
			HexcassettesMain.QUINE.trigger(ctx.caster)

		val playerState = HexcassettesAPI.getPlayerState(ctx.caster)

		// if running quinishly, the new one appears in this one's place so we can allow one extra
		val limit = if (isQuining) playerState.ownedCassettes  + 1 else playerState.ownedCassettes
		if (playerState.queuedHexes.size >= limit)
			throw TooManyCassettesMishap()


		args.getList(0, argc)
		val trueName = MishapOthersName.getTrueNameFromDatum(args[0], ctx.caster)
		if (trueName != null)
			throw MishapOthersName(trueName)

		val delay = args.getPositiveInt(1, argc)
		val label = args[2].display().string
		val shortened = if (label.length > HexcassettesMain.MAX_LABEL_LENGTH) { label.substring(0, HexcassettesMain.MAX_LABEL_LENGTH) } else { label }
		HexcassettesAPI.scheduleHex(ctx.caster, args[0] as ListIota, delay, shortened)
		return emptyList()
	}
}