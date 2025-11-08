package miyucomics.hexcassettes.actions

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.*
import at.petrak.hexcasting.api.casting.math.EulerPathFinder
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import gay.`object`.hexdebug.core.api.HexDebugCoreAPI
import gay.`object`.hexdebug.core.api.exceptions.DebugException
import miyucomics.hexcassettes.CassetteCastEnv
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.PlayerEntityMinterface
import miyucomics.hexcassettes.data.QueuedHex
import miyucomics.hexcassettes.interop.hexdebug.CassetteDebugEnv
import miyucomics.hexpose.iotas.TextIota
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import kotlin.math.roundToInt

class OpEnqueue : Action {
	override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
		if (env !is PlayerBasedCastEnv)
			throw MishapBadCaster()
		if (image.stack.size < 2)
			throw MishapNotEnoughArgs(2, image.stack.size)
		val cassetteState = (env.castingEntity as PlayerEntityMinterface).getCassetteState()

		val stack = image.stack.toMutableList()

		val key: String
		val keyText: Text
		val labelled: Int
		when {
			// get key from stack
			stack.last() is TextIota -> {
				keyText = (stack.removeLast() as TextIota).text
				key = Text.Serializer.toJson(keyText)
				labelled = 1
			}

			// get key from current cassette
			env is CassetteCastEnv && !cassetteState.hexes.containsKey(env.key) -> {
				key = env.key
				keyText = Text.Serializer.fromJson(key)!!
				labelled = 0
			}

			// generate random key
			else -> {
				keyText = PatternIota.display(EulerPathFinder.findAltDrawing(HexPattern.fromAngles("qeqwqwqwqwqeqaweqqqqqwweeweweewqdwwewewwewweweww", HexDir.EAST), env.world.time))
				key = Text.Serializer.toJson(keyText)
				labelled = 0
			}
		}

		val potentialDelay = stack.removeLast()
		if (potentialDelay !is DoubleIota || potentialDelay.double - potentialDelay.double.roundToInt() > DoubleIota.TOLERANCE || potentialDelay.double.roundToInt() <= 0)
			throw MishapInvalidIota.of(potentialDelay, labelled, "int.positive")

		val potentialHex = stack.removeLast()
		if (potentialHex !is ListIota)
			throw MishapInvalidIota.ofType(potentialHex, labelled + 1, "list")

		if (!cassetteState.hexes.containsKey(key) && cassetteState.hexes.keys.size >= cassetteState.owned)
			throw NoFreeCassettes()

		var depth = 0
		if (env is CassetteCastEnv && env.depth < 10)
			depth = env.depth + 1

		val queuedHex = QueuedHex(IotaType.serialize(potentialHex), potentialDelay.double.toInt(), depth)

		// if the current cast is being debugged, try to create the cassette in debug mode too
		if (HexDebugCoreAPI.INSTANCE.getDebugEnv(env) != null) {
			val debugEnv = CassetteDebugEnv(env.caster!!, key, keyText, queuedHex)
			try {
				HexDebugCoreAPI.INSTANCE.createDebugThread(debugEnv, null)
				queuedHex.debugSessionId = debugEnv.sessionId
			} catch (_: DebugException) {}
		}

		cassetteState.hexes[key] = queuedHex

		if (labelled == 0)
			stack.add(TextIota(keyText))
		return OperationResult(image.copy(stack = stack), listOf(), continuation, HexEvalSounds.SPELL)
	}
}

class NoFreeCassettes : Mishap() {
	override fun accentColor(env: CastingEnvironment, errorCtx: Context) = dyeColor(DyeColor.RED)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context) = error(HexcassettesMain.MOD_ID + ":no_free_cassettes")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}
