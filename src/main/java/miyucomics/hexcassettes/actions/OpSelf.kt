package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import miyucomics.hexcassettes.CassetteCastEnv
import miyucomics.hexpose.iotas.asActionResult
import net.minecraft.text.Text

class OpSelf : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		if (env !is CassetteCastEnv)
			return listOf(NullIota())
		return Text.Serializer.fromJson(env.key)!!.asActionResult
	}
}