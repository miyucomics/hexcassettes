package miyucomics.hexcassettes.inits

import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.math.HexDir
import at.petrak.hexcasting.api.spell.math.HexPattern
import miyucomics.hexcassettes.HexcassettesUtils
import miyucomics.hexcassettes.patterns.*

object HexcassettesPatterns {
	@JvmStatic
	fun init() {
		register("enqueue", "qeqwqwqwqwqeqaweqqqqqwweeweweewqdwwewewwewweweww", HexDir.EAST, OpEnqueue())
		register("dequeue", "eqeweweweweqedwqeeeeewwqqwqwqqweawwqwqwwqwwqwqww", HexDir.WEST, OpDequeue())
		register("killall", "eqeweweweweqedwqeeeeewwqqwqwqqw", HexDir.WEST, OpKillAll())
		register("specs", "qeqwqwqwqwqeqaweqqqqq", HexDir.EAST, OpSpecs())
		register("free", "qeqwqwqwqwqeqaweqqqqqwweeweweew", HexDir.EAST, OpFree())
	}

	private fun register(name: String, signature: String, startDir: HexDir, action: Action) =
		PatternRegistry.mapPattern(HexPattern.fromAngles(signature, startDir), HexcassettesUtils.id(name), action)
}