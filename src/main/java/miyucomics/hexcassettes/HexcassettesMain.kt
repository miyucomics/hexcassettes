package miyucomics.hexcassettes

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import com.google.gson.JsonObject
import miyucomics.hexcassettes.inits.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
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
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.world.World

class HexcassettesMain : ModInitializer {
	override fun onInitialize() {
		val cassette = CassetteItem()
		Registry.register(Registries.ITEM, id("cassette"), cassette)
		ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(Registries.ITEM_GROUP.key, HexAPI.modLoc("hexcasting"))).register { group -> group.add(cassette) }
		ServerPlayerEvents.AFTER_RESPAWN.register { oldPlayer, newPlayer, _ -> (newPlayer as PlayerEntityMinterface).getCassetteState().ownedSlots = (oldPlayer as PlayerEntityMinterface).getCassetteState().ownedSlots }

		QUINE = Criteria.register(QuineCriterion())
		TAPE_WORM = Criteria.register(TapeWormCriterion())
		FULL_ARSENAL = Criteria.register(FullArsenalCriterion())

		HexcassettesNetworking.init()
		HexcassettesActions.init()
		HexcassettesSounds.init()
	}

	companion object {
		const val MOD_ID: String = "hexcassettes"
		const val MAX_CASSETTES: Int = 6
		fun id(string: String) = Identifier(MOD_ID, string)

		lateinit var QUINE: QuineCriterion
		lateinit var TAPE_WORM: TapeWormCriterion
		lateinit var FULL_ARSENAL: FullArsenalCriterion

		fun serializeKey(pattern: HexPattern) = pattern.startDir.toString() + ":" + pattern.anglesSignature()
		fun deserializeKey(string: String): HexPattern {
			val fragments = string.split(":")
			return HexPattern.fromAngles(fragments[1], HexDir.fromString(fragments[0]))
		}
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

		val cassetteData = (user as PlayerEntityMinterface).getCassetteState()
		if (cassetteData.ownedSlots < HexcassettesMain.MAX_CASSETTES) {
			HexcassettesMain.TAPE_WORM.trigger(user)
			cassetteData.ownedSlots += 1
			if (cassetteData.ownedSlots == HexcassettesMain.MAX_CASSETTES)
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