package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexcassettes.PlayerEntityMinterface
import miyucomics.hexpose.iotas.getText
import net.minecraft.text.Text

class OpForetell : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		if (env !is PlayerBasedCastEnv)
			throw MishapBadCaster()
		val queuedHexes = (env.castingEntity as PlayerEntityMinterface).getCassetteState().hexes
		val pattern = Text.Serializer.toJson(args.getText(0, argc))
		return queuedHexes[pattern]?.delay?.asActionResult ?: null.asActionResult
	}
}