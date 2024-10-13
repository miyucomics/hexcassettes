package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import miyucomics.hexcassettes.data.HexcassettesAPI

class OpFreeSlots : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
		val playerState = HexcassettesAPI.getPlayerState(ctx.caster)
		return (playerState.ownedCassettes - playerState.queuedHexes.size).asActionResult
	}
}