package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexcassettes.HexcassettesAPI
import miyucomics.hexcassettes.HexcassettesMain

class OpInspect : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
		val label = args[0].display().string
		val shortened = if (label.length > HexcassettesMain.MAX_LABEL_LENGTH) {
			label.substring(0, HexcassettesMain.MAX_LABEL_LENGTH)
		} else {
			label
		}
		val queuedHexes = HexcassettesAPI.getPlayerState(ctx.caster).queuedHexes
		if (queuedHexes.containsKey(shortened))
			return listOf(HexIotaTypes.deserialize(HexcassettesAPI.getPlayerState(ctx.caster).queuedHexes[shortened]!!.hex, ctx.world))
		return listOf(NullIota())
	}
}