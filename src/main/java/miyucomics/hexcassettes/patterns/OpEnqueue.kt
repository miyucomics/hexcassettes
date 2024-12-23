package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import miyucomics.hexcassettes.CassetteCastEnv
import miyucomics.hexcassettes.HexcassettesAPI
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.HexcassettesUtils
import miyucomics.hexcassettes.inits.HexcassettesAdvancements
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.DyeColor

class OpEnqueue : ConstMediaAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster != null && caster is ServerPlayerEntity) {
			val isQuining = env is CassetteCastEnv
			if (isQuining)
				HexcassettesAdvancements.QUINE.trigger(caster)

			val playerState = HexcassettesAPI.getPlayerState(caster)

			// if running quinishly, the new one appears in this one's place so we can allow one extra
			val limit = if (isQuining) playerState.ownedCassettes + 1 else playerState.ownedCassettes
			if (playerState.queuedHexes.size >= limit)
				throw TooManyCassettesMishap()

			args.getList(0, argc)
			val trueName = MishapOthersName.getTrueNameFromDatum(args[0], caster)
			if (trueName != null)
				throw MishapOthersName(trueName)

			val delay = args.getPositiveInt(1, argc)
			HexcassettesAPI.queue(caster, args[0] as ListIota, delay, HexcassettesUtils.shortenLabel(args[2].display().string))
		}

		return emptyList()
	}
}

class TooManyCassettesMishap : Mishap() {
	override fun accentColor(env: CastingEnvironment, errorCtx: Context) = dyeColor(DyeColor.RED)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context) = error(HexcassettesMain.MOD_ID + ":too_many_cassettes")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
		val caster = env.castingEntity
		if (caster != null && caster is ServerPlayerEntity)
			HexcassettesAPI.dequeueAll(caster)
	}
}