package miyucomics.hexcassettes.inits

import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.math.HexDir
import at.petrak.hexcasting.api.spell.math.HexPattern
import miyucomics.hexcassettes.HexcassettesUtils
import miyucomics.hexcassettes.patterns.OpFreeSlots
import miyucomics.hexcassettes.patterns.OpKill
import miyucomics.hexcassettes.patterns.OpKillAll
import miyucomics.hexcassettes.patterns.OpSchedule

object HexcassettesPatterns {
	@JvmStatic
	fun init() {
		register("queue", "qqwqwqwqqwqawa", HexDir.WEST, OpSchedule())
		register("kill", "eeweweweewedwd", HexDir.EAST, OpKill())
		register("killall", "eeweweweeweewdwe", HexDir.EAST, OpKillAll())
		
		register("free_slots", "qqwqwqwqqwqqadaq", HexDir.WEST, OpFreeSlots())
	}

	private fun register(name: String, signature: String, startDir: HexDir, action: Action) = PatternRegistry.mapPattern(HexPattern.fromAngles(signature, startDir), HexcassettesUtils.id(name), action)
}