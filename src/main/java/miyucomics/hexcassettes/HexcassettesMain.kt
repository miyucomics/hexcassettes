package miyucomics.hexcassettes

import miyucomics.hexcassettes.HexcassettesUtils.id
import miyucomics.hexcassettes.advancements.QuineCriterion
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import miyucomics.hexcassettes.inits.HexcassettesPatterns
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.fabricmc.api.ModInitializer
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.util.registry.Registry

class HexcassettesMain : ModInitializer {
	override fun onInitialize() {
		QUINE = Criteria.register(QuineCriterion())
		Registry.register(Registry.ITEM, id("cassette"), CassetteItem())

		HexcassettesNetworking.init()
		HexcassettesPatterns.init()
		HexcassettesSounds.init()
	}

	companion object {
		const val MOD_ID: String = "hexcassettes"
		const val MAX_CASSETTES: Int = 6
		const val MAX_LABEL_LENGTH: Int = 20

		lateinit var QUINE: QuineCriterion
	}
}