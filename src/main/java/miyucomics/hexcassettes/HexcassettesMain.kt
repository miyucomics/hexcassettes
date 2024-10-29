package miyucomics.hexcassettes

import com.google.gson.JsonObject
import miyucomics.hexcassettes.HexcassettesUtils.id
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import miyucomics.hexcassettes.inits.HexcassettesPatterns
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.fabricmc.api.ModInitializer
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
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

// kinda messy, but I don't want to make a whole file for this
class QuineCriterion : AbstractCriterion<QuineCriterion.Condition>() {
	override fun conditionsFromJson(obj: JsonObject, playerPredicate: EntityPredicate.Extended, predicateDeserializer: AdvancementEntityPredicateDeserializer) = Condition()
	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId() = ID

	class Condition : AbstractCriterionConditions(ID, EntityPredicate.Extended.EMPTY)
	companion object {
		val ID: Identifier = id("quinio")
	}
}