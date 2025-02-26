package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import miyucomics.hexcassettes.CassetteCastEnv
import miyucomics.hexcassettes.HexcassettesAPI
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.HexcassettesUtils
import miyucomics.hexcassettes.inits.HexcassettesAdvancements
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.DyeColor

class OpEnqueue : SpellAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env !is PlayerBasedCastEnv)
			throw MishapBadCaster()
		val caster = (env.castingEntity as ServerPlayerEntity)

		val isQuining = env is CassetteCastEnv
		if (isQuining)
			HexcassettesAdvancements.QUINE.trigger(caster)

		val playerState = HexcassettesAPI.getPlayerState(caster)
		if (playerState.queuedHexes.size >= playerState.ownedCassettes)
			throw NotEnoughCassettes()

		args.getList(0, argc)
		val trueName = MishapOthersName.getTrueNameFromDatum(args[0], caster)
		if (trueName != null)
			throw MishapOthersName(trueName)

		return SpellAction.Result(Spell(caster, args[0] as ListIota, args.getPositiveInt(1, argc), HexcassettesUtils.shortenLabel(args[2].display().string)), 0, listOf())
	}

	private data class Spell(val caster: ServerPlayerEntity, val hex: ListIota, val delay: Int, val label: String) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			HexcassettesAPI.queue(caster, hex, delay, label)
		}
	}
}

class NotEnoughCassettes : Mishap() {
	override fun accentColor(env: CastingEnvironment, errorCtx: Context) = dyeColor(DyeColor.RED)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context) = error(HexcassettesMain.MOD_ID + ":too_many_cassettes")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
		val caster = env.castingEntity
		if (caster != null && caster is ServerPlayerEntity)
			HexcassettesAPI.dequeueAll(caster)
	}
}