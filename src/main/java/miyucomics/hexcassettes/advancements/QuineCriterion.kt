package miyucomics.hexcassettes.advancements

import com.google.gson.JsonObject
import miyucomics.hexcassettes.HexcassettesMain
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class QuineCriterion : AbstractCriterion<QuineCriterion.Condition>() {
	override fun conditionsFromJson(obj: JsonObject, playerPredicate: EntityPredicate.Extended, predicateDeserializer: AdvancementEntityPredicateDeserializer) = Condition()
	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId() = ID

	class Condition : AbstractCriterionConditions(ID, EntityPredicate.Extended.EMPTY)
	companion object {
		val ID: Identifier = HexcassettesMain.id("quinio")
	}
}