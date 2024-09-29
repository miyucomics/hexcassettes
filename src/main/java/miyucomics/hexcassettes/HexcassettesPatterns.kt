package miyucomics.hexcassettes

import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.math.HexDir
import at.petrak.hexcasting.api.spell.math.HexPattern
import miyucomics.hexcassettes.patterns.OpSchedule

object HexcassettesPatterns {
	@JvmStatic
	fun init() {
		register("queue", "wqqqqq", HexDir.EAST, OpSchedule())
	}

	private fun register(name: String, signature: String, startDir: HexDir, action: Action) = PatternRegistry.mapPattern(HexPattern.fromAngles(signature, startDir), HexcassettesMain.id(name), action)
}