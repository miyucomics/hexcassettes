package miyucomics.hexcassettes.inits

import miyucomics.hexcassettes.advancements.QuineCriterion
import net.minecraft.advancement.criterion.Criteria

object HexcassettesAdvancements {
	lateinit var QUINE: QuineCriterion

	@JvmStatic
	fun init() {
		QUINE = Criteria.register(QuineCriterion())
	}
}