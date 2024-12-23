package miyucomics.hexcassettes.inits

import com.google.gson.JsonObject
import miyucomics.hexcassettes.HexcassettesUtils.id
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object HexcassettesAdvancements {
	@JvmStatic
	fun init() {
		QUINE = Criteria.register(QuineCriterion())
		TAPE_WORM = Criteria.register(TapeWormCriterion())
		FULL_ARSENAL = Criteria.register(FullArsenalCriterion())
	}

	lateinit var QUINE: QuineCriterion
	lateinit var TAPE_WORM: TapeWormCriterion
	lateinit var FULL_ARSENAL: FullArsenalCriterion
}


class QuineCriterion : AbstractCriterion<QuineCriterion.Condition>() {
	override fun conditionsFromJson(jsonObject: JsonObject, lootContextPredicate: LootContextPredicate, advancementEntityPredicateDeserializer: AdvancementEntityPredicateDeserializer) = Condition()
	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId() = ID

	class Condition : AbstractCriterionConditions(ID, LootContextPredicate.EMPTY)
	companion object {
		val ID: Identifier = id("quinio")
	}
}

class TapeWormCriterion : AbstractCriterion<TapeWormCriterion.Condition>() {
	override fun conditionsFromJson(jsonObject: JsonObject, lootContextPredicate: LootContextPredicate, advancementEntityPredicateDeserializer: AdvancementEntityPredicateDeserializer) = Condition()
	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId() = ID

	class Condition : AbstractCriterionConditions(ID, LootContextPredicate.EMPTY)
	companion object {
		val ID: Identifier = id("tape_worm")
	}
}

class FullArsenalCriterion : AbstractCriterion<FullArsenalCriterion.Condition>() {
	override fun conditionsFromJson(jsonObject: JsonObject, lootContextPredicate: LootContextPredicate, advancementEntityPredicateDeserializer: AdvancementEntityPredicateDeserializer) = Condition()
	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId() = ID

	class Condition : AbstractCriterionConditions(ID, LootContextPredicate.EMPTY)
	companion object {
		val ID: Identifier = id("full_arsenal")
	}
}