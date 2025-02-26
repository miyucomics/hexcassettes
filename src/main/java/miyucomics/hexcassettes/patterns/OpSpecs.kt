package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexcassettes.HexcassettesAPI
import net.minecraft.server.network.ServerPlayerEntity

class OpSpecs : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		if (env !is PlayerBasedCastEnv)
			throw MishapBadCaster()
		return HexcassettesAPI.getPlayerState(env.castingEntity as ServerPlayerEntity).ownedCassettes.asActionResult
	}
}