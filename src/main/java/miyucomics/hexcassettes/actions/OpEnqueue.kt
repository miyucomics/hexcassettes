package miyucomics.hexcassettes.actions

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
import at.petrak.hexcasting.api.casting.math.EulerPathFinder
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import miyucomics.hexcassettes.CassetteCastEnv
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.PlayerEntityMinterface
import miyucomics.hexcassettes.data.QueuedHex
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
		var key = if (env is CassetteCastEnv && !cassetteState.hexes.containsKey(env.key))
			env.key
		else
			Text.Serializer.toJson(PatternIota.display(EulerPathFinder.findAltDrawing(HexPattern.fromAngles("qeqwqwqwqwqeqaweqqqqqwweeweweewqdwwewewwewweweww", HexDir.EAST), env.world.time)))
		var labelled = 0
		if (stack.last() is TextIota) {
			key = Text.Serializer.toJson((stack.removeLast() as TextIota).text)
			labelled++
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
		cassetteState.hexes[key] = QueuedHex(IotaType.serialize(potentialHex), potentialDelay.double.toInt(), depth)

		if (labelled == 0)
			stack.add(TextIota(Text.Serializer.fromJson(key)!!))
		return OperationResult(image.copy(stack = stack), listOf(), continuation, HexEvalSounds.SPELL)
	}
}

class NoFreeCassettes : Mishap() {
	override fun accentColor(env: CastingEnvironment, errorCtx: Context) = dyeColor(DyeColor.RED)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context) = error(HexcassettesMain.MOD_ID + ":no_free_cassettes")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}