package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.data.HexcassettesAPI

class OpFreeSlots : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, ctx: CastingContext) = (HexcassettesMain.MAX_CASSETTES - HexcassettesAPI.getPlayerState(ctx.caster).queuedHexes.size).asActionResult
}