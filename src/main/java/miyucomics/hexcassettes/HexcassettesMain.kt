package miyucomics.hexcassettes

import at.petrak.hexcasting.api.HexAPI
import com.google.gson.JsonObject
import miyucomics.hexcassettes.inits.HexcassettesActions
import miyucomics.hexcassettes.inits.HexcassettesNetworking
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.entity.LivingEntity
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.world.GameRules
import net.minecraft.world.World

class HexcassettesMain : ModInitializer {
	override fun onInitialize() {
		GameRuleRegistry.register("maxCassettes", GameRules.Category.PLAYER,
			GameRuleFactory.createIntRule(6, 0, 100) { _, rule ->
				HexcassettesConfiguration.instance.copy(maxCassettes = rule.get()).also {
					HexcassettesConfiguration.save(it)
				}
			}
		)

		ServerPlayerEvents.AFTER_RESPAWN.register { oldPlayer, newPlayer, _ -> (newPlayer as PlayerEntityMinterface).getCassetteState().owned = (oldPlayer as PlayerEntityMinterface).getCassetteState().owned }
		CassetteItem().also {
			Registry.register(Registries.ITEM, id("cassette"), it)
			ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(Registries.ITEM_GROUP.key, HexAPI.modLoc("hexcasting"))).register { group -> group.add(it) }
		}

		QUINIO = Criteria.register(QuineCriterion())
		TAPE_WORM = Criteria.register(TapeWormCriterion())
		FULL_ARSENAL = Criteria.register(FullArsenalCriterion())

		HexcassettesNetworking.init()
		HexcassettesActions.init()
		HexcassettesSounds.init()
	}

	companion object {
		const val MOD_ID: String = "hexcassettes"
		fun id(string: String) = Identifier(MOD_ID, string)

		lateinit var QUINIO: QuineCriterion
		lateinit var TAPE_WORM: TapeWormCriterion
		lateinit var FULL_ARSENAL: FullArsenalCriterion

		fun serializeKey(key: Text): String = Text.Serializer.toJson(key)
		fun deserializeKey(data: String) = Text.Serializer.fromJson(data)!!
	}
}

// kinda messy, but I don't want to make a whole file for these

class CassetteItem : Item(Settings().maxCount(1).rarity(Rarity.UNCOMMON).food(FoodComponent.Builder().alwaysEdible().build())) {
	override fun getMaxUseTime(stack: ItemStack) = 100
	override fun getEatSound() = HexcassettesSounds.CASSETTE_LOOP

	override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
		if (world.isClient) {
			world.playSound(user.x, user.y, user.z, HexcassettesSounds.CASSETTE_INSERT, SoundCategory.MASTER, 5f, 1f, false)
			return super.finishUsing(stack, world, user)
		}

		if (user !is ServerPlayerEntity)
			return super.finishUsing(stack, world, user)

		val maxCassettes = HexcassettesConfiguration.instance.maxCassettes
		val cassetteData = (user as PlayerEntityMinterface).getCassetteState()
		if (cassetteData.owned < maxCassettes) {
			HexcassettesMain.TAPE_WORM.trigger(user)
			cassetteData.owned += 1
			if (cassetteData.owned == maxCassettes)
				HexcassettesMain.FULL_ARSENAL.trigger(user)
			cassetteData.sync(user)
		}
		return super.finishUsing(stack, world, user)
	}
}

class QuineCriterion : AbstractCriterion<QuineCriterion.Condition>() {
	override fun conditionsFromJson(jsonObject: JsonObject, lootContextPredicate: LootContextPredicate, advancementEntityPredicateDeserializer: AdvancementEntityPredicateDeserializer) = Condition()
	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId() = ID

	class Condition : AbstractCriterionConditions(ID, LootContextPredicate.EMPTY)
	companion object {
		val ID: Identifier = HexcassettesMain.id("quinio")
	}
}

class TapeWormCriterion : AbstractCriterion<TapeWormCriterion.Condition>() {
	override fun conditionsFromJson(jsonObject: JsonObject, lootContextPredicate: LootContextPredicate, advancementEntityPredicateDeserializer: AdvancementEntityPredicateDeserializer) = Condition()
	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId() = ID

	class Condition : AbstractCriterionConditions(ID, LootContextPredicate.EMPTY)
	companion object {
		val ID: Identifier = HexcassettesMain.id("tape_worm")
	}
}

class FullArsenalCriterion : AbstractCriterion<FullArsenalCriterion.Condition>() {
	override fun conditionsFromJson(jsonObject: JsonObject, lootContextPredicate: LootContextPredicate, advancementEntityPredicateDeserializer: AdvancementEntityPredicateDeserializer) = Condition()
	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId() = ID

	class Condition : AbstractCriterionConditions(ID, LootContextPredicate.EMPTY)
	companion object {
		val ID: Identifier = HexcassettesMain.id("full_arsenal")
	}
}