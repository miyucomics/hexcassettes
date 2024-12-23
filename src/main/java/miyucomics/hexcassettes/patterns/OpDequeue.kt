package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexcassettes.HexcassettesAPI
import miyucomics.hexcassettes.HexcassettesUtils
import net.minecraft.server.network.ServerPlayerEntity

class OpDequeue : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster != null && caster is ServerPlayerEntity)
			HexcassettesAPI.dequeueByName(caster, HexcassettesUtils.shortenLabel(args[0].display().string))
		return emptyList()
	}
}