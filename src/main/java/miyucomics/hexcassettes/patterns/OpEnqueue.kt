package miyucomics.hexcassettes.patterns

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import miyucomics.hexcassettes.CassetteCastEnv
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.PlayerEntityMinterface
import miyucomics.hexcassettes.data.QueuedHex
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.DyeColor

class OpEnqueue : Action {
	override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
		if (env !is PlayerBasedCastEnv)
			throw MishapBadCaster()
		if (env is CassetteCastEnv)
			HexcassettesMain.QUINE.trigger(env.castingEntity as ServerPlayerEntity)
		val cassetteState = (env.castingEntity as PlayerEntityMinterface).getCassetteState()

		val stack = image.stack.toMutableList()
		val delay = stack.removeLast()
		if (delay !is DoubleIota || delay.double <= 0)
			throw MishapInvalidIota.of(delay, 0, "double.positive")
		val delayValue = delay.double.toInt()

		when (val next = stack.removeLast()) {
			is ListIota -> {
				val index = if (env is CassetteCastEnv) env.pattern else cassetteState.queuedHexes.indexOfFirst { it == null }
				if (cassetteState.queuedHexes.keys.size >= HexcassettesMain.MAX_CASSETTES)
					throw NoFreeCassettes()
				cassetteState.queuedHexes[index] = QueuedHex(IotaType.serialize(next), delayValue)
				stack.add(PatternIota(index))
			}
			is PatternIota -> {
				val key = next.pattern
				if (!cassetteState.queuedHexes.keys.contains(key) && cassetteState.queuedHexes.keys.size >= HexcassettesMain.MAX_CASSETTES)
					throw NotEnoughCassettes()

				val hex = stack.removeLast()
				if (hex !is ListIota)
					throw MishapInvalidIota.ofType(hex, 2, "list")

				cassetteState.queuedHexes[key] = QueuedHex(IotaType.serialize(hex), delayValue)
			}
			else -> throw MishapInvalidIota.of(next, 1, "hex_or_key")
		}

		return OperationResult(image.copy(stack = stack), listOf(), continuation, HexEvalSounds.SPELL)
	}
}

class NoFreeCassettes : Mishap() {
	override fun accentColor(env: CastingEnvironment, errorCtx: Context) = dyeColor(DyeColor.RED)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context) = error(HexcassettesMain.MOD_ID + ":no_free_cassettes")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}

class NotEnoughCassettes : Mishap() {
	override fun accentColor(env: CastingEnvironment, errorCtx: Context) = dyeColor(DyeColor.RED)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context) = error(HexcassettesMain.MOD_ID + ":not_enough_cassettes")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
		val caster = env.castingEntity
		if (caster is ServerPlayerEntity)
			(caster as PlayerEntityMinterface).getCassetteState().queuedHexes.replaceAll { null }
	}
}