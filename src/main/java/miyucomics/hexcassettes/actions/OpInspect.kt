package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexcassettes.PlayerEntityMinterface
import miyucomics.hexpose.iotas.getText
import net.minecraft.text.Text

class OpInspect : ConstMediaAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val player = args.getPlayer(0, argc)
		val pattern = Text.Serializer.toJson(args.getText(1, argc))
		return (player as PlayerEntityMinterface).getCassetteState().hexes.containsKey(pattern).asActionResult
	}
}