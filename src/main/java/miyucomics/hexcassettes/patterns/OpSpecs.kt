package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import miyucomics.hexcassettes.HexcassettesAPI
import net.minecraft.server.network.ServerPlayerEntity

class OpSpecs : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster != null && caster is ServerPlayerEntity)
			return HexcassettesAPI.getPlayerState(caster).ownedCassettes.asActionResult
		return listOf(NullIota())
	}
}