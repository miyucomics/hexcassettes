package miyucomics.hexcassettes

import com.google.gson.JsonObject
import miyucomics.hexcassettes.HexcassettesUtils.id
import miyucomics.hexcassettes.inits.HexcassettesPatterns
import miyucomics.hexcassettes.inits.HexcassettesSounds
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.advancement.criterion.AbstractCriterionConditions
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.entity.LivingEntity
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

class HexcassettesMain : ModInitializer {
	override fun onInitialize() {
		QUINE = Criteria.register(QuineCriterion())
		Registry.register(Registry.ITEM, id("cassette"), CassetteItem())

		ServerPlayNetworking.registerGlobalReceiver(CASSETTE_REMOVE) { _, player, _, packet, _ ->
			val label = packet.readString()
			val state = HexcassettesAPI.getPlayerState(player).queuedHexes
			state.remove(label)
		}

		ServerPlayNetworking.registerGlobalReceiver(SYNC_CASSETTES) { _, player, _, _, _ ->
			HexcassettesAPI.syncToClient(
				player
			)
		}
		ServerPlayerEvents.AFTER_RESPAWN.register { _, player, _ -> HexcassettesAPI.removeAllQueued(player) }

		HexcassettesPatterns.init()
		HexcassettesSounds.init()
	}

	companion object {
		const val MOD_ID: String = "hexcassettes"
		const val MAX_CASSETTES: Int = 6
		const val MAX_LABEL_LENGTH: Int = 32

		val CASSETTE_REMOVE: Identifier = id("cassette_remove")
		val SYNC_CASSETTES: Identifier = id("sync_cassettes")

		lateinit var QUINE: QuineCriterion
	}
}

// kinda messy, but I don't want to make a whole file
class QuineCriterion : AbstractCriterion<QuineCriterion.Condition>() {
	override fun conditionsFromJson(
		obj: JsonObject,
		playerPredicate: EntityPredicate.Extended,
		predicateDeserializer: AdvancementEntityPredicateDeserializer
	) = Condition()

	fun trigger(player: ServerPlayerEntity) = trigger(player) { true }
	override fun getId() = ID

	class Condition : AbstractCriterionConditions(ID, EntityPredicate.Extended.EMPTY)
	companion object {
		val ID: Identifier = id("quinio")
	}
}

class CassetteItem :
	Item(Settings().maxCount(1).rarity(Rarity.UNCOMMON).food(FoodComponent.Builder().alwaysEdible().snack().build())) {
	override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
		if (world.isClient)
			return super.finishUsing(stack, world, user)
		if (user !is ServerPlayerEntity)
			return super.finishUsing(stack, world, user)
		val playerState = HexcassettesAPI.getPlayerState(user)
		if (playerState.ownedCassettes < HexcassettesMain.MAX_CASSETTES) {
			HexcassettesAPI.getPlayerState(user).ownedCassettes += 1
			HexcassettesAPI.syncToClient(user)
		}
		return super.finishUsing(stack, world, user)
	}
}