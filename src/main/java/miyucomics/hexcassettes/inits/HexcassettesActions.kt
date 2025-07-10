package miyucomics.hexcassettes.inits

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexActions
import miyucomics.hexcassettes.HexcassettesMain
import miyucomics.hexcassettes.patterns.*
import net.minecraft.registry.Registry

object HexcassettesActions {
	@JvmStatic
	fun init() {
		register("enqueue", "qeqwqwqwqwqeqaweqqqqqwweeweweewqdwwewewwewweweww", HexDir.EAST, OpEnqueue())
		register("dequeue", "eqeweweweweqedwqeeeeewwqqwqwqqweawwqwqwwqwwqwqww", HexDir.WEST, OpDequeue())
		register("killall", "eqeweweweweqedwqeeeeewwqqwqwqqw", HexDir.WEST, OpKillAll())
		register("specs", "qeqwqwqwqwqeqaweqqqqq", HexDir.EAST, OpSpecs())
		register("busy", "qeqwqwqwqwqeqaweqqqqqaww", HexDir.EAST, OpBusy())
		register("inspect", "eqeweweweweqedwqeeeee", HexDir.WEST, OpInspect())
		register("foretell", "eqeweweweweqedwqeeeeedww", HexDir.WEST, OpForetell())
		register("self", "qeqwqwqwqwqeqaweqqqqqwweeweweew", HexDir.EAST, OpSelf())
	}

	private fun register(name: String, signature: String, startDir: HexDir, action: Action) =
		Registry.register(HexActions.REGISTRY, HexcassettesMain.id(name), ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action))
}