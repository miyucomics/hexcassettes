package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexcassettes.HexcassettesAPI
import net.minecraft.server.network.ServerPlayerEntity

class OpKillAll : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster != null && caster is ServerPlayerEntity)
			HexcassettesAPI.dequeueAll(caster)
		return emptyList()
	}
}