package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexcassettes.PlayerEntityMinterface
import miyucomics.hexpose.iotas.getText
import net.minecraft.text.Text

class OpDequeue : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env !is PlayerBasedCastEnv)
			throw MishapBadCaster()
		return SpellAction.Result(Spell(args.getText(0, argc)), 0, listOf())
	}

	private data class Spell(val pattern: Text) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			(env.castingEntity as PlayerEntityMinterface).getCassetteState().hexes.remove(Text.Serializer.toJson(pattern))
		}
	}
}